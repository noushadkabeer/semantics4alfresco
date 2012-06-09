
var nodeRef = args.noderef;
var calaisKey = args.key;
var autoTag = args.autoTag;
var saveRDF = args.saveRDF;
var saveJSON = args.saveJSON;

var node = search.findNode(nodeRef);

if ( (node != null) && (calaisKey != null) )
{
   var action = actions.create("calaisAction");

   action.parameters["calaisKey"] = calaisKey;

   if (autoTag == "false")
   {
      action.parameters["autoTag"] = false;
   }  
   else
   {
      action.parameters["autoTag"] = true;
   }   
   
   if (saveRDF == "true")
   {
      action.parameters["saveRDF"] = true;
   }  
   else
   {
      action.parameters["saveRDF"] = false;
   }   

   if (saveJSON == "true")
   {
      action.parameters["saveJSON"] = true;
   }  
   else
   {
      action.parameters["saveJSON"] = false;
   }   
   
   action.execute(node);
   
   model.node = node;    
   model.calaisKey = action.parameters["calaisKey"];
   model.autoTag = action.parameters["autoTag"];
   model.saveRDF = action.parameters["saveRDF"]
   model.saveJSON = action.parameters["saveJSON"];
   model.saveSimple = action.parameters["saveSimple"];
}