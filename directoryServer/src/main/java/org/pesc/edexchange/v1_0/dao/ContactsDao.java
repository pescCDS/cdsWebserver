package org.pesc.edexchange.v1_0.dao;

import org.pesc.edexchange.v1_0.OrganizationContact;

import java.util.List;

/**
 * Created by rgehbauer on 9/15/15.
 */
public interface ContactsDao extends DBDataSourceDao<OrganizationContact> {
	List<OrganizationContact> search(
			String city,
			Integer contactId,
			String contactName,
			String contactTitle,
			String contactType,
			String country,
			Long createdTime,
			Integer directoryId,
			String email,
			Long modifiedTime,
			String phone1,
			String phone2,
			String state,
			String streetAddress1,
			String streetAddress2,
			String streetAddress3,
			String streetAddress4,
			String zip
	);
}
