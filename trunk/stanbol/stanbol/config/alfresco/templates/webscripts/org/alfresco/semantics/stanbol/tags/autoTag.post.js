
var nodeRef = args.noderef;
var calaisKey = args.key;
var autoTag = args.autoTag;
var saveRDF = args.saveRDF;
var saveJSON = args.saveJSON;

var node = search.findNode(nodeRef);

if ( (node != null) && (calaisKey != null) )
{
   var action = actions.create("stanbolAction");
  
   action.execute(node);
   
   model.node = node;    

}