package org.alfresco.semantics.calais;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.template.BaseContentNode;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Status;

import com.clearforest.calais.simple.CalaisParser;

/**
 * Java portion of semantic tag suggest web script. 
 * Uses Calais service to provide semantic tag suggestion list.  
 * 
 * @author alexander
 *
 */
public class CalaisWebScript extends DeclarativeWebScript
{
    private ServiceRegistry services;

    private String calaisKey;
    

    /* web script execute implementation
     * 
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptStatus)
     */
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {
        Map<String, Object> model = new HashMap<String, Object>(1, 1.0f);
        try
        {
            String nodeRefString = req.getParameter("noderef");
            try
            {
                String keyString = req.getParameter("key");
                if (keyString != null && keyString.length() > 0)
                {    
                    calaisKey = keyString;
                }
            } 
            catch (Exception e)
            {
                // nothing to do here
            }
        
            NodeRef nodeRef = new NodeRef(nodeRefString);
            NodeService nodeService = services.getNodeService();
            String content;
            String result = "<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://clearforest.com/\"> </string>";
            CalaisParser parser = new CalaisParser(calaisKey, "1000", "false", "", "false");
            ContentData cdata = (ContentData) nodeService.getProperties(nodeRef).get(ContentModel.PROP_CONTENT);
            TemplateNode node = new TemplateNode(nodeRef, services, null);
            if (cdata.getMimetype() == "text/plain")
            {
                content = node.getContent();
            } 
            else
            {
                BaseContentNode.TemplateContentData data = node.new TemplateContentData(cdata, ContentModel.PROP_CONTENT);
                content = data.getContentAsText(100000);
            }
            
            result = parser.getJSON(content);
            HashMap<String, List<JSONObject>> types = CalaisAction.parseJSON(result);
            model.put("types", types);
        
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return model;
    }


    /**
     * @param services
     *            the services to set
     */
    public void setServices(ServiceRegistry services)
    {
        this.services = services;
    }

    /**
     * @return the services
     */
    public ServiceRegistry getServices()
    {
        return services;
    }

    /**
     * Calais key setter
     * 
     * @param calaisKey the calaisKey value to set
     */
    public void setCalaisKey(String calaisKey)
    {
        this.calaisKey = calaisKey;
    }

    /**
     * Calais key getter 
     * 
     * @return the calaisKey value
     */
    public String getCalaisKey()
    {
        return calaisKey;
    }
}
