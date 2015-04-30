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
	.json-output {
		height:250px;
		border:solid 1px #39c;
		overflow:auto;
	}
	p.extra-border-room {
		padding:20px;
	}
	.extra-margin-top {
		margin-top:35px;
	}
	</style>
</head>
<body>
	<div class="container">  
		<h2>Network Server</h2>
        
        <hr />
        <h3>Send A Document</h3>
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
        <table class="table table-striped table-bordered table-condensed table-hover xaction-history-table" width="100%">
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
        
        <h4>Delivery Options</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryOptions</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Delivery Options</dd>
        		</dl>
   				<p>
   				<h5>Sample Code:</h5>
				<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/deliveryOptions').done(function(data, textStatus, jqxhr){
	console.log(data);
})</pre>
        		</p>
        		<h5>Example</h5>
   				<p>
   				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
   				</p>
        		<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="deliveryOptions">Get Delivery Options</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="deliveryOptions"></samp></div>
        			</div>
        		</div>
        	</div>
        	<hr />
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryOptions/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        					<dt>memberId</dt>
        					<dd>Integer</dd>
        					<dt>formatId</dt>
        					<dd>Integer</dd>
        					<dt>webserviceUrl</dt>
        					<dd>String</dd>
        					<dt>deliveryMethodId</dt>
        					<dd>Integer</dd>
        					<dt>deliveryConfirm</dt>
        					<dd>Boolean</dd>
        					<dt>error</dt>
        					<dd>Boolean</dd>
        					<dt>operationalStatus</dt>
        					<dd>String</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Delivery Options that match the given parameters</dd>
        		</dl>
        		<p>
        		You can use the <code>deliveryOptions/search</code> endpoint to search for delivery options.
        		Below are some working examples for using the REST API and the <code>deliveryOptions/search</code> endpoint.<br />
        		<br />
        		In the below form, at least one must have a value. When the ajax response returns, the 
       			data will be rendered as a table below the form.
        		</p>
        		<div class="row">
    				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
      					<form class="form-horizontal doptForm">
      						
      						<div class="form-group">
      							<label class="control-label col-sm-4">id</label>
      							<div class="col-sm-4">
      								<input class="form-control" type="number" name="id" placeholder="id" min="1" step="1" value="" />
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">memberId</label>
      							<div class="col-sm-4">
      								<input class="form-control" type="number" name="memberId" placeholder="memberId" min="1" step="1" value="" />
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">formatId</label>
      							<div class="col-sm-4">
      								<input class="form-control" type="number" name="formatId" placeholder="formatId" min="1" step="1" value="" />
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">webserviceUrl</label>
      							<div class="col-sm-7">
      								<input class="form-control" type="text" name="webserviceUrl" placeholder="webserviceUrl" />
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">deliveryMethodId</label>
      							<div class="col-sm-7">
      								<input class="form-control" type="number" name="deliveryMethodId" placeholder="deliveryMethodId" />
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">deliveryConfirm</label>
      							<div class="col-sm-6">
      								<label class="radio-inline">
       								<input type="radio" name="deliveryConfirm" value="1" /> true
       							</label>
       							<label class="radio-inline">
       								<input type="radio" name="deliveryConfirm" value="0" /> false
       							</label>
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">error</label>
      							<div class="col-sm-8">
      								<label class="radio-inline">
       								<input type="radio" name="error" value="1" /> true
       							</label>
       							<label class="radio-inline">
       								<input type="radio" name="error" value="0" /> false
       							</label>
      							</div>
      						</div>
      						<div class="form-group">
      							<label class="control-label col-sm-4">operationalStatus</label>
      							<div class="col-sm-7">
      								<input class="form-control" type="text" name="operationalStatus" placeholder="operationalStatus" />
      							</div>
      						</div>
      						
      						<div class="form-group">
      							<label class="control-label col-sm-4">Request Type</label>
	      						<div class="btn-group" data-toggle="buttons">
	      							<label class="btn btn-primary active">
	      								<input type="radio" name="sendType" autocomplete="off" value="GET" checked="checked" /> GET
	      							</label>
	      							<label class="btn btn-primary">
	      								<input type="radio" name="sendType" autocomplete="off" value="POST" /> POST
	      							</label>
	      						</div>
      						</div>
      						
      						<div class="form-group text-center">
      							<button type="button" class="btn btn-default search-button">SEARCH</button>
      							<button type="button" class="btn btn-default reset-button">RESET</button>
      						</div>
      					</form>
    				</div>
    				<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 get-results-table-container"></div>
    			</div>
    			<p>
    				The input values in the above input collection will be gathered and added to a JQuery ajax call.<br />
    				The ajax configuration will need to change depending on the request type (GET/POST).
    			</p>
        		
        		<div role="tabpanel">
        			<ul class="nav nav-tabs" role="tablist">
        				<li role="presentation" class="active"><a href="#doptExampleGet" aria-controls="home" role="tab" data-toggle="tab">GET</a></li>
        				<li role="presentation"><a href="#doptExampleAjaxPost" aria-controls="home" role="tab" data-toggle="tab">POST</a></li>
        			</ul>
        			<div class="tab-content">
        				<div role="tabpanel" class="tab-pane fade in active" id="doptExampleGet">
        					<br /><br />
        					<pre>$.ajax({
	'url':'http://localhost:8080/EdExchange/services/rest/deliveryOptions/search',
	'data':data,
	'dataType':'json'
}).done(function(data, textStatus, jqxhr) {
	console.log(data);
	
}).fail(function(jqxhr, textStatus, errorThrown) {
	console.error(errorThrown);
});</pre>
		        		</div>
		        	
	       				<div role="tabpanel" class="tab-pane fade" id="doptExampleAjaxPost">
	       					<br /><br />
	       					<pre>$.ajax({
	'url':'http://localhost:8080/EdExchange/services/rest/deliveryOptions/search',
	'data':data,
	'dataType':'json'
	'type':'POST',
	'contentType':'application/json',
	'data':JSON.stringify(data),
	'processData':false
}).done(function(data, textStatus, jqxhr) {
	console.log(data);
	
}).fail(function(jqxhr, textStatus, errorThrown) {
	console.error(errorThrown);
});</pre>
	       				</div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryOptions/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing a Delivery Option or nothing if not found</dd>
        		</dl>
        		<p>
   				<h5>Sample Code:</h5>
				<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/deliveryOptions/5').done(function(data, textStatus, jqxhr){
	console.log(data);
})</pre>
        		</p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryOptions/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a DeliveryOption JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Delivery Option or nothing if a problem occurred</dd>
        		</dl>
        		<p>
        		The <code>deliveryOptions/save</code> endpoint can be used to create and update a Delivery Option. 
        		What causes a save operation instead of a create is the presence of a populated &quot;id&quot; property 
        		in the Delivery Option JSON object.<br /><br />
        		When sending the Delivery Option via AJAX, you should not url encode the object. For example in JQuery 
        		you would set the <code>processData</code> property in the ajax configuration object to <code>false</code> 
        		and convert the Delivery Option object to string format.
        		<br /><br />
        		<pre>$.ajax({
	'url':'http://localhost:8080/directoryServer/services/rest/deliveryOptions/save',
	'type':'POST',
	'contentType':'application/json',
	'dataType':'json',
	'data':JSON.stringify(deliveryOptionObject),
	'processData':false
})</pre>
        		</p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryOptions/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a DeliveryOption JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the removed Delivery Option or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        </div>
        
        <h4>Organizations</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/organizations</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Optional Parameters</dt>
        			<dd>
        				query<br />
        				A string parameter that will be tokenized to match the &quot;name&quot; field
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Organizations, unless a query parameter is passed</dd>
        		</dl>
        		<p>
        		<h5>Sample Code:</h5>
        		<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/organizations').done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				Or<br />
				<pre>$.ajax({
	'url':'http://localhost:8080/directoryServer/services/rest/organizations'
	'dataType':'json',
	'data':{'query':'transfer'}
}).done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				</p>
				<h5>Example</h5>
				<p>
				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
				</p>
				<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="organizations">Get Organizations</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="organizations"></samp></div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/organizations/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>directoryId</dt>
        					<dd>Integer</dd>
        					<dt>organizationId</dt>
        					<dd>String</dd>
        					<dt>organizationIdType</dt>
        					<dd>String</dd>
        					<dt>organizationName</dt>
        					<dd>String</dd>
        					<dt>organizationSubcode</dt>
        					<dd>String</dd>
        					<dt>entityId</dt>
        					<dd>Integer</dd>
        					<dt>organizationEin</dt>
        					<dd>String</dd>
        					<dt>createdTime</dt>
        					<dd>Long</dd>
        					<dt>modifiedTime</dt>
        					<dd>Long</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Organizations that match the given parameters</dd>
        		</dl>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/organizations/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing an Organization or nothing if not found</dd>
        		</dl>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/organizations/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>an Organization JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Organization or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/organizations/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>an Organization JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the removed Organization or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        </div>
        
        <h4>Contacts</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/contacts</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Optional Parameters</dt>
        			<dd>
        				query<br />
        				A string parameter that will be tokenized to match the &quot;name&quot; field
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Contacts, unless a query parameter is passed</dd>
        		</dl>
        		<p>
        		<h5>Sample Code:</h5>
        		<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/contacts').done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				Or<br />
				<pre>$.ajax({
	'url':'http://localhost:8080/directoryServer/services/rest/contacts'
	'dataType':'json',
	'data':{'query':'transfer'}
}).done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				</p>
				<h5>Example</h5>
				<p>
				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
				</p>
				<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="contacts">Get Contacts</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="contacts"></samp></div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/contacts/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>city</dt>
        					<dd>String</dd>
        					<dt>contactId</dt>
        					<dd>Integer</dd>
        					<dt>contactName</dt>
        					<dd>String</dd>
        					<dt>contactTitle</dt>
        					<dd>String</dd>
        					<dt>contactType</dt>
        					<dd>String</dd>
        					<dt>country</dt>
        					<dd>String</dd>
        					<dt>createdTime</dt>
        					<dd>Long</dd>
        					<dt>directoryId</dt>
        					<dd>Integer</dd>
        					<dt>email</dt>
        					<dd>String</dd>
        					<dt>modifiedTime</dt>
        					<dd>Long</dd>
        					<dt>phone1</dt>
        					<dd>String</dd>
        					<dt>phone2</dt>
        					<dd>String</dd>
        					<dt>state</dt>
        					<dd>String</dd>
        					<dt>streetAddress1</dt>
        					<dd>String</dd>
        					<dt>streetAddress2</dt>
        					<dd>String</dd>
        					<dt>streetAddress3</dt>
        					<dd>String</dd>
        					<dt>streetAddress4</dt>
        					<dd>String</dd>
        					<dt>zip</dt>
        					<dd>String</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Contacts that match the given parameters</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/contacts/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dt>Returns</dt>
        			<dd>JSON</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/contacts/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Contact JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Contact object</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/contacts/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Contact JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        </div>
        
        <h4>Delivery Methods</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryMethods</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Delivery Methods</dd>
        		</dl>
        		<p>
        		<h5>Sample Code:</h5>
        		<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/deliveryMethods').done(function(data, textStatus, jqxhr){
	console.log(data);
})</pre>
				</p>
				<h5>Example</h5>
				<p>
				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
				</p>
				<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="deliveryMethods">Get Delivery Methods</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="deliveryMethods"></samp></div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryMethods/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        					<dt>method</dt>
        					<dd>String</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Delivery Methods that match the given parameters</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryMethods/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing a Delivery Method or nothing if not found</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryMethods/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Delivery Method JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Delivery Method or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/deliveryMethods/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Delivery Method JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the removed Delivery Method or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        </div>
        
        <h4>Document Formats</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/documentFormats</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Optional Parameters</dt>
        			<dd>
        				query<br />
        				A string parameter that will be tokenized to match the &quot;name&quot; field
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Document Formats, unless a query parameter is passed</dd>
        		</dl>
        		<p>
        		<h5>Sample Code:</h5>
        		<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/documentFormats').done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				Or<br />
				<pre>$.ajax({
	'url':'http://localhost:8080/directoryServer/services/rest/documentFormats'
	'dataType':'json',
	'data':{'query':'transfer'}
}).done(function(data, textStatus, jqxhr){
	console.log(data);
})
</pre>
				</p>
				<h5>Example</h5>
				<p>
				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
				</p>
				<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="documentFormats">Get Document Formats</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="documentFormats"></samp></div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/documentFormats/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        					<dt>formatName</dt>
        					<dd>String</dd>
        					<dt>formatDescription</dt>
        					<dd>String</dd>
        					<dt>formatInuseCount</dt>
        					<dd>Integer</dd>
        					<dt>createdTime</dt>
        					<dd>Long</dd>
        					<dt>modifiedTime</dt>
        					<dd>Long</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Document Formats that match the given paramters</dd>
        		</dl>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/documentFormats/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the Document Format or nothing if not found</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/documentFormats/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Document Format JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Document Format or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/documentFormats/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>a Document Format JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the removed Document Format or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        </div>
        
        <h4>Entity Codes</h4>
        <div class="row extra-margin-top">
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/entityCodes</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Entity Codes</dd>
        		</dl>
        		<p>
        		<h5>Sample Code:</h5>
        		<pre>$.ajax('http://localhost:8080/directoryServer/services/rest/entityCodes').done(function(data, textStatus, jqxhr){
	console.log(data);
})</pre>
				</p>
				<h5>Example</h5>
				<p>
				The button will trigger a function that makes an ajax call to
   				the directory server. The result will display when the data is received.
				</p>
				<div class="row">
        			<div class="col-lg-2 col-md-2 col-sm-2">
        				<button type="button" class="btn btn-default dirServerButton" data-get-type="entityCodes">Get Entity Codes</button>
        			</div>
        			<div class="col-lg-1 col-md-1 col-sm-1"></div>
        			<div class="col-lg-8 col-md-8 col-sm-8">
        				<div class="json-output"><samp data-set-type="entityCodes"></samp></div>
        			</div>
        		</div>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/entityCodes/search</dd>
        			<dt>Request Method</dt>
        			<dd>GET, POST</dd>
        			<dt>Request Parameters</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        					<dt>code</dt>
        					<dd>Integer</dd>
        					<dt>description</dt>
        					<dd>String</dd>
        					<dt>createdTime</dt>
        					<dd>Long</dd>
        					<dt>modifiedTime</dt>
        					<dd>Long</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON array of all the Entity Codes that match the given parameters</dd>
        		</dl>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/entityCodes/{id}</dd>
        			<dt>Request Method</dt>
        			<dd>GET</dd>
        			<dt>Path Parameter</dt>
        			<dd>
        				<dl class="dl-horizontal">
        					<dt></dt><dd></dd>
        					<dt>id</dt>
        					<dd>Integer</dd>
        				</dl>
        			</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing an Entity Code or nothing if not found</dd>
        		</dl>
        		<p></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/entityCodes/save</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>an Entity Code JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the saved Entity Code or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
        	</div>
        	
        	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 extra-margin-top">
        		<dl class="dl-horizontal">
        			<dt>URL</dt>
        			<dd>[directory server root]/services/rest/entityCodes/remove</dd>
        			<dt>Request Method</dt>
        			<dd>POST</dd>
        			<dt>POST Payload</dt>
        			<dd>an Entity Code JSON object</dd>
        			<dt>Returns</dt>
        			<dd>JSON object representing the removed Entity Code or nothing if a problem occurred</dd>
        		</dl>
        		<p><em>see <code>deliveryOptions/save</code> for info on how to send the payload</em></p>
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
    <script src="resources/js/documentationPageAssets.js"></script>
	<script>
	
	//  dev: http://localhost:8080
	//   ci: http://local.pesc.dev
	// prod: http://pesc.cccnext.net
	var directoryServer = "<fmt:message key="directoryserver.host"/>";
	
	</script>
</body>
</html>