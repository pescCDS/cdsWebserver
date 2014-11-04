package org.pesc.cds.datatables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class DataTablesRequest {
	private Integer draw = 1;
	private Integer start = 0;
	private Integer length = 10;
	private Map<String,? extends Object> search;
	private List<HashMap<String, ? extends Object>> order;
	private List<HashMap<String, ? extends Object>> columns;
	
	private String table;
	private List<HashMap<String, ? extends Object>> columnfilters;
	
	public DataTablesRequest() {
		search = new HashMap();
		order = new ArrayList<HashMap<String, ? extends Object>>();
		columns = new ArrayList<HashMap<String, ? extends Object>>();
		columnfilters = new ArrayList<HashMap<String, ? extends Object>>();
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
	public Map<String,? extends Object> getSearch() {
		return search;
	}
	public void setSearch(Map<String,? extends Object> search) {
		this.search = search;
	}
	public List<HashMap<String,? extends Object>> getOrder() {
		return order;
	}
	public void setOrder(List<HashMap<String,? extends Object>> order) {
		this.order = order;
	}
	public List<HashMap<String,? extends Object>> getColumns() {
		return columns;
	}
	public void setColumns(List<HashMap<String,? extends Object>> columns) {
		this.columns = columns;
	}
	public List<HashMap<String,? extends Object>> getColumnfilters() {
		return columnfilters;
	}
	public String getTable() { return this.table; }
	public void setTable(String table) { this.table = table; }
	public void setColumnfilters(List<HashMap<String,? extends Object>> colfilters) {
		this.columnfilters = colfilters;
	}
	
	
	
	public String toString() {
		List<String> orders = new ArrayList<String>(order.size());
		for(Iterator<HashMap<String, ? extends Object>> iter = order.iterator(); iter.hasNext();) {
			HashMap<String, ? extends Object> ord = (HashMap<String, ? extends Object>)iter.next();
			List<String> oProps = new ArrayList<String>(ord.keySet().size());
			for(Iterator<String> iter2 = ord.keySet().iterator(); iter2.hasNext();) {
				String ordKey = iter2.next();
				oProps.add(ordKey+":"+ord.get(ordKey));
			}
			orders.add(String.format("{%n    %s%n}%n",StringUtils.join(oProps.toArray(),",")));
		}
		
		List<String> columnsArr = new ArrayList<String>(columns.size());
		for(Iterator<HashMap<String, ? extends Object>> iter = columns.iterator(); iter.hasNext();) {
			HashMap<String, ? extends Object> col = (HashMap<String, ? extends Object>)iter.next();
			List<String> colProps = new ArrayList<String>(col.keySet().size());
			for(Iterator<String> iter2 = col.keySet().iterator(); iter2.hasNext();) {
				String colKey = iter2.next();
				colProps.add(colKey+":"+col.get(colKey));
			}
			columnsArr.add(String.format("{%n    %s%n}%n",StringUtils.join(colProps.toArray(),",")));
		}
		
		List<String> columnFilters = new ArrayList<String>(columnfilters.size());
		for(Iterator<HashMap<String, ? extends Object>> iter = columnfilters.iterator(); iter.hasNext();) {
			HashMap<String, ? extends Object> col = (HashMap<String, ? extends Object>)iter.next();
			List<String> colProps = new ArrayList<String>(col.keySet().size());
			for(Iterator<String> iter2 = col.keySet().iterator(); iter2.hasNext();) {
				String colKey = iter2.next();
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
}
