/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Aug 20, 2003
 *  
 */
package org.eclipse.wst.internal.common.emf.utilities;


public class Namespace {

	protected String prefix;
	protected String nsURI;


	public Namespace(String prefix, String uri) {
		this.prefix = prefix;
		this.nsURI = uri;
	}

	public String getNsURI() {
		return nsURI;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setNsURI(String string) {
		nsURI = string;
	}

	public void setPrefix(String string) {
		prefix = string;
	}
}