 
/**
 * Stanbol Semantic Tag Cloud component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.StanbolSemanticTagCloud
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * Preferences
    */
   var PREFERENCES_DASHLET = "org.alfresco.share.dashlet",
      PREF_SITE_TAGS_FILTER = PREFERENCES_DASHLET + ".StanbolSemanticTagCloudFilter";


   /**
    * Dashboard StanbolSemanticTagCloud constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.StanbolSemanticTagCloud} The new component instance
    * @constructor
    */
   Alfresco.dashlet.StanbolSemanticTagCloud = function StanbolSemanticTagCloud_constructor(htmlId)
   {
      return Alfresco.dashlet.StanbolSemanticTagCloud.superclass.constructor.call(this, "Alfresco.dashlet.StanbolSemanticTagCloud", htmlId);
   };

   /**
    * Extend from Alfresco.component.Base and add class implementation
    */
   YAHOO.extend(Alfresco.dashlet.StanbolSemanticTagCloud, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Max items
          * 
          * @property maxItems
          * @type integer
          * @default 50
          */
         maxItems: 50,

         /**
          * Currently active filter.
          * 
          * @property activeFilter
          * @type string
          * @default "all"
          */
         activeFilter: "all",

         /**
          * Minimum tag font size.
          * 
          * @property minFontSize
          * @type number
          * @default 1.0
          */
         minFontSize: 1.0,

         /**
          * Maximum tag font size.
          * 
          * @property maxFontSize
          * @type number
          * @default 3.0
          */
         maxFontSize: 3.0,

         /**
          * Font size units
          * 
          * @property fontSizeUnits
          * @type string
          * @default "em"
          */
         fontSizeUnits: "em"
      },

      /**
       * Tags DOM container.
       * 
       * @property tagsContainer
       * @type object
       */
      tagsContainer: null,

      /**
       * ContainerId for tag scope query
       *
       * @property containerId
       * @type string
       * @default ""
       */
      categoryId: null,

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function StanbolSemanticTagCloud_onReady()
      {
         var me = this;
         
         // The tags container
         this.tagsContainer = Dom.get(this.id + "-tags");
         
         // Hook the refresh icon click
         Event.addListener(this.id + "-refresh", "click", this.onRefresh, this, true);

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // "All" filter
         this.widgets.all = new YAHOO.widget.Button(this.id + "-all",
         {
            type: "checkbox",
            value: "all",
            checked: true
         });
         this.widgets.all.on("checkedChange", this.onAllCheckedChanged, this.widgets.all, this);

         // Dropdown filter
         this.widgets.filter = new YAHOO.widget.Button(this.id + "-filter",
         {
            type: "split",
            menu: this.id + "-filter-menu",
            lazyloadmenu: false
         });
         this.widgets.filter.on("click", this.onFilterClicked, this, true);
         // Clear the lazyLoad flag and fire init event to get menu rendered into the DOM
         var menu = this.widgets.filter.getMenu();
         menu.subscribe("click", function (p_sType, p_aArgs)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.widgets.filter.set("label", menuItem.cfg.getProperty("text"));
               me.onFilterChanged.call(me, p_aArgs[1]);
            }
         });
         
         if (this.options.activeFilter == "all")
         {
            this.widgets.filter.value = "Place";
            this.setActiveFilter("all");
         }
         else
         {
            this.widgets.filter.value = this.options.activeFilter;

            // Loop through and find the menuItem corresponding to the default filter
            var menuItems = menu.getItems(),
               menuItem,
               i, ii;

            for (i = 0, ii = menuItems.length; i < ii; i++)
            {
               menuItem = menuItems[i];
               if (menuItem.value == this.options.activeFilter)
               {
                  menu.clickEvent.fire(
                  {
                     type: "click"
                  }, menuItem);
                  break;
               }
            }
         }
      },
      
      /**
       * Event handler for refresh click
       * @method onRefresh
       * @param e {object} Event
       */
      onRefresh: function StanbolSemanticTagCloud_onRefresh(e)
      {
         if (e)
         {
            // Stop browser's default click behaviour for the link
            Event.preventDefault(e);
         }
         this.refreshTags();
      },
      
      /**
       * Refresh tags
       * @method refreshTags
       */
      refreshTags: function StanbolSemanticTagCloud_refreshTags()
      {
         // Hide the existing content
         Dom.setStyle(this.tagsContainer, "display", "none");
         
         var getTagsUrl = Alfresco.constants.PROXY_URI + "/semantics/stanbol/getSemanticTags?format=json";
         
         if (this.categoryId !== "")
         {
            getTagsUrl += "&semanticCat=" + this.categoryId;
         }
         
         if ((this.options.siteId !== null) && (this.options.siteId.length > 0))
         {
            getTagsUrl += "&site=" + this.options.siteId;
         }
         else
         {
            getTagsUrl += "&site=*";
         }         
         
         // Make an AJAX request to the Tag Service REST API
         Alfresco.util.Ajax.jsonGet(
         {
            url: getTagsUrl,
            successCallback:
            {
               fn: this.onTagsSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onTagsFailed,
               scope: this
            },
            scope: this,
            noReloadOnAuthFailure: true
         });
      },

      /**
       * Tags retrieved successfully
       * @method onTagsSuccess
       * @param p_response {object} Response object from request
       */
      onTagsSuccess: function StanbolSemanticTagCloud_onTagsSuccess(p_response)
      {
          // Retrieve the tags list from the JSON response and trim accordingly
          var tags = p_response.json.tags.slice(0, this.options.maxItems),
             numTags = tags.length,
             html = "",
             i, ii,
             minFontSize = this.options.minFontSize,
             maxFontSize = this.options.maxFontSize,
             fontSizeUnits = this.options.fontSizeUnits,
             minTagCount, maxTagCount;

         // Tags to show?
         if (tags.length === 0)
         {
            html = '<div class="msg">' + this.msg("message.no-tags") + '</div>';
         }
         else
         {
            // Define inline scaling functions
            var tag,
	            fnMaxTagCount = function maxTagCount()
	            {
	         	   var maxCount = 0, count;
	         	   for (i = 0, ii = tags.length; i < ii; i++)
	                {
	         		   if ((count = tags[i].count) > maxCount)
	         			   maxCount = count;
	                }
	         	   return maxCount;
	            },
	            fnMinTagCount = function minTagCount()
	            {
	         	   var minCount = 1000000, count;
	         	   for (i = 0, ii = tags.length; i < ii; i++)
	                {
	         		   if ((count = tags[i].count) < minCount)
	         			   minCount = count;
	                }
	         	   return minCount;
	            },
               fnTagWeighting = function tagWeighting(thisTag)
               {
	              // should return a number between 0.0 (for smallest) and 1.0 (for largest)
	              return (tag.count - minTagCount) / (maxTagCount - minTagCount);
               },
               fnTagFontSize = function tagFontSize(thisTag)
               {
                  return (minFontSize + 
                		  (maxFontSize - minFontSize) * fnTagWeighting(thisTag)).toFixed(2);
               },
               fnSortByTagAlphabetical = function sortByTagAlphabetical(tag1, tag2)
               {
                  if (tag1.name < tag2.name)
                     return -1;
                  
                  if (tag1.name > tag2.name)
                     return 1;
                  
                  return 0;
               };
            
            // Initialise min and max tag counts
            minTagCount = fnMinTagCount(), maxTagCount = fnMaxTagCount();
            
            // Sort tags alphabetically - standard for tag clouds
            tags.sort(fnSortByTagAlphabetical);

            // Generate HTML mark-up for each tag
            for (i = 0, ii = tags.length; i < ii; i++)
            {
               tag = tags[i];
               html += '<div class="tag"><a href="' + this.getUriTemplate(tag) + '" class="theme-color-1" style="font-size: ' + fnTagFontSize(tag) + fontSizeUnits + '">' + $html(tag.name) + '</a></div> ';
            }
         }
         this.tagsContainer.innerHTML = html;
         
         // Fade the new content in
         Alfresco.util.Anim.fadeIn(this.tagsContainer);
      },

      /**
       * Tags request failed
       * @method onTagsFailed
       */
      onTagsFailed: function StanbolSemanticTagCloud_onTagsFailed()
      {
         this.tagsContainer.innerHTML = this.msg("refresh-failed");
         Alfresco.util.Anim.fadeIn(this.tagsContainer);
      },
      
      /**
       * Generate Uri template based on current active filter
       * @method getUriTemplate
       * @param tag {object} Tag object literal
       */
      getUriTemplate: function StanbolSemanticTagCloud_getUriTemplate(tag)
      {
         var uri = Alfresco.constants.URL_PAGECONTEXT + 'stanbol-semantic-search?semanticTag=';
         
         uri += encodeURIComponent(tag.name);
                 
         switch (this.options.activeFilter)
         {
            case "Place":
               uri += '&amp;semanticCategory=Place';
               break;

            case "Organisation":
               uri += '&amp;semanticCategory=Organisation';
               break;

            case "Person":
               uri += '&amp;semanticCategory=Person';
               break;
            
            default:
               // all semantic categories
         }                           
       
         if ((this.options.siteId !== null) && (this.options.siteId.length > 0))
         {
            uri += "&site=" + this.options.siteId;
         }
                          
         return uri;
      },

      /**
       * Sets the active filter highlight in the UI
       * @method updateFilterUI
       */
      updateFilterUI: function StanbolSemanticTagCloud_updateFilterUI()
      {
         switch (this.options.activeFilter)
         {
            case "all":
               Dom.removeClass(this.widgets.filter.get("element"), "yui-checkbox-button-checked");
               break;

            default:
               this.widgets.all.set("checked", false, true);
               Dom.addClass(this.widgets.filter.get("element"), "yui-checkbox-button-checked");
               break;
         }
      },

      /**
       * Sets active filter
       * @method saveActiveFilter
       * @param filter {string} New filter to set
       * @param noPersist {boolean} [Optional] If set, preferences are not updated
       */
      setActiveFilter: function StanbolSemanticTagCloud_setActiveFilter(filter, noPersist)
      {
         this.options.activeFilter = filter;
         this.categoryId = filter !== "all" ? filter : "";
         this.updateFilterUI();
         this.refreshTags();
         if (noPersist !== true)
         {
            this.services.preferences.set(PREF_SITE_TAGS_FILTER, filter);
         }
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * All tasks
       * @method onAllCheckedChanged
       * @param p_oEvent {object} Button event
       * @param p_obj {object} Button
       */
      onAllCheckedChanged: function StanbolSemanticTagCloud_onAllCheckedChanged(p_oEvent, p_obj)
      {
         this.setActiveFilter("all");
         p_obj.set("checked", true, true);
      },

      /**
       * Filter button clicked event handler
       * @method onFilterClicked
       * @param p_oEvent {object} Dom event
       */
      onFilterClicked: function StanbolSemanticTagCloud_onFilterClicked(p_oEvent)
      {
         this.setActiveFilter(this.widgets.filter.value);
      },
      
      /**
       * Filter drop-down changed event handler
       * @method onFilterChanged
       * @param p_oMenuItem {object} Selected menu item
       */
      onFilterChanged: function StanbolSemanticTagCloud_onFilterChanged(p_oMenuItem)
      {
         var filter = p_oMenuItem.value;
         this.widgets.filter.value = filter;
         this.setActiveFilter(filter);
      }
   });
})();
