<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
    <head>
        <meta name="decorator" content="login">
            <title><spring:message code="login.title"/></title>
        </head>
        <body>
            <script>
                loadScript('js/login');
            </script>
            
            <div style="margin-right: -25px;">
            
	            <table width="100%" border="0" cellpadding="0" cellspacing="0">
	            	<tr>
	            		<td style="width: 2%; background: url(../static/images/l01.gif) repeat left top; min-width: 25px;"></td>
	            		<td style="width: 80%; background: url(../static/images/l03.gif) left top;">
	            			<img src="../static/images/l02.gif" align="left">
	            		</td>
	            		<td style="width: 2%; background: url(../static/images/l03.gif)"></td>
	            	</tr>
	            	<tr>
	            		<td style="width: 2%; background: url(../static/images/l04.gif) repeat left top; min-width: 25px;"></td>
	            		<td style="width: 80%; background: url(../static/images/l08.gif) left top;">
	            			<img src="../static/images/l07.gif" align="left">
	            		</td>
	            		<td style="width: 2%; background: url(../static/images/l08.gif)"></td>
	            	</tr>
	            	<tr>
	            		<td style="width: 2%; background: url(../static/images/l04.gif) repeat left top; min-width: 25px;"></td>
	            		<td style="height: 280px; background: url(../static/images/l10.jpg) no-repeat left top; background-size: cover;">
								<div background="../static/images/l10.jpg">
									<div class="tr01" style="margin: 60px auto; width: 450px;">
										<table border="0" class="table2">
											<tr>
												<td colspan="3">
													<table border="0" style="width: 100%;">
														<tr>
															<td width="20%"><img src="../static/images/11.gif" width="39" height="41"></td>
															<td width="80%" class="style1">
																<h3>請 登 入 系 統</h3>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="right" nowrap class="style1">使用者帳號：</td>
												<td colspan="2" align="left"><input name="j_username" type="text" id="j_username" size="30" maxlength="20"></td>
											</tr>
											<tr>
												<td align="right" valign="top" nowrap class="style1">使用者密碼：</td>
												<td colspan="2" align="left"><input name="j_password" type="password" id="j_password" size="30" autocomplete="off"></td>
											</tr>
											<tr>
												<td align="right" valign="top" nowrap class="style1">權 限 角 色：</td>
												<td colspan="2" align="left"><input name="virtualRole" type="text" id="virtualRole" size="30" maxlength="20"></td>
											</tr>
											<tr>
												<td colspan="3">
													<div class="btns" align="center">
														<button type="button" class="btn btn-default" id="submit">
															<spring:message code="btn.login"></spring:message>
														</button>
														<button type="button" class="btn btn-default" id="clear">
															<spring:message code="btn.clear"></spring:message>
														</button>
													</div>
												</td>
											</tr>
										</table>
								</div>
							</div>
	            		</td>
	            		<td bgcolor="#FFFFFF"></td>
	            	</tr>
	            	<tr>
	            		<td style="width: 2%; background: url(../static/images/l09.gif) repeat left top;">
	            		</td>
	            		<td style="width: 60%; background: url(../static/images/l011.gif) left top;">
	            			<img src="../static/images/l010.gif" align="left">
	            		</td>
	            		<td style="width: 2%; background: url(../static/images/l011.gif)"></td>
	            	</tr>
	            </table>
            </div>
            
            
            
                                <table class="tb2" border="1">
									<tr>
										<td><a href="../page/index?employee_id=user1&jumper_id=123456">(O)user1-59010000-一般使用者</a></td>
									</tr>
									<tr>
										<td><a href="../page/index?employee_id=user2&jumper_id=123456">(O)user2-59020000-資訊室管理者</a></td>
									</tr>
									<tr>
										<td><a href="../page/index?employee_id=user3&jumper_id=123456">(O)user3-59030000-電金部管理者</a></td>
									</tr>
									<tr>
										<td><a href="../page/index?employee_id=user4&jumper_id=123456">(O)user4-59040000-帳戶管理者</a></td>
									</tr>
									
                            		<tr>
										<td><a href="../page/index">(X)index</a></td>
									</tr>
                            		<tr>
										<td><a href="../j_spring_security_check?employee_id=user3&jumper_id=123456">(O)授權成功user3, j_spring_security_check, employee_id, jumper_id</a></td>
									</tr>
                            		<tr>
										<td><a href="../page/index?employee_id=user99&jumper_id=123456">(X)user99, j_spring_security_check, employee_id, jumper_id</a></td>
									</tr>
                            		<tr>
										<td><a href="../j_spring_security_check?j_username=noSSOUser&j_password=123456">(X)j_spring_security_check, j_username, j_password, get</a></td>
									</tr>
                            		<tr>
										<td><a href="../j_spring_security_check">(X)j_spring_security_check</a></td>
									</tr>
									
									<tr>
										<td><a href="../page/aa?employee_id=user4&jumper_id=123456">(O)授權成功user4, page/aa, employee_id, jumper_id</a></td>
									</tr>
									<tr>
										<td><a href="../page/aa">(X)page/aa</a></td>
									</tr>
								</table>
        </body>
    </html>
