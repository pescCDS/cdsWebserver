package org.pesc.cds.webservice.service;

import org.pesc.cds.datatables.FilterSet;
import org.pesc.edexchange.v1_0.DeliveryMethod;
import org.pesc.edexchange.v1_0.DeliveryOption;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.OrganizationContact;
import org.pesc.edexchange.v1_0.dao.ContactsDao;
import org.pesc.edexchange.v1_0.dao.DBDataSourceDao;
import org.pesc.edexchange.v1_0.dao.DeliveryMethodsDao;
import org.pesc.edexchange.v1_0.dao.DeliveryOptionsDao;
import org.pesc.edexchange.v1_0.dao.DocumentFormatsDao;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.pesc.edexchange.v1_0.dao.FilterSetsDao;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;
import org.springframework.beans.factory.annotation.Autowired;

public class DatasourceManagerUtil {
	private DatasourceManagerUtil() {}
	private static DatasourceManagerUtil instance = null;

	@Autowired
	private EntityCodesDao entityCodes;
	@Autowired
	private DocumentFormatsDao docFormats;
	@Autowired
	private OrganizationsDao organizations;
	@Autowired
	private ContactsDao contacts;
	@Autowired
	private DeliveryMethodsDao deliveryMethods;
	@Autowired
	private DeliveryOptionsDao deliveryOptions;
	@Autowired
	private FilterSetsDao filterSets;

	public static DatasourceManagerUtil getInstance() {
		if(instance == null) {
			instance = new DatasourceManagerUtil();
		}
		return instance;
	}
	
	// public class methods
	public DBDataSourceDao<?> byName(String dsName) {
		if(dsName.equalsIgnoreCase("entities")) {
			return entityCodes;
		} else if(dsName.equalsIgnoreCase("documentformats")) {
			return docFormats;
		} else if(dsName.equalsIgnoreCase("organizations")) {
			return organizations;
		} else if(dsName.equalsIgnoreCase("contacts")) {
			return contacts;
		} else if(dsName.equalsIgnoreCase("deliverymethods")) {
			return deliveryMethods;
		} else if(dsName.equalsIgnoreCase("deliveryoptions")) {
			return deliveryOptions;
		} else if(dsName.equalsIgnoreCase("filterSets")) {
			return filterSets;
		} else {
			return null;
		}
	}
	
	
	// getter/setters for private properties
	public DBDataSourceDao<EntityCode> getEntityCodes() { return entityCodes; }
	public DBDataSourceDao<DocumentFormat> getDocumentFormats() { return docFormats; }
	public DBDataSourceDao<Organization> getOrganizations() { return organizations; }
	public DBDataSourceDao<OrganizationContact> getContacts() { return contacts; }
	public DBDataSourceDao<DeliveryMethod> getDeliveryMethods() { return deliveryMethods; }
	public DBDataSourceDao<DeliveryOption> getDeliveryOptions() { return deliveryOptions; }
	public DBDataSourceDao<FilterSet> getFilterSets() { return filterSets; }
}
