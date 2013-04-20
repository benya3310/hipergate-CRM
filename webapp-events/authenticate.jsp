<%@ page import="com.knowgate.jdc.JDCConnection,com.knowgate.acl.ACL,com.knowgate.acl.ACLUser,com.knowgate.workareas.WorkArea,com.knowgate.dataobjs.*" language="java" session="false" contentType="text/plain;charset=UTF-8" %><%@ include file="methods/dbbind.jsp" %><% 
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

  final String PAGE_NAME = "authenticate";

  final String sTxEMail = request.getParameter("tx_main_email");
  final String sTxPassw = request.getParameter("tx_pwd");
  String sGuWrkA = request.getParameter("gu_workarea");
  if (sGuWrkA==null) sGuWrkA = "";
  String sGuUser = request.getParameter("gu_writer");
  if (null==sGuUser) sGuUser = "";
  
  JDCConnection oConn = null;  
  short iStatus = ACL.INTERNAL_ERROR;
   
  try {
    oConn = GlobalDBBind.getConnection(PAGE_NAME);
    if (sGuUser.length()==0) sGuUser = ACLUser.getIdFromEmail(oConn, sTxEMail);
    if (null!=sGuUser) {
      ACLUser oUser = new ACLUser(oConn,sGuUser);
      iStatus = ACL.autenticate (oConn, oUser.getString(DB.gu_user), sTxPassw, ACL.PWD_CLEAR_TEXT);
      if (iStatus<0) {
        out.write("KO¨("+String.valueOf(iStatus)+") "+ACL.getErrorMessage(iStatus));
      } else if (oUser.isNull(DB.gu_workarea)) {
        out.write("KO¨("+String.valueOf(ACL.WORKAREA_NOT_SET)+") "+ACL.getErrorMessage(ACL.WORKAREA_NOT_SET));
      } else {
      	if (sGuWrkA.length()>0) {
      	  if (!sGuWrkA.equals(oUser.getString(DB.gu_workarea))) {
            out.write("KO¨("+String.valueOf(ACL.WORKAREA_ACCESS_DENIED)+") "+ACL.getErrorMessage(ACL.WORKAREA_ACCESS_DENIED));      	  
      	  } else {
      	  	if (WorkArea.isAnyRole(oConn, sGuWrkA, sGuUser))
              out.write("OK¨"+oUser.getString(DB.gu_user)+"`"+sTxEMail+"`"+sTxPassw+"`"+sGuWrkA);
						else
              out.write("KO¨("+String.valueOf(ACL.WORKAREA_ACCESS_DENIED)+") "+ACL.getErrorMessage(ACL.WORKAREA_ACCESS_DENIED));							
      	  }
      	} else {
      	  if (WorkArea.isAnyRole(oConn, oUser.getString(DB.gu_workarea), sGuUser))
            out.write("OK¨"+oUser.getString(DB.gu_user)+"`"+sTxEMail+"`"+sTxPassw+"`"+oUser.getString(DB.gu_workarea));
          else
            out.write("KO¨("+String.valueOf(ACL.WORKAREA_ACCESS_DENIED)+") "+ACL.getErrorMessage(ACL.WORKAREA_ACCESS_DENIED));							
        }
      }
    } else {
      out.write("KO¨("+String.valueOf(ACL.USER_NOT_FOUND)+") "+ACL.getErrorMessage(ACL.USER_NOT_FOUND));
    }
    
    oConn.close(PAGE_NAME);
  }
  catch (Exception e) {  
    disposeConnection(oConn,PAGE_NAME);
    oConn = null;
    out.write("KO¨"+e.getClass().getName()+" "+e.getMessage());
  }

%>