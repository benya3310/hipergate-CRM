<%@ page import="java.util.*,java.io.*,java.math.*,java.io.IOException,java.net.URLDecoder,java.sql.SQLException,com.knowgate.jdc.*,com.knowgate.dataobjs.*,com.knowgate.acl.*,com.knowgate.hipergate.*,com.knowgate.dataxslt.db.*,com.knowgate.dfs.FileSystem,com.knowgate.misc.*" language="java" session="false" %>
<%@ include file="../methods/dbbind.jsp" %>
<%@ include file="../methods/cookies.jspf" %>
<%@ include file="../methods/authusrs.jspf" %>
<%@ include file="../methods/clientip.jspf" %>
<%@ include file="../methods/reqload.jspf" %>
<%
/*
  Copyright (C) 2003  Know Gate S.L. All rights reserved.
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

  //Recuperar parametros generales
  String id_domain = getCookie(request,"domainid","");
  String n_domain = request.getParameter("n_domain");
  String gu_workarea = request.getParameter("gu_workarea");
  String sStorage = Environment.getProfileVar(GlobalDBBind.getProfileName(),"storage","/opt/knowgate/knowgate/storage");
  
  //Inicializaciones
  if (autenticateSession(GlobalDBBind, request, response)<0) return;
  String id_user = getCookie (request, "userid", null);
  FileSystem oFs = new FileSystem();
  oFs.os(FileSystem.OS_UNIX);
    
  //Recuperar datos de formulario
  String chkItems = request.getParameter("checkeditems");  
  String a_items[] = Gadgets.split(chkItems, ',');
  
  JDCConnection oConn = GlobalDBBind.getConnection("images_delete");  
  oConn.setAutoCommit (false);
  for (int i=0;i<a_items.length;i++)
  {
   oFs.delete("file://" +  a_items[i]);
   File oFile = new File("file://" + a_items[i]);
   Image oImg = new Image(oConn,oFile,a_items[i]);
   oImg.delete(oConn);
   oImg = null;
   oFile = null;
  }
  oConn.commit();
  oFs = null;
  oConn.close("images_delete");
  oConn = null;
%>
<HTML><HEAD><TITLE>[~Espere~]...</TITLE>
<script>
history.back();
</script>
</HEAD><BODY></BODY></HTML>