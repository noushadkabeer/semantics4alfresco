/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.client.model;

import org.apache.stanbol.client.ontology.FISE;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Represents an entity annotation
 * 
 * @author efoncubierta
 *
 */
@Deprecated
public class EntityAnnotation extends Annotation {
	
	// properties
	private String entityLabel ;       // http://fise.iks-project.eu/ontology/entity-label
	private String entityReference;    // http://fise.iks-project.eu/ontology/entity-reference
	private String entityType;         // http://fise.iks-project.eu/ontology/entity-type
	
	// sreiner: added lat / long
	private String latitude;
	private String longitude;
	// sreiner: added basoc type
	private String basicEntityType;         // http://fise.iks-project.eu/ontology/entity-type
	
	
	/**
	 * Constructor
	 * 
	 * @param resource Jena resource
	 */
	public EntityAnnotation(Resource resource) {
		super(resource);
	}

	/**
	 * Get the fise:entity-label property
	 * 
	 * @return fise:entity-label property
	 */
	public String getEntityLabel() {
		if(entityLabel == null && resource.hasProperty(FISE.ENTITY_LABEL)) {
			entityLabel = resource.getProperty(FISE.ENTITY_LABEL).getString();
		}
		return entityLabel;
	}

	/**
	 * Get the fise:entity-reference property
	 * 
	 * @return fise:entity-reference property
	 */
	public String getEntityReference() {
		if(entityReference == null && resource.hasProperty(FISE.ENTITY_REFERENCE)) {
			entityReference = resource.getPropertyResourceValue(FISE.ENTITY_REFERENCE).getURI();
		}
		return entityReference;
	}
	
	/**
	 * Get the fise:entity-type property
	 * 
	 * @return fise:entity-type property
	 */
	// sreiner: note: this like will just return 1st when have multiple entity types
	public String getEntityType() {
		if(entityType == null && resource.hasProperty(FISE.ENTITY_TYPE)) {
			entityType = resource.getPropertyResourceValue(FISE.ENTITY_TYPE).getURI();
		}
		return entityType;
	}

	
	// sreiner: added basic type
	public String getBasicEntityType()
	{
		if(basicEntityType == null && resource.hasProperty(FISE.ENTITY_TYPE)) {
			StmtIterator typesIterator = resource.listProperties(FISE.ENTITY_TYPE);
			while(typesIterator.hasNext())
			{
				Statement typeStatement = typesIterator.next();
				String type = typeStatement.getObject().asResource().getURI();
				if (type.equals("http://dbpedia.org/ontology/Person") || type.equals("http://dbpedia.org/ontology/Place") || 
				    type.equals("http://dbpedia.org/ontology/Organisation") )
				{
					basicEntityType = type;
					break;
				}
			}
		}
		return basicEntityType;
	}
	
	
	// sreiner: add latitude, longitude
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}
}
