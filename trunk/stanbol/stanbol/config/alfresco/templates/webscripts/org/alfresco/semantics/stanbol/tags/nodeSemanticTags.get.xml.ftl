<?xml version="1.0" encoding="UTF-8"?>
<nodeSemanticTags>

   <name>${node.name}</name>
   <id>${node.id}</id>
   <noderef>${node.nodeRef}</noderef>

   <tags>

   <#if node.properties["{http://www.alfresco.org/model/content/semantic/2.0}taggable"]?exists>
      <#list node.properties["{http://www.alfresco.org/model/content/semantic/2.0}taggable"] as tag>
      <tag>
         <name>${tag.name}</name>
         <id>${tag.id}</id>
         <noderef>${tag.nodeRef}</noderef>         
      </tag>
      </#list>
   </#if>

   </tags>

</nodeSemanticTags>
