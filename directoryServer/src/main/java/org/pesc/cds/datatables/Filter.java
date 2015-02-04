package org.pesc.cds.datatables;

import java.util.Map;

public class Filter {
	private String table;
	private String type;
	private String column;
	private String label;
	
	/* filterValue varies a few different ways
	 * 
	 * properties consistent in all types of filter widgets
	 * type, description
	 * 
	 * [text:equal], [text:search], [boolean:equal], [number:equals], [number:select], [date:equals], [date:select], [date:year]
	 * type (String)
	 * value (String)
	 * description (String)
	 * 
	 * [number:between]
	 * type (String)
	 * from (Number)
	 * to (Number)
	 * description (String)
	 * 
	 * [date:cycle]
	 * type (String)
	 * monthYear ({date: (Date), timestamp: (Integer)})
	 * cycle (Integer)
	 * cycleMap ([{label: (String), value: (Integer)},...])
	 * description (String)
	 * 
	 * [date:between]
	 * type (String)
	 * fromDate (Date)
	 * toDate (Date)
	 * description (String)
	 * 
	 * [enum:in]
	 * type (String)
	 * column (String)
	 * value ([{code: (Integer), name: (String)},...])
	 * description (String)
	 * 
	 * [biglist:equals]
	 * type (String)
	 * table (String)
	 * column (String)
	 * displayKey (String)
	 * valueKey (String)
	 * value ({[displayKey]: ?, [valueKey]: ?,...})
	 * description (String)
	 */
	private Map<String, ? extends Object> filterValue;
	
	// standard no-argument constructor
	public Filter() {}
	
	// constructor with all properties arguments
	public Filter(String table, String type, String column, String label, Map<String, ? extends Object> fiterValue) {
		this.table = table;
		this.type = type;
		this.column = column;
		this.label = label;
		this.filterValue = fiterValue;
	}
	
	// toString() override
	public String toString() {
		return String.format(
			"Filter {%n  table: %s,%n  type: %s,%n  column: %s,%n  label: %s,%n  filterValue: %s%n}",
			table, type, column, label, filterValue
		);
	}
	
	// standard getter/setters
	public String getTable() { return table; }
	public void setTable(String table) { this.table = table; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getColumn() { return column; }
	public void setColumn(String column) { this.column = column; }
	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }
	public Map<String, ? extends Object> getFilterValue() { return filterValue; }
	public void setFilterValue(Map<String, ? extends Object> filterValue) { this.filterValue = filterValue; }
}
