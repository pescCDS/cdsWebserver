package org.pesc.cds.datatables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class DataTablesRequest {
	private Integer draw = 1;
	private Integer start = 0;
	private Integer length = 10;
	private Map<String, Object> search;
	private List<HashMap<String, Object>> order;
	private List<HashMap<String, Object>> columns;
	
	private String table;
	private List<HashMap<String, Object>> columnfilters;
	
	
	public DataTablesRequest() {
		search = new HashMap();
		order = new ArrayList<HashMap<String, Object>>();
		columns = new ArrayList<HashMap<String, Object>>();
		columnfilters = new ArrayList<HashMap<String, Object>>();
	}
	
	public String toString() {
		List<String> orders = new ArrayList<String>(order.size());
		for(HashMap<String, Object> ord : order) {
			List<String> oProps = new ArrayList<String>(ord.keySet().size());
			for(String ordKey : ord.keySet()) {
				oProps.add(ordKey+":"+ord.get(ordKey));
			}
			orders.add(String.format("{%n    %s%n}%n",StringUtils.join(oProps.toArray(),",")));
		}
		
		List<String> columnsArr = new ArrayList<String>(columns.size());
		for(HashMap<String, Object> col : columns) {
			List<String> colProps = new ArrayList<String>(col.keySet().size());
			for(String colKey : col.keySet()) {
				colProps.add(colKey+":"+col.get(colKey));
			}
			columnsArr.add(String.format("{%n    %s%n}%n",StringUtils.join(colProps.toArray(),",")));
		}
		
		List<String> columnFilters = new ArrayList<String>(columnfilters.size());
		for(HashMap<String, Object> col : columnfilters) {
			List<String> colProps = new ArrayList<String>(col.keySet().size());
			for(String colKey : col.keySet()) {
				colProps.add(colKey+":"+col.get(colKey));
			}
			columnFilters.add(String.format("{%n    %s%n}%n",StringUtils.join(colProps.toArray(),",")));
		}
		
		
		return String.format(
			"DataTablesRequest {%n  draw:%s,%n  start:%s,%n  length:%s,%n  search.value:%s,%n  search.regex:%s,%n  orders:%n  %s,%n  columns:%n  %s,%n  columnfilters:%n  %s%n}",
			draw,
			start,
			length,
			search.get("value"),
			search.get("regex"),
			StringUtils.join(orders.toArray(),","),
			StringUtils.join(columns.toArray(),","),
			StringUtils.join(columnfilters.toArray(),",")
		);
	}
	
	
	public Integer getDraw() {
		return draw;
	}
	public void setDraw(Integer draw) {
		this.draw = draw;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Map<String,Object> getSearch() {
		return search;
	}
	public void setSearch(Map<String,Object> search) {
		this.search = search;
	}
	public List<HashMap<String,Object>> getOrder() {
		return order;
	}
	public void setOrder(List<HashMap<String,Object>> order) {
		this.order = order;
	}
	public List<HashMap<String,Object>> getColumns() {
		return columns;
	}
	public void setColumns(List<HashMap<String,Object>> columns) {
		this.columns = columns;
	}
	public List<HashMap<String,Object>> getColumnfilters() {
		return columnfilters;
	}
	public String getTable() { return this.table; }
	public void setTable(String table) { this.table = table; }
	public void setColumnfilters(List<HashMap<String,Object>> colfilters) {
		this.columnfilters = colfilters;
	}
}
