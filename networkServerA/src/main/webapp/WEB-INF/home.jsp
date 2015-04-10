<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
	<title><fmt:message key="home.title"/></title>
	<meta http-equiv="Content-Type" charset="utf-8" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="shortcut icon" type="image/png" href="resources/images/edex-favicon-16.png" sizes="16x16" />
    <link rel="shortcut icon" type="image/png" href="resources/images/edex-favicon-24.png" sizes="24x24" />
    <link rel="shortcut icon" type="image/png" href="resources/images/edex-favicon-32.png" sizes="32x32" />
    <link rel="shortcut icon" type="image/png" href="resources/images/edex-favicon-48.png" sizes="48x48" />
    <link rel="shortcut icon" type="image/png" href="resources/images/edex-favicon-64.png" sizes="64x64" />
    <link rel="stylesheet" href="resources/css/bootstrap.min.css" />
    <link rel="stylesheet" href="resources/css/bootstrap-theme.min.css" />
    <link rel="stylesheet" href="resources/css/dataTables.bootstrap.css" />
    <link rel="stylesheet" href="resources/css/datepicker.css" />
    <link rel="stylesheet" href="resources/css/datepicker3.css" />
    <link rel="stylesheet" href="resources/css/fuelux.min.css" />
    <link rel="stylesheet" href="resources/css/typeahead-ext.css" />
    <style>
	ul.extra-space li {
		padding:5px;
	}
	.full-width {
		width:100%;
	}
	</style>
</head>
<body>
	<div class="container">  
		<h2>Network Server</h2>
        
        <hr />
        <h3>Send A Transcript File</h3>
        <form action="sendFile" method="post" enctype="multipart/form-data" accept-charset="utf-8">
            <c:if test="${error}">
	           <p class="bg-danger">${error}</p>
            </c:if>
            <c:if test="${status}">
            	<p class="bg-info">${status}</p>
            </c:if>
            <div class="form-horizontal row">
            	<div class="form-group">
                	<label class="control-label col-lg-2 col-md-2 col-sm-4 col-xs-4">Recipient</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                        <input type="text" name="recipientId" class="form-control" placeholder="Where is this file going?" />
                    </div>
                    
                    <label class="control-label col-lg-2 col-md-2 col-sm-4 col-xs-4">Network Server</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                        <input type="text" name="networkServerId" class="form-control" placeholder="This should be assigned by the web application" />
                    </div>
                </div>
            </div>
            <div class="form-horizontal row">
            	<div class="form-group">
                	<label class="control-label col-lg-2 col-md-2 col-sm-4 col-xs-4">Sender</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                    	<input type="text" name="senderId" class="form-control" placeholder="This might be different than this network server" />
                    </div>
                    
                    <label class="control-label col-lg-2 col-md-2 col-sm-4 col-xs-4">Transcript File</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                    	<input type="file" name="file" />
                    </div>
                </div>
            </div>
            <div class="form-horizontal row">
            	<div class="form-group">
                	<label class="control-label col-lg-2 col-lg-offset-6 col-md-4 col-md-offset-4 col-sm-4 col-xs-4">File Format</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                    	<input type="text" name="fileFormat" class="form-control" placeholder="What type of file to transfer" />
                    </div>
                </div>
            </div>
            <div class="form-horizontal row">
            	<div class="form-group text-center">
                	<button type="submit" class="btn btn-default">SEND</button>
                </div>
            </div>
        </form>
        
        <br /><hr />
        <h3>Transaction History</h3>
        <table class="table table-striped table-bordered table-condensed table-hover">
        	<thead>
        		<tr>
        			<th>Recipient ID</th>
        			<th>Network Server ID</th>
        			<th>Sender ID</th>
        			<th>Direction</th>
        			<th>File Format</th>
        			<th>File Size</th>
        			<th>Sent</th>
        			<th>Received</th>
        			<th>Error</th>
        			<th>Status</th>
        		</tr>
        	</thead>
        	<tbody>
        		<c:forEach items="${transactions}" var="tx">
        		<tr>
        			<td>${tx.recipientId}</td>
        			<td>${tx.networkServerId}</td>
        			<td>${tx.senderId}</td>
        			<td>${tx.direction}</td>
        			<td>${tx.fileFormat}</td>
        			<td>${tx.fileSize}</td>
        			<td>${tx.sent}</td>
        			<td>${tx.received}</td>
        			<td>${tx.error}</td>
        			<td>${tx.status}</td>
        		</tr>
        		</c:forEach>
        	</tbody>
        </table>
        
        <br /><hr />
        <h3>Transaction History REST API</h3>
        <div class="form-horizontal">
        	<div class="form-group">
        		<label class="control-label col-sm-2">Status</label>
        		<div class="btn-group col-sm-10" data-toggle="buttons">
        			<label class="btn btn-primary active">
        				<input type="radio" name="tx-history-status" autocomplete="off" value="0" checked /> incomplete
        			</label>
        			<label class="btn btn-primary">
        				<input type="radio" name="tx-history-status" autocomplete="off" value="1" /> complete
        			</label>
        		</div>
        	</div>
        	<div class="form-group">
        		<label class="control-label col-sm-2">From / To</label>
        		<div class="input-daterange input-group date">
        			<input type="text" class="form-control" name="tx-history-from" />
        			<span class="input-group-addon">to</span>
        			<input type="text" class="form-control" name="tx-history-to" />
        		</div>
        	</div>
        	<div class="form-group">
        		<label class="control-label col-sm-2">Fetch Size</label>
        		<div class="col-sm-3">
        			<input type="number" class="form-control tx-history-fetchSize" min="1" max="1000" size="4" />
        		</div>
        	</div>
        </div>
        <div class="row">
        	<div class="col-lg-2 col-md-3 col-sm-4 col-xs-5">
        		<button type="button" class="btn btn-default getHistoryButton">Get Transactions</button>
        	</div>
        	<div class="col-lg-10 col-md-9 col-sm-8 col-xs-7">
        		<textarea rows="10" autocomplete="off" class="full-width txHistoryResponse"></textarea>
        	</div>
        </div>
        
        <br /><hr />
        <h3>Directory Server Communication REST API</h3>
        <div class="row">
        	<div class="col-lg-6 col-md-6 col-sm-6">
                <ul class="list-unstyled extra-space">
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="deliveryOptions">Get Delivery Options</button></li>
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="organizations">Get Organizations</button></li>
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="contacts">Get Contacts</button></li>
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="deliveryMethods">Get Delivery Methods</button></li>
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="documentFormats">Get Document Formats</button></li>
                    <li><button type="button" class="btn btn-default dirServerButton" data-get-type="entityCodes">Get Entity Codes</button></li>
                </ul>
            </div>
           	<div class="col-lg-6 col-md-6 col-sm-6">
        		<textarea rows="10" autocomplete="off" class="full-width dirServerResponse"></textarea>
            </div>
		</div>
	</div>
    
    <script src="resources/js/jquery-2.1.1.min.js"></script>
	<script src="resources/js/underscore-min.js"></script>
    <script src="resources/js/backbone-min.js"></script>
    <script src="resources/js/bootstrap.min.js"></script>
    <script src="resources/js/moment.min.js"></script>
    <script src="resources/js/typeahead.bundle.js"></script>
    <script src="resources/js/jquery.dataTables.min.js"></script>
    <script src="resources/js/dataTables.bootstrap.js"></script>
    <script src="resources/js/bootstrap-datepicker.js"></script>
    <script src="resources/js/spinbox.js"></script>
<script>

// http://localhost:8080
// http://pesc.cccnext.net
var directoryServer = 'http://pesc.cccnext.net';
$(document).ready(function() {
	
	// add button click event handler for the directory server API calls
	$('button.dirServerButton').click(function(e) {
		var type = $(e.currentTarget).data('get-type');
		$('textarea.dirServerResponse').val(null);
		$.ajax(
			[directoryServer,'EdExchange/services/rest',type].join('/')
		).done(function(data,textStatus,jqXHR){
			console.log(data);
			$('textarea.dirServerResponse').val(JSON.stringify(data));
		}).error(function(jqXHR, textStatus, errorThrown) {
			console.error(errorThrown);
			console.error(textStatus);
			$('textarea.dirServerResponse').val(errorThrown);
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
				'status':($('input[name="tx-history-status"]').val()*1)?true:false,
				'from':isNaN(fromDate.getTime())?null:fromDate.getTime(),
				'to':isNaN()?null:toDate.getTime(),
				'fetchSize':$('input[name="tx-history-fetchSize"]').val()
			}
		}).done(function(data, textStatus, jqxhr) {
			console.log(data);
			$('textarea.tx-history-fetchSize').val(JSON.stringify(data));
		}).fail(function(jqxhr, textStatus, errorThrown) {
			console.error(errorThrown);
			$('textarea.tx-history-fetchSize').val(errorThrown);
		});
	})
});


</script>
</body>
</html>