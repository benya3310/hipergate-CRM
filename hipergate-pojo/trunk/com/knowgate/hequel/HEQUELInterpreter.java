package com.knowgate.hequel;

import org.w3c.tidy.*;
import org.htmlparser.beans.StringBean;
import org.json.*;


import com.knowgate.debug.*;
import com.knowgate.jdc.*;
import com.knowgate.dataobjs.*;
import com.knowgate.dataxslt.*;
import com.knowgate.acl.*;
import com.knowgate.bing.Item;
import com.knowgate.bing.Search;

import com.knowgate.crm.*;
import com.knowgate.dfs.*;
import com.knowgate.ldap.*;
import com.knowgate.misc.*;
import com.knowgate.forums.*;
import com.knowgate.hipergate.*;
import com.knowgate.hipergate.datamodel.ModelManager;
import com.knowgate.hipergate.datamodel.ImportExport;
import com.knowgate.hipergate.datamodel.UNLoCode;
import com.knowgate.scheduler.*;
import com.knowgate.scheduler.events.ExecuteBeanShell;
import com.knowgate.cache.DistributedCachePeer;
import com.knowgate.math.Money;
import com.knowgate.lucene.*;
import com.knowgate.math.CurrencyCode;
import com.knowgate.hipergate.translator.Translate;
import com.knowgate.sms.SMSPushRealidadFutura;
import com.knowgate.storage.*;

import com.knowgate.sms.*;

import com.knowgate.ole.*;

import com.knowgate.hipermail.*;

import java.io.*;

import java.net.URL;
import java.rmi.RemoteException;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import java.sql.*;

import org.apache.oro.text.regex.*;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import java.security.Security;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.MessageDigest;
import com.knowgate.syndication.crawler.SearchRunner;

@SuppressWarnings("unused")
public class HEQUELInterpreter  {
  public HEQUELInterpreter() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  // ----------------------------------------------------------

  public static void main(String[] argv) throws Exception {

	 Manager oStr = new Manager();
	 
	 SearchRunner oRun = new SearchRunner("eoi", oStr.getProperties());
	 DataSource oDts = oStr.getDataSource();	  
     oRun.run(oDts);
     oStr.free(oDts);
	 oDts=null;
	 // RecordSet oRst = oStr.fetch("k_syndentries", "tx_sought", "eoi");
	 System.out.println("Done!");
	 
	  /*
	  Search s = new Search();
	  Item[] a = s.query("sergio montoro ten", "linkedin.com");
	  if (a==null) {
		  System.out.println("No results found");
	  } else {
		  for (int i=0; i<a.length; i++) {
			  System.out.println(a[i].title+" "+a[i].pubdate+"\n"+a[i].url+"\n"+a[i].abstrct+"\n\n");
		  }
	  }
    */

   /*
   FileSystem f = new FileSystem();
   String s = f.readfilestr("https://graph.facebook.com/sergiomt?fields=friends&access_token=10150089313085434|2.fyEYPoDS0A4ZqzNpMfFHfA__.3600.1288472400-837649508|Ubg3Uywn5Ca4dnd55pfCD8Yd17M&expires_in=4317","ASCII");
   JSONObject o = new JSONObject(s);
   JSONObject r = (JSONObject) o.get("friends");
   JSONArray a = r.getJSONArray("data");
   System.out.println(a);
   */
		
    /*
	FileSystem oFs = new FileSystem();
	Html2Pdf oH2P = new Html2Pdf();
	oFs.writefilebin("C:\\_Octubre\\Mensual_(2010-10-01_11.34.53)\\body.pdf",
	oH2P.transformHTML(oFs.readfilestr("C:\\_Octubre\\Mensual_(2010-10-01_11.34.53)\\body.htm", "ISO8859_1")).toByteArray());
    */
    
    /*
	final String sCDesc = "gu_contact VARCHAR, id_contact_ref VARCHAR, dt_created DATE yyyy-MM-DD, situacion NULL, tx_name VARCHAR, tx_surname VARCHAR, id_gender CHAR, pograma NULL, id_course NULL, ix_address INTEGER, tp_street VARCHAR, nm_street VARCHAR, zipcode VARCHAR, mn_city VARCHAR, nm_state VARCHAR, id_country VARCHAR, nm_country VARCHAR, work_phone VARCHAR, mov_phone VARCHAR, fax_phone VARCHAR, tx_email VARCHAR, "
	+ "tp_degree NULL, nm_degree NULL, id_institution NULL, nm_centro NULL, dt_degree_start NULL, dt_degree_end NULL, bo_english NULL, lv_english NULL, bo_french NULL, lv_french NULL, "
	+ "nm_birth_place NULL, dt_birth DATE yyyy-MM-DD, id_nationality VARCHAR, tp_passport VARCHAR, sn_passport VARCHAR, "
	+ "lv_experience NULL, nm_legal VARCHAR, de_title VARCHAR, ny_age SMALLINT, nm_legal_prev NULL, de_title_prev NULL, nu_year_prev1 NULL, nu_year_prev2 NULL, "
	+ "tp_contact_media NULL, dt_sent NULL, dt_promised NULL, dt_delivered NULL, beca NULL, im_beca NULL, tx_comments VARCHAR";

    ImportExport oImp = new ImportExport();
    int nErrors = oImp.perform("APPENDUPDATE CONTACTS CONNECT root TO \"jdbc:mysql://127.0.0.1/hipergate55\" IDENTIFIED BY manager WORKAREA test_default UNRECOVERABLE INPUTFILE \"C:\\\\WINDOWS\\\\Temp\\\\Admisiones.txt\" BADFILE \"C:\\\\WINDOWS\\\\Temp\\\\Bad.txt\" DISCARDFILE \"C:\\\\WINDOWS\\\\Temp\\\\Discard.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \"|\" ALLCAPS ("+sCDesc+")");
	*/
    
  //Tidy oTdy = new Tidy();
  //oTdy.parseDOM();

  
  //DBBind oDb = new DBBind();
  
  /*		
  com.knowgate.yahoo.Boss b = new com.knowgate.yahoo.Boss();
  com.knowgate.yahoo.YSearchResponse y = b.search("CCPHrtTV34Fxw3S4MtMcQ82YGu5H2f_VtcDINUxuGgKeByKmf2lAHbFiIbj4Dg--",
  							   "Sergio Montoro Ten","facebook.com");

  System.out.println(y.count());
  System.out.println(y.results(0).title);
  System.out.println(y.results(0).abstrct);
  System.out.println(y.results(0).url);
  
  Translate oTr = new Translate();
  HashMap oTags = oTr.extractTagsFromFile("C:\\ARCHIV~1\\Tomcat\\webapps\\hipergate", "", "login.html");
			
  oTr.writeTagsToDatabase(oTags, "com.knowgate.http.HttpDataObjsServlet",
  						  "http://www.hipergate.org/servlet/HttpDataObjsServlet",
                          "7f000001f864e38597100007cc08fd28", "",
                          "", "login.html", "work");
  */
	//ShoppingBasket oBsk = new ShoppingBasket();
	//oBsk.parse("<ShoppingBasket><Address><gu>123456</gu><ix>1</ix></Address></ShoppingBasket>");
	
	//if (oBsk!=null) return;
	 	
	//NewsMessageIndexer.rebuild(oDbb.getProperties(),"k_newsmsgs");
	//NewsMessageRecord[] aRecs = NewsMessageSearcher.search(oDbb.getProperty("luceneindex"),"3e571a161170d6d6f50100003a257265",
	//				      								   "PERIQUITO~00001",null,"posteo",null,null,null,50,null);

	//String w = aRecs[0].getWorkArea();
	//String c = aRecs[0].getNewsGroupName();
	//String t = aRecs[0].getTitle();

	//SPDBF oDbf = new SPDBF (oDbb, "jstels.jdbc.dbf.DBFDriver");
	//oDbf.connect("jdbc:jstels:dbf:C:\\GrupoSP\\FAE08R01\\Dbf01","supervisor","esplenio");
	//System.out.println(oDbf.getNextCustomerId());
	//oDbf.close();
	
	//ImportExport  oImpExp = new ImportExport();
    //oImpExp.perform("APPEND FELLOWS CONNECT knowgate TO \"jdbc:mysql://127.0.0.1/hipergate4\" IDENTIFIED BY knowgate WORKAREA test_default INSERTLOOKUPS INPUTFILE \"C:\\\\Fellows.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \"|\" BADFILE \"C:\\\\Fellows_bad.txt\" DISCARDFILE \"C:\\\\Fellows_discard.txt\" (nm_acl_group VARCHAR, tx_nickname VARCHAR,tx_pwd VARCHAR,tp_account VARCHAR,tx_main_email VARCHAR,nm_user VARCHAR,tx_surname1 VARCHAR, tx_surname2 VARCHAR, nm_company VARCHAR, tx_dept VARCHAR, de_title VARCHAR)");
				
    // com.knowgate.hipergate.translator.Translate.main(new String[]{"translate","C:\\translate.cnf"});

    //Class mssql = Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	// Class postgre = Class.forName("org.postgresql.Driver");
	//Class oracle = Class.forName("oracle.jdbc.driver.OracleDriver");
	//Class db2 = Class.forName("com.ibm.db2.jcc.DB2Driver");
	//Class mysql = Class.forName("com.mysql.jdbc.Driver");
    
    //com.knowgate.ldap.LDAPNovell.main(new String[]{"C:\\WINNT\\hipergate.cnf", "load", "all"});

    //final String sDataPath = "C:\\WorkingFolder\\kawa\\src\\com\\knowgate\\hipergate\\datamodel\\data\\";

    ModelManager oMan = new ModelManager();
	
	//oMan.main(new String[]{"C:\\Windows\\hipergate.cnf","execute","C:\\k_lu_cities_es.sql"});
	
    //ImportExport oImp = new ImportExport();

    try {
      //oImp.perform("APPENDUPDATE CONTACTS CONNECT sysadm TO \"jdbc:mysql://friki.kg.int/hgmysql1d\" IDENTIFIED BY LXCuU9qH7d97dcxf WORKAREA test_default INPUTFILE \"T:\\\\knowgate\\\\cargaprueba.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \"|\" BADFILE \"C:\\\\Temp\\\\Contacts_bad.txt\" DISCARDFILE \"C:\\\\Temp\\\\Contacts_discard.txt\" (nm_legal VARCHAR, id_company_ref VARCHAR, tx_name VARCHAR, tx_surname VARCHAR, sn_passport VARCHAR, nm_street VARCHAR, nu_street VARCHAR, zipcode VARCHAR, mn_city VARCHAR, work_phone VARCHAR)");

      //oImp.perform("APPENDUPDATE USERS CONNECT sysadm TO \"jdbc:mysql://friki.kg.int/hgmysql1d\" IDENTIFIED BY LXCuU9qH7d97dcxf WORKAREA test_default INPUTFILE \"T:\\\\usuarios.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \";\" BADFILE \"C:\\\\Users_bad.txt\" DISCARDFILE \"C:\\\\Users_discard.txt\" (id_domain INTEGER, nm_acl_group VARCHAR, tx_pwd VARCHAR, tx_nickname VARCHAR, ignore NULL, tx_main_email VARCHAR, nm_user VARCHAR, tx_surname1 VARCHAR, de_title VARCHAR)");

      //oImp.perform("APPENDUPDATE CONTACTS CONNECT knowgate TO \"jdbc:postgresql://192.168.1.10:10801/hgoltp8t\" IDENTIFIED BY knowgate WORKAREA test_default INPUTFILE \"C:\\\\Temp\\\\Contacts.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \"|\" BADFILE \"C:\\\\Temp\\\\Contacts_bad.txt\" DISCARDFILE \"C:\\\\Temp\\\\Contacts_discard.txt\" (nm_legal VARCHAR, id_company_ref VARCHAR, tx_name VARCHAR, tx_surname VARCHAR, sn_passport VARCHAR, nm_street VARCHAR, nu_street VARCHAR, zipcode VARCHAR, mn_city VARCHAR, work_phone VARCHAR)");

      //oImp.perform("APPENDUPDATE PRODUCTS CONNECT knowgate TO \"jdbc:postgresql://192.168.1.10:5432/hgoltp2d\" IDENTIFIED BY knowgate WORKAREA test_default CATEGORY AMAZON~00001 INPUTFILE \"C:\\\\Temp\\\\Products.txt\" CHARSET ISO8859_1 ROWDELIM CRLF COLDELIM \"|\" BADFILE \"C:\\\\Temp\\\\Contacts_bad.txt\" DISCARDFILE \"C:\\\\Temp\\\\Contacts_discard.txt\" (nm_product VARCHAR,id_ref VARCHAR,de_product VARCHAR,id_fare VARCHAR,pr_list DECIMAL,pr_sale DECIMAL,id_currency VARCHAR,pct_tax_rate FLOAT,is_tax_included SMALLINT,dt_acknowledge DATE DD/MM/yyyy,id_cont_type INTEGER,id_prod_type VARCHAR,author VARCHAR,days_to_deliver SMALLINT,isbn VARCHAR,pages INTEGER,url_addr VARCHAR)");

      // oMan.connect("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/hipergate55", "", "root", "manager");


	  // oMan.activateLog(false);
	  
      //oMan.connect("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://db.kg.int:50000/hg_db2", "", "db2inst1", "123456");
      //oMan.connect("com.microsoft.jdbc.sqlserver.SQLServerDriver", "jdbc:microsoft:sqlserver://192.168.1.24:1433;DatabaseName=hipergate21", "dbo", "sa", "kg");

      // oMan.connect("org.postgresql.Driver", "jdbc:postgresql://127.0.0.1:5432/postgres", "", "postgres", "postgres");

      //oMan.connect("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@127.0.0.1:1521:XE", "HIPERGATE", "HIPERGATE", "HIPERGATE");

     //oMan.dropAll();
     // oMan.clear();

     //String[] args = { "C:\\WINNT\\hipergate.cnf",  "script", "k_companies", "C:\\TEMP\\k_comapnies.sql" };
     //String[] args = { "C:\\WINNT\\hipergate.cnf",  "create", "database", "verbose" };
     //String[] args = { "C:\\WINNT\\hipergate.cnf",  "upgrade", "200", "210" };
     //ModelManager.main(args);

     // oMan.createDefaultDatabase();

     /*
     LDAPNovell oLDAP = new LDAPNovell();
     oLDAP.connect("ldap://fobos:389/dc=hipergate,dc=org");
     oLDAP.bind("cn=Manager,dc=hipergate,dc=org","manager");
     oLDAP.deleteWorkArea("KG", "ldap");
     oLDAP.loadWorkArea(oMan.getConnection(), "KG", "ldap");
     */

     //oMan.disconnect();

     //oLDAP.disconnect();
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }
    finally {
      File oLog = new File ("C:\\TEMP\\ModelManager.txt");
      oLog.delete();

      FileWriter oLogWrt = new FileWriter(oLog, true);

      oLogWrt.write(oMan.report());

      oLogWrt.close();

      if (null!=oMan) oMan.disconnect();
    }
  }

  private void jbInit() throws Exception {
  }
}
