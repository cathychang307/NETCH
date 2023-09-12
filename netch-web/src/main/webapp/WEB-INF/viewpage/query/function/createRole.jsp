<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.createRole.title"><!--新增角色--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/createRole');
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
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleName"><!--角色名稱--></spring:message><span class="text-red">(*)</span></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="roleName" name="roleName" style="width: 50%"/></div>
				</div>
				<div class="row row-flex">				
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleId"><!--角色代號--></spring:message><span class="text-red">(*)</span></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="roleId" name="roleId" style="width: 50%"/></div>
				</div>
				<div class="row row-flex">				
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.roleOperationResult.roleDesc"><!--角色描述--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="roleDesc" name="roleDesc" style="width: 80%"/></div>
				</div>
	        </form>
        </div>
		
		<!-- errorMsg -->
		<div class="row rowMargin"> 
		    <div id="errorMsgSection" class="text-red"></div>
		</div>
		
		<!-- btn -->
		<div class="row rowMargin">
		    <div class="btns" align="center">
		        <button id="sure" type="button" class="btn1"><spring:message code="btn.sure"><!--確定--></spring:message></button>
		        <button id="clear" type="button" class="btn1"><spring:message code="btn.clear"><!--清除--></spring:message></button>
		        <button id="cancel" type="button" class="btn1"><spring:message code="btn.cancel"><!--取消--></spring:message></button>
		    </div>
		</div>
		
</div>
</body>
</html>
