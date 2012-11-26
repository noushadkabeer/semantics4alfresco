
model.categoryActions = addNewSemanticTag(args.tagname, args.semanticCat, args.noderef, args.normalized, args.latitude, args.longitude, args.uri, args.website, args.ticker);

function addNewSemanticTag(tagName, semanticCategoryName, nodeRef, normalizedName, latitude, longitude, linkedDataURI, website, stockTicker)
{
   var resultString = "Action failed";
   var resultCode = false;
   var node = null;   
   
   var semanticCatNode = null;
   var catNodes = classification.getRootCategories("cm:semantictaggable2");
   for each (category in catNodes)
   {
      if (category != null)
      {
         if (category.name == semanticCategoryName)
         {
            semanticCatNode = category;
            break;
         }
      }
   } 
   
   if (semanticCatNode == null)
   {
      // need to add semantic tag category
      semanticCatNode = classification.createRootCategory("cm:semantictaggable2", semanticCategoryName);
   }
   
   
   if (semanticCatNode != null)
   {
      try
      {
         // check if semantic tag category already has sub category with the tag name
         var subCatNodes = semanticCatNode.subCategories;
         for each (subcategory in subCatNodes)
         {
            if (subcategory != null)
            {
               if (subcategory.name == tagName)
               {
                  tagNode = subcategory;
                  break;
               }
            }
         } 

         if (tagNode == null)
         {
            // need to add semantic tag as subcategory
            var tagNode = semanticCatNode.createSubCategory(tagName);
            resultString = "new semantic tag added";
         }
         else
         {
            resultString = "semantic tag existed already";         
         }
                       
         if (tagNode != null)
         {
            if (latitude != null)
            {
               tagNode.properties["{http://www.alfresco.org/model/content/semantic/2.0}latitude"] = latitude;
            }
            if (longitude != null)
            {
               tagNode.properties["{http://www.alfresco.org/model/content/semantic/2.0}longitude"] = longitude;
            }
            if (linkedDataURI != null)
            {
               tagNode.properties["{http://www.alfresco.org/model/content/semantic/2.0}URI"] = linkedDataURI;
            }
            if (website != null)
            {
               tagNode.properties["{http://www.alfresco.org/model/content/semantic/2.0}webaddress"] = website;
            }

            tagNode.save();

            // optionally add new tag to a node
            if (nodeRef != undefined)
            {
               var node = search.findNode(nodeRef);

               if (node != null)
               {
                  var categories;
                  categories = node.properties["{http://www.alfresco.org/model/content/semantic/2.0}taggable"];
                  if (categories == null)
                  {
                     categories = new Array();
                  }
                  // Check if node doesn't already have this semantic tag
                  var hasTag = false;
                  for each (category in categories)
                  {
                     if (category != null)
                     {
                        if (category.name == tagNode.name)
                        {
                           hasTag = true;
                           break;
                        }
                     }
                  }
                  if (hasTag == false)
                  {
                     // Add semantic tag to node
                     categories.push(tagNode);
                     var categoriesArray = new Array();
                     categoriesArray["{http://www.alfresco.org/model/content/semantic/2.0}taggable"] = categories;
                     node.addAspect("{http://www.alfresco.org/model/content/semantic/2.0}taggable", categoriesArray);
                     resultString = resultString + ", also tag added to node";
                     resultCode = true;
                  }
                  else
                  {
                     resultString = resultString + ", node already had tag";
                     resultCode = true;
                  }
               }   
            }
            else
            {
               resultCode = true;
            }
         }   
      }
      catch(e)
      {
         resultString = "Action failed due to exception [" + e.toString() + "]";
      }
   }

   var result =
   {
      "resultString": resultString,
      "resultCode": resultCode,
   };
   return result;
}

