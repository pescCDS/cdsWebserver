package org.pesc.api.model;

import com.sun.xml.txw2.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/18/16.
 */
@XmlRootElement(name="CSVStatus")
public class CSVStatusDTO {
    Integer lineNumber;
    Date endTime;

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
