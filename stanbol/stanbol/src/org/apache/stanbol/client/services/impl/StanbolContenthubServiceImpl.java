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
package org.apache.stanbol.client.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.services.StanbolContenthubService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Implementation of {@link StanbolContenthubService}
 * 
 * @author efoncubierta
 * 
 */
public class StanbolContenthubServiceImpl
	extends StanbolServiceAbstract implements StanbolContenthubService {
	private Logger logger = Logger.getLogger(StanbolContenthubServiceImpl.class);
	
	/**
	 * Constructor
	 * 
	 * @param restClient REST Client
	 */
	public StanbolContenthubServiceImpl(RestClient restClient) {
		super(restClient);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolContenthubService#add(java.lang.String, java.io.File)
	 */
	@Override
	public ContentItem add(String id, File file) throws StanbolServiceException {
		try {
			return add(id, new FileInputStream(file));
		} catch(FileNotFoundException e) {
			if(logger.isDebugEnabled()) {
				logger.debug(e.getMessage(), e);
			}
			throw new StanbolServiceException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolContenthubService#add(java.lang.String, java.io.InputStream)
	 */
	@Override
	public ContentItem add(String id, InputStream is) throws StanbolServiceException {
		String storeContentUrl = STANBOL_STORE_CONTENT_PATH + id;
         
        ClientResponse response = getRestClient().put(storeContentUrl, is,
        		MediaType.TEXT_PLAIN_TYPE, MediaType.WILDCARD_TYPE);
        
        int status = response.getStatus();
        if(status != 200 && status != 201 && status != 202) {
        	throw new StanbolServiceException(
        			"[HTTP " + status + "] Error while posting content into stanbol server"
        		);
        }
        
        if(logger.isDebugEnabled()) {
        	logger.debug("Content " + id +
        				 " has been sucessfully created at " + storeContentUrl);
    	}
        
        return parse(id, response.getEntityInputStream());
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.services.StanbolContenthubService#get(java.lang.String)
	 */
	@Override
	public ContentItem get(String id) throws StanbolServiceException {
		final String storeMetadataUrl = STANBOL_STORE_METADATA_PATH + id;
		
		// Retrieve metadata from Stanbol server
        ClientResponse response = getRestClient().get(storeMetadataUrl,
        		new MediaType("application", "rdf+xml"));
        
        // Check HTTP status code
        int status = response.getStatus();
        if(status != 200 && status != 201 && status != 202) {
        	throw new StanbolServiceException(
        			"[HTTP " + status + "] Error retrieving content from stanbol server"
        		);
        }
    	
    	if(logger.isDebugEnabled()) {
        	logger.debug("Content " + id +
        				 " has been sucessfully loaded from " + storeMetadataUrl);
    	}
    	
    	return parse(id, response.getEntityInputStream());
	}
	
	/**
	 * Extract a content item from an InputStream
	 * 
	 * @param id Content id
	 * @param is Content input stream
	 * @return Content item
	 */
	private ContentItem parse(String id, InputStream is) {
		Model model = ModelFactory.createDefaultModel();
    	model.read(is, null);
		
		return new ContentItem(id, model);
	}
}