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
package org.apache.stanbol.client;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.stanbol.client.exception.StanbolClientException;
import org.apache.stanbol.client.model.ContentItem;
import org.apache.stanbol.client.model.Enhancement;

/**
 * Define operations for Stanbol Services
 * 
 * @author efoncubierta
 *
 */
public interface StanbolClient {
	/**
	 * Create a content into contenthub/
	 * 
	 * @param id Content id
	 * @param file Content file
	 * @throws StanbolClientException
	 */
	public void addContent(String id, File file)
		throws StanbolClientException;
	
	/**
	 * Create a content into contenthub/
	 * 
	 * @param id Content id
	 * @param is Content input stream
	 * @throws StanbolClientException
	 */
	public void addContent(String id, InputStream is)
		throws StanbolClientException;
	
	/**
	 * Extract the content metadata from contenthub/
	 * 
	 * @param id Content id
	 * @return Content metadata
	 * @throws StanbolClientException
	 */
	public ContentItem getMetadata(String id)
		throws StanbolClientException;
	
	/**
	 * Extract content enhancements from engines/
	 * 
	 * @param file Content file
	 * @return List of enhancements
	 * @throws StanbolClientException
	 */
	public List<Enhancement> enhance(File file)
		throws StanbolClientException;
	
	/**
	 * Extract content enhancements from engines/
	 * 
	 * @param is Content input stream
	 * @return List of enhancements
	 * @throws StanbolClientException
	 */
	public List<Enhancement> enhance(InputStream is)
		throws StanbolClientException;
}
