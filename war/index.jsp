<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>推文投票計數器</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script src="//rawgithub.com/sydlawrence/jQuery-Shadow/master/jquery.shadow/jquery.shadow.js"></script>
<script src="/js/script.js"></script>
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css">
<link rel="stylesheet" href="//rawgithub.com/sydlawrence/jQuery-Shadow/master/jquery.shadow/jquery.shadow.css">
<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery.ui.all.css">
<link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div id='mainframe'>
<div id='inputs'>
	<table width='500'><tr><td><h1>PTT推文投票統計</h1>v1.3.7</td><td><span style='position:relative; top:10px;'><a href='#' id='intro'>使用說明</a></span></td></tr></table>
	<hr />
	<form id='myform'>
		<input type='hidden' name='hiddenValue' id='hiddenValue' />
		輸入方式: <input type='radio' name='input' id='single' value='single' value='single' checked/>單筆輸入　<input type='radio' name='input' id='oneline' value='oneline'/>簡易輸入　<input type='radio' name='input' id='web' value='web'/>自動輸入(<font color='red'>*</font>beta)<br/>
		投票選項: <input type='text' name='target' id='target' />　<input type='button' value='新增選項' id='append' /><br/>
		文章網址: <input type='text' name='url' id='url' size='60' value='http://www.ptt.cc/bbs/Test/M.1399873101.A.813.html'><br/>
		日期區間: <input type='text' name='sDate' id='sDate' readonly/> ~ <input type='text' name='eDate' id='eDate' readonly/><br />
		重投設定: <input type='radio' name='revote' value='O'>允許  <input type='radio' name='revote' value='X' checked='checked'>不允許<br/>
		可投票數: <input type='text' name='count' id='count' size='5' value='1'>&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' value='送出查詢' id='send'><span id='loading' style='display:none; text-align:right; width:12px; height:12px; color:red;'><img style='width:40px; height:40px;'src='images/loading.gif' /> data loading now....</span>
	</form>
	<p>
</div>
<div id='showArea'></div>
<div id='showArea2'></div>
<hr/>
</div>
<div id='introframe'>
	<hr/>
	1. PTT的文章TAG請用 [<strong>推投</strong>] 才會允許計算。<br/><p><p>
	
	2. 選項可以用中文、日文、英文、數字，不接受特殊符號。<br/><p><p>
	3. 選取允許重投，系統將取使用者最新一次的投票記錄。<br/>
	&nbsp;&nbsp;&nbsp;&nbsp;如果不允許重投，則不管使用者推文幾次都只會使用第一次的記錄。<br/><p><p>
	4. 網址部分請在PTT的文章按大寫Q，或於文章底部都可以找到該文章網址。<br/><p><p>
	5. 票數選項可以決定每個使用者可以投幾票，若准許投三票，<br/>
	&nbsp;&nbsp;&nbsp;&nbsp;則使用者可以推文三次進行投票。(重複內容視為一筆記錄)<br/><p><p>
	6. 投票推文加半形 "@" 符號完結，完結後即可聊天推文。<br/><p><p>
	7. 發文者的簽名檔請勿擺放符合投票格式的推文式簽名檔，此將影響計算結果。<br/><p><p>
	8. 日期區間若要填寫的話，請兩個都填，若無填寫就不會使用日期過濾。<br/><p><p>
	註：只要投票格式不符合，就不會列入計算，<br/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;因此即使允許重投，推發一般聊天文也不會洗掉結果。
	<hr/>
	<div style='width:480px; text-align:center;'>
	<input type='button' id='btn' value='close'>
	</div>
</div>
</body>
</html>