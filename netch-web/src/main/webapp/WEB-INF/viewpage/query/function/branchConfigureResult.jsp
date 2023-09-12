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
				loadScript('js/query/function/branchConfigureResult');
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
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchConfigure.grid.title.01">
								<!--單位代號-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="departmentId" name="departmentId"></span></div>
			    </div>
				<div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchConfigure.grid.title.02">
								<!--單位名稱-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="departmentName" name="departmentName"></span></div>
			    </div>
				<div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchConfigure.grid.title.03">
								<!--付費分行代號-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="chargeBankId" name="chargeBankId"></span></div>
			    </div>
				<div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchConfigure.grid.title.05">
								<!--交換所別-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="tchId" name="tchId"></span></div>
			    </div>
				<div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchConfigure.grid.title.06">
								<!--附註-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="memo" name="memo"></span></div>
			    </div>
				<div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="branchConfigure.status">
								<!--狀態-->
							</spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><span id="status" name="status"></span></div>
			    </div>
			 </div>
			    
				<!-- errorMsg -->
				<div class="row rowMargin">
					<div id="errorMsgSection" class="text-red"></div>
				</div>

				<div class="btns rowMargin" align="center">
					<button type="button" id="complete" name="complete" class="btn btn-default">
						<spring:message code="btn.end">
							<!-- 結 束 -->
						</spring:message>
					</button>
				</div>
			</form>
		</div>
		</body>
	</html>
