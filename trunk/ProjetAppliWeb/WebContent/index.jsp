<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="css/design.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="ajaxExtract.js"></script>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<title>Moodle Forum Extractor</title>
</head>
<body>
<div><center><img src="img/moodle2.jpg"> </center></div>
<p>
<form action="CopyFile" method="get">
<input type="submit" value="Copie de fichier">
<input type="text" id="adresse" name="adresse" value="file:///home/max06/localhost/moodle/mod/forum/index.php%3Fid=2" />
</form>
</p>
<p>
Adresse du forum à extraire :
<input type="text" id="adresse" name="adresse" value="file:///home/max06/localhost/moodle/mod/forum/index.php%3Fid=2" />
<input type="button" value="Extaire" onclick="extract()"/>
</p>
<div id="results">    
</div>
</body>
</html>