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
package org.eclipse.wst.common.componentcore.internal.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jem.util.emf.workbench.ResourceSetWorkbenchSynchronizer;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ComponentCoreURIConverter extends CompatibilityWorkbenchURIConverterImpl {
	
	private IProject containingProject;
	private IFolder archiveRoot;
	/**
	 *  
	 */
	public ComponentCoreURIConverter() {
		super();
	}

	/**
	 * @param anInputContainer
	 */
	public ComponentCoreURIConverter(IProject aContainingProject) {
		super();
		containingProject = aContainingProject;
	}

	/**
	 * @param aContainer
	 * @param aSynchronizer
	 */
	public ComponentCoreURIConverter(IProject aContainingProject, ResourceSetWorkbenchSynchronizer aSynchronizer) {
		super(aContainingProject, aSynchronizer);
		containingProject = aContainingProject;
	} 
 
	public IVirtualComponent findComponent(IResource res) {

		StructureEdit moduleCore = null;
		WorkbenchComponent module = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(containingProject);
			ComponentResource[] resources = moduleCore.findResourcesBySourcePath(res.getFullPath(), ResourceTreeNode.CREATE_RESOURCE_ALWAYS);
			for (int i = 0; i < resources.length; i++) {
				module = resources[i].getComponent();
				if (module != null)
					break;
			}
		} catch (UnresolveableURIException e) {
			// Ignore
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}
		return ComponentCore.createComponent(containingProject, module.getName());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.emf.workbench.WorkbenchURIConverterImpl#normalize(org.eclipse.emf.common.util.URI)
	 */
	public URI normalize(URI aURI) {
		URI normalizedURI = null;
		if(PlatformURLModuleConnection.MODULE.equals(aURI.scheme())) { 		
			try {
				normalizedURI = PlatformURLModuleConnection.resolve(aURI);
			} catch(IOException ioe) {
				ioe.printStackTrace();
			} 
		} else {
			normalizedURI = super.normalize(aURI);
		}
		if(normalizedURI == null) {
			normalizedURI = newPlatformURI(aURI);
		}
		else if(normalizedURI.scheme() == null || normalizedURI.scheme().length() == 0) {
			normalizedURI = URI.createPlatformResourceURI(getInputContainer().getFullPath().append(normalizedURI.toString()).toString());	
		}
		return normalizedURI;
	}
	
	private URI newPlatformURI(URI aNewURI) {
		
		try {
			String componentName = StructureEdit.getDeployedName(aNewURI);
			IVirtualComponent component = ComponentCore.createComponent(containingProject, componentName);

			URI deployPathSegment = ModuleURIUtil.trimToDeployPathSegment(aNewURI);
			
			//IVirtualFile newFile = component.getFile(new Path(deployPathSegment.path()));			
			IVirtualFolder rootFolder = component.getRootFolder();
			IVirtualFile newFile = rootFolder.getFile(new Path(deployPathSegment.path()));
			
			return URI.createPlatformResourceURI(newFile.getWorkspaceRelativePath().toString());
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see com.ibm.etools.xmi.helpers.CompatibilityURIConverter#deNormalize(URI)
	 */
	public URI deNormalize(URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri)) {
			IFile aFile = WorkbenchResourceHelper.getPlatformFile(uri);
			
			if (aFile != null) {
				IVirtualComponent component = findComponent(aFile);
				IProject fileProject = aFile.getProject();
				//If it is not in the same project then just return the URI as is.
				if (resourceSetSynchronizer.getProject() == fileProject)
					return getArchiveRelativeURI(aFile,component.getRootFolder().getUnderlyingFolder());
			}
		}
		return uri;
	}
	
	protected URI getContainerRelativeURI(IFile aFile) {
		IPath path = WorkbenchResourceHelperBase.getPathFromContainers(inputContainers, aFile.getFullPath());
		if (path != null)
			return URI.createURI(path.toString());
		return null;
	}
	protected URI getArchiveRelativeURI(IFile aFile, IContainer aContainer) {
		IPath path = WorkbenchResourceHelperBase.getPathFromContainers(Collections.singletonList(aContainer), aFile.getFullPath());
		if (path != null)
			return URI.createURI(path.toString());
		return null;
	}


}
