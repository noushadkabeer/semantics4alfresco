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

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Extract enhancements objects from several kind of objects 
 * 
 * @author efoncubierta
 *
 */
public class EnhancementParser {
	
	public static final Property LAT_PROP = new PropertyImpl("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
	public static final Property LONG_PROP = new PropertyImpl("http://www.w3.org/2003/01/geo/wgs84_pos#long");
	
	/**
	 * Parse a Jena model as a list of enhancements
	 * 
	 * @param model Jena model
	 * @return List of enhancements
	 */
	public static List<Enhancement> parse(Model model) {
		List<Enhancement> enhancements = new ArrayList<Enhancement>();
		
		final ResIterator enhancementsIterator = model.listSubjectsWithProperty(RDF.type);
		while(enhancementsIterator.hasNext()) {
			final Resource enhancementResource = enhancementsIterator.next();
			final Enhancement enhancement = parse(enhancementResource);
			
			if(enhancement != null)
			{
				enhancements.add(enhancement);
			}
		}
		return enhancements;
	}
	
	/**
	 * Parse a Jena resource as an enhancement
	 * 
	 * @param resource Jena resource
	 * @return Enhancement
	 */
	public static Enhancement parse(Resource resource) {
		Enhancement enhancement = null;
		
		if(resource != null) {
			final StmtIterator types = resource.listProperties(RDF.type);
			while(types.hasNext() && enhancement == null) {
				final Statement stmt = types.next();
				final String name = stmt.getObject().asResource().getURI();
				if("http://fise.iks-project.eu/ontology/TextAnnotation".equals(name)) {
					enhancement = new TextAnnotation(resource);
				} else if("http://fise.iks-project.eu/ontology/EntityAnnotation".equals(name)) {
					EntityAnnotation ea = new EntityAnnotation(resource);
					enhancement = ea;
					// sreiner: add setting lat,long
					String ref = ea.getEntityReference();
					if (ref != null && ref.length() > 0)
					{
						Model model = resource.getModel();
						Resource refRes = model.getResource(ref);
						String latitude = null;
						if (refRes.hasProperty(LAT_PROP))
						{
							ea.setLatitude(refRes.getProperty(LAT_PROP).getString());
						}
						String longitude = null;
						if (refRes.hasProperty(LONG_PROP))
						{
							ea.setLongitude(refRes.getProperty(LONG_PROP).getString());
						}
					}					
				}
			}
		}
		
		return enhancement;
	}
}
