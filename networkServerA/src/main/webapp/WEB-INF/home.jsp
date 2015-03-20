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
        
        <!--
        @RequestParam(value="recipientId", required=true) Integer recipientId, 
        @RequestParam(value="file", required=true) MultipartFile file,
        @RequestParam(value="networkServerId", required=true) Integer networkServerId,
        @RequestParam(value="senderId") Integer senderId,
        @RequestParam(value="fileFormat") String fileFormat,
        @RequestParam(value="fileSize", defaultValue="0") Float fileSize
        -->
        
        <form action="sendFile" method="post" enctype="multipart/form-data" accept-charset="utf-8">
            <h3>Send A Transcript File</h3>
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
                	<label class="control-label col-lg-2 col-md-4 col-sm-4 col-xs-4">File Format</label>
                    <div class="col-lg-4 col-md-4 col-sm-8 col-xs-8">
                    	<input type="text" name="fileFormat" class="form-control" placeholder="What type of file to transfer" />
                    </div>
                </div>
                
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
        <h3>Directory Server Communication</h3>
        <div class="row">
        	<div class="col-lg-6 col-md-6 col-sm-6">
                <ul class="list-unstyled extra-space">
                    <li><button type="button" class="btn btn-default" data-get-type="deliveryOptions">Get Delivery Options</button></li>
                    <li><button type="button" class="btn btn-default" data-get-type="organizations">Get Organizations</button></li>
                    <li><button type="button" class="btn btn-default" data-get-type="contacts">Get Contacts</button></li>
                    <li><button type="button" class="btn btn-default" data-get-type="deliveryMethods">Get Delivery Methods</button></li>
                    <li><button type="button" class="btn btn-default" data-get-type="documentFormats">Get Document Formats</button></li>
                    <li><button type="button" class="btn btn-default" data-get-type="entityCodes">Get Entity Codes</button></li>
                </ul>
            </div>
           	<div class="col-lg-6 col-md-6 col-sm-6">
        		<textarea rows="10" autocomplete="off" class="full-width"></textarea>
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

/*

http://localhost:8080/networkServerA/sendFile?
file=<multipart file>
recipientId=<destination identifier> Will use the recipientId to send to end point
fileFormat=<compliant file format>
networkServerId=<id of sending network server>
senderId=<id of sending organization>
fileSize=<float>

*/

// http://localhost:8080
// http://pesc.cccnext.net
var directoryServer = 'http://pesc.cccnext.net';
$(document).ready(function() {
	$('.btn').click(function(e) {
		var type = $(e.currentTarget).data('get-type');
		$('textarea').val(null);
		$.ajax(
			[directoryServer,'EdExchange/services/rest',type].join('/')
		).done(function(data,textStatus,jqXHR){
			console.log(data);
			$('textarea').val(JSON.stringify(data));
		}).error(function(jqXHR, textStatus, errorThrown) {
			console.error(errorThrown);
			console.error(textStatus);
			$('textarea').val(errorThrown);
		});
	});
});
</script>
</body>
</html>