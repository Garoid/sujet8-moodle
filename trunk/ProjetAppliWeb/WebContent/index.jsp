<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moodle Forum Extractor</title>
</head>
<body>
<p>Adresse du forum à extraire :
<form method="get" action="ExtractTopics"> 
<input type="text" name="adresse" value="file:///home/vincent/wget/localhost/moodle/mod/forum/view.php%3Ff=1"/>
<input type="submit" value="Extaire"/>
</form>

</p>
<p>Adresse à explorer :
<form method="get" action="SubDiscussionChecker"> 
<input type="text" name="adresse" value="file:///home/vincent/wget/localhost/moodle/mod/forum/view.php%3Ff=1"/>
<input type="submit" value="Extaire"/>
</p>
</form>
</body>
</html>