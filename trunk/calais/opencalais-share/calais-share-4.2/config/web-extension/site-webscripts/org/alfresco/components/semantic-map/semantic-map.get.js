
function main()
{
   // Widget instantiation metadata...
   
     
   // Component definition
   var dashlet = {
      id: "SemanticMap",
      name: "Alfresco.dashlet.SemanticMap",
      assignTo : "dashlet",                   // Need to reference the generated JS object
      options: {
         siteId : (page.url.templateArgs.site != null) ? page.url.templateArgs.site : "",
         componentId : instance.object.id,         // Reference to allow saving of component properties
         title: stringUtils.stripUnsafeHTML(args.title || "")
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

