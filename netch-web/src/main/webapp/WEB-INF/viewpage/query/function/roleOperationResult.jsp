<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.roleOperationResult.title"><!--新增角色--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/roleOperationResult');
    </script>
    
    <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/008.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.createRole.title"><!--新增角色--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- table -->
		<div class="rowMargin"> 
			<form id="mform" onsubmit="return false;">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleName"><!--角色名稱--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="enabledText" name="enabledText"></span><span id="roleName" name="roleName"></span></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleId"><!--角色代號--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="roleId" name="roleId"></span></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleDesc"><!--角色描述--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="roleDesc" name="roleDesc"></span></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleFunction"><!--角色功能--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><div id="roleFunction" name="roleFunction"></div></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.responseMessage"><!--狀　　態--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="responseMessage" name="responseMessage"></span></div>
				</div>
	        </form>
        </div>
    
    <!-- btn -->
    <div class="row rowMargin">
        <div class="btns" align="center">
            <button id="end" type="button" class="btn1"><spring:message code="btn.end"><!--結束--></spring:message></button>
        </div>
    </div>
</div>
</body>
</html>
