<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<!DOCTYPE html>
<html class="no-js" lang="zh-Hant-TW">
<!--[if lt IE 7 ]><html class="ie7"><![endif]-->
<!--[if IE 7 ]><html class="ie7"><![endif]-->
<!--[if IE 8 ]><html class="ie8"><![endif]-->
<!--[if IE 9 ]><html class="ie9"><![endif]-->
<!--[if (gt IE 9)|!(IE) ]><html class=""><![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, user-scalable=0" />
<title><decorator:title default="CapWebMVC" /></title>
<link rel="stylesheet" href="../static/css/login.css?cache=20220329" />
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
  if (self.location.hash){
    self.location = 'page/index';
  }
  var baseUrl = "../static";
//-->
</script>
<script src="../static/requirejs/2.3.2/require.min.js"></script>
<script src="../static/main.min.js?cache=202104081038"></script>
<decorator:getProperty property="reqJSON" default="" />
<decorator:getProperty property="i18n" default="" />
<decorator:head />
</head>
<body>
    <decorator:body />
</body>
</html>
