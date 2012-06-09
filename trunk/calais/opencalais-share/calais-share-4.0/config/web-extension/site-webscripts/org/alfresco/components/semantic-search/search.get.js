/**
 * Search component GET method
 */

function main()
{
   // fetch the request params required by the search component template
   var siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
   var siteTitle = null;
   if (siteId.length != 0)
   {
      // look for request scoped cached site title
      siteTitle = context.properties["site-title"];
      if (siteTitle == null)
      {
         // Call the repository for the site profile
         var json = remote.call("/api/sites/" + siteId);
         if (json.status == 200)
         {
            // Create javascript objects from the repo response
            var obj = eval('(' + json + ')');
            if (obj)
            {
               siteTitle = (obj.title.length != 0) ? obj.title : obj.shortName;
            }
         }
      }
   }
   
   // get the search sorting fields from the config
   var sortables = config.scoped["Search"]["sorting"].childrenMap["sort"];
   var sortFields = [];
   for (var i = 0, sort, label; i < sortables.size(); i++)
   {
      sort = sortables.get(i);
      
      // resolve label text
      label = sort.attributes["label"];
      if (label == null)
      {
         label = sort.attributes["labelId"];
         if (label != null)
         {
            label = msg.get(label);
         }
      }
      
      // create the model object to represent the sort field definition
      sortFields.push(
      {
         type: sort.value,
         label: label ? label : sort.value
      });
   }
   
   // Prepare the model
   model.siteId = siteId;
   model.siteTitle = (siteTitle != null ? siteTitle : "");
   model.sortFields = sortFields;
   model.searchTerm = (page.url.args["t"] != null) ? page.url.args["t"] : "";
   model.searchTag = (page.url.args["tag"] != null) ? page.url.args["tag"] : "";
   model.searchSort = (page.url.args["s"] != null) ? page.url.args["s"] : "";
   model.searchAll = (page.url.args["a"] == "true");
   // Advanced search forms based json query
   model.searchQuery = (page.url.args["q"] != null) ? page.url.args["q"] : "";
   // semantic search
   model.searchSemanticTag = (page.url.args["semanticTag"] != null) ? page.url.args["semanticTag"] : "";   
   model.searchSemanticCategory = (page.url.args["semanticCategory"] != null) ? page.url.args["semanticCategory"] : "";   
}

main();