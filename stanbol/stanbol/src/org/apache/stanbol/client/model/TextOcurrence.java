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

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Represents a text ocurrence
 * 
 * @author efoncubierta
 *
 */
public class TextOcurrence extends Enhancement {

	// properties
	private final String selectedText;
	private final Long start;
	private final Long end;
	private final String context;
	private final Integer ocurrenceWithinContext;
	
	public TextOcurrence(Resource resource) {
		super(resource);
		this.selectedText = "";
		this.start = 0L;
		this.end = 0L;
		this.context = "";
		this.ocurrenceWithinContext = 0;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public Long getStart() {
		return start;
	}

	public Long getEnd() {
		return end;
	}

	public String getContext() {
		return context;
	}

	public Integer getOcurrenceWithinContext() {
		return ocurrenceWithinContext;
	}
}
