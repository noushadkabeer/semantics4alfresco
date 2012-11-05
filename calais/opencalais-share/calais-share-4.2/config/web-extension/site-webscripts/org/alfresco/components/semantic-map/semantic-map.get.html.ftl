<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/semantic-map/semantic-map.css" group="dashlets" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   
   <#-- using regular script not @script to avoid google.com turning into google_.com so should only have one map dashlet per dashboard to avoid multiple includes -->
   <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false" group="dashlets"></script>   
   
   <@script type="text/javascript" src="${url.context}/res/components/semantic-map/semantic-map.js" group="dashlets"></@script>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div class="dashlet semantic-map-dashlet">
         <div class="title" id="${el}-title"><#if args.title?? && args.title != "">${args.title?html}<#else>${msg("semantic-map.defaultTitle")}</#if></div>
         <div class="refresh"><a id="${el}-refresh" href="#">&nbsp;</a></div>   
         <div class="body" <#if args.height??> style="height: ${args.height}px;"</#if>>
            <div id="${el}-map" class="geo-map"></div>
         </div>
      </div>
   </@>
</@>
