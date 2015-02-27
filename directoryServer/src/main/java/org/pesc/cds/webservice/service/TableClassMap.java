package org.pesc.cds.webservice.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.DefaultMapEntry;
import org.apache.commons.collections.MapUtils;
import org.pesc.edexchange.v1_0.DeliveryMethod;
import org.pesc.edexchange.v1_0.DeliveryOption;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.OrganizationContact;
import org.pesc.edexchange.v1_0.OrganizationCredential;

public class TableClassMap {
	private static Map<String, Class> tableMap = MapUtils.putAll(
		new HashMap(),
		new Map.Entry[] {
			new DefaultMapEntry("organizations", Organization.class),
			new DefaultMapEntry("entityCodes", EntityCode.class),
			new DefaultMapEntry("documentFormats", DocumentFormat.class),
			new DefaultMapEntry("users", OrganizationCredential.class),
			new DefaultMapEntry("contacts", OrganizationContact.class),
			new DefaultMapEntry("deliveryMethods", DeliveryMethod.class),
			new DefaultMapEntry("deliveryOptions", DeliveryOption.class)
		}
	);
	
	public static Class getClass(String tableName) throws Exception {
		if(tableMap.containsKey(tableName)) {
			return tableMap.get(tableName);
		} else {
			throw new Exception("table key not found");
		}
	}
}
