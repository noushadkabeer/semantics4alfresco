/**
 * Semantic map component.
 * 
 * @namespace Alfresco
 * @class Alfresco.dashlet.SemanticMap
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
    * SemanticMap constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.SemanticMap} The new component instance
    * @constructor
    */
   Alfresco.dashlet.SemanticMap = function SemanticMap_constructor(htmlId)
   {
      return Alfresco.dashlet.SemanticMap.superclass.constructor.call(this, "Alfresco.dashlet.SemanticMap", htmlId);
   };

   /**
    * Extend from Alfresco.component.Base and add class implementation
    */
   YAHOO.extend(Alfresco.dashlet.SemanticMap, Alfresco.component.Base,
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
          * The component id.
          *
          * @property componentId
          * @type string
          */
         componentId: "",

         /**
          * Title to display
          * 
          * @property title
          * @type string
          * @default ""
          */
         title: "",
      },
      
      /* Frequently-used DOM Containers - prevents these having to be reloaded each time 
       * they are used */

      /**
       * title DOM container.
       * 
       * @property title
       * @type object
       */
      titleContainer: null,
      
      mapContainer: null,
      
      map: null,


      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function SemanticMap_onReady()
      {                  
         this.titleContainer = Dom.get(this.id + "-title");
         this.mapContainer = Dom.get(this.id + "-map");

         // Hookup the refresh icon click event handler
         Event.addListener(this.id + "-refresh", "click", this.onRefresh, this, true);
         
         // Display the title on the dashlet
         this.refreshTitle();

         // initialize google map
         this.refreshMap();         
      },
      
      refreshMap: function SemanticMap_refreshMap()
      {
         this.initializeMap();
         this.refreshTags();
      },

      /**
       * Refresh dashlet title
       * @method refreshTitle
       */
      refreshTitle: function SemanticMap_refreshTitle()
      {
         if (this.options.title == "")
         { 
            this.options.title = this.msg("semantic-map.defaultTitle");
         }
         this.titleContainer.innerHTML = this.options.title;
      },
      
      
      initializeMap: function SemanticMap_initializeMap()
      {
         var myLatlng = new google.maps.LatLng(40.749444, -73.968056);
         var myOptions = 
         {
            zoom: 3,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP
         }
         map = new google.maps.Map(this.mapContainer, myOptions);
      },   
      
      
      /**
       * Refresh tags
       * @method refreshTags
       */
      refreshTags: function SemanticMap_refreshTags()
      {
         var getTagsUrl = Alfresco.constants.PROXY_URI + "/semantics/getSemanticTags?format=json";

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
      onTagsSuccess: function SemanticMap_onTagsSuccess(p_response)
      {
         // Retrieve the tags list from the JSON response and trim max markers to display
         var maxItems = 50;
         var tags = p_response.json.tags.slice(0, maxItems);
         var i;
         var tagsLength = tags.length;
         var latLong;

         for (i = 0; i < tagsLength; i++)
         {
            var tag = tags[i];

            if ((tag.latitude !== "") && (tag.longitude !== ""))
            {
               var lat = new Number(tag.latitude);
               var lng = new Number(tag.longitude);
               latLong = new google.maps.LatLng(lat, lng);
               
               this.createMarker(map, latLong, tag.name);
            }
         }         
      },
      
      /**
       * Tags request failed
       * @method onTagsFailed
       */
      onTagsFailed: function SemanticMap_onTagsFailed()
      {
      },

      createMarker: function SemanticMap_createMarker(map, latLong, tagName)
      {
         var marker = new google.maps.Marker(
         {
            position: latLong, 
            map: map,
            title: tagName
         });                                                

         var uri = this.getSearchUri(tagName);

         google.maps.event.addListener(marker, 'click', function() {
             window.location = uri;
         });      
      },

      /**
       * Generate Uri template based on the tag
       * @method getUriTemplate
       * @param tagname semantic tag name
       */
      getSearchUri: function SemanticMap_getSearchUri(tagname)
      {
         var uri = Alfresco.constants.URL_PAGECONTEXT + 'semantic-search?semanticTag=';
                  
         uri += encodeURIComponent(tagname);
         
         if ((this.options.siteId !== null) && (this.options.siteId.length > 0))
         {
            uri += "&site=" + this.options.siteId;
         }
                  
         return uri;
      },
      
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
             
      /**
       * Event handler for refresh button click
       * @method onRefresh
       * @param e {object} Event
       */
      onRefresh: function SemanticTagCloud_onRefresh(e)
      {
         if (e)
         {
            // Stop browser's default click behaviour for the link
            Event.preventDefault(e);
         }
         this.refreshMap();
      },             
      
   });
})();
