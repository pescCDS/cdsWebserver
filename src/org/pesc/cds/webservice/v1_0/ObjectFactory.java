
package org.pesc.cds.webservice.v1_0;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.pesc.cds.webservice.v1_0 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetCDSBatch_QNAME = new QName("http://service.cds.pesc.org/", "getCDSBatch");
    private final static QName _SendCDSBatch_QNAME = new QName("http://service.cds.pesc.org/", "sendCDSBatch");
    private final static QName _SendCDSBatchResponse_QNAME = new QName("http://service.cds.pesc.org/", "sendCDSBatchResponse");
    private final static QName _GetCDSBatchResponse_QNAME = new QName("http://service.cds.pesc.org/", "getCDSBatchResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.pesc.cds.webservice.v1_0
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetCDSBatch }
     * 
     */
    public GetCDSBatch createGetCDSBatch() {
        return new GetCDSBatch();
    }

    /**
     * Create an instance of {@link SendCDSBatchResponse }
     * 
     */
    public SendCDSBatchResponse createSendCDSBatchResponse() {
        return new SendCDSBatchResponse();
    }

    /**
     * Create an instance of {@link SendCDSBatch }
     * 
     */
    public SendCDSBatch createSendCDSBatch() {
        return new SendCDSBatch();
    }

    /**
     * Create an instance of {@link GetCDSBatchResponse }
     * 
     */
    public GetCDSBatchResponse createGetCDSBatchResponse() {
        return new GetCDSBatchResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCDSBatch }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.cds.pesc.org/", name = "getCDSBatch")
    public JAXBElement<GetCDSBatch> createGetCDSBatch(GetCDSBatch value) {
        return new JAXBElement<GetCDSBatch>(_GetCDSBatch_QNAME, GetCDSBatch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendCDSBatch }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.cds.pesc.org/", name = "sendCDSBatch")
    public JAXBElement<SendCDSBatch> createSendCDSBatch(SendCDSBatch value) {
        return new JAXBElement<SendCDSBatch>(_SendCDSBatch_QNAME, SendCDSBatch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendCDSBatchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.cds.pesc.org/", name = "sendCDSBatchResponse")
    public JAXBElement<SendCDSBatchResponse> createSendCDSBatchResponse(SendCDSBatchResponse value) {
        return new JAXBElement<SendCDSBatchResponse>(_SendCDSBatchResponse_QNAME, SendCDSBatchResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCDSBatchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.cds.pesc.org/", name = "getCDSBatchResponse")
    public JAXBElement<GetCDSBatchResponse> createGetCDSBatchResponse(GetCDSBatchResponse value) {
        return new JAXBElement<GetCDSBatchResponse>(_GetCDSBatchResponse_QNAME, GetCDSBatchResponse.class, null, value);
    }

}
