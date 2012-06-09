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
package com.clearforest.calais.simple;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.clearforest.calais.common.CalaisJavaIf;
import com.clearforest.calais.common.Property;
import com.clearforest.calais.common.StringUtils;


/*
 * Java class to return Calais Web service results in JSON
 */
public class CalaisParser implements ErrorHandler {

	private CalaisJavaIf 			m_calaisIf 		= null;
	private boolean		 			m_isLastErr		= false;
	private String		 			m_lastErr		= null;
	private int						m_level			= 0;
	private String					m_l0Tag			= "";
	private String					m_l1Tag			= "";
	private String					m_l2Tag			= "";
	private String					m_l3Tag			= "";
	private ArrayList<Property>		m_infoMap		= null;
	private ArrayList<Entity>		m_entities		= null;
	private Entity					m_currentEntity	= null;

	/**
	 * Pass a valid API key on construction. To obtain an API key please see
	 * http://www.opencalais.com
	 */
	public CalaisParser(String apiKey, String invokeTimeout,
			String overrideDefURL, String serviceURL,
			String verifyCerts) {

		m_calaisIf = new CalaisJavaIf(apiKey);
		m_calaisIf.setTimeout(Integer.parseInt(invokeTimeout));
		if (overrideDefURL.equals("true"))
		{
			m_calaisIf.setCalaisURL(serviceURL);
		}
		if (verifyCerts.equals("false"))
		{
			m_calaisIf.setVerifyCert(false);
		}
	}
	
	public boolean isLastErr() {
		return m_isLastErr;
	}

	public String getLastErr() {
		return m_lastErr;
	}

	/**
	 * Submit content to the OpenCalaisAPI, requesting output in
	 * simple format. Return the results in simple-format JSON
	 * @throws Exception 
	 */
	public ArrayList<Entity> getCalaisEntities(String content) throws Exception {

		String 			resp_simple = null;
		//StringBuilder 	json 		= null;


		/*
		 * Call the Web service on content
		 */
		m_calaisIf.setOutputFormat("text/simple");
		resp_simple = m_calaisIf.callEnlighten(content);

	
		return parseSimple(resp_simple);
	}
	
    /**
     * @param content
     * @return
     */
    public String getJSON(String content)
    {
        m_calaisIf.setOutputFormat("application/json");
        return m_calaisIf.callEnlighten(content);
    }
    
    public String getRDF(String content)
    {
        m_calaisIf.setOutputFormat("xml/rdf");
        return m_calaisIf.callEnlighten(content);
    }

	/**
	 * @param resp_simple
	 * @throws Exception
	 */
	public  ArrayList<Entity> parseSimple(String resp_simple) throws Exception {
		/*
		 * Analyze response errors
		 */
		if (m_calaisIf.isLastErr())
		{
			throw new Exception( m_calaisIf.getLastErr());
		}

		if (resp_simple.indexOf("Enlighten ERROR:") != -1)
		{
			throw new Exception(resp_simple);
		}

		if (resp_simple.indexOf("<Exception>") != -1)
		{
			throw new Exception("Enlighten ERROR: " + resp_simple);
		}

		if (resp_simple.indexOf("<h1>403 Developer Inactive</h1>") != -1)
		{
			throw new Exception("Enlighten ERROR: " + resp_simple);
		}

		/*
		 * Response is valid - parse XML
		 */
		try
		{
			m_level = 0;
			m_l0Tag = "";
			m_l1Tag = "";
			m_l2Tag = "";
			m_l3Tag = "";
			m_infoMap = new ArrayList<Property>();
			m_entities = new ArrayList<Entity>();
			
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(new ContentHandler());
			reader.setErrorHandler(this);
			resp_simple = StringUtils.unescapeHTML(resp_simple);
			reader.parse(new InputSource(new StringReader(resp_simple)));
		}
		catch(IOException e)
		{
			throw new Exception(e.getMessage());
		}
		catch (SAXException e)
		{
			throw new Exception(e.getMessage());
		}
		return m_entities;
	}
	
	

	
	/**
	 * Parse error handler functions
	 */
	public void warning(SAXParseException e)
	{
		m_isLastErr = true;
		m_lastErr = "Failed to parse response: " + e.getMessage();
	}
	
	public void error(SAXParseException e) 
	{
		m_isLastErr = true;
		m_lastErr = "Failed to parse response: " + e.getMessage();
	}
	
	public void fatalError(SAXParseException e)
	{
		m_isLastErr = true;
		m_lastErr = "Failed to parse response: " + e.getMessage();
	}
	
	private String err(String err)
	{
		m_isLastErr = true;
		m_lastErr = err;
		return m_lastErr;
	}


	/*
	 * XML parsing of Simple Output Format
	 */
	public class ContentHandler extends DefaultHandler {

		public void startElement(
			String 		namespaceURI,
			String 		localName,
			String 		qName,
			Attributes 	attributes)
		{
			String 	countStr 	= null;
			
			if (m_isLastErr)
			{
				return;
			}

			if (m_level == 0)
			{
				/*
				 * Level 0 - the string tag
				 */
				if (!qName.equals("string"))
				{
					err("Failed to parse Simple Format - root tag is not string - " + qName);
					return;		
				}
				
				m_l0Tag = qName;
				
			}
			else if (m_level == 1)
			{
				/*
				 * Level 1 - the OpenCalaisSimple tag
				 */
				if (!qName.equals("OpenCalaisSimple"))
				{
					err("Failed to parse Simple Format - below root tag is not OpenCalaisSimple - " + qName);
					return;		
				}
				
				m_l1Tag = qName;
			}
			else if (m_level == 2)
			{
				/*
				 * Level 2 - Description or CalaisSimpleOutputFormat
				 */
				if (!qName.equals("Description") && 
					!qName.equals("CalaisSimpleOutputFormat"))
				{
					err("Failed to parse Simple Format - level 2 tag is not Description or CalaisSimpleOutputFormat - " + qName);
					return;		
				}
				
				m_l2Tag = qName;
				
			}
			else if (m_level > 2)
			{
				/*
				 * Level 3+ - Description information or semantic data
				 */
				if (m_l2Tag.equals("Description"))
				{
					/*
					 * Information under the Description element - place
					 * in info array (done in characters)
					 */
					m_l3Tag = qName;
				}
				else if (m_l2Tag.equals("CalaisSimpleOutputFormat"))
				{
					/*
					 * Information under the CalaisSimpleOutputFormat - start
					 * an entities array element
					 */
					m_currentEntity = new Entity();
					m_currentEntity.setType(qName);
	
					countStr = attributes.getValue("count");
					if (countStr != null)
					{
						m_currentEntity.setCount(
								Integer.parseInt(countStr));
					}
				}
			}
			
			m_level++;
	
		}

		public void characters(
			char[] ch,
			int start,
			int length)
		{
		
			if (m_isLastErr)
			{
				return;
			}  	
			
			String data = new String(ch,start,length);
	
			/*
			 * Data within <tag> </tag> - set the name of the new element
			 */
			if (m_l2Tag.equals("CalaisSimpleOutputFormat") && m_level == 4)
			{
				m_currentEntity.setName(data.trim());
			}
	
			/*
			 * Data within <tag> </tag> for Description sub-elements - place
			 * in info array
			 */
			if (m_l2Tag.equals("Description") && m_level == 4)
			{
				m_infoMap.add(new Property(m_l3Tag, data.trim()));
			}
		
		}

		public void endElement(
			String namespaceURI,
			String localName,
			String qName)
		{
			
			if (m_isLastErr)
			{
				return;
			}
			
			if (m_level == 0)
			{
				err("Failed to parse Simple Format - internal error");
				return;		
			}
			
			if (m_level == 1)
			{
				/*
				 * Level 0 - closing the string tag 
				 */
				if (!qName.equals("string"))
				{
					err("Failed to parse Simple Format - root closing tag is not string - " + qName);
					return;		
				}
				
				m_l0Tag = "";
			}
			else if (m_level == 2)
			{
				/*
				 * Level 1 - closing the OpenCalaisSimple tag
				 */
				if (!qName.equals("OpenCalaisSimple"))
				{
					err("Failed to parse Simple Format - below root closing tag is not OpenCalaisSimple - " + qName);
					return;		
				}
				
				m_l1Tag = "";
			}
			else if (m_level == 3)
			{
				/*
				 * Level 2 - closing Description or CalaisSimpleOutputFormat
				 */
				if (!qName.equals("Description") && 
					!qName.equals("CalaisSimpleOutputFormat"))
				{
					err("Failed to parse Simple Format - level 2 closing tag is not Description or CalaisSimpleOutputFormat - " + qName);
					return;		
				}
				
				m_l2Tag = "";
			}
			else if (m_level == 4)
			{
				/*
				 * Level 3 - closing tags (maybe under description) - reset
				 * l3 tag
				 */
				m_l3Tag = "";

				/*
				 * add the new element to the array
				 */
				if (m_l2Tag.equals("CalaisSimpleOutputFormat"))
				{
					m_entities.add(m_currentEntity);
					m_currentEntity = null;
				}
			}
			else if (m_level > 4)
			{
				/*
				 * Level 4+ - no-op
				 */
			}

			m_level--;
			
		}
		
	}	
	
}

