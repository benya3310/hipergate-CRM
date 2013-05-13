
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.InternetAddress;

import com.sun.mail.smtp.SMTPMessage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;

import com.knowgate.dfs.FileSystem;
import com.knowgate.misc.Gadgets;
import com.knowgate.debug.DebugFile;

Integer returnValue = new Integer(0);
Integer ErrorCode = new Integer(0);
String  ErrorMessage = "";

final boolean bTrace = DebugFile.trace;

if (bTrace) DebugFile.writeln("Begin beforesmtpmime script for job "+Parameters.get("tl_job"));

SMTPMessage oSentMessage = (SMTPMessage) Parameters.get("bin_smtpmessage");

// *****************************************
// *** Inicio hojas Excel personalizadas ***

if (Parameters.get("tl_job").startsWith("facturasombrafse")) {

  String FILE_NAME = "NotaInformativaEOI-"+Parameters.get("pg_atom")+".xls";
  
  MimeMultipart oParts = (MimeMultipart) oSentMessage.getContent();
  HSSFRow  oXlsRow;
  HSSFCell oXlsCel;
  for (int p=0; p<oParts.getCount(); p++) {
    String sFileName = oParts.getBodyPart(p).getFileName();
    if (null!=sFileName) {
      if (sFileName.equals("NotaInformativaEOI.xls")) {
        HSSFWorkbook oXlsWrk = new HSSFWorkbook(oParts.getBodyPart(p).getInputStream());
        HSSFSheet oXlsSht = oXlsWrk.getSheetAt(0);
        InternetAddress[] aTo = oSentMessage.getRecipients(Message.RecipientType.TO);
        Connection oConn = DriverManager.getConnection(EnvironmentProperties.getProperty("dburl"),EnvironmentProperties.getProperty("dbuser"),EnvironmentProperties.getProperty("dbpassword"));
        PreparedStatement oStmt = oConn.prepareStatement("SELECT nm_alumno,nm_curso,nu_importe,nm_socio FROM k_factura_sombra WHERE tx_email=?");
        oStmt.setString(1,aTo[0].getAddress());
        ResultSet oRSet = oStmt.executeQuery();
        if (oRSet.next()) {
          oXlsRow = oXlsSht.getRow(9);
          oXlsCel = oXlsRow.getCell(5);
          oXlsCel.setCellValue(oRSet.getString(1));
          oXlsRow = oXlsSht.getRow(12);
          oXlsCel = oXlsRow.getCell(2);
          oXlsCel.setCellValue(oRSet.getString(2));
          oXlsCel = oXlsRow.getCell(6);
          oXlsCel.setCellValue(oRSet.getInt(3));
          oXlsRow = oXlsSht.getRow(22);
          oXlsCel = oXlsRow.getCell(6);
          oXlsCel.setCellValue(oRSet.getInt(3));
          oXlsRow = oXlsSht.getRow(26);
          oXlsCel = oXlsRow.getCell(1);
          oXlsCel.setCellValue(oRSet.getString(4));
          File oFout = new File("/tmp/"+FILE_NAME);
          if (oFout.exists()) oFout.delete();
          FileOutputStream oSout = new FileOutputStream(oFout);
          oXlsWrk.write(oSout);
          oSout.close();
          oParts.getBodyPart(p).attachFile("/tmp/"+FILE_NAME);
  
  				ErrorMessage = "";
  				ErrorCode = new Integer(0);
  				ReturnValue = new Integer(0);
  
        } else {
  				ErrorMessage = "No record found for e-mail "+aTo[0].getAddress()+" at table k_factura_sombra";
  				ErrorCode = new Integer(100);
  				ReturnValue = new Integer(100);      
        }
        oRSet.close();
        oStmt.close();
        oConn.close();
      }
    }
  }
}

// *** Fin hojas Excel personalizadas ***
// **************************************


// **************************************************
// *** Inicio encuesta alumnos link personalizado ***

if (Parameters.get("tl_job").startsWith("encuestaalumnos")) {

  InternetAddress[] aTo = oSentMessage.getRecipients(Message.RecipientType.TO);
  
  if (bTrace) DebugFile.writeln("Processing email "+aTo[0].getAddress());

  String sText = (String) oSentMessage.getContent();
  
  Connection oConn = DriverManager.getConnection(EnvironmentProperties.getProperty("dburl"),EnvironmentProperties.getProperty("dbuser"),EnvironmentProperties.getProperty("dbpassword"));
  PreparedStatement oStmt = oConn.prepareStatement("SELECT tx_salutation,tx_name,tx_info FROM k_x_list_members WHERE gu_list IN ('ac1263a412c9853a407100002f41e1ad','ac1263a412c562fbfb9100010f3509aa','ac1263a412cc1007367100038f1bb36d','ac1263a412cc0f8a17c10001ddcd0b7d') AND tx_email=?");
  oStmt.setString(1,aTo[0].getAddress());
  ResultSet oRSet = oStmt.executeQuery();
  if (oRSet.next()) {
    if (bTrace) DebugFile.writeln("found replacement for "+aTo[0].getAddress());
    // sText = Gadgets.replace(sText, "Estimado", "Estimad"+oRSet.getString(1));
    sText = Gadgets.replace(sText, "Amigo", oRSet.getString(2));
    sText = Gadgets.replace(sText, "LINKENCUESTA", oRSet.getString(3));
    oSentMessage.setText(sText);
  }
  oRSet.close();
  oStmt.close();
  oConn.close();
}

// *** Fin encuesta alumnos link personalizado ***
// ***********************************************

// *****************************************************
// *** Inicio encuesta profesores link personalizado ***

if (Parameters.get("tl_job").startsWith("encuestaprofesores")) {

  InternetAddress[] aTo = oSentMessage.getRecipients(Message.RecipientType.TO);
  
  if (bTrace) DebugFile.writeln("Processing email "+aTo[0].getAddress());

  String sText = (String) oSentMessage.getContent();
  
  Connection oConn = DriverManager.getConnection(EnvironmentProperties.getProperty("dburl"),EnvironmentProperties.getProperty("dbuser"),EnvironmentProperties.getProperty("dbpassword"));
  PreparedStatement oStmt = oConn.prepareStatement("SELECT tx_info FROM k_x_list_members WHERE gu_list IN ('ac1263a412c562fbfb9100010f3509aa','ac1263a412c7f41397c10000aeaf490e','ac1263a412c97d963df100feee5e4220','ac1263a412cb10444a110014fcecea46') AND tx_email=?");
  oStmt.setString(1,aTo[0].getAddress());
  ResultSet oRSet = oStmt.executeQuery();
  if (oRSet.next()) {
    if (bTrace) DebugFile.writeln("found replacement for "+aTo[0].getAddress());
    sText = Gadgets.replace(sText, "LINKENCUESTA", oRSet.getString(1));
    oSentMessage.setText(sText);
  }
  oRSet.close();
  oStmt.close();
  oConn.close();
}

// *** Fin encuesta profesores link personalizado ***
// **************************************************

// **************************************************************
// *** Inicio tutorias crecimiento Redepyme PDF personalizado ***

if (Parameters.get("tl_job").startsWith("plancrecimiento")) {
  String sGuActivity = null;

  if (Parameters.get("tl_job").startsWith("plancrecimientocontinuan1"))
    sGuActivity = "ac1263a413567824e231003b9862ad4c";
  else if (Parameters.get("tl_job").startsWith("plancrecimientocontinuan2"))
    sGuActivity = "ac1263a413581cc992c1000dfac5b9ee";
  else if (Parameters.get("tl_job").startsWith("plancrecimientodesestimados"))
    sGuActivity = "ac1263a41357cbf00a6100025e2ffb3c";
  else if (Parameters.get("tl_job").startsWith("plancrecimientodatospublico"))
    sGuActivity = "ac1263a41357cb96ef8100012b1c2167";
    
  String BASE_PATH = "/opt/data/storage/domains/2052/workareas/ac1263a41235762fe5b1000c49e09610/apps/Marketing/"+sGuActivity+"/";
  String PDF_NAME = Parameters.get("tx_email")+".pdf";

  MimeMultipart oParts = (MimeMultipart) oSentMessage.getContent();
  for (int p=0; p<oParts.getCount(); p++) {
    if (null!=oParts.getBodyPart(p).getFileName()) {
      oParts.getBodyPart(p).attachFile(BASE_PATH+PDF_NAME);
      oParts.getBodyPart(p).setFileName("InformeTutoriaInicial"+Parameters.get("pg_atom")+".pdf");
    } // fi
  } // next
} // fi

// *** Fin tutorias crecimiento Redepyme PDF personalizado ***
// ***********************************************************

if (bTrace) DebugFile.writeln("End beforesmtpmime script");
