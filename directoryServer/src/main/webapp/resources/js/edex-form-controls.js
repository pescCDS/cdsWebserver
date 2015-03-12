var DropdownSelectInputControl = Backbone.View.extend({
	'getDisplayValue':function() {
		return typeof(this.model.get('displayKey'))==='function'
			? this.model.get('displayKey')(this.model.get('dataset')[this.model.get('selectedIndex')])
			: this.model.get('dataset')[this.model.get('selectedIndex')][this.model.get('displayKey')];
	},
	'changeSelectedIndex':function(newIndex) {
		this.model.set('selectedIndex', newIndex);
		$('button.cdd-label', this.$el).html(this.getDisplayValue());
	},
	
	'get':function() {
		//var r = {};
		//r[this.model.get('inputName')] = this.model.get('dataset')[this.model.get('selectedIndex')];
		//return r;
		var retVal = this.model.get('dataset')[this.model.get('selectedIndex')];
		
		return this.model.get('valueOnly') ? retVal[this.model.get('valueKey')] : retVal;
		
	},
	'set':function(data) {
		// the value object we want will be a property of data
		if( _.has(data, this.model.get('inputName')) ) {
			var idx;
			if(this.model.get('valueOnly')) {
				var kv = {};
				kv[this.model.get('valueKey')] = data[this.model.get('inputName')];
				idx = _.findIndex(this.model.get('dataset'), kv);
			} else {
				idx = _.findIndex(this.model.get('dataset'), data[this.model.get('inputName')]);
			}
			if(idx>-1) {
				this.changeSelectedIndex(idx);
			}
		}
	},
	'reset':function() {
		this.model.set('selectedIndex', 0);
		this.changeSelectedIndex(0);
	},
	'template':_.template([
		'<label class="control-label text-nowrap"><%= cData.label %></label><br />',
		'<div class="btn-group">',
			'<button type="button" class="btn btn-sm btn-default cdd-label">',
				'<%= typeof(cData.displayKey)==="function"?cData.displayKey(cData.dataset[cData.selectedIndex]):cData.dataset[cData.selectedIndex][cData.displayKey] %>',
			'</button>',
			'<button type="button" class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown">',
				'<span class="caret"></span>',
			'</button>',
			'<ul class="dropdown-menu scroll-list" role="menu">',
				'<% for(var i in cData.dataset) { %>',
					'<li data-index="<%= i %>"><a href="#">',
						'<%= typeof(cData.displayKey)==="function"?cData.displayKey(cData.dataset[i]):cData.dataset[i][cData.displayKey] %>',
					'</a></li>',
				'<% } %>',
			'</ul>',
		'</div>'
	].join(''),{'variable':'cData'}),
	'events':{
		'click ul.dropdown-menu li':function(e) {
			this.changeSelectedIndex($(e.currentTarget).data('index')*1);
		}
	},
	'className':'text-center',
	'initialize':function(options) {
		// required: dataset, inputName, displayKey, label
		// displayKey and valueKey can be a string or function
		this.model = new Backbone.Model({
			'dataset':options.dataset,
			'inputName':options.inputName,
			'displayKey':options.displayKey,
			'label':options.label,
			'valueOnly':false,
			'selectedIndex':0
		});
		if(_.has(options, 'selectedIndex')) {
			this.model.set('selectedIndex', options.selectedIndex);
		}
		
		// ASSERTION: options.valueKey must be provided if options.valueOnly == true
		if(_.has(options, 'valueOnly')) {
			this.model.set('valueOnly', options.valueOnly);
			if(this.model.get('valueOnly')) {
				this.model.set('valueKey', options.valueKey);
			}
		}
	},
	'render':function() {
		this.$el.empty().append(this.template(this.model.toJSON()));
		return this.$el;
	}
});