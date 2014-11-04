package org.pesc.cds.datatables;

public enum Column_Type {
	TEXT (0, "TEXT", "Text Column Type"),
	NUMBER (1, "NUMBER", "Number Column Type"),
	DATE (2, "DATE", "Date Column Type"),
	BOOLEAN (3, "BOOLEAN", "Boolean Column Type"),
	ENUM (4, "ENUM", "Enum Column Type");
	// TODO biglist, foreignkey
	
	private Integer code;
	private String label;
	private String description;
	
	public Integer getCode() { return code; }
	public String getLabel() { return label; }
	public String getDescription() { return description; }
	
	private Column_Type(Integer code, String label, String description) {
		this.code = code;
		this.label = label;
		this.description = description;
	}
	
	public static Column_Type getType(Integer c) throws Exception {
		switch(c) {
		case 0: return Column_Type.TEXT;
		case 1: return Column_Type.NUMBER;
		case 2: return Column_Type.DATE;
		case 3: return Column_Type.BOOLEAN;
		case 4: return Column_Type.ENUM;
		default: throw new Exception("Unkown Datatables Type");
		}
	}
	public static Column_Type getType(String v) throws Exception {
		if(v.equalsIgnoreCase("text")) { return Column_Type.TEXT; }
		else if(v.equalsIgnoreCase("number")) { return Column_Type.NUMBER; }
		else if(v.equalsIgnoreCase("date")) { return Column_Type.DATE; }
		else if(v.equalsIgnoreCase("boolean")) { return Column_Type.BOOLEAN; }
		else if(v.equalsIgnoreCase("enum")) { return Column_Type.ENUM; }
		else { throw new Exception("Unknown Datatables Type"); }
	}
	
	@Override
	public String toString() {
		return String.format("Datatables_Type { code:%s, label:%s, description:%s }", code, label, description);
	}
}