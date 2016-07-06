package org.pesc.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/10/16.
 */
public class PagedData<T> {

    private Integer limit;
    private Integer offset;
    private Long total;

    private List<T> data;

    public PagedData() {

    };

    public PagedData(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
