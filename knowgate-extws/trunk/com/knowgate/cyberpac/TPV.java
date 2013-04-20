/*
  Copyright (C) 2003-2012  Know Gate S.L. All rights reserved.

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

package com.knowgate.cyberpac;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import java.math.BigDecimal;
import java.math.MathContext;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;

import com.knowgate.debug.DebugFile;
import com.knowgate.misc.Gadgets;

public class TPV extends Properties {

  private static final long serialVersionUID = 55000l;

  // --------------------------------------------------------------------------

  public TPV () { }

  // --------------------------------------------------------------------------

  public TPV (File oFile) throws SecurityException, FileNotFoundException, IOException {
	if (!oFile.exists()) throw new FileNotFoundException("File not found "+oFile.getPath());
	if (!oFile.canRead()) throw new SecurityException("Cannot read from file "+oFile.getPath());
  	FileInputStream oFis = new FileInputStream(oFile);
  	load(oFis);
  	oFis.close();
  }

  // --------------------------------------------------------------------------

  public TPV (Properties oProps) throws IOException {
	Enumeration oKeys = keys();
	while (oKeys.hasMoreElements()) {
	  String sKey = (String) oKeys.nextElement();
	  setProperty(sKey, oProps.getProperty(sKey));
	} // wend
  }

  // --------------------------------------------------------------------------

  // public int Merchant_Terminal;
  // public String Merchant_Name;
  // public String Merchant_Code;
  // public String Merchant_Password;
  // public String Merchant_Currency;
  // public String Merchant_TransactionType;
  // public String Merchant_MerchantURL;

  
  // --------------------------------------------------------------------------

  public String post (String sOrderId, String sProductDescription, BigDecimal oTotalAmount, String sData)
  	throws MalformedURLException, IOException {

	if (DebugFile.trace) {
	  DebugFile.writeln("Begin TPV.post("+sOrderId+","+sProductDescription+""+oTotalAmount+","+sData+")");
	  DebugFile.incIdent();
	}

	String sHtml = null;
	String sTotalAmount100 = oTotalAmount.movePointRight(2).round(MathContext.DECIMAL32).toString();
	StringBuffer oParams = new StringBuffer();

    oParams.append("Ds_Merchant_Amount="+sTotalAmount100);
    oParams.append("&Ds_Merchant_Order="+sOrderId);
    oParams.append("&Ds_Merchant_ProductDescription="+URLEncoder.encode(sProductDescription,"ISO8859_1")); 
	oParams.append("&Ds_Merchant_MerchantSignature="+getSignature(sOrderId, sTotalAmount100));
	if (sData!=null) if (sData.length()>0)
      oParams.append("&Ds_Merchant_MerchantData="+URLEncoder.encode(sData,"ISO8859_1")); 
		
	Enumeration oKeys = keys();
	while (oKeys.hasMoreElements()) {
	  String sKey = (String) oKeys.nextElement();
	  if (!sKey.equalsIgnoreCase("Ds_Merchant_MerchantPassword"))
        oParams.append("&"+sKey+"="+URLEncoder.encode(getProperty(sKey),"ISO8859_1"));
	} // wend
	String sParams = oParams.toString();
	oParams = null;
	
	URL oUrl = new URL(getProperty("Ds_Merchant_BankURL"));
		
	HttpsURLConnection oCon = (HttpsURLConnection) oUrl.openConnection();
    oCon.setUseCaches(false);
    oCon.setFollowRedirects(false);
    oCon.setInstanceFollowRedirects(false);
    oCon.setDoInput (true);
    oCon.setRequestProperty("Referer", getProperty("Ds_Merchant_MerchantURL"));
    oCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	oCon.setRequestProperty("Content-Length", String.valueOf(sParams.getBytes().length));
    oCon.setFixedLengthStreamingMode(sParams.getBytes().length);
    oCon.setDoOutput(true);

	if (DebugFile.trace) {
	  DebugFile.writeln("HttpsURLConnection.setRequestMethod(POST) to "+getProperty("Ds_Merchant_BankURL"));
	}
    
	oCon.setRequestMethod("POST");
	OutputStreamWriter oWrt = new OutputStreamWriter(oCon.getOutputStream());
    oWrt.write(sParams);
    oWrt.flush();
    oWrt.close();
		        
	int responseCode = oCon.getResponseCode();
	if (DebugFile.trace) DebugFile.writeln("response code is "+String.valueOf(responseCode));
	if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
		responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
	  if (DebugFile.trace) DebugFile.writeln("must redirect to "+oCon.getHeaderField("Location"));
	} else {

	  InputStream oStm = oCon.getInputStream();
	  String sEnc = (oCon.getContentEncoding()==null ? "ISO8859_1" : oCon.getContentEncoding());
	  InputStreamReader oRdr = new InputStreamReader(oStm, sEnc);

      ByteArrayOutputStream oByArrOut = new ByteArrayOutputStream(8000);
      int c = oRdr.read();
      while (c!=-1) {
        oByArrOut.write(c);
        c = oRdr.read();
      } // wend
      sHtml = oByArrOut.toString("ISO_8859_1");
      int iHead = Gadgets.indexOfIgnoreCase(sHtml, "<head>", 0);
      sHtml = sHtml.substring(0,iHead+6)+"<base href=\""+oUrl.toString()+"\"\n>"+sHtml.substring(iHead+6);
	  oRdr.close();	  
	}

	if (DebugFile.trace) {
	  DebugFile.decIdent();
	  DebugFile.writeln("End TPV.post()");
	}
    
    return sHtml;
  } // post

  // --------------------------------------------------------------------------

  public String getSignature(String sStr) {
		MessageDigest sha;

		final int SHA1_DIGEST_LENGTH = 20;

		try {
		  sha = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException neverthrown) {
		  sha = null;
		}

		sha.update(sStr.getBytes());

		byte[] hash = sha.digest(getProperty("Ds_Merchant_MerchantPassword").getBytes());

		StringBuffer oSignature = new StringBuffer();

		int h = 0;
		String s = new String();

		for (int i = 0; i < SHA1_DIGEST_LENGTH; i++) {
			h = (int) hash[i]; // Convertir de byte a int
			// Si son valores negativos, pueden haber problemas de conversion.
			if (h < 0) h += 256; 
			s = Integer.toHexString(h); // Devuelve el valor hexadecimal como String
			if (s.length() < 2) oSignature.append("0");		
			oSignature.append(s);
		} // next

		return oSignature.toString().toUpperCase();
  } // getSignature

  // --------------------------------------------------------------------------
  	
  private String getSignature(String sOrderId, String sTotalAmount) {

		MessageDigest sha;

		final int SHA1_DIGEST_LENGTH = 20;

		try {
		  sha = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException neverthrown) {
		  sha = null;
		}

		sha.update(sTotalAmount.getBytes());
		sha.update(sOrderId.getBytes());
		sha.update(getProperty("Ds_Merchant_MerchantCode").getBytes());
		sha.update(getProperty("Ds_Merchant_Currency").getBytes());
		sha.update(getProperty("Ds_Merchant_TransactionType").getBytes());
		sha.update(getProperty("Ds_Merchant_MerchantURL").getBytes());
		byte[] hash = sha.digest(getProperty("Ds_Merchant_MerchantPassword").getBytes());

		StringBuffer oSignature = new StringBuffer();

		int h = 0;
		String s = new String();

		for (int i = 0; i < SHA1_DIGEST_LENGTH; i++) {
			h = (int) hash[i]; // Convertir de byte a int
			// Si son valores negativos, pueden haber problemas de conversion.
			if (h < 0) h += 256; 
			s = Integer.toHexString(h); // Devuelve el valor hexadecimal como String
			if (s.length() < 2) oSignature.append("0");		
			oSignature.append(s);
		} // next

		return oSignature.toString().toUpperCase();
	} // getSignature

  // --------------------------------------------------------------------------

  public String responseMessage(int iResponseCode) {

	String sRespText;

	if (iResponseCode>=0 && iResponseCode<=99)
		sRespText = "Transaccion autorizada para pagos y preautorizaciones";
	else {
		switch (iResponseCode) {
			case 101:
				sRespText = "Tarjeta caducada";
				break;
			case 102:
				sRespText = "Tarjeta en excepcion transitoria o bajo sospecha de fraude";
				break;
			case 103:
				sRespText = "Operacion no permitida para esa tarjeta o terminal";
				break;
			case 116:
				sRespText = "Disponible insuficiente";
				break;
			case 118:
				sRespText = "Tarjeta no registrada";
				break;
			case 192:
				sRespText = "Codigo de seguridad (CVV2/CVC2) incorrecto";
				break;
			case 180:
				sRespText = "Tarjeta ajena al servicio";
				break;
			case 184:
				sRespText = "Error en la autenticacion del titular";
				break;
			case 190:
				sRespText = "Denegacion sin especificar Motivo";
				break;
			case 191:
				sRespText = "Fecha de caducidad erronea";
				break;
			case 202:
				sRespText = "Tarjeta en excepcion transitoria o bajo sospecha de fraude con retirada de tarjeta";
				break;
			case 900:
				sRespText = "Transaccion autorizada para devoluciones y confirmaciones";
				break;
			case 912:
			case 9912:
				sRespText = "Emisor no disponible";
				break;
			case 913:
				sRespText = "Pedido repetido";
				break;
			default:
				sRespText = "Transaccion denegada "+String.valueOf(iResponseCode);
		}
	} // fi
	return sRespText;

  } // responseMessage

  // --------------------------------------------------------------------------

  public static void main (String[] args) throws Exception {
  	java.text.SimpleDateFormat oFmt = new java.text.SimpleDateFormat("yyMMddHHmmss");
  	TPV oTpv = new TPV(new File("C:\\caixa.txt"));
  	
	System.out.println(oTpv.post(oFmt.format(new Date()), "Descripcion de Producto", new BigDecimal("113.45"), ""));
  }
}  // TPV
