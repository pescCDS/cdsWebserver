'use strict';
var transferFormView = Backbone.View.extend({
	
	'events':{
		'submit form':function(e) {
			console.log('form has been submitted');
			
			// if the recipientId input is populated then get the webservice url from model.deliveryOptions
			var rid = $.trim($('form input[name="recipientId"]', this.$el).val());
			console.log(rid);
			if(rid.length) {
				var dopt = null, memberId = rid*1;
				for(var i in this.model.get('deliveryOptions')) {
					if(this.model.get('deliveryOptions')[i].member.directoryId===memberId && this.model.get('deliveryOptions')[i].webserviceUrl!=null) {
						dopt = this.model.get('deliveryOptions')[i];
					}
				}
				console.log(dopt);
				if(dopt) {
					// update hidden value for webServiceUrl
					console.log(dopt.webserviceUrl);
					$('form input[name="webServiceUrl"]', this.$el).val(dopt.webserviceUrl);
					
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	},
	
	'initialize':function(options) {
		// deliveryOptions is required
		this.model = new Backbone.Model({
			'deliveryOptions':options.deliveryOptions
		});
	}
});

$(document).ready(function() {
	// populate delivery options for sending
	$.ajax(
		[directoryServer,'services/rest/deliveryOptions'].join('/')
	).done(function(data,textStatus,jqXHR){
		new transferFormView({'el':$('#transferForm'), 'deliveryOptions':data})
	}).error(function(jqXHR, textStatus, errorThrown) {
		console.error(errorThrown);
		console.error(textStatus);
	});
});