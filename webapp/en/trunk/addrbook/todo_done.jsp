<%@ page import="java.util.*,java.io.IOException,java.net.URLDecoder,java.sql.PreparedStatement,java.sql.SQLException,com.knowgate.jdc.*,com.knowgate.dataobjs.*,com.knowgate.acl.*,com.knowgate.hipergate.*,com.knowgate.misc.Gadgets" language="java" session="false" %>
<%@ include file="../methods/page_prolog.jspf" %><%@ include file="../methods/dbbind.jsp" %><%@ include file="../methods/cookies.jspf" %><%@ include file="../methods/authusrs.jspf" %>
<%
  if (autenticateSession(GlobalDBBind, request, response)<0) return;
  
  String id_user = getCookie (request, "userid", null);

  String a_items[] = Gadgets.split(request.getParameter("checkeditems"), ',');
    
  JDCConnection oCon = null;
      
  try {
    oCon = GlobalDBBind.getConnection("to_do_done");

    oCon.setAutoCommit (false);

    PreparedStatement oStm = oCon.prepareStatement("UPDATE " + DB.k_to_do + " SET " + DB.tx_status + "='DONE' WHERE " + DB.gu_to_do + "=?");
      
    for (int i=0;i<a_items.length;i++) {
      oStm.setString (1, a_items[i]);
      oStm.executeUpdate();
      
    } // next ()
    
    oStm.close();

    oCon.commit();

    oCon.setAutoCommit (true);
    
    com.knowgate.http.portlets.HipergatePortletConfig.touch(oCon, id_user, "com.knowgate.http.portlets.CalendarTab", getCookie(request,"workarea",""));
     
    oCon.close("to_do_done");
  } 
  catch(SQLException e) {
      disposeConnection(oCon,"to_do_done");
      oCon = null; 

      if (com.knowgate.debug.DebugFile.trace) {      
        com.knowgate.dataobjs.DBAudit.log ((short)0, "CJSP", sUserIdCookiePrologValue, request.getServletPath(), "", 0, request.getRemoteAddr(), e.getMessage(), "");
      }

      response.sendRedirect (response.encodeRedirectUrl ("../common/errmsg.jsp?title=SQLException&desc=" + e.getMessage() + "&resume=_back"));
    }
  
  if (null==oCon) return;
  
  oCon = null; 

  out.write("<HTML><HEAD><TITLE>Wait...</TITLE><" + "SCRIPT LANGUAGE='JavaScript' TYPE='text/javascript'>window.document.location='to_do_listing.jsp?selected=" + request.getParameter("selected") + "&subselected=" + request.getParameter("subselected") + "'<" + "/SCRIPT" +"></HEAD></HTML>"); 
 %>
<%@ include file="../methods/page_epilog.jspf" %>