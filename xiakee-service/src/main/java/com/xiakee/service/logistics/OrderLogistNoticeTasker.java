package com.xiakee.service.logistics;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.xiakee.dao.logistics.LogisticsDao;
import com.xiakee.dao.smsc.SmscRecordDao;
import com.xiakee.dao.smsc.SmscResultDao;
import com.xiakee.dao.yz.YouzanOrderDao;
import com.xiakee.domain.logistics.LogisticsBean;
import com.xiakee.domain.order.EcOrderLogistBean;
import com.xiakee.domain.smsc.SmscContentBean;
import com.xiakee.domain.utils.LogistNodeEnum;
import com.xiakee.domain.utils.SmscContentTypeEnum;
import com.xiakee.domain.yz.YzordersBean;
import com.xiakee.ecdao.order.EcOrderDao;
import com.xiakee.service.AutoExecuteTask;
import com.xiakee.service.AutoExecuteTasker;
import com.xiakee.service.smsc.SmscControlTasker;
import com.xiakee.service.utils.EcstoreApiUtil;
import com.xiakee.service.utils.SmscTempletUtil;
import com.xiakee.service.utils.TimeUtils;

public class OrderLogistNoticeTasker extends AutoExecuteTask<LogisticsBean> {
	private static Logger log = Logger.getLogger(OrderLogistNoticeTasker.class);

	private YouzanOrderDao orderDao;
	private LogisticsDao logisticsDao;
	private SmscRecordDao recordDao;
	private SmscResultDao resultDao; 
	private EcOrderDao ecOrderDao;
	
	public OrderLogistNoticeTasker(YouzanOrderDao orderDao,LogisticsDao logisticsDao,
			SmscRecordDao recordDao,SmscResultDao resultDao,EcOrderDao ecOrderDao){
		this.orderDao = orderDao;
		this.logisticsDao = logisticsDao;
		this.recordDao = recordDao;
		this.resultDao = resultDao;
		this.ecOrderDao = ecOrderDao;
	}

	@Override
	public boolean executeTask() {
		//getTaskBean()该bean中必须存在node值，orderid和infoid两个值必须存在一个,还有是否需要发送邮件
		boolean isOK = false;
		if (getTaskBean() != null && this.orderDao != null && this.logisticsDao != null) {
			log.info("后台自动执行物流节点信息：" + getTaskBean());
			getTaskBean().setNode(getTaskBean().getLogistNode().toCode());
			YzordersBean bean = null;
			
			String orderId = getTaskBean().getOrderid();
			//获取当前物流节点信息是否存在
			if(StringUtils.isNotBlank(orderId)){
				bean = this.orderDao.findOrderLogistByorderId(getTaskBean());
			}else {
				bean = this.orderDao.findOrderLogistByInfoId(getTaskBean());
			}
			
			if (bean == null) {//当前物流节点不存在
				if(StringUtils.isNotBlank(orderId)){
					bean = this.orderDao.findOrderByOrderid(orderId);
				}else {
					bean = this.orderDao.findOrderByInfoId(getTaskBean().getInfoId());
				}
				
				LogisticsBean logistBean = new LogisticsBean();
				logistBean.setOrderid(bean.getOrderid());
				logistBean.setContent(getTaskBean().getLogistNode().toDescription());
				logistBean.setNode(getTaskBean().getLogistNode().toCode());
				int sum = logisticsDao.addLogistics(logistBean);
				if(sum > 0){//本地物流节点信息插入成功，把该信息插入到商场订单物流中
					if(LogistNodeEnum.OK != getTaskBean().getLogistNode()){
						log.info("本地物流信息节点插入成功，即将导入到商城订单物流节点上");
						EcOrderLogistBean ecBean = new EcOrderLogistBean();
						ecBean.setRel_id(bean.getOrderid());
						String time = getTaskBean().getCreated();
						if(StringUtils.isNotBlank(time)){
							ecBean.setAlttime(TimeUtils.formatTimeForPhp(time));
						}else {
							ecBean.setAlttime(TimeUtils.getCurrentTimeForPhp());
						}
						ecBean.setLog_text(getTaskBean().getLogistNode().toDescription());
						
						//获取该订单最后一次插入的物流节点
						Long currLogTime = ecOrderDao.findLastAlttimeByOrderid(bean.getOrderid());
						if(ecBean.getAlttime() > currLogTime){	
							if(getTaskBean().getSmscType() != SmscContentTypeEnum.LOGISTONE){
								int ecSum = ecOrderDao.addOrderLogist(ecBean);
								if(ecSum > 0){
									log.info("成功插入商城物流节点：" + ecBean);//录入海外包裹信息阶段修改商城订单发货状态
									if(getTaskBean().getLogistNode() == LogistNodeEnum.EXPRESSNO){
										// boolean delivery = EcstoreApiUtil.updateShipOrderStatus(bean.getOrderid(),"1");
										Map<String, Object> params = new HashMap<String, Object>();
										params.put("ship_status", "1");
										params.put("order_id", bean.getOrderid());
										int row = ecOrderDao.updateOrderShipStatus(params);
										log.info("成功修改商城订单的发货状态：" + (row > 0));
									}else if(getTaskBean().getLogistNode() == LogistNodeEnum.FINISH){
										Map<String, Object> params = new HashMap<String, Object>();
										params.put("status", "finish");
										params.put("ship_status", "1");
										params.put("order_id", bean.getOrderid());
										int row = ecOrderDao.updateOrderShipStatus(params);
										log.info("成功修改商城订单的结束状态：" + (row > 0));
									}
								}else {
									log.error("插入商城物流节点失败：" + ecBean);
								}
							}
							if(getTaskBean().isOpenSmsc() && getTaskBean().getSmscType() != null){
								log.info("触发短信推送任务==" + bean);
								SmscContentBean smsc = SmscTempletUtil.getSmscLogistics(bean.getMobile(), bean.getName(), bean.getCreated(), getTaskBean().getSmscType());
								SmscControlTasker task = new SmscControlTasker(recordDao,resultDao);
								task.setTaskBean(smsc);
								AutoExecuteTasker.addAutoExecuteTasker(task);
							}else {
								log.info("短信提醒功能没有开启:" + getTaskBean().isOpenSmsc());
							}
						}else {
							log.info("该条物流信息节点时间小于当前最后一条物流时间");
						}
					}
				}
			}else {
				log.info("该订单已发送过短信" + bean);
			}
			isOK = true;
		}
		return isOK;
	}

}
