<%@ page language="java" session="false" %>
<%@ include file="../methods/dbbind.jsp" %>
<%@ include file="../methods/cookies.jspf" %>
<%@ include file="../methods/authusrs.jspf" %>
<% boolean bIsGuest = isDomainGuest (GlobalDBBind, request, response); %>
<HTML>
  <HEAD>
    <TITLE>hipergate ::</TITLE>
    <SCRIPT LANGUAGE="JavaScript" SRC="../javascript/cookies.js"></SCRIPT>
    <SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript">
      <!--
      var skin = getCookie("skin");
      if (""==skin) skin="xp";
      document.write ('<LINK REL="stylesheet" TYPE="text/css" HREF="../skins/' + skin + '/styles.css">');                  
      //-->
    </SCRIPT>  
  </HEAD>
  <BODY >
    <FORM>
    <CENTER>
<% if (bIsGuest) { %>
      <INPUT TYPE="button" CLASS="pushbutton" VALUE="[~Siguiente~] >>" onClick="alert('[~Su nivel de privilegio como Invitado no le permite efectuar esta acciÃ³n~]')">
<% } else { %>
      <INPUT TYPE="button" CLASS="pushbutton" VALUE="[~Siguiente~] >>" onClick="window.top.frames[1].choose()">
<% } %>
      &nbsp;&nbsp;&nbsp;<INPUT TYPE="button" CLASS="closebutton" VALUE="[~Cancelar~]" onClick="window.parent.close()">
    </CENTER>
    </FORM>
  </BODY>
</HTML>