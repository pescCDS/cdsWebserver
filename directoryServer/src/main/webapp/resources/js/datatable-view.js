var DataTableView = Backbone.View.extend({
	'sourceData':null,
	
	'dataTypeName':{
		'SHORT':'default',
		'LONG':'Default Data Type Name'
	},
	'DB_TO_DT_TYPES':{
		'varchar':'string',
		'tinytext':'string',
		'mediumtext':'string',
		'text':'string',
		'longtext':'string',
		'tinyint':'num',
		'smallint':'num',
		'mediumint':'num',
		'int':'num',
		'bigint':'num',
		'double':'num',
		'float':'num',
		'decimal':'num',
		'date':'date',
		'datetime':'date',
		'timestamp':'date',
		
		'object':'num'
	},
	'DB_TO_CF_TYPES':{
		'varchar':'text',
		'tinytext':'text',
		'mediumtext':'text',
		'text':'text',
		'longtext':'text',
		'tinyint':'number',
		'smallint':'number',
		'mediumint':'number',
		'int':'number',
		'bigint':'number',
		'double':'number',
		'float':'number',
		'decimal':'number',
		'date':'date',
		'datetime':'date',
		'timestamp':'date',
		
		'biglist':'',
		'object':'enum'
	},
	
	// this is for columnfilters and datatable ajax service
	'cfWebServiceUrl':'/columnfilters',
	
	// this is for the crud ajax calls that manage the individual objects in the datatable
	'webServiceUrl':'/wsc/rest',
	
	'columnFilters':null,
	'modalForm':null,
	'actionProgressPanel':null,
	'progressBar':null,
	'datatable':null,
	'datatableColumnMeta':[],
	'datatableConfig':{},
	
	'formInputs':[],
	
	'templates':{
		'columnVisListItem':_.template([
			'<li><div><label><span>Check to Show/Hide Column</span></label></div></li>',
			'<li class="divider"></li>',
			'<% for(var i in dtColumns) { %>',
				'<% if((_.has(dtColumns[i], "cfexclude") && !dtColumns[i].cfexclude) || !_.has(dtColumns[i],"cfexclude")) { %>',
			'<li>',
				'<div class="checkbox">',
					'<label>',
						'<input type="checkbox" value="<%= i %>"<% if(dtColumns[i].bVisible) { %> checked="checked"<% } %> />',
						'<span class="text-capitalize"><%= dtColumns[i].sTitle %></span>',
					'</label>',
				'</div>',
			'</li>',
			'<% } } %>'
		].join(''), {'variable':'dtColumns'})
	},
	
	
	// modify the progress bar label
	'setProgressBarLabel':function(label) {
		$('div.progress-bar.progress-bar-striped span',this.progressBar).empty().html(label);
	},
	
	// change progress bar style and display error message
	'progressErrorMessage':function(message) {
		this.setProgressBarLabel('error');
		// change the panel coloring to red
		$('div.action-progress-panel div.panel',this.modalForm).addClass('panel-danger');
		$('div.action-progress-panel .progress-bar',this.modalForm).addClass('progress-bar-danger');
		// display error message in modal progress panel body 
		$('div.action-progress-panel div.panel-body',this.modalForm).empty().append([
			'<p>',message,'</p>',
			'<div class="pull-right clearfix">',
				'<button type="button" class="btn btn-danger">OK</button>',
			'</div>'
		].join(''));
	},
	
	// restore progress bar to original state
	'resetProgressBar':function() {
		this.setProgressBarLabel('saving');
		$('div.action-progress-panel div.panel',this.modalForm).removeClass('panel-danger');
		$('div.action-progress-panel .progress-bar',this.modalForm).removeClass('progress-bar-danger');
		$('div.edit-dtview-modal div.action-progress-panel div.panel-body').empty();
	},
	
	// showProgress and hideProgress are for saving, the progress bar is
	// in a panel header inside the add/edit modal form
	'showProgress':function() {
		// disable modal buttons
		$('div.modal-header button.close',this.modalForm).hide();
		$('div.modal-footer button',this.modalForm).each(function(i,e) {
			e.disabled = true;
		});
		
		// hide (with transition) input form
		$('form',this.modalForm).hide();
		
		// show (with transition) progress bar
		this.actionProgressPanel.show();
	},
	'hideProgress':function() {
		// enable modal buttons
		$('div.modal-header button.close',this.modalForm).show();
		$('div.modal-footer button',this.modalForm).each(function(i,e) {
			e.disabled = false;
		});
		
		// reset progress bar
		this.resetProgressBar();
		
		// hide progress bar
		this.actionProgressPanel.hide();
		$('div.progress-bar',this.modalForm).removeClass('progress-bar-danger');
		
		// show input form
		$('form',this.modalForm).show();
	},
	
	// 
	'displayInputValidation':function(finput, isValid) {
		//var formGroup = $('div.dtview-form-'+formKey,this.modalForm);
		finput.input.formGroup.toggleClass('has-success',isValid).toggleClass('has-error',!isValid);
		// some input contolls won't have the feedback visual element
		if($('span.glyphicon.form-control-feedback',finput.input.formGroup).length) {
			$('span.glyphicon.form-control-feedback',finput.input.formGroup)
				.removeClass('hidden')
				.toggleClass('glyphicon-ok',isValid)
				.toggleClass('glyphicon-remove',!isValid);
		}
	},
	
	'resetForm':function() {
		//reset primary key hidden input
		$('#'+this.sourceData.primaryKey,this.modalForm).val(null);
		
		//loop through all inputs
		for(var i in this.formInputs) {
			var inpt = this.formInputs[i];
			if(inpt.type==='text' || inpt.type==='textarea') {
				inpt.input.formInput.val(null);
			} else if(inpt.type==='boolean') {
				// put the default value radio button first in your form group
				inpt.input.formInput.first().addClass('active');
				inpt.input.formInput.last().removeClass('active');
			} else if(inpt.type==='spinbox') {
				inpt.input.formInput.spinbox('value',0);
			} else if(inpt.type==='datepicker') {
				inpt.input.formInput.datepicker('setDate',null);
			} else if(inpt.type==='timestamp') {
				inpt.input.formInput.val(null);
			} else if(inpt.type==='radioset') {
				inpt.input.formInput.removeClass('active');
				inpt.input.formInput.first().addClass('active');
			} else if(inpt.type==='dropdown') {
				$('input:hidden',inpt.input.formGroup).val(null);
				$('input:text',inpt.input.formGroup).val(null);
			} else if(inpt.type==='typeahead') {
				$('input:text',inpt.input.formGroup).typeahead('val',null);
				inpt.value = null;
			}
			
			if(inpt.required) {
				inpt.input.formGroup.removeClass('has-success has-error');
				$('span.glyphicon.form-control-feedback',inpt.input.formGroup).removeClass('glyphicon-ok glyphicon-remove').addClass('hidden');
			}
		}
	},
	
	'getFormInputValue':function(fi) {
		var retVal = false;
		
		switch(fi.type) {
			case 'spinbox':
				retVal = fi.input.formInput.spinbox('value')*1;
				break;
			case 'boolean':
				var bVal = $('label.active input',fi.input.formGroup).val()*1;
				retVal = bVal ? 1 : 0;
				break;
			case 'datepicker':
				var idate = fi.input.formInput.datepicker('getDate');
				if(_.isDate(idate)) {
					retVal = !isNaN(idate.getTime()) ? idate.getTime() : false;
				}
				break;
			case 'timestamp':
				// this is here just so the default case won't try and grab the value
				break;
			case 'radioset':
				// will always have an active button
				var findObj = {},
					activeValue = $('label.active input', fi.input.formGroup).val();
				findObj[fi.valueKey] = typeof(fi.datasource[0][fi.valueKey])==='number' ? activeValue*1 : activeValue;
				retVal = _.findWhere(fi.datasource, findObj);
				break;
			case 'dropdown':
				var findObj = {},
					displayVal = $.trim($('input:text', fi.input.formGroup).val());
				if(displayVal.length) {
					var idValue = $('input:hidden', fi.input.formGroup).val();
					findObj[fi.valueKey] = typeof(fi.datasource[0][fi.valueKey])==='number' ? idValue*1 : idValue;
					retVal = _.findWhere(fi.datasource, findObj);
				}
				break;
			case 'typeahead':
				if(fi.value) {
					retVal = fi.value;
				}
				break;
			default:
				//assumes text or textarea input
				var txtVal = $.trim(fi.input.formInput.val());
				retVal = txtVal.length>0 ? txtVal : false;
				break;
		}
		return retVal;
	},
	
	'getFormData':function() {
		var returnData = {},
			idValue = $('#'+this.sourceData.primaryKey,this.modalForm).val(),
			editMode = _.isFinite(idValue),
			requiredInputs = _.where(this.formInputs, {'required':true}),
			optionalInputs = _.where(this.formInputs, {'required':false,'disabled':false}),
			validInputsTotal = requiredInputs.length,
			validInputCount = 0
		;
		
		if(editMode) {
			returnData[this.sourceData.primaryKey] = idValue*1;
		}
		
		// required inputs
		for(var i in requiredInputs) {
			var inputValue = this.getFormInputValue(requiredInputs[i]);
			if(inputValue!==false) {
				validInputCount++;
				returnData[requiredInputs[i].name] = inputValue;
			}
			this.displayInputValidation(requiredInputs[i], inputValue!==false);
		}
		
		//optional inputs
		for(var j in optionalInputs) {
			var inputValue = this.getFormInputValue(optionalInputs[j]);
			if(inputValue!==false) {
				returnData[optionalInputs[j].name] = inputValue;
			}
		}
		
		return (validInputCount===validInputsTotal) ? returnData : false;
	},
	
	// populates the form inputs with data grabbed from the data table
	// dtData: data from the data table row
	'setFormData':function(dtData) {
		for(var i in this.formInputs) {
			var inpt = this.formInputs[i];
			if(inpt.type==='text' || inpt.type==='textarea') {
				inpt.input.formInput.val(dtData[inpt.name]);
			} else if(inpt.type==='boolean') {
				inpt.input.formInput.removeClass('active'); 
				inpt.input.formInput.each(function(idx,eLabel) {
					var lbl = $(eLabel),
						isMatch = ($('input:radio',lbl).val()*1)==dtData[inpt.name];
					if(isMatch) {
						lbl.addClass('active');
					}
				});
			} else if(inpt.type==='spinbox' && _.isFinite(dtData[inpt.name])) {
				inpt.input.formInput.spinbox('value',dtData[inpt.name]);
			} else if(inpt.type==='datepicker' && _.isFinite(dtData[inpt.name])) {
				inpt.input.formInput.datepicker('setDate', new Date(dtData[inpt.name]));
			} else if(inpt.type==='timestamp' && _.isFinite(dtData[inpt.name])) {
				inpt.input.formInput.val( moment(dtData[inpt.name]).format("d/M/YYYY h:mm:ss a") );
			} else if(inpt.type==='radioset') {
				$('label.btn', inpt.input.formGroup).removeClass('active');
				$(['label.btn[data-',inpt.valueKey,'="',dtData[inpt.name][inpt.valueKey],'"]'].join(''), inpt.input.formGroup).addClass('active');
			} else if(inpt.type==='dropdown') {
				try {
					$('input:hidden',inpt.input.formGroup).val(dtData[inpt.name][inpt.valueKey]);
					if(typeof(inpt.displayKey)==='string') {
						$('input:text',inpt.input.formGroup).val(dtData[inpt.name][inpt.displayKey]);
					} else {
						$('input:text',inpt.input.formGroup).val(inpt.displayKey(dtData[inpt.name]));
					}
				} catch(err) {
					console.log(err);
				}
			} else if(inpt.type==='typeahead') {
				inpt.value = dtData[inpt.name];
				if(typeof(inpt.displayKey)==='string') {
					$('input:text',inpt.input.formGroup).typeahead('val', dtData[inpt.name][inpt.displayKey]);
				} else {
					$('input:text',inpt.input.formGroup).typeahead('val', inpt.displayKey(inpt.value));
				}
			}
		}
		
		// hidden input primary key
		$('#'+this.sourceData.primaryKey,this.modalForm).val(dtData[this.sourceData.primaryKey]);
	},
	
	
	'className':'data-table-view',
	'events':{
		// DATATABLE INIT COMPLETE
		'init.dt':function(e, settings, json) {
			var dt = $(this.datatable.table().container());
			// create refresh data table button
			$('div.refresh-datatable-btn-container',dt).append([
				'<button type="button" class="btn btn-default" title="refresh data">',
					'<span class="glyphicon glyphicon-refresh"></span>',
				'</button>'
			].join(''));
			
			// create column visibility data table button
			$('.column-visibility-container',dt).append (
				[
					'<div class="btn-group pull-left">',
						'<button type="button" class="btn btn-default dropdown-toggle pull-left" data-toggle="dropdown" aria-expanded="false">',
							'<span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span> <span class="caret"></span>',
						'</button>',
						'<ul class="dropdown-menu cf-enum-dropdown-list" role="menu">',
							this.templates.columnVisListItem(this.datatableColumnMeta),
						'</ul>',
					'</div>'
				].join('')
			);
			
			// create add row data table button
			$('div.add-button-container',dt).append(
				['<button type="button" class="btn btn-success pull-left">Add ','</button>'].join(this.dataTypeName.LONG)
			);
			
			this.$el.prepend(this.columnFilters.$el);
		},
		
		// CLICK AND CHANGE EVENTS FOR COLUMN VISIBILITY CONTROL
		'click .column-visibility-container ul.dropdown-menu input, .column-visibility-container ul.dropdown-menu label':function(e) {
			e.stopPropagation();
		},
		'change .column-visibility-container ul.dropdown-menu input':function(e) {
			this.datatable.columns(e.currentTarget.value*1).visible(e.currentTarget.checked);
			e.stopPropagation();
			return false;
		},
		
		// REFRESH DATATABLE
		'click div.refresh-datatable-btn-container':function(e) {
			this.datatable.ajax.reload();
		},
		
		// COLUMN VISIBILITY TOGGLE CLICK
		'click div.column-visibility-container button':function(e) {
			
		},
		
		// ADD CLICK
		'click div.add-button-container button':function(e) {
			$('h4.modal-title',this.modalForm).html('Create '+this.dataTypeName.LONG);
			$('button.dtview-modal-form-action-button',this.modalForm).html('Create');
			this.modalForm.modal('show');
		},
		
		// EDIT CLICK
		'click button.dtview-edit-btn':function(e) {
			var domRow = $(e.currentTarget).parent().parent().parent()[0],
				d = this.datatable.row(domRow).data();
			if(d) {
				// ajax get a fresh record and place that data into the modal and datatable
				$('h4.modal-title',this.modalForm).html('Edit '+this.dataTypeName.LONG);
				$('button.dtview-modal-form-action-button',this.modalForm).html('Save');
				this.resetProgressBar();
				this.setFormData(d);
				this.setProgressBarLabel('loading');
				this.showProgress();
				this.modalForm.modal('show');
				$.ajax({
					'context':this,
					'url':[this.webServiceUrl,this.dataTypeName.SHORT,d[this.sourceData.primaryKey]].join('/'),
					'type':'GET',
					'dataType':'json'
				}).done(function(data, textStatus, jqXHR) {
					if(!_.isNull(data)) {
						// populate modal form with data and then display it
						// also update datatable row with this data
						this.datatable.row(domRow).data(data);
						this.setFormData(data);
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					//error, use data from table
					console.error(errorThrown);
				}).always(function(data_jqXHR, textStatus, jqXHR_errorThrown) {
					this.hideProgress();
				});
			}
		},
		
		// REMOVE CLICK
		'click button.dtview-del-btn':function(e) {
			//get the index of the data tables row
			
			if(confirm(['Are you sure you want to remove this ','?'].join(this.dataTypeName.LONG))) {
				var d = this.datatable.row($(e.currentTarget).parent().parent().parent()[0]).data();
				$.ajax({
					'url':[this.webServiceUrl,this.dataTypeName.SHORT,'remove'].join('/'),
					'type':'POST',
					'contentType':'application/json',
					'dataType':'json',
					'data':JSON.stringify(d),
					'processData':false
				}).done(function(data,textStatus,jqXHR) {
					dtView.datatable.ajax.reload(null,false);
				}).fail(function(jqXHR,textStatus,errorThrown) {
					alert(errorThrown);
				});
			}
		},
		
		// MODAL ACTION BUTTON CLICK
		'click button.dtview-modal-form-action-button':function(e) {
			var fd = this.getFormData();
			if(fd) {
				this.showProgress();
				// ajax to server and update data table
				$.ajax({
					'context':this,
					'url':[this.webServiceUrl,this.dataTypeName.SHORT,'save'].join('/'),
					'type':'POST',
					'contentType':'application/json',
					'dataType':'json',
					'data':JSON.stringify(fd),
					'processData':false
				}).done(function(data,textStatus,jqXHR) {
					dtView.datatable.ajax.reload(null,false);
					dtView.modalForm.modal('hide');
				}).fail(function(jqXHR,textStatus,errorThrown) {
					this.progressErrorMessage(errorThrown);
				});
			}
		},
		
		// PROCESS ERROR MESSAGE BUTTON CLICK
		'click div.action-progress-panel div.panel-body button':function(e) {
			dtView.modalForm.modal('hide');
		},
		
		// EDIT MODAL HIDE
		'hidden.bs.modal div.edit-dtview-modal':function() {
			this.resetProgressBar();
			this.resetForm();
			this.hideProgress();
		},
		
		// DROPDOWN MENU ITEM CLICK (MODAL FORM DROPDOWN INPUT TYPE)
		'click div.edit-dtview-modal div.modal-body form.form-horizontal ul.dropdown-menu li':function(e) {
			// put the label data into the display input and the value data into the hidden input
			var ct = $(e.currentTarget),
				d_id = ct.data('id'),
				d_key = ct.data('key'),
				d_label = ct.data('label'),
				formGroup = _.findWhere(this.formInputs, {'name':d_key}).input.formGroup;
			$('input:hidden', formGroup).val(d_id);
			$('input:text', formGroup).val(d_label);
		},
		
		'typeahead:selected div.edit-dtview-modal div.modal-body form.form-horizontal':function(jqEvent, suggestion, datasetName) {
			//put the suggestion in the formInput
			var input = _.findWhere(this.formInputs, {'name':datasetName});
			if(input) {
				input.value = suggestion;
			}
		}
	},
	'initialize':function(options) {
		// ASSERTION: options has these required properties: table, webServiceUrl
		// ASSERTION: table will have at least these: name, primaryKey, columns
		
		this.sourceData = options.table;
		this.webServiceUrl = options.webServiceUrl;
		
		//populate data type names
		this.dataTypeName.SHORT = options.table.name;
		this.dataTypeName.LONG = options.dataClass;
		
		// the column filters and data table will use the same url, but the rest api url
		// for adding/editing/removing will be different
		if(_.has(options,'cfWebServiceUrl')) {
			this.cfWebServiceUrl = options.cfWebServiceUrl;
		}
		
		/*
		TODO populate formInputs from options.table
		TODO populate requiredInputNames from formInputs
		
		datatableColumnMeta will be constructed using options.table properties 
		options.table = {
			name: a key string that TableClassMap.getClass() would return a class with,
			primaryKey: a string that should match one of the columns[n].id, used for the edit/remove function
			columns:[
				{
					--- required ---
					data:	<database column name or data column name>
					name:	<xjc generated field name>
					title:	descriptive name for column, default=this.name
					type:	<database column type>, will be put through DB_TO_DT_TYPES (e.g. varchar, text, int, date, ...)
					
					
					--- general purpose when type==object, or cftype==biglist ---
					table: a string used to identify the database table server side
					datasource: a Bloodhound data source object to populate the typeahead
					displayKey: a string or function used to show the primary data field(s) value
					valueKey: a string used to retrieve the identifier field
					
					--- optional (for datatableColumnMeta) ---
					visible:	<boolean> default=true
					
					--- required (for formInputs) ---
					controlType:	<string>; text, textarea, spinbox, datepicker
					
					--- optional (for formInputs) ---
					required:					<boolean> default=true
					disabled:					<boolean> default=false
					<config>:					<object> can be for spinbox or datepicker
					dropdownSourceValueKey:		<string> when control type is 'dropdown'
					dropdownSourceDisplayKey:	<string> when control type is 'dropdown'
					
					--- any extra column filters properties (e.g. cfexclude, cftype, cfenumsource, cfenumlabelkey) --
					cfexclude:	default=false, if you don't want to ColumnFilters to use this column
					// when cftype is present then it overrides the default mapping with DB_TO_CF_TYPES
					cftype:	if present, then this value will override the default DB_TO_CF_TYPES[this.type]
					cfenumsource, cfenumlabelkey: only used when cftype == 'enum'
					
				}, ...
			]
		 }
		 
		 table column data to datatableColumnMeta conversion:
		 -------------------------------------------------------
		 column type: varchar
		 {'data':'<xjc generated field name>', 'name':'<same as data>', 'title':'<display name>', 'type':'string', 'cftype':'text'}
		 
		 column type: text
		 {'data':'<xjc generated field name>', 'name':'<same as data>', 'title':'<display name>', 'type':'string', 'cftype':'text'}
		 
		 column type: int
		 {'data':'<xjc generated field name>', 'name':'<same as data>', 'title':'<display name>', 'type':'num', 'cftype':'number'}
		 
		 column type: date, datetime, timestamp
		 {'data':'<xjc generated field name>', 'name':'<same as data>', 'title':'<display name>', 'type':'date', 'cftype':'date'}
		 
		 column type: object
		 {'data':'<xjc generated field name>', 'name':'<object property, e.g. area.id>', 'title':'<display name>', 'type':'object' 'cftype':'enum'}
		 
		 
		 table column data to datatableConfig.columnDefs conversion:
		 DB_TYPE_TO_DT_CLASS[col.type] varchar, text, number
		 ---------------------------------------------------
		 column type: varchar
		 {'targets':<iterator>, 'className':'text-center text-nowrap text-capitalize'}
		 
		 column type: text
		 {'targets':<iterator>, 'className':'notes-column'}
		 
		 column type: int
		 {'targets':<iterator>, 'className':'text-center'}
		 
		 
		 table column data to formInputs conversion:
		 -------------------------------------------------------
		 form input
		 {
			 'name':col.name,
			 'input':{
				 'formGroup':<$('div.form-group.dtview-form-{col.name}')>,
				 'formInput':<the dom element>
			 }, 
			 'type':<controlType>, 
			 'required':<required>, 
			 'disabled':<disabled>
		 }
		 
		*/
		
		this.$el.append(_.template( $('#pageTemplate').html() )({}));
		
		// configure modal
		this.modalForm = $('div.edit-dtview-modal',this.$el).modal({
			'backdrop':'static',
			'keyboard':false,
			'show':false
		});
		
		// action progress panel
		this.actionProgressPanel = $('div.action-progress-panel',this.modalForm).hide();
		// progress bar
		this.progressBar = $('div.progress',this.actionProgressPanel);
		
		// generate datatableColumnMeta
		// generate columnDefs for datatableConfig
		// generate datatableConfig
		
		// pop the columns array element where the id matches options.table.primaryKey
		// ASSERTION primCol will no be undefined;
		var primCol = _.findWhere(options.table.columns, {'data':options.table.primaryKey}),
			columnDefs = [],
			tableColumnsLength = options.table.columns.length,
			defaultSpinboxOpts = {'min':0, 'max':1000, 'step':1},
			defaultDatepickerOpts = {'autoclose':true, 'format':'mm/dd/yyyy'},
			datatableview = this;
		
		this.datatableColumnMeta.push({
			'data':primCol.data, 
			'name':'editor', 
			'title':'', 
			'cfexclude':true,
			'render':function(data,type,full,meta){
				return [
					'<div class="btn-group center-block">',
						'<button type="button" class="btn btn-default btn-info btn-xs dtview-edit-btn" data-row-id="',data,'">',
							'<span class="glyphicon glyphicon-cog"></span>',
						'</button>',
						'<button type="button" class="btn btn-default btn-danger btn-xs dtview-del-btn">',
							'<span class="glyphicon glyphicon-remove"></span>',
						'</button>',
					'</div>'
				].join('');
			}
		});
		columnDefs.push({'targets':0, 'orderable':false, 'sortable':false, 'width':'35px', 'className':'action-column'});
		
		/*
		--- DataTables properties ---
		'data':string, 
		'name':string, 
		'title':string, 
		'type':string, 
		'visible':boolean,
		'render':function,
		
		--- ColumnFilters properties ---
		'cfexclude':boolean,
		'cftype':string,
		'cfenumsource':array,
		'cfenumvaluekey':string
		'cfenumlabelkey':string
		
		--- properties for ColumnFilters and form inputs ---
		'table':string
		'datasource':Bloodhound object
		'displayKey':string or function
		'valueKey':string
		
		ColumnFilter Data Object Row:
		{name:, type:, title: }
		will look for the optional properties:
		cfexclude
		cfenumsource
		cfenumlabelkey
		*/
		for(var i=0; i<tableColumnsLength; i++) {
			var col = options.table.columns[i];
			if(col.data != options.table.primaryKey) {
				// datatableColumnMeta
				var metaCol = {
					'data':col.data, // data is the key in the data row object (area)
					'name':col.name, // for data tables, name can be used for search selectors (area.id)
					'title':col.title,
					'type':this.DB_TO_DT_TYPES[col.type.toLowerCase()]
				};
				
				// DataTables properties
				if(_.has(col,'visible') && _.isBoolean(col.visible)) {
					_.extend(metaCol, {'visible':col.visible});
				} else {
					_.extend(metaCol, {'visible':true});
				}
				
				var colClassName = 'text-center';
				switch(col.type) {
					// anything short-form text
					case 'varchar'://TODO need to add clipping at nth character class
					case 'tinytext':
						colClassName = 'text-center text-nowrap text-capitalize';
						break;
					// any long text type
					case 'mediumtext':
					case 'text':
					case 'longtext':
						colClassName = 'notes-column';
						break;
				}
				// adds a column def entry
				columnDefs.push({'targets':i, 'className':colClassName});
				
				// the render data table funciton can be overridden, otherwise the defaults will be used for certain types
				if(_.has(col,'render')) {
					_.extend(metaCol, {'render':col.render});
				} else if(col.type==='object' && _.has(col,'displayKey') && _.has(col,'valueKey')) {
					// object type requires displayKey and valueKey
					_.extend(metaCol, {
						'displayKey':col.displayKey,
						'valueKey':col.valueKey,
						'render':{_:col.displayKey, 'sort':col.valueKey}
					});
					
					// check for biglist/typeahead
					if(_.has(col,'datasource')) {
						_.extend(metaCol, {'datasource':col.datasource,'table':col.table});
					}
					if(_.has(col,'table')) {
						_.extend(metaCol, {'table':col.table});
					}
				} else if(col.type==='date') {
					// dates will converted to timestamps (big-ass numbers) in CXF AJAX responses
					_.extend(metaCol, {'render': function(data,type,full,meta) {
						return _.isFinite(data) ? new Date(data).toLocaleDateString() : '';
					}});
				}
				
				
				// ColumnFilters properties
				if(_.has(col,'cfexclude') && _.isBoolean(col.cfexclude)) {
					_.extend(metaCol, {'cfexclude':col.cfexclude});
				}
				if(_.has(col,'cftype')) {
					_.extend(metaCol, {'cftype':col.cftype});
					if(col.cftype==='enum') {
						// ASSERTION cfenumsource, cfenumlabelkey will be available
						_.extend(metaCol, {'cfenumsource':col.cfenumsource, 'cfenumlabelkey':col.cfenumlabelkey});
					}
				} else {
					_.extend(metaCol, {'cftype':this.DB_TO_CF_TYPES[col.type.toLowerCase()]});
					// handle case when the type is object, but the cftype was not passed
					// type==object defaults to enum type, so an error will occur if cfenumsource and cfenumlabelkey do not exist
					if(metaCol.cftype==='enum') {
						_.extend(metaCol, {'cfenumsource':col.cfenumsource, 'cfenumvaluekey':col.cfenumvaluekey, 'cfenumlabelkey':col.cfenumlabelkey});
					}
				}
				
				this.datatableColumnMeta.push(metaCol);
				
				
				// TODO create formInputs and requiredInputNames
				// TODO create a Backbone View to handle the form
				var inpt = {
					'name':col.data,
					'input':{
						'formGroup':$('div.form-group.dtview-form-'+col.data,this.modalForm),
						'formInput':null
					}, 
					'required':true, 
					'type':col.controlType, 
					'disabled':false
				};
				
				if(_.has(col,'required') && _.isBoolean(col.required)) {
					inpt.required = col.required;
					if(col.required) {
						this.requiredInputNames.push(col.data);
					}
				}
				if(_.has(col,'disabled') && _.isBoolean(col.disabled)) {
					inpt.disabled = col.disabled;
				}
				
				switch(inpt.type) {
					case 'text':
					case 'timestamp':
						_.extend(inpt.input, {'formInput':$('input',inpt.input.formGroup)});
						break;
					case 'textarea':
						_.extend(inpt.input, {'formInput':$('textarea',inpt.input.formGroup)});
						//inpt.input.formInput = $('textarea',inpt.input.formGroup);
						break;
					case 'boolean':
						_.extend(inpt.input, {'formInput':$('label.btn', inpt.input.formGroup)});
						break;
					case 'spinbox':
						// spinbox number control
						inpt.input.formInput = $('div.spinbox',inpt.input.formGroup);
						inpt.input.formInput.spinbox(_.has(col,'config')?col.config:defaultSpinboxOpts);
						break;
					case 'datepicker':
						inpt.input.formInput = $('input',inpt.input.formGroup);
						inpt.input.formInput.datepicker(_.has(col,'config')?col.config:defaultDatepickerOpts);
						// assign any event handlers
						if(_.has(col,'events')) {
							for(var evt in col.events) {
								inpt.input.formInput.on(evt, col.events[evt]);
							}
						}
						break;
					case 'radioset':
						// for small set enum or boolean types
						// is a radioset button group
						for(var j in col.cfenumsource) {
							var enumDataItem = col.cfenumsource[j],
								isFirst = j==0;
							$('div.btn-group',inpt.input.formGroup).append([
								'<label class="btn btn-sm btn-primary',isFirst?' active':'','" data-id="',enumDataItem[col.valueKey],'">',
									'<input type="radio" value="',enumDataItem[col.valueKey],'"',isFirst?' checked="checked" ':' ','/> ',
									'<span>',typeof(col.displayKey)==='string'?enumDataItem[col.displayKey]:col.displayKey(enumDataItem),'</span>',
								'</label>'
							].join(''));
						}
						_.extend(inpt, {'displayKey':col.cfenumlabelkey, 'valueKey':col.valueKey, 'datasource':col.cfenumsource});
						inpt.input.formInput = $('label.btn',inpt.input.formGroup);//will be more than 1
						break;
					case 'dropdown':
						// for enum types
						// ASSERTION: type==object or has(cfenumsource), then has(valueKey) and has(displayKey)
						_.extend(inpt, {'displayKey':col.displayKey, 'valueKey':col.valueKey, 'datasource':col.cfenumsource});
						inpt.input.formInput = $('input',inpt.input.formGroup);//will be more than 1 (hidden and text)
						//loop through cfenumsource array
						for(var j in col.cfenumsource) {
							var enumDataItem = col.cfenumsource[j];
							$('ul.dropdown-menu',inpt.input.formGroup).append([
								'<li class="dropdown-menu-item-faux-a" data-id="',enumDataItem[col.valueKey],
								'" data-key="',col.data,
								'" data-label="',typeof(col.displayKey)==='string'?enumDataItem[col.displayKey]:col.displayKey(enumDataItem),'">',
								'<a>',
								typeof(col.displayKey)==='string'?enumDataItem[col.displayKey]:col.displayKey(enumDataItem),
								'</a></li>'
							].join(''));
						}
						break;
					case 'typeahead':
						// typeahead input element
						_.extend(inpt, {'value':null, 'displayKey':col.displayKey, 'valueKey':col.valueKey});
						inpt.input.formInput = $('input:text.typeahead',inpt.input.formGroup);
						inpt.input.formInput.typeahead(
							{highlight:false, hint:false, minLength:3},
							{
								'name':inpt.name,
								'displayKey':col.displayKey,
								'source':col.datasource.ttAdapter()
							}
						);
						break;
				}
				
				this.formInputs.push(inpt);
			}
		}
		
		//set initial data table configuration
		this.datatableConfig = {
			'exteriorController':datatableview,
			'searching':false,
			'scrollX':true,
			'scrollY':'400px',
			'scrollCollapse':true,
			'dom':[
				'<"panel panel-default"',
					'<"panel-heading clearfix"',
						'<"refresh-datatable-btn-container pull-left">',// the refresh table button
							'l',// the number of results per page drop down
							'<"column-visibility-container">',// 
							'<"add-button-container">',// the add row button
							'p',// the pagination control
					'>',
				'>',
				'tpi<"clearfix">'].join(''),
			'processing':true,
			'serverSide':true,
			'ajax':{
				'context':datatableview,
				'url':[this.cfWebServiceUrl,'data'].join('/'),
				'type':'POST',
				'dataType':'json',
				'contentType':'application/json',
				'data':function(d){
					if(this) {//ASSERTION: dtView is this DataTableView instance in window scope
						var cf = datatableview.columnFilters.getCurrentFilter();
						if(cf) {
							d.columnfilters = cf;
						}
					}
					d.table = datatableview.dataTypeName.SHORT;
					return JSON.stringify(d);
				},
				'processData':false
			},
			'order':_.has(options,'defaultColumnOrder')?options.defaultColumnOrder:[[1,'asc']],
			'columnDefs':columnDefs,
			'columns':this.datatableColumnMeta
		};
		
		//create column filters
		var cfOptions = {
			'webServiceUrl':this.cfWebServiceUrl,
			'filterCategories':[],
			'table':options.table.name,
			'tableColumns':this.datatableColumnMeta
		};
		if(_.has(options,'mode')) {
			_.extend(cfOptions,{'mode':options.mode});
		}
		if(_.has(options,'filterCategories')) {
			_.extend(cfOptions,{'filterCategories':options.filterCategories});
		}
		if(_.has(options,'filters')) {
			_.extend(cfOptions,{'filters':options.filters});
		}
		this.columnFilters = new VDataFilters(cfOptions);
		
		//create data table
		this.datatable = $('table.table',this.$el).DataTable(this.datatableConfig);
		
		// TODO this causes the edit button to fail, actually it's the datatable .row(<tr>) api
		//		call that fails, so for now disable it.
		// apply fixed column extension
		//new $.fn.dataTable.FixedColumns(this.datatable);
	},
	
	'render':function() { return this; }
});