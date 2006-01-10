/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.core.search.scope;

import org.eclipse.core.resources.IFile;

/**
 * A <code>SearchScope</code> defines where search result should be found by a
 * <code>SearchEngine</code> (e.g. project, workspace).
 * 
 * Clients must pass an instance of this class to the <code>search(...)</code>
 * methods. Such an instance can be created using the following factory methods
 * on <code>SearchScope</code>: <code>newSearchScope(IResource[])</code>,
 * <code>newWorkspaceScope()</code>
 * 
 * The default implementaion of the search scope has no filter, and at creation
 * does not contain any files, It could accept any workspace file.
 */
public abstract class SearchScope
{
	/**
	 * Returns the path to the workspace files that belong in this search scope.
	 * (see <code>IResource.getFullPath()</code>). For example,
	 * /MyProject/MyFile.txt
	 * 
	 * @return an array of files in the workspace that belong to this scope.
	 */
	public abstract IFile[] enclosingFiles();



	

}
