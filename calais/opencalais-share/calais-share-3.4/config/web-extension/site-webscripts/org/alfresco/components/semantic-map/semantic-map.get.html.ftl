<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.SemanticMap("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",   
      "componentId": "${instance.object.id}",
      "title": "<#if args.title?exists>${args.title?js_string}</#if>",
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
      
//]]></script>

<div class="dashlet semantic-map-dashlet">
   <div class="title" id="${args.htmlid}-title"><#if args.title?? && args.title != "">${args.title}<#else>${msg("semantic-map.defaultTitle")}</#if></div>
   <div class="refresh"><a id="${args.htmlid}-refresh" href="#">&nbsp;</a></div>   
   <div class="body" <#if args.height??> style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-map" style="width: 100%; height: 100%">
      </div>
   </div>
</div>