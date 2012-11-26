/**
 * DocumentList "Auto Tag with Stanbol" action
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentList
 */
(function()
{
   /**
    * Auto tag a single document.
    *
    * @method onActionStanbolAutoTag
    * @param file {object} Object literal representing one or more file(s) or folder(s) to be actioned
    */
   YAHOO.Bubbling.fire("registerAction",
   {
      actionName: "onActionStanbolAutoTag",
      fn: function stanbol_onActionAutoTag(file)
      {
	      this.modules.actions.genericAction(
	      {
	         success:
	         {
	            message: this.msg("message.autotag.success", file.displayName)
	         },
	         failure:
	         {
	            message: this.msg("message.autotag.failure", file.displayName)
	         },
	         webscript:
	         {
	            name: "/slingshot/doclib/action/stanbol/autotag/site/{site}/{container}",
	            stem: Alfresco.constants.PROXY_URI,
	            method: Alfresco.util.Ajax.POST,
            	params:
		        {
		            site: this.options.siteId,
		            container: this.options.containerId
		        }
	         },
	         config:
	         {
	            requestContentType: Alfresco.util.Ajax.JSON,
	            dataObj:
	            {
	               nodeRefs: [file.nodeRef]
	            }
	         }
	      });
      }
   });
})();
