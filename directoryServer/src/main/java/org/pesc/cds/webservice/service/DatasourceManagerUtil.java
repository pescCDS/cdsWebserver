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


public class DatasourceManagerUtil {
	private static DBDataSourceDao<EntityCode> entityCodes = buildEntityCodes();
	private static DBDataSourceDao<DocumentFormat> docFormats = buildDocFormats();
	private static DBDataSourceDao<Organization> organizations = buildOrganizations();
	private static DBDataSourceDao<OrganizationContact> contacts = buildContacts();
	private static DBDataSourceDao<DeliveryMethod> deliveryMethods = buildDeliveryMethods();
	private static DBDataSourceDao<DeliveryOption> deliveryOptions = buildDeliveryOptions();
	private static DBDataSourceDao<FilterSet> filterSets = buildFilterSets();
	
	
	private static DBDataSourceDao<EntityCode> buildEntityCodes() {
		return new EntityCodesDao();
	}
	private static DBDataSourceDao<DocumentFormat> buildDocFormats() {
		return new DocumentFormatsDao();
	}
	private static DBDataSourceDao<Organization> buildOrganizations() {
		return new OrganizationsDao();
	}
	private static DBDataSourceDao<OrganizationContact> buildContacts() {
		return new ContactsDao();
	}
	private static DBDataSourceDao<DeliveryMethod> buildDeliveryMethods() {
		return new DeliveryMethodsDao();
	}
	private static DBDataSourceDao<DeliveryOption> buildDeliveryOptions() {
		return new DeliveryOptionsDao();
	}
	private static DBDataSourceDao<FilterSet> buildFilterSets() {
		return new  FilterSetsDao();
	}
	
	
	// public class methods
	public static DBDataSourceDao<?> byName(String dsName) {
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
	public static DBDataSourceDao<EntityCode> getEntityCodes() { return entityCodes; }
	public static DBDataSourceDao<DocumentFormat> getDocumentFormats() { return docFormats; }
	public static DBDataSourceDao<Organization> getOrganizations() { return organizations; }
	public static DBDataSourceDao<OrganizationContact> getContacts() { return contacts; }
	public static DBDataSourceDao<DeliveryMethod> getDeliveryMethods() { return deliveryMethods; }
	public static DBDataSourceDao<DeliveryOption> getDeliveryOptions() { return deliveryOptions; }
	public static DBDataSourceDao<FilterSet> getFilterSets() { return filterSets; }
}
