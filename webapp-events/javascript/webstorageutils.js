/***************************************************************
  JavaScript Functions for local plain text files read and write
*/

// ----------------------------------------------------------------------------

function makeLocalStorageName() {
  var dt = new Date();
  return "cs_"+ String(year)+"_"+(dt.getMonth()+1<=9 ? "0" : "")+String(dt.getMonth()+1)+"_"+(dt.getDate()<=9 ? "0" : "")+String(dt.getDate())+"_log";
} // makeLocalStorageName

// ----------------------------------------------------------------------------

function readLine(fn, id) {
	var lo = null;
	var ln;	               // Line String
	var la = null;         // Line Array
	var il = id.length;    // Id. length
  var cl = ColumnNames.length;
  var tf = window.localStorage.getItem(fn);
  if (tf!=null) {
  	lo = new Object();
  	tf = tf.split("\n");
    for (var l=0; l<tf.length; tf++) {
      if (tf[l].substr(0,il)==id || 0==id) {
        la = tf[l].split("|");
        for (var c=0; c<cl; c++) {
          lo[ColumnNames[c]] = la[c];
        } // next
        break;
      } // fi
    } // next
    if (la==null) alert ("Line "+id+" not found");
  } else {
    // alert ("Record not found");
  }
  return lo;
} // readLine

// ----------------------------------------------------------------------------

function writeLine(fn, ln) {
	var fl;
	try {
		ln = ln.replace(/\r/g,'');
		ln = ln.replace(/\n/g,'');
		fl = window.localStorage.getItem(fn); 
		if (fl==null) {
			window.localStorage.setItem(fn,ln);
		} else {
			if (fl.length==0)
			  window.localStorage.setItem(fn,ln);
			else
			  window.localStorage.setItem(fn,fl+"\n"+ln);
		}
	} catch (fileException) {
		alert('Error al grabar el localStorage' + ' ' + fn + '\n' + fileException.description);
		return false;
	}
	return true;
} // writeLine

// ----------------------------------------------------------------------------

function saveRecord(frm,rky) {
  var frm = document.forms[0];

  if (frm.gu_record.value.length==0) {
    alert ("Record identifier is empty");
    return false;
  }
  
  var ln = getValueOf(frm.elements[ColumnNames[0]]);
	for (var c=1; c<ColumnNames.length; c++) {
		if (frm.elements[ColumnNames[c]]) {
		  var vl = getValueOf(frm.elements[ColumnNames[c]]);
	    if (vl=="null" || vl==null) vl = "";
	    ln += "|" + vl;
    } else {
	    ln += "|";    
    }
  } // next

  var fl = localStorage.getItem(rky);
        
  if (fl==null)
    writeLine(rky, ColumnNames.join("|"));

  writeLine(rky, ln);
  
  // document.getElementById("feedback").innerHTML = (ok ? "Registro Grabado con Éxito" : "No fue posible grabar el registro correctamente");
} // saveRecord

// ----------------------------------------------------------------------------

function clearRecord(frm) {
  window.localStorage.removeItem(frm.name);
}

// ----------------------------------------------------------------------------

function saveSession(frm) {
  sessionStorage.session = frm.gu_writer.value+"|"+frm.gu_workarea.value+"|"+frm.tx_main_email.value+"|"+frm.tx_pwd.value;
}

// ----------------------------------------------------------------------------

function loadSession(frm) {
	var b;
	var s = sessionStorage.session;
	if (s!=null) {
	  s = s.split("|");
	  frm.gu_writer.value = s[0];
	  frm.gu_workarea.value = s[1];
	  frm.tx_main_email.value = s[2];
	  frm.tx_pwd.value = s[3];
	  b = true;
	} else {
	  b = false;
	}
	return b;
}

// ----------------------------------------------------------------------------

function seekZipcode(zipcode,city) {
	var e;
	var fz = httpCachedRequestText("z"+zipcode.substr(0,2),"zipcodes/"+zipcode.substr(0,2)+",txt");
	if (fz!=null) {
	  fz = fz.split("\n");	
	  for (var ln=0; ln<fz.length; ln++) {
		  var zc = fz[ln];
		  if (zc==zipcode) {
		    e = '<rs id="'+zc[1]+'" info=""><![CDATA['+zc[0]+']]></rs>';	      
	    }
	  } //next
	} // fi
} // seekZipcode

// ----------------------------------------------------------------------------

function checkEmail() {
			var frm = document.forms[0];
			if (frm.tx_email.value.length>0) {
			  if (!check_email(frm.tx_email.value.toLowerCase())) {
		      alert (Resources["msg_tx_email_invalid"]);
		      return false;
			  }
			  
				var dup = false;
				var emails = localStorage.getItem("emails");
			  if (emails!=null) {
			    emails = emails.split("\n");
			    for (var e=0; e<emails.length && !dup; e++) {
			      if (emails[e].toLowerCase()==frm.tx_email.value.toLowerCase())
			        dup = true;
			    }
			  }

				if (dup) {
          alert (Resources["msg_tx_email_duplicated"]);
				  document.forms[0].tx_email.focus();          	  
					return false;
				}			  

			  if (!req) {
			    req = createXMLHttpRequest();
			    req.onreadystatechange = processMailLookUp;
  				try {
			      req.open("GET", "activity_addr_exists.jsp?email="+frm.tx_email.value+"&workarea="+frm.gu_workarea.value+"&activity="+frm.gu_activity.value, true);
			      req.send(null);
			    } catch (xcpt) {
			      req = false;
			    }
			  } // fi
			}
} // checkEmail

// ----------------------------------------------------------------------------

function sync(rky) {
  var errcount= 0;
  var errlines= "";
  var errmsgs = new Array();
  var records = localStorage.getItem(rky);
  
	if (null!=records) {
    var ses = sessionStorage.session;
	  if (ses==null) {
	    alert ("Access Denied. No user session found.");
    }	    
	  else {
	    ses = ses.split("|");

	    records = records.split("\n");		
      var pending = records.length;
	    var colvalues;
	    for (var r=1; r<pending; r++) {
		    var colvalues = records[r].split("|");
		    for (var c=0; c<colvalues.length; c++)
		      colvalues[c] = escape(encodeURI(colvalues[c]));
		    colvalues = colvalues.join("|");
		    var par = "gu_workarea="+ses[1]+"&nm_machine=localhost&gu_list=&gu_writer="+ses[0]+"&tx_pwd="+escape(encodeURI(ses[3]))+"&tx_coldelimiter=|&tx_colnames="+records[0]+"&tx_colvalues="+colvalues;
		    var pst = createXMLHttpRequest();
		    pst.open("POST","activity_audience_load.jsp",false);
		    pst.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		    pst.setRequestHeader("Content-length", par.length);
		    pst.setRequestHeader("Connection", "close");
		    try {
		      pst.send(par);
		      if (pst.responseText.indexOf("1.0:OK")<0) {
		        errlines += (++errcount==1 ? records[0] : "") +"\n" + records[r];
		        errmsgs.push(records[r]+"<br/>"+pst.responseText);
          }
          document.getElementById("pending").innerHTML = Resources["msg_pending_registers"]+": "+String(--pending);
	      } catch (xcpt) {
		      errlines += (++errcount==1 ? records[0] : "") +"\n" + records[r];
		      errmsgs.push(records[r]+"<br/>"+xcpt.name+" "+xcpt.message);
	      }
	    } // next
	    if (errlines.length>0)
	      localStorage.setItem(rky, errlines);
	    else
	    	localStorage.removeItem(rky);
	    if (errcount==0)
        document.getElementById("feedback").innerHTML = Resources["msg_sync_sucessfully_completed"];
      else
        document.getElementById("feedback").innerHTML = "Sincronizacion completada con "+String(errcount)+" errores <br/>"+errmsgs[0];
    } // fi (sessionStorage.session)
  } // fi (records)
} // sync
