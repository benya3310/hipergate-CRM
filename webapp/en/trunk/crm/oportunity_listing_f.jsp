<%@ page language="java" session="false" contentType="text/html;charset=UTF-8" %>
<%@ include file="../methods/nullif.jspf" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 FRAMESET//EN" "http://www.w3.org/TR/REC-html40/FRAMESET.dtd">
<!-- +----------------------------------------------+ -->
<!-- | Marco principal de listado de oportunidades  | -->
<!-- | (c) KnowGate 2003                            | -->
<!-- +----------------------------------------------+ -->  
<HTML>
  <HEAD>
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
    <TITLE>hipergate :: [~Listado de Oportunidades~]</TITLE>
    <SCRIPT LANGUAGE="javascript" SRC="../javascript/getparam.js"></SCRIPT>
    <SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" DEFER="defer">
    <!--
      function setURL() {
        contactslist.location = "oportunity_listing.jsp?selected=" + getURLParam("selected") + "&subselected=" + getURLParam("subselected") + "&show=oportunities&skip=<%=nullif(request.getParameter("skip"),"0")%>&field=" + escape("<%=nullif(request.getParameter("field"))%>") + "&find=" + escape("<%=nullif(request.getParameter("find"))%>") + "&gu_contact=<%=nullif(request.getParameter("gu_contact"))%>" +"&gu_campaign=<%=nullif(request.getParameter("gu_campaign"))%>" + "&id_status=" + escape("<%=nullif(request.getParameter("id_status"))%>") + "&gu_sales_man=<%=nullif(request.getParameter("gu_sales_man"))%>&where=" + escape("<%=nullif(request.getParameter("where"))%>") + "<%=(nullif(request.getParameter("orderby")).length()>0 ? "&orderby=" + request.getParameter("orderby") : "")%>";
      }
    //-->
    </SCRIPT>    
  </HEAD>
  <FRAMESET NAME="contactsframe" ROWS="*,0" BORDER="0" FRAMEBORDER="0" onLoad="setURL()">
    <FRAME NAME="contactslist" FRAMEBORDER="no" MARGINWIDTH="16" MARGINHEIGHT="0" NORESIZE SRC="../common/blank.htm" >
    <FRAME NAME="contactsexec" FRAMEBORDER="no" MARGINWIDTH="0 marginheight=" NORESIZE SRC="../common/blank.htm" >
  </FRAMESET>
  <NOFRAMES>
    <BODY>
      <P>[~Esta p&aacute;gina usa marcos, pero su explorador no los admite.~]</P>
    </BODY>
  </NOFRAMES>
</HTML>