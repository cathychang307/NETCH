<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html class="no-js" lang="zh-Hant-TW">
    <!--[if lt IE 7 ]><html class="ie7"><![endif]-->
    <!--[if IE 7 ]><html class="ie7"><![endif]-->
    <!--[if IE 8 ]><html class="ie8"><![endif]-->
    <!--[if IE 9 ]><html class="ie9"><![endif]-->
    <!--[if (gt IE 9)|!(IE) ]><html class=""><![endif]-->
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, user-scalable=0"/>
		<link rel="stylesheet" href="../static/css/default.css?cache=20220329" />
        <title><decorator:title default="台灣銀行票據信用查詢系統"/></title>
        <link rel="shortcut icon" href="../static/images/favicon.ico"/>
        <link rel="bookmark" href="../static/images/favicon.ico"/>
        <link rel="apple-touch-icon" href="../static/images/apple-touch-icon.png"/>
        <!--[if lt IE 9]>
    <script src="../static/lib/js/html5.js"></script>
    <script src="../static/lib/js/respond/respond.min.js"></script>
    <link rel="stylesheet" href="../static/css/ie.css">
<![endif]-->
        <script src="../static/requirejs/2.3.2/require.min.js"></script>
        <script src="../static/main.min.js?cache=202104081038"></script>
        <script>
            function showMenu() {
                $(".menu_sub").each(function () {
                    $(this).css({display: "block"});
                    $(this).prev("a").find("span").removeClass("icon-1").addClass("icon-5");
                });
            }
            function hideMenu() {
                $(".menu_sub").each(function () {
                    $(this).css({display: "none"});
                    $(this).prev("a").find("span").removeClass("icon-5").addClass("icon-1");
                });
            }
            function refreshDatetime() {
              var date = new Date();
              var dateStr = date.getFullYear() + "-" 
                          + ( date.getMonth() + 1 ) + "-" 
                          + date.getDate() + " " 
                          + date.getHours() + ":"
                          + ( date.getMinutes() < 10 ? "0" : "" ) + date.getMinutes() + ":"
                          + ( date.getSeconds() < 10 ? "0" : "" ) + date.getSeconds();
              
              var dateField = document.getElementById( "currentDatetime" );
              dateField.innerHTML = dateStr;

              setTimeout( "refreshDatetime()", 1000 );
            }
            window.onload = function ()
            {
              refreshDatetime();
            }
            function logout() {
              var flag = confirm( "確定要登出 [票信查詢系統] 嗎" );
              if( flag )
                  top.location.href = "../j_spring_security_logout";
          }
        </script>
        <decorator:getProperty property="prop" default=""/>
        <decorator:head/>
    </head>
    <body>
        <script>
            // loadScript('js/common/cust.socket');
        </script>
        <div class="mainBody container" style="width:100%">
            <header class="visible-md visible-lg">
                <div class="row">
                    <div class="logo col-md-12 nopadding">
                        <a><img src="../static/images/b.jpg" style="width: 100%;"/></a>
                    </div>
                </div>
            </header>
            <nav class="top hide">
                <ul class="block"></ul>
                <ul class="navmenu"></ul>
            </nav>
            <div class="clear"></div>
            <div class="main">
                <div class="row hidden-print">
                    <div class="visible-xs visible-sm">
                        <button type="button" class="navbar-toggle hamburger-menu" data-toggle="collapse" data-target=".navbar-collapse">
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-2 navbar-collapse collapse nopadding">
                    	<div style="padding: 5px; height: 10vh; background: url(../static/images/back00_modified.png) repeat scroll left top transparent">
                    		<table border="0" width="171" cellpadding="2" style="width:100%; height:100%;">
								<tr>
									<td width="16"><img border="0" src="../static/images/clock.gif"/></td>
									<td colspan="2" nowrap>
										<span class="leftmenu">
											<span class="leftmenu" id="currentDatetime">2017-4-18 17:44:15</span>
										</span>
									</td>
								<tr>
									<td width="16"><img border="0" src="../static/images/user.gif" /></td>
									<td width="87" nowrap class="leftmenu">${userDetails.userName}</td>
									<td width="48" nowrap>
<!-- 										<a href="../j_spring_security_logout"> -->
											<img border="0" src="../static/images/logout-r.gif" align="middle" onclick="logout()"/>
<!-- 										</a> -->
									</td>
								</tr>
							</table>
	                    </div>
                        <nav style="min-height: 75vh;" class="sub navbar navbar-default" role="navigation">
                        	<div style="width:100%; height:4px; background: linear-gradient(#b9bfd4, #4d5266)"></div>
							<div class="btns" align="center" width="100%">
								<div id="showMenu" class="showMenu" onclick="showMenu();return false;" style="display:inline-block;"></div>
	                            <div id="hideMenu" class="hideMenu" onclick="hideMenu();return false;" style="display:inline-block;"></div>
	                        </div>
	                        <div class="treeview" style="padding: 5px;">
                            	<ol></ol>
                            </div>
                            <div style="color: #eae2d4;">${hostName}</div>
						</nav>
                    </div>
                    <div class="col-md-10" style="background: url(../static/images/back01.gif)">
                    	<div class="row col-md-9">
                    		<div id="breadcrumb" style="min-height: 10vh; text-align: right; padding: 20px;"></div>
                        </div>
                        <div class="row">
	                        <article id="article" class="col-xs-12 col-sm-12 col-md-9" style="min-height: 75vh; padding:0px 30px 50px;">
									<div class="row row-flex">
										<div class="col-sm-12 col-xs-12 td_style_1">
											<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
										    	<tr>
										        	<td bgcolor="#A7C0BF">&nbsp;</td>
										        	<td height="36" bgcolor="#E6ECEC" class="style1">
										            	<div align="center"><strong>台灣銀行票據信用查詢系統</strong></div>
										        	</td>
										        	<td class="style1" bgcolor="#A7C0BF">&nbsp;</td>
										    	</tr>
											</table>
											<div align="center">
										       	<h4 class="style4">&nbsp;</h4>
										       	<h4 class="style4">&nbsp;</h4>
										       	<h4 class="style4">&nbsp;</h4>
										       	<h4 class="style4">&nbsp;</h4>
										       	<h4 class="style4" style="color: brown">歡迎使用台灣銀行票信查詢系統！</h4>
										       	<h4 class="style4" style="color: brown">依票交所規定開放查詢時間為9:00~22:00</h4>
										       	<p class="style4">&nbsp;</p>
										   	</div>
										</div>
									</div>
	                        </article>
                    	</div>
                    </div>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </body>
    <!--[if lt IE 8]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->
</html>
