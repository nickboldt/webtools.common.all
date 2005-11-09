/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.wst.common.componentcore.datamodel.ProjectMigratorDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectMigratorDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResource;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesResourceFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emf.resource.ReferencedResource;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
/**
 * Manages the underlying Module Structural Metamodel.
* <a name="module-structural-model"/>
* <p>
* Each ModuleCoreNature from a given project can provide access to the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; of the project.
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; is a subclass of
* {@see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel}&nbsp;that manages
* resources associated with the Module Structural Metamodel. As an EditModel, the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; references EMF resources,
* that contain EMF models -- in this case, the EMF model of <i>.component </i> file.
* </p>
* <p>
* Clients are encouraged to use the Edit Facade pattern (via
* {@see org.eclipse.wst.common.modulecore.ModuleCore}&nbsp; or one if its relevant subclasses)
* to work directly with the Module Structural Metamodel.
* </p> 
* <p>
* <a href="ModuleCoreNature.html#model-discussion">See the discussion</a> of how ModuleStructuralModel relates to the ArtifactEditModel and ModuleCoreNature.
* <a name="accessor-key"/>
* <p>
* All EditModels have a lifecycle that must be enforced to keep the resources loaded that are in
* use, and to unload resources that are not in use. To access an EditModel, clients are required to
* supply an object token referred to as an accessor key. The accessor key allows the framework to
* better track which clients are using the EditModel, and to ensure that only a client which has
* accessed the EditModel with an accessor key may invoke save*()s on that EditModel.
* </p>
*/ 
public class ModuleStructuralModel extends EditModel implements IAdaptable {
	
	public static final String MODULE_CORE_ID = "moduleCoreId"; //$NON-NLS-1$ 
	private boolean migrating;
	private HashSet migrated = new HashSet();
	private HashSet moved = new HashSet();
  

    public ModuleStructuralModel(String editModelID, EMFWorkbenchContext context, boolean readOnly) {
        super(editModelID, context, readOnly);
    }
    /**
	 * Release each of the referenced resources.
	 */
	protected void release(ReferencedResource aResource) {
		if (isReadOnly() && aResource.getReadCount() != 0)
			aResource.releaseFromRead();
		else
			aResource.releaseFromWrite();

	}
	protected boolean removeResource(Resource aResource) {
		if (aResource != null) {
			synchronized (aResource) {
				aResource.eAdapters().remove(resourceAdapter);
				return getResources().remove(aResource);
			}
		}
		return false;
	}
    
    /* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel#getPrimaryRootObject()
	 */
	public EObject getPrimaryRootObject() {
		
		EObject modelRoot = super.getPrimaryRootObject();
		if(modelRoot == null) {
			prepareProjectModulesIfNecessary();
			return super.getPrimaryRootObject();
		}
		return modelRoot;
	}
       
	public WTPModulesResource  makeWTPModulesResource() {
		return (WTPModulesResource) createResource(WTPModulesResourceFactory.WTP_MODULES_URI_OBJ);
	}

	public Resource prepareProjectModulesIfNecessary() {

		XMIResource res = (XMIResource) getPrimaryResource();
		if (!res.isLoaded() && !migrating) {
			migrateOldMetaData();
			res = (XMIResource) getPrimaryResource();
		}
		if(res == null)
			res = makeWTPModulesResource();		
		try {
			addProjectModulesIfNecessary(res);
		} catch (IOException e) {		
			Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
		}
		return res;
	}
	
	private synchronized void migrateOldMetaData() {
		migrating = true;
		moveOldMetaDataFile();
		migrateComponentsIfNecessary();
	}
	private void migrateComponentsIfNecessary() {
		
		WorkspaceJob job = new WorkspaceJob("Adding Facets")
	      {
	        
	        public IStatus runInWorkspace(IProgressMonitor monitor)
	        {
	          try
	          {	
	        	  IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
			      for (int i = 0; i < projects.length; i++) {
						IProject project = projects[i];
						IDataModel dm = DataModelFactory.createDataModel(new ProjectMigratorDataModelProvider());
						dm.setStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME,project.getName());
						try {
							dm.getDefaultOperation().execute(null,null);
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}}
	          catch (Exception e)
	          {
	        	  return Status.CANCEL_STATUS;
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.schedule(50000);
		
	}
	private void moveOldMetaDataFile() {
		WorkspaceJob job = new WorkspaceJob("Migrating metadata")
	      {
	        
	        public IStatus runInWorkspace(IProgressMonitor monitor)
	        {
	          try
	          {
	        	IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
	      		for (int i = 0; i < projects.length; i++) {
	      			IProject project = projects[i];
	      			if (!moved.contains(project))
		      			moveMetaDataFile(project);
		      			project.getFolder(".deployables").delete(true,monitor);
		      			project.refreshLocal(IResource.DEPTH_INFINITE,monitor);
		      			moved.add(project);
	      			}
	      		}
	          catch (Exception e)
	          {
	        	  return Status.CANCEL_STATUS;
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.schedule();
	}
	private void moveMetaDataFile(IProject project) {
		IResource oldfile = project.findMember(".wtpmodules");
		if (oldfile != null && oldfile.exists()) {
			
			try {
				if (!migrated.contains(project)) {
					IFolder settingsFolder = project.getFolder(".settings");
					if (!settingsFolder.exists())
						settingsFolder.create(true,true,null);
					oldfile.move(new Path(".settings/.component"),true,null);
					migrated.add(project);
				}
			} catch (CoreException e) {
				Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
			}
		}
	}
	public Object getAdapter(Class anAdapter) {
		return Platform.getAdapterManager().getAdapter(this, anAdapter); 
	}
	
	protected void addProjectModulesIfNecessary(XMIResource aResource) throws IOException {
		
		if (aResource != null) { 
			if(aResource.getContents().isEmpty()) {
				ProjectComponents projectModules = ComponentcorePackage.eINSTANCE.getComponentcoreFactory().createProjectComponents();
				projectModules.setProjectName(project.getName());
				aResource.getContents().add(projectModules); 
				aResource.setID(projectModules, MODULE_CORE_ID);
			}
		}
	}
	protected Resource getAndLoadLocalResource(URI aUri) {
		
			Resource resource = getLocalResource(aUri);
			if (null != resource) {
				synchronized (resource) {
					if (!resource.isLoaded()) {
						try {
							resource.load(Collections.EMPTY_MAP); // reload it
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
			return resource;
	}
}
