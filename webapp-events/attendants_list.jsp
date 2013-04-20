<%@ page import="java.util.Date,java.sql.Timestamp,com.knowgate.jdc.JDCConnection,com.knowgate.acl.ACL,com.knowgate.acl.ACLUser,com.knowgate.workareas.WorkArea,com.knowgate.dataobjs.*" language="java" session="false" contentType="text/xml;charset=UTF-8" %><%@ include file="methods/dbbind.jsp" %><% 
/*
  Copyright (C) 2003-2010  Know Gate S.L. All rights reserved.
                           C/Oña, 107 1º2 28050 Madrid (Spain)

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.

  2. The end-user documentation included with the redistribution,
     if any, must include the following acknowledgment:
     "This product includes software parts from hipergate
     (http://www.hipergate.org/)."
     Alternately, this acknowledgment may appear in the software itself,
     if and wherever such third-party acknowledgments normally appear.

  3. The name hipergate must not be used to endorse or promote products
     derived from this software without prior written permission.
     Products derived from this software may not be called hipergate,
     nor may hipergate appear in their name, without prior written
     permission.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

  You should have received a copy of hipergate License with this code;
  if not, visit http://www.hipergate.org or mail to info@hipergate.org
*/

  final String sGuUser = request.getParameter("gu_writer");
  final String sTxPass = request.getParameter("tx_pwd");
  final String sGuWrkA = request.getParameter("gu_workarea");
  final String sGuActy = request.getParameter("gu_activity");
  final String sTxName = request.getParameter("tx_name");
  final String sTxSurn = request.getParameter("tx_surname");
  final String sSnPass = request.getParameter("sn_passport");
  
  final String PAGE_NAME = "activity_list";

  JDCConnection oConn = null;  
      
  try {
    oConn = GlobalDBBind.getConnection(PAGE_NAME);
    
    // ********************************************
    // Begin authenticate user session from cookies
    
    ACLUser oUser = new ACLUser();
    if (oUser.load(oConn, new Object[]{sGuUser})) {
      short iStatus = ACL.autenticate (oConn, oUser.getString(DB.gu_user), sTxPass, ACL.PWD_CLEAR_TEXT);
      if (iStatus<0) {
        out.write("KO¨"+ACL.getErrorMessage(ACL.USER_NOT_FOUND));
        oConn.close(PAGE_NAME);
        return;
      } else if (!WorkArea.isAnyRole(oConn, sGuWrkA, oUser.getString(DB.gu_user))) {
        out.write("KO¨"+ACL.getErrorMessage(ACL.WORKAREA_ACCESS_DENIED));      
        oConn.close(PAGE_NAME);
				return;
      }
    } else {
      out.write("KO¨"+ACL.getErrorMessage(ACL.USER_NOT_FOUND));
      oConn.close(PAGE_NAME);
      return;
    }

    // End authenticate user session from cookies
    // ******************************************
    
    
    DBSubset oAttns = new DBSubset (DB.k_contacts+" c", // +","+DB.k_x_activity_audience+" a",
    																"c."+DB.gu_contact+",c."+DB.tx_name+",c."+DB.tx_surname+",c."+DB.sn_passport,
    																"c."+DB.gu_workarea+"=? AND "+
    																(sTxName==null ? "" : "c." + DB.tx_name+" LIKE ? AND ")+
    																(sTxSurn==null ? "" : "c." + DB.tx_surname+" LIKE ? AND ")+
    																(sSnPass==null ? "" : "c." + DB.sn_passport+" LIKE ? AND ")+
                                    // "c."+DB.gu_contact+"=a."+DB.gu_contact+ " AND a."+DB.gu_activity+"=? AND "+
                                    // "("+DB.bo_went+" IS NULL OR "+DB.bo_went+"=0) AND "+
                                    "1=1 ORDER BY 2,3", 20);
    
    oAttns.setMaxRows(20);

    int iAttns = 0;
    if (sTxName!=null)
      iAttns = oAttns.load(oConn, new Object[]{sGuWrkA, sTxName.toUpperCase()+"%"});
    else if (sTxSurn!=null)
      iAttns = oAttns.load(oConn, new Object[]{sGuWrkA, sTxSurn.toUpperCase()+"%"});
    else if (sSnPass!=null)
      iAttns = oAttns.load(oConn, new Object[]{sGuWrkA, sSnPass+"%"});
    else
      iAttns = oAttns.load(oConn, new Object[]{sGuWrkA, sGuActy});
            
    oConn.close(PAGE_NAME);

    out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?><results count=\""+String.valueOf(iAttns)+"\">");

    for (int s=0; s<iAttns; s++)
      out.write("<rs id=\""+oAttns.getString(0,s)+"\" info=\""+oAttns.getStringNull(3,s,"")+"\"><![CDATA["+oAttns.getStringNull(1,s,"")+" "+oAttns.getStringNull(2,s,"")+"]]></rs>");
    
    out.write("</results>");

  }
  catch (Exception e) {  
    disposeConnection(oConn,PAGE_NAME);
    oConn = null;
    out.write("KO¨"+e.getClass()+" "+e.getMessage());
  }
%>