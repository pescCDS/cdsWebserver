// Dev-Only: templates and template global variable will be included in one file
var CFTEMPLATES = {
	'DATEPICKER_DATE_FORMATS':{
		'en_us':'m/d/yyyy',
		'en_gb':'dd-mm-yyyy',
		'zh_cn':'yyyy.mm.dd',
		'month_year':'MM, yyyy',
		'year':'yyyy'
	},
	'DATEPICKER_VIEW_MODES':{
		'DAYS':0,
		'MONTHS':1,
		'YEARS':2
	},
	'DATEPICKER_WEEK_START_DAYS':{
		'SUNDAY':0,
		'MONDAY':1,
		'TUESDAY':2,
		'WEDNESDAY':3,
		'THURSDAY':4,
		'FRIDAY':5,
		'SATURDAY':6
	},
	
	'datepicker':[
		'<div class="input-group date<% _.isString(datepicker.name)?print(" "+datepicker.name):"" %>">',
			'<input type="text" class="form-control date" value="" />',
			'<span class="input-group-addon">',
				'<span class="glyphicon glyphicon-calendar"></span>',
			'</span>',
		'</div>'
	].join(''),
	
	'datepickerBetween':[
		'<div class="input-daterange input-group date<% _.isString(datepicker.name)?print(" "+datepicker.name):"" %>">',
			'<input type="text" class="form-control" name="start" />',
			'<span class="input-group-addon">to</span>',
			'<input type="text" class="form-control" name="end" />',
		'</div>'
	].join('')
	
	
	//[[SCRIPT_INSERT]]//
};

CFTEMPLATES.commonValueController = [
	'<button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown">',
		'Select Columns <span class="caret"></span>',
	'</button>',
	'<ul class="dropdown-menu" role="menu">',//dropdown-menu-sm
		'<% for(var i in data.columns) { %>',
			'<li class="cf-cvdd-active">',
				'<button type="button" class="btn btn-block text-capitalize " data-name="<%= data.columns[i].name %>" data-type="<%= data.columns[i].type %>">',
					'<span class="glyphicon glyphicon-ok pull-left hidden">',
					'</span> <%= data.columns[i].label %>',
				'</button>',
			'</li>',
		'<% } %>',
	'</ul>'
].join('');

// variable { panelheading.filterFactory (View.el), panelheading.filterColumns (Array) }
CFTEMPLATES.dataFiltersPanelContent = ['<div class="panel-heading well-sm">',
	'<div class="row">',
		'<div class="col-lg-5 col-md-5 col-sm-7 col-xs-8 text-nowrap">',
			
			// FILTER SELECTION TYPE
			'<div class="btn-group cf-data-filter-type-selection pull-left" data-toggle="buttons">',
				'<label class="btn btn-info active"><input type="radio" name="options" id="cf-data-type-option-default" value="0" checked="checked" /> Data Filters</label>',
				'<label class="btn btn-info"><input type="radio" name="options" id="cf-data-type-option-common-value" value="1" /> Common Value</label>',
			'</div>',
			
			// COMMON VALUE FILTER SELECTION TYPE
			'<div class="cf-common-value-controller-replace pull-left"></div>',
			
			// ADD FILTER/COLUMN SELECT DROP DOWN
			'<div class="btn-group pull-left">',
				'<button type="button" class="btn btn-success btn-sm cf-edit-filter-button">Save</button>',
				'<button type="button" class="btn btn-default btn-sm cf-cancel-edit-filter-button">Cancel</button>',
			'</div>',
			
			// CUSTOM UI EXTENSION CONTAINER
			'<div class="btn-group cf-custom-ui-container pull-left"></div>',
			
			// DEFAULT FILTER SELECTION TYPE
			'<div class="btn-group cf-add-change-filter-group-button cf-dropdown-menu-scroll-medium pull-left">',
				'<button type="button" class="btn btn-default btn-xs cf-add-filter-button">Add Filter</button>',
				'<button type="button" data-toggle="dropdown" class="btn btn-default btn-xs dropdown-toggle">',
					'<span class="caret"></span>',
					'<span class="sr-only">Toggle Dropdown</span>',
				'</button>',
				'<ul role="menu" class="dropdown-menu cf-columns-select-dd">',//dropdown-menu-sm
				'<% for(var i in panelheading.filterColumns) { %>',
					'<% if(!panelheading.filterColumns[i].cfexclude) { %>',
						'<%= _.template(CFTEMPLATES.filterOptionListItem,{variable:\'columnData\'})(panelheading.filterColumns[i]) %>',
					'<% } %>',
				'<% } %>',
				'</ul>',
			'</div>',
		'</div>',
		'<div class="col-lg-7 col-md-7 col-sm-5 col-xs-12 cf-filter-factory-container-row"></div>',
	'</div>',
'</div>'].join('');

// 
CFTEMPLATES.filterOptionListItem = [
	'<li>',
		'<a href="#" data-type="<%= columnData.type %>" data-name="<%= _.has(columnData,"dataColumn")?columnData.dataColumn:_.has(columnData,"data")?columnData.data:columnData.name %>"><%= columnData.label %></a>',
	'</li>'
].join('');

// controller.filterCategories
CFTEMPLATES.dataFiltersControlFooter = [
	'<div class="container-fluid">',
		'<div class="collapse navbar-collapse">',
			
			'<button type="button" class="navbar-btn btn btn-default btn-xs navbar-left cf-cancel-filter-set-changes-button">Cancel</button>',
			'<button type="button" class="navbar-btn btn btn-success btn-xs navbar-left cf-save-filter-set-changes-button">Done</button>',
			
			'<ul class="nav navbar-nav navbar-right">',
				'<li class="dropup btn btn-xs cf-save-filter-list" title="save">',
					'<a href="#" class="dropdown-toggle btn btn-xs" data-toggle="dropdown">',
						'<span class="glyphicon glyphicon-floppy-disk"></span>',
						'<span class="caret"></span>',
					'</a>',
					'<ul class="dropdown-menu" role="menu">',
						'<li data-save-type="__new_category__">',
							'<a href="#">',
								'<span class="badge pull-right">',
									'<span class="glyphicon glyphicon-plus"></span>',
								'</span> new category',
							'</a>',
						'</li>',
					'</ul>',
				'</li>',
			'</ul>',
			
			'<button type="button" class="close navbar-btn navbar-right cf-clear-all-filters-button disabled" disabled="disabled" title="clear all working filters">',
				'<span aria-hidden="true">&times;</span><span class="sr-only">Clear Filters</span>',
			'</button>',
			
		'</div>',
	'</div>',
	'<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="save category or filter set" aria-hidden="true">',
		'<div class="modal-dialog modal-lg">',
			'<div class="modal-content">',
				'<div class="modal-header">',
					'<button type="button" class="close" data-dismiss="modal">',
						'<span aria-hidden="true">&times;</span>',
						'<span class="sr-only">Cancel</span>',
					'</button>',
					'<h4 class="modal-title">Modal title</h4>',
				'</div>',
				'<div class="modal-body"></div>',
				'<div class="modal-footer">',
					'<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>',
					'<button type="button" class="btn btn-primary" data-save-type="category">Save</button>',
				'</div>',
			'</div>',
		'</div>',
	'</div>'
].join('');

CFTEMPLATES.numberSpinner1 = '<div class="spinbox digits-5<% print(_.has(spinbox,"name")?(" "+spinbox.name):"") %>">'+
'  <input type="text" class="form-control input-mini spinbox-input" />'+
'  <div class="spinbox-buttons btn-group btn-group-vertical">'+
'    <button class="btn btn-default spinbox-up btn-xs">'+
'      <span class="glyphicon glyphicon-chevron-up"></span><span class="sr-only">Increase</span>'+
'    </button>'+
'    <button class="btn btn-default spinbox-down btn-xs">'+
'      <span class="glyphicon glyphicon-chevron-down"></span><span class="sr-only">Decrease</span>'+
'    </button>'+
'  </div>'+
'</div>';

/* Data Column Filters Container Model
 * this model can be thought of as a named group of all the filters the user wants saved 
 * an instance of this model could contain filters that limit the results
 * of a automobile database table to only electric cars made in California 
*/
var MDataFilter = Backbone.Model.extend({});


// Collection for the DataFiltersContainer class
var CDataFilters = Backbone.Collection.extend({
	'model':MDataFilter
});

// JavaScript Document
var MFilterSet = Backbone.Model.extend({
	'defaults':{
		//'id':null,
		'category':null,
		'table':null,
		'name':null,
		'description':null,
		'filters':null
	}
});

// Collection for the DataFiltersControlBar class
var CDataFilterSets = Backbone.Collection.extend({
	'model':MFilterSet
});

/**
 * An abstract view to be extended by a filter widget
 */
var VFilterWidgetType = Backbone.View.extend({
	'type':'equals',//abstract
	'visible':false,
	'active':false,
	
	// abstract functions (must override)
	
	// if you just want to know if the widget inputs are valid for returning value(s)
	'isValid':function() {},
	
	// calling this function will cause the widget to check that it can return values from its inputs
	'validate':function() {},
	
	// returns a human-readable description of the filter input values
	'getValueDescription':function() {},
	
	// returns an object representing the filter values and properties if valid, otherwise false
	'getValue':function() {},
	
	// will set the inputs to the values given
	'setValue':function(filterValue) {},
	
	//
	//load:function(data) {},
	
	// restores the filter widget back to its initial state
	'reset':function() {},
	
	// default class functions, can override, but it's not neccessary to do so
	'show':function() {
		this.visible = true;
		this.active = true;
		this.$el.show();
	},
	'hide':function() {
		this.visible = false;
		this.active = false;
		this.$el.hide();
	},
	'enable':function() {
		this.$el[0].disabled = false;
	},
	'disable':function() {
		this.$el[0].disabled = true;
	},
	
	// default view properties/functions
	'tagName':'fieldset',
	'className':'cf-widget-type',
	'render':function() { return this; }
});


// DataColumnFilterWidget Class
// collection: a collection of VFilterWidgetType (extended to an instance)
var VDataColumnFilterWidget = Backbone.View.extend({
	'type':'text',
	'visible':false,
	'active':false,
	
	'notify':function(level, title, message) {
		//console.log('VDataColumnFilterWidget notification event ('+level+', '+title+', '+message+')');
		this.getFactory().notify(level,title,message);
		this.trigger('notify', level, title, message);
	},
	'factory':[null],//hack to get a Backbone object to update a property
	'getFactory':function() {
		return this.factory[0];
	},
	'setFactory':function(f) {
		this.factory[0] = f;
	},
	
	'activeType':function() {
		return this.collection.findWhere({active:true});
	},
	'getSubType':function(subType) {
		return this.collection.findWhere({'type':subType});
	},
	
	'getFilterValue':function() {
		var at = this.activeType();
		if(at) {
			return at.getValue();
		} else {
			return false;
		}
	},
	
	'setFilterValue':function(filterValue) {
		var fwt = this.collection.findWhere({'type':filterValue.type});
		if(fwt) {
			// fwt is the sub filter widget
			fwt.attributes.setValue(filterValue);
		}
	},
	
	'getLabel':function() {
		return $('div.cf-widget-type-label',this.$el).html();
	},
	'setLabel':function(label) {
		$('div.cf-widget-type-label',this.$el).html(label);
	},
	
	'changeSubType':function(subType) {
		var at = this.activeType(),
			selAt = this.collection.findWhere({'type':subType});
		if(at && (subType!=at.attributes.type)){
			//change filter widget type selector label
			$('span.cf-widget-type-selector-btn-title', this.$el).html(subType);
			//hide current widget type
			at.attributes.hide();
			//show selected widget type
			selAt.attributes.show();
		}
	},
	
	'show':function() {
		this.visible = true;
		this.active = true;
		this.$el.show();
		//render the active type
		var at = this.activeType();
		if(at) {
			//console.log(this.type+':'+at.attributes.type);
			at.attributes.show();
		}
	},
	'hide':function() {
		this.visible = false;
		this.active = false;
		this.$el.hide();
	},
	
	'enable':function() {
		var ddbtn = $('button.dropdown-toggle',this.$el);
		if(ddbtn) {
			ddbtn[0].disabled = false;
		}
		var at = this.activeType();
		if(at) {
			at.attributes.enable();
		}
	},
	'disable':function() {
		//disable the drop down
		var ddbtn = $('button.dropdown-toggle',this.$el);
		if(ddbtn) {
			ddbtn[0].disabled = true;
		}
		
		//need to get active widget and call disable on it
		var at = this.activeType();
		if(at) {
			at.attributes.disable();
		}
	},
	
	'reset':function() {
		this.collection.each(function(filterWidget) {
			filterWidget.attributes.reset();
		});
	},
	
	'tagName':'div',
	'className':'cf-filter-widget row',
	
	'events':{
		// triggered when the type dropdown item is clicked
		'click ul.dropdown-menu li a':function(e) {
			this.changeSubType($(e.currentTarget).html());
		}
	},
	
	'initialize':function(options) {
		// ASSERTION: options will always have type and collection passed
		if(options.hasOwnProperty('type')) {
			this.type = options.type;
		} else {
			console.error('"type" must be passed with VDataColumnFilterWidget constructor');
		}
		
		this.$el.addClass('cf-filter-widget-'+this.type);
		
		//build selector drop down
		var typeSelectorDropdown = $(document.createElement('ul')).attr({'role':'menu'}).addClass('dropdown-menu pull-right'),
			typeSelector = $(document.createElement('div')).addClass('cf-widget-type-selector col-lg-4 col-md-4 col-sm-6 col-xs-4 row btn-group').append(
				$(document.createElement('div')).addClass('cf-widget-type-label text-right text-nowrap col-lg-8 col-md-7 col-sm-7 col-xs-12'),
				$(document.createElement('button')).attr({'type':'button','data-toggle':'dropdown'})
												   .addClass('btn btn-default btn-xs dropdown-toggle col-lg-3 col-md-4 col-sm-4 col-xs-12')
												   .append('<span class="cf-widget-type-selector-btn-title"></span> <span class="caret"></span>'),
				typeSelectorDropdown
		),
			typesContainer = $(document.createElement('div')).addClass('cf-widget-types-container col-lg-8 col-md-8 col-sm-6 col-xs-8');
		
		if(options.hasOwnProperty('collection')) {
			var that = this;
			typesContainer.append($.map(options.collection.models, function(fwm) {
				typeSelectorDropdown.append(
					$(document.createElement('li')).append($(document.createElement('a')).attr({'href':'#'}).html(fwm.attributes.type))
				);
				that.listenTo(fwm.attributes, 'notify', that.notify);// these are the VFilterWidgetType implementations
				return fwm.attributes.$el.hide();
			}));
			
			var firstWidget = options.collection.at(0).attributes;
			// show the first widget type and set the type selector drop down button title to its type
			$('span.cf-widget-type-selector-btn-title',typeSelector).html(firstWidget.type);
			firstWidget.active = true;
			firstWidget.show();
		} else {
			console.error('a collection must be passed with VDataColumnFilterWidget constructor');
		}
		
		// add the type selector and types container to the DOM element
		this.$el.append([typeSelector,typesContainer]);
	},
	'render':function() { return this; }
});


// DataFilterFactory Class
// collection: a collection of VDataColumnFilterWidget objects
var VDataFilterFactory = Backbone.View.extend({
	'types':[],
	'activeColumn':null,
	
	'notify':function(level, title, message) {
		this.trigger('notify', level, title, message);
	},
	
	'savedState':null,
	'saveState':function() {
		var af = this.activeFilter();
		if(af) {
			var fw = af.activeType();
			this.savedState = {
				'type':af.type,
				'label':af.getLabel(),
				'subtype':fw.attributes.type
			};
			if(af.type==='enum' || af.type==='biglist') {
				_.extend(this.savedState, {'dataCol':fw.attributes.model.get('currentColumn')});
			}
		} else {
			this.savedState = null;
		}
	},
	'restoreState':function() {
		if(this.savedState) {
			this.load(this.savedState.dataCol, this.savedState.type, this.savedState.label, this.savedState.subtype);
		} else {
			//check if there is an active filter; hide it if so
			var af = this.activeFilter();
			if(af) {
				af.hide();
			}
		}
	},
	
	'activeFilter':function(){
		//return any active && visible filter widgets (should only be 1)
		var af = this.collection.findWhere({'active':true,'visible':true});
		return af?af.attributes:false;
	},
	
	'getFilterValue':function() {
		return this.activeFilter().activeType().attributes.getValue();
	},
	
	'setFilterValue':function(filter) {
		//first we have to find the current filter widget
		var fw = this.collection.findWhere({'type':filter.type});
		if(fw) {
			// fw is a DataColumnFilterWidget (container of widgets of a specific type)
			fw.attributes.setFilterValue(filter.filterValue);
		}
		return this;
	},
	
	'updateFilterLabel':function(newLabel) {
		if(_.isString(newLabel)) {
			var af = this.activeFilter();
			if(af) {
				af.setLabel(newLabel);
			}
		}
	},
	
	'updateMultiColumnFilter':function(columns) {
		switch(this.activeFilter().type) {
			case 'biglist':
				this.activeFilter().activeType().attributes.updateMultiColumns(columns);
				break;
		}
	},
	
	'show':function() {
		var af = this.activeFilter();
		if(af){
			af.show();
		}
		return this;
	},
	'hide':function() {
		var af = this.activeFilter();
		if(af){
			af.hide();
		}
		return this;
	},
	
	'enable':function() {
		//enable the active filter
		var af = this.activeFilter();
		if(af){
			af.enable();
		}
		return this;
	},
	'disable':function() {
		//disable the active filter
		var af = this.activeFilter();
		if(af) {
			af.disable();
		}
		return this;
	},
	
	'reset':function(resetAll) {
		if(resetAll) {
			
		} else {
			var af = this.activeFilter();
			if(af) {
				af.reset();
				af.setLabel('');
			}
		}
		return this;
	},
	
	// displays the requested filter widget type
	'load':function(dataCol, dataType, dataLabel, subType) {
		//console.log(['dataCol: ',dataCol, ', dataType: ',dataType, ', dataLabel: ', dataLabel, ', subType: ', subType].join(''));
		//find it in the collection
		var reqfw = this.collection.findWhere({'type':dataType}),
			curfw = this.activeFilter();
		if(reqfw) {
			//if not asking for the currently visible filter widget, and there is one visible, hide it
			if(curfw && (curfw.cid!=reqfw.cid)) {
				curfw.hide();
			}
			
			//set the data label for the widget
			reqfw.attributes.setLabel(dataLabel);
			
			//perform any extra tasks before showing filter widget
			//for enum types
			if(reqfw.attributes.type==='enum') {
				//tell the widget to set up for dataCol
				reqfw.attributes.getSubType('in').attributes.config(dataCol);
			} else if(reqfw.attributes.type==='biglist') {
				// change out biglist filter widget data
				reqfw.attributes.getSubType('equals').attributes.config(dataCol);
			}
			
			//show the requested filter widget
			reqfw.attributes.show();
			
			if(_.isString(subType)) {
				reqfw.attributes.changeSubType(subType);
			}
		}
		return this;
	},
	
	// 
	'postConfig':function() {
		this.collection.each(function(filterWidget) {
			filterWidget.attributes.setFactory(this);
		}, this);
	},
	
	
	'tagName':'div',
	'className':'cf-filter-factory',
	'initialize':function(options) {
		if(options.hasOwnProperty('collection')) {
			// collection of VDataColumnFilterWidget (where models[n].attributes == VDataColumnFilterWidget)
			this.types = options.collection.pluck('type');
			
			// append the filter widget DOM element to the filter factory element
			var that = this;
			this.$el.append($.map(options.collection.models, function(fwm) {
				return fwm.attributes.$el.hide();//this works
			}));
			
			if(options.hasOwnProperty('showOnInit') && options.showOnInit) {
				options.collection.at(0).attributes.show();
			}
		} else {
			console.error('a collection must be passed with the VDataFilterFactory constructor.');
		}
	},
	'render':function() { return this; }
});


/**
 * Data Filters Container Controller
 * This view holds the list of filters applied to each data column.
 * There are 2 lists, the main list is a tab navigation and each tab
 * represents the column that one or more filters have been added to.
 * The second list is the tab nav panel and it contains a list of 
 * filters for the column.
 */
var VDataFiltersContainer = Backbone.View.extend({
	'version':'1.0.1',
	'preDisableTabStates':[],
	
	// this is the main view template
	'dataFiltersControlBody':_.template([
		'<div class="row" role="tabpanel">',
			'<div class="col-xs-4">',
				'<ul class="nav nav-pills nav-stacked" role="tablist"></ul>',
			'</div>',
			'<div class="col-xs-8">',
				'<div class="tab-content"></div>',
			'</div>',
		'</div>'
	].join(''), {'variable':'container'}),
	
	// this is the tab, it represents filters for a particular column (identified by )
	'filterColumnTemplate':_.template(
		['<li role="presentation">',
			'<a href="#<%= columnData.columnId %>" role="pill" data-toggle="pill" class="list-group-item">',
				'<%= _.isArray(columnData.column) ? columnData.label : (columnData.label[0].toUpperCase()+columnData.label.substring(1)) %> <span class="badge pull-right">1</span>',
			'</a>',
		'</li>'].join(''), {'variable':'columnData'}),
	
	// this is the content for the tab
	'filterColumnTabTemplate':_.template(
		['<div class="tab-pane" role="tabpanel" id="<%= columnData.column %>">',
			'<div class="list-group"></div>',
		'</div>'].join(''), {'variable':'columnData'}),
	
	// this is an item in the tab content list
	'filterListItemTemplate':_.template(
		[
			'<a href="#" class="list-group-item" data-filter-cid="<%= filterData.cid %>">',
				'<h4 class="list-group-item-heading"><strong><%= filterData.filterValue.type %></strong>',
					'<button class="close"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>',
					'<% if(!_.isArray(filterData.column)) { %>'+
					'<span class="btn pull-right cf-filter-edit-button"><span class="glyphicon glyphicon-cog"></span></span>',
					'<% } %>'+
				'</h4>',
				'<p class="list-group-item-text">',
					'<span><%= filterData.table %><%= _.isArray(filterData.column)?(" ("+filterData.column.join(",")+")"):("."+filterData.column) %> <%= filterData.filterValue.description %></span>',
				'</p>',
			'</a>'
		].join(''), {'variable':'filterData'}),
	
	/**
	 * This is only the view for the current filter group, it should NOT control
	 * the interaction of filter groups, only add/edit/remove/interaction of the view elements
	 */
	'filterItemMouseover':function(e){
		$('button.close',$(e.currentTarget)).show();
		$('span.cf-filter-edit-button',$(e.currentTarget)).show();
	},
	
	'filterItemMouseleave':function(e){
		$('button.close',$(e.currentTarget)).hide();
		$('span.cf-filter-edit-button',$(e.currentTarget)).hide();
	},
	
	'enable':function() {
		$('ul.nav li',this.$el).removeClass('disabled');
		for(var i in this.preDisableTabStates) {
			var pdts = this.preDisableTabStates[i];
			pdts.tabLink.attr({'href':pdts.href,'data-toggle':pdts.dataToggle});
		}
		var dfc = this;
		$('div.tab-content div.tab-pane.active a.list-group-item',this.$el).each(function(i,e) {
			$(e).attr('href','#').removeClass('disabled');
			$(e).on({'mouseover':dfc.filterItemMouseover, 'mouseleave':dfc.filterItemMouseleave});
		});
	},
	
	'disable':function() {
		this.preDisableTabStates = [];
		var pdts = this.preDisableTabStates;
		$('ul.nav li',this.$el).addClass('disabled');
		$('ul.nav li a.list-group-item',this.$el).each(function(i,e) {
			var a = $(e);
			pdts.push({'tabLink':a, 'href':a.attr('href'), 'dataToggle':a.attr('data-toggle')});
			a.removeAttr('href');
			a.removeAttr('data-toggle');
		});
		
		$('div.tab-content div.tab-pane.active a.list-group-item',this.$el).each(function(i,e) {
			$(e).removeAttr('href').off('mouseover mouseleave').addClass('disabled');
			$('button.close, span.cf-filter-edit-button',$(e)).hide();
		});
	},
	
	// add filter to current filter group
	'add':function(filter) {
		// ASSERTION: filter will be a valid filter model
		// filter = filter.attributes: {table, category, column, type, label, filterValue:{type, ...}}
		// TODO move element events into the 'events' object
		//console.log(filter);
		var mAtt = _.clone(filter.attributes);
		mAtt.cid = filter.cid;
		mAtt.columnId = _.isArray(mAtt.column) ? mAtt.column.join('') : mAtt.column.replace(".","_");
		
		// the filter list item
		var flit = $(this.filterListItemTemplate(mAtt));
		
		//show/hide action button functionality
		flit.on({'mouseover':this.filterItemMouseover, 'mouseleave':this.filterItemMouseleave});
		$('h4.list-group-item-heading button.close',flit).hide();
		$('h4.list-group-item-heading span.cf-filter-edit-button',flit).hide();
		
		//click event handler for the remove filter icon button
		$('h4.list-group-item-heading button.close', flit).click({'dfc':this, 'filter':mAtt},function(e) {
			// do what we need to do in this view then trigger removeClick so the DataFilters
			// View can handle what it needs to do
			var dfc = e.data.dfc,
				fData = e.data.filter;
			
			// delete this tab content list item
			$(e.currentTarget).parent().parent().remove();
			
			// if there are no more list items in the tab content, delete the tab content and the tab
			var remainingFiltersCount = $('div.tab-pane#'+fData.columnId+' div.list-group a.list-group-item', dfc.$el).length;
			if(remainingFiltersCount) {
				//filters remain, just update the filter count for this column set
				$('a.list-group-item[href="#'+fData.columnId+'"] span.badge', dfc.$el).html(remainingFiltersCount);
			} else {
				//no more filters remain for this column set, remove tab panel and tab
				$('div.tab-pane#'+fData.columnId, dfc.$el).remove();
				$('a.list-group-item[href="#'+fData.columnId+'"]', dfc.$el).parent().remove();
			}
			
			//dispatch event up the chain, pass cid so the model can be removed from the collection
			dfc.trigger('removeClick',fData.cid);
		});
		
		//click event for the edit filter icon button
		$('h4.list-group-item-heading span.cf-filter-edit-button', flit).click({'dfc':this, 'cid':mAtt.cid},function(e) {
			//just send the filter cid up the chain (dfc = (d)ata (f)ilter (c)ontroller == DataFilters
			e.data.dfc.trigger('changeClick',e.data.cid);
		});
		
		
		//first look for an existing li (tab) in <ul class="dropdown-menu" role="menu">
		var existingPill = $(['ul.nav-pills li a[href="#', mAtt.columnId ,'"]'].join(''), this.$el);
		if(existingPill.length) {
			var columnTabContent = $(['div#', mAtt.columnId , ' div.list-group'].join(''), this.$el),
				columnFilterCount = $('span.badge', existingPill).html()*1;
			$('span.badge', existingPill).html(++columnFilterCount);
			
			columnTabContent.append(flit);
			
		} else {//tab doesn't exist for this type, create new one
			var currentTabsCount = $('ul.nav-pills li a',this.$el).length;
			//add column pill to tab set
			var newTabLi = $(this.filterColumnTemplate(mAtt));
			$('a', newTabLi).click(function(e) {
				e.preventDefault();
				$(this).tab('show');
			});
			$('ul.nav',this.$el).append(newTabLi);
			
			//add tab content if needed, or create one
			var columnTabContent = $(['div#',mAtt.columnId].join(''),this.$el);
			if(columnTabContent.length<1) {
				// need to create a new tab content container
				$('div.tab-content',this.$el).append(this.filterColumnTabTemplate({'column':mAtt.columnId,'cid':mAtt.cid}));
				columnTabContent = $(['div#',mAtt.columnId,' div.list-group'].join(''),this.$el);
			}
			
			//add it to the current tab content and update counts
			// label, type, table, category, column, filterValue:{type, }
			columnTabContent.append(flit);
			//set this tab to active if it's the only one
			if(currentTabsCount<1) {
				//console.log($('ul.nav-pills li a', this.$el).first());
				$('ul.nav-pills li a', this.$el).first().tab('show');
			}
		}
	},
	
	'showTabContent':function() {
		var activeTabA = $('ul.nav li.active a', this.$el);
		if(activeTabA.length) {
			$(['div.tab-content div',activeTabA.attr('href')].join(''), this.$el).addClass('active');
		}
		
	},
	
	/** 
	 * Updates the label text for the tab and the filter list item link 
	 * within the tab content
	 * The filter argument should be a Filter Model
	 */
	'updateFilter':function(filter) {
		//console.log('updating filter ui view');
		//console.log(filter);
		var fALink = $('div.tab-content div.list-group a.list-group-item[data-filter-cid="'+filter.cid+'"]', this.$el),
			fa = filter.attributes,
			fv = filter.attributes.filterValue;
		if(fALink.length) {
			$('h4.list-group-item-heading strong',fALink).html(fv.type);
			$('p.list-group-item-text span',fALink).html([fa.table, ("."+fa.column+" "), fv.description].join(''));
		}
	},
	
	/**
	 * Uses the filters argument to add filter tabs/tab content to the view
	 */
	'load':function(filters) {
		// filters is actually a collection of filters
		for(var i in filters.models) {
			this.add(filters.models[i]);
		}
	},
	
	/**
	 * Removes all filter tabs/tab content from the view 
	 */
	'clear':function() {
		$('ul[role="tablist"] li', this.$el).remove();
		$('div.tab-content', this.$el).empty();
	},
	
	
	'events':{},// TODO move click events into here
	
	'tagName':'div',
	'className':'panel-body cf-data-filters-container',
	
	'initialize':function(options) {
		// ASSERTION: these will always be passed
		// filtersController
		this.filtersController = options.filtersController;
		
		this.$el.append(this.dataFiltersControlBody({}));
		
		this.listenTo(this.filtersController.filters, 'reset', function(newFilters) {
			// newFilters should be a collection of filters
			this.clear();
			this.load(newFilters);
		});
	},
	'render':function() { return this; }
});

// View for the Common Value Filter Selection Control
var VCommonValueFilterControl = Backbone.View.extend({
	
	'selectedColumns':[],
	'selectedCount':0,
	
	'hide':function() {
		this.$el.hide();
	},
	'show':function() {
		this.$el.show();
	},
	'disable':function() {
		$('button.dropdown-toggle',this.$el).addClass('disabled');
	},
	'enable':function() {
		$('button.dropdown-toggle',this.$el).removeClass('disabled');
	},
	
	'getSelectedColumnData':function() {
		return this.selectedCount ? {
			'label':_.map(this.selectedColumns, function(c) { return c.attributes.label[0].toUpperCase()+c.attributes.label.substring(1); }).join(','), 
			'type':this.selectedColumns[0].attributes.type, 
			'name':_.map(this.selectedColumns, function(c) { return c.attributes.name; })  
		} : false;
	},
	
	
	'tagName':'div',
	'className':'btn-group cf-common-value-dropdown cf-dropdown-menu-scroll-small pull-left',
	'events':{
		// HOVER EVENTS FOR THE COLUMN DROPDOWN LIST ITEMS
		'mouseover ul.dropdown-menu li.cf-cvdd-active':function(e) {
			$(e.currentTarget).addClass('cf-common-value-list-item-hover');
		},
		'mouseleave ul.dropdown-menu li.cf-cvdd-active':function(e) {
			$(e.currentTarget).removeClass('cf-common-value-list-item-hover');
		},
		
		// DISABLED LIST ITEM CLICK (probably to prevent the click event from closing the dropdown)
		'click ul.dropdown-menu li.disabled':function(e) {
			return false;
		},
		
		// COLUMN LIST ITEM CLICK
		'click ul.dropdown-menu li.cf-cvdd-active button':function(e) {
			//if it wasn't selected, then make it selected
			//if it was selected, then de-select it
			var col = this.collection.findWhere({'type':$(e.currentTarget).data('type'),'name':$(e.currentTarget).data('name')}),
				newSelectedStatus = !col.get('selected'),
				//enables = this.collection.where({'type':col.get('type')}),
				disables = this.collection.difference(this.collection.where({'type':col.get('type')}));
			col.set('selected',newSelectedStatus);
			//console.log(col);//wondering if enum type should be excluded if their enum values are different
			
			this.selectedColumns = this.collection.where({'type':col.get('type'),'selected':true});
			this.selectedCount = this.selectedColumns.length;
			
			// toggle check visibility
			$('span',$(e.currentTarget)).toggleClass('hidden', !newSelectedStatus);
			
			if(newSelectedStatus) {//was not selected, now is
				
				// every model in the collection NOT of this type should be disabled
				for(var i in disables) {
					var elToDisable = $('ul.dropdown-menu li button[data-name="'+disables[i].get('name')+'"]',this.$el);
					elToDisable.attr('disabled','disabled');
					elToDisable.parent().removeClass('cf-cvdd-active').addClass('disabled');
				}
			} else {//de-selecting
				//de-select and remove from display list (trigger event for parent to handle)
				
				if(this.selectedCount<1) {
					//nothing selected, need to enable the other columns
					for(var i in disables) {
						var elToEnable = $('ul.dropdown-menu li button[data-name="'+disables[i].get('name')+'"]',this.$el);
						elToEnable.removeAttr('disabled');
						elToEnable.parent().addClass('cf-cvdd-active').removeClass('disabled');
					}
				}
				
			}
			
			this.trigger('columnClick', {
				'label':_.map(this.selectedColumns, function(c) { return c.attributes.label[0].toUpperCase()+c.attributes.label.substring(1); }).join(','), 
				'type':col.attributes.type, 
				'name':_.map(this.selectedColumns, function(c) { return c.attributes.name; })  
			});
			return false;
		}
	},
	
	'template':_.template(CFTEMPLATES.commonValueController,{variable:'data'}),
	
	'initialize':function(options) {
		/*
		 * columns is required in the options
		 * parse the columns array and remove any columns that are:
		 *   - the only one of its type
		 *   - enum type
		 * group the biglist types by their datasource, remove any that don't share a datasource
		*/
		var colTypes = _.countBy(options.columns, function(c) {return c.type;}),
			nonUniques = _.filter(options.columns, function(c) { return ( colTypes[c.type]>1 && c.type!=='enum' && c.type!=='biglist'); });
		
		if(_.has(colTypes,'biglist') && colTypes.biglist>1) {
			var bigLists = _.filter(options.columns, function(c) { return (c.type=='biglist'); }),
				bigListTables = _.countBy(bigLists, function(b) { return b.table; }),
				multiBigLists = _.filter(bigLists, function(c) { return bigListTables[c.table]>1; });
			nonUniques = _.union(nonUniques, multiBigLists);
		}
		
		this.collection = new Backbone.Collection( nonUniques );
		this.$el.append(this.template({'columns':nonUniques}));
	},
	
	'render':function() {
		return this;
	}
});


/* DataFilters
 * the main control class/view for column filters
 * 
*/
var VDataFilters = Backbone.View.extend({
	'version':'0.0.1c',
	/*
	Default: Column/Type-Based
	these should translate to AND clauses being appended to WHERE
	i.e. WHERE id>1 AND {column} {filter opperand} {filter value(s)}
	[column filter dropdown] [filter factory]
	
	Common Value: Value/Type-Based
	this should translate to a reverse IN clause being appended to WHERE for the columns given
	(technically it translates to an OR clause)
	i.e. WHERE id>1 AND (
		{filter value} IN({column1},{column2},...)
	)
	[column filter multi select dropdown] [filter factory]
	*/
	'FILTER_SELECTION_TYPES':{ 'DEFAULT':0, 'COMMON_VALUE':1 },
	
	// Enum of the different interactive modes this control can be put into
	// the dataFiltersControl (DataFiltersControlBar/VDataFiltersControlBar) has a version of this
	// DEFAULT: the controls for saving and creating filter sets are not available
	// CATEGORY_SETS: controls for creating/saving filter sets are available
	// NO_TYPES: the "Data Filters"/"Common Value" toggle buttons and the controls for creating/saving filter sets are not available
	// CATEGORIES_NO_TYPES: the controls for creating/saving filter sets are available, but not the "Data Filters"/"Common Value" toggle buttons
	'MODES':{ 'DEFAULT':0, 'CATEGORY_SETS':1, 'NO_TYPES':2, 'CATEGORIES_NO_TYPES':3 },
	
	'defaultConfig':{
		'mode':0,
		'table':'undefined',
		'showFirst':null,
		'filterSelectionType':0,
		'filters':false,
		'filterCategories':[],
		'convertBooleanToNumeric':true,
		'webServiceUrl':'/columnfilters'
	},
	'mode':0,					// setting the mode to 1 enables the saving filter sets and filter set groups
	'table':'undefined',		// the name of the database table or virtual source
	'filterSelectionType':0,	// the type of filter selection to display
	'filters':null,				// a collection of MDataFilter
	'filterCategories':[],		// array of names
	
	// this is for the Boolean filter widget
	'convertBooleanToNumeric':true,
	
	// for saving and loading the column filters (Filter Sets) to the server
	// if null, then local storage will be used
	'webServiceUrl':null,
	
	//the modal for add/edit filter sets
	// TODO this should be moved to VDataFiltersControlBar
	'modal':null,
	
	//cid of the model in the filters collection during an edit
	'editFilterCid':null,
	
	//used to keep track of filters displayed in the dataFiltersContainer
	'currentColumnFilter':{'table':null,'type':null,'column':null,'label':null},
	
	//used to restore after a save/cancel (filter edit)
	'previousColumnFilter':{'type':null, 'column':null, 'label':null},
	
	//used to keep track of the filter control nav bar dropdowns
	'preEditFilterControlStates':[],
	
	'commonValueControl':null,		//multi-column value filter control
	'filterFactory':null,			//all filter widgets
	'dataFiltersContainer':null,	//panel body view
	'dataFiltersControl':null,		//panel footer
	
	// Notification system:
	// Will be a warning or danger alert overlay in the filters container. The alert will fade out after about
	// 1 second unless the user hovers over (TODO implement touch system method)
	// the user will have to mouse out of the alert div in order to start the hide timer again.
	'notification':{
		'timeoutID':null,
		'displayDelay':1777,
		'templates':{
			'warning':_.template([
				'<div class="alert alert-warning alert-dismissable cf-notification fade in" role="alert">',
					'<button type="button" class="close" data-dismiss="alert">',
						'<span aria-hidden="true">&times;</span>',
						'<span class="sr-only">Close</span>',
					'</button>',
					'<h4><%= notification.title %></h4>',
					'<p><%= notification.message %></p>',
				'</div>'
			].join(''), {'variable':'notification'}),
			'danger':_.template([
				'<div class="alert alert-danger alert-dismissable cf-notification fade in" role="alert">',
					'<button type="button" class="close" data-dismiss="alert">',
						'<span aria-hidden="true">&times;</span>',
						'<span class="sr-only">Close</span>',
					'</button>',
					'<h4><%= notification.title %></h4>',
					'<p><%= notification.message %></p>',
				'</div>'
			].join(''), {'variable':'notification'})
		}
	},
	'notify':function(level, title, message) {
		// put an alert div in the filters container and set the width so we can 
		// center it with it being fixed position
		var newAlertDiv = $(this.notification.templates[level==='danger'?'danger':'warning']({'title':title, 'message':message}))
			.css({'width':$('.cf-data-filters-container').width()+'px'}),
			dfContext = this;
		//this.listenTo(newAlertDiv, 'mouseover', dfContext.quitHideNotifyTimer);
		//newAlertDiv.on('mouseover', function() { dfContext.quitHideNotifyTimer });
		newAlertDiv.hover(
			function() { dfContext.quitHideNotifyTimer(); },
			function() {
				dfContext.notification.timeoutID = setTimeout( function(){ dfContext.hideNotification(); }, dfContext.notification.displayDelay);
			}
		);
		//this.listenTo(newAlertDiv, 'mouseout', function() { setTimeout( function(){ dfContext.hideNotification(); }, dfContext.notification.displayDelay) });
		this.dataFiltersContainer.$el.prepend(newAlertDiv);
		
		// set the alert div to fade out after displayDelay milliseconds (use the DataFilters context)
		this.notification.timeoutID = setTimeout( function(){ dfContext.hideNotification(); }, dfContext.notification.displayDelay);
		
	},
	'hideNotification':function() {
		// this is executed in Window context
		$('div.cf-notification',this.dataFiltersContainer.$el).alert('close');
	},
	'quitHideNotifyTimer':function(e) {
		clearTimeout(this.notification.timeoutID);
	},
	
	// called from the event when the filter selection type radio set is changed
	'filterSelectionTypeChange':function(newSelectionType) {
		switch(newSelectionType) {
			case this.FILTER_SELECTION_TYPES.DEFAULT:
				this.filterSelectionType = this.FILTER_SELECTION_TYPES.DEFAULT;
				$('.cf-add-change-filter-group-button button.dropdown-toggle',this.$el).removeAttr('disabled').removeClass('disabled');
				
				// TODO check filter factory
				// reset filter factory
				this.filterFactory.reset().hide();
				
				this.commonValueControl.hide();
				break;
			case this.FILTER_SELECTION_TYPES.COMMON_VALUE:
				this.filterSelectionType = this.FILTER_SELECTION_TYPES.COMMON_VALUE;
				// disable change column dropdown
				$('.cf-add-change-filter-group-button button.dropdown-toggle',this.$el).attr('disabled','disabled').addClass('disabled');
				
				// check if columns are selected in the drop down
				// change to/show filter factory type if there is a selection
				// hide filter factory type if no selection
				if(this.commonValueControl.selectedCount) {
					var selColData = this.commonValueControl.getSelectedColumnData();
					this.changeFilterFactoryType(selColData.type,selColData.name,selColData.label);
					//this.commonValueColumnSelectionChange(this.commonValueControl.selectedColumns[0].attributes);
				} else {
					var af = this.filterFactory.activeFilter();
					if(af) {
						af.hide();
					}
				}
				
				// show common value control
				this.commonValueControl.show();
				break;
		}
	},
	
	// when a common value column item in the drop down list is changed
	// columnData: {label: string, name: could be a string or an array, type: string }
	// 
	'commonValueColumnSelectionChange':function(columnData) {
		//console.log(this.commonValueControl.selectedCount);
		if(this.commonValueControl.selectedCount) {// columns are selected
			//tell the filter factory to show this data type (if it isn't already)
			var af = this.filterFactory.activeFilter();
			if(af && af.type === columnData.type) {
				// type is the same, so just update the column
				this.currentColumnFilter.label = columnData.label;
				this.currentColumnFilter.column =_.map(this.commonValueControl.selectedColumns, function(c) { return c.attributes.name; });
				this.filterFactory.updateMultiColumnFilter(this.currentColumnFilter.column);
			} else {
				// type is not the same, change the type
				this.changeFilterFactoryType(columnData.type,columnData.name,columnData.label);
			}
		} else {//no more columns are selected
			//tell filter factorty to hide the active filter (if one is visible)
			var af = this.filterFactory.activeFilter();
			if(af) {
				af.hide();
				this.currentColumnFilter.type = null;
				this.currentColumnFilter.column = [];
				this.currentColumnFilter.label = null;
			}
		}
	},
	
	// changes the filter factory widget to the given type
	// column could be a string or an array
	'changeFilterFactoryType':function(type,column,label,subType) {
		//console.log(['changeFilterFactoryType >> type: ',type,', column: ',column,', label: ',label,', subType: ',subType].join(''));
		this.currentColumnFilter = {
			'table':this.table,
			'type':type,
			'column':column,
			'label':label
		};
		this.filterFactory.load(this.currentColumnFilter.column, this.currentColumnFilter.type, _.isArray(column)?'multi-column':this.currentColumnFilter.label, subType);
	},
	
	// show the save/cancel edit button group and disable everything but it and the filter factory
	'editFilterMode':function() {
		// show cancel and save filter button
		$('button.cf-edit-filter-button', this.$el).show();
		$('button.cf-cancel-edit-filter-button', this.$el).show();
		
		//	hide add filter/change column button
		$('.cf-add-change-filter-group-button button',this.$el).hide();
		
		//disable data filter type button group
		$('.cf-data-filter-type-selection label').addClass('disabled');
		$('.cf-data-filter-type-selection input').attr('disabled','disabled');
		
		//disable the common value control
		this.commonValueControl.disable();
		
		//save the filter factory state
		this.filterFactory.saveState();
		this.previousColumnFilter.type = this.currentColumnFilter.type;
		this.previousColumnFilter.column = this.currentColumnFilter.column;
		this.previousColumnFilter.label = this.currentColumnFilter.label;
		
		//	disable filter container
		this.dataFiltersContainer.disable();
		
		//disable the filter control nav bar
		if(this.mode === this.MODES.CATEGORY_SETS) {
			this.dataFiltersControl.disable();
		}
		
		//	disable filters control (need to keep track of what was already disabled)
		this.preEditFilterControlStates = [];
		var pefcs = this.preEditFilterControlStates;
		$('ul.nav li',this.dataFiltersControl).each(function(i,e) {
			pefcs.push({'listItem':$(e), 'hasDisabledClass':$(e).hasClass('disabled')});
			if(!$(e).hasClass('disabled')) {
				$(e).addClass('disabled');
			}
		});
	},
	
	// undo everything done in editFilterMode
	'cancelEditFilterMode':function() {
		$('button.cf-edit-filter-button', this.$el).hide();
		$('button.cf-cancel-edit-filter-button', this.$el).hide();
		$('.cf-add-change-filter-group-button button',this.$el).show();
		$('.cf-data-filter-type-selection label').removeClass('disabled');
		$('.cf-data-filter-type-selection input').removeAttr('disabled');
		//enable common value control
		this.commonValueControl.enable();
		
		this.filterFactory.restoreState();
		if(this.previousColumnFilter) {
			this.currentColumnFilter.type = this.previousColumnFilter.type;
			this.currentColumnFilter.column = this.previousColumnFilter.column;
			this.currentColumnFilter.label = this.previousColumnFilter.label;
		}
		
		this.dataFiltersContainer.enable();
		for(var i in this.preEditFilterControlStates) {
			var preFilterState = this.preEditFilterControlStates[i];
			if(!preFilterState.hasDisabledClass) {
				preFilterState.listItem.removeClass('disabled');
			}
		}
		
		//disable the filter control nav bar
		if(this.mode === this.MODES.CATEGORY_SETS) {
			this.dataFiltersControl.enable();
		}
	},
	
	// PUBLIC Functions
	// returns filters as an object, or false if there aren't filters to return
	'getCurrentFilter':function() {// deprecated name, will be removed
		return this.filters.length ? this.filters.toJSON() : false ;
	},
	'getFilters':function() {
		return this.filters.length ? this.filters.toJSON() : false ;
	},
	
	// adds a filter to the filter collection (filters)
	'addFilter':function(newFilter) {
		/* newFilter is expected to be:
		 * { table:, column:, label:, type:, filterValue:{ description:, type:, [value:], ... } }
		*/
		//console.log(newFilter);
		// check if we are in COMMON_VALUE mode
		// if it is, then check if more than 1 column has been selected
		if(this.filterSelectionType && this.currentColumnFilter.column.length<2) {
			alert('Multiple columns are required for a common value, otherwise just use a regular data filter.');
			return false;
		}
		
		// enable save filter dropdown
		if(this.mode === this.MODES.CATEGORY_SETS) {
			if($('li.cf-save-filter-list', this.dataFiltersControl).hasClass('disabled')) {
				$('li.cf-save-filter-list', this.dataFiltersControl).removeClass('disabled');
			}
		}
		
		// create new data filter
		// MDataFilter is the same thing as a standard Modal (it doesn't define anything specific)
		
		
		// listen for change event on the model
		newFilter.on('change:filterValue', function(filter) {
			//need to update filter tab content list item
			//console.log('filterValue change');
			this.dataFiltersContainer.updateFilter(filter);
		}, this);
		
		//add to the current category of filters
		this.filters.add(newFilter);
	},
	
	'tagName':'div',
	'className':'panel panel-default',
	
	'events':{
		
		// DATA FILTER TYPE CHANGE
		// triggered when the filter type (default/common value) is changed
		'change .btn-group.cf-data-filter-type-selection input':function(e) {
			var eVal = e.currentTarget.value*1;
			this.filterSelectionTypeChange(eVal);
		},
		
		
		// COLUMN FILTER CHANGE
		// triggered when a column list item is clicked in the columns dropdown menu
		// is to load the data info from the clicked event into the filter factory
		'click ul.cf-columns-select-dd li a':function(e) {
			this.changeFilterFactoryType($(e.currentTarget).data('type'),$(e.currentTarget).data('name'),$(e.currentTarget).html());
		},
		
		// ADD FILTER CLICK
		// triggered when the 'add filter' button is clicked
		// should first call validate on the active filter type
		'click button.cf-add-filter-button':function(e) {
			var af = this.filterFactory.activeFilter(),
				fVal = af?this.filterFactory.getFilterValue():false;
			//console.log(fVal);
			if(fVal) {
				var f = new MDataFilter({
					'table':this.table,
					'type':this.currentColumnFilter.type,
					'column':this.currentColumnFilter.column,
					'label':this.currentColumnFilter.label,
					'filterValue':fVal
				});
				this.addFilter(f);
			}
		},
		
		// SAVE EDIT FILTER CLICK
		// triggered when a filter is in edit mode and the 'save' button is clicked
		'click button.cf-edit-filter-button':function(e) {
			//get filter value from filterFactory and apply it to the filter in the collection
			//this should update the dataFiltersContainer view
			var fVal = this.filterFactory.getFilterValue();
			if(fVal) {
				this.cancelEditFilterMode();
				var f = this.filters.get(this.editFilterCid);
				f.set({'filterValue':fVal});
			}
		},
		
		// CANCEL EDIT FILTER CLICK
		// triggered when the cancel button has been clicked (when editing a filter)
		'click button.cf-cancel-edit-filter-button':function(e) {
			this.cancelEditFilterMode();
		}
	},
	
	
	'initialize':function(options) {
		if(_.has(options,'mode') && _.isNumber(options.mode)) {
			// TODO make sure passed in value exists in MODES
			this.defaultConfig.mode = this.mode = options.mode;
		}
		if(_.has(options,'table') && _.isString(options.table)) {
			this.table = options.table;
		}
		if(_.has(options,'showFirst') && _.isString(options.showFirst)) {
			this.defaultConfig.showFirst = options.showFirst;
		}
		if(_.has(options,'filterSelectionType') && _.isNumber(options.filterSelectionType)) {
			this.defaultConfig.filterSelectionType = this.filterSelectionType = options.filterSelectionType;
		}
		// webServiceUrl
		if(_.has(options,'webServiceUrl')) {
			this.webServiceUrl = options.webServiceUrl;
		}
		// can fetch filters from AJAX, or just populate
		if(_.has(options,'filterCategories') && _.isArray(options.filterCategories)) {
			this.defaultConfig.filterCategories = options.filterCategories;
		}
		// for the boolean filter widget
		if(_.has(options, 'convertBooleanToNumeric') && !options.convertBooleanToNumeric) {
			this.convertBooleanToNumeric = false;
		}
		
		// a collection to hold all the filters
		this.filters = new CDataFilters();
		
		// validTableColumns will populate the dropdown list of columns and the common value control
		var validTableColumns = [];
		if(options.hasOwnProperty('tableColumns') && _.isArray(options.tableColumns) && options.tableColumns.length) {
			/*assert tableColumns is an array of objects:
			--- DataTables properties ---
			'data':string, 
			'name':string, 
			'title':string, 
			'type':string, 
			'visible':boolean,
			'render':function,
			
			--- ColumnFilters properties ---
			'table':string
			'cfexclude':boolean,
			'cftype':string,
			'cfenumsource':array,
			'cfenumvaluekey':string // TODO implement
			'cfenumlabelkey':string
			
			  [biglist properties]
			  'table':string
			  'datasource':a bloodhound object
			  'displayKey':string
			  'valueKey':string
			*/
			for(var i in options.tableColumns) {
				var tc = options.tableColumns[i];
				if(_.isObject(tc) && (_.has(tc,'name') && _.has(tc,'type') && _.has(tc,'title'))) {
					// look for excluded data
					var excluded = false;
					if(_.has(tc,'cfexclude') && _.isBoolean(tc.cfexclude)) {
						excluded = tc.cfexclude;
					}
					if(!excluded) {
						// add extra properties for the common value control
						var mappedCol = {
							'label':tc.title,
							'type':tc.cftype,
							'name':tc.name
						};
						if(tc.cftype==='enum') {
							_.extend(mappedCol, {'cfenumsource':tc.cfenumsource,'table':tc.table, 'data':tc.data});
						}
						if(tc.cftype==='biglist') {
							// then datasource, displayKey, valueKey MUST exists
							_.extend(mappedCol, {
								'table':tc.table,
								'dataColumn':tc.data,
								'datasource':tc.datasource,
								'displayKey':tc.displayKey,
								'valueKey':tc.valueKey
							});
						}
						if(_.has(tc,'cfexclude')) {
							_.extend(mappedCol, {'cfexclude':tc.cfexclude});
						}
						if(_.has(tc,'cfenumlabelkey')) {
							_.extend(mappedCol, {'cfenumlabelkey':tc.cfenumlabelkey});
						}
						if(_.has(tc,'config')) {
							_.extend(mappedCol, {'config':tc.config});
						}
						_.extend(mappedCol,{'selected':false});
						validTableColumns.push(mappedCol);
					}
				}
			}
		}
		
		// TODO implement a way to override and add filter widget types and sub-types
		// Create and Populate the filter factory
		this.filterFactory = new VDataFilterFactory({
			'showOnInit':this.defaultConfig.showOnInit, 
			'collection':new Backbone.Collection([
				new VDataColumnFilterWidget({
					'type':'text', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeTextEq(),
						new VFilterWidgetTypeTextSrch()
					])
				}),
				new VDataColumnFilterWidget({
					'type':'number', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeNumberEq(),
						new VFilterWidgetTypeNumberBtwn(),
						new VFilterWidgetTypeNumberSel()
					])
				}),
				new VDataColumnFilterWidget({
					'type':'date', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeDateEq(),
						new VFilterWidgetTypeDateB4(),
						new VFilterWidgetTypeDateAfter(),
						new VFilterWidgetTypeDateBtwn(),
						new VFilterWidgetTypeDateSel(),
						new VFilterWidgetTypeDateCycle(),
						new VFilterWidgetTypeDateM(),
						new VFilterWidgetTypeDateMY(),
						new VFilterWidgetTypeDateYr()
					])
				}),
				new VDataColumnFilterWidget({
					'type':'boolean', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeBoolEq({'convertNumeric':this.convertBooleanToNumeric})
					])
				}),
				new VDataColumnFilterWidget({
					'type':'enum', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeEnumIn({'enums':_.where(validTableColumns, {'type':'enum'})})
					])
				}),
				new VDataColumnFilterWidget({
					'type':'biglist', 
					'collection':new Backbone.Collection([
						new VFilterWidgetTypeBiglistEq({'datasets':_.where(validTableColumns, {'type':'biglist'})})
					])
				})
			])
		});
		
		//////////////////////////////////
		// There will always be a user (or default) filter
		// should pull all table filters/column filters for this user + common and public
		this.dataFiltersContainer = new VDataFiltersContainer({'filtersController':this});
		//////////////////////////////////
		
		
		//////////////////////////////////
		// filters control; toolbar for saving groups of filters
		this.dataFiltersControl = new VDataFiltersControlBar({
			'url':this.webServiceUrl,
			'filtersController':this,
			'mode':this.defaultConfig.mode,
			'filterCategories':this.defaultConfig.filterCategories,
			'table':this.table
		});
		//////////////////////////////////
		
		// constructing the View elements (Heading:Filter Tools, Body:Filters, Footer:Save Controls)
		this.$el.append(
			_.template(CFTEMPLATES.dataFiltersPanelContent,{variable:'panelheading'})({'filterColumns':validTableColumns}),
			this.dataFiltersContainer.el,
			this.dataFiltersControl.el
		);
		
		// hack to get Backbone objects to update their 'this' references
		this.filterFactory.postConfig();
		
		//add UI components and set initial display states for UI
		this.commonValueControl = new VCommonValueFilterControl({'columns':validTableColumns});
		$('div.cf-common-value-controller-replace',this.$el).replaceWith(this.commonValueControl.$el);
		$('.cf-filter-factory-container-row',this.$el).append(this.filterFactory.el);
		$('button.cf-edit-filter-button', this.$el).hide();
		$('button.cf-cancel-edit-filter-button', this.$el).hide();
		
		
		// EVENT HANDLERS
		// event handler when a filter is added
		this.filters.on('add', function(filter) {
			//console.log('handling filters.add');
			this.dataFiltersContainer.add(filter);
			if(this.mode===this.MODES.CATEGORY_SETS) {
				this.dataFiltersControl.refreshClearFiltersButton();
			}
		}, this);
		
		this.filters.on('remove', function(filter) {
			//console.log('handling filters.remove');
			if(this.filters.length<1) {
				// disable the add filter dropdown
				$('li.cf-save-filter-list', this.dataFiltersControl).addClass('disabled');
			}
			if(this.mode===this.MODES.CATEGORY_SETS) {
				this.dataFiltersControl.refreshClearFiltersButton();
			}
		}, this);
		
		// when the remove button from a filter in the filter container view is clicked
		this.listenTo(this.dataFiltersContainer,'removeClick', function(filterCid) {
			this.filters.remove(this.filters.get(filterCid));
		});
		
		// when the edit button from a filter in the filter container view is clicked
		// sets the filter factory to the correct filter type and initializes with filter value
		this.listenTo(this.dataFiltersContainer,'changeClick', function(filterCid) {
			this.editFilterCid = filterCid;
			this.editFilterMode();
			
			var f = this.filters.get(this.editFilterCid).attributes;
			//console.log(f);
			this.changeFilterFactoryType(f.type,f.column,f.label,f.filterValue.type);
			this.filterFactory.setFilterValue(f);
		});
		
		// when a common value column is clicked
		this.listenTo(this.commonValueControl, 'columnClick', this.commonValueColumnSelectionChange);
		
		// when a updateFilter event is triggered in the filter control bar
		this.listenTo(this.dataFiltersControl, 'updateFilter', function(filter) {
			this.dataFiltersContainer.updateFilter(filter);
		});
		
		// when a clear filters event is triggered from the filter control bar
		// newSet is either empty or a filterSet clone
		this.listenTo(this.dataFiltersControl, 'resetFilters', function(newSet) {
			if(newSet) {
				this.filters.reset(newSet);
			} else {
				this.filters.reset();
			}
		});
		
		// notification events (level,title,message)
		this.listenTo(this.filterFactory, 'notify', this.notify);
		this.listenTo(this.dataFiltersControl, 'notify', this.notify);
		
		
		// handle when filterSelectionType is passed with a value other than FILTER_SELECTION_TYPES.DEFAULT
		if(this.filterSelectionType != this.FILTER_SELECTION_TYPES.DEFAULT) {
			//call function as if the click event was triggered
			this.filterSelectionTypeChange(this.filterSelectionType);
		} else {
			// default to single filter type, hide commonValueControl
			this.commonValueControl.hide();
		}
		
		// TODO also check if filter selection type is DEFAULT
		if(_.isString(this.defaultConfig.showFirst)) {
			var dfDdLi = $('ul.cf-columns-select-dd li a[data-name="'+this.defaultConfig.showFirst+'"]',this.$el);
			if(dfDdLi.length) {
				this.changeFilterFactoryType(dfDdLi.first().data('type'),dfDdLi.first().data('name'),dfDdLi.first().html());
			}
		}
		
		// check for custom UI to add
		// options.customUI is assumed to be anyting $.append() would expect
		if(_.has(options, 'customUI')) {
			$('div.cf-custom-ui-container', this.$el).append(options.customUI);
		}
		
		// the MODES.NO_TYPES is a custom mode where custom UI buttons can be added to the panel header
		switch(this.mode) {
			case this.MODES.NO_TYPES:
			case this.MODES.CATEGORIES_NO_TYPES:
				//console.log('setting up column filters for custom mode');
				$('div.cf-data-filter-type-selection',this.$el).hide();
				break;
			case this.MODES.ALL:
				
				break;
		}
		
		
		// check for filters passed in
		if(_.has(options, 'filters')) {
			// need to pre-populate the filters collection
			for(var fidx in options.filters) {
				var mdf = new MDataFilter({
					'table':options.filters[fidx].table,
					'type':options.filters[fidx].type,
					'column':options.filters[fidx].column,
					'label':options.filters[fidx].label,
					'filterValue':options.filters[fidx].filterValue
				});
				this.addFilter(mdf);
				//this.dataFiltersControl.enable();
			}
			this.dataFiltersContainer.showTabContent();
		}
	},
	
	'render':function() { return this; }
});


/*
The view controller for saving/loading/removing filter sets
This view will have a collection that makes use of a properly structured JSON Object, 
LocalStorage, Backbone.Collection with AJAX backend to a DB

Filter Category Structure
{
	name:	<string>	short-form label (under 45 characters)
	sets:	<array>		a collection of filter sets // TODO 
}

Filter Set Structure
[
	{
		id:				<integer>	unique identifier (usually a database auto increment sequence)
		name:			<string>	short-form label (under 45 characters)
		category:		<string>	name of the category that this set belongs
		table:			<string>	the database table (or parent-level object) name
		description:	<string>	long-form description of category
		filters:		<array>		a collection of filter objects
	},...

this.collection({model:Filter Set})

There is also a local version of the internal collection
*/
var VDataFiltersControlBar = Backbone.View.extend({
	
	// Enum of the different interactive modes this control can be put into
	'MODES':{ 'DEFAULT':0, 'CATEGORY_SETS':1, 'NO_TYPES':2, 'CATEGORIES_NO_TYPES':3 },
	
	// 
	'isLocalStorage':false,
	
	// the parent controller view that has a 'filters' collection
	'filtersController':null,
	'modal':null,// form modal for inputting category/filter set names and descriptions
	
	// these are passed from the parent controller and are attached to each filter set
	// for use in storing them  in a database per user
	'table':null,
	
	'categories':null,			// a collection of category names
	'currentFilterSetCid':null,	// should be the cid of the category in the categories collection
	'editMode':false,			// set to true when editing a filter set
	
	// for rendering components of this view
	'templates':{
		
		'filterCategorySaveItem':_.template([
			'<li data-save-type="<%= filterCategory.name %>">',
				'<a href="#">',
					'<span class="badge pull-right">',
						'<span class="glyphicon <%= filterCategory.glyph %>"></span>',
					'</span> to <%= filterCategory.name %>',
				'</a>',
			'</li>'
			].join(''),
		{'variable':'filterCategory'}),
		
		'filterCategoryMenu':_.template([
			'<ul class="nav navbar-nav" data-category-name="<%= filterCategory.name %>">',
				'<li class="dropup btn btn-xs disabled">',
					'<a href="#" class="dropdown-toggle btn btn-xs" data-toggle="dropdown"><%= filterCategory.name %> ',
						'<span class="badge"></span>',
						'<span class="caret"></span>',
					'</a>',
					'<ul class="dropdown-menu list-group cf-filter-category-menu-list" role="menu"></ul>',
				'</li>',
			'</ul>'
			].join(''),
		{'variable':'filterCategory'}),
		
		'filterSetMenuItem':_.template([
			'<li class="list-group-item" data-id="<%= filterSet.cid %>">',
				'<button type="button" class="close" title="edit this filter set" data-type="edit" data-id="<%= filterSet.cid %>">',
					'<span class="glyphicon glyphicon-cog btn-sm"></span>',
				'</button>',
				'<button type="button" class="close" title="delete this filter set" data-type="remove" data-id="<%= filterSet.cid %>">',
					'<span class="glyphicon glyphicon-remove btn-sm"></span>',
				'</button>',
				'<h4 class="list-group-item-heading" title="load filters from this set">',
					'<a href="#" data-id="<%= filterSet.cid %>"><%= filterSet.get("name") %></a>',
				'</h4>',
				'<p class="list-group-item-text"><%= filterSet.get("description") %></p>',
			'</li>'
		].join(''),
		{'variable':'filterSet'}),
		
		'saveFilterSetModalForm':_.template([
			'<form class="form-horizontal" role="form" data-category="">',
				'<div class="form-group">',
					'<label for="cfFilterSetSaveName" class="col-sm-2 control-label">Name</label>',
					'<div class="col-sm-10">',
						'<input type="text" class="form-control" id="cfFilterSetSaveName" placeholder="Name for this set of filters" autocomplete="off">',
					'</div>',
				'</div>',
				'<div class="form-group cf-form-filter-set-desc">',
					'<label for="cfFilterSetSaveDescription" class="col-sm-2 control-label">Description</label>',
					'<div class="col-sm-10">',
						'<textarea class="form-control" rows="3" id="cfFilterSetSaveDescription" autocomplete="off"></textarea>',
					'</div>',
				'</div>',
			'</form>'
		].join(''))
	},
	
	'navbar':null,				// the main navbar control for this view
	'saveDropdown':null,		// the dropdown menu for saving filter sets or creating a new category
	'cancelButton':null,		// cancel button displayed when editing a filter set
	'saveButton':null,			// action button displayed when editing a filter set
	'clearFiltersButton':null,	// button for triggering a 'clearFilters' event up-stream to clear the filters container view
	
	'enable':function() {
		this.saveButton.removeClass('disabled')[0].disabled = false;
		this.refreshClearFiltersButton();
		if(!this.editMode) {
			$('ul.navbar-nav',this.navbar).each(function(i,navUl) {
				if($('li.dropup ul.dropdown-menu li',$(navUl)).length) {
					$('li.dropup',$(navUl)).removeClass('disabled');
				}
			});
		}
	},
	'disable':function() {
		this.saveButton.addClass('disabled')[0].disabled = true;
		this.clearFiltersButton.addClass('disabled')[0].disabled = true;
		$('ul.navbar-nav li.dropup',this.navbar).addClass('disabled');
	},
	
	/**
	 * This will enable/disable the "clear filters" button (the "x" button next to the "save to"
	 * menu drop up) based on the number of existing filters. If less than 1 then disable
	*/
	'refreshClearFiltersButton':function() {
		this.clearFiltersButton.toggleClass('disabled', this.filtersController.filters.length<1);//was <1
		this.clearFiltersButton[0].disabled = this.filtersController.filters.length?false:true;
	},
	
	// 
	'filterCategoryGlyphMapping':function(catName) {
		var retVal = 'glyphicon-cloud-upload';
		switch(catName) {
			case 'User':
			case 'user':
				retVal = 'glyphicon-user';
				break;
		}
		return retVal;
	},
	
	/**
	 * This will add a menu list item to the save category dropdown menu. It will 
	 * also add a list item to the category dropdown menu for the FilterSet, and 
	 * create the category dropdown menu if needed.
	 * This function does NOT add a FilterSet to the collection.
	 */
	'addFilterSet':function(filterSet) {
		//console.log('adding filter set');
		//console.log(filterSet);
		
		// check if the filter set category exists; add it if it doesn't
		if(this.categories.where({'name':filterSet.get('category')}).length<1) {
			//console.log('adding filter category: '+filterSet.get('category'));
			this.addCategory(filterSet.get('category'));
		}
		
		// adds a dropdown menu item to the category dropdown menu on the nav bar
		// removes the disabled state of the category dropdown menu
		// updates the category dropdown menu label to include the number of filter sets
		var fcMenuDropdown = $('ul.navbar-nav[data-category-name="'+filterSet.get('category')+'"]',this.navbar),
			newFilterSet = this.templates['filterSetMenuItem'](filterSet);
		
		//add filter set menu item
		$('ul.cf-filter-category-menu-list', fcMenuDropdown).append(newFilterSet);
		$('li.dropup',fcMenuDropdown).removeClass('disabled');
		$('li.dropup span.badge',fcMenuDropdown).html(this.collection.where({'category':filterSet.get('category')}).length);
	},
	
	// makes sure there are no duplicates and then adds a menu dropup to the footer control
	// and a dropup link to the save filters menu
	'addCategory':function(categoryName) {
		//console.log('addCategory('+categoryName+')');
		//console.log(this.collection);
		//console.log(this.categories);
		// if a category menu with the same name doesn't already exist
		if(this.categories.where({'category':categoryName}).length<1) {
			// add category name to the categories collection
			this.categories.add({'name':categoryName});
			
			// add a category menu dropup to the footer control nav bar
			this.navbar.append( this.templates.filterCategoryMenu({'name':categoryName}) );
			
			// add a divider inbetween each category list item in the save menu (after the first)
			if(this.collection.length) {
				this.saveDropdown.append( $(document.createElement('li')).addClass('divider') );
			}
			
			// add list item to the save menu dropup
			this.saveDropdown.append(
				this.templates.filterCategorySaveItem({
					'name':categoryName,
					'glyph':this.filterCategoryGlyphMapping(categoryName)
				})
			);
		}
	},
	
	// configure modal and show
	'modalConfigAndShow':function(isNewCategory) {
		var mTitle = isNewCategory?'Create New Category':'Save to Filter Set',
			mSaveType = isNewCategory?'category':'set',
			mBtnLabel = isNewCategory?'Create':'Save',
			saveBtn = $('div.modal-footer button:last-child',this.modal);
		
		$('form', this.modal)[0].reset();
		saveBtn.data('save-type',mSaveType);
		saveBtn.html(mBtnLabel);
		$('h4.modal-title',this.modal).html(mTitle);
		$('div.cf-form-filter-set-desc',this.modal).toggle(!isNewCategory);
		this.modal.modal('show');
	},
	
	/**
	 * Resets the filtersController.filters collection with the filters retrieved 
	 * from this.collection by using the filterSetId argument and then triggers 
	 * the resetFilters event for the DataFilters parent controller to handle.
	 */
	'loadFilters':function(filterSetId) {
		//console.log(filterSetId);
		var clonedFilterSet = this.collection.get(filterSetId).clone(),
			clonedFilterSetObject = $.extend(true,{},clonedFilterSet.attributes),// this should've made a deep copy and converted the model to an object
			deepCopyFilterArray = [];
		// the filters might be an array of javascript objects or models
		for(var i in clonedFilterSetObject.filters) {//loop through each filter
			var fsFilter = clonedFilterSetObject.filters[i],
				isModel = _.has(fsFilter,'attributes'),
				f = new MDataFilter();
			f.set({
				'table'			: isModel ? fsFilter.get('table') : fsFilter.table,
				'type'			: isModel ? fsFilter.get('type') : fsFilter.type,
				'column'		: isModel ? fsFilter.get('column') : fsFilter.column,
				'label'			: isModel ? fsFilter.get('label') : fsFilter.label,
				'filterValue'	: $.extend(true, {}, isModel ? fsFilter.get('filterValue') : fsFilter.filterValue)
			});
			
			// listen for change event on the model and update the text labels in the filter container
			f.on('change:filterValue', function(filter) {
				//need to update filter tab content list item
				this.trigger('updateFilter',filter);
				//this.filtersController.dataFiltersContainer.updateFilter(filter);
			}, this);
			
			deepCopyFilterArray.push( f );
		}
		//console.log(deepCopyFilterArray);
		// the resetFilters event should pass a deep-copy clone collection of filters
		this.trigger( 'resetFilters', deepCopyFilterArray );
	},
	
	
	'events':{
		// NEW FILTER CATEGORY CLICK
		// triggered when the "create new" filter list item in the save filter set menu is clicked
		// or the "save to ..." menu item in the save filter set is clicked
		'click li.cf-save-filter-list ul.dropdown-menu li':function(e) {
			// are there any filters to save ?
			var dataSaveType = $(e.currentTarget).data('save-type'),
				isCreatingNewCategory = (dataSaveType==='__new_category__');
			
			if(isCreatingNewCategory) {
				this.modalConfigAndShow(isCreatingNewCategory);
			} else {
				// creating a new filter set put category name in the form data-category attribute
				$('form',this.modal).data('category',dataSaveType);
				if(this.filtersController.filters.length) {
					this.modalConfigAndShow(isCreatingNewCategory);
				} else {
					this.trigger('notify', 'danger', 'No filters to save', 'There must be some filters to save.');
				}
			}
		},
		
		// CLEAR FILTERS CLICK
		// triggered when the clear filters button is clicked
		'click button.cf-clear-all-filters-button':function(e) {
			this.trigger('resetFilters');
		},
		
		// DONE EDITING FILTER SET CLICK
		// triggered when the "Done" button in the nav bar has been clicked
		'click button.cf-save-filter-set-changes-button':function(e) {
			// check if there are any filters to save
			if(this.filtersController.filters.length) {
				// put all existing filters (filtersController.filters) into the filters attribute of this collection model
				this.collection.get(this.currentFilterSetCid).attributes.filters = this.filtersController.filters.clone().toJSON();
				
				// enable category menus and save menu
				$('ul.navbar-nav li.dropup',this.navbar).addClass('disabled');
				
				// hide editing buttons and set the mode back to normal
				this.saveButton.hide();
				this.cancelButton.hide();
				this.editMode = false;
				this.refreshClearFiltersButton();
				
				// update the collection filter set model
				this.collection.sync('update', this.collection.get(this.currentFilterSetCid), {
					'context':this,
					'success':function(data, textStatus, jqXHR){
						this.enable();
				}});
				
			} else {
				this.trigger(
					'notify', 
					'danger', 
					'No filters to save to filter group', 
					['There are no filters to save, if your intent is to remove this filter group, ',
					'click the remove button (next to the edit button) on the filter group in the category menulist.'].join('')
				);
			}
		},
		
		// CANCEL EDIT FILTER SET CLICK
		// triggered when the "Cancel" button in the nav bar has been clicked
		'click button.cf-cancel-filter-set-changes-button':function(e) {
			// restore filters in the filter set
			this.loadFilters(this.currentFilterSetCid);
			
			// restore navbar controls
			//check menus for list items, only enable if there are some
			$('ul.navbar-nav',this.navbar).each(function(i,navUl) {
				if($('li.dropup ul.dropdown-menu li',$(navUl)).length) {
					$('li.dropup',$(navUl)).removeClass('disabled');
				}
			});
			this.saveButton.hide();
			this.cancelButton.hide();
			this.editMode = false;
			this.refreshClearFiltersButton();
		},
		
		// LOAD FILTER SET CLICK
		// when the link in the category menu item is clicked
		'click ul.cf-filter-category-menu-list li h4 a':function(e) {
			var fsId = $(e.currentTarget).data('id');
			this.loadFilters($(e.currentTarget).data('id'));
			this.refreshClearFiltersButton();
		},
		
		// EDIT FILTER SET CLICK
		// triggered when the edit button is clicked in a filter set menu item
		'click ul.navbar-nav li.dropup ul.cf-filter-category-menu-list button[data-type="edit"]':function(e) {
			this.editMode = true;
			
			// store the selected filter set in currentFilterSetCid variable
			this.currentFilterSetCid = $(e.currentTarget).data('id');
			
			// load filters from the selected filter set
			this.loadFilters(this.currentFilterSetCid);
			
			// show the "done editing" button
			this.saveButton.show();
			this.cancelButton.show();
			// disable category menus and save menu
			$('ul.navbar-nav li.dropup',this.navbar).addClass('disabled');
		},
		
		// REMOVE FILTER SET CLICK
		// triggered when the remove button is clicked in a filter set menu item
		'click ul.navbar-nav li.dropup ul.cf-filter-category-menu-list button[data-type="remove"]':function(e) {
			if(confirm('Are you sure you want to remove this Filter Set?')) {
				this.collection.remove( this.collection.get($(e.currentTarget).data('id')) );
			}
		},
		
		// MODAL ACTION BUTTON CLICK
		// triggered when the 'save' button in the modal is clicked (new category or set)
		'click div.modal div.modal-footer button:last-child':function(e) {
			var saveType = $(e.currentTarget).data('save-type'),
				fsName = $.trim($('input#cfFilterSetSaveName',this.modal).val()),
				valid = (fsName.length>0);
			
			if(valid) {
				if(saveType==='set') {
					var category = $('form',this.modal).data('category'),
						fsDesc = $.trim($('textarea#cfFilterSetSaveDescription',this.modal).val());
					//create new filter set with all the filters
					// send filters as javascript objects (not models)
					//console.log('creating new category');
					
					// I think this works for remote and local
					/**/
					this.collection.create({
						'category':category,
						'table':this.table,
						'name':fsName,
						'description':fsDesc.length?fsDesc:null,
						'filters':this.filtersController.filters.clone().toJSON()
					});
					
					// This works for remote and local storage
					/*
					this.collection.add(
						new MDataFilter({
							'category':category,
							'table':this.table,
							'name':fsName,
							'description':fsDesc.length?fsDesc:null,
							'filters':this.filtersController.filters.clone().models
						})
					);
					*/
				} else {
					//adding a new category
					this.addCategory(fsName);
				}
				
				//close the modal
				this.modal.modal('hide');
			} else {
				alert('The name input can not be empty.');
			}
		}
	},
	
	'tagName':'nav',
	'className':'navbar navbar-default cf-datafilters-controller-footer',
	
	'initialize':function(options) {
		// ASSERTION: these will always be passed
		// url
		// filtersController
		// mode
		// filterCategories
		// table
		
		// add role=navigation attribute to root dom element
		this.$el.attr('role','navigation');
		// if the CATEGORY_SETS mode was passed into the constructor then 
		if(options.mode===options.filtersController.__proto__.MODES.CATEGORY_SETS || options.mode===options.filtersController.__proto__.MODES.CATEGORIES_NO_TYPES) {
			
			// the parent DataFilters View controller
			this.filtersController = options.filtersController;
			
			// set the table property (there should only be 1 table per column filters controller)
			this.table = options.table;
			
			// just a collection of names
			this.categories = new Backbone.Collection();
			
			this.editMode = false;
			
			// the collection of filter sets, where we can pluck the categories from the category property of each set
			if(options.url) {
				this.collection = new CDataFilterSets();
				this.collection.url = options.url;
			} else {
				this.collection = new CDataFilterSetsLocal();
				this.isLocalStorage = true;
			}
			
			// create the DOM elements
			this.$el.append(
				_.template(
					CFTEMPLATES.dataFiltersControlFooter,
					{variable:'controller'}
				)({'filterCategories':options.filterCategories}));
			
			// set the navbar property
			this.navbar = $('div.collapse.navbar-collapse',this.$el);
			
			// set the saveDropdown and cancelButton properties
			this.saveDropdown = $('ul.navbar-right li.cf-save-filter-list ul.dropdown-menu',this.navbar);
			this.cancelButton = $('button.cf-cancel-filter-set-changes-button',this.navbar).hide();
			
			// set the saveButton and clearFiltersButton properties and disable
			// the clearFiltersButton since there won't be any filters to begin with
			this.saveButton = $('button.cf-save-filter-set-changes-button',this.navbar).hide();
			this.clearFiltersButton = $('button.cf-clear-all-filters-button',this.navbar);
			this.clearFiltersButton[0].disabled = true;
			
			// re-usable modal
			this.modal = $('div.modal',this.$el);
			$('div.modal-body', this.modal).html(this.templates.saveFilterSetModalForm({}));
			this.modal.modal({
				'backdrop':'static',
				'keyboard':false,
				'show':false
			});
			
			// if there were filter categories passed in then add the menu items for each one
			if(options.filterCategories.length) {
				for(var i in options.filterCategories) {
					//a category is just a small string
					this.addCategory(options.filterCategories[i]);
				}
			}
			
			// EVENT LISTENERS
			
			// add filter set
			this.collection.on('add', function(filterSet) {
				//console.log('data filter collection add');
				//console.log(filterSet);
				// add filterset to ui nav bar and add sync listeners
				this.addFilterSet(filterSet);
			}, this);
			
			// remove filter set
			this.collection.on('remove', function(filterSet) {
				// check for filter set category
				if(filterSet.attributes.category) {
					// remove list item from category dropdown menu
					// disable filer category dropdown menu if no more filter sets exists in that category
					var fcMenuDropdown = $('ul.navbar-nav[data-category-name="'+filterSet.get('category')+'"]', this.navbar);
					$('ul.cf-filter-category-menu-list li[data-id="'+filterSet.cid+'"]',fcMenuDropdown).remove();
					var filterSetsArray = this.collection.where({'category':filterSet.get('category')});
					$('li.dropup',fcMenuDropdown).toggleClass('disabled', filterSetsArray.length===0);
					$('li.dropup span.badge',fcMenuDropdown).html(filterSetsArray.length?filterSetsArray.length:'');
					
					this.collection.sync('delete', filterSet, {
						'context':this,
						'success':function(data, textStatus, jqXHR){
							this.enable();
					}});
				}
			}, this);
			
			// sync request event handler for the collection
			// starting a request to the server
			this.collection.on('request', function(col, xhr, opts) {
				//console.log('colleciton.request');
				if(!this.isLocalStorage) {
					//console.log('collection.request');
					this.disable();
				}
			}, this);
			
			// sync response event handler for the collection
			this.collection.on('sync', function(col, resp, opts) {
				// resp should be an Array of filterSet models
				// TODO ?? update the category dropdown menus
				//console.log('collection.sync');
				this.enable();
			}, this);
			
			// sync error event handler for the collection
			this.collection.on('error', function(col, resp, opts) {
				if(this.isLocalStorage) {
					console.warn('sync.error, but handled because isLocalStorage is true');
					this.enable();
				} else {
					console.error('sync:error');
					console.log(resp);
					console.log(opts);
				}
			}, this);
			
			// pass the table with the fetch -- collection.read
			if(!this.isLocalStorage) {
				this.collection.fetch({'data':{'table':options.table}});
			}
		}
	},
	'render':function() { return this; }
});

var CDataFilterSetsLocal = Backbone.Collection.extend({
	'model':MFilterSet,
	'localStorage':null,
	
	'initialize':function(options) {
		console.log('initializing Local CDataFilterSets collection');
		this.url = 'columnfilters';
		this.sync = function(method, payload, opts) {
			console.log('Data Filters Control.sync');
			switch(method) {
				case 'create':
					console.log('Data Filters Control.sync.create');
					console.log(payload);
					//console.log(opts);
					this.localStorage.add(payload);
					opts.success();
					break;
				case 'read':
					console.log('Data Filters Control.sync.read');
					console.log(payload);//the collection
					console.log(opts);
					if(this.localStorage==null) {
						// create a new local filterset database
						this.localStorage = new Backbone.Collection([], {'model':MFilterSet});
					} else {
						var fsets = this.localStorage.where({'table':opts.table});
						if(fsets.length) {
							payload.add(fsets);
						}
					}
					opts.success();
					break;
				case 'update':
					console.log('Data Filters Control.sync.update');
					console.log(payload);
					console.log(opts);
					// value alread changed in collection
					// TODO opts.context.enable();
					this.trigger('sync', this, null,null);
					//opts.success(payload, null, null);
					break;
				case 'delete':
					console.log('Data Filters Control.sync.delete');
					console.log(payload);
					console.log(opts);
					
					break;
				default:
					console.log('Data Filters Control.sync.'+method);
					//console.log(payload);
					//console.log(opts);
					
					break;
			}
		};
	}
});

// Filter Widget Type Implementation Class for Text (Equals)
var VFilterWidgetTypeTextEq = VFilterWidgetType.extend({
	'version':'1.0.4',
	'type':'equals',
	
	
	'isValid':function() {
		return $.trim($('input',this.$el).val()).length>0;
	},
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Text Filter (Equals) Error', 'The text for the equals filter can not be empty.');
		return false;
	},
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is equal to ' + $.trim($('input',this.$el).val());
		} else {
			return false;
		}
	},
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':$.trim($('input',this.$el).val()),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	'setValue':function(filterValue) {
		$('input',this.$el).val(filterValue.value);
	},
	'reset':function() {
		$('input',this.$el).val(null);
	},
	
	'className':'row',
	'template':_.template([
		'<div class="col-xs-12">',
			'<input type="text" placeholder="equals" class="form-control" autocomplete="off" value="" />',
			'<span class="help-block">filtering the results by column values equal to this</span>',
		'</div>',
	].join('')),
	
	'initialize':function(options) {
		// TODO attributes for text input
		this.$el.html(this.template());
	}
});


// Filter Widget Type Implementation Class for Text (Search)
var VFilterWidgetTypeTextSrch = VFilterWidgetType.extend({
	'version':'1.0.2',
	'type':'search',
	
	
	'isValid':function() {
		return $.trim($('input',this.$el).val()).length>0;
	},
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Text Filter ('+this.type+') Error', 'The text for the search filter can not be empty.');
		return false;
	},
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is like to ' + $.trim($('input',this.$el).val());
		} else {
			return false;
		}
	},
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':$.trim($('input',this.$el).val()),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	'setValue':function(filterValue) {
		$('input',this.$el).val(filterValue.value);
	},
	'reset':function() {
		$('input',this.$el).val(null);
	},
	
	
	'className':'row',
	'template':_.template([
		'<div class="col-xs-12">',
			'<input type="text" placeholder="is like" class="form-control" autocomplete="off" value="" />',
			'<span class="help-block">filtering the results by column values similar to this</span>',
		'</div>',
	].join('')),
	
	'initialize':function(options) {
		// TODO attributes for text input
		this.$el.html(this.template());
	}
});


// Filter Widget Type Implementation Class for Number (Equals)
var VFilterWidgetTypeNumberEq = VFilterWidgetType.extend({
	'version':'1.0.3',
	'type':'equals',
	
	'isValid':function() {
		return !isNaN(this.model.get('sb').spinbox('value')*1);
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Number Filter ('+this.type+') Error', 'A valid number must be given.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is equal to ' + this.model.get('sb').spinbox('value')*1;
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'numberType':this.model.get('numberType'),
				'value':this.model.get('sb').spinbox('value')*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		this.model.get('sb').spinbox('value', filterValue.value);
	},
	
	'reset':function() {
		this.setValue(0);
	},
	
	'template':_.template([
		'<div class="spinbox digits-5<% if(_.has(spinbox,"name")) { %> <%= spinbox.name %><% } %>">',
			'<input type="text" class="form-control input-mini spinbox-input" />',
			'<div class="spinbox-buttons btn-group btn-group-vertical">',
				'<button class="btn btn-default spinbox-up btn-xs">',
					'<span class="glyphicon glyphicon-chevron-up"></span><span class="sr-only">Increase</span>',
				'</button>',
				'<button class="btn btn-default spinbox-down btn-xs">',
					'<span class="glyphicon glyphicon-chevron-down"></span><span class="sr-only">Decrease</span>',
				'</button>',
			'</div>',
		'</div>',
		'<span class="help-block">filtering the results by column values equal to this</span>'
	].join(''), {variable:'spinbox'}),
	
	'initialize':function(options) {
		this.$el.addClass('fuelux');
		
		this.model = new Backbone.Model({
			'sb':null,
			'sbOptions':{'min':-10, 'max':100, 'step':.25},
			'numberType':'integer'
		});
		
		console.log(options);
		
		// make this a spinner (FuelUX, JQueryUI)
		this.$el.html(this.template({}));
		$('.spinbox', this.$el).spinbox(this.model.get('sbOptions'));
		this.model.set('sb', $('.spinbox', this.$el));
	},
	
	'render':function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Between)
var VFilterWidgetTypeNumberBtwn = VFilterWidgetType.extend({
	'version':'1.0.2',
	'type':'between',
	'sbFrom':null,
	'sbTo':null,
	'sbOptions':{
		//value:<number>
		//min:<number>
		//max:<number>
		//step:<number>
		//hold:<boolean>
		//speed:<string> "fast","medium","slow"
		//disabled:<boolean>
		//units:<array> array of strings that are allowed to be entered in the input with the number
		'min':-10, 'max':100, 'step':.25
	},
	
	
	'isValid':function() {
		var fromNum = this.sbFrom.spinbox('value')*1,
			toNum = this.sbTo.spinbox('value')*1,
			fromNumCheck = !isNaN(fromNum),
			toNumCheck = !isNaN(toNum),
			isNotEqualCheck = (fromNum!==toNum);
		return (fromNumCheck && toNumCheck && isNotEqualCheck);
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Number Filter ('+this.type+') Error', 'A from and to number must be given.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is between ' + this.sbFrom.spinbox('value') + ' and ' + this.sbTo.spinbox('value');
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'from':this.sbFrom.spinbox('value')*1,
				'to':this.sbTo.spinbox('value')*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		//data is expected to be an object with from/to keys
		if(_.has(filterValue,'from') && _.isNumber(filterValue.from)) {
			this.sbFrom.spinbox('value',filterValue.from);
		}
		if(_.has(filterValue,'to') && _.isNumber(filterValue.to)) {
			this.sbTo.spinbox('value',filterValue.to);
		}
	},
	
	'reset':function() {
		this.setValue({'from':0,'to':0});
	},
	
	
	'events':{
		'changed.fu.spinbox div.spinbox.sbFrom':function(e) {
			//console.log('spinbox from changed');
			// TODO
		},
		'changed.fu.spinbox div.spinbox.sbTo':function(e) {
			//console.log('spinbox to changed');
			
		}
	},
	
	'template':_.template(
		'<div class="row"><div class="col-xs-4">'+_.template(CFTEMPLATES.numberSpinner1,{variable:'spinbox'})({name:'sbFrom'})+'</div>'+
		'<div class="col-xs-2"><span class="btn btn-default disabled"><span class="glyphicon glyphicon-resize-horizontal"></span> to</span></div>'+
		'<div class="col-xs-6">'+_.template(CFTEMPLATES.numberSpinner1,{variable:'spinbox'})({name:'sbTo'})+'</div>'+
		'<span class="help-block">filtering the results by column values between these numbers</span>'
	),
	
	'initialize':function(options) {
		this.$el.addClass('fuelux');
		this.$el.html(this.template);
		$('.spinbox',this.$el).spinbox(this.sbOptions);
		this.sbFrom = $('.spinbox.sbFrom',this.$el);
		this.sbTo = $('.spinbox.sbTo',this.$el);
	},
	
	'render':function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Select)
var VFilterWidgetTypeNumberSel = VFilterWidgetType.extend({
	'version':'1.0.2',
	'type':'select',
	
	'collectionInterface':Backbone.Collection.extend({
		'model':Backbone.Model.extend({
			'defaults':{
				'number':null
			}
		})
	}),
	
	/*
	Name		type	default value	description
	value 		number 	1 				Sets the initial spinbox value
	min 		number 	1 				Sets the minimum spinbox value
	max 		number 	999 			Sets the maximum spinbox value
	step 		number 	1 				Sets the increment by which the value in the spinbox will change each time the spinbox buttons are pressed
	hold 		boolean true 			If true, the spinbox will react to its buttons being pressed and held down
	speed 		string 	"medium" 		Assigns spinbox speed when buttons are held down. Options include "fast","medium","slow".
	disabled 	boolean false 			Creates a disables spinbox.
	units 		array 	[] 				Units that will be allowed to appear and be typed into the spinbox input along with the numeric value. 
										For example, setting units to a value of ['px', 'en', 'xx'] would allow px, en, and xx units to appear 
										within the spinbox input
	Events		changed.fu.spinbox
	Methods		
				destroy		Removes all functionality, jQuery data, and the markup from the DOM. Returns string of control markup.
				value		Sets or returns the spinner value
	*/
	'sb':null,
	'sbOptions':{'min':-100, 'max':100, 'step':.25},
	
	'addBtn':null,
	'listEl':null,
	
	'isValid':function() {
		return this.collection.length>0;
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Number Filter ('+this.type+') Error', 'One or more numbers must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		return this.isValid() ? ['is one of these: (',$.map(this.collection.models,function(md){return md.get('number');}),')'].join(''):false;
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.collection.map(function(mn){return mn.get('number');}),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		//expecting what getValue would return
		this.collection.reset(_.map(filterValue.value, function(number){ return {'number':number}; }));
	},
	
	'reset':function() {
		this.sb.spinbox('value',0);
		this.collection.reset();
	},
	
	'addNumber':function(numberModel) {
		$('span.badge',this.addBtn).html(this.collection.length);
		this.listEl.append(this.listTemplate(numberModel));
		if(this.collection.length===1) {
			$('button.dropdown-toggle',this.$el).removeClass('disabled');
		}
	},
	'removeNumber':function(numberModel) {
		$('span.badge',this.addBtn).html(this.collection.length);
		$('li[data-cid="'+numberModel.cid+'"]',this.listEl).remove();
		if(this.collection.length<1) {
			$('button.dropdown-toggle',this.$el).addClass('disabled');
		}
	},
	'resetCollection':function(newCol) {
		$('span.badge',this.addBtn).empty();
		this.listEl.empty();
		if(newCol && newCol.length) {//is an actual collection
			newCol.each(function(numberModel) {
				this.addNumber(numberModel);
			}, this);
			$('button.dropdown-toggle',this.$el).removeClass('disabled');
		} else {
			$('button.dropdown-toggle',this.$el).addClass('disabled');
		}
	},
	
	'events':{
		'click button.sbadd':function(e) {
			var n = this.sb.spinbox('value')*1;
			
			// only add number if it isn't in the collection
			if(this.collection.where({'number':n}).length<1) {
				this.collection.add({'number':n});
			}
		},
		'click ul.cf-select-widget-list li button.close':function(e) {
			this.collection.remove($(e.currentTarget).data('cid'));
		}
	},
	
	'className':'fuelux',
	'listTemplate':_.template([
			'<li class="list-group-item" data-cid="<%= nm.cid %>">',
				'<button class="close" data-cid="<%= nm.cid %>"><span class="glyphicon glyphicon-remove btn-sm"></span></button>',
				'<p class="list-group-item-heading"><%= nm.get("number") %></p>',
			'</li>'
		].join(''),
		{'variable':'nm'}
	),
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-4 col-md-5 col-sm-10 col-xs-8">',CFTEMPLATES.numberSpinner1,'</div>',
			'<div class="col-lg-4 col-md-6 col-sm-12 col-xs-8">',
				'<div class="btn-group">',
					'<button type="button" class="btn btn-default sbadd">Add <span class="badge">0</span></button>',
					'<button type="button" class="btn btn-default dropdown-toggle disabled" data-toggle="dropdown" aria-expanded="false">',
						'<span class="caret"></span>',
						'<span class="sr-only">Toggle Dropdown</span>',
					'</button>',
					'<ul class="dropdown-menu list-group cf-select-widget-list" role="menu"></ul>',
				'</div>',
			'</div>',
		'</div>',
		'<div class="row">',
			'<div class="col-xs-12">',
				'<span class="help-block">filtering the results by column values in this list</span>',
			'</div>',
		'</div>'
		].join(''),
		{variable:'spinbox'}
	),
	
	'initialize':function(options) {
		
		this.collection = new this.collectionInterface();
		this.collection.on({
			'add':this.addNumber,
			'reset':this.resetCollection,
			'remove':this.removeNumber
		}, this);
		
		// add ui
		this.$el.html(this.template({name:'sb'}));
		
		// initialize spinbox
		$('.spinbox',this.$el).spinbox( _.has(options,'config')?options.config : this.sbOptions);
		
		// assign class variables
		this.sb = $('.spinbox.sb',this.$el);
		this.addBtn = $('button.sbadd',this.$el);
		this.listEl = $('.dropdown-menu',this.$el);
	}
});


/**
 * Filter Widget Type Implementation Class for Date (Equals)
 * !!! SEND/RECEIVE ALL TIMESTAMPS AS UTC !!!
 */
var VFilterWidgetTypeDateEq = VFilterWidgetType.extend({
	'version':'1.0.7',
	'type':'equals',
	'dp':null,
	'dpConfig':{
		autoclose:true,
		'name':'dpeq',
		'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
	},
	
	'isValid':function() {
		var d = this.dp.datepicker('getUTCDate');
		return d && !isNaN(d.getTime());
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A date must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is equal to ' + moment.utc(this.dp.datepicker('getUTCDate')).format('M/D/YYYY');
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.dp.datepicker('getUTCDate').getTime(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// new way with moment
		if(filterValue.value) {
			this.dp.datepicker('setUTCDate', moment.utc(filterValue.value).toDate());
		}
	},
	
	'reset':function() {
		this.dp.datepicker('update',null);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-5 col-md-7 col-sm-12 col-xs-8">',
				'<div class="input-group date">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join(''), {variable:'datepicker'}),
	
	'initialize':function(options) {
		this.$el.html(this.template(this.dpConfig));
		$('input.date',this.$el).datepicker(this.dpConfig);
		this.dp = $('input.date', this.$el);
	}
});


// Filter Widget Type Implementation Class for Date (Before)
var VFilterWidgetTypeDateB4 = VFilterWidgetType.extend({
	'version':'1.0.4',
	'type':'before',
	
	'isValid':function() {
		var d = this.model.get('dp').datepicker('getUTCDate');
		return d && !isNaN(d.getTime());
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A date must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is before ' + moment.utc(this.model.get('dp').datepicker('getUTCDate')).format('M/D/YYYY');
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':moment.utc(this.dp.datepicker('getUTCDate')).valueOf(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// new way with moment
		if(filterValue.value) {
			this.model.get('dp').datepicker('setUTCDate', moment.utc(filterValue.value).toDate());
		}
	},
	
	'reset':function() {
		this.model.get('dp').datepicker('update', null);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-5 col-md-7 col-sm-12 col-xs-8">',
				'<div class="input-group">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'dp':null,
			'dpConfig':{
				'autoclose':true,
				'name':'dpb4',
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
			}
		});
		
		this.$el.html(this.template({}));
		
		$('input.date', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dp', $('input.date', this.$el));
	}
});

// Filter Widget Type Implementation Class for Date (After)
var VFilterWidgetTypeDateAfter = VFilterWidgetType.extend({
	'version':'1.0.4',
	'type':'after',
	
	'isValid':function() {
		var d = this.model.get('dp').datepicker('getUTCDate');
		return d && !isNaN(d.getTime());
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A date must be selected.');
		return false;
	},
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is after ' + moment.utc(this.model.get('dp').datepicker('getUTCDate')).format('M/D/YYYY');
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':moment.utc(this.model.get('dp').datepicker('getUTCDate')).valueOf(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// new way with moment
		if(filterValue.value) {
			this.model.get('dp').datepicker('setUTCDate', moment.utc(filterValue.value).toDate());
		}
	},
	
	'reset':function() {
		this.model.get('dp').datepicker('update', null);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-5 col-md-7 col-sm-12 col-xs-8">',
				'<div class="input-group">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'dp':null,
			'dpConfig':{
				'autoclose':true,
				'name':'dpafter',
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
			}
		});
		
		this.$el.html(this.template({}));
		
		$('input.date', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dp', $('input.date', this.$el));
	}
});

/**
 * Filter Widget Type Implementation Class for Date (Equals)
 * requires:
 * 		bootstrap-datepicker.js (https://github.com/eternicode/bootstrap-datepicker/)
 * 		moment.js (http://momentjs.com/)
 */
var VFilterWidgetTypeDateBtwn = VFilterWidgetType.extend({
	'version':'1.0.8',
	'type':'between',
	
	'isValid':function() {
		var dFrom = this.model.get('dpFrom').datepicker('getUTCDate'),
			dTo = this.model.get('dpTo').datepicker('getUTCDate'),
			uxfrom = _.isDate(dFrom) ? dFrom.getTime() : NaN,
			uxto = _.isDate(dTo) ? dTo.getTime() : NaN,
			areDatesCheck = !isNaN(uxfrom) && !isNaN(uxto),
			isChronologicalCheck = uxfrom <= uxto;
		return areDatesCheck && isChronologicalCheck;
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		this.trigger(	'notify',
						'danger',
						'Date Filter ('+this.type+') Error',
						'A "from" and "to" date must be selected and in chronological order.'
		);
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return [
				'is between ',
				moment.utc(this.model.get('dpFrom').datepicker('getUTCDate')).format('M/D/YYYY'),
				' and ',
				moment.utc(this.model.get('dpTo').datepicker('getUTCDate')).format('M/D/YYYY')
			].join('');
		} else {
			return false;
		}
	},
	
	'getValue':function() {		
		if(this.validate()) {
			return {
				'type':this.type,
				'fromDate':this.model.get('dpFrom').datepicker('getUTCDate').getTime(),
				'toDate':this.model.get('dpTo').datepicker('getUTCDate').getTime(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// from/toDate can be: 1) an ISO Date string, 2) a Timestamp integer, 3) a javascript Date object
		this.model.get('dpFrom').datepicker('setUTCDate', moment.utc(filterValue.fromDate).toDate());
		this.model.get('dpTo').datepicker('setUTCDate', moment.utc(filterValue.toDate).toDate());
	},
	
	'reset':function() {
		this.model.get('dpFrom').datepicker('update',null);
		this.model.get('dpTo').datepicker('update',null);
	},
	
	'template':_.template([
		'<div class="form-inline">',
			'<div class="form-group">',
				'<div class="input-group input-daterange">',
					'<input type="text" class="form-control date" value="" />',
					'<span  class="input-group-addon">to</span>',
					'<input type="text" class="form-control date" value="" />',
				'</div>',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		
		this.model = new Backbone.Model({
			'dpFrom':null,
			'dpTo':null,
			'dpConfig':{
				'autoclose':true,
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
			}
		});
		
		this.$el.html(this.template({}));
		$('div.input-daterange', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dpFrom', $('input:first-child', this.$el));
		this.model.set('dpTo', $('input:last-child', this.$el));
	}
});


// Filter Widget Type Implementation Class for Date (Select)
var VFilterWidgetTypeDateSel = VFilterWidgetType.extend({
	'version':'1.0.10',
	'type':'select',
	
	'isValid':function() {
		return this.collection.length>0;
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'One or more dates must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return [
				'is one of these: (',
				$.map(this.collection.models, function(md) {
					return moment.utc(md.get('date')).format('M/D/YYYY');
				}),
				')'
			].join('');
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.collection.toJSON(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		this.collection.reset(filterValue.value);
	},
	
	'reset':function() {
		//reset datepicker and list
		this.collection.reset();
		this.model.get('dp').datepicker('update',null);
	},
	
	'events':{
		'click button.dpadd':function(e) {
			var d = this.model.get('dp').datepicker('getUTCDate');
			
			// only add date if it isn't in the valueList array
			if(d && !isNaN(d.getTime()) && this.collection.where({'timestamp':d.getTime()}).length<1) {
				this.collection.add({'date':d, 'timestamp':d.getTime()});
			}
		},
		
		'click ul.cf-select-widget-list li button.close':function(e) {
			this.collection.remove($(e.currentTarget).data('cid'));
		}
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-4 col-md-5 col-sm-10 col-xs-8">',
				'<div class="input-group">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			,'</div>',
			'<div class="col-lg-4 col-md-6 col-sm-12 col-xs-8">',
				'<div class="btn-group">',
					'<button type="button" class="btn btn-default dpadd">Add <span class="badge"><%= collection.length %></span></button>',
					'<button type="button" class="btn btn-default dropdown-toggle<% if(collection.length<1) { %>disabled<% } %>"<% if(collection.length<1) { %> disabled="disabled"<% } %> data-toggle="dropdown" aria-expanded="false">',
						'<span class="caret"></span>',
						'<span class="sr-only">Toggle Dropdown</span>',
					'</button>',
					'<ul class="dropdown-menu list-group cf-select-widget-list" role="menu">',
						'<% collection.each(function(m, i) { %>',
						'<li class="list-group-item" data-cid="<%= m.cid %>">',
							'<button class="close" data-cid="<%= m.cid %>"><span class="glyphicon glyphicon-remove btn-sm"></span></button>',
							'<p class="list-group-item-heading"><%= moment.utc(m.get("date")).format("M/D/YYYY") %></p>',
						'</li>',
						'<% }) %>',
					'</ul>',
				'</div>',
			'</div>',
		'</div>',
		'<div class="row">',
			'<div class="col-xs-12">',
				'<span class="help-block">filtering the results by column values in this list</span>',
			'</div>',
		'</div>'
	].join(''), {'variable':'collection'}),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'dp':null,
			'dpConfig':{
				'name':'dpsel',
				'autoclose':true,
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
			},
			'addBtn':null,
			'listEl':null
		});
		
		this.collection = new Backbone.Collection();
		this.collection.on('add', this.render, this);
		this.collection.on('reset', this.render, this);
		this.collection.on('remove', this.render, this);
		
		this.render(null, this.collection);
	},
	
	'render':function(m, col) {
		this.$el.empty().append(this.template(this.collection));
		// initialize datepicker
		$('input.date', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dp', $('input.date', this.$el));
	}
});


// Filter Widget Type Implementation Class for Date (Equals)
var VFilterWidgetTypeDateCycle = VFilterWidgetType.extend({
	'version':'1.0.4',
	'type':'cycle',
	
	'isValid':function() {
		var d = this.model.get('dp').datepicker('getUTCDate');
		return d && !isNaN(d.getTime());
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A month and year must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			var d = this.model.get('dp').datepicker('getUTCDate');
			return [
				'for the ',
				 _.findWhere(this.model.get('cycle'), {'value':$('div.btn-group label.active input', this.$el).val()*1}).label, 
				 ' billing cycle of ',
				this.model.get('months')[d.getMonth()+1], 
				', ', 
				d.getFullYear()
			].join('');
		} else {
			return false;
		}
	},
	
	/**
	 * Returns:
	 * {
		 type:'cycle',
		 monthYear:{ date:<ISO 8601 string>, timestamp:<timestamp> },
		 cycle:<int>,
		 cycleMap:<array>,
		 description:<string>
	 * }
	 */
	'getValue':function() {
		if(this.validate()) {
			// pass along the cycle map object for server-side processing
			// the date will always be the first day of the month
			var d = moment.utc(this.model.get('dp').datepicker('getUTCDate')),
				cycle = $('div.btn-group label.active input', this.$el).val()*1;
			return {
				'type':this.type,
				'monthYear':{
					'date':this.model.get('dp').datepicker('getUTCDate'), 
					'timestamp':moment.utc(this.model.get('dp').datepicker('getUTCDate')).valueOf()
				},
				'cycle':cycle,
				'cycleMap':this.model.get('cycle'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		if(_.has(filterValue,'monthYear') && _.isDate(filterValue.monthYear.date)) {
			this.model.get('dp').datepicker('setUTCDate', filterValue.monthYear.date);
		} else {
			this.model.get('dp').datepicker('update', null);
		}
		if(_.has(filterValue,'cycle')) {
			$('div.btn-group label', this.$el).each(function(i, e) {
				var lbl = $(e),
					inpt = $('input',$(e));
				lbl.removeClass('active');
				inpt.removeAttr('checked');
				if((inpt.val()*1)==filterValue.cycle) {
					lbl.addClass('active');
					inpt.attr('checked','checked');
					
				}
			});
		}
	},
	
	'reset':function() {
		this.setValue({'cycle':1});
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-6 col-md-7 col-sm-12 col-xs-12">',
				'<div class="btn-group" data-toggle="buttons">',
					'<% for(var i in config.cycle) { %>',
						'<label class="btn btn-xs btn-primary<% if(i==0) { %> active<% } %>">',
							'<input type="radio" value="<%= config.cycle[i].value %>" <% if(i==0) { %> checked="checked"<% } %> /> ',
							'<%= config.cycle[i].label %>',
						'</label>',
					'<% } %>',
				'</div>',
			'</div>',
			'<div class="col-lg-6 col-md-5 col-sm-12 col-xs-12">',
				'<div class="input-group">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join(''), {variable:'config'}),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'months':[
				'January','February','March',
				'April','May','June',
				'July','August','September',
				'October','November','December'
			],
			// cycle is expected to be an array of date range objects within 1 month
			'cycle':[
				{'label':'1st-15th', 'value':1},
				{'label':'16th-End Of Month', 'value':2}
			],
			'dp':null,
			'dpConfig':{
				'autoclose':true,
				'minViewMode':1,
				'startView':1,
				'name':'dpcy',
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.month_year
			}
		});
		
		if(options) {
			console.log(options);
			for(var i in options) {
				this.model.set(i, options[i]);
			}
		}
		
		this.$el.html(this.template(this.model.toJSON()));
		$('input.date', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dp', $('input.date', this.$el));
	}
});


// Filter Widget Type Implementation Class for Date (Month)
var VFilterWidgetTypeDateM = VFilterWidgetType.extend({
	'version':'1.0.7',
	'type':'month',
	
	'isValid':function() {
		// a month and year is selected
		var retM = $('select', this.$el).val()*1
		return (retM>-1 && retM<12);
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A month must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return [
				'is in ', 
				moment({'month':$('select', this.$el).val()*1}).format('MMMM')
			].join('');
		} else {
			return false;
		}
	},
	
	/**
	 * Returns:
	 * { type:'month', month:<0-11>, description:<string> } 
	 */
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'month':$('select', this.$el).val()*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// filterValue = {month:<int>}
		if(filterValue.month) {
			$('select', this.$el).val(filterValue.month);
		}
	},
	
	'reset':function() {
		$('select', this.$el).val(0);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-8 col-md-8 col-sm-12 col-xs-8">',
				'<label class="control-label">Month: </label>',
				'<select class="form-control">',
				'<% for(var i in months) { %>',
					'<option value="<%= ((i*1)) %>"><%= months[i] %></option>',
				'<% } %>',
				'</select>',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'months':[
				'January', 'February', 'March', 'April',
				'May', 'June', 'July', 'August',
				'September', 'October', 'November', 'December' 
			]
		});
		
		// render this view's elements
		this.$el.html( this.template(this.model.toJSON()) );
	}
});

// Filter Widget Type Implementation Class for Date (Month Year)
var VFilterWidgetTypeDateMY = VFilterWidgetType.extend({
	'version':'1.0.8',
	'type':'monthyear',
	
	'isValid':function() {
		// a month and year is selected
		var retM = $('select', this.$el).val()*1,
			retY = this.model.get('dp').datepicker('getUTCDate');
		return ( (retM>-1) && (retY));
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A month and year must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return [
				'is in ', 
				moment({'month':$('select', this.$el).val()*1}).format('MMMM'),
				' of year ',
				moment.utc(this.model.get('dp').datepicker('getUTCDate')).format('YYYY')
			].join('');
		} else {
			return false;
		}
	},
	
	/**
	 * Returns:
	 * { type:'monthyear', month:<0-11>, start:<timestamp>, year:<int>, description:<string> }
	 */
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'month':$('select', this.$el).val()*1,
				'start':moment.utc(this.model.get('dp').datepicker('getUTCDate')).valueOf(),
				'year':moment.utc(this.model.get('dp').datepicker('getUTCDate')).year(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// filterValue = {month:<int>, year:<int>}
		if(filterValue.month && filterValue.year) {
			$('select', this.$el).val(filterValue.month);
			this.model.get('dp').datepicker('update', moment({'year':filterValue.year}).toDate());
		}
	},
	
	'reset':function() {
		$('select', this.$el).val(0);
		this.model.get('dp').datepicker('update',null);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-5 col-md-6 col-sm-12 col-xs-8">',
				'<label class="control-label">Month: </label>',
				'<select class="form-control">',
				'<% for(var i in months) { %>',
					'<option value="<%= ((i*1)) %>"><%= months[i] %></option>',
				'<% } %>',
				'</select>',
			'</div>',
			'<div class="col-lg-5 col-md-6 col-sm-12 col-xs-8">',
				'<label class="control-label">Year: </label>',
				'<div class="input-group date">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join('')),
	
	'events':{
		/* for testing
		'changeDate div.dpeq':function(e) {
			console.log(e);
			return false;
		}*/
	},
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'dp':null,
			'dpConfig':{
				'autoclose':true,
				'name':'dpmy',
				'format':'yyyy',
				'defaultViewDate':'year',
				'minViewMode':'years'
			},
			'months':[
				'January', 'February', 'March', 'April',
				'May', 'June', 'July', 'August',
				'September', 'October', 'November', 'December' 
			]
		});
		
		// render this view's elements
		this.$el.html( this.template({'datepicker':this.model.get('dpConfig'), 'months':this.model.get('months')}) );
		
		// create the datepicker
		$('input', this.$el).datepicker(this.model.get('dpConfig'));
		
		// set the model's dp property now that the datepicker has been created
		this.model.set('dp', $('input', this.$el));
	}
});

// Filter Widget Type Implementation Class for Date Year List (Equals)
var VFilterWidgetTypeDateYr = VFilterWidgetType.extend({
	'version':'1.0.4',
	'type':'year',
	
	'isValid':function() {
		var d = this.model.get('dp').datepicker('getUTCDate');
		return d && !isNaN(d.getTime());
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		this.trigger('notify', 'danger', 'Date Filter ('+this.type+') Error', 'A year must be selected.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			var d = moment.utc(this.model.get('dp').datepicker('getUTCDate'));
			return 'year is ' + d.year();
		} else {
			return false;
		}
	},
	
	/**
	 * Returns:
	 * { type:'year', value:<int>, start:<timestamp>, description:<int> }
	 */
	'getValue':function() {
		if(this.validate()) {
			var d = this.model.get('dp').datepicker('getUTCDate');
			return {
				'type':this.type,
				'value':moment.utc(d).year(),
				'start':moment.utc(d).valueOf(),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		// date should be a date
		if(_.isFinite(filterValue.value)) {
			this.model.get('dp').datepicker('setUTCDate', new Date(filterValue.value, 1, 1));
		}
	},
	
	'reset':function() {
		this.model.get('dp').datepicker('update',null);
	},
	
	'template':_.template([
		'<div class="row">',
			'<div class="col-lg-5 col-md-7 col-sm-12 col-xs-8">',
				'<div class="input-group date">',
					'<input type="text" class="form-control date" value="" />',
					'<span class="input-group-addon">',
						'<span class="glyphicon glyphicon-calendar"></span>',
					'</span>',
				'</div>',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'dp':null,
			'dpConfig':{
				autoclose:true,
				'name':'dpyr',
				'minViewMode':'years',
				'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.year
			}
		});
		
		this.$el.html(this.template({}));
		$('input.date', this.$el).datepicker(this.model.get('dpConfig'));
		this.model.set('dp', $('input.date', this.$el));
	}
});

// Filter Widget Type Implementation Class for Number (Select)
var VFilterWidgetTypeBoolEq = VFilterWidgetType.extend({
	'version':'1.0.3',
	'type':'equals',
	
	'defaultConfig':{
		'value':true,
		'trueLabel':'Active',
		'falseLabel':'Inactive',
		'convertNumeric':false
	},
	
	'model':null,
	
	'isValid':function() {
		return true;//this ui should alway return a value
	},
	'validate':function() {
		return true;
	},
	'getValueDescription':function() {
		return ('is '+(this.model.get('value')?this.model.get('trueLabel'):this.model.get('falseLabel')));
	},
	'getValue':function() {
		// TODO use convertNumeric config option
		if(this.validate()) {
			return {
				'type':this.type,
				'value': this.model.get('convertNumeric')? (this.model.get('value')?1:0) : this.model.get('value'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	'setValue':function(filterValue) {
		this.model.set('value', filterValue.value?true:false);
		//also change UI
		$('.btn-group label',this.$el).first().toggleClass('active', this.model.get('value'));
		$('.btn-group label',this.$el).last().toggleClass('active', !this.model.get('value'));
	},
	'reset':function() {
		this.setValue({'value':true})
	},
	
	'events':{
		'click .btn-group label':function(e) {
			this.model.set('value', $(e.currentTarget).hasClass('cf-fw-type-bool-true'));
		}
	},
	
	'template':_.template([
		'<div class="btn-group" data-toggle="buttons">',
			'<label class="btn btn-primary cf-fw-type-bool-true<%= value?" active":"" %>">',
				'<input type="radio" name="cf-fw-type-bool" id="cf-fw-type-bool-true"<%= value?" checked":"" %>> <%= trueLabel %>',
			'</label>',
			'<label class="btn btn-primary<%= value?"":" active" %>">',
				'<input type="radio" name="cf-fw-type-bool" id="cf-fw-type-bool-false" <%= value?"":" checked" %>> <%= falseLabel %>',
			'</label>',
		'</div>'
		].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model();
		//options that affect UI
		this.model.set('trueLabel', (_.has(options,'trueLabel') && _.isString(options.trueLabel)) ? options.trueLabel : this.defaultConfig.trueLabel);
		this.model.set('falseLabel',(_.has(options,'falseLabel') && _.isString(options.falseLabel))?options.falseLabel:this.defaultConfig.falseLabel);
		this.model.set('convertNumeric',_.has(options,'convertNumeric') ? (options.convertNumeric?true:false) : this.defaultConfig.convertNumeric);
		
		this.$el.html(this.template(this.defaultConfig));
		
		//default value is true unless config value passed
		this.model.set('value',(_.has(options,'value') && !options.value)?false:this.defaultConfig.value);
	},
	'render':function() {
		return this;
	}
});

// Filter Widget Type Implementation Class for Enum (Select)
var VFilterWidgetTypeEnumIn = VFilterWidgetType.extend({
	'version':'1.0.3',
	'type':'in',
	
	'isValid':function() {
		return $.map($('.dropdown-menu input:checked',this.$el), function(e,i){ return e.value*1; }).length>0;
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Enum Filter ('+this.type+') Error', 'Enum checklist cannot be empty.');
		return false;
	},
	
	'getValueDescription':function() {
		if(this.isValid()) {
			return 'is one of these : (' + $.map($('.dropdown-menu input:checked',this.$el), function(e,i){ return e.value*1; }).join(',') + ')';
		} else {
			return false;
		}
	},
	
	'getValue':function() {
		if(this.validate()) {
			var checkMap = [],
				desc_1 = 'is one of these : (',
				checkNames = [],
				desc_2 = ')';
			$('.dropdown-menu label',this.$el).each(function(i,e) {
				var chkInpt = $('input',$(e))[0],
					span = $('span',$(e));
				if(chkInpt.checked) {
					checkMap.push({'code':chkInpt.value*1, 'name':span.html()});
					checkNames.push(span.html());
				}
			});
			
			return {
				'type':this.type,
				'table':this.collection.findWhere({'column':this.model.get('currentColumn')}).get('table'),
				'column':this.model.get('currentColumn'),
				'value':checkMap,
				'description':[desc_1,checkNames.join(','),desc_2].join('')
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		//set the checkboxes to the values in valueList
		var vl = filterValue.value,
			c = this.collection;
		$('.dropdown-menu input',this.$el).each(function(i,e) {
			for(var i in vl) {
				var foundMatch = ( (e.value*1)===vl[i].code );
				e.checked = foundMatch;
				if (foundMatch) {
					break;
				} 
			}
		});
	},
	
	'reset':function() {
		//reset happens just before setValue
		//$('.dropdown-menu input',this.$el).each(function(i,e) {
			//e.checked = false;
		//});
		//this.$el.empty();
	},
	
	'config':function(dataCol) {
		// dataCol will be a string
		if(dataCol!==this.model.get('currentColumn')) {
			this.model.set('currentColumn', dataCol);
			this.$el.html(this.template(this.collection.findWhere({'column':dataCol}).attributes));
		}
	},
	
	'events':{
		'click .dropdown-menu input, .dropdown-menu label':function(e) {
			e.stopPropagation();
		},
		'change .dropdown-menu input':function(e) {
			e.stopPropagation();
			return false;
		},
	},
	
	//className:'dropdown',
	// data.enums = array of {code, column, <label key>}
	// data.column = string name of column, used for grouping
	// data.labelKey = 
	'template':_.template([
		'<div class="keep-open">',
			'<button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown">Check 1 or more <span class="caret"></span></button>',
			'<ul class="dropdown-menu cf-enum-dropdown-list" role="menu">',
				'<% for(var i in data.enums) { %>',
					'<li>',
						'<div class="checkbox">',
							'<label>',
								'<input type="checkbox" value="<%= data.enums[i].id %>" data-column="<%= data.column %>" />',
								'<span class="text-capialize"><%= data.enums[i][data.labelKey] %></span>',
							'</label>',
						'</div>',
					'</li>',
				'<% } %>',
			'</ul>',
		'</button>',
		'</div>'
	].join(''),{variable:'data'}),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'currentColumn':null
		});
		
		//split enums into groups by options.enums[i].name
		// check options.enums array of keys named 'id', a mapped copy of the array will 
		// need to be configured so that the 'id' keys are renamed to 'code' (mimicing java Enum class)
		var enumData;
		if(_.has(options,'enums') && _.isArray(options.enums) && options.enums.length) {
			// incoming meta data
			// table: string - e.table - the main data table (not the source table of the enum set)
			// column: string - e.data - the column name in the main data table
			// enums: array - e.cfenumsource - the data array that populates each grouped enum set
			// labelKey: string - e.cfenumlabelkey - the property key used to retrieve the iterated enum value label
			this.collection = new Backbone.Collection(
				$.map(options.enums, function(e,i){
					return {
						'table':e.table,
						'column':e.data,
						'enums':e.cfenumsource,
						'labelKey':e.cfenumlabelkey
					};
				})
			);
			this.model.set('currentColumn', this.collection.at(0).get('column'));
			this.$el.html(this.template(this.collection.at(0).toJSON()));
		} else {
			// shouldn't we error out?
			this.$el.html(this.template({'enums':[]}));
		}
	}
});


/*
An input for a column with a very large set of (known) values (too many to put into the page)
the primary input is a typeahead
[typeahead] -scrollable -custom templates 1)local, 2)prefetch 3)remote
*/
var VFilterWidgetTypeBiglistEq = VFilterWidgetType.extend({
	'version':'1.0.3',
	'type':'equals',
	
	// the text input used for typeahead
	'taInput':null,
	
	'isValid':function() {
		return this.model.get('currentData')!=null;
	},
	
	'validate':function() {
		if(this.isValid()) {
			return true;
		}
		
		this.trigger('notify', 'danger', 'Big List Filter ('+this.type+') Error', 'Biglist input cannot be empty.');
		return false;
	},
	
	'getDisplayValue':function() {
		if(this.isValid()) {
			if(typeof(this.model.get('displayKey'))==='function') {
				return this.model.get('displayKey')(this.model.get('currentData'));
			} else {
				return this.model.get('currentData')[this.model.get('displayKey')];
			}
		} else {
			return '';
		}
	},
	
	'getValueDescription':function() {//is this public?
		return this.isValid() ? ('is '+this.getDisplayValue()) : false;
	},
	
	'getValue':function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'table':this.model.get('table'),
				'column':this.model.get('currentColumn'),
				'displayKey':this.model.get('displayKey'),
				'valueKey':this.model.get('valueKey'),
				'value':this.model.get('currentData'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	'setValue':function(filterValue) {
		/*
		filterValue:
			value:<some kind of object that would come from one of the datasets>
			column:string -- should match a 'dataColumn' attribute in one of the collection models
		*/
		// TODO multi-column type
		//console.log(filterValue);
		var dataset = this.collection.findWhere({'dataColumn':filterValue.column});
		if(dataset && _.has(filterValue,'column') && _.has(filterValue,'value')) {
			this.model.set('table', filterValue.table);
			this.model.set('currentColumn', filterValue.column);
			this.model.set('currentData', filterValue.value);
			
			this.model.set('displayKey', dataset.attributes.displayKey);
			this.model.set('valueKey', dataset.attributes.valueKey);
			$('input:text',this.$el).typeahead('val', this.getDisplayValue());
		}
	},
	'reset':function() {
		//reset happens just before setValue
		this.model.clear();
		this.taInput.val(null);
	},
	
	// called from the filter factory
	'updateMultiColumns':function(columnsArray) {
		this.model.set('currentColumn', columnsArray);
	},
	
	//called from the filter factory
	'config':function(dataCol) {
		//console.log(dataCol);
		if(_.isArray(dataCol)) {
			// for a "common value" filter type
			var firstDataset = this.collection.findWhere({'column':dataCol[0]}),
				sameDataset = this.collection.where({'table':firstDataset.get('table')});
			
			this.model.set('table', $.map(sameDataset, function(e,i) {
				return e.get('column');
			}));
			this.model.set('currentColumn', dataCol);
			// displayKey and valueKey should be the same for items with the same dataset (source table)
			this.model.set('displayKey', firstDataset.get('displayKey'));
			this.model.set('valueKey', firstDataset.get('valueKey'));
			this.model.set('currentData', null);
		} else {
			//console.log(this.collection.toJSON());
			var newData = this.collection.findWhere({'column':dataCol});
			if(dataCol!==this.model.get('currentColumn')) {
				this.model.set('table', newData.get('table'));
				this.model.set('currentColumn', newData.get('column'));
				this.model.set('displayKey', newData.get('displayKey'));
				this.model.set('valueKey', newData.get('valueKey'));
				this.model.set('currentData', null);
				
				// destroy current typeahead and rebuild using new dataset
				this.taInput.typeahead('val',null);
				this.taInput.typeahead('destroy');
				newData.get('dataset').initialize();
				this.taInput.typeahead(
					{'highlight':false, 'hint':false, 'minLength':3},
					{
						'name':newData.get('dataColumn'),
						'displayKey':newData.get('displayKey'),
						'source':newData.get('dataset').ttAdapter()
					}
				);
			}
		}
	},
	
	
	'events':{
		'typeahead:selected input.typeahead':function(jqEvent, suggestion, datasetName) {
			//this.model.set('currentColumn', datasetName);
			this.model.set('currentData', suggestion);
			var dataset = this.collection.findWhere({'dataColumn':datasetName});
			if(dataset) {
				this.model.set('displayKey', dataset.attributes.displayKey);
				this.model.set('valueKey', dataset.attributes.valueKey);
			}
		}
	},
	
	'template':_.template([
		'<div class="form-group row">',
			'<div class="col-lg-col-xs-12">',
			'<input type="text" data-provide="typeahead" autocomplete="off" class="form-control typeahead" value="" />',
			'</div>',
		'</div>'
	].join('')),
	
	'initialize':function(options) {
		this.model = new Backbone.Model({
			'table':null,
			'currentData':null,
			'currentColumn':null,
			'displayKey':null,
			'valueKey':null
		});
		
		if(_.has(options,'datasets') && _.isArray(options.datasets) && options.datasets.length) {
			//split datasets into groups by options.datasets[i].name (column name)
			this.collection = new Backbone.Collection(
				$.map(options.datasets, function(e,i){
					return {
						'table':e.table,
						'column':e.name,			// 'name' property from data table column meta data (will not be a sub-field identifier)
						'dataColumn':e.dataColumn,	// 'data' property from data table column meta data (will not be a sub-field identifier)
						'dataset':e.datasource,
						'displayKey':e.displayKey,
						'valueKey':e.valueKey
					};
				})
			);
			
			// use the first data set
			var defaultDataset = this.collection.at(0);
			this.model.set('table', defaultDataset.get('table'));
			this.model.set('currentColumn', defaultDataset.get('column'));
			this.model.set('displayKey', defaultDataset.get('displayKey'));
			this.model.set('valueKey', defaultDataset.get('valueKey'));
			this.model.set('currentData', null);
			
			// remember to initialize the bloodhound search engine
			defaultDataset.get('dataset').initialize();
			
			this.$el.html(this.template());
			
			// we may need to have an input for each different dataset (employees, clients, etc. etc.)
			this.taInput = $('input.typeahead',this.$el);
			this.taInput.typeahead(
				{'highlight':false, 'hint':false, 'minLength':3},
				{
					'name':defaultDataset.get('dataColumn'),
					'displayKey':defaultDataset.get('displayKey'),
					'source':defaultDataset.get('dataset').ttAdapter()
				}
			);
		} else {
			// shouldn't we error out at this point?
			this.$el.html(this.template());
			this.taInput = $('input.typeahead', this.$el);
		}
	},
	'render':function() {
		return this;
	}
});

