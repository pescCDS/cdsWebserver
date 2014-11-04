package org.pesc.cds.datatables;

public enum Filter_Type {
	EQUALS (0, "EQUALS", "Filter type where the filter value is explicitely equal to another value"),
	SEARCH (1, "SEARCH", "Filter type where the filter value is similar to another value"),
	BETWEEN (2, "BETWEEN", "Filter type where the filter value is between two values"),
	SELECT (3, "SELECT", "Filter type where the filter value will be in a list of values"),
	CYCLE (4, "CYCLE", "Filter type (most likely date) where the filter value matches a particular cycle"),
	IN (5, "IN", "Filter type where the filter value is a list of values");
	// TODO year, month, month-day, day, weekday
	
	private Integer code;
	private String label;
	private String description;
	
	public Integer getCode() { return code; }
	public String getLabel() { return label; }
	public String getDescription() { return description; }
	
	private Filter_Type(Integer code, String label, String description) {
		this.code = code;
		this.label = label;
		this.description = description;
	}
	
	public static Filter_Type getType(Integer c) throws Exception {
		switch(c) {
		case 0: return Filter_Type.EQUALS;
		case 1: return Filter_Type.SEARCH;
		case 2: return Filter_Type.BETWEEN;
		case 3: return Filter_Type.SELECT;
		case 4: return Filter_Type.CYCLE;
		case 5: return Filter_Type.IN;
		default: throw new Exception("Unknown Filter Type");
		}
	}
	
	public static Filter_Type getType(String v) throws Exception {
		if(v.equalsIgnoreCase("equals")) { return Filter_Type.EQUALS; }
		else if(v.equalsIgnoreCase("search")) { return Filter_Type.SEARCH; }
		else if(v.equalsIgnoreCase("between")) { return Filter_Type.BETWEEN; }
		else if(v.equalsIgnoreCase("select")) { return Filter_Type.SELECT; }
		else if(v.equalsIgnoreCase("cycle")) { return Filter_Type.CYCLE; }
		else if(v.equalsIgnoreCase("in")) { return Filter_Type.IN; }
		else { throw new Exception("Unknown Filter Type"); }
	}
	
	
	@Override
	public String toString() {
		return String.format("Filter_Type { code:%s, label:%s, description:%s }",this.code,this.label,this.description);
	}
}