package org.apache.taverna.scufl2.api.io;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * Thrown when there is a problem reading a {@link WorkflowBundle}
 * 
 * @see WorkflowBundleIO#readBundle(java.io.File, String)
 * @see WorkflowBundleIO#readBundle(java.io.InputStream, String)
 * @see WorkflowBundleIO#readBundle(java.net.URL, String)
 * @see WorkflowBundleReader#readBundle(java.io.File, String)
 * @see WorkflowBundleReader#readBundle(java.io.InputStream, String)
 */
@SuppressWarnings("serial")
public class ReaderException extends Exception {
	/**
	 * Constructs an exception with no message or cause.
	 */
	public ReaderException() {
	}

	/**
	 * Constructs an exception with the specified message and no cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 */
	public ReaderException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with the specified message and cause.
	 * 
	 * @param message
	 *            details about the exception. Can be <code>null</code>
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public ReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with the specified cause and and the same message
	 * as the cause (if the cause is not null).
	 * 
	 * @param cause
	 *            the cause of the exception. Can be <code>null</code>
	 */
	public ReaderException(Throwable cause) {
		super(cause);
	}
}
