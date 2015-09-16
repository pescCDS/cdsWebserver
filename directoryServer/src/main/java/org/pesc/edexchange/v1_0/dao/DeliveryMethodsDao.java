package org.pesc.edexchange.v1_0.dao;

import org.pesc.edexchange.v1_0.DeliveryMethod;

import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface DeliveryMethodsDao extends DBDataSourceDao<DeliveryMethod> {
    List<DeliveryMethod> search(Integer id, String method);
}
