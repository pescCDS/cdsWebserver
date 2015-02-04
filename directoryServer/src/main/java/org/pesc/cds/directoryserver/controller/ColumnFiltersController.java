package org.pesc.cds.directoryserver.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.pesc.cds.datatables.DataTablesRequest;
import org.pesc.cds.datatables.FilterSet;
import org.pesc.cds.datatables.FiltersToHQLUtil;
import org.pesc.cds.webservice.service.DatasourceManagerUtil;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.webservice.service.TableClassMap;
import org.pesc.edexchange.v1_0.dao.FilterSetsDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ColumnFiltersController {
	private static final Log log = LogFactory.getLog(ColumnFiltersController.class);
	
	/* Column Filters REST Interface for the DataTables data
	 * and the filter sets CRUD
	 * 
	 * create → POST     /columnfilters
	 * read   → GET      /columnfilters[/id]
	 * update → PUT      /columnfilters/id
	 * patch  → PATCH    /columnfilters/id -- TODO
	 * delete → DELETE   /columnfilters/id
	 * 
	 * fetch  → GET      /collection
	 * 
	 * read in table for create, read, fetch
	 * 
	 * The DataTables REST signature is:
	 * read   → POST     /columnfilters/data/
	 */
	
	/**
	 * This is for the "Fetch" method of Backbone.Sync.
	 * It must pass in the table property so the returned FilterSet's are 
	 * grouped by the logged-in user (using the user_id field) and the 
	 * table field.
	 * @return List<FilterSet>
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/columnfilters", method=RequestMethod.GET)
	@ResponseBody
	public List<FilterSet> bbSyncFetch(@RequestParam("table") String table) {
		log.debug(String.format("passed parameter 'table': %s", table));
		List<FilterSet> retList = new ArrayList<FilterSet>();
		if(table==null) {
			// this will still limit the results to the logged in user id
			retList = ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).all();
		} else {
			// limits results by table and logged in user id
			retList = ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).byTable(table);
		}
		
		// TODO include any public filter sets
		
		return retList;
	}
	
	/**
	 * This is for the "Create" method of Backbone.Sync.
	 * It must pass in a new FilterSet object to insert into the
	 * FilterSet persistence layer (and then into the database)
	 * The new FilterSet is transmitted as a <code>POST</code> payload and converted 
	 * in the <code>@JsonProperty</code> method parameter
	 * @param fs FilterSet
	 * @return FilterSet the saved FilterSet
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/columnfilters", method=RequestMethod.POST)
	@ResponseBody
	public FilterSet bbSyncCreate(@RequestBody FilterSet fs) {
		log.debug(fs.toString());
		return ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).save(fs);
	}
	
	/**
	 * This is for the "Read" method of Backbone.Sync.
	 * It must pass an Integer that is the identifier for the FilterSet
	 * in the persistence layer/Database. The <code>id</code> parameter is
	 * transmitted as a url path parameter in a <code>GET</code> transmission.
	 * @param id Integer
	 * @return FilterSet in the persistence layer identified by the id parameter.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping("/columnfilters/{id}")
	@ResponseBody
	public FilterSet bbSyncRead(@PathVariable("id") Integer id) {
		return (FilterSet)DatasourceManagerUtil.getFilterSets().byId(id);
	}
	
	
	/**
	 * This is the "Update" method for Backbone.Sync.
	 * The <code>id</code> parameter is transmitted as a url path parameter and 
	 * the <code>FilterSet</code> is transmitted as a <code>PUT</code> payload.
	 * @param id Integer
	 * @param fs FilterSet
	 * @return FilterSet The updated FilterSet object.
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/columnfilters/{id}", method=RequestMethod.PUT)
	@ResponseBody
	public FilterSet bbSyncUpdate(@PathVariable("id") Integer id, @RequestBody FilterSet fs) {
		return ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).save(fs);
	}
	
	
	/**
	 * This is the "Delete" method for Backbone.Sync
	 * The <code>id</code> parameter is transmitted as a url path parameter
	 * @param id Integer
	 * @return FilterSet The deleted FilterSet object
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@RequestMapping(value="/columnfilters/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public FilterSet bbSyncDelete(@PathVariable("id") Integer id) {
		FilterSet removedFilterSet = ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).byId(id);
		return ((FilterSetsDao)DatasourceManagerUtil.getFilterSets()).remove(removedFilterSet);
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RequestMapping("/columnfilters/data")
	@ResponseBody
	public Map<String, ?> getDataTableData(Model model, @RequestBody DataTablesRequest dtr) {
		
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
				 * columnfilters [n]
				 * table: the database table name
				 * column: database table column name
				 * type: data type
				 * label: desctriptive name for the column
				 * filterValue: {
				 *	type: sup-type
				 *	value - varies based on type
				 *  <other properties>
				 * }
				 * 
				 * example:
				 * columnfilters:[
				   { 
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
					FiltersToHQLUtil.addRestriction(ct, cf);
					
				} catch(Exception ex) {
					log.error(ex.getMessage());
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
			
			if(!session.getTransaction().wasCommitted()) {
				session.getTransaction().commit();
			}
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			retHash.put("error", ex.getMessage());
		}
		
		return retHash;
	}
}
