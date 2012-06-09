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

import java.util.*;
import java.util.Map.Entry;

public class StringUtils {

  private static HashMap<String,String> s_htmlEntities;
  private static HashMap<String,String> s_reverseHtmlEntities;

  static {
    s_htmlEntities = new HashMap<String,String>();

    s_htmlEntities.put("&nbsp;"," ");
    s_htmlEntities.put("&cent;","¢");
    s_htmlEntities.put("&pound;","£");
    s_htmlEntities.put("&yen;","¥");
    s_htmlEntities.put("&brvbar;","¦");
    s_htmlEntities.put("&sect;","§");
    s_htmlEntities.put("&uml;","¨");
    s_htmlEntities.put("&copy;","©");
    s_htmlEntities.put("&ordf;","×");
    s_htmlEntities.put("&laquo;","«");
    s_htmlEntities.put("&not;","¬");
    s_htmlEntities.put("&shy;","­");
    s_htmlEntities.put("&reg;","®");
    s_htmlEntities.put("&macr;","¯");
    s_htmlEntities.put("&deg;","°");
    s_htmlEntities.put("&plusmn;","±");
    s_htmlEntities.put("&sup2;","²");
    s_htmlEntities.put("&sup3;","³");
    s_htmlEntities.put("&acute;","´");
    s_htmlEntities.put("&micro;","µ");
    s_htmlEntities.put("&para;","¶");
    s_htmlEntities.put("&middot;","·");
    s_htmlEntities.put("&cedil;","¸");
    s_htmlEntities.put("&sup1;","¹");
    s_htmlEntities.put("&ordm;","÷");
    s_htmlEntities.put("&raquo;","»");
    s_htmlEntities.put("&frac14;","¼");
    s_htmlEntities.put("&frac12;","½");
    s_htmlEntities.put("&frac34;","¾");
    s_htmlEntities.put("&iquest;","¿");
    s_htmlEntities.put("&Agrave;","?");
    s_htmlEntities.put("&Aacute;","?");
    s_htmlEntities.put("&Acirc;","?");
    s_htmlEntities.put("&Atilde;","?");
    s_htmlEntities.put("&Auml;","?");
    s_htmlEntities.put("&Aring;","?");
    s_htmlEntities.put("&AElig;","?");
    s_htmlEntities.put("&Ccedil;","?");
    s_htmlEntities.put("&Egrave;","?");
    s_htmlEntities.put("&Eacute;","?");
    s_htmlEntities.put("&Ecirc;","?");
    s_htmlEntities.put("&Euml;","?");
    s_htmlEntities.put("&Igrave;","?");
    s_htmlEntities.put("&Iacute;","?");
    s_htmlEntities.put("&Icirc;","?");
    s_htmlEntities.put("&Iuml;","?");
    s_htmlEntities.put("&ETH;","?");
    s_htmlEntities.put("&Ntilde;","?");
    s_htmlEntities.put("&Ograve;","?");
    s_htmlEntities.put("&Oacute;","?");
    s_htmlEntities.put("&Ocirc;","?");
    s_htmlEntities.put("&Otilde;","?");
    s_htmlEntities.put("&Ouml;","?");
    s_htmlEntities.put("&times;","?");
    s_htmlEntities.put("&Oslash;","?");
    s_htmlEntities.put("&Ugrave;","?");
    s_htmlEntities.put("&Uacute;","?");
    s_htmlEntities.put("&Ucirc;","?");
    s_htmlEntities.put("&Uuml;","?");
    s_htmlEntities.put("&Yacute;","?");
    s_htmlEntities.put("&THORN;","?");
    s_htmlEntities.put("&szlig;","?");
    s_htmlEntities.put("&ucirc;","?");
    s_htmlEntities.put("&uuml;","?");
    s_htmlEntities.put("&yacute;","?");
    s_htmlEntities.put("&thorn;","?");
    s_htmlEntities.put("&yuml;","?");
    s_htmlEntities.put("&quot;","\"");
    s_htmlEntities.put("&lt;","<");
    s_htmlEntities.put("&gt;",">");
    s_htmlEntities.put("&amp;","&");
    
    s_reverseHtmlEntities = new HashMap<String,String>();
    Iterator<Entry<String,String>> iter = s_htmlEntities.entrySet().iterator();
    while (iter.hasNext())
    {
    	Entry<String,String> entry = iter.next();
    	s_reverseHtmlEntities.put(entry.getValue(), entry.getKey());
    }
  }

  /**
   * Convert a string with escaped HTML characters to the
   * string with the unescaped value (e.g. &lt; changes to <) 
   */
  public static final String unescapeHTML(String source){
     
	  StringBuilder ret 	= new StringBuilder();
	  int 			open	= -1;
	  int			close	= -1;
	  int			last 	= 0;
     
	  while((open = source.indexOf("&", last)) != -1)
	  {
        close = source.indexOf(";" ,open);
        if (close < open)
        {
        	/*
        	 * Error encountered - stop unescaping here
        	 */
        	return ret.toString();
        }
        
        ret.append(source.substring(last, open));
        
        String entityToLookFor = source.substring(open , close + 1);
        String value = (String)s_htmlEntities.get(entityToLookFor);
        if (value != null) {
        	ret.append(value);
        }

        last = close + 1;
     }
	 ret.append(source.substring(last));

	 return ret.toString();
  }

  /**
   * Convert a regular string to a string with escaped
   * HTML entities (e.g. < changes to &lt; )
   */
  public static final String escapeHTML(String source){
     
	  StringBuilder ret 	= new StringBuilder();
	  String		val		= null;
	  int			idx		= 0;
	  
	  for (idx = 0; idx < source.length(); idx++)
	  {
		  val = s_reverseHtmlEntities.get(source.substring(idx, idx + 1));
		  if (val == null)
		  {
			  ret.append(source.charAt(idx));
		  }
		  else
		  {
			  ret.append(val);
		  }
	  }

	 return ret.toString();
  }
  
	/*
	 * Return the original string repeated count times
	 */
	public static String strRepeat(String str, int count)
	{
		StringBuilder ret = new StringBuilder();
		
		for (int i = 0; i < count; i++)
		{
			ret.append(str);
		}
		
		return ret.toString();
	}
	
	/**
	 * Split string str based on separator sep. Retrieve the 
	 * index'th element. If index >= 0 it's the index'th from
	 * the start. If index < 0 it's the -index'th from the end.
	 * 
	 * E.g. for str 'abc/def/ghi/jkl' using sep '/' refer by index
	 * 				  0   1   2   3
	 * 				 -4  -3  -2  -1
	 * 
	 * @param str - the string to analyze
	 * @param sep - the separator in the string
	 * @param index - the index (>= 0 from start, < 0 from end)
	 * @return the requested substring
	 */	
	public static String getSubstringBySeparator(String str,
			String sep, int index) {
		
		String[] arr = null;
		
		if (str == null)
		{
			return null;
		}
		
		arr = str.split(sep);
		if (index >= 0)
		{
			/*
			 * Index from start of string
			 */
			if (index >= arr.length)
			{
				return null;
			}
			
			return arr[index];
		}
		else
		{
			if ((index * -1) > arr.length)
			{
				return null;
			}
			
			return arr[arr.length + index];
		}
	}
}

