<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<script type="text/javascript">
			
			function go()
			{
				var data = {response:{addressId: val("addressid"), q1: val("q1"), q2: val("q2"), q3: val("q3"), photo: "ABCDEF12"}};
				var xmlHttp = new XMLHttpRequest();
				xmlHttp.onreadystatechange = function()
				{
					if (xmlHttp.readyState == 4)
					{
						alert(xmlHttp.status + " " + xmlHttp.statusText);
					}
				};
				xmlHttp.open("POST", "http://localhost:8080/resteasy/restful-services/acquiredataservice/uploadresponsej", true);
				xmlHttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
				alert(JSON.stringify(data));
    		xmlHttp.send(JSON.stringify(data));
			}
			
			/*
			function go(imgdata)
			{
				if (!imgdata)
				{
					var read = new FileReader();
					read.onloadend = function(e) { 
						go(e.target.result); 
					};  
					read.readAsBinaryString(document.getElementById("photo").files[0]);
					return;
				}
				
				var data = {response:{addressId: val("addressid"), q1: val("q1"), q2: val("q2"), q3: val("q3"), photo: imgdata}};
				var xmlHttp = new XMLHttpRequest();
				xmlHttp.onreadystatechange = function()
				{
					if (xmlHttp.readyState == 4)
					{
						alert(xmlHttp.status + " " + xmlHttp.statusText);
					}
				};
				xmlHttp.open("POST", "http://localhost:8080/resteasy/restful-services/acquiredataservice/uploadresponsej", true);
				xmlHttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
				alert(JSON.stringify(data));
    		xmlHttp.send(JSON.stringify(data));
			}
			*/
			
			function val(id)
			{
				return document.getElementById(id).value;
			}
		</script>
 	</head>
	<body>
		
		<input type="text" id="addressid">
		<input type="text" id="q1">
		<input type="text" id="q2">
		<input type="text" id="q3">
		<input type="file" id="photo">
		
		<input type="button" onclick="go();" value="Go">
	</body>
</html>
