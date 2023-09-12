<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.configureRoleDetail.title"><!--新增角色--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/configureRoleDetail');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/008.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.configureRoleDetail.title"><!--月收費彙整--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- table -->
		<div class="rowMargin"> 
			<form id="mform" onsubmit="return false;">
				<div class="row row-flex">		
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.configureRoleDetail.01"><!--名稱--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="roleName" name="roleName" class="field"></span></div>
				</div>
				<div class="row row-flex">		
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.configureRoleDetail.02"><!--代號--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="roleId" name="roleId" class="field"></span></div>
				</div>
				<div class="row row-flex">		
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.configureRoleDetail.03"><!--描述--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="roleDesc" name="roleDesc" style="width: 80%;"></span></div>
				</div>
				<div class="row row-flex">		
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.configureRoleDetail.04"><!--生效--></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1">
						<div class="row">
							<div class="col-sm-4 col-xs-12">
								<input type="radio" id="roleEnabled" name="roleEnabled" value="1">
								<label for="roleEnabled"><spring:message code="js.configureRoleDetail.04.1"> <!--生效 --> </spring:message></label>
							</div>
							<div class="col-sm-4 col-xs-12">
								<input type="radio" id="roleDisabled" name="roleEnabled" value="0">
								<label for="roleDisabled"><spring:message code="js.configureRoleDetail.04.2"> <!--停用--></spring:message></label>
							</div>	
						</div>
					</div>
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
		        <button id="update" type="button" class="btn1"><spring:message code="btn.update"><!--更新--></spring:message></button>
		        <button id="reload" type="button" class="btn1"><spring:message code="btn.reload"><!--重新讀取--></spring:message></button>
		        <button id="cancel" type="button" class="btn1"><spring:message code="btn.cancel"><!--取消--></spring:message></button>
		        <button id="deleteRole" type="button" class="btn1"><spring:message code="btn.deleteRole"><!--刪除角色--></spring:message></button>
		    </div>
		</div>

		<!-- table -->
		<div class="rowMargin">
			<form id="mform" onsubmit="return false;">
				<div id="tableContainer">
				</div>
			</form>
		</div>
</div>
</body>
</html>
