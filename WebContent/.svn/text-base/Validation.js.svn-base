function message()
{
alert("This alert box was called with the onload event");
}

function checkIfBothSecurityQuestionsAreSame(str)
{
	var question1 = document.getElementById("q1dropdown").value;
	if(str==question1)
	{
		alert('Both the security questions cannot be same. Please change one of them.');
	}
}

function enableDisableWorkfielddropdown(str)
{
	if(str==' 0 hours/week')
	{
		document.getElementById("workfielddropdown").disabled=true;
	}
	else
	{
		document.getElementById("workfielddropdown").disabled=false;
	}
}

function updateStatus(num)
{	
	 var id_value='survey_id_'+num;
	var value='';
	try{ 
	value=document.getElementById(id_value).href;
	}catch(err ){
		alert("Error description: " + err.description);
	}
	
	 //alert("Invoked anchor "+ value);
if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
  xmlhttp=new XMLHttpRequest();
  }
else
  {// code for IE6, IE5
  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xmlhttp.onreadystatechange=function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
	  alert('processed request');
   }
  }
xmlhttp.open("GET","LandingPageForUser?value="+value,true);
xmlhttp.send();
}
