<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style><jsp:include page="/css/design.css" flush='true' /></style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="ajaxSearch.js"></script>
<script src=\"http://code.jquery.com/jquery-latest.js\"></script>
<script>
$(document).ready(function(){
	$('p').click(function () {
			$(this).siblings().slideToggle("slow");
	});
});
</script>
<title>Moodle Forum Extractor</title>
<link href="/home/max06/Documents/web/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/ProjetAppliWeb/WEB-INF/classes/resources/design.css" rel="stylesheet" type="text/css" />
</head>
<body>
<p>
Adresse du forum à extraire :
<input type="text" id="adresse" name="adresse" value="file:///home/max06/localhost/moodle/mod/forum/view.php%3Ff=1" />
<input type="button" value="Extaire" onclick="search()"/>
</p>
<div id="results">
        
</div>
</body>
</html>