package org.pesc.cds.datatables;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class FilterSet {
	private Integer id;
	private Integer credentialId;
	private String category;
	private String name;
	private String description;
	private String table;
	
	// this is the field that stores the filters as a string (unprocessed)
	private String filtersJson;
	
	// these are the processed filters from the json string
	private List<Filter> filters;
	
	// Standard constructor
	public FilterSet() {}
	
	// Constructor for populating properties with fields from a database row
	// the database table stores the filters as text so the List filters property
	// will need to be processed after the String jsonFilters property has been set
	public FilterSet(Integer id, Integer credentialId, String category, String name, String description, String table, String filtersJson) {
		setId(id);
		setCredentialId(credentialId);
		setCategory(category);
		setName(name);
		setDescription(description);
		setTable(table);
		setFiltersJson(filtersJson);
		
		// now we need to parse the filtersJson into the List<Filter> filters field
		try {
			ObjectMapper filtersMapper = new ObjectMapper();
			filters = filtersMapper.readValue(getFiltersJson(), List.class);
		} 
		catch (JsonMappingException jsex) {} 
		catch(JsonParseException jsex) {} 
		catch(Exception ex) {}
	}
	
	/**
	 * Takes the List of Filters in the filters property and converts it to a JSON string
	 */
	public void stringifyFilters() {
		if(getFilters()!=null) {
			ObjectMapper jsonMapper = new ObjectMapper();
			try {
				setFiltersJson(jsonMapper.writeValueAsString(getFilters()));
			}
			catch (JsonGenerationException jsex) {}
			catch (JsonMappingException jsex) {}
			catch (Exception ex) {}
		}
	}
	
	/**
	 * Takes the JSON String value in filtersJson and parses it into a List of Filters
	 */
	public void parseJsonFilters() {
		if(getFilters()==null && getFiltersJson()!=null) {
			try {
				ObjectMapper filtersMapper = new ObjectMapper();
				setFilters(filtersMapper.readValue(getFiltersJson(), List.class));
			}
			catch (JsonMappingException jsex) {} 
			catch(JsonParseException jsex) {} 
			catch(Exception ex) {}
		}
	}
	
	public String toString() {
		return String.format(
			"FilterSet {%n  id: %s,%n  credentialId: %s,%n  category: %s,%n  name: %s,%n  description: %s,%n  table: %s,%n  filtersJson: %s,%n  filters: [%s]%n}",
			getId(),
			getCredentialId(),
			getCategory(),
			getName(),
			getDescription(),
			getTable(),
			getFiltersJson()==null ? "" : getFiltersJson(),
			getFilters()==null ? "" : StringUtils.join(getFilters().toArray(),",")
		);
	}
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Integer getCredentialId() { return credentialId; }
	public void setCredentialId(Integer credentialId) { this.credentialId = credentialId; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public String getTable() { return table; }
	public void setTable(String table) { this.table = table; }
	public String getFiltersJson() { return filtersJson; }
	public void setFiltersJson(String filtersJson) { this.filtersJson = filtersJson; }
	public List<Filter> getFilters() { return filters; }
	public void setFilters(List<Filter> filters) { this.filters = filters; }
}
