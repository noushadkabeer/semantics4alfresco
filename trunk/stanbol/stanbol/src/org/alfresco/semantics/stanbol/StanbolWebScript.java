package org.alfresco.semantics.stanbol;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.repo.template.BaseContentNode;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.stanbol.client.StanbolClient;
import org.apache.stanbol.client.impl.StanbolClientImpl;
import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.model.EntityAnnotation;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Status;


/**
 * Java portion of semantic tag suggest web script. 
 * Uses Apache Stanbol to provide semantic tag suggestion list.  
 * 
 */
public class StanbolWebScript extends DeclarativeWebScript
{
    private ServiceRegistry services;
    
    // URL of Stanbol server
	private String stanbolURL;      

	
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
        
            NodeRef nodeRef = new NodeRef(nodeRefString);
            NodeService nodeService = services.getNodeService();
			StanbolClient client = new StanbolClientImpl(stanbolURL);
	        ContentReader reader = getTextContentReader(nodeRef);
    		InputStream is = reader.getContentInputStream();
			
    		List<Enhancement> enhancements = client.enhance(is);

            for(Enhancement enhancement : enhancements)
            {
            	String typeUrl = "";
            	String type = "";
            	String entityRef = "";
            	String value = "";
            	
    			if (enhancement instanceof EntityAnnotation)
    			{
    				EntityAnnotation annotation = (EntityAnnotation)enhancement;
    				System.out.println("Entity label: " + annotation.getEntityLabel());
    				System.out.println("Entity type: " + annotation.getEntityType());
    				System.out.println("Entity reference: " + annotation.getEntityReference());
    				
    				value = annotation.getEntityLabel();
    				typeUrl = annotation.getBasicEntityType();
    				entityRef = annotation.getEntityReference();
            	
                    if (typeUrl.startsWith("http://dbpedia.org/ontology/"))
                    {
                    	type = typeUrl.substring(28);
                    }
    			} 
            }      

        	// TODO: finish porting suggest tags webscript from calais to stanbol
            
            // todo HashMap<String, List<JSONObject>> types = CalaisAction.parseJSON(result);
            // todo model.put("types", types);
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
     * Stanbol server URL setter
     * 
     * @param stanbolURL the stanbolURL value to set
     */
    public void setStanbolURL(String stanbolURL)
    {
        this.stanbolURL = stanbolURL;
    }

    /**
     * Stanbol server URL getter 
     * 
     * @return the stanbolURL value
     */
    public String getStanbolURL()
    {
        return stanbolURL;
    }     
    
        
    // based on method from zaizi fise integ
    // todo consolidate with method on StanbolAction
    private ContentReader getTextContentReader(NodeRef nodeRef)
    {
    	ContentService contentService = getServices().getContentService();
    	
        ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        
        if (MimetypeMap.MIMETYPE_TEXT_PLAIN.equals(reader.getMimetype()))
        {
            return reader;
        }
        else
        {
            ContentWriter writer = contentService.getWriter(null, ContentModel.PROP_CONTENT, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN); 
            writer.setEncoding(reader.getEncoding());
            
            // try and transform the content
            if (contentService.isTransformable(reader, writer))
            {
                contentService.transform(reader, writer);
                
                ContentReader resultReader = writer.getReader();
                return resultReader;
            }
            return null;
        }
    }      

}
