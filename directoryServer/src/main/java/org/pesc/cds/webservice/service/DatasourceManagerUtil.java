package org.pesc.cds.webservice.service;

import org.pesc.cds.datatables.FilterSet;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.dao.DBDataSourceDao;
import org.pesc.edexchange.v1_0.dao.DocumentFormatsDao;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.pesc.edexchange.v1_0.dao.FilterSetsDao;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;


public class DatasourceManagerUtil {
	private static DBDataSourceDao<EntityCode> entityCodes = buildEntityCodes();
	private static DBDataSourceDao<DocumentFormat> docFormats = buildDocFormats();
	private static DBDataSourceDao<Organization> organizations = buildOrganizations();
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
	public static DBDataSourceDao<FilterSet> getFilterSets() { return filterSets; }
}
