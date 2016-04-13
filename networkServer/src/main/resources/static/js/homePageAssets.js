'use strict';
var dt = null;

$(document).ready(function() {
	//configure the transaction history UI controls
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
			//console.log(data);
			// reset data table with incoming data
			dt.clear();
			dt.rows.add(data).draw();
			
		}).fail(function(jqxhr, textStatus, errorThrown) {
			console.error(errorThrown);
			$('textarea.txHistoryResponse').val(errorThrown);
		});
	});
	
	// datatable for transaction history
	dt = $('table.xaction-history-table').DataTable({
		'searching':false,
		'scrollX':true,
		'scrollY':'400px',
		'scrollCollapse':true,
		'dom':'ti<"clearfix">',
		'columns':[
		    {'data':'id', 'title':'ID', 'type':'num', 'visible':false},
		    {'data':'recipientId', 'title':'Recipient ID', 'type':'string'},
		    {'data':'networkServerId', 'title':'Network Server ID', 'type':'string'},
		    {'data':'senderId', 'title':'Sender ID', 'type':'string'},
		    {'data':'direction', 'title':'Direction', 'type':'string'},
		    {'data':'fileFormat', 'title':'File Format', 'type':'string'},
		    {'data':'fileSize', 'title':'File Size', 'type':'num'},
		    {'data':'filePath', 'title':'File Path', 'type':'string'},
		    {
		    	'data':'sent', 
		    	'title':'Sent', 
		    	'type':'date',
		    	'render':function(data, type, row, meta) {
		    		if(data) {
		    			return moment.utc(data).format('M/D/YYYY');
		    		} else return '';
		    	}
		    },
		    {
		    	'data':'received', 
		    	'title':'Received', 
		    	'type':'date',
				'render':function(data, type, row, meta) {
					if(data) {
		    			return moment.utc(data).format('M/D/YYYY');
		    		} else return '';
		    	}
		    },
		    {'data':'error', 'title':'Error', 'type':'string'},
		    {'data':'status', 'title':'Status', 'type':'string'}
		],
		'order':[[1,'asc']],
		'initComplete':function(settings, json) {
			$('button.getHistoryButton').click();
		}
	});
});