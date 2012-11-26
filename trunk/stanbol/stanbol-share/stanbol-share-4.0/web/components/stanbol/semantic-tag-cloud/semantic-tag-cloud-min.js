(function(){var Dom=YAHOO.util.Dom,Event=YAHOO.util.Event;var $html=Alfresco.util.encodeHTML,$combine=Alfresco.util.combinePaths;var PREFERENCES_DASHLET="org.alfresco.share.dashlet",PREF_SITE_TAGS_FILTER=PREFERENCES_DASHLET+".StanbolSemanticTagCloudFilter";Alfresco.dashlet.StanbolSemanticTagCloud=function StanbolSemanticTagCloud_constructor(htmlId){return Alfresco.dashlet.StanbolSemanticTagCloud.superclass.constructor.call(this,"Alfresco.dashlet.StanbolSemanticTagCloud",htmlId);};YAHOO.extend(Alfresco.dashlet.StanbolSemanticTagCloud,Alfresco.component.Base,{options:{maxItems:50,activeFilter:"all",minFontSize:1,maxFontSize:3,fontSizeUnits:"em"},tagsContainer:null,categoryId:null,onReady:function StanbolSemanticTagCloud_onReady(){var me=this;this.tagsContainer=Dom.get(this.id+"-tags");Event.addListener(this.id+"-refresh","click",this.onRefresh,this,true);this.services.preferences=new Alfresco.service.Preferences();this.widgets.all=new YAHOO.widget.Button(this.id+"-all",{type:"checkbox",value:"all",checked:true});this.widgets.all.on("checkedChange",this.onAllCheckedChanged,this.widgets.all,this);this.widgets.filter=new YAHOO.widget.Button(this.id+"-filter",{type:"split",menu:this.id+"-filter-menu",lazyloadmenu:false});this.widgets.filter.on("click",this.onFilterClicked,this,true);var menu=this.widgets.filter.getMenu();menu.subscribe("click",function(p_sType,p_aArgs){var menuItem=p_aArgs[1];if(menuItem){me.widgets.filter.set("label",menuItem.cfg.getProperty("text"));me.onFilterChanged.call(me,p_aArgs[1]);}});if(this.options.activeFilter=="all"){this.widgets.filter.value="Place";this.setActiveFilter("all");}else{this.widgets.filter.value=this.options.activeFilter;var menuItems=menu.getItems(),menuItem,i,ii;for(i=0,ii=menuItems.length;i<ii;i++){menuItem=menuItems[i];if(menuItem.value==this.options.activeFilter){menu.clickEvent.fire({type:"click"},menuItem);break;}}}},onRefresh:function StanbolSemanticTagCloud_onRefresh(e){if(e){Event.preventDefault(e);}this.refreshTags();},refreshTags:function StanbolSemanticTagCloud_refreshTags(){Dom.setStyle(this.tagsContainer,"display","none");var getTagsUrl=Alfresco.constants.PROXY_URI+"/semantics/stanbol/getSemanticTags?format=json";if(this.categoryId!==""){getTagsUrl+="&semanticCat="+this.categoryId;}if((this.options.siteId!==null)&&(this.options.siteId.length>0)){getTagsUrl+="&site="+this.options.siteId;}else{getTagsUrl+="&site=*";}Alfresco.util.Ajax.jsonGet({url:getTagsUrl,successCallback:{fn:this.onTagsSuccess,scope:this},failureCallback:{fn:this.onTagsFailed,scope:this},scope:this,noReloadOnAuthFailure:true});},onTagsSuccess:function StanbolSemanticTagCloud_onTagsSuccess(p_response){var tags=p_response.json.tags.slice(0,this.options.maxItems),numTags=tags.length,html="",i,ii,minFontSize=this.options.minFontSize,maxFontSize=this.options.maxFontSize,fontSizeUnits=this.options.fontSizeUnits,minTagCount,maxTagCount;if(tags.length===0){html='<div class="msg">'+this.msg("message.no-tags")+"</div>";}else{var tag,fnMaxTagCount=function maxTagCount(){var maxCount=0,count;for(i=0,ii=tags.length;i<ii;i++){if((count=tags[i].count)>maxCount){maxCount=count;}}return maxCount;},fnMinTagCount=function minTagCount(){var minCount=1000000,count;for(i=0,ii=tags.length;i<ii;i++){if((count=tags[i].count)<minCount){minCount=count;}}return minCount;},fnTagWeighting=function tagWeighting(thisTag){return(tag.count-minTagCount)/(maxTagCount-minTagCount);},fnTagFontSize=function tagFontSize(thisTag){return(minFontSize+(maxFontSize-minFontSize)*fnTagWeighting(thisTag)).toFixed(2);},fnSortByTagAlphabetical=function sortByTagAlphabetical(tag1,tag2){if(tag1.name<tag2.name){return -1;}if(tag1.name>tag2.name){return 1;}return 0;};minTagCount=fnMinTagCount(),maxTagCount=fnMaxTagCount();tags.sort(fnSortByTagAlphabetical);for(i=0,ii=tags.length;i<ii;i++){tag=tags[i];html+='<div class="tag"><a href="'+this.getUriTemplate(tag)+'" class="theme-color-1" style="font-size: '+fnTagFontSize(tag)+fontSizeUnits+'">'+$html(tag.name)+"</a></div> ";}}this.tagsContainer.innerHTML=html;Alfresco.util.Anim.fadeIn(this.tagsContainer);},onTagsFailed:function StanbolSemanticTagCloud_onTagsFailed(){this.tagsContainer.innerHTML=this.msg("refresh-failed");Alfresco.util.Anim.fadeIn(this.tagsContainer);},getUriTemplate:function StanbolSemanticTagCloud_getUriTemplate(tag){var uri=Alfresco.constants.URL_PAGECONTEXT+"stanbol-semantic-search?semanticTag=";uri+=encodeURIComponent(tag.name);switch(this.options.activeFilter){case"Place":uri+="&amp;semanticCategory=Place";break;case"Organisation":uri+="&amp;semanticCategory=Organisation";break;case"Person":uri+="&amp;semanticCategory=Person";break;default:}if((this.options.siteId!==null)&&(this.options.siteId.length>0)){uri+="&site="+this.options.siteId;}return uri;},updateFilterUI:function StanbolSemanticTagCloud_updateFilterUI(){switch(this.options.activeFilter){case"all":Dom.removeClass(this.widgets.filter.get("element"),"yui-checkbox-button-checked");break;default:this.widgets.all.set("checked",false,true);Dom.addClass(this.widgets.filter.get("element"),"yui-checkbox-button-checked");break;}},setActiveFilter:function StanbolSemanticTagCloud_setActiveFilter(filter,noPersist){this.options.activeFilter=filter;this.categoryId=filter!=="all"?filter:"";this.updateFilterUI();this.refreshTags();if(noPersist!==true){this.services.preferences.set(PREF_SITE_TAGS_FILTER,filter);}},onAllCheckedChanged:function StanbolSemanticTagCloud_onAllCheckedChanged(p_oEvent,p_obj){this.setActiveFilter("all");p_obj.set("checked",true,true);},onFilterClicked:function StanbolSemanticTagCloud_onFilterClicked(p_oEvent){this.setActiveFilter(this.widgets.filter.value);},onFilterChanged:function StanbolSemanticTagCloud_onFilterChanged(p_oMenuItem){var filter=p_oMenuItem.value;this.widgets.filter.value=filter;this.setActiveFilter(filter);}});})();