<?xml version="1.0" encoding="UTF-8"?>
<semanticTags>

   <countMin>${tagQuery.countMin}</countMin>
   <countMax>${tagQuery.countMax}</countMax>

   <tags>
   <#list tagQuery.tags as tag>
      <tag>
         <name>${tag.name}</name>
         <count>${tag.count}</count>
         <latitude>${tag.latitude}</latitude>
         <longitude>${tag.longitude}</longitude>
      </tag>
   </#list>
   </tags>

</semanticTags>