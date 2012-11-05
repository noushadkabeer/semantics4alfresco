<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/semantic-tag-cloud/semantic-tag-cloud.css" group="dashlets" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/semantic-tag-cloud/semantic-tag-cloud.js" group="dashlets"></@script>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>


<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="dashlet semantic-tag-cloud">
         <div class="title">${msg("header")}</div>
         <div class="refresh"><a id="${el}-refresh" href="#">&nbsp;</a></div>
         <div class="toolbar flat-button">
            <input id="${el}-all" type="checkbox" name="all" value="${msg("filter.all")}" checked="checked" />
            <input id="${el}-filter" type="button" name="filter" value="${msg("filter.company")}" />
            <select id="${el}-filter-menu">
               <option value="City">${msg("filter.city")}</option>
               <option value="Company">${msg("filter.company")}</option>
               <option value="Organization">${msg("filter.organization")}</option>                
               <option value="Person">${msg("filter.person")}</option>
            </select>
         </div>
         <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
            <div id="${el}-tags"></div>
            <div class="clear"></div>
         </div>
      </div>

   </@>
</@>