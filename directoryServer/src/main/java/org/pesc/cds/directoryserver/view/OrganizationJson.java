package org.pesc.cds.directoryserver.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrganizationJson {
	private static final Log log = LogFactory.getLog(OrganizationJson.class);
	private Integer organization_id;
	private String organization_id_type;
	private String organization_name;
	private String organization_subcode;
	private Integer organization_entity_code;
	private String organization_ein;
	private String organization_site_url;
	private String description;
	private String terms_of_use;
	private String privacy_policy;
	
	
	public Integer getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(Integer organization_id) {
		this.organization_id = organization_id;
	}
	public String getOrganization_id_type() {
		return organization_id_type;
	}
	public void setOrganization_id_type(String organization_id_type) {
		this.organization_id_type = organization_id_type;
	}
	public String getOrganization_name() {
		return organization_name;
	}
	public void setOrganization_name(String organization_name) {
		this.organization_name = organization_name;
	}
	public String getOrganization_subcode() {
		return organization_subcode;
	}
	public void setOrganization_subcode(String organization_subcode) {
		this.organization_subcode = organization_subcode;
	}
	public Integer getOrganization_entity_code() {
		return organization_entity_code;
	}
	public void setOrganization_entity_code(Integer organization_entity_code) {
		this.organization_entity_code = organization_entity_code;
	}
	public String getOrganization_ein() {
		return organization_ein;
	}
	public void setOrganization_ein(String organization_ein) {
		this.organization_ein = organization_ein;
	}
	public String getOrganization_site_url() {
		return organization_site_url;
	}
	public void setOrganization_site_url(String organization_site_url) {
		this.organization_site_url = organization_site_url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTerms_of_use() {
		return terms_of_use;
	}
	public void setTerms_of_use(String terms_of_use) {
		this.terms_of_use = terms_of_use;
	}
	public String getPrivacy_policy() {
		return privacy_policy;
	}
	public void setPrivacy_policy(String privacy_policy) {
		this.privacy_policy = privacy_policy;
	}
	
	public String toString() {
		return String.format(
			"OrganizationJson {%n  organization_id:%s,%n  organization_id_type:%s,%n  organization_name:%s,%n  organization_subcode:%s,%n  "+
			"organization_entity_code:%s,%n  organization_ein:%s,%n  organization_site_url:%s,%n  description:%s,%n  terms_of_use:%s,%n  privacy_policy:%s,%n}",
			organization_id,
			organization_id_type,
			organization_name,
			organization_subcode,
			organization_entity_code,
			organization_ein,
			organization_site_url,
			description,
			terms_of_use,
			privacy_policy
		);
	}
}
