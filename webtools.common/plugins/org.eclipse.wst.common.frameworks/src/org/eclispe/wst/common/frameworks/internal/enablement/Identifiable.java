/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclispe.wst.common.frameworks.internal.enablement;


/**
 * @author mdelder
 *  
 */
public interface Identifiable {

	/**
	 * return id that uniquely identifies this instance of an extension point. It is up to each
	 * individual extension point provider to decide what that might be (e.g., "editorID", "pageID",
	 * etc.
	 */
	String getID();

	/**
	 * 
	 * @return the order the Identifiable element was loaded
	 */
	int getLoadOrder();
}