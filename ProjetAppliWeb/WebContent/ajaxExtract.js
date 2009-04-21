
var req;
function extract(){
	copyFile();
	var adresse = document.getElementById("adresse");
	var url = "ForumHierarchyCrosser?adresse="+ adresse.value;
	//alert(url);
	req = new XMLHttpRequest();
	req.onreadystatechange = callback;
    req.open("get",url,true);
    req.send(null);
}

function callback(){
    var div = document.getElementById('results');
    if (req.readyState==4){
        if (req.status == 200){
            //it works:
            div.innerHTML = req.responseText;
        }
        else{
            //problem:
            div.innerHTML = "research failed";
        }
    }
    
}

function copyFile(){

	var adresse = document.getElementById("adresse");
	var url = "CopyFile?adresse="+ adresse.value;
	req = new XMLHttpRequest();
    req.open("get",url,true);
    req.send(null);
}

function exportXmlToRdf(){

	var url = "XmlToRdf";
	req = new XMLHttpRequest();
    req.open("get",url,true);
    req.onreadystatechange = afficherAlert;
    req.send(null);
}

function afficherAlert(){
    if (req.readyState==4){
        if (req.status == 200){
            //it works:
            alert(req.responseText);
        }
        else{
            //problem:
            alert('export failed');
        }
    }
    
}

//function extract(){
//	var adresse = document.getElementById("adresse");
//	var div = document.getElementById('results');
//	var url = "ForumHierarchyCrosser?adresse="+ adresse.value;
//	$.get(url, function(data){
//	    div.innerHTML = data;
//	  });
//}
