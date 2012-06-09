package org.alfresco.semantics.calais;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.constraint.RegexConstraint;
import org.alfresco.repo.template.BaseContentNode;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO9075;

import com.clearforest.calais.simple.CalaisParser;

/**
 * Provides an Action for semantic auto-tagging with the Open Calais service.
 * Also can store extracted metadata as rdf and/or json metadata content.
 * 
 * @author alexander
 * 
 */
public class CalaisAction extends ActionExecuterAbstractBase
{
    private ServiceRegistry services;

    private final QName ASPECT_CALAIS = QName
            .createQName("{http://www.alfresco.org/model/content/semantic/1.0}taggable");

    private final QName ASPECT_CALAIS_RDF = QName.createQName("{http://www.alfresco.org/model/content/semantic/1.0}CalaisRDF");

    private final QName PROP_RDF_NAME = QName
            .createQName("{http://www.alfresco.org/model/content/semantic/1.0}CalaisRDFContent");

    private final QName ASPECT_CALAIS_JSON = QName
            .createQName("{http://www.alfresco.org/model/content/semantic/1.0}CalaisJSON");

    private final QName PROP_CALAIS_JSON = QName
            .createQName("{http://www.alfresco.org/model/content/semantic/1.0}CalaisJSONContent");

    private final QName SEMANTIC_ROOT_CATEGORY = QName
            .createQName("{http://www.alfresco.org/model/content/1.0}semantictaggable");
    
    private final QName ASPECT_SEMANTIC_CATEGORY = QName
    .createQName("{http://www.alfresco.org/model/content/semantic/1.0}category");

    private final QName PROP_CALAIS_URI = QName
        .createQName("{http://www.alfresco.org/model/content/semantic/1.0}URI");  
     
    private Boolean useNormalized = true;
 
    private String calaisKey;
    
    
    private QName createResolitionsQName(String localName)
    {
        return QName
            .createQName("{http://www.alfresco.org/model/content/semantic/1.0}" + localName);  
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
        // calaisKey action parm overrides key in module context config
        String keyString = (String) action.getParameterValue(CalaisActionHandler.PROP_CALAIS_KEY);
        if (keyString != null && keyString.length() > 0)
        {    
            calaisKey = keyString;
        }        
        
        Boolean isSaveRDF = (Boolean) action.getParameterValue(CalaisActionHandler.PROP_SAVE_RDF);
        Boolean isSaveJSON = (Boolean) action.getParameterValue(CalaisActionHandler.PROP_SAVE_JSON);
        Boolean isAutoTag = (Boolean) action.getParameterValue(CalaisActionHandler.PROP_AUTO_TAG);
        String content;
        NodeService nodeService = services.getNodeService();
        ContentData cdata = (ContentData) nodeService.getProperties(actionedUponNodeRef).get(ContentModel.PROP_CONTENT);
        TemplateNode node = new TemplateNode(actionedUponNodeRef, services, null);
        if (cdata.getMimetype() == "text/plain")
        {
            content = node.getContent();
        } 
        else
        {
            BaseContentNode.TemplateContentData data = node.new TemplateContentData(cdata, ContentModel.PROP_CONTENT);
            content = data.getContentAsText(100000);
        }
        String json = null;
        try
        {
            if (isSaveJSON)
            {
                json = getCalaisResult(actionedUponNodeRef, calaisKey, false, nodeService, content, true);
            }
            if (isSaveRDF)
            {
                getCalaisResult(actionedUponNodeRef, calaisKey, true, nodeService, content, true);
            }
            if (isAutoTag)
            {
                // System.out.println("start auto tag");
                if (json == null && nodeService.hasAspect(actionedUponNodeRef, ASPECT_CALAIS_JSON))
                {
                    try
                    {
                        ContentService contentService = services.getContentService();
                        ContentReader reader = contentService.getReader(actionedUponNodeRef, ASPECT_CALAIS_JSON);
                        reader.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                        reader.setEncoding("UTF-8");
                        json = reader.getContentString();
                    } 
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                Map<String, List<JSONObject>> topicsMap = null;
                try{
                    if (json == null)
                    {
                        json = getCalaisResult(actionedUponNodeRef, calaisKey, false, nodeService, content, false);
                    } 
                    topicsMap = parseJSON(json);
                }
                catch(Exception e)
                {
                    //Clais exception like non-English language
                }
                
                if (topicsMap != null)
                {
                    Serializable property = nodeService.getProperty(actionedUponNodeRef, ASPECT_CALAIS);
                    ArrayList<Serializable> newProperty;
                    if (property == null || !(property instanceof ArrayList))
                    {
                        newProperty = new ArrayList<Serializable>();
                    } 
                    else
                    {
                        newProperty = (ArrayList<Serializable>) property;
                    }
                    for (String type : topicsMap.keySet())
                    {
                        List<JSONObject> entities =  topicsMap.get(type);
                        for (JSONObject entity : entities)
                        {
                            String value = entity.getString("name");
                            boolean hasResolutions = entity.containsKey("resolutions");
                            JSONObject resolutions = null;
                            if(hasResolutions)
                            {
                                JSONArray resloutionArray = entity.getJSONArray("resolutions");
                                if(resloutionArray.size() > 0 )
                                {    
                                    resolutions = resloutionArray.getJSONObject(0);
                                }
                                else
                                {    
                                    hasResolutions = false;
                                }
                            }
                            
                            if(useNormalized && hasResolutions && resolutions.containsKey("name"))
                            {    
                                value = resolutions.getString("name");
                            }
                            //System.out.println("Debug  : " + type + " : " + value);
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
                                    NodeRef calaisTag = addCalaisTag(actionedUponNodeRef, type, value);
                                    addPropertyWithAspect(calaisTag, nodeService, entity.getString("href"), ASPECT_SEMANTIC_CATEGORY, PROP_CALAIS_URI);
                                    if (hasResolutions && resolutions.containsKey("name"))
                                    {    
                                        addPropertyWithAspect(calaisTag, nodeService, resolutions.getString("name"), 
                                                ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("normalizedName"));
                                    }    
                                    if (hasResolutions && resolutions.containsKey("webaddress"))
                                    {    
                                        addPropertyWithAspect(calaisTag, nodeService, resolutions.getString("webaddress"), 
                                                ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("webaddress"));
                                    }
                                    if (hasResolutions && resolutions.containsKey("latitude"))
                                    {    
                                        addPropertyWithAspect(calaisTag, nodeService, resolutions.getDouble("latitude"), 
                                                ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("latitude"));
                                    }    
                                    if (hasResolutions && resolutions.containsKey("longitude"))
                                    {    
                                        addPropertyWithAspect(calaisTag, nodeService, resolutions.getDouble("longitude"), 
                                                ASPECT_SEMANTIC_CATEGORY, createResolitionsQName("longitude"));
                                    }  
                                    if(!newProperty.contains(calaisTag)) //Fix should avoid assigning same categories twice
                                        newProperty.add(calaisTag);
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
                        }                       
                    }
                    addPropertyWithAspect(actionedUponNodeRef, nodeService, newProperty, ASPECT_CALAIS, ASPECT_CALAIS);                
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
     * Calls Calais service and saves extracted metadata as rdf or simple format
     * content
     * 
     * @param actionedUponNodeRef
     * @param calaisKey
     * @param isRDFnotJSON
     * @param nodeService
     * @param content
     * @throws InvalidNodeRefException
     * @throws InvalidAspectException
     * @throws InvalidTypeException
     * @throws ContentIOException
     */
    private String getCalaisResult(NodeRef actionedUponNodeRef, String calaisKey, Boolean isRDFnotJSON,
            NodeService nodeService, String content, Boolean persist) throws InvalidNodeRefException,
            InvalidAspectException, InvalidTypeException, ContentIOException
    {
        CalaisParser parser = new CalaisParser(calaisKey, "1000", "false", "", "false");
        String result = (isRDFnotJSON) ? parser.getRDF(content) : parser.getJSON(content);
        if (persist)
        {
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            if (!nodeService.hasAspect(actionedUponNodeRef, isRDFnotJSON ? ASPECT_CALAIS_RDF : ASPECT_CALAIS_JSON))
            {
                try
                {
                    nodeService.addAspect(actionedUponNodeRef, isRDFnotJSON ? ASPECT_CALAIS_RDF : ASPECT_CALAIS_JSON, props);
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            ContentService contentService = services.getContentService();
            ContentWriter writer = contentService.getWriter(actionedUponNodeRef, isRDFnotJSON ? PROP_RDF_NAME
                    : PROP_CALAIS_JSON, true);
            writer.setMimetype(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            writer.setEncoding("UTF-8");
            writer.putContent(result);
        }
        return result;
    }

    /*
     * Adds parameter definitions
     * 
     * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
     */
    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList)
    {
        paramList.add(new ParameterDefinitionImpl(CalaisActionHandler.PROP_CALAIS_KEY, DataTypeDefinition.TEXT, true,
                getParamDisplayLabel(CalaisActionHandler.PROP_CALAIS_KEY)));
        paramList.add(new ParameterDefinitionImpl(CalaisActionHandler.PROP_SAVE_JSON, DataTypeDefinition.BOOLEAN, true,
                getParamDisplayLabel(CalaisActionHandler.PROP_SAVE_JSON)));
        paramList.add(new ParameterDefinitionImpl(CalaisActionHandler.PROP_SAVE_RDF, DataTypeDefinition.BOOLEAN, true,
                getParamDisplayLabel(CalaisActionHandler.PROP_SAVE_RDF)));
        paramList.add(new ParameterDefinitionImpl(CalaisActionHandler.PROP_AUTO_TAG, DataTypeDefinition.BOOLEAN, true,
                getParamDisplayLabel(CalaisActionHandler.PROP_AUTO_TAG)));
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
     * Adds category for semantic type (if need to) and adds category for
     * semantic tag name
     * 
     * @param nodeRef
     * @param categoryName
     * @param tagName
     * @return
     */
    public NodeRef addCalaisTag(final NodeRef nodeRef, final String categoryName, final String tagName)
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

    /**
     * @param result
     * @return
     */
    public static HashMap<String, List<JSONObject>> parseJSON(String result)
    {
        int start = "<?xml version=\"1.0\" encoding=\"utf-8\"?><string xmlns=\"http://clearforest.com/\">".length();
        int length = result.length() - "</string>".length();
        String substring = result.substring(start, length);
        HashMap<String, List<JSONObject>> types = null;
        try
        {
            JSONObject obj = JSONObject.fromObject(substring);
            types = new HashMap<String, List<JSONObject>>();
            for (Object en : obj.names())
            {
                // System.out.println(en.toString());
                Object value = obj.get(en);
                if (value instanceof JSONObject)
                {
                    JSONObject jsonValue = (JSONObject) value;
                    Object typeGroup = jsonValue.get("_typeGroup");
                    if ("entities".equals(typeGroup))
                    {
                        Object type = jsonValue.get("_type");
                        if (type != null)
                        {
                            List<JSONObject> entities = types.get(type.toString());
                            if (entities == null)
                            {
                                entities = new ArrayList<JSONObject>();
                                types.put(type.toString(), entities);
                            }
                            jsonValue.put("href", en);
                            entities.add(jsonValue);
                            // System.out.println(topics);
                        }
                    }
                }
                // System.out.println();
            }
        }
        catch (Exception e)
        {
            System.out.println("CalaisAction.parseJSON exception, likely language not recognized by Calais error (not English or too short to tell if its English)");
        }        
        return types;
    }

    /**
     * @param useNormalized the useNormalized to set
     */
    public void setUseNormalized(Boolean useNormalized)
    {
        this.useNormalized = useNormalized;
    }

    /**
     * @return the useNormalized
     */
    public Boolean getUseNormalized()
    {
        return useNormalized;
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
