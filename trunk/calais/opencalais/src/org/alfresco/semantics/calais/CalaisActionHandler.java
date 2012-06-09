package org.alfresco.semantics.calais;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.wizard.IWizardBean;

/**
 * Calais action handler
 * 
 * @author alexander
 *
 */
public class CalaisActionHandler extends BaseActionHandler
{
	private static final long serialVersionUID = -4041478169628200193L;
	public static final String PROP_CALAIS_KEY = "calaisKey";
	public static final String PROP_SAVE_JSON = "saveJSON";
	public static final String PROP_SAVE_RDF = "saveRDF";
	public static final String PROP_AUTO_TAG = "autoTag";

	/* (non-Javadoc)
	 * @see org.alfresco.web.bean.actions.IHandler#generateSummary(javax.faces.context.FacesContext, org.alfresco.web.bean.wizard.IWizardBean, java.util.Map)
	 */
	public String generateSummary(FacesContext context, IWizardBean wizard, Map<String, Serializable> props)
	{
		return "Get Calais Metadata";
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.bean.actions.IHandler#getJSPPath()
	 */
	public String getJSPPath() 
	{
		return "/jsp/extension/calais.jsp";
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.bean.actions.IHandler#prepareForEdit(java.util.Map, java.util.Map)
	 */
	public void prepareForEdit(Map<String, Serializable> props, Map<String, Serializable> repoProps)
	{
		props.put(PROP_CALAIS_KEY, (String)repoProps.get(PROP_CALAIS_KEY));
		props.put(PROP_SAVE_JSON, (Boolean)repoProps.get(PROP_SAVE_JSON));
		props.put(PROP_SAVE_RDF, (Boolean)repoProps.get(PROP_SAVE_RDF));
		props.put(PROP_AUTO_TAG, (Boolean)repoProps.get(PROP_AUTO_TAG));
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.bean.actions.IHandler#prepareForSave(java.util.Map, java.util.Map)
	 */
	public void prepareForSave(Map<String, Serializable> props, Map<String, Serializable> repoProps)
	{
		 repoProps.put(PROP_CALAIS_KEY, (String)props.get(PROP_CALAIS_KEY));
		 repoProps.put(PROP_SAVE_JSON, (Boolean)props.get(PROP_SAVE_JSON));
		 repoProps.put(PROP_SAVE_RDF, (Boolean)props.get(PROP_SAVE_RDF));
		 repoProps.put(PROP_AUTO_TAG, (Boolean)props.get(PROP_AUTO_TAG));
	}

}
