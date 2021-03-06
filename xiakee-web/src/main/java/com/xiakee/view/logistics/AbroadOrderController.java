package com.xiakee.view.logistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alibaba.fastjson.JSON;
import com.talkyun.apus.mybatis.plugin.Page;
import com.xiakee.bean.ConsigneeInfo;
import com.xiakee.bean.PiecesItem;
import com.xiakee.bean.SplitOrder;
import com.xiakee.bean.UdfexDeclare;
import com.xiakee.dao.logistics.BoxnoDao;
import com.xiakee.dao.udfex.CategoryCustomMapper;
import com.xiakee.dao.udfex.CityMapper;
import com.xiakee.dao.udfex.DistrictMapper;
import com.xiakee.dao.udfex.StateMapper;
import com.xiakee.domain.logistics.AbroadOrderBean;
import com.xiakee.domain.logistics.BoxnoBean;
import com.xiakee.domain.logistics.DeclareGoodsBean;
import com.xiakee.domain.logistics.UdfexDeclareResultJson;
import com.xiakee.domain.logistics.UdfexSplitOrderChildResiltJson;
import com.xiakee.domain.logistics.UdfexSplitOrderResultJson;
import com.xiakee.domain.udfex.CategoryCustom;
import com.xiakee.domain.udfex.City;
import com.xiakee.domain.udfex.District;
import com.xiakee.domain.udfex.State;
import com.xiakee.domain.utils.Constants;
import com.xiakee.service.logistics.AbroadOrderService;
import com.xiakee.service.logistics.BoxnoService;
import com.xiakee.service.utils.UdfexApiUtil;

@Controller
@SessionAttributes("mergeDeclareBeans")
public class AbroadOrderController {
	private static Logger log = Logger.getLogger(AbroadOrderController.class);

	@Autowired
	private BoxnoService boxnoService;

	@Autowired
	private AbroadOrderService abroadService;

	@Autowired
	private CityMapper cityMapper;

	@Autowired
	private StateMapper stateMapper;

	@Autowired
	private DistrictMapper districtMapper;

	@Autowired
	private CategoryCustomMapper categoryCustomMapper;

	@Autowired
	private BoxnoDao boxnoDao;

	@RequestMapping("/displayAllNoExpressno")
	public String displayAllNoExpressno(HttpServletRequest req) {
		log.info("获取所有尚未填写海外包裹id信息的信息列表");
		List<BoxnoBean> beans = boxnoService.getAllNoExpressnoBean();
		req.setAttribute("beans", beans);
		return "purchase/displayAllNoExpressno";
	}

	@RequestMapping("/displayAllAbroadOrders")
	public String displayAllAbroadOrders(@RequestParam(value = "page", defaultValue = "1") int page, @Param("outOrderId") String outOrderId, @Param("expressno") String expressno, ModelMap model) {
		log.info("获取所有海外订单信息列表");
		if (page < 1) {
			page = 1;
		}
		List<AbroadOrderBean> beans = this.abroadService.getAllAbroadOrderByPage(page, outOrderId, expressno);
		if (beans == null || beans.size() < 1) {
			page = 1;
		}
		model.addAttribute("beans", beans);
		model.addAttribute("page", page);
		model.addAttribute("outOrderId", outOrderId);
		model.addAttribute("expressno", expressno);
		return "purchase/displayAllAbroadOrders";
	}

	@RequestMapping("/displayAllExpressnos")
	public String displayAllExpressnos(HttpServletRequest req) {
		log.info("获取所有已填写海外包裹id信息列表");
		List<BoxnoBean> beans = boxnoService.getAllExpressnoedBean();
		req.setAttribute("beans", beans);
		return "purchase/displayAllExpressnos";
	}

	@RequestMapping("/updateExpressno")
	public String updateExpressno(@Param("infos") String infos, @Param("expressno") String expressno, @Param("transfer_id") String transfer_id) {
		if (StringUtils.isNotBlank(infos)) {
			String[] infoIds = infos.split(",");
			for (String infoId : infoIds) {
				Integer sum = boxnoService.updateExpressno(Long.parseLong(infoId), expressno, transfer_id);
				log.info("海外包裹号修改状态==infoId=" + infoId + "===expressno=" + expressno);
			}
		}
		return "redirect:/displayAllNoExpressno.do";
	}

	@RequestMapping("/displayAllDeclareBeans")
	public String displayAllDeclareBeans(ModelMap model, HttpServletRequest request) {
		log.info("获取所有待申报的信息列表");
		int currPage = ServletRequestUtils.getIntParameter(request, "currPage", 1);
		int pageSize = ServletRequestUtils.getIntParameter(request, "pageSize", 30);
		Map<String, Object> param = new HashMap<String, Object>();
		Page page = buildPage(currPage, pageSize);
		param.put(Constants.PAGE, page);
		List<BoxnoBean> beans = boxnoService.getAllDeclareBean(param);

		model.addAttribute("beans", beans);
		model.addAttribute("page", page);
		return "purchase/displayAllDeclareBeans";
	}

	@RequestMapping("/displayDeclaredBeans")
	public String displayDeclaredBeans(ModelMap model) {
		log.info("获取所有已经申报的信息列表");
		List<BoxnoBean> beans = boxnoService.getDeclaredBean();
		model.addAttribute("beans", beans);
		return "purchase/displayDeclaredBeans";
	}

	@RequestMapping("/udfexBeforeDeclare")
	public String udfexBeforeDeclare(@Param("expressno") String expressno, HttpServletRequest req) {
		if (StringUtils.isNotBlank(expressno)) {
			List<UdfexDeclare> beanList = boxnoService.getUdfexDeclareGoodsBean(expressno);
			// // 获取行政区
			// List<District> districtList =
			// districtMapper.selectByCityCode(bean.getConsigneeInfo().getConsigneeCityCode());
			// // 获取城市
			// List<City> cityList =
			// cityMapper.selectByStateCode(bean.getConsigneeInfo().getConsigneeStateCode());
			// 获取省州
			List<State> stateList = stateMapper.selectByAll();
			// 一级分类
			List<CategoryCustom> firstCategoryCustomList = categoryCustomMapper.selectByParentCategoryId(0);
			// bean.setInfoId(infoId);
			if(beanList != null){
			req.setAttribute("beanList", beanList);
			if(beanList.get(0) != null){
			req.setAttribute("logisticsNo", beanList.get(0).getLogisticsNo());
			req.setAttribute("logisticsVendor", beanList.get(0).getLogisticsVendor());
			req.setAttribute("size", beanList.size());
			if(beanList.get(0).getInfoId() != null){
				try{
			BoxnoBean boxnoBean=boxnoDao.findBoxnoBeanById(Long.parseLong(beanList.get(0).getInfoId().split("-")[0]));
			req.setAttribute("boxnoBeanTransferId", boxnoBean.getTransfer_id());
				}catch(Exception e){
					e.printStackTrace();
					
				}
			
			}
			}
			}
			
			// req.setAttribute("districtList", districtList);
			// req.setAttribute("cityList", cityList);
			req.setAttribute("stateList", stateList);
			req.setAttribute("firstCategoryCustomList", firstCategoryCustomList);

			return "purchase/udfexAddDeclareInfo";
		}
		return "redirect:/displayAllDeclareBeans.do";
	}

	@RequestMapping("/udfexDeclareOrderDo")
	public String udfexDeclareOrderDo(HttpServletRequest request) {

		String logisticsNo = request.getParameter("logisticsNo");
		// String logisticsVendor = request.getParameter("logisticsVendor");
		String warehouseCode = request.getParameter("warehouseCode");
		String serviceProductCode = request.getParameter("serviceProductCode");
		List<String> infoIdList = new ArrayList<String>();
		SplitOrder splitOrder = new SplitOrder();
		List<UdfexDeclare> subConsignmentList = new ArrayList<UdfexDeclare>();
		for (int i = 1; i <= 50; i++) {
			UdfexDeclare bean = new UdfexDeclare();
			bean.setLogisticsNo(logisticsNo);
			bean.setWarehouseCode(warehouseCode);
			bean.setServiceProductCode(serviceProductCode);
			String infoId = request.getParameter("infoId" + i);
			if (StringUtils.isNotBlank(infoId)) {
				infoIdList.add(infoId);
				bean.setInfoId(infoId);
				bean.setRefLogisticsNo(infoId);
				String consigneeStateCode = request.getParameter("consigneeStateCode" + i);
				String consigneeCityCode = request.getParameter("consigneeCityCode" + i);
				String consigneeDistrictCode = request.getParameter("consigneeDistrictCode" + i);
				String consigneeName = request.getParameter("consigneeName" + i);
				String consigneePhoneNo = request.getParameter("consigneePhoneNo" + i);
				String consigneeMobileNo = request.getParameter("consigneeMobileNo" + i);
				String consigneeStreet = request.getParameter("consigneeStreet" + i);
				String consigneePostCode = request.getParameter("consigneePostCode" + i);
				ConsigneeInfo consigneeInfo = new ConsigneeInfo();
				consigneeInfo.setConsigneeCityCode(consigneeCityCode);
				consigneeInfo.setConsigneeCountryCode("CN");
				consigneeInfo.setConsigneeDistrictCode(consigneeDistrictCode);
				consigneeInfo.setConsigneeMobileNo(consigneeMobileNo);
				consigneeInfo.setConsigneeName(consigneeName);
				consigneeInfo.setConsigneePhoneNo(consigneePhoneNo);
				consigneeInfo.setConsigneePostCode(consigneePostCode);
				consigneeInfo.setConsigneeStateCode(consigneeStateCode);
				consigneeInfo.setConsigneeStreet(consigneeStreet);
				bean.setConsigneeInfo(consigneeInfo);
				bean.setConsigneeInfoDto(consigneeInfo);
				List<PiecesItem> piecesItems = new ArrayList<PiecesItem>();
				for (int j = 1; j <= 50; j++) {
					String amount = request.getParameter("amount" + i + j);
					if (StringUtils.isNotBlank(amount)) {
						String brandName = request.getParameter("brandName" + i + j);
						String currency = request.getParameter("currency" + i + j);
						String goodsDescription = request.getParameter("goodsDescription" + i + j);
						String piecesItemQty = request.getParameter("piecesItemQty" + i + j);
						String spec = request.getParameter("spec" + i + j);
						String goodsCode = request.getParameter("goodsCode" + i + j);
						PiecesItem piecesItem = new PiecesItem();
						piecesItem.setAmount(amount);
						piecesItem.setBrandName(brandName);
						piecesItem.setCurrency(currency);
						piecesItem.setGoodsDescription(goodsDescription);
						piecesItem.setPiecesItemQty(piecesItemQty);
						piecesItem.setPackageNum(piecesItemQty);
						piecesItem.setSpec(spec);
						piecesItem.setUnit("件");
						piecesItem.setGoodsCode(goodsCode);
						piecesItems.add(piecesItem);
					} else {
						break;
					}
				}
				bean.setPiecesItems(piecesItems);
				bean.setPiecesItemDetailDtoList(piecesItems);
			} else {
				break;
			}
			subConsignmentList.add(bean);
		}
		splitOrder.setSubConsignmentList(subConsignmentList);
		if (subConsignmentList.size() <= 1) {
			// 直接申报，不需要拆单
			UdfexDeclareResultJson jsonBean = UdfexApiUtil.udfexDeclare(subConsignmentList.get(0));
			BoxnoBean boxnoBean = new BoxnoBean();
			if (StringUtils.equals("0", jsonBean.getStatus())) {// 如果申报失败，默认修改为待出库方式申报
				boxnoBean.setDeclared(1);// 成功代码
				log.info("申报成功：infoId = " + JSON.toJSONString(jsonBean));
			} else {
				boxnoBean.setDeclared(2);// 成功代码
				log.info("申报失败：infoId = " + JSON.toJSONString(jsonBean));
			}
			for (String iid : infoIdList) {
				for (String iiiid : iid.split("-")) {
					boxnoBean.setInfoId(Long.valueOf(iiiid));
					boxnoDao.updateDeclate(boxnoBean);
				}
			}

			request.setAttribute("jsonBean", jsonBean.getMessage());
		} else {
			// 需要拆单
			// 先申报
			UdfexDeclare bean = subConsignmentList.get(0);
			List<PiecesItem> piecesItems = new ArrayList<PiecesItem>();
			for (int j = 0; j < subConsignmentList.size(); j++) {
				piecesItems.addAll(subConsignmentList.get(j).getPiecesItems());
			}
			bean.setPiecesItems(piecesItems);
			UdfexDeclareResultJson jsonBean = UdfexApiUtil.udfexDeclare(bean);
			if (StringUtils.equals("0", jsonBean.getStatus())) {// 如果申报失败，默认修改为待出库方式申报
				log.info("申报成功：infoId = " + JSON.toJSONString(jsonBean));
				String consignmentNo = jsonBean.getResult().getConsignmentNo();
				splitOrder.setConsignmentNo(consignmentNo);
				// 拆单
				UdfexSplitOrderResultJson resultJson = UdfexApiUtil.udfexSplitOrder(splitOrder);
				if (StringUtils.equals("0", resultJson.getStatus())) {// 如果申报失败，默认修改为待出库方式申报
					log.info("拆单成功：infoId = " + JSON.toJSONString(resultJson));
					for (UdfexSplitOrderChildResiltJson logisticsNoBean : resultJson.getResult()) {
						String rlogisticsNo = logisticsNoBean.getLogisticsNo();
						String rrefLogisticsNo = logisticsNoBean.getRefLogisticsNo();
						for (String rNo : rrefLogisticsNo.split("-")) {
							BoxnoBean boxnoBean = new BoxnoBean();
							boxnoBean.setInfoId(Long.valueOf(rNo));
							boxnoBean.setExpressno(rlogisticsNo);
							boxnoBean.setDeclared(1);
							boxnoDao.updateExpressnoByInfoId(boxnoBean);
						}
					}
					request.setAttribute("jsonBean", jsonBean.getMessage());
				} else {
					log.info("拆单失败：infoId = " + JSON.toJSONString(resultJson));
				}
			} else {
				log.info("申报失败：infoId = " + subConsignmentList.get(0).getInfoId());
				request.setAttribute("jsonBean", jsonBean.getMessage());
			}
		}

		return "purchase/udfexDeclareResult";
	}

	@RequestMapping("/udfexCity")
	@ResponseBody
	public String udfexGetCityList(@RequestParam String stateCode, HttpServletRequest req) {
		List<City> cityList = cityMapper.selectByStateCode(stateCode);
		return JSON.toJSONString(cityList);
	}

	@RequestMapping("/udfexDistrict")
	@ResponseBody
	public String udfexGetDistrictList(@RequestParam String cityCode, HttpServletRequest req) {
		List<District> districtList = districtMapper.selectByCityCode(cityCode);
		return JSON.toJSONString(districtList);
	}

	@RequestMapping("/udfexClassify")
	@ResponseBody
	public String udfexClassIfy(@RequestParam Integer classifyId, HttpServletRequest req) {
		List<CategoryCustom> classifyList = categoryCustomMapper.selectByParentCategoryId(classifyId);
		return JSON.toJSONString(classifyList);
	}

	@RequestMapping("/beforeDeclare")
	public String beforeDeclare(@Param("infoId") String infoId, HttpServletRequest req) {
		if (StringUtils.isNotBlank(infoId)) {
			DeclareGoodsBean bean = boxnoService.getDeclareGoodsBean(infoId);
			log.info("==AbroadOrderController==beforeDeclare==DeclareGoodsBean==" + bean);
			req.setAttribute("bean", bean);
			return "purchase/addDeclareInfo";
		}
		return "redirect:/displayAllDeclareBeans.do";
	}

	@RequestMapping("/declareOrder")
	public String declareOrder(@Param("bean") DeclareGoodsBean bean) {
		log.info("==AbroadOrderController==declareOrder==DeclareGoodsBean==" + bean);
		boxnoService.declareBaiweiOrderInfo(bean);
		return "redirect:/displayAllDeclareBeans.do";
	}

	@RequestMapping("/mergeDeclare")
	public String mergeDeclare(@Param("infoIds") String infoIds, ModelMap model) {
		List<DeclareGoodsBean> beans = new ArrayList<DeclareGoodsBean>();
		model.addAttribute("mergeDeclareBeans", beans);
		if (StringUtils.isNotBlank(infoIds)) {
			String[] ids = infoIds.split(",");
			if (ids.length > 1) {
				DeclareGoodsBean bean = boxnoService.getDeclareGoodsBean(ids[0]);
				infoIds = infoIds.substring(infoIds.indexOf(",") + 1);
				bean.setInfoIds(infoIds);
				log.info("即将合箱操作信息：" + bean);
				model.addAttribute("bean", bean);
				return "purchase/setBrandAndEnname";
			}
		}
		return "redirect:/displayAllDeclareBeans.do";
	}

	@RequestMapping("/setBrandAndEnname")
	public String setBrandAndEnname(@Param("bean") DeclareGoodsBean bean, ModelMap model) {
		List<DeclareGoodsBean> beans = (List<DeclareGoodsBean>) model.get("mergeDeclareBeans");
		if (beans == null) {
			beans = new ArrayList<DeclareGoodsBean>();
		}
		beans.add(bean);
		model.addAttribute("mergeDeclareBeans", beans);

		String infoIds = bean.getInfoIds();
		if (StringUtils.isNotBlank(infoIds)) {
			String[] ids = infoIds.split(",");
			if (ids.length > 0) {
				DeclareGoodsBean waitBean = boxnoService.getDeclareGoodsBean(ids[0]);
				if (ids.length != 1) {
					infoIds = infoIds.substring(infoIds.indexOf(",") + 1);
					waitBean.setInfoIds(infoIds);
				} else {
					waitBean.setInfoIds("");
				}
				log.info("等待合箱的物品信息" + waitBean);
				model.addAttribute("bean", waitBean);
				return "purchase/setBrandAndEnname";
			}
		} else {
			boxnoService.declareBaiweiOrderInfo(beans);
		}
		return "redirect:/displayAllDeclareBeans.do";
	}

	private Page buildPage(int start, int pageSize) {
		Page page = new Page();
		page.setCurrentPage(start);
		page.setShowCount(pageSize);
		return page;
	}
}
