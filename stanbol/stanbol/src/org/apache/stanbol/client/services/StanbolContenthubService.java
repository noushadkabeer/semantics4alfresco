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
package org.apache.stanbol.client.services;

import java.io.File;
import java.io.InputStream;

import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.services.exception.StanbolServiceException;

/**
 * Define operations for Stanbol Enhancer
 * 
 * @author efoncubierta
 *
 */
public interface StanbolContenthubService extends StanbolService {
	
	// Stanbol Contenthub services URLs
	public static final String STANBOL_STORE_PATH = "contenthub/";
	public static final String STANBOL_STORE_CONTENT_PATH = STANBOL_STORE_PATH + "content/";
	public static final String STANBOL_STORE_METADATA_PATH = STANBOL_STORE_PATH + "metadata/";
	
	/**
	 * Create a content
	 * 
	 * @param id Content ID
	 * @param file File
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem add(String id, File file) throws StanbolServiceException;
	
	/**
	 * Create a content
	 * 
	 * @param id Content ID
	 * @param is InputStream
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem add(String id, InputStream is) throws StanbolServiceException;

	/**
	 * Get metadata information of a content
	 * 
	 * @param id Content ID
	 * @return Content item
	 * @throws StanbolServiceException
	 */
	public ContentItem get(String id) throws StanbolServiceException;
}
