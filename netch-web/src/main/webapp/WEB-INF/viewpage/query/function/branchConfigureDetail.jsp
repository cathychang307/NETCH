<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="decorator" content="none">
			<title>
				<spring:message code="branchConfigure.title">
					<!--分行基本資料維護-->
				</spring:message>
			</title>
		</head>
		<body>
		<div class="tr01">
			<script>
				loadScript('js/query/function/branchConfigureDetail');
			</script>
			
			<!-- title -->
	        <div class="row rowMargin">
	        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
					<tr>
						<td width="8%">
							<img src="../static/images/010.jpg" width="40" height="40">
						</td>
						<td width="92%" class="style1">
							<h3>
								<spring:message code="branchConfigure.title">
									<!--分行基本資料維護-->
								</spring:message>
							</h3>
						</td>
					</tr>
				</table>			
			</div>

			<form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
				<div class="rowMargin">
					<div class="row row-flex">
						<div class="col-sm-4 col-xs-12 hd_style_1">
							<spring:message code="js.branchConfigure.grid.title.01">
								<!--單位代號-->
							</spring:message>
							<span class="text-red">(*)</span>
						</div>
						<div class="col-sm-8 col-xs-12 td_style_1">
							<input type="text" class="validate[required]"
								id="departmentId" name="departmentId" style="width:75%;" />
						</div>
					</div>
					<div class="row row-flex">
						<div class="col-sm-4 col-xs-12 hd_style_1">
							<spring:message code="js.branchConfigure.grid.title.02">
								<!--單位名稱-->
							</spring:message>
							<span class="text-red">(*)</span>
						</div>
						<div class="col-sm-8 col-xs-12 td_style_1">
							<input type="text" class="validate[required]"
								id="departmentName" name="departmentName" style="width:75%;" />
						</div>
					</div>
					<div class="row row-flex">
						<div class="col-sm-4 col-xs-12 hd_style_1">
							<spring:message code="js.branchConfigure.grid.title.03">
								<!--付費分行代號-->
							</spring:message>
							<span class="text-red">(*)</span>
						</div>
						<div class="col-sm-8 col-xs-12 td_style_1">
							<input type="text" class="validate[required]"
								id="chargeBankId" name="chargeBankId" style="width:75%;"/>
						</div>
					</div>
					<div class="row row-flex">
						<div class="col-sm-4 col-xs-12 hd_style_1">
							<spring:message code="js.branchConfigure.grid.title.05">
								<!--交換所別-->
							</spring:message>
							<span class="text-red">(*)</span>
						</div>
						<div class="col-sm-8 col-xs-12 td_style_1">
							<input type="text" class="validate[required]"
								id="tchId" name="tchId" style="width:75%;"/>
						</div>
					</div>
					<div class="row row-flex">
						<div class="col-sm-4 col-xs-12 hd_style_1">
							<spring:message code="js.branchConfigure.grid.title.06">
								<!--附註-->
							</spring:message>
						</div>
						<div class="col-sm-8 col-xs-12 td_style_1">
							<input type="text" id="memo" name="memo" style="width:75%;"/>
						</div>
					</div>
				</div>
				<!-- errorMsg -->
				<div class="row rowMargin">
					<div id="errorMsgSection" class="text-red"></div>
				</div>
				<!-- btns -->
				<div class="btns rowMargin" align="center">
					<button type="button" id="execute" name="execute" class="btn btn-default">
						<spring:message code="btn.execute">
							<!-- 執 行 -->
						</spring:message>
					</button>
					<button type="button" id="reload" name="reload" class="btn btn-default">
						<spring:message code="btn.reset">
							<!-- 重 設 -->
						</spring:message>
					</button>
					<button type="button" id="cancel" name="cancel" class="btn btn-default">
						<spring:message code="btn.cancel">
							<!-- 取 消 -->
						</spring:message>
					</button>
				</div>
			</form>
		</div>
		</body>
	</html>
