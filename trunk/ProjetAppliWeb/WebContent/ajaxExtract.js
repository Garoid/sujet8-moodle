
var req;
function extract(){

	var adresse = document.getElementById("adresse");
	var url = "ForumHierarchyCrosser?adresse="+ adresse.value;
	//alert(url);
	req = new XMLHttpRequest();
	req.onreadystatechange = callback;
    req.open("POST",url,true);
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

