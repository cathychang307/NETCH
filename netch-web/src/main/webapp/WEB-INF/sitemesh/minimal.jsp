<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<!DOCTYPE HTML>
<html class="no-js" lang="zh-Hant-TW">
<!--[if lt IE 7 ]><html class="ie7"><![endif]-->
<!--[if IE 7 ]><html class="ie7"><![endif]-->
<!--[if IE 8 ]><html class="ie8"><![endif]-->
<!--[if IE 9 ]><html class="ie9"><![endif]-->
<!--[if (gt IE 9)|!(IE) ]><html class=""><![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, user-scalable=0" />
<title><decorator:title default="CapWebMVC" /></title>
<link rel="stylesheet" href="../../static/css/main.css?cache=20220329" />
<link rel="shortcut icon" href="../static/images/favicon.ico"/>
<link rel="bookmark" href="../static/images/favicon.ico"/>
<link rel="apple-touch-icon" href="../static/images/apple-touch-icon.png" />
<!--[if lt IE 9]>
    <script src="../static/lib/js/html5.js"></script>
    <script src="../static/lib/js/respond/respond.min.js"></script>
    <link rel="stylesheet" href="../static/css/ie.css">
<![endif]-->
<style>
html {
  display: none;
}
</style>
<script>
<!--
  if (self == top) {
    document.documentElement.style.display = 'block';
  }
  var baseUrl = "../../static";
//-->
</script>
<script src="../../static/requirejs/2.3.2/require.min.js"></script>
<script src="../../static/main.min.js?cache=202104081038"></script>
<decorator:getProperty property="reqJSON" default="" />
<decorator:head />
</head>
<body>
    <div class="mainBody">
        <header>
            <div class="logo">
                <a><img src="../static/images/logo.png"></a>
            </div>
            <ol style="height: 18px;">
                <li class="lang"><a href="#language">&nbsp;LANGUAGE&nbsp;</a></li>
                <li>&nbsp;-<a href="?lang=zh_TW">&nbsp;正體&nbsp;</a></li>
                <li>&nbsp;-<a href="?lang=zh_CN">&nbsp;简体&nbsp;</a></li>
                <li>&nbsp;-<a href="?lang=en">&nbsp;ENGLISH&nbsp;</a></li>
            </ol>
        </header>
        <decorator:body />
        <div class="clear"></div>
        <footer>
            <div class="copyright">資拓宏宇國際股份有限公司 © 2016 版權所有</div>
        </footer>
        <div class="bg-around right">&nbsp;</div>
        <div class="bg-around left">&nbsp;</div>
    </div>
</body>
</html>
