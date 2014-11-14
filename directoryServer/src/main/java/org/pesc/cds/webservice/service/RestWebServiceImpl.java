package org.pesc.cds.webservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;


import org.pesc.cds.datatables.DataTablesRequest;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.datatables.TableClassMap;
import org.pesc.cds.datatables.Column_Type;
import org.pesc.cds.datatables.Filter_Type;
import org.pesc.cds.directoryserver.view.DocumentFormatJson;
import org.pesc.cds.directoryserver.view.EntityCodeJson;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.dao.DocumentFormatsDao;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;

public class RestWebServiceImpl {
	
	private static final Log log = LogFactory.getLog(RestWebServiceImpl.class);
	
	// Document Formats
	@Path("/documentFormats/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DocumentFormat saveDocumentFormat(@JsonProperty DocumentFormat docFormat) {
		// TODO validate document format object
		log.debug(docFormat);
		
		//save document format object to persistence layer
		DocumentFormat df = DocumentFormatsDao.save(docFormat);
		log.debug(df);
		return df;
	}
	
	@Path("/documentFormats/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeDocumentFormat(@JsonProperty DocumentFormat docFormat) {
		log.debug(docFormat);
		
		//remove document format object from persistence layer
		DocumentFormatsDao.remove(docFormat);
	}
	
	
	// Entity Codes
	@Path("/entityCodes/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EntityCode saveEntityCode(@JsonProperty EntityCode entityCode) {
		// TODO validate document format object
		log.debug(entityCode);
		
		//save document format object to persistence layer
		EntityCode df = EntityCodesDao.save(entityCode);
		log.debug(df);
		return df;
	}
	
	@Path("/entityCodes/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeEntityCode(@JsonProperty EntityCode entityCode) {
		log.debug(entityCode);
		
		//remove document format object from persistence layer
		EntityCodesDao.remove(entityCode);
	}
	
	
	// Organizations
	@Path("/organizations/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Organization saveOrganization(@JsonProperty Organization org) {
		// TODO validate organization object
		log.debug(org);
		log.debug(String.format(
			"Organization {%n  directoryId:%s,%n  Id:%s,%n  Name:%s,%n  IdType:%s,%n  SubCode:%s,%n  EIN:%s,%n  EntityCode:%s,%n  SiteUrl:%s,%n  Description:%s,%n  termsOfUser:%s,%n  privacyPolicy:%s",
			org.getDirectoryId(),
			org.getOrganizationId(),
			org.getOrganizationName(),
			org.getOrganizationIdType(),
			org.getOrganizationSubcode(),
			org.getOrganizationEin(),
			org.getOrganizationEntityCode(),
			org.getOrganizationSiteUrl(),
			org.getDescription(),
			org.getTermsOfUse(),
			org.getPrivacyPolicy()
		));
		
		//save organization object to persistence layer
		Organization retOrg = OrganizationsDao.save(org);
		log.debug(retOrg);
		return retOrg;
	}
	
	@Path("/organizations/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeOrganization(@JsonProperty Organization org) {
		log.debug(org);
		
		//remove organization object from persistence layer
		OrganizationsDao.remove(org);
	}
	
	
	/**
	 * REST method for datatable queries. Handled by Apache CXF and not Spring
	 * This method conforms to the DataTables 1.10 server-side standard for requesting and returning filtered data.
	 * Except the search parameters, which conform to the columnfilters standard. 
	 * @param draw Draw counter. This is used by DataTables to ensure that the Ajax returns from server-side processing 
	 *             requests are drawn in sequence by DataTables (Ajax requests are asynchronous and thus can return out 
	 *             of sequence). This is used as part of the draw return parameter.
	 * @param start Paging first record indicator. This is the start point in the current data set (0 index based - i.e. 0 is the first record).
	 * @param length Number of records that the table can display in the current draw. It is expected that the number of 
	 *               records returned will be equal to this number, unless the server has fewer records to return. Note 
	 *               that this can be -1 to indicate that all records should be returned.
	 * @param search {<p>
	 *     value: Global search value. To be applied to all columns which have <code>searchable</code> as <code>true</code>.
	 *     regex: <code>true</code> if the global filter should be treated as a regular expression for advanced searching, 
	 *            <code>false</code> otherwise
	 * @param order an array defining how many columns are being ordered upon - i.e. if the array length is 1, then a 
	 *              single column sort is being performed, otherwise a multi-column sort is being performed
	 * @param columns an array defining all columns in the table
	 * 
	 * @param table the table name.
	 * 
	 * @param columnfilters @see columnfilters https://github.com/owenwe/columnfilters
	 * 
	 * @return Map<String, Object>{<p>
	 *     draw: The draw counter that this object is a response to - from the draw parameter sent as part of the data request. 
	 *           Note that it is strongly recommended for security reasons that you cast this parameter to an integer, rather 
	 *           than simply echoing back to the client what it sent in the draw parameter, in order to prevent Cross Site 
	 *           Scripting (XSS) attacks.<p>
	 *     recordsTotal: Total records, before filtering (i.e. the total number of records in the database)<p>
	 *     recordsFiltered: Total records, after filtering (i.e. the total number of records after filtering has been applied 
	 *                      - not just the number of records being returned for this page of data).<p>
	 *     data: The data to be displayed in the table. This is an array of data source objects, one for each row, which will be used by DataTables.<p>
	 *     error: Optional: If an error occurs during the running of the server-side processing script, you can inform the user of 
	 *                      this error by passing back the error message to be displayed using this parameter. 
	 *                      Do not include if there is no error.<p>
	 * }<p>
	 * 
	 * In addition to the above parameters which control the overall table, DataTables can use the following optional parameters on 
	 * each individual row's data source object to perform automatic actions for you:<p>
	 * 
	 * DT_RowId String Set the ID property of the tr node to this value
	 * DT_RowClass String Add this class to the tr node
	 * DT_RowData Object Add this data property to the row's tr node allowing abstract data to be added to the node, using the 
	 *                   HTML5 data-* attributes. This uses the jQuery data() method to set the data, which can also then be 
	 *                   used for later retrieval (for example on a click event).
	 * 
	 */
	@Path("/data/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, ?> getDataTableData(@JsonProperty DataTablesRequest dtr) {
		
		Integer draw = dtr.getDraw();
		int start = dtr.getStart();
		int length = dtr.getLength();
		
		
		Map retHash = new HashMap();
		retHash.put("draw", draw);
		
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			session.beginTransaction();
			// include table property in AJAX call and use it to get the class 
			Class mappedTableClass = TableClassMap.getClass(dtr.getTable());
			Criteria ct = session.createCriteria(mappedTableClass);
			
			
			for(Iterator<HashMap<String, ? extends Object>> iter = dtr.getColumnfilters().iterator(); iter.hasNext();) {
				/* 
				 * columnfilters []
				 * table: the database table name
				 * label: desctriptive name for the column
				 * type: data type
				 * column: database table column name
				 * category: (not used for server-side processing)
				 * filterValue: {
				 *	type: sup-type
				 *	value - varies based on type
				 * }
				 * columnfilters:[
				   {
					     category:null, 
					     filterValue:{
					       type:'equals',
					       value:1,
						   description:'is equal to 1'
					     },
					     column:['status', 'programId'], 
					     label:'Status,Program',
					     table:'employees', 
					     type:'number'
					   },
					   {
					     category:null, 
					     filterValue:{
					         type:'equals', 
					         value:'2014-09-30T07:00:00.000Z', 
					         description:'is equal to 9/30/2014'
					     }, 
					     column:'hired', 
					     label:'hired', 
					     table:'employees', 
					     type:'date'
					   }
					 }
				 */
				HashMap<String, ? extends Object> cf = (HashMap<String, ? extends Object>)iter.next();
				try {
					Map<String, ? extends Object> filterValue = (Map<String, ? extends Object>)cf.get("filterValue");
					//log.debug(String.format("filterValue.type: %s", filterValue.get("type")));
					//log.debug(String.format("filterValue.value: %s", filterValue.get("value")));
					//log.debug(String.format("filterValue.description: %s", filterValue.get("description")));
					
					Boolean isMultiColumn = false;
					if(cf.get("column") instanceof ArrayList) {
						isMultiColumn = true;
					}
					//log.debug(String.format("label:%s", cf.get("label")));
					//log.debug(String.format("table:%s", cf.get("table")));
					//log.debug(String.format("type:%s", cf.get("type")));
					
					Column_Type ctype = Column_Type.getType(cf.get("type").toString());
					Filter_Type ftype = Filter_Type.getType(filterValue.get("type").toString());
					
					
					switch(ctype){
					case TEXT:
						String textValue = filterValue.get("value").toString();
						switch(ftype){
						case EQUALS:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.eq(col, filterValue.get("value")));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.eq(cf.get("column").toString(), textValue));
							}
							break;
						case SEARCH:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.ilike(col, textValue));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.like(cf.get("column").toString(), textValue, MatchMode.ANYWHERE) );
							}
							break;
						}
						break;
					case NUMBER:
						switch(ftype){
						case EQUALS:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.eq(col, filterValue.get("value")));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.eq(cf.get("column").toString(), filterValue.get("value")));
							}
							break;
						case BETWEEN:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.between(col, filterValue.get("from"), filterValue.get("to")));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.between(cf.get("column").toString(), filterValue.get("from"), filterValue.get("to")));
							}
							break;
						case SELECT:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.in(col, (ArrayList)filterValue.get("value")));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.in(cf.get("column").toString(), (ArrayList)filterValue.get("value")));
							}
							break;
						}
						break;
					case DATE:
						DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
						switch(ftype){
						case EQUALS:
							DateTime dt = fmt.parseDateTime(filterValue.get("value").toString());
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add(Restrictions.eq(col, dt.toDate()));
								}
								ct.add(d);
							} else {
								ct.add(Restrictions.eq(cf.get("column").toString(), dt.toDate()));
							}
							break;
						case BETWEEN:
							DateTime fromDate = fmt.parseDateTime(filterValue.get("fromDate").toString());
							DateTime toDate = fmt.parseDateTime(filterValue.get("toDate").toString());
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add( Restrictions.between(col, fromDate, toDate) );
								}
								ct.add(d);
							} else {
								ct.add( Restrictions.between(cf.get("column").toString(), fromDate, toDate) );
							}
							break;
						case SELECT:
							ArrayList<Date> dateSelList = new ArrayList<Date>();
							ArrayList<String> cf_fv = (ArrayList)filterValue.get("value");
							for(ListIterator<String> fvIter = cf_fv.listIterator(); fvIter.hasNext();) {
								dateSelList.add( fmt.parseDateTime(fvIter.next()).toDate() );
							}
							
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									d.add( Restrictions.in(col, dateSelList) );
								}
								ct.add(d);
							} else {
								ct.add( Restrictions.in(cf.get("column").toString(), dateSelList) );
							}
							break;
						case CYCLE:
							if(isMultiColumn) {
								Disjunction d = Restrictions.disjunction();
								for(ListIterator<String> li = ((ArrayList<String>)cf.get("column")).listIterator(); li.hasNext();) {
									String col = li.next();
									
								}
								ct.add(d);
							} else {
								
							}
							break;
						}
						break;
					case BOOLEAN:
						switch(ftype){
							case EQUALS:
								// TODO
							break;
						}
						break;
					case ENUM:
						switch(ftype) {
							case IN:
								ArrayList<Integer> inValues = new ArrayList<Integer>();
								ArrayList<Map> filterInValues = (ArrayList)filterValue.get("value");
								for(Iterator inIter = filterInValues.iterator(); inIter.hasNext();) {
									LinkedHashMap<String, ? extends Object> inVal = (LinkedHashMap<String, ? extends Object>)inIter.next();
									inValues.add(Integer.parseInt(inVal.get("code").toString()));
								}
								ct.add(Restrictions.in(cf.get("column").toString(), inValues) );
							break;
						}
						break;
					}
					
				} catch(Exception ex) {
					log.debug(ex.getMessage());
				}
			}
			
			
			
			//orders (orders[i].column == zero-based index of the columns array
			if(dtr.getOrder().size()>0) {
				for(Iterator<? extends Object> iter = dtr.getOrder().iterator(); iter.hasNext();) {
					Map<String,Object> o = (Map<String,Object>)iter.next();
					Integer colIndx = Integer.parseInt(o.get("column").toString());
					Map<String, Object> col = ((Map<String, Object>) dtr.getColumns().get(colIndx));
					String orderDir = (String)o.get("dir");
					String columnName = (String)col.get("name");
					if((Boolean)col.get("orderable")) {
						ct.addOrder(orderDir.toLowerCase().equals("asc") ? Order.asc(columnName) : Order.desc(columnName));
					}
				}
			}
			
			
			
			// recordsFiltered is the number of rows after any filtering has been applied
			// but not including paging limits
			// if no filtering (search=!null) then this should be the same as recordsTotal
			retHash.put("recordsFiltered", ct.list().size());
			
			//limits
			ct.setFirstResult(start);
			ct.setMaxResults(length);
			
			List filteredList = ct.list();
			retHash.put("data", filteredList.toArray());
			
			Number totalRows = (Number)session.createCriteria(mappedTableClass).setProjection(Projections.rowCount()).uniqueResult();
			retHash.put("recordsTotal", totalRows);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			retHash.put("error", ex.getMessage());
		}
		
		return retHash;
	}
	
	
	/***********************************************************************************
	 * These are for AJAX web services
	 * The only data served out should be auxilary tables where the total row count
	 * is under 300 or so. Bigger tables will need to go through the SOAP or REST
	 * web services agreed upon by the PESC EdExchange group
	 ***********************************************************************************/
	// TODO EntityCode
	
	/**
	 * 
	 * @return List of DocumentFormat
	 */
	@Path("/documentFormats/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List getDocumentFormatsRest() {
		
		List retList = new ArrayList();
		
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			log.debug("grabbing document formats from session");
			List dfArrayList = session.createQuery("FROM DocumentFormat").list();
			if(dfArrayList.size()>0) {
				retList = dfArrayList;
			}
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			//for now swallow exception
			log.debug(ex.getMessage());
		}
		
		log.debug("returning list of document formats, size:"+retList.size());
		return retList;
	}
}
