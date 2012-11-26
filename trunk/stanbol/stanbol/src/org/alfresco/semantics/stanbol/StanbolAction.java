package org.alfresco.semantics.stanbol;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.namespace.QName;

import org.apache.stanbol.client.StanbolClient;
import org.apache.stanbol.client.impl.StanbolClientImpl;
import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.model.EntityAnnotation;


/**
 * Provides an Action for semantic auto-tagging with Apache Stanbol.
 * 
 */
@SuppressWarnings("deprecation")
public class StanbolAction extends ActionExecuterAbstractBase
{
    private ServiceRegistry services;

    // aspect on doc
    private final QName ASPECT_STANBOL = QName
            .createQName("{http://www.alfresco.org/model/content/semantic/2.0}taggable");
    
    // properties on doc aspect
    // todo
    
    // root of semantic categories
    private final QName SEMANTIC_ROOT_CATEGORY = QName
            .createQName("{http://www.alfresco.org/model/content/1.0}semantictaggable2");
    
    // aspect on semantic categories
    private final QName ASPECT_SEMANTIC_CATEGORY = QName
    .createQName("{http://www.alfresco.org/model/content/semantic/2.0}category");

    // properties on aspect on categories
    
    private final QName PROP_SEMANTIC_URI = QName
        .createQName("{http://www.alfresco.org/model/content/semantic/2.0}URI");  
    
    
    // URL of Stanbol server
	private String stanbolURL;      

    private QName createResolitionsQName(String localName)
    {
        return QName
            .createQName("{http://www.alfresco.org/model/content/semantic/2.0}" + localName);  
    }
	
	
    /*
     * Action execute implementation
     * 
     * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action,
     *      org.alfresco.service.cmr.repository.NodeRef)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        NodeService nodeService = services.getNodeService();

        try
        {
			StanbolClient client = new StanbolClientImpl(stanbolURL);
	        ContentReader reader = getTextContentReader(actionedUponNodeRef);
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
                    Serializable property = nodeService.getProperty(actionedUponNodeRef, ASPECT_STANBOL);
                    ArrayList<Serializable> newProperty;
                    if (property == null || !(property instanceof ArrayList))
                    {
                        newProperty = new ArrayList<Serializable>();
                    } 
                    else
                    {
                        newProperty = (ArrayList<Serializable>) property;
                    }

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
                    
                    System.out.println("Debug  : " + type + " : " + value);
                    
                    if (type != null && value != null && type.length() > 0 && value.length() > 0)
                    {
                        // Check if name of new category / tag will not be a
                        // violation of naming strategy
                        RegexConstraint constraint = new RegexConstraint();
                        constraint
                                .setExpression("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)");
                        constraint.setRequiresMatch(false);
                        constraint.initialize();
                        try
                        {
                            constraint.evaluate(type);
                            constraint.evaluate(value);
                            NodeRef semanticTag = addSemanticTag(actionedUponNodeRef, type, value);
                            addPropertyWithAspect(semanticTag, nodeService, entityRef, ASPECT_SEMANTIC_CATEGORY, PROP_SEMANTIC_URI);
                            
                            if (annotation.getLatitude() != null)
                            {    
                                addPropertyWithAspect(semanticTag, nodeService, annotation.getLatitude(), 
                                        ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("latitude"));
                            }    
                            if (annotation.getLongitude() != null)
                            {    
                                addPropertyWithAspect(semanticTag, nodeService, annotation.getLongitude(), 
                                        ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("longitude"));
                            }                              
                            
                            if (!newProperty.contains(semanticTag)) //Fix should avoid assigning same categories twice
                            {
                                newProperty.add(semanticTag);
                            }   
                        } 
                        catch (ConstraintException e)
                        {
                            //Nothing to do here - expected outcome
                        } 
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }                            
                    }
                    
                    addPropertyWithAspect(actionedUponNodeRef, nodeService, newProperty, ASPECT_STANBOL, ASPECT_STANBOL);             
    			} 
            }      
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param actionedUponNodeRef
     * @param nodeService
     * @param newProperty
     * @param aspectName
     * @param propertyName
     * @throws InvalidNodeRefException
     * @throws InvalidAspectException
     */
    private void addPropertyWithAspect(NodeRef actionedUponNodeRef, NodeService nodeService,
            Serializable newProperty, QName aspectName, QName propertyName) throws InvalidNodeRefException,
            InvalidAspectException
    {
        if (!nodeService.hasAspect(actionedUponNodeRef, aspectName))
        {
            HashMap<QName, Serializable> props = new HashMap<QName, Serializable>();
            props.put(propertyName, newProperty);
            nodeService.addAspect(actionedUponNodeRef, aspectName, props);
        } 
        else
        {
            nodeService.setProperty(actionedUponNodeRef, propertyName, newProperty);
        }
    }

    /**
     * Services setter
     * 
     * @param services
     */
    public void setServices(ServiceRegistry services)
    {
        this.services = services;
    }

    /**
     * Services getter
     * 
     * @return services
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

    /*
     * Adds parameter definitions
     * 
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
    }   
    
    /**
     * Adds category for semantic type (if need to) and adds category for
     * semantic tag name
     * 
     * @param nodeRef
     * @param categoryName
     * @param tagName
     * @return
     */
    public NodeRef addSemanticTag(final NodeRef nodeRef, final String categoryName, final String tagName)
    {
        CategoryService categoryService = services.getCategoryService();
        
        // get top level category for semantic categoryName, creating if needed by passing true to getRootCategories
        Collection<ChildAssociationRef> assocs = categoryService.getRootCategories(nodeRef.getStoreRef(), SEMANTIC_ROOT_CATEGORY, categoryName, true);
        ChildAssociationRef[] assocsArray = assocs.toArray(new ChildAssociationRef[assocs.size()]);
        NodeRef rootCategoryNodeRef = assocsArray[0].getChildRef();
        
        // Get the tag node (category) reference
        NodeRef newTagNodeRef = services.getNodeService().getChildByName(rootCategoryNodeRef, ContentModel.ASSOC_SUBCATEGORIES, tagName);
        if (newTagNodeRef == null)
        {
            // Create the new tag / category
            newTagNodeRef = categoryService.createCategory(rootCategoryNodeRef, tagName);
        }
        return newTagNodeRef;
    } 

        
    // based on method from zaizi fise integ
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
