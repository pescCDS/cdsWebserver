package org.pesc.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/24/16.
 */
@XmlRootElement(name="CertificateInfo")
public class CertificateInfo {
    private Integer version;
    private String subjectDN;
    private String issuerDN;
    private Date notBefore;
    private Date notAfter;
    private BigInteger serialNumber;
    private String algorithmUsedToSign;
    private String pem;
    private String commonName;

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonaName) {
        this.commonName = commonaName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public String getIssuerDN() {
        return issuerDN;
    }

    public void setIssuerDN(String issuerDN) {
        this.issuerDN = issuerDN;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAlgorithmUsedToSign() {
        return algorithmUsedToSign;
    }

    public void setAlgorithmUsedToSign(String algorithmUsedToSign) {
        this.algorithmUsedToSign = algorithmUsedToSign;
    }

    public String getPem() {
        return pem;
    }

    public void setPem(String pem) {
        this.pem = pem;
    }
}
