/**
 * DocumentList "Auto Tag with Calais" action
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentList
 */
(function()
{
   /**
    * Auto tag a single document.
    *
    * @method onActionAutoTag
    * @param file {object} Object literal representing one or more file(s) or folder(s) to be actioned
    */
   Alfresco.doclib.Actions.prototype.onActionAutoTag = function DL_onActionAutoTag(file)
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
            name: "autotag/site/{site}/{container}",
            method: Alfresco.util.Ajax.POST
         },
         params:
         {
            site: this.options.siteId,
            container: this.options.containerId
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
   };
})();
