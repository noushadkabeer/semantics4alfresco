/*
 * Copyright (c) 2008, ClearForest Ltd.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * 		- 	Redistributions of source code must retain the above 
 * 			copyright notice, this list of conditions and the 
 * 			following disclaimer.
 * 
 * 		- 	Redistributions in binary form must reproduce the above 
 * 			copyright notice, this list of conditions and the 
 * 			following disclaimer in the documentation and/or other 
 * 			materials provided with the distribution. 
 * 
 * 		- 	Neither the name of ClearForest Ltd. nor the names of 
 * 			its contributors may be used to endorse or promote 
 * 			products derived from this software without specific prior 
 * 			written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.clearforest.calais.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Java Class to access the Calais Web service
 *
 */
public class CalaisJavaIf {
		
	private String 	m_apiKey 		= null;
	private String 	m_url 			= "http://api.opencalais.com/enlighten/calais.asmx/Enlighten";
	private String 	m_paramsXML 	= null;
	private String 	m_contentType 	= "text/txt";
	private String 	m_outputFormat	= "xml/rdf";
	private String 	m_reltagBase	= null;
	private boolean m_isLastErr		= false;
	private String 	m_lastErr		= null;
	private int		m_timeout		= 60;
	private boolean m_verifyCert	= true;		// For future use - Whether to verify SSL certificate in Web service call
	
	/**
	 * Pass a valid API key on construction. To obtain an API key please see
	 * http://www.opencalais.com
	 */
	public CalaisJavaIf(String apiKey) {
		
		m_apiKey = apiKey;
		m_paramsXML = buildParamsXML();
	}
	
	/**
	 * Call the OpenCalais Enlighten Web Service and return the raw results
	 * 
	 * content is the text to analyze. If you called setContentType to change
	 * the content type, make sure you provide the content here in the 
	 * appropriate format
	 */
	public String callEnlighten(String content) {

		URL					url		= null;
		URLConnection		conn	= null;
		OutputStreamWriter	out		= null;
		BufferedReader		in		= null;
		String 				data 	= null;	// POST data
		StringBuilder		result	= null;
		String				str		= null;
		String				strRes	= null;

		/*
		 * Some content must be provided
		 */
		if (content == null || content.length() == 0)
		{
			m_isLastErr = true;
			m_lastErr = "ERROR: Non-empty content is required";
			return m_lastErr;
		}
	
		/*
		 * Construct the POST data string
		 */
		try
		{
			data = "licenseID=" + URLEncoder.encode(m_apiKey, "UTF-8") +
				"&paramsXML=" + URLEncoder.encode(m_paramsXML, "UTF-8") +
				"&content=" + URLEncoder.encode(content, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			m_isLastErr = true;
			m_lastErr = "ERROR: Internal error";
			return m_lastErr;
		}

		
		/*
		 * Invoke the Web service via HTTP POST
		 */
		try
		{
			url = new URL(m_url);
			conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(data);
			out.close();
			
			/*
			 * Make the call to the Web service
			 */
			conn.connect();
			conn.setConnectTimeout(m_timeout * 1000);
			
			/*
			 * Process the Web service response
			 */
			result = new StringBuilder();
			
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			while (null != ((str = in.readLine())))
			{
				result.append(str);
			}
		}
		catch(MalformedURLException e)
		{
			m_isLastErr = true;
			m_lastErr = e.getMessage();
			return m_lastErr;
		}
		catch(IOException e)
		{
			m_isLastErr = true;
			m_lastErr = e.getMessage();
			return m_lastErr;
		}

		/*
		 * Analyze response
		 */
		strRes = result.toString();
		if (strRes.indexOf("<Exception>") != -1)
		{
			m_isLastErr = true;
			m_lastErr = "Enlighten ERROR: " + strRes;
			return m_lastErr;
		}

		m_isLastErr = false;
		m_lastErr = null;
		
		return strRes;
	}
	
	/*
	 * Change the content type submitted to the Calais Web service.
	 * Pass strings with this content in future calls to callEnlighten
	 * 
	 * Default is text/txt 
	 */
	public void setContentType(String contentType) {
		m_contentType = contentType;
		m_paramsXML = buildParamsXML();
	}
	
	/*
	 * Returns the current content type
	 */	
	public String getContentType() {
		return m_contentType;
	}
	
	/*
	 * Change the output format returned by callEnlighten
	 * 
	 * Default is xml/rdf
	 */
	public void setOutputFormat(String outputFormat) {
		m_outputFormat = outputFormat;
		m_paramsXML = buildParamsXML();
	}
	
	/*
	 * Returns the current output format
	 */
	public String getOutputFormat() {
		return m_outputFormat;
	}
	
	/*
	 * Set the timeout in seconds for the API call
	 */
	public void setTimeout(int timeout) {
		m_timeout = timeout;
	}
	
	/*
	 * Get the timeout in seconds for the API call
	 */
	public int getTimeout() {
		return m_timeout;
	}
	
	/**
	 * Set the reltagBaseURL param in calls to Calais Web service
	 * when retrieving output in Microformats
	 */
	public void setReltagBaseURL(String baseURL) {
		m_reltagBase = baseURL;
		m_paramsXML = buildParamsXML();
	}
	
	/**
	 * Get the reltagBaseURL param in use when calling Calais Web 
	 * service with output Microformats
	 */
	public String getReltagBaseURL() {
		return m_reltagBase;
	}
	
	/**
	 * Override the default Calais Web service URL
	 * (Normally, you should not change it)
	 */
	public void setCalaisURL(String url) {
		m_url = url;
	}
	
	/**
	 * Returns the Calais Web service URL in use
	 */
	public String getCalaisURL() {
		return m_url;
	}
	
	/**
	 * For future use
	 * Sets flag - whether to verify SSL certificate of Calais
	 * Web service when calling. Default is TRUE.
	 * (Normally, there's no need to change this)
	 */
	public void setVerifyCert(boolean verify) {
		m_verifyCert = verify;
	}
	
	/**
	 * Return the current value of the verify certificate flag
	 */
	public boolean isVerifyCert() {
		return m_verifyCert;
	}
	
	/*
	 * Returns true if last invocation of Web service failed
	 * 
	 * You can retrieve the error message using getLastErr();
	 */
	public boolean isLastErr() {
		return m_isLastErr;
	}
	
	/**
	 * Return the last error value returned by the Web service.
	 * 
	 * Empty string is returned when no error occurred.
	 */
	public String getLastErr() {
		return m_lastErr;
	}
	
	/*
	 * Internal function that generates the paramsXML from class members
	 */
	public String buildParamsXML() {
	
		String ret = 
			"<c:params xmlns:c=\"http://s.opencalais.com/1/pred/\" " + 
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"> " +
			"<c:processingDirectives c:contentType=\"" +
			m_contentType +
			"\" c:outputFormat=\"" +
			m_outputFormat + "\" " +
			(m_outputFormat.equalsIgnoreCase("text/microformats") ? 
				(" c:reltagBaseURL=\"" + m_reltagBase + "\"")
				: "") +
			"></c:processingDirectives> " +
			"<c:userDirectives c:allowDistribution=\"false\" " +
			"c:allowSearch=\"false\" c:externalID=\" \" " +
			"c:submitter=\"Calais Java Interface\"></c:userDirectives> " +
			"<c:externalMetadata></c:externalMetadata></c:params>";
			
		return ret;
		
		
	}
	
	
}