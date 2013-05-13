import java.util.Date;
import java.util.ArrayList;
import java.util.Properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.knowgate.jdc.JDCConnection;
import com.knowgate.dataobjs.DB;
import com.knowgate.dataobjs.DBBind;
import com.knowgate.dataobjs.DBCommand;
import com.knowgate.dfs.FileSystem;
import com.knowgate.dfs.chardet.CharacterSetDetector;
import com.knowgate.misc.Gadgets;
import com.knowgate.dataxslt.db.PageSetDB;
import com.knowgate.hipermail.MailAccount;
import com.knowgate.hipermail.SendMail;
import com.knowgate.crm.GlobalBlackList;
import com.knowgate.crm.DistributionList;

// ************************************************************************
// Este script BeanShell recibe de su proceso padre las variables globales:
// ThisEvent -> instancia de com.knowgate.scheduler.Event
// JDBCConnection -> instancia de com.knowgate.jdc.JDCConnection

ThisEvent.log("Start script sendmailtest "+(new Date().toString()));

Integer returnValue = new Integer(0);
Integer ErrorCode = new Integer(0);
String  ErrorMessage = "";

// DataSource conectado a la BB.DD. 127.0.0.1:5432/hipergate7 en el fichero /etc/hipergate.cnf
ThisEvent.log("Getting DataSource");
DBBind oDbb = new DBBind();
ThisEvent.log("Got DBBind");

FileSystem oFs = new FileSystem();

ThisEvent.log("Got FileSystem");

// *********************************
// Declaracion de variables globales

final String GU_REAL = "ac1263a41235762fe5b1000c49e09610"; // GUID del Area de Trabajo de Promocion en la tabla k_workareas
final String GU_USER = "ac1263a412357dc444c100038ecbd2fc"; // GUID del usuario administrador de Promocion en la tabla k_users

final String gu_workarea = GU_REAL;
final String id_command = "MAIL";
final String id_status = "1";
final String bo_webbeacon = "1"; // Incluir un web beacon para trazar las aperturas
final String bo_clickthrough = "1"; // Pasar todos los enlaces por el redirector que recoge las estadisticas

// GUID de la plantilla a enviar en la tabla k_pagesets
final String gu_pageset = "ac1263a4135cd9ce7b81002cec8f04eb";

// Parametros adicionales que controlan si el mail llevara las imagenes adjuntas o enlazadas,
// si se añadira un web beacon y si se registrara el clickthrough
final String tx_parameters = "gu_pageset:"+gu_pageset+",bo_attachimages:0,bo_webbeacon:"+bo_webbeacon+",bo_clickthrough:"+bo_clickthrough+",workareasput:"+oDbb.getPropertyPath("workareasput");

// Directorio que contiene los archivos necesarios para componer el mail
final String target_dir = "/opt/apache-tomcat-6.0.20/webapps/hipergate/workareas/"+gu_workarea+"/apps/Mailwire/html/"+gu_pageset+"/";

// ********************************

try {

  ThisEvent.log("Creating recipients list");

  // ***********************************************
  // Aqui es donde se crea la lista de destinatarios
  // El mail se enviara a todas las direcciones que
  // se incluyan en el array aRecipients

  if (true) {
    String[] aRecipients = new String[]{"luisc@knowgate.es"};
    
    ThisEvent.log("Collecting files to be sent");
    
    String[] aFiles = new File(target_dir).list();
    String sHtmlFile = null, sPlainFile = null;
    ArrayList oAttachments = new ArrayList();
    
    // ***********************************************
    // Esta seccion busca el el directorio donde estan
    // los archivos para encontrar el cuerpo en html y
    // texto simple, las imagenes, adjuntos, etc.
    
    if (aFiles!=null) {
      for (int f=0; f<aFiles.length; f++) {
        String sFileName = aFiles[f].toLowerCase();
        if ((sFileName.endsWith(".htm") || sFileName.endsWith(".html")) &&
            (id_command.equals("SEND") || !sFileName.endsWith("_.html"))) {
          if (sHtmlFile!=null) {
          	throw new FileNotFoundException("More than one HTML body part was uploaded for the e-mail "+sHtmlFile+", "+sFileName);
          }
          sHtmlFile = aFiles[f];
        }
        if (sFileName.endsWith(".txt")) {
          if (sPlainFile!=null) {
          	throw new FileNotFoundException("More than one HTML body part was uploaded for the e-mail "+sPlainFile+", "+sFileName);
          }
          sPlainFile = aFiles[f];
        }
        if (sFileName.endsWith(".pdf") || sFileName.endsWith(".doc") || sFileName.endsWith(".xls") || sFileName.endsWith(".ppt") ||
            sFileName.endsWith(".odf") || sFileName.endsWith(".odg") || sFileName.endsWith(".zip") || sFileName.endsWith(".arj") ||
            sFileName.endsWith(".rar") || sFileName.endsWith(".avi") || sFileName.endsWith(".mpg") || sFileName.endsWith(".mpeg") ||
            sFileName.endsWith(".wmv") || sFileName.endsWith(".docx") || sFileName.endsWith(".xlsx"))
          oAttachments.add(aFiles[f]);         
      } // next
    } // fi
        
    if (null==sHtmlFile && null==sPlainFile) {
      throw new FileNotFoundException("Could not find any valid file for e-mail body at directory "+target_dir);
    } else {

      // **************************************************
      // Si es HTML, en el cuerpo reemplazar los caracteres 
      // no-ASCII por entidades  HTML para evitar problemas
      // con la codificacion
    
      ThisEvent.log("Formatting message body");
    
      String sHtmlText = null, sPlainText = null, sEncoding = "UTF-8";
      CharacterSetDetector oCDet = new CharacterSetDetector();
    
      if (null!=sPlainFile) {
        sEncoding = oCDet.detect(target_dir+sPlainFile, sEncoding);
        sPlainText = oFs.readfilestr(target_dir+sPlainFile, sEncoding);
      }
    
      if (null!=sHtmlFile) {
        sEncoding = oCDet.detect(target_dir+sHtmlFile, sEncoding);
        sHtmlText = oFs.readfilestr(target_dir+sHtmlFile, sEncoding);
        int iBodyStart = Gadgets.indexOfIgnoreCase(sHtmlText,"<body>", 0);
        int iCloseTag = -1;
        if (iBodyStart>=0) {
          iCloseTag = iBodyStart+5;
        } else {
          iBodyStart = Gadgets.indexOfIgnoreCase(sHtmlText,"<body ", 0);
          iCloseTag = sHtmlText.indexOf('>', iBodyStart+6);
        }
    
        if (iBodyStart>=0) {
          int iBodyEnd = Gadgets.indexOfIgnoreCase(sHtmlText,"</body>", iCloseTag+1);
          if (iBodyEnd!=-1) {
            sHtmlText = sHtmlText.substring(0, iBodyStart+6) + Gadgets.XHTMLEncode(sHtmlText.substring(iCloseTag+1, iBodyEnd)) + sHtmlText.substring(iBodyEnd);
          } // fi
        } // fi
      } // fi
    
      ThisEvent.log("Getting sender account");
    
      // El e-mail se enviara desde el servidor SMTP
      // especificado en esta cuenta de la tabla k_user_mail

      MailAccount oMacc = MailAccount.forUser(JDBCConnection, GU_USER);
    
      ThisEvent.log("Got sender");
    
      JDBCConnection.setAutoCommit (false);
    
      if (oMacc==null) {
	  	  throw new SQLException("Could not find any valid file for e-mail body at directory "+target_dir);
	    } else {
    
        Properties oProps = oMacc.getProperties();
        oProps.put("webbeacon", bo_webbeacon);
        oProps.put("clickthrough", bo_clickthrough);
        oProps.put("webserver", oDbb.getProperty("webserver"));		    	
    
        if (id_command.equals("MAIL")) {
    
          ThisEvent.log("Starting job creation");
    
          PageSetDB oPgDb = new PageSetDB();
    
          if (oPgDb.load(JDBCConnection, new Object[]{gu_pageset})) {
        
          	ThisEvent.log("Link images to server");
	  			  oProps.put("attachimages", "0");
	  			  oPgDb.addRecipients(aRecipients);
	  		    final String sGuJob = Gadgets.generateUUID();
	  		    
	  		    // *******************************************************************
	  		    // Esta es la funcion que finalmente crea el lote para enviar el email
	  		    
	  		    /*
	  		    SendMail.send(oMacc, oProps, target_dir, sHtmlText, sPlainText, sEncoding, null,
	  		                  oPgDb.getStringNull(DB.tx_subject,"")+(new Date().toString()), oPgDb.getString(DB.tx_email_from),
	  		                  oPgDb.getStringNull(DB.nm_from, oPgDb.getString(DB.tx_email_from)),
	  		                  oPgDb.getStringNull(DB.tx_email_reply, oPgDb.getString(DB.tx_email_from)),
	  		                  oPgDb.getRecipients(), "to", sGuJob, 
	  		                  oDbb.getProfileName(), oPgDb.getString(DB.nm_pageset)+" ("+(new Date().toString())+")",
	  		                  false, oDbb);
	  			  */

	  			  // for (int m=0; m<aMsgs.size(); m++) {
	  			  //   ThisEvent.log("SendMail "+aMsgs.get(m));
	  			  // }
	  			  
	  			  DBCommand.executeUpdate(JDBCConnection, "UPDATE "+DB.k_jobs+" SET "+DB.gu_job_group+"='"+gu_pageset+"' WHERE "+DB.gu_job+"='"+sGuJob+"'");
          	ThisEvent.log("Job successfully created");
    
          } else {
            throw new SQLException("Could not find PageSet "+gu_pageset);
	  	    } // fi
        }        
      } // fi (MailAccount.exists)
      
      JDBCConnection.commit();
    } // fi (sHtmlFile && sPlainFile)
  } // fi (found recipients)

} catch (Exception e) {  
  if (JDBCConnection!=null) { if (!JDBCConnection.isClosed()) { JDBCConnection.rollback(); } }
  returnValue = new Integer(-1);
	ErrorCode = new Integer(-1);
	ErrorMessage = e.getMessage();
  ThisEvent.log(ErrorMessage);
}

oDbb.close();

ThisEvent.log("End script sendmailtest "+(new Date().toString()));
