<%@ page language="java" import="com.knowgate.misc.Gadgets" session="false" contentType="text/html;charset=UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 FRAMESET//EN" "http://www.w3.org/TR/REC-html40/FRAMESET.dtd">  
<HTML>
  <HEAD>
    <TITLE>hipergate :: [~Editar Vendedor~]</TITLE>
  </HEAD>
  <FRAMESET NAME="editaddrf" ROWS="100%,*" BORDER="0" FRAMEBORDER="0">
    <FRAME NAME="editaddr" FRAMEBORDER="no" MARGINWIDTH="0" MARGINHEIGHT="0" NORESIZE SRC="salesman.jsp?gu_sales_man=<%=request.getParameter("gu_sales_man")%>&gu_workarea=<%=request.getParameter("gu_workarea")%>">
    <FRAME NAME="liststates" FRAMEBORDER="no" MARGINWIDTH="0" MARGINHEIGHT="0" NORESIZE SRC="blank.htm">
    </FRAMESET>
    <NOFRAMES>
      <BODY>
	<P>[~Esta p&aacute;gina usa marcos, pero su explorador no los admite.~]</P>
      </BODY>
    </NOFRAMES>
  </FRAMESET>
</HTML>
