package org.pesc.cds.datatables;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.pesc.cds.webservice.service.DatasourceManagerUtil;
import org.pesc.edexchange.v1_0.dao.DBDataSourceDao;

public class FiltersToHQLUtil {
private static final Log log = LogFactory.getLog(FiltersToHQLUtil.class);
	
	private static DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();
	
	// for custom Date filter type (CYCLE)
	private static HashMap<String, String> columnAliasMap = buildColumnAliasMap();
	
	// this map maps hibernate property names to actual database column names
	// it's needed only when they are different and the column is NOT a foreign key
	// I think the columns that are not dates can be removed
	private static HashMap<String, String> buildColumnAliasMap() {
		HashMap<String, String> retMap = new HashMap<String, String>();
		//retMap.put("authorizedHours", "authorized_hours");
		//retMap.put("phoneCellPrimary", "phone_cell_primary");
		//retMap.put("phoneCellSecondary", "phone_cell_secondary");
		//retMap.put("phoneHomePrimary", "phone_home_primary");
		//retMap.put("phoneHomeSecondary", "phone_home_secondary");
		//retMap.put("serviceCoordinator", "service_coordinator");
		retMap.put("authExpires", "auth_expires");
		retMap.put("lastReport", "last_report");
		retMap.put("endDate", "end_date");
		retMap.put("intakeDate", "intake_date");
		retMap.put("initialEval", "initial_eval");
		retMap.put("serviceDate",  "service_date");
		retMap.put("serviceDate|A", "completed");
		retMap.put("dateEntered",  "date_entered");
		retMap.put("dateEntered|A", "entered");
		return retMap;
	}
	
	private static String createSqlColumn(String tableColumn, String table) {
		String s = String.format("%s", tableColumn);
		// the only time we need to swap out the column name is for tables that
		// have columns with different names than the hibernate property value
		if(table.equals("clients") || table.equals("timesheets") || table.equals("r1")) {
			s = columnAliasMap.get(tableColumn);
		}
		return s;
	}
	
	// adds restrictions from the filters list into the Criteria
	public static void applyFiltersToCriteria(Criteria ct, List<Map<String,? extends Object>> filters) {
		for(ListIterator<Map<String,? extends Object>> iter = filters.listIterator(); iter.hasNext();) {
			Map<String,? extends Object> filter = iter.next();
			addRestriction(ct, filter);
		}
	}
	
	// adds restrictions from the filters list into the Criteria, but not filters in the exclude list
	public static void applyFiltersToCriteria(Criteria ct, List<Map<String,? extends Object>> filters, List<String> excludeFilters) {
		for(ListIterator<Map<String,? extends Object>> iter = filters.listIterator(); iter.hasNext();) {
			Map<String,? extends Object> filter = iter.next();
			if(!excludeFilters.contains(filter.get("column").toString())) {
				addRestriction(ct, filter);
			}
		}
	}
	
	// adds the filter as a restriction to the criteria
	public static Criterion addRestriction(Criteria ct, Map<String, ? extends Object> filter) {
		Criterion retC = null;
		try {
			Map<String, ? extends Object> filterValue = (Map<String, ? extends Object>)filter.get("filterValue");
			
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
					ct.add(retC = processNumberFilter((ArrayList<String>)filter.get("column"), filterValue));
				} else {
					ct.add(retC = processNumberFilter(filter.get("column").toString(), filterValue));
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
		}
		
		return retC;
	}
	
	// TODO public methods for each of the process?Filter(), applyTextFilter(?, Map filter)
	
	private static Criterion processTextFilter(String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		String textValue = filterValue.get("value").toString();
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case EQUALS:
			r = Restrictions.eq(column, textValue);
			break;
		case SEARCH:
			r = Restrictions.like(column, textValue, MatchMode.ANYWHERE);
			break;
		}
		return r;
	}
	private static Disjunction processTextFilter(List<String> column, Map<String, ? extends Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		String textValue = filterValue.get("value").toString();
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
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
	
	
	private static Criterion processNumberFilter(String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case EQUALS:
			r = Restrictions.eq(column, filterValue.get("value"));
			break;
		case BETWEEN:
			r = Restrictions.between(column, filterValue.get("from"), filterValue.get("to"));
			break;
		case SELECT:
			r = Restrictions.in(column, (ArrayList)filterValue.get("value"));
			break;
		}
		return r;
	}
	private static Disjunction processNumberFilter(List<String> column, Map<String, ? extends Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case EQUALS:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.eq(col, filterValue.get("value")));
			}
			break;
		case BETWEEN:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.between(col, filterValue.get("from"), filterValue.get("to")));
			}
			break;
		case SELECT:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.in(col, (ArrayList)filterValue.get("value")));
			}
			break;
		}
		return d;
	}
	
	
	private static Criterion processDateFilter(String table, String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case YEAR:
			log.debug(String.format("processing YEAR date filter for: table:%s column:%s, sqlColumn:%s", table, column, createSqlColumn(column,table)));
			log.debug(filterValue);
			
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-M-d");
			DateTime yearDay1 = fmt.parseDateTime( String.format("%s-1-1", filterValue.get("value")) );
			DateTime yearDayLast = fmt.parseDateTime( String.format("%s-12-31", filterValue.get("value")) );
			r = Restrictions.between(column, (Calendar)yearDay1.toGregorianCalendar(), (Calendar)yearDayLast.toGregorianCalendar());
			break;
		case EQUALS:
			DateTime dt = dateFormat.parseDateTime(filterValue.get("value").toString());
			r = Restrictions.eq(column, (Calendar)dt.toGregorianCalendar());
			break;
		case BEFORE:
			DateTime dtb4 = dateFormat.parseDateTime(filterValue.get("value").toString());
			r = Restrictions.lt(column, (Calendar)dtb4.toGregorianCalendar());
			break;
		case AFTER:
			DateTime dtAfter = dateFormat.parseDateTime(filterValue.get("value").toString());
			r = Restrictions.gt(column, (Calendar)dtAfter.toGregorianCalendar());
			break;
		case BETWEEN:
			DateTime fromDate = dateFormat.parseDateTime(filterValue.get("fromDate").toString());
			DateTime toDate = dateFormat.parseDateTime(filterValue.get("toDate").toString());
			r = Restrictions.between(column, (Calendar)fromDate.toGregorianCalendar(), (Calendar)toDate.toGregorianCalendar());
			break;
		case SELECT:
			// the select widget will save the list of dates as an array of {date:<Date>, timestamp:<Long>} objects
			// so we need to convert to a list of Dates or Calendars
			ArrayList<Calendar> fvalues = new ArrayList<Calendar>();
			List<HashMap<String, ? extends Object>> datesList = (ArrayList<HashMap<String, ? extends Object>>)filterValue.get("value");
			for(ListIterator<HashMap<String, ? extends Object>> dIter = datesList.listIterator(); dIter.hasNext();) {
				HashMap<String, ? extends Object> dateHash = dIter.next();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((Long)dateHash.get("timestamp"));
				fvalues.add((Calendar)cal.clone());
			}
			
			r = Restrictions.in(column, fvalues);
			break;
		case CYCLE:
			/*
			 * This is a custom filter type.
			 * In order for this to work we need the original database column name since we are 
			 * going to use a native SQL clause.
			 * WHERE <column> BETWEEN DATE( CONCAT( YEAR(<D>),'-',MONTH(<D>),'-16' ) ) 
			 * AND DATE( CONCAT( YEAR(<D>),'-',MONTH(<D>),'-',DAY( LAST_DAY(<D>) ) ) )
			 * 1 = column, 2 = date string, 3 = date string, 4 = start day variable, 5 = date string, 6 = date string, 7 = end day variable
			 */
			Map<String, ? extends Object> cycleDateObj = (HashMap<String, ? extends Object>)filterValue.get("monthYear");
			DateTime cycleDT = new DateTime((Long)cycleDateObj.get("timestamp"));
			DateTimeFormatter sqlDateFormat = DateTimeFormat.forPattern("yyyy-M-d");
			String cycleDateStr = sqlDateFormat.print(cycleDT);
			String startDay = "16";
			String endDaySQL = String.format("DAY(LAST_DAY('%s'))", cycleDateStr);
			
			// set start/end days based on the cycle property
			if(filterValue.get("cycle").toString().equals("1")) {
				startDay = "1";
				endDaySQL = "'15'";
			}
			
			r = Restrictions.sqlRestriction(
				String.format(
					"%s BETWEEN DATE( CONCAT(YEAR('%s'),'-',MONTH('%s'),'-','%s') ) AND DATE( CONCAT(YEAR('%s'),'-',MONTH('%s'),'-',%s) )",
					createSqlColumn(column,table),
					cycleDateStr,
					cycleDateStr,
					startDay,
					cycleDateStr,
					cycleDateStr,
					endDaySQL
					)
			);
			break;
		}
		return r;
	}
	private static Disjunction processDateFilter(String table, List<String> column, Map<String, ? extends Object> filterValue) throws Throwable {
		// TODO finish processDateFilter() for multi-column
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case YEAR:
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.sqlRestriction( String.format("YEAR(%s) = %s", createSqlColumn(col,table), filterValue.get("value")) ));
			}
			break;
		case EQUALS:
			DateTime dt = dateFormat.parseDateTime(filterValue.get("value").toString());
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.eq(col, (Calendar)dt.toGregorianCalendar()));
			}
			break;
		case BEFORE:
			DateTime dtB4 = dateFormat.parseDateTime(filterValue.get("value").toString());
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.lt(col, (Calendar)dtB4.toGregorianCalendar()));
			}
			break;
		case AFTER:
			DateTime dtAfter = dateFormat.parseDateTime(filterValue.get("value").toString());
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(Restrictions.gt(col, (Calendar)dtAfter.toGregorianCalendar()));
			}
			break;
		case BETWEEN:
			DateTime fromDate = dateFormat.parseDateTime(filterValue.get("fromDate").toString());
			DateTime toDate = dateFormat.parseDateTime(filterValue.get("toDate").toString());
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add( Restrictions.between(col, (Calendar)fromDate.toGregorianCalendar(), (Calendar)toDate.toGregorianCalendar()) );
			}
			break;
		case SELECT:
			ArrayList<Calendar> fvalues = new ArrayList<Calendar>();
			List<HashMap<String, ? extends Object>> datesList = (ArrayList<HashMap<String, ? extends Object>>)filterValue.get("value");
			for(ListIterator<HashMap<String, ? extends Object>> dIter = datesList.listIterator(); dIter.hasNext();) {
				HashMap<String, ? extends Object> dateHash = dIter.next();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((Long)dateHash.get("timestamp"));
				fvalues.add((Calendar)cal.clone());
			}
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add( Restrictions.in(col, fvalues) );
			}
			break;
		case CYCLE:
			Map<String, ? extends Object> cycleDateObj = (HashMap<String, ? extends Object>)filterValue.get("monthYear");
			DateTime cycleDT = new DateTime((Long)cycleDateObj.get("timestamp"));
			DateTimeFormatter sqlDateFormat = DateTimeFormat.forPattern("yyyy-M-d");
			String cycleDateStr = sqlDateFormat.print(cycleDT);
			String startDay = "16";
			String endDaySQL = String.format("DAY(LAST_DAY('%s'))", cycleDateStr);
			String sqlColumn = String.format("%s", column);//ensures a copy
			
			if(filterValue.get("cycle").toString().equals("1")) {
				startDay = "1";
				endDaySQL = "'15'";
			}
			
			for(ListIterator<String> li = column.listIterator(); li.hasNext();) {
				String col = li.next();
				d.add(
					Restrictions.sqlRestriction(
						String.format(
							"%s BETWEEN DATE( CONCAT(YEAR('%s'),'-',MONTH('%s'),'-','%s') ) AND DATE( CONCAT(YEAR('%s'),'-',MONTH('%s'),'-',%s) )",
							createSqlColumn(col,table),
							cycleDateStr,
							cycleDateStr,
							startDay,
							cycleDateStr,
							cycleDateStr,
							endDaySQL
							)
					)
				);
			}
			break;
		}
		return d;
	}
	
	
	private static Criterion processBooleanFilter(String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
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
	private static Disjunction processBooleanFilter(List<String> column, Map<String, ? extends Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
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
	
	
	private static Criterion processEnumFilter(String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case IN:
			ArrayList inValues = new ArrayList();
			ArrayList<Map> filterInValues = (ArrayList)filterValue.get("value");
			try {
				for(Iterator inIter = filterInValues.iterator(); inIter.hasNext();) {
					HashMap<String, ? extends Object> inVal = (HashMap<String, ? extends Object>)inIter.next();
					Integer iCode = Integer.parseInt(inVal.get("code").toString());
					inValues.add(((DBDataSourceDao)DatasourceManagerUtil.byName( filterValue.get("table").toString() )).byId(iCode));
				}
			} catch(NumberFormatException nfe) {
				log.error(nfe.getMessage());
			}
			r = Restrictions.in(column, inValues);
			break;
		}
		return r;
	}
	
	
	private static Criterion processBiglistFilter(String column, Map<String, ? extends Object> filterValue) throws Throwable {
		Criterion r = null;
		String tableKey = filterValue.get("table").toString();
		String valueKey = filterValue.get("valueKey").toString();
		HashMap<String, ? extends Object> filterValueMap = (HashMap<String, ? extends Object>)filterValue.get("value");
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
		switch(type) {
		case EQUALS:
			r = Restrictions.eq(String.format("%s.%s", column, valueKey), filterValueMap.get(valueKey));
			break;
		}
		return r;
	}
	
	private static Disjunction processBiglistFilter(List<String> column, Map<String, ? extends Object> filterValue) throws Throwable {
		Disjunction d = Restrictions.disjunction();
		String tableKey = filterValue.get("table").toString();
		String valueKey = filterValue.get("valueKey").toString();
		HashMap<String, ? extends Object> filterValueMap = (HashMap<String, ? extends Object>)filterValue.get("value");
		Filter_Type type = Filter_Type.getType(filterValue.get("type").toString());
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
