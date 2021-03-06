package org.pesc.cds.webservice.v1_0;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2014-01-23T17:40:43.503-08:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "http://service.cds.pesc.org/", name = "WebServicesInterface")
@XmlSeeAlso({ObjectFactory.class})
public interface WebServicesInterface {

    @WebResult(name = "sendCDSBatch", targetNamespace = "")
    @RequestWrapper(localName = "sendCDSBatch", targetNamespace = "http://service.cds.pesc.org/", className = "org.pesc.cds.webservice.v1_0.SendCDSBatch")
    @WebMethod
    @ResponseWrapper(localName = "sendCDSBatchResponse", targetNamespace = "http://service.cds.pesc.org/", className = "org.pesc.cds.webservice.v1_0.SendCDSBatchResponse")
    public java.lang.String sendCDSBatch(
        @WebParam(name = "psisCode", targetNamespace = "")
        java.lang.String psisCode,
        @WebParam(name = "username", targetNamespace = "")
        java.lang.String username,
        @WebParam(name = "password", targetNamespace = "")
        java.lang.String password,
        @WebParam(name = "CDSBatchXML", targetNamespace = "")
        java.lang.String cdsBatchXML
    );

    @WebResult(name = "getCDSBatch", targetNamespace = "")
    @RequestWrapper(localName = "getCDSBatch", targetNamespace = "http://service.cds.pesc.org/", className = "org.pesc.cds.webservice.v1_0.GetCDSBatch")
    @WebMethod
    @ResponseWrapper(localName = "getCDSBatchResponse", targetNamespace = "http://service.cds.pesc.org/", className = "org.pesc.cds.webservice.v1_0.GetCDSBatchResponse")
    public java.lang.String getCDSBatch(
        @WebParam(name = "psisCode", targetNamespace = "")
        java.lang.String psisCode,
        @WebParam(name = "username", targetNamespace = "")
        java.lang.String username,
        @WebParam(name = "password", targetNamespace = "")
        java.lang.String password
    );
}
