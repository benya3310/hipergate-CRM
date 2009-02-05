/*
  Copyright (C) 2003-2008  Know Gate S.L. All rights reserved.
                           C/O�a, 107 1�2 28050 Madrid (Spain)

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
package com.knowgate.forums;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import com.knowgate.debug.DebugFile;
import com.knowgate.misc.Gadgets;
import com.knowgate.jdc.JDCConnection;
import com.knowgate.dataobjs.DB;
import com.knowgate.dataobjs.DBBind;
import com.knowgate.dataobjs.DBSubset;

import org.apache.lucene.queryParser.ParseException;

import com.knowgate.lucene.NewsMessageRecord;
import com.knowgate.lucene.NewsMessageSearcher;

/**
 * <p>Forums Model Class</p>
 * @author Sergio Montoro Ten
 * @version 4.0
 */

public class Forums {
  public Forums() {
  }

  /**
   * Not implemented
   */
  public static String RSSListNewsGroups(JDCConnection oConn, int iDomainId, String sWorkAreaId)
    throws SQLException {

    if (DebugFile.trace) {
      DebugFile.writeln("Begin Forums.RSSListNewsGroups(" + String.valueOf(iDomainId) + "," + sWorkAreaId + ")");
      DebugFile.incIdent();
    }

    String sWhere = "g." + DB.id_domain + "=" + String.valueOf(iDomainId);

    if (null!=sWorkAreaId)
      sWhere += " AND g." + DB.gu_workarea + "=" + String.valueOf(sWorkAreaId);

    DBSubset oNewsGrps = new DBSubset(DB.k_newsgroups + " g," + DB.k_categories + " c",
                                      "g." + DB.gu_newsgrp + ",g." + DB.id_domain +
                                      ",g." + DB.gu_workarea + ",g." + DB.dt_created +
                                      ",g." + DB.bo_binaries + ",g." + DB.dt_expire +
                                      ",g." + DB.de_newsgrp + ",c." + DB.nm_category +
                                      ",c." + DB.bo_active + ",c." + DB.dt_modified +
                                      ",c." + DB.nm_icon + ",c." + DB.nm_icon2, sWhere, 10);

    final int iNewsGrps = oNewsGrps.load(oConn);

    StringBuffer oStrBuff = new StringBuffer();

    oStrBuff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

    oStrBuff.append("<!DOCTYPE rdf:RDF [\n");
    oStrBuff.append("<!ENTITY % HTMLsymbol PUBLIC \"-//W3C//ENTITIES Symbols for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent\"> %HTMLsymbol;\n");
    oStrBuff.append("<!ENTITY % HTMLspecial PUBLIC \"-//W3C//ENTITIES Specials for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent\"> %HTMLspecial;\n");
    oStrBuff.append("<!ENTITY % HTMLlatin1 PUBLIC \"-//W3C//ENTITIES Latin 1 for XHTML//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent\"> %HTMLlatin1;\n");
    oStrBuff.append("]>\n");

    oStrBuff.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns=\"http://purl.org/rss/1.0/\">\n");
    oStrBuff.append("  <channel rdf:about=\"http://www.hipergate.org/newsgroups\">\n");
    oStrBuff.append("    <title>NewsGroups List</title>\n");
    oStrBuff.append("  </channel>\n");
    oStrBuff.append("</rdf:RDF>");

    for (int n=0; n<iNewsGrps; n++) {

    } // next (n)

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End Forums.RSSListNewsGroups()");
    }

    return oStrBuff.toString();
  } // RSSListNewsGroups

  // --------------------------------------------------------------------------

  public static String XMLListNewsGroups(JDCConnection oConn, int iDomainId, String sWorkAreaId, Boolean bActiveOnly, String sOrderBy)
    throws SQLException {
    DBSubset oGroups = getNewsGroupsList(oConn, iDomainId, sWorkAreaId, bActiveOnly, sOrderBy);
    int nGroups = oGroups.getRowCount();

    if (DebugFile.trace) {
      DebugFile.writeln("Begin Forums.XMLListNewsGroups([JDCConnection]," + String.valueOf(iDomainId) + "," + sWorkAreaId + "," + sOrderBy + ")");
      DebugFile.incIdent();
    }

    StringBuffer oStrBuff = new StringBuffer(4000);
    oStrBuff.append("<NewsGroups>\n");
	if (0!=nGroups) {
	  SimpleDateFormat oXMLDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	  PreparedStatement oStmt = oConn.prepareStatement("SELECT "+DB.id_language+","+DB.tr_category+","+DB.url_category+" FROM "+DB.k_cat_labels+" WHERE "+DB.gu_category + "=?",
	  												   ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	  for (int g=0; g<nGroups; g++) {
        oStrBuff.append("<NewsGroup>\n");
		oStrBuff.append("  <gu_newsgrp>"+oGroups.getString(0,g)+"</gu_newsgrp>\n");
		oStrBuff.append("  <dt_created>"+oGroups.getDateFormated(1,g,oXMLDate)+"</dt_created>\n");
		oStrBuff.append("  <dt_last_update>"+oGroups.getDateFormated(2,g,oXMLDate)+"</dt_last_update>\n");
		oStrBuff.append("  <dt_expire>"+oGroups.getDateFormated(3,g,oXMLDate)+"</dt_expire>\n");
		oStrBuff.append("  <bo_binaries>"+String.valueOf(oGroups.getShort(4,g))+"</bo_binaries>\n");
		oStrBuff.append("  <de_newsgrp>"+oGroups.getStringNull(5,g,"")+"</de_newsgrp>\n");
		oStrBuff.append("  <gu_owner>"+oGroups.getString(6,g)+"</gu_owner>\n");
		oStrBuff.append("  <nm_category>"+oGroups.getString(7,g)+"</nm_category>\n");
		oStrBuff.append("  <bo_active>"+String.valueOf(oGroups.getShort(8,g))+"</bo_active>\n");
		oStrBuff.append("  <nm_icon>"+oGroups.getStringNull(9,g,"")+"</nm_icon>\n");
		oStrBuff.append("  <nm_icon2>"+oGroups.getStringNull(10,g,"")+"</nm_icon2>\n");
		if (oGroups.isNull(11,g))
		  oStrBuff.append("  <id_doc_status/>\n");
		else
		  oStrBuff.append("  <id_doc_status>"+String.valueOf(oGroups.getShort(11,g))+"</id_doc_status>\n");
		if (oGroups.isNull(12,g))
		  oStrBuff.append("  <len_size/>\n");
		else
		  oStrBuff.append("  <len_size>"+String.valueOf(oGroups.getInt(12,g))+"</len_size>\n");
	    oStrBuff.append("  <labels>\n");
	    oStmt.setString(1, oGroups.getString(0,g));
	    ResultSet oRSet = oStmt.executeQuery();
	    while (oRSet.next()) {
	      oStrBuff.append("    <label id_language=\"");
	      oStrBuff.append(oRSet.getString(1));
	      oStrBuff.append("\"><![CDATA[");
	      oStrBuff.append(oRSet.getString(2));
	      oStrBuff.append("]]></label>\n");
	    } // wend
	    oRSet.close();
	    oStrBuff.append("  </labels>\n");
        oStrBuff.append("</NewsGroup>\n");
	  } // next(g)
	  oStmt.close();
	} // fi (nGroups)
    oStrBuff.append("</NewsGroups>");

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End Forums.XMLListNewsGroups()");
    }

	return oStrBuff.toString();
  } // XMLListNewsGroups

  // --------------------------------------------------------------------------

  public static String XMLListTopLevelMessages(JDCConnection oConn, int nMaxMsgs,
  											   int iDomainId, String sWorkAreaId,
  											   Boolean bActiveOnly, String sOrderBy)
    throws SQLException,IllegalArgumentException {
	DBSubset oDbss = getTopLevelMessages(oConn, nMaxMsgs,sWorkAreaId, bActiveOnly, sOrderBy);
    return "<NewsMessages count=\""+String.valueOf(oDbss.getRowCount())+"\">\n"+oDbss.toXML("","NewsMessage","dd/MM/yyyy hh:mm", null)+"</NewsMessages>";
  } // XMLListTopLevelMessages

  // --------------------------------------------------------------------------

  public static String XMLListMessagesForThread(JDCConnection oConn, String sGuThread)
    throws SQLException,IllegalArgumentException {
    DBSubset oDbss = getMessagesForThread(oConn, sGuThread);
    
	return "<NewsMessages count=\""+String.valueOf(oDbss.getRowCount())+"\">\n"+oDbss.toXML("","NewsMessage","dd/MM/yyyy hh:mm", null)+"</NewsMessages>\n";
  } // XMLListMessagesForThread

  // --------------------------------------------------------------------------

  public static String XMLListMessagesForGroup(JDCConnection oConn, 
  											   int nMaxMsgs, int nOffset,
  											   String sGroupId, String sOrderBy)
    throws SQLException,IllegalArgumentException {
    DBSubset oDbss = getMessagesForGroup(oConn, sGroupId, nMaxMsgs, nOffset, sOrderBy);
    
	return "<NewsMessages offset=\""+String.valueOf(nOffset)+"\" eof=\""+String.valueOf(oDbss.eof())+"\" count=\""+String.valueOf(oDbss.getRowCount())+"\">\n"+oDbss.toXML("","NewsMessage","dd/MM/yyyy hh:mm", null)+"</NewsMessages>\n";
  } // XMLListMessagesForGroup

  // --------------------------------------------------------------------------

  public static String XMLListTopLevelMessagesForGroup(JDCConnection oConn, 
  											   Date dtStart, Date dtEnd,
  											   String sGroupId, String sOrderBy)
    throws SQLException,IllegalArgumentException {
    DBSubset oDbss = getTopLevelMessagesForGroup(oConn, sGroupId, dtStart, dtEnd, sOrderBy);
    
	return "<NewsMessages offset=\"0\" eof=\"true\" count=\""+String.valueOf(oDbss.getRowCount())+"\">\n"+oDbss.toXML("","NewsMessage","dd/MM/yyyy hh:mm", null)+"</NewsMessages>\n";
  } // XMLListMessagesForGroup

  // --------------------------------------------------------------------------

  public static String XMLListTopLevelMessagesForGroup(JDCConnection oConn, 
  											           int nMaxMsgs, int nOffset,
  											           String sGroupId, String sOrderBy)
    throws SQLException,IllegalArgumentException {
    DBSubset oDbss = getTopLevelMessagesForGroup(oConn, sGroupId, nMaxMsgs, nOffset, sOrderBy);

	return "<NewsMessages offset=\""+String.valueOf(nOffset)+"\" eof=\""+String.valueOf(oDbss.eof())+"\" count=\""+String.valueOf(oDbss.getRowCount())+"\">\n"+oDbss.toXML("","NewsMessage","dd/MM/yyyy hh:mm", null)+"</NewsMessages>\n";
  } // XMLListTopLevelMessagesForGroup

  // --------------------------------------------------------------------------
  
  public static String XMLListDaysWithPosts(JDCConnection oConn, String sGuNewsGrp, Date dtFrom, Date dtTo)
  	throws SQLException {

	SimpleDateFormat oShortDate = new SimpleDateFormat("yyyy-MM-dd");

    if (DebugFile.trace) {
      DebugFile.writeln("Begin Forums.XMLListDaysWithPosts([JDCConnection], "+oShortDate.format(dtFrom)+","+oShortDate.format(dtTo)+")");
      DebugFile.incIdent();
    }
    
    StringBuffer oBuffer = new StringBuffer();
	ArrayList oDaysList = getDaysWithPosts(oConn, sGuNewsGrp, dtFrom, dtTo);
	int nDays = oDaysList.size();
	
	oBuffer.append("<Days>");
	Date dtCurr = new Date(dtFrom.getTime());
	for (int d=0; d<nDays; d++) {
      oBuffer.append("<Day date=\""+oShortDate.format(dtCurr)+"\">"+String.valueOf(((Boolean) oDaysList.get(d)).booleanValue())+"</Day>");	    
	  dtCurr = new Date(dtCurr.getTime()+86400000l);
	} // next
	oBuffer.append("</Days>");
	
    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End Forums.XMLListDaysWithPosts()");
    }

    return oBuffer.toString();
  } // XMLListDaysWithPosts

  // --------------------------------------------------------------------------

  public static String XMLSearchMessages(JDCConnection oConn,
										 String sLuceneIndexPath,
                                         String sWorkArea, String sGroup,
                                         String sSought, int iLimit)
    throws IOException, ParseException {

    StringBuffer oRetXml = new StringBuffer(8000);
    NewsMessageRecord[] aRecs = NewsMessageSearcher.search (sLuceneIndexPath,
                                            				sWorkArea, sGroup,
                                            				sSought, sSought,
                                            				null, null,
                                            			    sSought, iLimit, null);
    if (null==aRecs)
      oRetXml.append("<NewsMessages count=\"0\" />");
    else {
      int nCount = aRecs.length;
      float fTopScore = 0f;
      for (int r=0; r<nCount; r++) {
        if (aRecs[r].getScore()>fTopScore)
          fTopScore = aRecs[r].getScore();
      } // next      
      oRetXml.append("<NewsMessages count=\""+String.valueOf(nCount)+"\" topscore=\""+String.valueOf(fTopScore)+"\">\n");
      for (int r=0; r<nCount; r++) {
      	oRetXml.append(aRecs[r].toXML());
      	oRetXml.append("\n");
      } // next
      oRetXml.append("</NewsMessages>\n");
    }
	return oRetXml.toString();  
  } // XMLSearchMessages

  // --------------------------------------------------------------------------
  
  /**
   * Get all groups of a WorkArea including those that are not active
   * @param oConn Database Connection 
   * @param sOrderBy Attribute to sort messages. By default it is dt_published which corresponds to publishing date. Can be also nu_votes to sort messages by number of votes or nm_author to sort by author.
   * @param sGuWorkArea WorkArea GUID
   * @throws SQLException
   * @return DBSubset containing the following columns: gu_newsgrp,dt_created,dt_last_update,dt_expire,bo_binaries,de_newsgrp,gu_owner,nm_category,bo_active,nm_icon,nm_icon2,id_doc_status,len_size
   * @since 4.0
   */
  public static DBSubset getNewsGroupsList(JDCConnection oConn,
  										   int iDomainId, String sGuWorkArea, Boolean bActive, String sOrderBy)
    throws SQLException {
    	
	String sActiveOnly = "";
	if (bActive!=null) sActiveOnly = (bActive.booleanValue() ? "c."+DB.bo_active+"<>0 AND " : "");
	if (null==sOrderBy) sOrderBy = DB.nm_category;
	 
	DBSubset  oGroups = new DBSubset (DB.k_newsgroups+" g,"+DB.k_categories+" c",
		                              "g."+DB.gu_newsgrp+",g."+DB.dt_created+",g."+DB.dt_last_update+",g."+DB.dt_expire+",g."+DB.bo_binaries+",g."+DB.de_newsgrp+",c."+DB.gu_owner+",c."+DB.nm_category+",c."+DB.bo_active+",c."+DB.nm_icon+",c."+DB.nm_icon2+",c."+DB.id_doc_status+",c."+DB.len_size,
									  sActiveOnly +
									  "g."+DB.gu_newsgrp+"=c."+DB.gu_category+" AND "+
									  "g."+DB.id_domain+"=? AND g."+DB.gu_workarea+"=? "+
									  "ORDER BY "+sOrderBy, 50);

    oGroups.load(oConn, new Object[]{new Integer(iDomainId), sGuWorkArea});
	return oGroups;
  } // getNewsGroupsList

  // --------------------------------------------------------------------------

  public static DBSubset getMessagesForThread(JDCConnection oConn, String sGuThread)
    throws SQLException {

    DBSubset  oPosts = new DBSubset (DB.k_newsmsgs + " m",
    	      "m." + DB.gu_msg + ",m." + DB.gu_product + ",m." + DB.nm_author + ",m." + DB.tx_subject +
    	      ",m." + DB.dt_published + ",m." + DB.tx_email + ",m." + DB.nu_thread_msgs + ",m." + DB.gu_thread_msg +
    	      ",m." + DB.gu_parent_msg + ",m." + DB.nu_votes + ", 'permalink' AS tx_permalink, m." + DB.tx_msg,
    	      "m." + DB.id_status + "="+String.valueOf(NewsMessage.STATUS_VALIDATED)+" AND "+
    	      "m." + DB.gu_thread_msg + "=? ORDER BY "+DB.dt_published, 100);
    int nPosts = oPosts.load (oConn, new Object[]{sGuThread});
    int iPermalink = oPosts.getColumnPosition("tx_permalink");
    int iSubject = oPosts.getColumnPosition(DB.tx_subject);    
	for (int p=0; p<nPosts; p++) {
	  oPosts.setElementAt(Gadgets.ASCIIEncode(oPosts.getStringNull(iSubject, p, "")).toLowerCase(), iPermalink, p);
	} // next
    return oPosts;
  } // getMessagesForThread

  // --------------------------------------------------------------------------

  public static DBSubset getMessagesForGroup(JDCConnection oConn,
  											 String sGuNewsGroup,
  											 int nMaxMsgs, int nOffset,
  											 String sOrderBy)
  throws SQLException,IllegalArgumentException {
	
	if (nOffset<0) throw new IllegalArgumentException("Forums.getMessagesForGroup() The offset of messages to get must be greater than or equal to zero");
	if (nMaxMsgs<=0) throw new IllegalArgumentException("Forums.getMessagesForGroup() The number of messages to get must be greater than zero");
	if (null==sOrderBy) sOrderBy = DB.dt_published;
    if (sOrderBy.length()==0) sOrderBy = DB.dt_published;

    DBSubset  oPosts = new DBSubset (DB.k_newsmsgs + " m," + DB.k_x_cat_objs + " x," + DB.k_newsgroups + " g," + DB.k_categories + " c",
    	      "x." + DB.gu_category + ",m." + DB.gu_msg + ",m." + DB.gu_product + ",m." + DB.nm_author + ",m." + DB.tx_subject +
    	      ",m." + DB.dt_published + ",m." + DB.tx_email + ",m." + DB.nu_thread_msgs + ",m." + DB.gu_thread_msg +
    	      ",m." + DB.gu_parent_msg + ",m." + DB.nu_votes + ", 'permalink' AS tx_permalink, m." + DB.tx_msg,
    	      "m." + DB.id_status + "="+String.valueOf(NewsMessage.STATUS_VALIDATED)+" AND x." + DB.gu_category + "=" + "g." + DB.gu_newsgrp + " AND " +
    	      "c." + DB.gu_category + "=g." + DB.gu_newsgrp + " AND " +
    	      "m." + DB.gu_msg + "=x." + DB.gu_object + " AND g." + DB.gu_newsgrp + "=? ORDER BY "+sOrderBy+" DESC", nMaxMsgs);
    oPosts.setMaxRows(nMaxMsgs);    
    int nPosts = oPosts.load (oConn, new Object[]{sGuNewsGroup});
    int iPermalink = oPosts.getColumnPosition("tx_permalink");
    int iSubject = oPosts.getColumnPosition(DB.tx_subject);    
	for (int p=0; p<nPosts; p++) {
	  oPosts.setElementAt(Gadgets.ASCIIEncode(oPosts.getStringNull(iSubject, p, "")).toLowerCase(), iPermalink, p);
	} // next
    return oPosts;
  } // getMessagesForGroup

  // --------------------------------------------------------------------------

  public static DBSubset getTopLevelMessagesForGroup(JDCConnection oConn,
  											         String sGuNewsGroup,
  											         int nMaxMsgs, int nOffset,
  											         String sOrderBy)
  throws SQLException,IllegalArgumentException {
	
	if (nOffset<0) throw new IllegalArgumentException("Forums.getMessagesForGroup() The offset of messages to get must be greater than or equal to zero");
	if (nMaxMsgs<=0) throw new IllegalArgumentException("Forums.getMessagesForGroup() The number of messages to get must be greater than zero");
	if (null==sOrderBy) sOrderBy = DB.dt_published;
    if (sOrderBy.length()==0) sOrderBy = DB.dt_published;

    DBSubset  oPosts = new DBSubset (DB.k_newsmsgs + " m," + DB.k_x_cat_objs + " x," + DB.k_newsgroups + " g," + DB.k_categories + " c",
    	      "x." + DB.gu_category + ",m." + DB.gu_msg + ",m." + DB.gu_product + ",m." + DB.nm_author + ",m." + DB.tx_subject +
    	      ",m." + DB.dt_published + ",m." + DB.tx_email + ",m." + DB.nu_thread_msgs + ",m." + DB.gu_thread_msg +
    	      ",m." + DB.gu_parent_msg + ",m." + DB.nu_votes + ", 'permalink' AS tx_permalink, m." + DB.tx_msg,
    	      "m." + DB.id_status + "="+String.valueOf(NewsMessage.STATUS_VALIDATED)+" AND x." + DB.gu_category + "=" + "g." + DB.gu_newsgrp + " AND " +
    	      "c." + DB.gu_category + "=g." + DB.gu_newsgrp + " AND " +
    	      "m." + DB.gu_parent_msg + " IS NULL AND "+
    	      "m." + DB.gu_msg + "=x." + DB.gu_object + " AND g." + DB.gu_newsgrp + "=? ORDER BY "+sOrderBy+" DESC", nMaxMsgs);
    oPosts.setMaxRows(nMaxMsgs);    
    int nPosts = oPosts.load (oConn, new Object[]{sGuNewsGroup}, nOffset);
    int iPermalink = oPosts.getColumnPosition("tx_permalink");
    int iSubject = oPosts.getColumnPosition(DB.tx_subject);    
	for (int p=0; p<nPosts; p++) {
	  oPosts.setElementAt(Gadgets.ASCIIEncode(oPosts.getStringNull(iSubject, p, "")).toLowerCase(), iPermalink, p);
	} // next
    return oPosts;
  } // getMessagesForGroup

  // --------------------------------------------------------------------------

  public static DBSubset getTopLevelMessagesForGroup(JDCConnection oConn,
  											         String sGuNewsGroup,
  											         Date dtStart, Date dtEnd,
  											         String sOrderBy)
  throws SQLException,IllegalArgumentException {
	
	dtStart = new Date(dtStart.getYear(), dtStart.getMonth(), dtStart.getDate(), 0, 0, 0);
	dtEnd = new Date(dtEnd.getYear(), dtEnd.getMonth(), dtEnd.getDate(), 23, 59, 59);
	if (null==sOrderBy) sOrderBy = DB.dt_published;
    if (sOrderBy.length()==0) sOrderBy = DB.dt_published;

    DBSubset  oPosts = new DBSubset (DB.k_newsmsgs + " m," + DB.k_x_cat_objs + " x," + DB.k_newsgroups + " g," + DB.k_categories + " c",
    	      "x." + DB.gu_category + ",m." + DB.gu_msg + ",m." + DB.gu_product + ",m." + DB.nm_author + ",m." + DB.tx_subject +
    	      ",m." + DB.dt_published + ",m." + DB.tx_email + ",m." + DB.nu_thread_msgs + ",m." + DB.gu_thread_msg +
    	      ",m." + DB.gu_parent_msg + ",m." + DB.nu_votes + ", 'permalink' AS tx_permalink, m." + DB.tx_msg,
    	      "m." + DB.id_status + "="+String.valueOf(NewsMessage.STATUS_VALIDATED)+
    	      " AND x." + DB.gu_category + "=" + "g." + DB.gu_newsgrp + " AND " +
    	      "c." + DB.gu_category + "=g." + DB.gu_newsgrp + " AND " +
    	      "m." + DB.gu_parent_msg + " IS NULL AND "+
    	      "m." + DB.gu_msg + "=x." + DB.gu_object + " AND "+
    	      "g." + DB.gu_newsgrp + "=? AND "+
    	      "m." + DB.dt_published + " BETWEEN ? AND ? "+
    	      "ORDER BY "+sOrderBy+" DESC", 100);
    int nPosts = oPosts.load (oConn, new Object[]{sGuNewsGroup});
    int iPermalink = oPosts.getColumnPosition("tx_permalink");
    int iSubject = oPosts.getColumnPosition(DB.tx_subject);    
	for (int p=0; p<nPosts; p++) {
	  oPosts.setElementAt(Gadgets.ASCIIEncode(oPosts.getStringNull(iSubject, p, "")).toLowerCase(), iPermalink, p);
	} // next
    return oPosts;
  } // getMessagesForGroup

  // --------------------------------------------------------------------------
  
  /**
   * <p>Get top level messages from all groups of a WorkArea</p>
   * @param oConn Database Connection 
   * @param nMaxMsgs Maximum number of messages to get
   * @param sOrderBy Attribute to sort messages. By default it is dt_published which corresponds to publishing date. Can be also nu_votes to sort messages by number of votes or nm_author to sort by author.
   * @param sGuWorkArea WorkArea GUID
   * @param sOrderBy
   * @return DBSubset containing the following columns: gu_category,gu_msg,gu_product,nm_author,tx_subject,dt_published,tx_email,nu_thread_msgs,gu_thread_msg,nu_votes,tx_permalink,tx_msg
   * @throws SQLException
   * @throws IllegalArgumentException If nMaxMsgs<=0
   */
  public static DBSubset getTopLevelMessages(JDCConnection oConn, int nMaxMsgs,
  											 String sGuWorkArea, Boolean bActive,
  											 String sOrderBy)
  throws SQLException,IllegalArgumentException {
	
	String sActiveOnly = "";
	if (nMaxMsgs<=0) throw new IllegalArgumentException("Forums.getTopLevelMessages() The number of messages to get must be greater than zero");
	if (null==sOrderBy) sOrderBy = DB.dt_published;
    if (sOrderBy.length()==0) sOrderBy = DB.dt_published;
	if (bActive!=null) sActiveOnly = (bActive.booleanValue() ? "c."+DB.bo_active+"<>0 AND " : "");
		
    DBSubset  oPosts = new DBSubset (DB.k_newsmsgs + " m," + DB.k_x_cat_objs + " x," + DB.k_newsgroups + " g," + DB.k_categories + " c",
    	      "x." + DB.gu_category + ",m." + DB.gu_msg + ",m." + DB.gu_product + ",m." + DB.nm_author + ",m." + DB.tx_subject +
    	      ",m." + DB.dt_published + ",m." + DB.tx_email + ",m." + DB.nu_thread_msgs + ",m." + DB.gu_thread_msg +
    	      ",m." + DB.nu_votes + ", 'permalink' AS tx_permalink, m." + DB.tx_msg,
    	      sActiveOnly +
    	      "m." + DB.id_status + "=0 AND x." + DB.gu_category + "=" + "g." + DB.gu_newsgrp + " AND " +
    	      "c." + DB.gu_category + "=g." + DB.gu_newsgrp + " AND " +
    	      "m." + DB.gu_msg + "=x." + DB.gu_object + " AND m." + DB.gu_parent_msg + " IS NULL AND g." + DB.gu_workarea +
    	      "=? ORDER BY "+sOrderBy+(sOrderBy.equalsIgnoreCase(DB.dt_published) || sOrderBy.equalsIgnoreCase(DB.nu_votes) ? " DESC" : ""), nMaxMsgs);
    oPosts.setMaxRows(nMaxMsgs);    
    int nPosts = oPosts.load (oConn, new Object[]{sGuWorkArea});
    int iPermalink = oPosts.getColumnPosition("tx_permalink");
    int iSubject = oPosts.getColumnPosition(DB.tx_subject);    
	for (int p=0; p<nPosts; p++) {
	  oPosts.setElementAt(Gadgets.ASCIIEncode(oPosts.getStringNull(iSubject, p, "")).toLowerCase(), iPermalink, p);
	} // next
    return oPosts;
  } // getTopLevelMessages

  // --------------------------------------------------------------------------
  
  public static ArrayList<Boolean> getDaysWithPosts(JDCConnection oConn, String sGuNewsGrp,
  										            Date dtFrom, Date dtTo)
  	throws SQLException {

	SimpleDateFormat oShortDate = new SimpleDateFormat("yyyy-MM-dd");
    if (DebugFile.trace) {
      DebugFile.writeln("Begin Forums.getDaysWithPosts([JDCConnection], "+oShortDate.format(dtFrom)+","+oShortDate.format(dtTo)+")");
      DebugFile.incIdent();
    }
    
    String sCurrDate, sPostDate;
    ArrayList<Boolean> oDaysList = new ArrayList<Boolean>(com.knowgate.misc.Calendar.DaysBetween(dtFrom,dtTo));
	Calendar oDay = Calendar.getInstance(); 
	Calendar oTo = Calendar.getInstance();
	oTo.setTime(dtTo);
    DBSubset oDbs = new DBSubset(DB.k_newsmsgs + " m," + DB.k_x_cat_objs + " x",
    							 "DISTINCT("+DBBind.Functions.ISNULL+"(m."+DB.dt_start+",m."+DB.dt_published+")) AS dt_show",
    							 "x."+DB.gu_category+"=? AND x. "+DB.gu_object+"=m."+DB.gu_msg+" AND x."+DB.id_class+"="+String.valueOf(NewsMessage.ClassId)+" AND "+
    							 DBBind.Functions.ISNULL+"(m."+DB.dt_start+",m."+DB.dt_published+") BETWEEN ? AND ? ORDER BY 1",100);
    int nDays = oDbs.load(oConn, new Object[]{sGuNewsGrp, new Timestamp(dtFrom.getTime()),new Timestamp(dtTo.getTime())});
    if (DebugFile.trace) {
      DebugFile.writeln("Found posts for "+String.valueOf(nDays)+" distinct days");
    }
    oDay.setTime(dtFrom);
	if (0==nDays) {
	  while (oDay.compareTo(oTo)<=0) {
    	if (DebugFile.trace) DebugFile.writeln("-> "+oShortDate.format(oDay.getTime())+" has no posts");
	  	oDaysList.add(new Boolean(false));
	    oDay.add(Calendar.DATE,1);
	  } // wend

	} else {

	  sCurrDate = oShortDate.format(oDay.getTime());
	  int iDay=0;

	  while (oDay.compareTo(oTo)<=0) {
    	while (sCurrDate.compareTo(oDbs.getDateShort(0,0))<0) {
    	  if (DebugFile.trace) DebugFile.writeln("-> "+sCurrDate+" has no posts");
	  	  oDaysList.add(new Boolean(false));
	      oDay.add(Calendar.DATE,1);
	      sCurrDate = oShortDate.format(oDay.getTime());
    	} // wend

    	if (oDay.compareTo(oTo)>0) break;
    	
		boolean bHasPosts = false;

		while (iDay<nDays) {
		  sPostDate = oDbs.getDateShort(0,iDay);
		  int iCompare = sCurrDate.compareTo(sPostDate);
		  if (iCompare>0) iCompare=1; else if (iCompare<0) iCompare=-1;
		  if (0==iCompare) {
		    bHasPosts = true;
	        iDay++;
		    break;		      
		  } else if (-1==iCompare) {
		  	break;
		  } else {
		    iDay++;
		  }
		} // wend (iDay<nDays)
    	if (DebugFile.trace) DebugFile.writeln("=> "+oShortDate.format(oDay.getTime())+" has"+(bHasPosts ? " " : " no ")+"posts");
	  	oDaysList.add(new Boolean(bHasPosts));
	  	oDay.add(Calendar.DATE,1);
		sCurrDate = oShortDate.format(oDay.getTime());
	  } //wend (oDay<=oTo)
	} // fi

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End Forums.getDaysWithPosts()");
    }

    return oDaysList;
  } // getDaysWithPosts

  // --------------------------------------------------------------------------

}