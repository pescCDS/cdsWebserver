'use strict';

$(document).ready(function() {
	
	// add button click event handler for the directory server API calls
	$('button.dirServerButton').click(function(e) {
		var type = $(e.currentTarget).data('get-type'),
			outputContainer = $(['samp[data-set-type="',type,'"]'].join(''));
		outputContainer.val(null);
		$.ajax(
			[directoryServer,'EdExchange/services/rest',type].join('/')
		).done(function(data,textStatus,jqXHR){
			console.log(data);
			outputContainer.html(JSON.stringify(data));
		}).error(function(jqXHR, textStatus, errorThrown) {
			console.error(errorThrown);
			console.error(textStatus);
			outputContainer.html(errorThrown);
		});
	});
	
	// configure the transaction history UI controls
	$('.date').datepicker({'autoclose':true, 'format':'m/d/yyyy'});
	$('button.getHistoryButton').click(function(e) {
		var fromDate = $('input[name="tx-history-from"]').datepicker('getDate'),
			toDate = $('input[name="tx-history-to"]').datepicker('getDate');
		$.ajax({
			'url':'getTransactions',
			'data':{
				'status':($('input[name="tx-history-status"]:checked').val()*1)?true:false,
				'from':isNaN(fromDate.getTime())?null:fromDate.getTime(),
				'to':isNaN()?null:toDate.getTime(),
				'fetchSize':$('input[name="tx-history-fetchSize"]').val()
			}
		}).done(function(data, textStatus, jqxhr) {
			console.log(data);
			$('textarea.txHistoryResponse').val(JSON.stringify(data));
		}).fail(function(jqxhr, textStatus, errorThrown) {
			console.error(errorThrown);
			$('textarea.txHistoryResponse').val(errorThrown);
		});
	});
	
	// datatable for transaction history
	$('table.xaction-history-table').DataTable({
		'searching':false,
		'scrollX':true,
		'scrollY':'400px',
		'scrollCollapse':true,
		'dom':'ti<"clearfix">',
		'order':[[1,'asc']]
	});
	
	// Delivery Options Search
	function sendDeliveryOptionData(data, sendType) {
		var ajaxConfig = {
			'url':'/EdExchange/services/rest/deliveryOptions/search',
			'dataType':'json'
		};
		
		if(sendType) {
			_.extend(ajaxConfig, {'type':'POST', 'contentType':'application/json', 'processData':false, 'data':JSON.stringify(data)});
		} else {
			_.extend(ajaxConfig, {'data':data});
		}
		
		$.ajax(ajaxConfig).done(function(data, textStatus, jqxhr) {
			console.log(data);
			$('.get-results-table-container').empty().append(_.template([
				'<table class="table table-striped table-condensed">',
					'<thead>',
						'<tr>',
							'<th>id</th>',
							'<th>memberId</th>',
							'<th>formatId</th>',
							'<th>webserviceUrl</th>',
							'<th>deliveryMethodId</th>',
							'<th>deliveryConfirm</th>',
							'<th>error</th>',
							'<th>operationalStatus</th>',
						'</tr>',
						'<% for(var i in deliveryOptions) { %>',
							'<tr>',
								'<td><%= deliveryOptions[i].id %></td>',
								'<td><%= deliveryOptions[i].member.directoryId %></td>',
								'<td><%= deliveryOptions[i].format.id %></td>',
								'<td><%= deliveryOptions[i].webserviceUrl %></td>',
								'<td><%= deliveryOptions[i].deliveryMethod.id %></td>',
								'<td><%= deliveryOptions[i].deliveryConfirm %></td>',
								'<td><%= deliveryOptions[i].error %></td>',
								'<td><%= deliveryOptions[i].operationalStatus %></td>',
							'</tr>',
						'<% } %>',
					'</thead>',
				'</table>'
			].join(''), {'variable':'deliveryOptions'})(data));
			
		}).fail(function(jqxhr, textStatus, errorThrown) {
			console.error(errorThrown);
		});
	}
	
	$('form.doptForm button.reset-button').click(function(e) {
		$('form.doptForm')[0].reset();
	});
	
	$('form.doptForm button.search-button').click(function(e) {
		// gather data
		var data = {},
			SEND_TYPE = $('input[name="sendType"]:checked', $('form.doptForm')).val()==='GET'?0:1,
			inpt_id = $('input[name="id"]', $('form.doptForm')),
			inpt_memberId = $('input[name="memberId"]', $('form.doptForm')),
			inpt_formatId = $('input[name="formatId"]', $('form.doptForm')),
			inpt_webserviceUrl = $('input[name="webserviceUrl"]', $('form.doptForm')),
			inpt_deliveryMethodId = $('input[name="deliveryMethodId"]', $('form.doptForm')),
			inpt_deliveryConfirm = $('input[name="deliveryConfirm"]:checked', $('form.doptForm')).val(),
			inpt_error = $('input[name="error"]:checked', $('form.doptForm')).val(),
			inpt_operationalStatus = $('input[name="operationalStatus"]', $('form.doptForm'));
		if($.trim(inpt_id.val()).length && !_.isNaN(inpt_id.val()*1)) {
			data.id = $.trim(inpt_id.val())*1;
		}
		if($.trim(inpt_memberId.val()).length && !_.isNaN(inpt_memberId.val()*1)) {
			data.memberId = $.trim(inpt_memberId.val())*1;
		}
		if($.trim(inpt_formatId.val()).length && !_.isNaN(inpt_formatId.val()*1)) {
			data.formatId = $.trim(inpt_formatId.val())*1;
		}
		if($.trim(inpt_webserviceUrl.val()).length) {
			data.webserviceUrl = $.trim(inpt_webserviceUrl.val());
		}
		if($.trim(inpt_deliveryMethodId.val()).length && !_.isNaN(inpt_deliveryMethodId.val()*1)) {
			data.deliveryMethodId = $.trim(inpt_deliveryMethodId.val())*1;
		}
		if(inpt_deliveryConfirm) {
			//data.deliveryConfirm = SEND_TYPE ? (inpt_deliveryConfirm==='1') : inpt_deliveryConfirm*1;
			data.deliveryConfirm = inpt_deliveryConfirm==='1';
		}
		if(inpt_error) {
			//data.error = SEND_TYPE ? (inpt_error==='1') : inpt_error*1;
			data.error = inpt_error==='1';
		}
		if($.trim(inpt_operationalStatus.val()).length) {
			data.operationalStatus = $.trim(inpt_operationalStatus.val());
		}
		
		if(!_.isEmpty(data)) {
			sendDeliveryOptionData(data, SEND_TYPE);
		} else {
			alert('At least 1 input must have a value.');
		}
	});
});