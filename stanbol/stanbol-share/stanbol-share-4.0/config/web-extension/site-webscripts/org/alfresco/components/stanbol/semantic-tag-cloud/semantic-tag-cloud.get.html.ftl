<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.StanbolSemanticTagCloud("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      maxItems: ${(maxItems?string!"50")},
      activeFilter: "${preferences.SemanticTagCloudFilter!"all"}",
      minFontSize: ${(minFontSize?string!"1.0")},
      maxFontSize: ${(maxFontSize?string!"3.0")},
      fontSizeUnits: "${(fontSizeUnits?string!"em")}"
   }).setMessages(
      ${messages}
   );
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet semantic-tag-cloud">
   <div class="title">${msg("header")}</div>
   <div class="refresh"><a id="${args.htmlid}-refresh" href="#">&nbsp;</a></div>
   <div class="toolbar flat-button">
      <input id="${args.htmlid}-all" type="checkbox" name="all" value="${msg("filter.all")}" checked="checked" />
      <input id="${args.htmlid}-filter" type="button" name="filter" value="${msg("filter.place")}" />
      <select id="${args.htmlid}-filter-menu">
         <option value="Place">${msg("filter.place")}</option>
         <option value="Organisation">${msg("filter.organisation")}</option>                
         <option value="Person">${msg("filter.person")}</option>
      </select>
   </div>
   <div class="body" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-tags"></div>
      <div class="clear"></div>
   </div>
</div>