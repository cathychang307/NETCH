<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="connectionOperation.title">
        <!--連線作業-->
    </spring:message></title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/connectionOperation');
        </script>
        
        <!-- title -->
        <div class="row">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/011.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="connectionOperation.title"><!--連線作業--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>

		<form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="connectionOperation.title"><!-- 連線作業 --></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1">
						<select size="1" id="inputAction" name="inputAction">
							<option>LOGON</option>
							<option>LOGOFF</option>
						</select>
					</div>
				</div>
			</div>
		</form>

		<!-- btn -->
		<div class="row">
		    <div class="btns" align="center">
				<button type="button" id="sendOperation" name="sendOperation" class="btn btn-default">
					<spring:message code="connectionOperation.btn.execute">
						<!-- 執 行 -->
					</spring:message>
				</button>
			</div>
		</div>
    
</div>
</body>
</html>
