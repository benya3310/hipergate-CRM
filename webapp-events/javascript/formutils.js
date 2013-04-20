
// ----------------------------------------------------------------------------

var ColumnNames = new Array("gu_record","nm_machine","dt_created","gu_activity","gu_address","gu_company","gu_contact","gu_geozone","gu_list","gu_sales_man","gu_workarea","id_batch","gu_writer","id_transact","ix_address","bo_allows_ads","bo_confirmed","bo_paid","bo_private","bo_went","contact_person","de_title","direct_phone","dt_birth","dt_confirmed","dt_paid","fax_phone","full_addr","home_phone","id_country","id_gender","id_legal","id_ref","id_sector","id_state","id_status","im_paid","im_revenue","mn_city","mov_phone","nm_commercial","nm_country","nm_legal","nm_state","nm_street","nu_employees","nu_street","ny_age","other_phone","po_box","sn_passport","tp_billing","tp_company","tp_location","tp_origin","tp_street","tr_title","tx_addr1","tx_addr2","tx_comments","tx_dept","tx_division","tx_email","tx_franchise","tx_name","tx_remarks","tx_salutation","tx_surname","url_addr","work_phone","zipcode","id_data1","de_data1","tx_data1","id_data2","de_data2","tx_data2","id_data3","de_data3","tx_data3","id_data4","de_data4","tx_data4","id_data5","de_data5","tx_data5","id_data6","de_data6","tx_data6","id_data7","de_data7","tx_data7","id_data8","de_data8","tx_data8","id_data9","de_data9","tx_data9");

var left_padding = 10;

var top_padding = 40;

var inter_line = 30;

/********************************
  Data entry validation functions 
*/

function uidgen() {
  var uid  = "";
	var chst = "abcdefghijklmnopqrstuvwxyz";
	var clen = chst.length;
	for (var c=0; c<32; c++) {
	  uid += chst.charAt(Math.random()*clen);
	} // next
	return uid;
} // uidgen

// ----------------------------------------------------------------------------

function readParamIntoForm(frm, paramname, paramvalue) {
	var up;
	var tp;
	var fe = frm.elements[paramname];
	if (fe) {
	  var up = paramvalue;
	      up = (up==null || up=="null" || up=="NULL" ? "" : up);
	  if (up.length>0) {
	    var tp = frm.elements[paramname].type;
	    if (tp=="text" || tp=="hidden") {
	      frm.elements[paramname].value = up;
	    } else if (tp=="select-one") {
	      setCombo(frm.elements[paramname], up);
	    } else if (frm.elements[paramname].length) {
	      setCheckedValue(frm.elements[paramname],up);
	    } else {
	      frm.elements[paramname].checked = (frm.elements[paramname].value==up);
	    }
	  }
	} else {
	  // alert ("Element "+paramname+" not found at form");
	}
} // readParamIntoForm

// ----------------------------------------------------------------------------

function setDefaultValues(frm) {
  if (frm.gu_record.value=="" || frm.gu_record.value=="null" || frm.gu_record.value==null)
    frm.gu_record.value = uidgen();

  if (frm.dt_created.value=="" || frm.dt_created.value=="null" || frm.dt_created.value==null) 
    frm.dt_created.value = dateToString(new Date(), "ts"); 

  frm.nm_machine.value = "localhost";
}

// ----------------------------------------------------------------------------

function readRecordIntoForm(frm) {
  var ncols = ColumnNames.length;
  
  var lin = readLine(frm.name,0);
	
  if (lin!=null) {
    for (var c=0; c<ncols; c++) {    
      readParamIntoForm(frm,ColumnNames[c],lin[ColumnNames[c]]);
    } // next
  } // fi

  if (isDate(frm.dt_birth.value,"d")) {
    var dt = frm.dt_birth.value.split("-");
    setCombo(frm.sel_birth_year, dt[0]);
    setCombo(frm.sel_birth_month, dt[1]);
    setCombo(frm.sel_birth_day, dt[2]);
  }

  setDefaultValues(frm);

} // readRecordIntoForm

// ----------------------------------------------------------------------------

function readFormIntoRecord(frm) {
  
  var ncols = ColumnNames.length;

  setDefaultValues(frm);

  var ln = getValueOf(frm.elements[ColumnNames[0]]);
	for (var c=1; c<ncols; c++) {
		if (frm.elements[ColumnNames[c]]) {
			var vl = getValueOf(frm.elements[ColumnNames[c]]);
	    if (vl=="null" || vl==null) vl = "";
	    ln += "|" + vl;
	  } else {
	    ln += "|";
	  } // fi
	} // next
	clearRecord(frm);
  writeLine(frm.name, ln);
} // readFormIntoRecord

// ----------------------------------------------------------------------------

/*******************************************
  JavaScript Functions for validating inputs
*/

function check_name(frm) {
  if (frm.tx_name.style.visibility=="visible") {
	  if (frm.tx_name.className.indexOf("required")>=0 && frm.tx_name.value.length==0) {
		  alert (Resources["msg_tx_name_required"]);
		  frm.tx_name.focus();
		  return false;
		} else if (hasForbiddenChars(frm.tx_name.value)) {
		  alert (Resources["msg_tx_name_forbidden"]);
		  frm.tx_name.focus();
		  return false;		    	
		}	else {
		  if (frm.tx_name.className.indexOf("ttu")>=0)
		    frm.tx_name.value = frm.tx_name.value.toUpperCase();
		}
  }
  return true;
} // check_name

function check_surname(frm) {
  if (frm.tx_name.style.visibility=="visible") {
    if (frm.tx_surname.value.length==0) {
		  alert (Resources["msg_tx_surname_required"]);
		  frm.tx_surname.focus();
		  return false;
		} else if (hasForbiddenChars(frm.tx_surname.value)) {
		  alert (Resources["msg_tx_surname_forbidden"]);
		  frm.tx_surname.focus();
		  return false;		    	
		}	else {
		  if (frm.tx_surname.className.indexOf("ttu")>=0)
		    frm.tx_surname.value = frm.tx_surname.value.toUpperCase();
		}
  }
  return true;
} // check_surname

function check_mail(frm) {
  if (frm.tx_email.style.visibility=="visible") {
    if (frm.tx_email.value.length==0) {
		  alert (Resources["msg_tx_email_required"]);
		  frm.tx_email.focus();
		  return false;
		} else if (!check_email(frm.tx_email.value.toLowerCase())) {
		  alert (Resources["msg_tx_email_invalid"]);
		  frm.tx_email.focus();
		  return false;
		} else {
		  if (frm.tx_surname.className.indexOf("ttl")>=0)
		    frm.tx_email.value = frm.tx_email.value.toLowerCase();
		}
  }
  return true;
}	

function check_gender(frm) {
  if (frm.id_gender.style.visibility=="visible") {
    if (getCheckedValue(frm.id_gender)==null) {
		  alert (Resources["msg_id_gender_required"]);
		  return false;
		}
  }
  return true;
}

function check_zipcode(frm,countrycode) {
  var cpe = /[0-5][\d]{4}/;
  if (frm.zipcode.style.visibility=="visible") {
    if (countrycode=="es" && !frm.zipcode.value.match(cpe)) {
	    alert (Resources["msg_zipcode_invalid"]);
		  frm.zipcode.focus();
		  return false;
		}
    if (frm.id_state.style.visibility=="visible" && getCombo(frm.id_state)!=frm.zipcode.value.substring(0,2)) {
	    alert (Resources["msg_zipcode_mismatch"]);
		  frm.zipcode.focus();
		  return false;    	
    }
  }
  return true;
} // check_zipcode

function check_city(frm) {
  if (frm.mn_city.style.visibility=="visible") {
    if (frm.mn_city.value.length==0) {
		  alert (Resources["msg_mn_city_required"]);
		  frm.mn_city.focus();
		  return false;
		} else if (hasForbiddenChars(frm.mn_city.value)) {
		  alert (Resources["msg_mn_city_forbidden"]);
		  frm.mn_city.focus();
		  return false;		    	
		}	else {
		  if (frm.mn_city.className.indexOf("ttu")>=0)
		    frm.mn_city.value = frm.mn_city.value.toUpperCase();
		}
  }
  return true;
} // check_city

function check_birth_date(frm) {
  if (frm.dt_birth.style.visibility=="visible" || frm.sel_birth_day.style.visibility=="visible") {
    if (frm.dt_birth.value.length==0) {
      alert (Resources["msg_dt_birth_required"]);
		  return false;		    	
    } else if (!isDate (frm.dt_birth.value, "d")) {
      alert (Resources["msg_dt_birth_invalid"]);
		  return false;		    	    
    }
  }
  return true;
} // check_birth_date

function check_passport(frm,countrycode) {
  var cpe = /[0-5][\d]{4}/;
  if (frm.sn_passport.style.visibility=="visible") {
    if (frm.sn_passport.value.length==0) {
      alert (Resources["msg_sn_passport_required"]);
      frm.sn_passport.focus();
		  return false;   	
    }
  }
  return true;
} // check_passport

// ----------------------------------------------------------------------------

function check_all(frm,countrycode) {
  return check_name(frm) && check_surname(frm) && check_mail(frm) && check_gender(frm) && check_city(frm) && check_zipcode(frm,countrycode) &&
         check_passport(frm,countrycode) && check_birth_date(frm);
} // check_all

// ----------------------------------------------------------------------------


var req = false;		  

function processMailLookUp() {
        if (req.readyState == 4) {
          if (req.status == 200) {
          	if (req.responseText.substr(0,5)=="found") {
          	  alert (Resources["msg_tx_email_duplicated"]);
							document.forms[0].tx_email.focus();          	  
          	}
          	req = false;
          } else {
            // No conectivity to host available
            // alert ("status="+String(req.status));
          }
        }
} // processMailLookUp


/***************************************************
  JavaScript Functions for displaying data on screen
*/

var tabidx = 1;

function D (id, tp, lf, nm) {
  var dv = document.getElementById("div_"+id);
  var lb = document.getElementById("lbl_"+id);
  if (id=="sel_birth") {
    ui = document.forms[0].elements[id+"_year"];
    ui.style.visibility = "visible";
    ui.style.display = "block";  
    ui.tabIndex = tabidx++;
    if (nm.length>0) ui.className = nm;
    ui = document.forms[0].elements[id+"_month"];
    ui.style.visibility = "visible";
    ui.style.display = "block";  
    ui.tabIndex = tabidx++;
    if (nm.length>0) ui.className = nm;
    ui = document.getElementById(id+"_day");
    ui.style.visibility = "visible";
    ui.style.display = "block";  
    ui.tabIndex = tabidx++;
    if (nm.length>0) ui.className = nm;
    if (lb) lb.innerHTML = Resources["dt_birth"];
  } else {
  	ui = document.forms[0].elements[id];
  	if (ui.length && ui.type!="select-one") {
		  for (var e=0; e<ui.length; e++) {
        ui[e].tabIndex = tabidx++;
        if (nm.length>0) ui[e].className = nm;
      } // next    
		} else {
      ui.style.visibility = "visible";
      ui.style.display = "block";  
      ui.tabIndex = tabidx++;
      if (nm.length>0) ui.className = nm;
		}
    if (lb) lb.innerHTML = Resources[id];
  }
  dv.style.top = tp;
  dv.style.left= lf;
  dv.style.visibility = "visible";
  dv.style.display = "block";  
} // D

function B (id, tp, lf, tx) {
  var div = document.getElementById("div_"+id);
	
	div.style.display="block";
	div.style.visibility="visible";
	div.style.top=tp;
  div.style.left=lf;
	document.forms[0].elements["btn_"+id].value = tx;
}

// ----------------------------------------------------------------------------
