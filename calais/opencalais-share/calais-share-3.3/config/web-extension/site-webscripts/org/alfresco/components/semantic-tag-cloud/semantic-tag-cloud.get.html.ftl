<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.SemanticTagCloud("${args.htmlid}").setOptions(
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
      <input id="${args.htmlid}-filter" type="button" name="filter" value="${msg("filter.company")}" />
      <select id="${args.htmlid}-filter-menu">
         <option value="City">${msg("filter.city")}</option>
         <option value="Company">${msg("filter.company")}</option>
         <option value="Organization">${msg("filter.organization")}</option>                
         <option value="Person">${msg("filter.person")}</option>
      </select>
   </div>
   <div class="body" <#if args.height??>style="height: ${args.height}px;"</#if>>
      <div id="${args.htmlid}-tags"></div>
      <div class="clear"></div>
   </div>
</div>