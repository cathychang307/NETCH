<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.configureRole.title"><!--角色功能維護--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/configureRole');
        </script>
        
		<!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/008.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.configureRole.title"><!--角色功能維護--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- errorMsg -->
		<div class="row rowMargin"> 
		    <div id="errorMsgSection" class="text-red"></div>
		</div>
		
		<!-- grid -->
		<div class="row rowMargin">
			<div class="col-sm-12">
				<div id="gridview"></div>
			</div>
		</div>
</div>
</body>
</html>
