package org.pesc.cds.datatables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.pesc.cds.webservice.service.DatasourceManagerUtil;
import org.pesc.edexchange.v1_0.dao.DBDataSourceDao;

public class FiltersToHQLUtil {
	private static final Log log = LogFactory.getLog(FiltersToHQLUtil.class);
		
	private static DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();
	
	private static enum NumberType {
		INTEGER,
		FLOAT,
		BIGDECIMAL
	}
	
	private static HashMap<String, NumberType> columnNumberType = buildColumnNumberTypeMap();
	
	private static HashMap<String, NumberType> buildColumnNumberTypeMap() {
		HashMap<String, NumberType> retMap = new HashMap<String, NumberType>();
		retMap.put("document_format.formatInuseCount", NumberType.INTEGER);
		retMap.put("organization_entity_code.code", NumberType.INTEGER);
		retMap.put("filter_sets.credentialId", NumberType.INTEGER);
		return retMap;
	}
	
	// for custom Date filter type (CYCLE)
	private static HashMap<String, String> columnAliasMap = buildColumnAliasMap();
	
	// this map maps hibernate property names to actual database column names
	// it's needed only when they are different and the column is NOT a foreign key
	// I think the columns that are not dates can be removed
	private static HashMap<String, String> buildColumnAliasMap() {
		HashMap<String, String> retMap = new HashMap<String, String>();
		retMap.put("contacts.createdTime", "created_time");
		retMap.put("contacts.modifiedTime", "modified_time");
		retMap.put("organizations.createdTime", "created_time");
		retMap.put("organizations.modifiedTime", "modified_time");
		retMap.put("documentFormats.createdTime", "created_time");
		retMap.put("documentFormats.modifiedTime", "modified_time");
		retMap.put("entityCodes.createdTime", "created_time");
		retMap.put("entityCodes.modifiedTime", "modified_time");
		return retMap;
	}
	
	/**
	 * This method is needed for filters that have to use native sql on a column. 
	 * Normally this is just a simple mapping of hibernate column name to table name, but
	 * there is a more complicated case where the column requested is a generated type.<br>
	 * @param tableColumn The hibernate column
	 * @param table The table
	 * @return
	 */
	private static String createSqlColumn(String tableColumn, String table) {
		if(tableColumn==null || table==null) {
			return null;
		}
		String tableAndColumn = String.format("%s.%s", table, tableColumn);
		return columnAliasMap.get(tableAndColumn);
	}
	
	/**
	 * Takes a UTC unix timestamp and creates a <code>Calendar</code> that can be used in <code>.equals()</code> 
	 * methods on other <code>Calendar</code> instances with the same timezone.
	 * @param timestamp A UTC unix timestamp
	 * @return <b><code>Calendar</code></b> A <code>Calendar</code> that can be used to compare 
	 * against other <code>Calendar</code> instances in the same timezone.
	 */
	public static Calendar createCalendarFromTimestamp(long timestamp) {
		DateTime dtConverter = new DateTime(timestamp, DateTimeZone.UTC);
		return dtConverter.toLocalDate().toDateTimeAtStartOfDay().toGregorianCalendar();
	}
	
	/**
	 * Adds restrictions from the filters list into the Criteria.
	 * @param ct The criteria to be modified.
	 * @param filters A list of filters to be applied as restrictions to the criteria.
	 */
	public static void applyFiltersToCriteria(Criteria ct, List<Map<String, Object>> filters) {
		for(ListIterator<Map<String, Object>> iter = filters.listIterator(); iter.hasNext();) {
			Map<String, Object> filter = iter.next();
			addRestriction(ct, filter);
		}
	}
	
	/**
	 * Adds restrictions from the filters list into the Criteria, but not filters in the exclude list.
	 * @param ct The criteria to be modified.
	 * @param filters A list of filters to be applied as restrictions to the criteria.
	 * @param excludeFilters A list of filters in the <code>filters</code> parameter that shouldn't be added to the criteria.
	 */
	public static void applyFiltersToCriteria(Criteria ct, List<Map<String, Object>> filters, List<String> excludeFilters) {
		for(ListIterator<Map<String, Object>> iter = filters.listIterator(); iter.hasNext();) {
			Map<String, Object> filter = iter.next();
			if(!excludeFilters.contains(filter.get("column").toString())) {
				addRestriction(ct, filter);
			}
		}
	}
	
	/**
	 * Adds the passed filter as a restriction to the passed criteria.
	 * @param ct The criteria to be modified.
	 * @param filter A filter to be applied as a restriction on the criteria.
	 * @return <b><code>Criterion</code></b> A restriction applied to the criteria based on the filter.
	 * @throws Throwable 
	 */
	public static Criterion addRestriction(Criteria ct, Map<String, ? extends Object> filter) {
		Criterion retC = null;
		try {
			Map<String, Object> filterValue = (Map<String, Object>)filter.get("filterValue");
			
			String filterTable = filter.get("table").toString();
			String filterType = filter.get("type").toString();
			
			Column_Type columnType = Column_Type.getType(filterType);
			
			Boolean isMultiColumn = false;
			if(filter.get("column") instanceof ArrayList) {
				isMultiColumn = true;
			}
			
			switch(columnType) {
			case TEXT:
				if(isMultiColumn) {
					ct.add(retC = processTextFilter((ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processTextFilter(filter.get("column").toString(), filterValue));
				}
				break;
			case NUMBER:
				if(isMultiColumn) {
					ct.add(retC = processNumberFilter(filterTable, (ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processNumberFilter(filterTable, filter.get("column").toString(), filterValue));
				}
				break;
			case DATE:
				if(isMultiColumn) {
					ct.add(retC = processDateFilter(filterTable, (ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processDateFilter(filterTable, filter.get("column").toString(), filterValue));
				}
				break;
			case BOOLEAN:
				if(isMultiColumn) {
					ct.add(retC = processBooleanFilter((ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processBooleanFilter(filter.get("column").toString(), filterValue));
				}
				break;
			case ENUM:
				ct.add(retC = processEnumFilter(filter.get("column").toString(), filterValue));
				break;
			case BIGLIST:
				if(isMultiColumn) {
					ct.add(retC = processBiglistFilter((ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processBiglistFilter(filter.get("column").toString(), filterValue));
				}
				break;
			}
			
			if(retC==null) {
				throw new Exception("Unknown filter type to add");
			}
		
		} catch(Throwable ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		
		return retC;
	}
	
	// TODO public methods for each of the process?Filter(), applyTextFilter(?, Map filter)
	
	private static Criterion processTextFilter(String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		String textValue = filterValue.get("value").toString();
		Filter_Type operator = Filter_Type.getType(filterValue.get("operator").toString());
		switch(operator) {
		case EQUALS:
			r = Restrictions.eq(column, textValue);
			break;
		case SEARCH:
			r = Restrictions.like(column, textValue, MatchMode.ANYWHERE);
			break;
		}
		return r;
	}
	private static Disjunction processTextFilter(List<String> column, Map<String, Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		String textValue = filterValue.get("value").toString();
		Filter_Type operator = Filter_Type.getType(filterValue.get("operator").toString());
		switch(operator) {
		case EQUALS:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.eq(col, textValue));
			}
			break;
		case SEARCH:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.ilike(col, textValue));
			}
			break;
		}
		return d;
	}
	
	
	private static Criterion processNumberFilter(String table, String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			switch(columnNumberType.get(String.format("%s.%s", table, column))) {
			case INTEGER:
				r = Restrictions.eq(column, new Integer(filterValue.get("value").toString()));
				break;
			case FLOAT:
				r = Restrictions.eq(column, new Float(filterValue.get("value").toString()));
				break;
			case BIGDECIMAL:
				r = Restrictions.eq(column, new BigDecimal(filterValue.get("value").toString()));
				break;
			}
			
			break;
		case BETWEEN:
			switch(columnNumberType.get(String.format("%s.%s", table, column))) {
			case INTEGER:
				r = Restrictions.between(column, new Integer(filterValue.get("from").toString()), new Integer(filterValue.get("to").toString()));
				break;
			case FLOAT:
				r = Restrictions.between(column, new Float(filterValue.get("from").toString()), new Float(filterValue.get("to").toString()));
				break;
			case BIGDECIMAL:
				r = Restrictions.between(column, new BigDecimal(filterValue.get("from").toString()), new BigDecimal(filterValue.get("to").toString()));
				break;
			}
			break;
		case SELECT:
			ArrayList lookupNumberList = null;
			switch(columnNumberType.get(String.format("%s.%s", table, column))) {
			case INTEGER:
				lookupNumberList = new ArrayList<Integer>();
				for(Object num : (ArrayList<Object>)filterValue.get("value")) {
					lookupNumberList.add(new Integer(num.toString()));
				}
				break;
			case FLOAT:
				lookupNumberList = new ArrayList<Float>();
				for(Object num : (ArrayList<Object>)filterValue.get("value")) {
					lookupNumberList.add(new Float(num.toString()));
				}
				break;
			case BIGDECIMAL:
				lookupNumberList = new ArrayList<BigDecimal>();
				for(Object num : (ArrayList<Object>)filterValue.get("value")) {
					lookupNumberList.add(new BigDecimal(num.toString()));
				}
				break;
			}
			r = Restrictions.in(column, lookupNumberList);
			break;
		}
		return r;
	}
	private static Disjunction processNumberFilter(String table, List<String> column, Map<String, Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			for(String col : column) {
				switch(columnNumberType.get(String.format("%s.%s", table, col))) {
				case INTEGER:
					d.add(Restrictions.eq(col, new Integer(filterValue.get("value").toString())));
					break;
				case FLOAT:
					d.add(Restrictions.eq(col, new Float(filterValue.get("value").toString())));
					break;
				case BIGDECIMAL:
					d.add(Restrictions.eq(col, new BigDecimal(filterValue.get("value").toString())));
					break;
				}
			}
			break;
		case BETWEEN:
			for(String col : column) {
				switch(columnNumberType.get(String.format("%s.%s", table, col))) {
				case INTEGER:
					d.add(Restrictions.between(col, new Integer(filterValue.get("from").toString()), new Integer(filterValue.get("to").toString())));
					break;
				case FLOAT:
					d.add(Restrictions.between(col, new Float(filterValue.get("from").toString()), new Float(filterValue.get("to").toString())));
					break;
				case BIGDECIMAL:
					d.add(Restrictions.between(col, new BigDecimal(filterValue.get("from").toString()), new BigDecimal(filterValue.get("to").toString())));
					break;
				}
			}
			break;
		case SELECT:
			for(String col : column) {
				ArrayList lookupNumberList = null;
				switch(columnNumberType.get(String.format("%s.%s", table, col))) {
				case INTEGER:
					lookupNumberList = new ArrayList<Integer>();
					for(Object num : (ArrayList<Object>)filterValue.get("value")) {
						lookupNumberList.add(new Integer(num.toString()));
					}
					break;
				case FLOAT:
					lookupNumberList = new ArrayList<Float>();
					for(Object num : (ArrayList<Object>)filterValue.get("value")) {
						lookupNumberList.add(new Float(num.toString()));
					}
					break;
				case BIGDECIMAL:
					lookupNumberList = new ArrayList<BigDecimal>();
					for(Object num : (ArrayList<Object>)filterValue.get("value")) {
						lookupNumberList.add(new BigDecimal(num.toString()));
					}
					break;
				}
				d.add(Restrictions.in(col, lookupNumberList));
			}
			break;
		}
		return d;
	}
	
	private static Criterion processDateFilter(String table, String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case MONTH:
			// ASSERTION: month will be zero-based
			// do a check on the month value to keep it within range
			Integer m1 = (Integer)filterValue.get("month");
			if(m1<0) {
				m1 = 1;
			} else if(m1>12) {
				m1 = 12;
			} else {
				m1++;
			}
			
			r = Restrictions.sqlRestriction( String.format("MONTH(%s) = %d", createSqlColumn(column, table), m1) );
			break;
		
		case MONTHYEAR:
			// filterValue:{month:<int>, year:<int>,...}
			Calendar monthStartDate = createCalendarFromTimestamp((long)filterValue.get("start"));
			Calendar monthEndDate = createCalendarFromTimestamp(monthStartDate.getTimeInMillis());
			monthEndDate.set(Calendar.DATE, monthEndDate.getActualMaximum(Calendar.DATE));
			monthEndDate.set(Calendar.HOUR_OF_DAY, 23);
			monthEndDate.set(Calendar.MINUTE, 59);
			monthEndDate.set(Calendar.SECOND, 59);
			monthEndDate.set(Calendar.MILLISECOND, 999);
			
			r = Restrictions.between(column, monthStartDate, monthEndDate);
			break;
		
		case YEAR:
			Calendar startDay = createCalendarFromTimestamp((long)filterValue.get("start"));
			Calendar endDay = createCalendarFromTimestamp((long)filterValue.get("start"));
			endDay.set(Calendar.DAY_OF_YEAR, endDay.getActualMaximum(Calendar.DAY_OF_YEAR));
			endDay.set(Calendar.HOUR_OF_DAY, 23);
			endDay.set(Calendar.MINUTE, 59);
			endDay.set(Calendar.SECOND, 59);
			endDay.set(Calendar.MILLISECOND, 999);
			
			r = Restrictions.between(column, startDay, endDay);
			break;
		
		case EQUALS:
			long filterTime = (long)filterValue.get("value");
			r = Restrictions.eq(column, createCalendarFromTimestamp(filterTime));
			break;
		
		case BEFORE:
			Calendar dtb4 = createCalendarFromTimestamp((long)filterValue.get("value"));
			r = Restrictions.lt(column, dtb4);
			break;
		
		case AFTER:
			Calendar dtAfter = createCalendarFromTimestamp((long)filterValue.get("value"));
			r = Restrictions.gt(column, dtAfter);
			break;
		
		case BETWEEN:
			Calendar from = createCalendarFromTimestamp((long)filterValue.get("fromDate"));
			Calendar to = createCalendarFromTimestamp((long)filterValue.get("toDate"));
			// set time parts to end of day
			to.set(Calendar.HOUR_OF_DAY, 23);
			to.set(Calendar.MINUTE, 59);
			to.set(Calendar.SECOND, 59);
			to.set(Calendar.MILLISECOND, 999);
			
			r = Restrictions.between(column, from, to);
			break;
		
		case SELECT:
			// the select widget will save the list of dates as an array of {date:<Date>, timestamp:<Long>} objects
			// so we need to convert to a list of Dates or Calendars
			ArrayList<Calendar> fvalues = new ArrayList<Calendar>();
			List<HashMap<String, Object>> datesList = (ArrayList<HashMap<String, Object>>)filterValue.get("value");
			
			for(ListIterator<HashMap<String, Object>> dIter = datesList.listIterator(); dIter.hasNext();) {
				HashMap<String, Object> dateHash = dIter.next();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((Long)dateHash.get("timestamp"));
				fvalues.add((Calendar)cal.clone());
			}
			r = Restrictions.in(column, fvalues);
			break;
		}
		
		return r;
	}
	
	
	private static Disjunction processDateFilter(String table, List<String> column, Map<String, Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case MONTH:
			Integer m1 = (Integer)filterValue.get("month");
			if(m1<0) {
				m1 = 1;
			} else if(m1>12) {
				m1 = 12;
			} else {
				m1++;
			}
			for(String col : column) {
				d.add( Restrictions.sqlRestriction( String.format("MONTH(%s) = %d", createSqlColumn(col, table), m1)) );
			}
			break;
		
		case MONTHYEAR:
			Calendar monthStartDate = createCalendarFromTimestamp((long)filterValue.get("start"));
			Calendar monthEndDate = createCalendarFromTimestamp(monthStartDate.getTimeInMillis());
			monthEndDate.set(Calendar.DATE, monthEndDate.getActualMaximum(Calendar.DATE));
			monthEndDate.set(Calendar.HOUR_OF_DAY, 23);
			monthEndDate.set(Calendar.MINUTE, 59);
			monthEndDate.set(Calendar.SECOND, 59);
			monthEndDate.set(Calendar.MILLISECOND, 999);
			
			for(String col : column) {
				d.add( Restrictions.between(col, monthStartDate, monthEndDate) );
			}
			break;
		
		case YEAR:
			Calendar startDay = createCalendarFromTimestamp((long)filterValue.get("start"));
			Calendar endDay = createCalendarFromTimestamp((long)filterValue.get("start"));
			endDay.set(Calendar.DAY_OF_YEAR, endDay.getActualMaximum(Calendar.DAY_OF_YEAR));
			endDay.set(Calendar.HOUR_OF_DAY, 23);
			endDay.set(Calendar.MINUTE, 59);
			endDay.set(Calendar.SECOND, 59);
			endDay.set(Calendar.MILLISECOND, 999);
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.between(col, startDay, endDay));
			}
			break;
		
		case EQUALS:
			long filterTime = (long)filterValue.get("value");
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.eq(col, createCalendarFromTimestamp(filterTime)));
			}
			break;
		
		case BEFORE:
			Calendar dtb4 = createCalendarFromTimestamp((long)filterValue.get("value"));
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.lt(col, dtb4));
			}
			break;
		
		case AFTER:
			Calendar dtAfter = createCalendarFromTimestamp((long)filterValue.get("value"));
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.gt(col, dtAfter));
			}
			break;
		
		case BETWEEN:
			Calendar from = createCalendarFromTimestamp((long)filterValue.get("fromDate"));
			Calendar to = createCalendarFromTimestamp((long)filterValue.get("toDate"));
			// set time parts to end of day
			to.set(Calendar.HOUR_OF_DAY, 23);
			to.set(Calendar.MINUTE, 59);
			to.set(Calendar.SECOND, 59);
			to.set(Calendar.MILLISECOND, 999);
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add( Restrictions.between(col, from, to) );
			}
			break;
		
		case SELECT:
			ArrayList<Calendar> fvalues = new ArrayList<Calendar>();
			List<HashMap<String, Object>> datesList = (ArrayList<HashMap<String, Object>>)filterValue.get("value");
			
			for(ListIterator<HashMap<String, Object>> dIter = datesList.listIterator(); dIter.hasNext();) {
				HashMap<String, Object> dateHash = dIter.next();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((Long)dateHash.get("timestamp"));
				fvalues.add((Calendar)cal.clone());
			}
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add( Restrictions.in(col, fvalues) );
			}
			break;
		}
		
		return d;
	}
	
	
	private static Criterion processBooleanFilter(String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			String bString = filterValue.get("value").toString();
			Boolean b = false;
			if("1".equals(bString)) { b = true; }
			else if("0".equals(bString)) { b = false; }
			else { b = Boolean.parseBoolean(bString); }
			
			int bool_numeric = b?1:0;
			
			r = Restrictions.eq(column, bool_numeric);
			break;
		}
		return r;
	}
	private static Disjunction processBooleanFilter(List<String> column, Map<String, Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			String bString = filterValue.get("value").toString();
			Boolean b = false;
			if("1".equals(bString)) { b = true; }
			else if("0".equals(bString)) { b = false; }
			else { b = Boolean.parseBoolean(bString); }
			
			int bool_numeric = b?1:0;
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add( Restrictions.eq(col, bool_numeric) );
			}
			break;
		}
		return d;
	}
	
	
	private static Criterion processEnumFilter(String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			ArrayList inValues = new ArrayList();
			ArrayList<Map> filterInValues = (ArrayList)filterValue.get("value");
			Boolean valueOnly = filterValue.containsKey("valueOnly") ? 
				Boolean.parseBoolean(filterValue.get("valueOnly").toString()) :
				false;
			String valueKey = filterValue.get("valueKey").toString();
			try {
				for(Map<String, Object> inVal : filterInValues) {
					if(valueOnly) {
						inValues.add(inVal.get(valueKey).toString());
					} else {
						Integer id = Integer.parseInt(inVal.get(valueKey).toString());
						inValues.add(((DBDataSourceDao) DatasourceManagerUtil.getInstance().byName(filterValue.get("table").toString())).byId(id));
					}
				}
			} catch(NumberFormatException nfe) {
				log.error(nfe.getMessage());
			}
			
			r = Restrictions.in(column, inValues);
			
			break;
		}
		return r;
	}
	
	
	private static Criterion processBiglistFilter(String column, Map<String, Object> filterValue) throws Throwable {
		Criterion r = null;
		String tableKey = filterValue.get("table").toString();
		String valueKey = filterValue.get("valueKey").toString();
		HashMap<String, Object> filterValueMap = (HashMap<String, Object>)filterValue.get("value");
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			r = Restrictions.eq(String.format("%s.%s", column, valueKey), filterValueMap.get(valueKey));
			break;
		}
		return r;
	}
	
	private static Disjunction processBiglistFilter(List<String> column, Map<String, Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		String tableKey = filterValue.get("table").toString();
		String valueKey = filterValue.get("valueKey").toString();
		HashMap<String, ? extends Object> filterValueMap = (HashMap<String, Object>)filterValue.get("value");
		Filter_Type type = Filter_Type.getType(filterValue.get("operator").toString());
		switch(type) {
		case EQUALS:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.eq(String.format("%s.%s",col,valueKey), filterValueMap.get(valueKey)));
			}
			break;
		}
		return d;
	}
}
