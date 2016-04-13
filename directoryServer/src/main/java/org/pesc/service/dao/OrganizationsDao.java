package org.pesc.service.dao;

import org.pesc.api.model.Organization;

import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface OrganizationsDao extends DBDataSourceDao<Organization> {
	List<Organization> search(
			Integer directoryId,
			String organizationId,
			String organizationIdType,
			String organizationName,
			String organizationSubcode,
			Integer entityId,
			String organizationEin,
			Long createdTime,
			Long modifiedTime
	);
}
