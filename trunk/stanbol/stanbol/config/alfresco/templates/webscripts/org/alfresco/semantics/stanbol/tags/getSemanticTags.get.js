const SITES_SPACE_QNAME_PATH = "/app:company_home/st:sites/";

model.tagQuery = semanticTagQuery(args["noderef"], args["max"], args["semanticCat"], args["site"]);

function semanticTagQuery(nodeRef, maxResults, semanticCategory, shareSiteId)
{
   var tags = new Array();
   var countMin = Number.MAX_VALUE;
   var countMax = 0;
   
   /* nodeRef input */
   var node = null;
   if ((nodeRef != null) && (nodeRef != ""))
   {
      node = search.findNode(nodeRef);
   }
   if (node == null)
   {
      node = companyhome;
   }

   /* maxResults input */
   if ((maxResults == null) || (maxResults == ""))
   {
      maxResults = -1;
   }
   
   /* Query for tagged node(s) */

   var query = "";

   // optional constrain to specific share site area
   if (shareSiteId !== null && shareSiteId.length > 0)
   {
      var path = SITES_SPACE_QNAME_PATH;
      if (shareSiteId == "*")
      {
         path += "*/*//*";      
      }
      else
      {
         path += "cm:" + search.ISO9075Encode(shareSiteId) + "/*//*";
      }
      query = 'PATH:"' + path;
   }
   else if (node == companyhome)
   {
      query = "PATH:\"" + node.qnamePath + "//*";
   }
   else
   {
      query = "PATH:\"" + node.qnamePath;
   }

   if ( (semanticCategory != null) && (semanticCategory != "") )
   {
      query += "\" AND PATH:\"/cm:semantictaggable2/cm:" + semanticCategory + "//*\"";
   }
   else
   {
      query += "\" AND PATH:\"/cm:semantictaggable2//*\"";
   }   

   var taggedNodes = search.luceneSearch(query);

   if (taggedNodes.length == 0)
   {
      countMin = 0;
   }
   else
   {   
      /* Build a hashtable of tags and tag count */
      var countHash = {};
      var latHash = {};
      var longHash = {};      
      var count;
      
      for each (taggedNode in taggedNodes)
      {
         for each(tag in taggedNode.properties["{http://www.alfresco.org/model/content/semantic/2.0}taggable"])
         {
            if (tag != null  && (semanticCategory == null || semanticCategory == tag.parent.name ))
            {
				count = countHash[tag.name];
				countHash[tag.name] = count ? count+1 : 1;
				
				var latitude = tag.properties["{http://www.alfresco.org/model/content/semantic/2.0}latitude"];
				var longitude = tag.properties["{http://www.alfresco.org/model/content/semantic/2.0}longitude"];
				latHash[tag.name] = (latitude != null) ? latitude : "";               
				longHash[tag.name] = (longitude != null) ? longitude : "";                
            }
         }
      }
      
      /* Convert the hashtable into an array of objects */
      for (key in countHash)
      {
         tag =
         {
            name: key,
            count: countHash[key],
            latitude: latHash[key],
            longitude: longHash[key],            
            toString: function()
            {
               return this.name;
            }
         };
         tags.push(tag);
      }
   
      /* Sort the results by count (descending) */
      tags.sort(sortByCountDesc);
   
      /* Trim the results to maxResults if specified */
      if (maxResults > -1)
      {
         tags = tags.slice(0, maxResults);
      }
   
      /* Calculate the min and max tag count values */
      for each(tag in tags)
      {
         countMin = Math.min(countMin, tag.count);
         countMax = Math.max(countMax, tag.count);
      }
   
      /* Sort the results by tag name (ascending) */
      tags.sort();
   }
   if(tags.length == 0)
      countMin = 0;
   var results =
   {
      "countMin": countMin,
      "countMax": countMax,
      "tags": tags
   };
   return results;
}

function sortByCountDesc(a, b)
{
   return (b.count - a.count);
}
