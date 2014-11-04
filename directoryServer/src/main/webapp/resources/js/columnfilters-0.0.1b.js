// Dev-Only: templates and template global variable will be included in one file
var CFTEMPLATES = {
	DATEPICKER_DATE_FORMATS:{
		'en_us':'mm/dd/yyyy',
		'en_gb':'dd-mm-yyyy',
		'zh_cn':'yyyy.mm.dd',
		'month_year':'MM, yyyy'
	},
	DATEPICKER_VIEW_MODES:{
		'DAYS':0,
		'MONTHS':1,
		'YEARS':2
	},
	DATEPICKER_WEEK_START_DAYS:{
		'SUNDAY':0,
		'MONDAY':1,
		'TUESDAY':2,
		'WEDNESDAY':3,
		'THURSDAY':4,
		'FRIDAY':5,
		'SATURDAY':6
	}
	
	//[[SCRIPT_INSERT]]//
};

CFTEMPLATES.commonValueController = '<button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown">Select Columns <span class="caret"></span></button><ul class="dropdown-menu dropdown-menu-sm" role="menu"><% for(var i in data.columns) { %><li class="cf-cvdd-active"><button type="button" class="btn btn-block text-capitalize " data-name="<%= data.columns[i].name %>" data-type="<%= data.columns[i].type %>"><span class="glyphicon glyphicon-ok pull-left hidden"></span> <%= data.columns[i].label %></button></li><% } %></ul>';

// variable { panelheading.filterFactory (View.el), panelheading.filterColumns (Array) }
CFTEMPLATES.dataFiltersPanelContent = '<div class="panel-heading well-sm">'+
	'<div class="row">'+
		'<div class="col-lg-5 col-md-6 col-sm-7 col-xs-8 text-nowrap">'+
			
			// FILTER SELECTION TYPE
			'<div class="btn-group cf-data-filter-type-selection" data-toggle="buttons">'+
				'<label class="btn btn-info active"><input type="radio" name="options" id="cf-data-type-option-default" value="0" checked="checked" /> Data Filters</label>'+
				'<label class="btn btn-info"><input type="radio" name="options" id="cf-data-type-option-common-value" value="1" /> Common Value</label>'+
			'</div>'+
			
			// COMMON VALUE FILTER SELECTION TYPE
			'<div class="cf-common-value-controller-replace"></div>'+
			
			// ADD FILTER/COLUMN SELECT DROP DOWN
			'<div class="btn-group">'+
				'<button type="button" class="btn btn-success btn-sm cf-edit-filter-button">Save</button>'+
				'<button type="button" class="btn btn-default btn-sm cf-cancel-edit-filter-button">Cancel</button>'+
			'</div>'+
			
			// DEFAULT FILTER SELECTION TYPE
			'<div class="btn-group cf-add-change-filter-group-button">'+
				'<button type="button" class="btn btn-default btn-xs cf-add-filter-button">Add Filter</button>'+
				'<button type="button" data-toggle="dropdown" class="btn btn-default btn-xs dropdown-toggle">'+
					'<span class="caret"></span>'+
					'<span class="sr-only">Toggle Dropdown</span>'+
				'</button>'+
				'<ul role="menu" class="dropdown-menu cf-columns-select-dd">'+
				'<% for(var i in panelheading.filterColumns) { %>'+
					'<% if(!panelheading.filterColumns[i].cfexclude) { %>'+
						'<%= _.template(CFTEMPLATES.filterOptionListItem,{variable:\'columnData\'})(panelheading.filterColumns[i]) %>'+
					'<% } %>'+
				'<% } %>'+
				'</ul>'+
			'</div>'+
			
		'</div>'+
		'<div class="col-lg-7 col-md-6 col-sm-5 cf-filter-factory-container-row"></div>'+
	'</div>'+
'</div>';
//'<%= $.map(panelheading.filterColumns, function(c,i) { return _.template(CFTEMPLATES.filterOptionListItem,{variable:\'columnData\'})(c); }).join("") %>'+

// 
CFTEMPLATES.filterOptionListItem = [
	'<li>',
		'<a href="#" data-type="<%= columnData.type %>" data-name="<%= columnData.name %>"><%= columnData.label %></a>',
	'</li>'
].join('');

// 
CFTEMPLATES.dataFiltersControlBody = '<div class="row">'+
	'<div class="col-xs-4">'+
			'<ul class="nav nav-pills nav-stacked" role="tablist"></ul>'+
	'</div>'+
	'<div class="col-xs-8">'+
		'<div class="tab-content"></div>'+
	'</div>'+
'</div>';

// controller.filterCategories
CFTEMPLATES.dataFiltersControlFooter = '<nav class="navbar navbar-default cf-datafilters-controller-footer" role="navigation">'+
	'	<div class="container-fluid">'+
	'		<div class="collapse navbar-collapse">'+	
	'<% if(controller.filterCategories.length){ print(\'<ul class=\"nav navbar-nav navbar-right\"><li class=\"btn btn-xs cf-delete-filter-list disabled\" title=\"delete\"><a href=\"#\" class=\" btn btn-xs\"><span class=\"glyphicon glyphicon-remove\"></span> </a></li><li class=\"dropup btn btn-xs cf-save-filter-list disabled\" title=\"save\"><a href=\"#\" class=\"dropdown-toggle btn btn-xs\" data-toggle=\"dropdown\"><span class=\"glyphicon glyphicon-floppy-disk\"></span><span class=\"caret\"></span></a><ul class=\"dropdown-menu\" role=\"menu\"></ul></li></ul>\'); } %>'+
	'		</div>'+
	'	</div>'+
'</nav>'+
'<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">'+
	'<div class="modal-dialog modal-lg">'+
		'<div class="modal-content">'+
			'<div class="modal-header">'+
				'<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Cancel</span></button>'+
				'<h4 class="modal-title" id="cf-modal-title">Modal title</h4>'+
			'</div>'+
			'<div class="modal-body"></div>'+
			'<div class="modal-footer">'+
				'<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>'+
				'<button type="button" class="btn btn-primary">Save</button>'+
			'</div>'+
		'</div>'+
	'</div>'+
'</div>';

CFTEMPLATES.filterCategoryMenu = '<ul class="nav navbar-nav" data-category-name="<%= filterCategory.name %>">'+
	'<li class="dropup btn btn-xs disabled">'+
		'<a href="#" class="dropdown-toggle btn btn-xs" data-toggle="dropdown"><%= filterCategory.name %> '+
			'<span class="badge"></span>'+
			'<span class="caret"></span>'+
		'</a>'+
		'<ul class="dropdown-menu" role="menu"></ul>'+
	'</li>'+
'</ul>';

// filterCategory: {name:<string>, glyph:<string>}
CFTEMPLATES.filterCategorySaveItem = '<li data-save-type="<%= filterCategory.name %>">'+
	'<a href="#">'+
		'<span class="badge pull-right">'+
			'<span class="glyphicon <%= filterCategory.glyph %>"></span>'+
		'</span> to <% print(filterCategory.name[0].toUpperCase()+filterCategory.name.substring(1)) %>'+
	'</a>'+
'</li>';

/*
<form class="form-horizontal" role="form">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
    <div class="col-sm-10">
      <input type="email" class="form-control" id="inputEmail3" placeholder="Email">
    </div>
  </div>
  <div class="form-group">
    <label for="inputPassword3" class="col-sm-2 control-label">Password</label>
    <div class="col-sm-10">
      <input type="password" class="form-control" id="inputPassword3" placeholder="Password">
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <div class="checkbox">
        <label>
          <input type="checkbox"> Remember me
        </label>
      </div>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-default">Sign in</button>
    </div>
  </div>
</form>
*/
CFTEMPLATES.saveFilterSetModalForm = '<form class="form-horizontal" role="form">'+
	'<div class="form-group">'+
		'<label for="cfFilterSetSaveName" class="col-sm-2 control-label">Name</label>'+
		'<div class="col-sm-10">'+
			'<input type="text" class="form-control" id="cfFilterSetSaveName" placeholder="Name for this set of filters" autocomplete="off">'+
		'</div>'+
	'</div>'+
	'<div class="form-group">'+
		'<label for="cfFilterSetSaveDescription" class="col-sm-2 control-label">Description</label>'+
		'<div class="col-sm-10">'+
			'<textarea class="form-control" rows="3" id="cfFilterSetSaveDescription" autocomplete="off"></textarea>'+
		'</div>'+
	'</div>'+
'</form>';

CFTEMPLATES.datepicker4 = '<div class="input-group<% _.isString(datepicker.name)?print(" "+datepicker.name):"" %>">'+
	'  <input type="text" class="form-control" size="16" value="" readonly />'+
	'  <span class="input-group-addon add-on">to</span>'+
	'  <input type="text" class="form-control" size="16" value="" readonly />'+
	
'</div>';

CFTEMPLATES.datepicker3 = '<div class="input-group date<% _.isString(datepicker.name)?print(" "+datepicker.name):"" %>">'+
	//'<% print(_.has(datepicker,"date")?" data-date=\"datepicker.date\"":"")'+
	//'<% print(_.has(datepicker,"format")?" data-date-format=\"datepicker.format\"":"data-date-format=\"mm/dd/yyyy\"") %>'+
	//'<% print(_.has(datepicker,"viewMode")?" data-date-viewmode=\"+datepicker.viewMode+\"":"") %>'+
	//'<% print(_.has(datepicker,"minViewMode")?" data-date-minviewmode=\"+datepicker.minViewMode+\"":"") %>>'+
	'  <input type="text" class="form-control date" size="16" value="" readonly />'+
	'  <span class="input-group-addon btn btn-default"><span class="glyphicon glyphicon-calendar"></span></span>'+
'</div>';

/*
<div class="row">
	<div class="col-md-3"><%= fromDatepicker %></div>
	<div class="col-md-1"><span class="glyphicon glyphicon-calendar"></span></div>
	<div><%= toDatepicker %></div>
</div>
CFTEMPLATES.datebetweenFilterWidgetType = '<div class="row">'+
'<div class="col-md-6" title="from"><%= fromDatepicker %></div>'+
'<div class="col-md-6" title="to"><%= toDatepicker %></div>'+
'</div>';
*/
CFTEMPLATES.datebetweenFilterWidgetType = '<div class="row">'+
'<div class="col-md-12" title="from"><%= fromDatepicker %></div>'+
'</div>';


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

var MColumnFilter = Backbone.Model.extend({
	defaults:{
		'description':'',
		'value':{'type':'text',value:null}
	}
});


var CColumnFilters = Backbone.Collection.extend({
	model:MColumnFilter
});


var MDataColumnFilter = Backbone.Model.extend({
	
	name:null,
	column:null,
	type:'text',
	filters:null
	
});

// Data Column Filter Collection
var CDataColumnFilters = Backbone.Collection.extend({
	model:MDataColumnFilter
});


/* Data Column Filters Container Model
 * this model can be thought of as a named group of all the filters the user wants saved 
 * for instance, an instance of this model could contain filters that limit the results
 * of a automobile database table to only electric cars made in California 
*/
var MDataFilter = Backbone.Model.extend({
	/*
	when the collection pulls data down it will be in this format:
	[]{
		name:<string> the descriptive name
		category:<string> key for separating data filters
		collection:[]{
			name:<string> the descriptive name for labels
			column:<string> the table column name used in the query
			type:<string> the type of data
			filters:collection[] {
				description:<string>
				value:<custom>
		}
	}
	*/
	
	initialize:function(options) {
		
	}
});


// Collection for the DataFiltersContainer class
var CDataFilters = Backbone.Collection.extend({
	model:MDataFilter
});

var VFilterWidgetType = Backbone.View.extend({
	type:'equals',//abstract
	visible:false,
	active:false,
	
	
	// abstract functions (must override)
	
	// if you just want to know if the widget inputs are valid for returning value(s)
	isValid:function() {},
	
	// calling this function will cause the widget to check that it can return values from its inputs
	validate:function() {},
	
	// returns a human-readable description of the filter input values
	getValueDescription:function() {},
	
	// returns an object representing the filter values and properties if valid, otherwise false
	getValue:function() {},
	
	// will set the inputs to the values given
	setValue:function(filterValue) {},
	
	//
	//load:function(data) {},
	
	// restores the filter widget back to its initial state
	reset:function() {},
	
	// default class functions, can override, but it's not neccessary to do so
	show:function() {
		this.visible = true;
		this.active = true;
		this.$el.show();
	},
	hide:function() {
		this.visible = false;
		this.active = false;
		this.$el.hide();
	},
	enable:function() {
		this.$el[0].disabled = false;
	},
	disable:function() {
		this.$el[0].disabled = true;
	},
	
	// default view properties/functions
	tagName:'fieldset',
	className:'cf-widget-type',
	render:function() { return this; }
});


// DataColumnFilterWidget Class
// collection: a collection of VFilterWidgetType (extended to an instance)
var VDataColumnFilterWidget = Backbone.View.extend({
	type:'text',
	visible:false,
	active:false,
	
	activeType:function() {
		return this.collection.findWhere({active:true});
	},
	getSubType:function(subType) {
		return this.collection.findWhere({'type':subType});
	},
	getFilterValue:function() {
		var at = this.activeType();
		if(at) {
			return at.getValue();
		} else {
			return false;
		}
	},
	setFilterValue:function(filterValue) {
		var fwt = this.collection.findWhere({'type':filterValue.type});
		if(fwt) {
			fwt.attributes.setValue(filterValue);
		}
	},
	getLabel:function() {
		return $('div.cf-widget-type-label',this.$el).html();
	},
	setLabel:function(label) {
		$('div.cf-widget-type-label',this.$el).html(label);
	},
	
	changeSubType:function(subType) {
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
	
	show:function() {
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
	hide:function() {
		this.visible = false;
		this.active = false;
		this.$el.hide();
	},
	enable:function() {
		var ddbtn = $('button.dropdown-toggle',this.$el);
		if(ddbtn) {
			ddbtn[0].disabled = false;
		}
		var at = this.activeType();
		if(at) {
			at.attributes.enable();
		}
	},
	disable:function() {
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
	reset:function() {
		this.collection.each(function(filterWidget) {
			filterWidget.attributes.reset();
		});
	},
	
	tagName:'div',
	className:'cf-filter-widget',
	events:{
		// triggered when the type dropdown item is clicked
		'click ul.dropdown-menu li a':function(e) {
			this.changeSubType($(e.currentTarget).html());
		}
	},
	initialize:function(options) {
		if(options.hasOwnProperty('type')) {
			this.type = options.type;
		}
		//should be passed in: type, collection
		this.$el.addClass('cf-filter-widget-'+this.type);
		
		//build selector drop down
		var typeSelectorDropdown = $(document.createElement('ul')).attr({'role':'menu'}).addClass('dropdown-menu'),
			typeSelector = $(document.createElement('div')).addClass('cf-widget-type-selector btn-group pull-left').append(
				$(document.createElement('div')).addClass('cf-widget-type-label pull-left'),
				$(document.createElement('button')).attr({'type':'button','data-toggle':'dropdown'})
												   .addClass('btn btn-default btn-xs dropdown-toggle')
												   .append('<span class="cf-widget-type-selector-btn-title"></span> <span class="caret"></span>'),
				typeSelectorDropdown
		),
			typesContainer = $(document.createElement('div')).addClass('cf-widget-types-container pull-left');
		if(options.hasOwnProperty('collection')) {
			$('span.cf-widget-type-selector-btn-title',typeSelector).html(options.collection.at(0).attributes.type);
			var dsp = this.dispatcher;
			options.collection.each(function(widgetType) {
				widgetType.attributes.hide();
				typeSelectorDropdown.append(
					$(document.createElement('li')).append($(document.createElement('a')).attr({'href':'#'}).html(widgetType.attributes.type))
				);
				typesContainer.append(widgetType.attributes.el);
			});
			//show the first widget type
			options.collection.at(0).attributes.active = true;
			options.collection.at(0).attributes.show();
		}
		this.$el.append([typeSelector,typesContainer]);
	},
	render:function() { return this; }
});


// DataFilterFactory Class
// collection: a collection of VDataColumnFilterWidget objects
var VDataFilterFactory = Backbone.View.extend({
	types:[],
	activeColumn:null,
	
	savedState:null,
	saveState:function() {
		var af = this.activeFilter();
		if(af) {
			var fw = af.activeType();
			//console.log(af);
			//console.log(fw);
			this.savedState = {
				'dataCol':fw.attributes.currentColumn,
				'type':af.type,
				'label':af.getLabel(),
				'subtype':fw.attributes.type
			};
		} else {
			this.savedState = null;
		}
	},
	restoreState:function() {
		if(this.savedState) {
			//console.log(this.savedState);
			this.load(this.savedState.dataCol,this.savedState.type,this.savedState.label,this.savedState.subtype);
		} else {
			//check if there is an active filter; hide it if so
			var af = this.activeFilter();
			if(af) {
				af.hide();
			}
		}
	},
	
	activeFilter:function(){
		//return any active && visible filter widgets (should only be 1)
		var af = this.collection.findWhere({'active':true,'visible':true});
		return af?af.attributes:false;
	},
	
	getFilterValue:function() {
		return this.activeFilter().activeType().attributes.getValue();
	},
	
	setFilterValue:function(filter) {
		//first we have to find the current filter widget
		var fw = this.collection.findWhere({'type':filter.type});
		if(fw) {
			fw.attributes.reset();
			fw.attributes.setFilterValue(filter.filterValue);
		}
		return this;
	},
	
	updateFilterLabel:function(newLabel) {
		if(_.isString(newLabel)) {
			var af = this.activeFilter();
			if(af) {
				af.setLabel(newLabel);
			}
		}
	},
	
	show:function() {
		var af = this.activeFilter();
		if(af){
			af.show();
		}
		return this;
	},
	hide:function() {
		var af = this.activeFilter();
		if(af){
			af.hide();
		}
		return this;
	},
	
	enable:function() {
		//enable the active filter
		var af = this.activeFilter();
		if(af){
			af.enable();
		}
		return this;
	},
	disable:function() {
		//disable the active filter
		var af = this.activeFilter();
		if(af) {
			af.disable();
		}
		return this;
	},
	
	reset:function(resetAll) {
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
	load:function(dataCol, dataType, dataLabel, subType) {
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
			}
			
			
			//show the requested filter widget
			reqfw.attributes.show();
			
			if(_.isString(subType)) {
				reqfw.attributes.changeSubType(subType);
			}
		}
		return this;
	},
	
	
	tagName:'div',
	className:'cf-filter-factory',
	initialize:function(options) {
		
		if(options.hasOwnProperty('collection')) {
			var ffEl = this.$el,
				ffTypes = this.types;
			options.collection.each(function(filterWidget) {
				filterWidget.attributes.hide();
				ffEl.append(filterWidget.attributes.el);
				ffTypes.push(filterWidget.attributes.type);
			});
			
			if(options.hasOwnProperty('showOnInit') && options.showOnInit) {
				options.collection.at(0).attributes.show();
			}
		}
	},
	render:function() {
		return this;
	}
});


// Data Filters Container Controller
var VDataFiltersContainer = Backbone.View.extend({
	
	preDisableTabStates:[],
	
	/*
	this is only the view for the current filter group, it should NOT control
	the interaction of filter groups, only add/edit/remove/interaction of the view elements
	*/
	filterItemMouseover:function(e){
		$('button.close',$(e.currentTarget)).show();
		$('span.cf-filter-edit-button',$(e.currentTarget)).show();
	},
	filterItemMouseleave:function(e){
		$('button.close',$(e.currentTarget)).hide();
		$('span.cf-filter-edit-button',$(e.currentTarget)).hide();
	},
	
	enable:function() {
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
	disable:function() {
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
	
	add:function(filterData) {
		// add filter to current filter group
		// ASSERTION: filterData will be valid
		// filterData: {table, category, column, type, label, filterValue:{type, ...}}
		//console.log(filterData.attributes);
		var mAtt = _.clone(filterData.attributes);
		mAtt.cid = filterData.cid;
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
			
			//dispatch event up the chain, pass cid so the model can be remove from the collection
			dfc.trigger('removeClick',fData.cid);
		});
		
		//click event for the edit filter icon button
		$('h4.list-group-item-heading span.cf-filter-edit-button', flit).click({dfc:this, 'cid':mAtt.cid},function(e) {
			//just send the filter cid up the chain
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
			$('ul.nav',this.$el).append(this.filterColumnTemplate(mAtt));
			
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
				//console.log($('ul.nav-pills li', this.$el));
				$('ul.nav-pills li a', this.$el).first().tab('show');
			}
		}
	},
	
	updateFilter:function(filter) {
		console.log(filter);
		var fALink = $('div.tab-content div.list-group a.list-group-item[data-filter-cid="'+filter.cid+'"]', this.$el),
			fa = filter.attributes,
			fv = filter.attributes.filterValue;
		if(fALink.length) {
			$('h4.list-group-item-heading strong',fALink).html(fv.type);
			$('p.list-group-item-text span',fALink).html([fa.table, ("."+fa.column+" "), fv.description].join(''));
		}
	},
	
	remove:function() {
		
	},
	
	tagName:'div',
	className:'panel-body cf-data-filters-container',
	events:{},
	
	template:_.template(CFTEMPLATES.dataFiltersControlBody,{variable:'container'}),
	
	// this is the tab, it represents filters for a particular column (identified by )
	filterColumnTemplate:_.template(
		['<li>',
			'<a href="#<%= columnData.columnId %>" role="pill" data-toggle="pill" class="list-group-item">',
				'<%= _.isArray(columnData.column) ? columnData.label : (columnData.label[0].toUpperCase()+columnData.label.substring(1)) %> <span class="badge pull-right">1</span>',
			'</a>',
		'</li>'].join(''),
		{variable:'columnData'}
	),
	
	// this is the content for the tab
	filterColumnTabTemplate:_.template(
		['<div class="tab-pane" id="<%= columnData.column %>">',
			'<div class="list-group"></div>',
		'</div>'].join(''),
		{variable:'columnData'}
	),
	
	// this is an item in the tab content list
	filterListItemTemplate:_.template(
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
		].join(''),
		{variable:'filterData'}
	),
	
	initialize:function(options) {
		/*
		.nav : add > <li><a href="#column1" role="pill" data-toggle="pill">Column 1 <span class="badge pull-right">99</span></a></li>
		.tab-content : add > <div class="tab-pane" id="column1">
								<div class="list-group">
									<a href="#" class="list-group-item">Cras justo odio</a> ...
		*/
		
		this.$el.append(this.template({}));
		
	},
	render:function() {
		return this;
	}
});

// View for the Common Value Filter Selection Control
var VCommonValueFilterControl = Backbone.View.extend({
	
	selectedColumns:[],
	selectedCount:0,
	
	hide:function() {
		this.$el.hide();
	},
	show:function() {
		this.$el.show();
	},
	disable:function() {
		$('button.dropdown-toggle',this.$el).addClass('disabled');
	},
	enable:function() {
		$('button.dropdown-toggle',this.$el).removeClass('disabled');
	},
	
	getSelectedColumnData:function() {
		return this.selectedCount ? {
			'label':_.map(this.selectedColumns, function(c) { return c.attributes.label[0].toUpperCase()+c.attributes.label.substring(1); }).join(','), 
			'type':this.selectedColumns[0].attributes.type, 
			'name':_.map(this.selectedColumns, function(c) { return c.attributes.name; })  
		} : false;
	},
	
	
	tagName:'div',
	className:'btn-group cf-common-value-dropdown',
	events:{
		'mouseover ul.dropdown-menu li.cf-cvdd-active':function(e) {
			$(e.currentTarget).addClass('cf-common-value-list-item-hover');
		},
		'mouseleave ul.dropdown-menu li.cf-cvdd-active':function(e) {
			$(e.currentTarget).removeClass('cf-common-value-list-item-hover');
		},
		'click ul.dropdown-menu li.disabled':function(e) {
			return false;
		},
		'click ul.dropdown-menu li.cf-cvdd-active button':function(e) {
			//if it wasn't selected, then make it selected
			//if it was selected, then de-select it
			var col = this.collection.findWhere({'type':$(e.currentTarget).data('type'),'name':$(e.currentTarget).data('name')}),
				newSelectedStatus = !col.get('selected'),
				//enables = this.collection.where({'type':col.get('type')}),
				disables = this.collection.difference(this.collection.where({'type':col.get('type')}));
			col.set('selected',newSelectedStatus);
			console.log(col);//wondering if enum type should be excluded if their enum values are different
			
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
	
	template:_.template(CFTEMPLATES.commonValueController,{variable:'data'}),
	
	initialize:function(options) {
		/*
		 * columns is required in the options
		 * parse the columns array and pull out any columns that are:
		 *   - the only one of its type
		 *   - a single-value filter type
		*/
		var colTypes = _.countBy(options.columns, function(c) {return c.type;}),
			nonUniques = _.filter(options.columns, function(c) { return ( colTypes[c.type]>1 && c.type!='enum'); });
		
		this.collection = new Backbone.Collection( nonUniques );
		this.$el.append(this.template({'columns':nonUniques}));
	},
	render:function() {
		return this;
	}
});


// DataFilters (the main shit)
var VDataFilters = Backbone.View.extend({
	
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
	FILTER_SELECTION_TYPES:{ 'DEFAULT':0, 'COMMON_VALUE':1 },
	
	// Enum of the different interactive modes this control can be put into
	MODES:{ 'DEFAULT':0, 'CATEGORY_SETS':1 },
	
	defaultConfig:{
		'mode':0,
		'table':'undefined',
		'showFirst':null,
		'filterSelectionType':0,
		'filters':false,
		'filterCategories':[]
	},
	mode:0,					// setting the mode to 1 enables the save/remove filter set and filter set groups
	table:'undefined',		// the name of the database table or virtual source
	filterSelectionType:0,  // the type of filter selection to display
	filters:null,			// a collection of MDataFilter
	filterCategories:[],	// array of names
	
	//key/value container for groups filter categories
	// TODO JS Object, LocalStorage, Backbone.Collection with AJAX backend to a DB
	// { <key = name>:{description:<string>, filters:[]} }
	filterCategorySets:{},
	
	//the modal for add/edit filter sets
	modal:null,
	
	// TODO turn categories into collections
	
	//key for filtering models in the filters
	//categories end up being drop down lists in the dataFiltersControl nav bar
	currentFilterCategory:null,
	
	//index to the filter set in this.filterCategorySets[currentFilterCategory].filters
	currentWorkingFilterSet:null,
	
	//cid of the model in the filters collection during an edit
	editFilterCid:null,
	
	//used to keep track of filters displayed in the dataFiltersContainer
	currentColumnFilter:{'table':null,'type':null,'column':null,'label':null},
	
	//used to restore after a save/cancel
	previousColumnFilter:{'type':null, 'column':null, 'label':null},
	
	//used to keep track of the filter control nav bar dropdowns
	preEditFilterControlStates:[],
	
	commonValueControl:null,	//multi-column value filter control
	filterFactory:null,			//all filter widgets
	dataFiltersContainer:null,	//panel body view
	dataFiltersControl:null,	//panel footer, kind of
	
	filterCategoryGlyphMapping:function(catName) {
		var retVal = 'glyphicon-cloud-upload';
		switch(catName) {
			case 'User':
			case 'user':
				retVal = 'glyphicon-user';
				break;
		}
		return retVal;
	},
	
	
	// called from the event when the filter selection type radio set is changed
	filterSelectionTypeChange:function(newSelectionType) {
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
	commonValueColumnSelectionChange:function(columnData) {
		console.log(columnData);
		console.log(this.commonValueControl.selectedCount);
		if(this.commonValueControl.selectedCount) {// columns are selected
			//tell the filter factory to show this data type (if it isn't already)
			if(this.filterFactory.activeFilter().type !== columnData.type) {
				this.changeFilterFactoryType(columnData.type,columnData.name,columnData.label);
			} else {
				// type is the same, so just update the column
				this.currentColumnFilter.label = columnData.label;
				this.currentColumnFilter.column =_.map(this.commonValueControl.selectedColumns, function(c) { return c.attributes.name; })
				//this.filterFactory.updateFilterLabel(this.currentColumnFilter.label);
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
	changeFilterFactoryType:function(type,column,label,subType) {
		this.currentColumnFilter = {
			'table':this.table,
			'type':type,
			'column':column,
			'label':label
		};
		this.filterFactory.load(this.currentColumnFilter.column, this.currentColumnFilter.type, _.isArray(column)?'multi-column':this.currentColumnFilter.label, subType);
	},
	
	// show the save/cancel edit button group and disable everything but it and the filter factory
	editFilterMode:function() {
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
		this.dataFiltersContainer.disable()
		
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
	cancelEditFilterMode:function() {
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
	},
	
	// makes sure there are no duplicates and then adds a menu dropup to the footer control
	// and a dropup link to 
	addCategory:function(name, filters) {
		if($.inArray(name,this.filterCategories)<0) {
			this.filterCategories.push(name);
			
			// add a menu dropup to the footer control nav bar
			$('nav.cf-datafilters-controller-footer div.navbar-collapse',this.$el).append(
				_.template(CFTEMPLATES.filterCategoryMenu,{variable:'filterCategory'})({'name':name})
			);
			
			// add list item to the save menu dropup
			var saveUl = $('nav.cf-datafilters-controller-footer ul.navbar-right li.cf-save-filter-list ul.dropdown-menu',this.$el);
			saveUl.append(
				_.template(
					CFTEMPLATES.filterCategorySaveItem,
					{variable:'filterCategory'}
				)({'name':name, 'glyph':this.filterCategoryGlyphMapping(name)})
			);
			
			// if there are more categories to add after this one, add a divider (for style)
			if(this.filterCategories.length < this.defaultConfig.filterCategories.length) {
				saveUl.append( $(document.createElement('li')).addClass('divider') );
			}
			
			// set the current filter category to the first category added
			if(this.filterCategories.length===1) {
				this.currentFilterCategory = this.filterCategories[0];
			}
		}
		// TODO handle filters arg (used when filters are pulled from existing data): 
		
	},
	
	// PUBLIC Functions
	// returns filters as an object, or false if there aren't filters to return
	getCurrentFilter:function() {
		if(this.mode==this.MODES.DEFAULT) {
			return this.filters.length ? this.filters.toJSON() : false ;
		} else {
			// TODO look at currentWorkingFilterSet and currentFilterCategory and currentColumnFilter
			console.log(this.currentColumnFilter);
		}
	},
	
	tagName:'div',
	className:'panel panel-default',
	
	events:{
		
		// DATA FILTER TYPE CHANGE
		// is to change the data filter type selection to the selected type
		'change .btn-group.cf-data-filter-type-selection input':function(e) {
			var eVal = e.currentTarget.value*1;
			this.filterSelectionTypeChange(eVal);
		},
		
		
		// COLUMN FILTER CHANGE
		// is to load the data info from the clicked event into the filter factory
		'click ul.cf-columns-select-dd li a':function(e) {
			this.changeFilterFactoryType($(e.currentTarget).data('type'),$(e.currentTarget).data('name'),$(e.currentTarget).html());
		},
		
		// ADD FILTER CLICK
		// should first call validate on the active filter type
		'click button.cf-add-filter-button':function(e) {
			
			//this.filterFactory.disable();
			var af = this.filterFactory.activeFilter(),
				fVal = af?this.filterFactory.getFilterValue():false;
			
			// check if we are in COMMON_VALUE mode
			// if it is, then check if more than 1 column has been selected
			if(this.filterSelectionType && this.currentColumnFilter.column.length<2) {
				alert('Multiple columns are required for a common value, otherwise just use a regular data filter.');
				return false;
			}
			
			if(fVal) {
				// enable save filter dropdown
				if($('li.cf-save-filter-list', this.dataFiltersControl).hasClass('disabled')) {
					$('li.cf-save-filter-list', this.dataFiltersControl).removeClass('disabled');
				}
				
				// create new data filter
				var ndf = new MDataFilter({
					'table':this.table,
					'category':this.currentFilterCategory,
					'type':this.currentColumnFilter.type,
					'column':this.currentColumnFilter.column,
					'label':this.currentColumnFilter.label,
					'filterValue':fVal
				});
				
				// listen for change event on the model
				ndf.on('change:filterValue', function(filter) {
					//need to update filter tab content list item
					this.dataFiltersContainer.updateFilter(filter);
				}, this);
				
				//add to the current category of filters
				this.filters.add(ndf);
			}
		},
		
		// SAVE FILTER CLICK
		// triggered when the save filter item is clicked
		'click nav.cf-datafilters-controller-footer ul.nav li.btn[title="save"] ul.dropdown-menu li':function(e) {
			var cat = $(e.currentTarget).data('save-type'),
				catDd = $('nav.cf-datafilters-controller-footer div.navbar-collapse ul[data-category-name="'+cat+'"]',this.$el),
				catDdLi = $('li.dropup', catDd),
				catDdMenu = $('ul.dropdown-menu',catDdLi);
			// TODO filter category dropup will be enabled and have a list item associated with the current filters
			// TODO check if there is filter data to save
			
			//reset the modal and then show it
			$('div.modal form', this.$el)[0].reset();
			this.modal.modal('show');
			
			if(catDdLi.hasClass('disabled')) {
				//this is the first filter set being saved to this category
				catDdLi.removeClass('disabled');
				
			} else {
				//add another filter set to the existing category
				
			}
		},
		
		// SAVE EDIT FILTER CLICK
		'click button.cf-edit-filter-button':function(e) {
			//get filter value from filterFactory and apply it to the filter in the collection
			//this should update the dataFiltersContainer view
			//if this.currentWorkingFilterSet is null then we don't have to trigger an update event on the collection model
			var fVal = this.filterFactory.getFilterValue();
			if(fVal) {
				this.cancelEditFilterMode();
				var f = this.filters.get(this.editFilterCid);
				this.filters.get(this.editFilterCid).set({'filterValue':fVal});
			}
		},
		
		// CANCEL EDIT FILTER CLICK
		'click button.cf-cancel-edit-filter-button':function(e) {
			this.cancelEditFilterMode();
		},
		
		// MODAL ACTION BUTTON CLICK
		'click div.modal div.modal-footer button:last-child':function(e) {
			//for now this is only triggered for saving filter sets
			// TODO validate form inputs
			var fsName = $.trim($('input#cfFilterSetSaveName',this.modal).val());
			if(fsName.length) {
				var fsDesc = $.trim($('textarea#cfFilterSetSaveDescription',this.modal).val());
				if(_.has(this.filterCategorySets, this.currentFilterCategory)) {
					//add to the existing filter set
					
				} else {
					//create new filter set
					/*this.filterCategorySets[this.currentFilterCategory] = {
						'table':this.table,
						'name':fsName,
						'description':fsDesc.length?fsDesc:null,
						'filters':this.filters.where({'category':})
					};*/
				}
				
				//currentWorkingFilterSet
				
			}
			
			//use info from form inputs to create a list item in the category dropdown
			
			
			//
			
		}
	},
	
	
	initialize:function(options) {
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
		if(_.has(options,'filters')) {
			// TODO populate
			this.defaultConfig.filters = options.filters;
		} else {
			this.filters = new CDataFilters();
		}
		// can fetch filters from AJAX, or just populate
		if(_.has(options,'filterCategories') && _.isArray(options.filterCategories)) {
			this.defaultConfig.filterCategories = options.filterCategories;
		}
		
		
		// validTableColumns will populate the dropdown list of columns and the common value control
		var validTableColumns = [];
		if(options.hasOwnProperty('tableColumns') && _.isArray(options.tableColumns) && options.tableColumns.length) {
			/*assert tableColumns is an array of objects:
			'data':string, 
			'name':string, 
			'title':string, 
			'type':string, 
			'visible':boolean,
			'render':function,
			'cfexclude':boolean,
			'cftype':string,
			'cfenumsource':array,
			'cfenumlabelkey':string
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
							'name':tc.data
						};
						if(tc.cftype==='enum') {
							_.extend(mappedCol, {'cfenumsource':tc.cfenumsource});
						}
						if(_.has(tc,'cfexclude')) {
							_.extend(mappedCol, {'cfexclude':tc.cfexclude});
						}
						if(_.has(tc,'cfenumlabelkey')) {
							_.extend(mappedCol, {'cfenumlabelkey':tc.cfenumlabelkey});
						}
						_.extend(mappedCol,{'selected':false});
						validTableColumns.push(mappedCol);
					}
				}
			}
		}
		
		// Create and Populate the filter factory
		this.filterFactory = new VDataFilterFactory({showOnInit:this.defaultConfig.showOnInit, collection:new Backbone.Collection(
			[
				new VDataColumnFilterWidget({'type':'text', collection:new Backbone.Collection([
					new VFilterWidgetTypeTextEq(),
					new VFilterWidgetTypeTextSrch()
				])}),
				new VDataColumnFilterWidget({'type':'number', collection:new Backbone.Collection([
					new VFilterWidgetTypeNumberEq(),
					new VFilterWidgetTypeNumberBtwn(),
					new VFilterWidgetTypeNumberSel()
					
				])}),
				new VDataColumnFilterWidget({'type':'date', collection:new Backbone.Collection([
					new VFilterWidgetTypeDateEq(),
					new VFilterWidgetTypeDateBtwn(),
					new VFilterWidgetTypeDateSel(),
					new VFilterWidgetTypeDateCycle()
					
				])}),
				new VDataColumnFilterWidget({'type':'boolean', collection:new Backbone.Collection([
					new VFilterWidgetTypeBoolEq()
				])}),
				new VDataColumnFilterWidget({'type':'enum', collection:new Backbone.Collection([
					new VFilterWidgetTypeEnumIn({'enums':_.where(validTableColumns, {'type':'enum'})})
				])})
			]
		)});
		
		// There will always be a user (or default) filter
		// should pull all table filters/column filters for this user + common and public
		this.dataFiltersContainer = new VDataFiltersContainer();
		
		this.$el.append(
			_.template(CFTEMPLATES.dataFiltersPanelContent,{variable:'panelheading'})({'filterColumns':validTableColumns}),
			this.dataFiltersContainer.el,
			_.template(CFTEMPLATES.dataFiltersControlFooter,{variable:'controller'})({'filterCategories':this.defaultConfig.filterCategories})
		);
		
		//add UI components and set initial display states for UI
		this.commonValueControl = new VCommonValueFilterControl({'columns':validTableColumns});
		$('div.cf-common-value-controller-replace',this.$el).replaceWith(this.commonValueControl.$el);
		$('.cf-filter-factory-container-row',this.$el).append(this.filterFactory.el);
		$('button.cf-edit-filter-button', this.$el).hide();
		$('button.cf-cancel-edit-filter-button', this.$el).hide();
		
		// set properties for view
		this.dataFiltersControl = $('nav.cf-datafilters-controller-footer',this.$el);
		
		// re-usable modal
		$('div.modal div.modal-body', this.$el).html(_.template(CFTEMPLATES.saveFilterSetModalForm)({}));
		this.modal = $('div.modal',this.$el).modal({
			'backdrop':'static',
			'keyboard':false,
			'show':false
		});
		
		// EVENT HANDLERS
		// event handler when a filter is added
		this.filters.on('add', function(filter) {
			this.dataFiltersContainer.add(filter);
		}, this);
		
		this.filters.on('remove', function(filter) {
			if(this.filters.length<1) {
				// disable the save filter dropdown
				$('li.cf-save-filter-list', this.dataFiltersControl).addClass('disabled');
			}
		}, this);
		
		// when the remove button from a filter in the filter container view is clicked
		this.listenTo(this.dataFiltersContainer,'removeClick', function(filterCid) {
			this.filters.remove(this.filters.get(filterCid));
		});
		
		// upstream handler when a filter item edit click event
		this.listenTo(this.dataFiltersContainer,'changeClick', function(filterCid) {
			this.editFilterCid = filterCid;
			this.editFilterMode();
			
			var f = this.filters.get(this.editFilterCid).attributes;
			this.changeFilterFactoryType(f.type,f.column,f.label,f.filterValue.type);
			this.filterFactory.setFilterValue(f);
		});
		
		// upstream handler when a common value column is clicked
		this.listenTo(this.commonValueControl, 'columnClick', this.commonValueColumnSelectionChange);
		
		
		// check if the save filter and filter category controls should be visible
		if(this.defaultConfig.mode && this.defaultConfig.filterCategories.length) {
			for(var i in this.defaultConfig.filterCategories) {
				this.addCategory(this.defaultConfig.filterCategories[i]);
			}
		}
		
		// handle when filterSelectionType is passed with a value other than FILTER_SELECTION_TYPES.DEFAULT
		if(this.filterSelectionType != this.FILTER_SELECTION_TYPES.DEFAULT) {
			//call function as if the click event was triggered
			this.filterSelectionTypeChange(this.filterSelectionType);
		} else {
			// hide commonValueControl
			this.commonValueControl.hide();
		}
		
		// TODO also check if filter selection type is DEFAULT
		if(_.isString(this.defaultConfig.showFirst)) {
			var dfDdLi = $('ul.cf-columns-select-dd li a[data-name="'+this.defaultConfig.showFirst+'"]',this.$el);
			if(dfDdLi.length) {
				this.changeFilterFactoryType(dfDdLi.first().data('type'),dfDdLi.first().data('name'),dfDdLi.first().html());
			}
		}
		
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Text (Equals)
var VFilterWidgetTypeTextEq = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'equals',
	
	
	isValid:function() {
		return $.trim($('input',this.$el).val()).length>0;
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('text cannot be empty');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is equal to ' + $.trim($('input',this.$el).val());
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				value:$.trim($('input',this.$el).val()),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		$('input',this.$el).val(filterValue.value);
	},
	reset:function() {
		$('input',this.$el).val(null);
	},
	
	
	initialize:function(options) {
		this.$el.html(
			'<input type="text" placeholder="equals" size="32" maxlength="45" autocomplete="off" value="" />'+
			'<span class="help-block">filtering the results by column values equal to this</span>'
		);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Text (Search)
var VFilterWidgetTypeTextSrch = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'search',
	
	
	isValid:function() {
		return $.trim($('input',this.$el).val()).length>0;
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('text cannot be empty');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is like to ' + $.trim($('input',this.$el).val());
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				value:$.trim($('input',this.$el).val()),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		$('input',this.$el).val(filterValue.value);
	},
	reset:function() {
		$('input',this.$el).val(null);
	},
	
	
	initialize:function(options) {
		this.$el.html(
			'<input type="text" placeholder="equals" size="32" maxlength="45" autocomplete="off" value="" />'+
			'<span class="help-block">filtering the results by column values similar to this</span>'
		);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Equals)
var VFilterWidgetTypeNumberEq = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'equals',
	sb:null,
	sbOptions:{
		//value:<number>
		//min:<number>
		//max:<number>
		//step:<number>
		//hold:<boolean>
		//speed:<string> "fast","medium","slow"
		//disabled:<boolean>
		//units:<array> array of strings that are allowed to be entered in the input with the number
		min:-10, max:100, step:.25
	},
	
	
	isValid:function() {
		return !isNaN(this.sb.spinbox('value')*1);
	},
	validate:function() {
		if(this.isValid()) {
			return true;
		}
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is equal to ' + this.sb.spinbox('value')*1;
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.sb.spinbox('value')*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		this.sb.spinbox('value',filterValue.value);
	},
	reset:function() {
		this.setValue(0);
	},
	
	template:_.template(
		CFTEMPLATES.numberSpinner1+
		'<span class="help-block">filtering the results by column values equal to this</span>',
		{variable:'spinbox'}
	),
	initialize:function(options) {
		this.$el.addClass('fuelux');
		// TODO make this a spinner (FuelUX, JQueryUI)
		this.$el.html(this.template({}));
		$('.spinbox',this.$el).spinbox(this.sbOptions);
		this.sb = $('.spinbox',this.$el);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Between)
var VFilterWidgetTypeNumberBtwn = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'between',
	sbFrom:null,
	sbTo:null,
	sbOptions:{
		//value:<number>
		//min:<number>
		//max:<number>
		//step:<number>
		//hold:<boolean>
		//speed:<string> "fast","medium","slow"
		//disabled:<boolean>
		//units:<array> array of strings that are allowed to be entered in the input with the number
		min:-10, max:100, step:.25
	},
	
	
	isValid:function() {
		var fromNum = this.sbFrom.spinbox('value')*1,
			toNum = this.sbTo.spinbox('value')*1,
			fromNumCheck = !isNaN(fromNum),
			toNumCheck = !isNaN(toNum),
			isNotEqualCheck = (fromNum!==toNum);
		return (fromNumCheck && toNumCheck && isNotEqualCheck);
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('a from and to number must be given');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is between ' + this.sbFrom.spinbox('value') + ' and ' + this.sbTo.spinbox('value');
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				from:this.sbFrom.spinbox('value')*1,
				to:this.sbTo.spinbox('value')*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		//data is expected to be an object with from/to keys
		if(_.has(filterValue,'from') && _.isNumber(filterValue.from)) {
			this.sbFrom.spinbox('value',filterValue.from);
		}
		if(_.has(filterValue,'to') && _.isNumber(filterValue.to)) {
			this.sbTo.spinbox('value',filterValue.to);
		}
	},
	reset:function() {
		this.setValue({'from':0,'to':0});
	},
	
	
	events:{
		'changed.fu.spinbox div.spinbox.sbFrom':function(e) {
			//console.log('spinbox from changed');
			// TODO
		},
		'changed.fu.spinbox div.spinbox.sbTo':function(e) {
			//console.log('spinbox to changed');
			
		}
	},
	template:_.template(
		'<div class="row"><div class="col-xs-4">'+_.template(CFTEMPLATES.numberSpinner1,{variable:'spinbox'})({name:'sbFrom'})+'</div>'+
		'<div class="col-xs-2"><span class="btn btn-default disabled"><span class="glyphicon glyphicon-resize-horizontal"></span> to</span></div>'+
		'<div class="col-xs-6">'+_.template(CFTEMPLATES.numberSpinner1,{variable:'spinbox'})({name:'sbTo'})+'</div>'+
		'<span class="help-block">filtering the results by column values between these numbers</span>'
	),
	initialize:function(options) {
		this.$el.addClass('fuelux');
		this.$el.html(this.template);
		$('.spinbox',this.$el).spinbox(this.sbOptions);
		this.sbFrom = $('.spinbox.sbFrom',this.$el);
		this.sbTo = $('.spinbox.sbTo',this.$el);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Select)
var VFilterWidgetTypeNumberSel = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'select',
	sb:null,
	sbOptions:{min:-10, max:100, step:.25},
	valueList:[],
	listEl:null,
	
	
	isValid:function() {
		return this.valueList.length>0;
	},
	
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('one or more numbers must be selected');
		return false;
	},
	
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is one of these numbers: (' + this.valueList.join(',') + ')';
		} else {
			return false;
		}
	},
	
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.valueList,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	
	setValue:function(filterValue) {
		//expecting array of numbers
		this.valueList = filterValue.value;
		for(var i in filterValue.value) {
			addToList(filterValue.value[i]);
		}
	},
	
	reset:function() {
		this.sb.spinbox('value',0);
		this.listEl.empty();
		this.valueList = [];
	},
	
	addToList:function(value) {
		/*
		<div class="cf-list-item">
			<span>x.x</span>
			<button class="close" data-numberValue="x.x"><span area-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		</div>
		*/
		this.valueList.push(value);
		return $(document.createElement('div')).addClass('cf-list-item')
											   .mouseover(function(e){
													$('button.close',$(e.currentTarget)).show();
											 }).mouseleave(function(e){
													$('button.close',$(e.currentTarget)).hide();
											 }).append(
			$(document.createElement('span')).html(value),
			$(document.createElement('button')).addClass('close')
											   .data('numberValue',value)
											   .click({dataList:this.valueList}, function(e) {
												   var idx = _.indexOf(e.data.dataList, $(e.currentTarget).data('numberValue')*1);
												   e.data.dataList.splice(idx,1);
												   $(e.currentTarget).parent().remove();
											   })
											   .html('<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>')
											   .hide()
		);
	},
	
	events:{
		'click button.sbadd':function(e) {
			// TODO make sure it's not a duplicate
			var num = this.sb.spinbox('value')*1;
			if($.inArray(num, this.valueList)<0) {
				this.listEl.append(this.addToList(this.sb.spinbox('value')*1));
			}
		}
	},
	
	template:_.template(
		'<div class="row">'+
		'	<div class="col-md-5">'+CFTEMPLATES.numberSpinner1+'</div>'+
		'	<div class="col-md-2">'+
		'		<div class="pull-left"><button class="btn btn-default sbadd"><span class="glyphicon glyphicon-plus"></span></button></div>'+
		'	</div>'+
		'	<div class="col-md-5">'+
		'		<div class="panel panel-default">'+
		'			<div class="panel-heading">List of Values</div>'+
		'			<div class="panel-body"><div class="cf-list"></div>'+
		'		</div>'+
		'	</div>'+
		'</div>'+
		'<span class="help-block">filtering the results by column values in this list</span>',
		{variable:'spinbox'}
	),
	
	initialize:function(options) {
		this.$el.addClass('fuelux');
		this.$el.html(this.template({name:'sb'}));
		$('.spinbox',this.$el).spinbox(this.sbOptions);
		this.sb = $('.spinbox.sb',this.$el);
		this.listEl = $('.cf-list',this.$el);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Date (Equals)
var VFilterWidgetTypeDateEq = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'equals',
	dp:null,
	dpConfig:{
		autoclose:true,
		'name':'dpeq',
		'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
	},
	
	isValid:function() {
		return !isNaN(this.dp.datepicker('getDate').getTime());
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('a date must be selected');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return 'is equal to ' + this.dp.datepicker('getDate').toLocaleDateString();
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.dp.datepicker('getDate'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		// date should be a date
		if(_.isDate(filterValue.value)) {
			this.dp.datepicker('setDate',filterValue.value);
		}
	},
	reset:function() {
		this.dp.datepicker('setDate',null);
		
		//this.dp.datepicker('setEndDate',null);
		//this.dp.datepicker('setStartDate',null);
	},
	
	
	template:_.template(CFTEMPLATES.datepicker3,{variable:'datepicker'}),
	events:{
		'changeDate div.dpeq':function(e) {
			return false;
			
		}
	},
	initialize:function(options) {
		/*
		template datepicker3 wants: 
			name -required: string that will be added to the class list, 
			date: string date that should be in the same format as what you assign the datepicker, 
			format: string format - viewMode:CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us/en_gb/zh_cn, 
			viewMode: use CFTEMPLATES.DATEPICKER_VIEW_MODES.YEARS/MONTHS/DAYS, 
			minViewMode: same as viewMode
		*/
		this.$el.html(this.template(this.dpConfig));
		$('.dpeq',this.$el).datepicker(this.dpConfig);
		this.dp = $('.dpeq input',this.$el);
	}
});


// Filter Widget Type Implementation Class for Date (Equals)
var VFilterWidgetTypeDateBtwn = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'between',
	dpFrom:null,
	dpStartDate:null,
	dpTo:null,
	dpEndDate:null,
	dpConfig:{
		autoclose:true,
		format:CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
	},
	
	isValid:function() {
		return !isNaN(this.dpFrom.datepicker('getDate').getTime()) && !isNaN(this.dpTo.datepicker('getDate').getTime());
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('a to and from date must be selected');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			return [
				'is between ',
				this.dpFrom.datepicker('getDate').toLocaleDateString(),
				' and ',
				this.dpTo.datepicker('getDate').toLocaleDateString()
			].join('');
		} else {
			return false;
		}
	},
	getValue:function() {		
		if(this.validate()) {
			return {
				'type':this.type,
				'fromDate':this.dpFrom.datepicker('getDate'),
				'toDate':this.dpTo.datepicker('getDate'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		this.dpStartDate = filterValue.fromDate;
		this.dpEndDate = filterValue.toDate;
		this.dpFrom.datepicker('setDate', this.dpStartDate);
		this.dpTo.datepicker('setDate', this.dpEndDate);
		this.dpFrom.datepicker('setEndDate',this.dpEndDate);
		this.dpTo.datepicker('setStartDate',this.dpStartDate);
	},
	reset:function() {
		this.dpStartDate = null;
		this.dpEndDate = null;
		this.dpFrom.datepicker('setDate',null);
		this.dpTo.datepicker('setDate',null);
		this.dpFrom.datepicker('setEndDate',null);
		this.dpTo.datepicker('setStartDate',null);
	},
	
	
	template:_.template(CFTEMPLATES.datepicker4,{variable:'datepicker'}),
	events:{
		'changeDate .dpbtw input:first-child':function(e) {
			this.dpFrom.datepicker('setEndDate',e.date);
			if(e.date) {
				//date is valid
				//does the to-date have a limiter?
				this.dpStartDate = new Date(e.date.valueOf()+86400000);
				this.dpTo.datepicker('setStartDate',this.dpStartDate);
			} else {
				//cleared date, clear dpTo.startDate
				this.dpStartDate = null;
				this.dpTo.datepicker('setStartDate',this.dpStartDate);
			}
			if(isNaN(this.dpTo.datepicker('getDate').getTime())) {
				this.dpTo[0].focus();
			}
		},
		'changeDate .dpbtw input:last-child':function(e) {
			//place date value in text input
			this.dpTo.datepicker('setStartDate',e.date);
			if(e.date) {
				this.dpEndDate = new Date(e.date.valueOf()-86400000);
				this.dpFrom.datepicker('setEndDate',this.dpEndDate);
			} else {
				//cleared date, clear dpFrom.endDate
				this.dpEndDate = null;
				this.dpFrom.datepicker('setEndDate',this.dpEndDate);
			}
			if(isNaN(this.dpFrom.datepicker('getDate').getTime())) {
				this.dpFrom[0].focus();
			}
		},
		'click .test':function(e) {
			console.log(this.dp1.getDate());
			console.log(this.dp2.getDate());
		}
	},
	
	initialize:function(options) {
		this.$el.html(this.template({name:'dpbtw'}));
		$('.dpbtw input',this.$el).datepicker(this.dpConfig);
		this.dpFrom = $('.dpbtw input:first-child',this.$el);
		this.dpTo = $('.dpbtw input:last-child',this.$el);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Select)
var VFilterWidgetTypeDateSel = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'select',
	dp:null,
	dpConfig:{
		'name':'dpsel',
		autoclose:true,
		'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.en_us
	},
	valueList:[],
	listEl:null,
	
	isValid:function() {
		return this.valueList.length>0;
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('one or more dates must be selected');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			var dStrArr = [];
			for(var d in this.valueList) {
				dStrArr.push(new Date(this.valueList[d]).toLocaleDateString());
			}
			return 'is one of these dates: (' + dStrArr.join(',') + ')';
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.valueList,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		//expecting array of date timestamp numbers
		this.valueList = filterValue.value;
		for(var i in filterValue.value) {
			addToList(new Date(filterValue.value[i]));
		}
	},
	reset:function() {
		//TODO reset datepicker and list
		this.dp.datepicker('setDate',null);
		this.listEl.empty();
		this.valueList = [];
	},
	
	addToList:function(value) {
		this.valueList.push(value.getTime());
		return $(document.createElement('div')).addClass('cf-list-item')
											   .mouseover(function(e){
													$('button.close',$(e.currentTarget)).show();
											 }).mouseleave(function(e){
													$('button.close',$(e.currentTarget)).hide();
											 }).append(
			$(document.createElement('span')).html(value.toLocaleDateString()),
			$(document.createElement('button')).addClass('close')
											   .data('dateValue',value)
											   .click({dataList:this.valueList}, function(e) {
												   var idx = _.indexOf(e.data.dataList, $(e.currentTarget).data('dateValue')*1);
												   e.data.dataList.splice(idx,1);
												   $(e.currentTarget).parent().remove();
											   })
											   .html('<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>')
											   .hide()
		);
	},
	
	events:{
		'click button.dpadd':function(e) {
			// make sure it's not a duplicate
			var d = this.dp.datepicker('getDate');
			if(!isNaN(d.getTime()) && ($.inArray(d.getTime(), this.valueList)<0)) {
				this.listEl.append(this.addToList(d));
			}
		}
	},
	template:_.template(
		'<div class="row">'+
		'	<div class="col-md-5">'+CFTEMPLATES.datepicker3+'</div>'+
		'	<div class="col-md-2">'+
		'		<div class="pull-left"><button class="btn btn-default dpadd"><span class="glyphicon glyphicon-plus"></span></button></div>'+
		'	</div>'+
		'	<div class="col-md-5">'+
		'		<div class="panel panel-default">'+
		'			<div class="panel-heading">List of Dates</div>'+
		'			<div class="panel-body"><div class="cf-list"></div>'+
		'		</div>'+
		'	</div>'+
		'</div>'+
		'<span class="help-block">filtering the results by column values in this list</span>',
		{variable:'datepicker'}
	),
	initialize:function(options) {
		this.$el.html(this.template(this.dpConfig));
		$('.dpsel',this.$el).datepicker(this.dpConfig);
		this.dp = $('.dpsel',this.$el);
		this.listEl = $('.cf-list',this.$el);
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Date (Equals)
var VFilterWidgetTypeDateCycle = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'cycle',
	
	////////////////////////////////////////////////////////////////////
	// TODO Fix bug where the datepicker looses minViewMode setting
	////////////////////////////////////////////////////////////////////
	
	//aren't these available somewhere else like JQuery or Backbone or something?
	months:['January','February','March','April','May','June','July','August','September','October','November','December'],
	
	dp:null,
	dpConfig:{
		autoclose:true,
		minViewMode:1,
		startView:1,
		'name':'dpcy',
		'format':CFTEMPLATES.DATEPICKER_DATE_FORMATS.month_year
	},
	cycle:[
		{label:'1st-15th', value:1},
		{label:'16th-End Of Month', value:2}
	],
	
	
	isValid:function() {
		var d = this.dp.datepicker('getDate');
		return !isNaN(d.getTime());
	},
	validate:function() {
		// TODO unset inputs/labels from danger status
		if(this.isValid()) {
			// TODO set inputs/labels to danger status
			return true;
		}
		
		console.log('a month and year must be selected');
		return false;
	},
	getValueDescription:function() {
		if(this.isValid()) {
			var d = this.dp.datepicker('getDate');
			return 'for the billing cycle of ' + this.months[d.getMonth()] + ', ' + d.getFullYear();
		} else {
			return false;
		}
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'monthYear':this.dp.datepicker('getDate'),
				'cycle':$('div.btn-group label.active input',this.$el).val()*1,
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		if(_.has(filterValue,'monthYear') && _.isDate(filterValue.monthYear)) {
			this.dp.datepicker('setDate',filterValue.monthYear);
		} else {
			this.dp.datepicker('setDate',null);
		}
		if(_.has(filterValue,'cycle')) {
			// here it is
			$('div.btn-group label',this.$el).each(function(i,e){
				var lbl = $(e),
					inpt = $('input',$(e));
				lbl.removeClass('active');
				inpt.removeAttr('checked');
				if((inpt.val()*1)==filterValue.cycle){
					lbl.addClass('active');
					inpt.attr('checked','checked');
				}
			});
		}
	},
	reset:function() {
		this.setValue({'date':null,'cycle':1});
	},
	
	
	template:_.template(
		'<div class="btn-group" data-toggle="buttons"></div>'+CFTEMPLATES.datepicker3,
		{variable:'datepicker'}
	),
	initialize:function(options) {
		if(options && options.hasOwnProperty('cycle')) {
			// cycle is expected to be an array of date range objects within 1 month
			// [{label:<str>,value:<?>},...]
			this.cycle = options.cycle;
		}
		this.$el.html(this.template(this.dpConfig));
		$('.dpcy',this.$el).datepicker(this.dpConfig);
		this.dp = $('.dpcy input',this.$el);
		
		//populate buttons
		for(var i in this.cycle) {
			$('div.btn-group',this.$el).append(
				$(document.createElement('label')).addClass('btn btn-primary').append(
					$(document.createElement('input')).attr({'type':'radio','id':_.uniqueId('cf-dpcy_'),'value':this.cycle[i].value}),
					this.cycle[i].label
				)
			);
		}
		$('div.btn-group label.btn:first-child',this.$el).addClass('active');
		$('div.btn-group label.btn:first-child input',this.$el).first().attr('checked','checked');
	},
	render:function() {
		return this;
	}
});


// Filter Widget Type Implementation Class for Number (Select)
var VFilterWidgetTypeBoolEq = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'equals',
	
	defaultConfig:{
		'value':true,
		'trueLabel':'Active',
		'falseLabel':'Inactive'
	},
	
	model:null,
	
	isValid:function() {
		return true;//this ui should alway return a value
	},
	validate:function() {
		return true;
	},
	getValueDescription:function() {
		return ('is '+(this.model.get('value')?this.model.get('trueLabel'):this.model.get('falseLabel')));
	},
	getValue:function() {
		if(this.validate()) {
			return {
				'type':this.type,
				'value':this.model.get('value'),
				'description':this.getValueDescription()
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		if(_.isBoolean(filterValue.value)) {
			this.model.set('value', filterValue.value);
			//also change UI
			$('.btn-group label',this.$el).first().toggleClass('active', this.model.get('value'));
			$('.btn-group label',this.$el).last().toggleClass('active', !this.model.get('value'));
		}
	},
	reset:function() {
		this.setValue({'value':true})
	},
	
	events:{
		'click .btn-group label':function(e) {
			this.model.set('value', $(e.currentTarget).hasClass('cf-fw-type-bool-true'));
		}
	},
	
	template:_.template([
		'<div class="btn-group" data-toggle="buttons">',
			'<label class="btn btn-primary cf-fw-type-bool-true<%= value?" active":"" %>">',
				'<input type="radio" name="cf-fw-type-bool" id="cf-fw-type-bool-true"<%= value?" checked":"" %>> <%= trueLabel %>',
			'</label>',
			'<label class="btn btn-primary<%= value?"":" active" %>">',
				'<input type="radio" name="cf-fw-type-bool" id="cf-fw-type-bool-false" <%= value?"":" checked" %>> <%= falseLabel %>',
			'</label>',
		'</div>'
		].join('')),
	
	initialize:function(options) {
		this.model = new Backbone.Model();
		//options that affect UI
		this.model.set('trueLabel', (_.has(options,'trueLabel') && _.isString(options.trueLabel)) ? options.trueLabel : this.defaultConfig.trueLabel);
		this.model.set('falseLabel',(_.has(options,'falseLabel') && _.isString(options.falseLabel))?options.falseLabel:this.defaultConfig.falseLabel);
		
		this.$el.html(this.template(this.defaultConfig));
		
		this.model.set('value',(_.has(options,'value') && _.isBoolean(options.value) && !options.value)?false:this.defaultConfig.value);
	},
	render:function() {
		return this;
	}
});

// Filter Widget Type Implementation Class for Enum (Select)
var VFilterWidgetTypeEnumIn = VFilterWidgetType.extend({
	version:'1.0.2',
	type:'in',
	
	currentColumn:null,
	
	isValid:function() {
		return $.map($('.dropdown-menu input:checked',this.$el), function(e,i){ return e.value*1; }).length>0;
	},
	validate:function() {
		if(this.isValid()) {
			return true;
		}
		
		console.log('Enum checklist cannot be empty');
		return false;
	},
	getValueDescription:function() {//is this public?
		
		if(this.isValid()) {
			return 'is one of these : (' + $.map($('.dropdown-menu input:checked',this.$el), function(e,i){ return e.value*1; }).join(',') + ')';
		} else {
			return false;
		}
	},
	getValue:function() {
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
				'column':this.currentColumn,
				'value':checkMap,
				'description':[desc_1,checkNames.join(','),desc_2].join('')
			};
		}
		return false;
	},
	setValue:function(filterValue) {
		//TODO check if we need to set the enum group
		console.log(filterValue);
		
		
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
	reset:function() {
		//reset happens just before setValue
		//$('.dropdown-menu input',this.$el).each(function(i,e) {
			//e.checked = false;
		//});
		//this.$el.empty();
	},
	
	config:function(dataCol) {
		// dataCol must be a string; as of now I can't figure out how a multi-column filter
		// would handle multiple values, e.g. WHERE (1,2,3) IN('program_id, area_id)
		
		if(dataCol!==this.currentColumn) {
			this.currentColumn = dataCol;
			this.$el.html(this.template(this.collection.findWhere({'column':dataCol}).attributes));
		}
	},
	
	
	events:{
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
	template:_.template([
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
	initialize:function(options) {
		//split enums into groups by options.enums[i].name
		// check options.enums array of keys named 'id', a mapped copy of the array will 
		// need to be made where the 'id' keys are renamed to 'code'
		var enumData;
		if(_.has(options,'enums') && _.isArray(options.enums) && options.enums.length) {
			this.collection = new Backbone.Collection(
				$.map(options.enums, function(e,i){
					return { 'column':e.name, 'enums':e.cfenumsource, 'labelKey':e.cfenumlabelkey };
				})
			);
			this.currentColumn = this.collection.at(0).attributes.column;
			this.$el.html(this.template(this.collection.at(0).attributes));
		} else {
			this.$el.html(this.template({'enums':[]}));
		}
	},
	render:function() {
		return this;
	}
});


