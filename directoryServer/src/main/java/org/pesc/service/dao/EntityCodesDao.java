package org.pesc.service.dao;

import org.pesc.edexchange.v1_0.EntityCode;

import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface EntityCodesDao extends DBDataSourceDao<EntityCode> {
	List<EntityCode> search(Integer id, Integer code, String description,
							Long createdTime, Long modifiedTime);
}
