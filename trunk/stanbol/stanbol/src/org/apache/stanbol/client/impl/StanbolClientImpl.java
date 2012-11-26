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
package org.apache.stanbol.client.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.stanbol.client.StanbolClient;
import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.model.Enhancement;
import org.apache.stanbol.client.restclient.RestClient;
import org.apache.stanbol.client.restclient.RestClientImpl;
import org.apache.stanbol.client.services.StanbolContenthubService;
import org.apache.stanbol.client.services.StanbolEnhancerService;
import org.apache.stanbol.client.services.StanbolEntityhubService;
import org.apache.stanbol.client.services.exception.StanbolServiceException;
import org.apache.stanbol.client.services.impl.StanbolContenthubServiceImpl;
import org.apache.stanbol.client.services.impl.StanbolEnhancerServiceImpl;
import org.apache.stanbol.client.services.impl.StanbolEntityhubServiceImpl;

import com.sun.jersey.api.client.Client;

/**
 * Implementation of {@link StanbolClient}
 * 
 * @author efoncubierta
 *
 */
public class StanbolClientImpl implements StanbolClient {	
	// Stanbol services
	private StanbolEnhancerService enhancer;
	private StanbolContenthubService contenthub;
	private StanbolEntityhubService entityhub;
	
	/**
	 * Constructor
	 * 
	 * @param endpoint URL to Stanbol server
	 * @throws StanbolClientException
	 */
	public StanbolClientImpl(String endpoint) throws StanbolClientException {
		final RestClient restClient = new RestClientImpl();
		restClient.setHttpClient(Client.create());
		
		try {
			restClient.setEndpoint(endpoint);
		} catch(MalformedURLException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
		
		enhancer   = new StanbolEnhancerServiceImpl(restClient);
		entityhub  = new StanbolEntityhubServiceImpl(restClient);
		contenthub = new StanbolContenthubServiceImpl(restClient);
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#addContent(java.lang.String, java.io.File)
	 */
	@Override
	public void addContent(String id, File file) throws StanbolClientException {
		try {
			contenthub.add(id, file);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#addContent(java.lang.String, java.io.InputStream)
	 */
	@Override
	public void addContent(String id, InputStream is) throws StanbolClientException {
		try {
			contenthub.add(id, is);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#getMetadata(java.lang.String)
	 */
	@Override
	public ContentItem getMetadata(String id) throws StanbolClientException {
		try {
			return contenthub.get(id);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#enhance(java.io.File)
	 */
	@Override
	public List<Enhancement> enhance(File file) throws StanbolClientException {
		try {
			return enhancer.enhance(file);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.zaizi.stanbol.client.StanbolClient#enhance(java.io.InputStream)
	 */
	@Override
	public List<Enhancement> enhance(InputStream is) throws StanbolClientException {
		try {
			return enhancer.enhance(is);
		} catch(StanbolServiceException e) {
			throw new StanbolClientException(e.getMessage(), e);
		}
	}
}
