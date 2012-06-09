{
   "countMin" : ${tagQuery.countMin},
   "countMax" : ${tagQuery.countMax},

   "tags" : [
   
      <#import "tagging.lib.ftl" as taggingLib/>   
   	
   	<#list tagQuery.tags as item>
           <@taggingLib.tagJSON item=item />
           <#if item_has_next>,</#if>
        </#list>
   ]
}