
if (args.noderef == "rootCategory")
{
   nodes = classification.getRootCategories("cm:semantictaggable2");
} 
else 
{
   nodes = search.findNode(args.noderef).children;
}
model.nodes = nodes;