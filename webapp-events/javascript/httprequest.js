function createXMLHttpRequest() {
  var req = false;
  // branch for native XMLHttpRequest object
  if (window.XMLHttpRequest) {
    try {
      req = new XMLHttpRequest();
    } catch(e) {
      alert("new XMLHttpRequest() failed"); req = false;
    }
  } else if (window.ActiveXObject) {
    try {
      req = new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e) {
      try {
        req = new ActiveXObject("Microsoft.XMLHTTP");
      } catch(e) {
      	alert("ActiveXObject(Microsoft.XMLHTTP) failed"); req = false; }
      }
   } // fi
   return req;
} // createXMLHttpRequest

function httpRequestText(fromurl) {
  var PrivateTextRequest = createXMLHttpRequest();
  PrivateTextRequest.open("GET",fromurl,false);
  PrivateTextRequest.send(null);
  return PrivateTextRequest.responseText;
}

function httpPostForm(tourl,frm) {
  var params = "";
  var le = frm.elements.length;

  for (var e=0; e<le; e++) {

	  var tp = frm.elements[e].type;
	  var vl = null;
	  if (tp=="text" || tp=="hidden")
	    vl = frm.elements[e].value;
	  else if (tp=="select-one")
	    if (frm.elements[e].selectedIndex<0)
	      vl = "";
	    else
	    	vl = frm.elements[e].options[frm.elements[e].selectedIndex];
	  else if (frm.elements[e].length)
      for (var r=0; r<frm.elements[e].length && vl==null; r++)
        if (frm.elements[e].checked) vl = frm.elements[e].value;
	  else {
	  	alert ("Unknown type "+tp+" for form element "+frm.elements[e].name);
	  	// vl = (frm.elements[e].checked ? frm.elements[e].value : "");
	  }
    params += (params.length==0 ? "" : "&") + frm[e].name+"="+frm[e].value;
  } // next

  var PrivateFormPost = createXMLHttpRequest();
  PrivateFormPost.open("POST",tourl,false);
  PrivateFormPost.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
  PrivateFormPost.setRequestHeader("Content-length", params.length);
  PrivateFormPost.setRequestHeader("Connection", "close");
  PrivateFormPost.send(params);
  return PrivateFormPost.responseText;
}

function httpCachedRequestText(resourcename,fromurl) {
  var txt = null; // window.localStorage.getItem(resourcename);
  if (null==txt) {
  	if (navigator.onLine) {
      txt = httpRequestText(fromurl);
      window.localStorage.setItem(resourcename,txt);
    } else {
      alert ("Not connected!");
    }    
  } // fi
  return txt;
}

function httpCachedPostForm(resourcename) {
  var txt = null; // window.localStorage.getItem(resourcename);
  if (null==txt) {
  	if (navigator.onLine) {
		  txt = httpPostForm(resourcename+".jsp",document.forms[0]);
      window.localStorage.setItem(resourcename,txt);
    } else {
      alert ("Not connected!");
    }
  } else if (txt.substring(0,2)=="KO" && navigator.onLine) {
		txt = httpPostForm(resourcename+".jsp",document.forms[0]);
    window.localStorage.setItem(resourcename,txt);  	
  }
  return txt;
}
