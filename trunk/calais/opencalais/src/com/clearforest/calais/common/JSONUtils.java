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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Common JSON utils used by different servlets
 *
 */
public class JSONUtils {

	/**
	 * Escape JSON special characters in string by adding
	 * backslash (\) before
	 * @param str - string to escape
	 * @return JSON-escaped string
	 */
	public static String escapeForJSON(String str) {
		
		StringBuilder 	ret = new StringBuilder();
		int				idx	= 0;
		
		if (str == null)
		{
			return null;
		}
		
		while (idx < str.length())
		{
			switch(str.charAt(idx))
			{
				case '"':
					ret.append("\\\"");
					break;
					
				case '\\':
					ret.append("\\\\");
					break;
					
				case '/':
					ret.append("\\/");
					break;
					
				case '\b':
					ret.append("\\b");
					break;
					
				case '\f':
					ret.append("\\f");
					break;
					
				case '\n':
					ret.append("\\n");
					break;
					
				case '\r':
					ret.append("\\r");
					break;
					
				case '\t':
					ret.append("\\t");
					break;
					
				default:
					ret.append(str.charAt(idx));
					break;
					
			}
			
			idx++;
		}
		
		return ret.toString();
	}

	/*
	 * Convert provided json string into HTML-printable format
	 * by providing indentation and <br>s
	 */
	public static String convertJSONToPrintableHTML(String json)
	{
	    String 			tab 		= "&nbsp;&nbsp;&nbsp;&nbsp;";
	    StringBuilder	new_json 	= new StringBuilder();
	    String			retStr		= null;
	    int				indent_level= 0;
	    boolean			in_string 	= false;
	    int				i;
	    char			c;
	    
	    json = StringUtils.escapeHTML(json);
	    
	    for(i = 0; i < json.length(); i++)
	    {
	    	c = json.charAt(i);

	        switch(c)
	        {
	            case '{':
	            case '[':
	                if(!in_string)
	                {
	                    new_json.append("" + c + "<br>\n" + StringUtils.strRepeat(tab, indent_level+1));
	                    indent_level++;
	                }
	                else
	                {
	                    new_json.append(c);
	                }
	                break;
	            case '}':
	            case ']':
	                if(!in_string)
	                {
	                    indent_level--;
	                    new_json.append("<br>\n" + StringUtils.strRepeat(tab, indent_level) + c);
	                }
	                else
	                {
	                    new_json.append(c);
	                }
	                break;
	            case ',':
	                if(!in_string)
	                {
	                    new_json.append(",<br>\n" + StringUtils.strRepeat(tab, indent_level));
	                }
	                else
	                {
	                    new_json.append(c);
	                }
	                break;
	            case ':':
	                if(!in_string)
	                {
	                    new_json.append(": ");
	                }
	                else
	                {
	                	new_json.append(c);
	                }
	                break;
	            case '&':	// deal with &quot;
	            	if (json.substring(i).startsWith("&quot;"))
	            	{
	            		if (i == 0 || (json.charAt(i - 1) != '\\'))
	            		{
	            			in_string = !in_string;
	            		}
	            	}
	            	/*
	            	 * Fall through to append the '"'
	            	 */
	            default:
	            	new_json.append(c);
	                break;                    
	        }
	    }
	    
	    retStr = new_json.toString();
	    retStr = retStr.replace("\\/", "/");
	    retStr = retStr.replace("http: //", "http://");
	    return retStr;
	} 

	/*
	 * Convert a String to String map as a JSON object of the form
	 * {
	 *   "key1":"value1",
	 *   "key2":"value2",
	 *   
	 *   "keyn":"valuen"
	 * }
	 */
	public static String mapToJSON(HashMap<String,String> map)
	{
		StringBuilder 	ret 	= new StringBuilder();
		boolean			first 	= true;
		
		ret.append("{ ");
		
		if (map != null && map.size() > 0)
		{
			Iterator<Entry<String,String>> iter = map.entrySet().iterator();
			while(iter.hasNext())
			{
				Entry<String,String> entry = iter.next();
				
				if (!first)
				{
					ret.append(", ");
				}
				ret.append("\"" + JSONUtils.escapeForJSON(entry.getKey()) + "\":");
				ret.append("\"" + JSONUtils.escapeForJSON(entry.getValue()) + "\"");
				first = false;
			}
		}
		
		ret.append("}");
		
		return ret.toString();
	}
	
	/*
	 * Return a Property ArrayList as a JSON object of the form
	 * {
	 *   "name1":"value1",
	 *   "name2":"value2",
	 *   
	 *   "namen":"valuen"
	 * }
	 */
	public static String propertyListToJSON(ArrayList<Property> list)
	{
		StringBuilder 	ret 	= new StringBuilder();
		boolean			first 	= true;
		
		ret.append("{ ");
		
		if (list != null && list.size() > 0)
		{
			Iterator<Property> iter = list.iterator();
			while(iter.hasNext())
			{
				Property prop = iter.next();
				
				if (!first)
				{
					ret.append(", ");
				}
				ret.append("\"" + JSONUtils.escapeForJSON(prop.getName()) + "\":");
				ret.append("\"" + JSONUtils.escapeForJSON(prop.getValue()) + "\"");
				first = false;
			}
		}
		
		ret.append("}");
		
		return ret.toString();
	}
}
