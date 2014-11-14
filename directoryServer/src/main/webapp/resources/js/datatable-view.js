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
		'timestamp':'date'
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
		'timestamp':'date'
	},
	
	'webServiceUrl':'/services/rest',
	
	'columnFilters':null,
	'modalForm':null,
	'actionProgressPanel':null,
	'progressBar':null,
	'datatable':null,
	'datatableColumnMeta':[],
	'datatableConfig':{},
	
	'formInputs':[],
	
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
		
		// change the panel coloring
		$('div.action-progress-panel div.panel',this.modalForm).removeClass('panel-danger');
		$('div.action-progress-panel .progress-bar',this.modalForm).removeClass('progress-bar-danger');
		
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
			} else if(inpt.type==='spinbox') {
				inpt.input.formInput.spinbox('value',0);
			}
			
			if(inpt.required) {
				inpt.input.formGroup.removeClass('has-success has-error');
				$('span.glyphicon',inpt.input.formGroup).removeClass('glyphicon-ok glyphicon-remove').addClass('hidden');
			}
		}
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
			var inpt = requiredInputs[i];
			if(inpt.type==='text' || inpt.type==='textarea') {
				var inptVal = inpt.input.formInput.val(),
					isValid = (inptVal.length > 1);
				if(isValid) {
					// valid input
					validInputCount++;
					returnData[inpt.name] = $.trim(inptVal);
				}
				this.displayInputValidation(inpt, isValid);
			} else if(inpt.type==='spinbox') {
				//spinboxes always return a number
				validInputCount++;
				returnData[inpt.name] = inpt.input.formInput.spinbox('value');
				this.displayInputValidation(inpt, true);
			}
		}
		
		//optional inputs
		for(var j in optionalInputs) {
			var inpt = optionalInputs[j];
			if(inpt.type==='text' || inpt.type==='textarea') {
				var inptVal = inpt.input.formInput.val(),
					isValid = (inptVal.length > 1);
				if(isValid) {
					// valid input
					returnData[inpt.name] = $.trim(inptVal);
				}
			} else if(inpt.type==='spinbox') {
				returnData[inpt.name] = inpt.input.formInput.spinbox('value');
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
			} else if(inpt.type==='spinbox' && _.isFinite(dtData[inpt.name])) {
				inpt.input.formInput.spinbox('value',dtData[inpt.name])
			}
		}
		
		// hidden input primary key
		$('#'+this.sourceData.primaryKey,this.modalForm).val(dtData[this.sourceData.primaryKey]);
	},
	
	
	'className':'data-table-view',
	'events':{
		// DATATABLE INIT COMPLETE
		'init.dt .dataTables_wrapper':function(e, settings, json) {
			// create refresh data table button
			$('div.refresh-datatable-btn-container',this.$el).append([
				'<button type="button" class="btn btn-default" title="refresh data">',
					'<span class="glyphicon glyphicon-refresh"></span>',
				'</button>'
			].join(''));
			 
			$('div.add-button-container',this.$el).append(
				['<button type="button" class="btn btn-success pull-left">Add ','</button>'].join(this.dataTypeName.LONG)
			);
			
			this.$el.prepend(this.columnFilters.$el);
		},
		
		// REFRESH DATATABLE
		'click div.refresh-datatable-btn-container':function(e) {
			this.datatable.ajax.reload();
		},
		
		// ADD CLICK
		'click div.add-button-container button':function(e) {
			$('h4.modal-title',this.modalForm).html('Create '+this.dataTypeName.LONG);
			$('button.dtview-modal-form-action-button',this.modalForm).html('Create');
			this.modalForm.modal('show');
		},
		
		// EDIT CLICK
		'click button.dtview-edit-btn':function(e) {
			var d = this.datatable.row($(e.currentTarget).parent().parent().parent()[0]).data();
			if(d) {
				$('h4.modal-title',this.modalForm).html('Edit Document Format');
				$('button.dtview-modal-form-action-button',this.modalForm).html('Save');
				
				//populate modal form with data and then display it
				this.setFormData(d);
				this.modalForm.modal('show');
			}
		},
		
		// REMOVE CLICK
		'click button.dtview-del-btn':function(e) {
			//get the index of the data tables row
			
			if(confirm(['Are you sure you want to remove this ','?'].join(this.dataTypeName.LONG))) {
				var d = this.datatable.row($(e.currentTarget).parent().parent().parent()[0]).data();
				$.ajax({
					'url':[this.webServiceUrl,this.dataTypeName.SHORT,'remove'].join('/'),
					type:'POST',
					contentType:'application/json',
					dataType:'json',
					data:JSON.stringify(d),
					processData:false
				}).done(function(data,textStatus,jqXHR) {
					dtView.datatable.ajax.reload();
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
					'url':[this.webServiceUrl,this.dataTypeName.SHORT,'save'].join('/'),
					type:'POST',
					contentType:'application/json',
					dataType:'json',
					data:JSON.stringify(fd),
					processData:false
				}).done(function(data,textStatus,jqXHR) {
					dtView.datatable.ajax.reload();
					dtView.modalForm.modal('hide');
				}).fail(function(jqXHR,textStatus,errorThrown) {
					// change the panel coloring to red
					$('div.action-progress-panel div.panel',this.modalForm).addClass('panel-danger');
					$('div.action-progress-panel .progress-bar',this.modalForm).addClass('progress-bar-danger');
					
					// display error message in modal progress panel body 
					$('div.edit-dtview-modal div.action-progress-panel div.panel-body').empty().append(
						[
							'<p>',errorThrown,'</p>',
							'<div class="pull-right clearfix">',
								'<button type="button" class="btn btn-danger">OK</button>',
							'</div>'
						].join('')
					);
				});
			}
		},
		
		// PROCESS ERROR MESSAGE BUTTON CLICK
		'click div.action-progress-panel div.panel-body button':function(e) {
			dtView.modalForm.modal('hide');
		},
		
		// EDIT MODAL HIDE
		'hidden.bs.modal div.edit-dtview-modal':function() {
			this.resetForm();
			this.hideProgress();
		}
	},
	'initialize':function(options) {
		// ASSERTION: options has these required properties: table, webServiceUrl
		this.sourceData = options.table;
		this.webServiceUrl = options.webServiceUrl;
		
		//populate data type names
		this.dataTypeName.SHORT = options.table.name;
		this.dataTypeName.LONG = options.dataClass;
		
		/*
		TODO populate formInputs from options.table
		TODO populate requiredInputNames from formInputs
		
		TODO datatableColumnMeta will be constructed using options.table properties 
		 options.table = {
			name: a key string that TableClassMap.getClass() would return a class with,
			primaryKey: a string that should match one of the columns[n].id, used for the edit/remove function
			columns:[
				{
					--- required ---
					id:		<database column name>
					name:	<xjc generated field name>
					title:	descriptive name for column, default=this.name
					type:	<database column type>, will be put through DB_TO_DT_TYPES (e.g. varchar, text, int, date, ...)
					
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
					
					[any extra column filters properties (e.g. cfexclude, cftype, cfenumsource, cfenumlabelkey)
					cfexclude:	default=false, if you don't want to ColumnFilters to use this column
					// when cftype is present then it overrides the default mapping with DB_TO_CF_TYPES
					cftype:		if present, then this value will be used instead of the default DB_TO_CF_TYPES[this.type]
					cfenumsource, cfenumlabelkey: onlu used when cftype == 'enum'
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
		console.log('this should only display on datatable pages');
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
		var primCol = _.findWhere(options.table.columns, {'id':options.table.primaryKey}),
			columnDefs = [],
			tableColumnsLength = options.table.columns.length,
			defaultSpinboxOpts = {'min':0, 'max':1000, 'step':1},
			defaultDatepickerOpts = {'autoclose':true, 'format':'mm/dd/yyyy'},
			datatableview = this;
		
		this.datatableColumnMeta.push({
			'data':primCol.id, 
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
		columnDefs.push({'targets':0, 'orderable':false, 'width':'35px', 'className':'action-column'});
		
		for(var i=0; i<tableColumnsLength; i++) {
			var col = options.table.columns[i];
			if(col.id != options.table.primaryKey) {
				// datatableColumnMeta
				var metaCol = {
					'data':col.name,
					'name':col.name,
					'title':col.title,
					'type':this.DB_TO_DT_TYPES[col.type.toLowerCase()]
				};
				
				// DataTables properties
				if(_.has(col,'visible') && _.isBoolean(col.visible)) {
					_.extend(metaCol, {'visible':col.visible});
				} else {
					_.extend(metaCol, {'visible':true});
				}
				
				if(metaCol.visible) {
					var colClassName = 'text-center';
					switch(col.type) {
						// anything short-form text
						case 'varchar'://TODO need to add clipping at nth character class
							colClassName = 'text-center text-nowrap text-capitalize';
							break;
						//any long text type
						case 'tinytext':
						case 'mediumtext':
						case 'text':
						case 'longtext':
							colClassName = 'notes-column';
							break;
					}
					columnDefs.push({'targets':i, 'className':colClassName});
					
					if(_.has(col,'render')) {
						_.extend(metaCol, {'render':col.render});
					}
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
				}
				
				this.datatableColumnMeta.push(metaCol);
				
				
				// TODO create formInputs and requiredInputNames
				// TODO create a Backbone View to handle the form
				var inpt = {
					'name':col.name,
					'input':{
						'formGroup':$('div.form-group.dtview-form-'+col.name,this.modalForm),
						'formInput':null
					}, 
					'required':true, 
					'type':col.controlType, 
					'disabled':false
				};
				
				if(_.has(col,'required') && _.isBoolean(col.required)) {
					inpt.required = col.required;
					if(col.required) {
						this.requiredInputNames.push(col.name);
					}
				}
				if(_.has(col,'disabled') && _.isBoolean(col.disabled)) {
					inpt.disabled = col.disabled;
				}
				
				switch(inpt.type) {
					case 'text':
						_.extend(inpt.input, {'formInput':$('input',inpt.input.formGroup)});
						//inpt.input.formInput = $('input',inpt.input.formGroup);
						break;
					case 'textarea':
						_.extend(inpt.input, {'formInput':$('textarea',inpt.input.formGroup)});
						//inpt.input.formInput = $('textarea',inpt.input.formGroup);
						break;
					case 'spinbox':
						// spinbox number control
						inpt.input.formInput = $('div.spinbox',inpt.input.formGroup);
						inpt.input.formInput.spinbox(_.has(col,'config')?col.config:defaultSpinboxOpts);
						break;
					case 'datepicker':
						inpt.input.formInput = $('input',inpt.input.formGroup);
						inpt.input.formInput.datepicker(_.has(col,'config')?col.config:defaultDatepickerOpts);
						break;
					case 'radioset':
						// for small set enum or boolean types
						// TODO
						
						break;
					case 'dropdown':
						// for enum types
						// ASSERTION: cfenumsource, dropdownSourceValueKey and dropdownSourceDisplayKey are available
						inpt.input.formInput = $('input',inpt.input.formGroup);//will be more than 1 (hidden and text)
						//loop through cfenumsource array
						for(var j in col.cfenumsource) {
							var enumDataItem = col.cfenumsource[j];
							$('ul.dropdown-menu',inpt.input.formGroup).append([
								'<li data-id="',enumDataItem[col.dropdownSourceValueKey],
								'" data-label="',enumDataItem[col.dropdownSourceDisplayKey],'">',
								'<a href="#">',
								col.cfenumsource[i][col.dropdownSourceDisplayKey],
								'</a></li>'
							].join(''));
						}
						break;
				}
				
				this.formInputs.push(inpt);
			}
		}
		
		//set initial data table configuration
		this.datatableConfig = {
			'exteriorController':datatableview,
			'searching':false,
			'dom':'<"panel panel-default"<"panel-heading clearfix"<"refresh-datatable-btn-container pull-left">l<"add-button-container">p>>tpi<"clearfix">',
			'processing':true,
			'serverSide':true,
			'ajax':{
				'context':datatableview,
				'url':[this.webServiceUrl,'data'].join('/'),
				'type':'POST',
				'dataType':'json',
				'contentType':'application/json',
				'data':function(d){
					if(this) {//ASSERTION: dtView is this DataTableView instance in window scope
						var cf = datatableview.columnFilters.getCurrentFilter();
						if(cf) {
							d.table = datatableview.dataTypeName.SHORT;
							d.columnfilters = cf;
						} else {
							d.table = datatableview.dataTypeName.SHORT;
						}
					}
					d.table = datatableview.dataTypeName.SHORT;
					return JSON.stringify(d);
				}
			},
			'order':_.has(options,'defaultColumnOrder')?options.defaultColumnOrder:[[1,'asc']],
			'columnDefs':columnDefs,
			'columns':this.datatableColumnMeta
		};
		
		//create column filters
		this.columnFilters = new VDataFilters({'table':options.table.name, 'tableColumns':this.datatableColumnMeta});
		
		//create data table
		this.datatable = $('table.table',this.$el).DataTable(this.datatableConfig);
	},
	
	'render':function() { return this; }
});