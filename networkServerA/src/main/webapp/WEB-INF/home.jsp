<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
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
</head>
<body>
	<div class="container">  
		<h2>Network Server</h2>
		<ul class="list-unstyled">
			<li><button type="button" class="btn btn-default" data-get-type="deliveryOptions">Get Delivery Options</button></li>
            <li><button type="button" class="btn btn-default" data-get-type="organizations">Get Organizations</button></li>
            <li><button type="button" class="btn btn-default" data-get-type="contacts">Get Contacts</button></li>
            <li><button type="button" class="btn btn-default" data-get-type="deliveryMethods">Get Delivery Methods</button></li>
            <li><button type="button" class="btn btn-default" data-get-type="documentFormats">Get Document Formats</button></li>
            <li><button type="button" class="btn btn-default" data-get-type="entityCodes">Get Entity Codes</button></li>
		</ul>
        
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
$(document).ready(function() {
	
	$('.btn').click(function(e) {
		var type = $(e.currentTarget).data('get-type');
		$.ajax(
			'http://local.pesc.dev/EdExchange/services/rest/'+type
		).done(function(data,textStatus,jqXHR){
			console.log(data);
		}).error(function(jqXHR, textStatus, errorThrown) {
			console.error(errorThrown);
			console.error(textStatus);
		});
	});
});
</script>
</body>
</html>