
package com.xiakee.service.wcf;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ISjzyBatchDataOperate", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ISjzyBatchDataOperate {


    /**
     * 
     * @param userName
     * @param importJsonData
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "BatchInputData", action = "http://tempuri.org/ISjzyBatchDataOperate/BatchInputData")
    @WebResult(name = "BatchInputDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "BatchInputData", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BatchInputData")
    @ResponseWrapper(localName = "BatchInputDataResponse", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BatchInputDataResponse")
    public String batchInputData(
        @WebParam(name = "userName", targetNamespace = "http://tempuri.org/")
        String userName,
        @WebParam(name = "importJsonData", targetNamespace = "http://tempuri.org/")
        String importJsonData);

    /**
     * 
     * @param expressNo
     * @param userName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "BatchDeleteData", action = "http://tempuri.org/ISjzyBatchDataOperate/BatchDeleteData")
    @WebResult(name = "BatchDeleteDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "BatchDeleteData", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BatchDeleteData")
    @ResponseWrapper(localName = "BatchDeleteDataResponse", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BatchDeleteDataResponse")
    public String batchDeleteData(
        @WebParam(name = "userName", targetNamespace = "http://tempuri.org/")
        String userName,
        @WebParam(name = "expressNo", targetNamespace = "http://tempuri.org/")
        String expressNo);

    /**
     * 
     * @param buyRecordServiceChargeTermByJson
     * @param buyRecordByJson
     * @param boxServiceChargeTermByJson
     * @param boxesByJson
     * @param userName
     * @param buyThingsByJson
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "BranchBox", action = "http://tempuri.org/ISjzyBatchDataOperate/BranchBox")
    @WebResult(name = "BranchBoxResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "BranchBox", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BranchBox")
    @ResponseWrapper(localName = "BranchBoxResponse", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.BranchBoxResponse")
    public String branchBox(
        @WebParam(name = "userName", targetNamespace = "http://tempuri.org/")
        String userName,
        @WebParam(name = "buyRecordByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordByJson,
        @WebParam(name = "boxesByJson", targetNamespace = "http://tempuri.org/")
        String boxesByJson,
        @WebParam(name = "buyThingsByJson", targetNamespace = "http://tempuri.org/")
        String buyThingsByJson,
        @WebParam(name = "boxServiceChargeTermByJson", targetNamespace = "http://tempuri.org/")
        String boxServiceChargeTermByJson,
        @WebParam(name = "buyRecordServiceChargeTermByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordServiceChargeTermByJson);

    /**
     * 
     * @param buyRecordServiceChargeTermByJson
     * @param buyRecordByJson
     * @param boxServiceChargeTermByJson
     * @param boxesByJson
     * @param userName
     * @param buyThingsByJson
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "FCL", action = "http://tempuri.org/ISjzyBatchDataOperate/FCL")
    @WebResult(name = "FCLResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "FCL", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.FCL")
    @ResponseWrapper(localName = "FCLResponse", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.FCLResponse")
    public String fcl(
        @WebParam(name = "userName", targetNamespace = "http://tempuri.org/")
        String userName,
        @WebParam(name = "buyRecordByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordByJson,
        @WebParam(name = "boxesByJson", targetNamespace = "http://tempuri.org/")
        String boxesByJson,
        @WebParam(name = "buyThingsByJson", targetNamespace = "http://tempuri.org/")
        String buyThingsByJson,
        @WebParam(name = "boxServiceChargeTermByJson", targetNamespace = "http://tempuri.org/")
        String boxServiceChargeTermByJson,
        @WebParam(name = "buyRecordServiceChargeTermByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordServiceChargeTermByJson);

    /**
     * 
     * @param buyRecordServiceChargeTermByJson
     * @param buyRecordByJson
     * @param userName
     * @param buyThingsByJson
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "NotBoxedData", action = "http://tempuri.org/ISjzyBatchDataOperate/NotBoxedData")
    @WebResult(name = "NotBoxedDataResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "NotBoxedData", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.NotBoxedData")
    @ResponseWrapper(localName = "NotBoxedDataResponse", targetNamespace = "http://tempuri.org/", className = "com.xiakee.service.wcf.NotBoxedDataResponse")
    public String notBoxedData(
        @WebParam(name = "userName", targetNamespace = "http://tempuri.org/")
        String userName,
        @WebParam(name = "buyRecordByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordByJson,
        @WebParam(name = "buyThingsByJson", targetNamespace = "http://tempuri.org/")
        String buyThingsByJson,
        @WebParam(name = "buyRecordServiceChargeTermByJson", targetNamespace = "http://tempuri.org/")
        String buyRecordServiceChargeTermByJson);

}
