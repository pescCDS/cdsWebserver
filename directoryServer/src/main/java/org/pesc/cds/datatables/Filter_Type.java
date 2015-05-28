package org.pesc.cds.datatables;

public enum Filter_Type {
	EQUALS	  (0, "EQUALS", "Filter type where the filter value is explicitely equal to another value"),
	SEARCH	  (1, "SEARCH", "Filter type where the filter value is similar to another value"),
	BETWEEN	  (2, "BETWEEN", "Filter type where the filter value is between two values"),
	SELECT	  (3, "SELECT", "Filter type where the filter value will be in a list of values"),
	CYCLE	  (4, "CYCLE", "Filter type (most likely date) where the filter value matches a particular cycle"),
	IN		  (5, "IN", "Filter type where the filter value is a list of values"),
	BEFORE    (6, "BEFORE", "Filter type where the expected value (date) is before the filter value"),
	AFTER	  (7, "AFTER", "Filter type where the expected value (date) is after the filter value"),
	YEAR	  (8, "YEAR", "Filter type where the filter value is just the year component of a date value"),
	MONTH	  (9, "MONTH", "Filter type where the filter value is a month"),
	MONTHDAY  (10, "MONTHDAY", "Filter type where the filter value is the month and day compenents of a date value"),
	MONTHYEAR (11, "MONTHYEAR", "Filter type where the filter value is the month and year components of a date value"),
	DAY		  (12, "DAY", "Filter type where the filter value is a list of numbers from 1 to 31"),
	WEEKDAY   (13, "WEEKDAY", "Filter type where the filter value is the name of a week day"),
	RELATIVE  (14, "RELATIVE", "Filter type where the filter value is a relative date phrase with a numeric value");
	
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
		case 6: return Filter_Type.BEFORE;
		case 7: return Filter_Type.AFTER;
		case 8: return Filter_Type.YEAR;
		case 9: return Filter_Type.MONTH;
		case 10: return Filter_Type.MONTHDAY;
		case 11: return Filter_Type.MONTHYEAR;
		case 12: return Filter_Type.DAY;
		case 13: return Filter_Type.WEEKDAY;
		case 14: return Filter_Type.RELATIVE;
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
		else if(v.equalsIgnoreCase("before")) { return Filter_Type.BEFORE; }
		else if(v.equalsIgnoreCase("after")) { return Filter_Type.AFTER; }
		else if(v.equalsIgnoreCase("year")) { return Filter_Type.YEAR; }
		else if(v.equalsIgnoreCase("month")) { return Filter_Type.MONTH; }
		else if(v.equalsIgnoreCase("monthday")) { return Filter_Type.MONTHDAY; }
		else if(v.equalsIgnoreCase("monthyear")) { return Filter_Type.MONTHYEAR; }
		else if(v.equalsIgnoreCase("day")) { return Filter_Type.DAY; }
		else if(v.equalsIgnoreCase("weekday")) { return Filter_Type.WEEKDAY; }
		else if(v.equalsIgnoreCase("relative")) { return Filter_Type.RELATIVE; }
		else { throw new Exception("Unknown Filter Type"); }
	}
	
	
	@Override
	public String toString() {
		return String.format("Filter_Type { code:%s, label:%s, description:%s }",this.code,this.label,this.description);
	}
}