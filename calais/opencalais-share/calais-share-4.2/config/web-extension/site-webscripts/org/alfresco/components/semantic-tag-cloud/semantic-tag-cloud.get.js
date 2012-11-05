const PREFERENCES_ROOT = "org.alfresco.share.dashlet";

function main()
{
   var s = new XML(config.script);
   model.maxItems = parseInt(s.maxItems, 10);
   model.minFontSize = parseFloat(s.minFontSize, 1.0);
   model.maxFontSize = parseFloat(s.maxFontSize, 3.0);
   model.fontSizeUnits = s.fontSizeUnits.toString();

   var result, preferences = {};
   
   // Request the current user's preferences
   var result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/preferences?pf=" + PREFERENCES_ROOT);
   if (result.status == 200 && result != "{}")
   {
      var prefs = eval('(' + result + ')');
      try
      {
         // Populate the preferences object literal for easy look-up later
         preferences = eval('(prefs.' + PREFERENCES_ROOT + ')');
         if (typeof preferences != "object")
         {
            preferences = {};
         }
      }
      catch (e)
      {
      }
   }

   model.preferences = preferences;
   
   
   // Widget instantiation metadata...
   
     
   // Component definition
   var dashlet = {
      id: "SemanticTagCloud",
      name: "Alfresco.dashlet.SemanticTagCloud",
      assignTo : "dashlet",                   // Need to reference the generated JS object
      options: {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         componentId : instance.object.id,         // Reference to allow saving of component properties
         title: stringUtils.stripUnsafeHTML(args.title || ""),
         maxItems: model.maxItems,
         activeFilter: (preferences.SemanticTagCloudFilter != null) ? preferences.SemanticTagCloudFilter : "all",
         minFontSize: model.minFontSize,
         maxFontSize: model.maxFontSize,
         fontSizeUnits: model.fontSizeUnits         
      }
   };   
   
   
   // Dashlet title bar component actions and resizer   
   
   var actions = [];
   
   var dashletResizer = {
      id : "DashletResizer",
      name : "Alfresco.widget.DashletResizer",
      initArgs : ["\"" + args.htmlid + "\"", "\"" + instance.object.id + "\""],
      useMessages: false
   };

   var dashletTitleBarActions = {
      id : "DashletTitleBarActions",
      name : "Alfresco.widget.DashletTitleBarActions",
      useMessages : false,
      options : {
         actions : actions
      }
   };
   model.widgets = [dashlet, dashletResizer, dashletTitleBarActions];   
   
}

main();