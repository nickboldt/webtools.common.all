package org.eclipse.wst.common.componentcore.internal.operation;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jem.util.emf.workbench.WorkbenchResourceHelperBase;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.ArtifactEditRegistryReader;
import org.eclipse.wst.common.componentcore.internal.util.IArtifactEditFactory;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;

public abstract class ArtifactEditProviderOperation extends AbstractDataModelOperation {
	
	protected ArtifactEdit artifactEdit;
	protected EMFWorkbenchContext emfWorkbenchContext;
	private CommandStack commandStack;

	public ArtifactEditProviderOperation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ArtifactEditProviderOperation(IDataModel model) {
		super(model);
	}
	
	protected final void initialize(IProgressMonitor monitor) {
		emfWorkbenchContext = (EMFWorkbenchContext) WorkbenchResourceHelperBase.createEMFContext(getTargetProject(), null);
		WorkbenchComponent module = getWorkbenchModule(); 
		artifactEdit = getArtifactEditForModule(module);
		doInitialize(monitor);
	}
	
	public IProject getTargetProject() {
		String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
		return ProjectUtilities.getProject(projectName);
	}
	
	public IVirtualComponent getTargetComponent() {
		String moduleName = model.getStringProperty(IArtifactEditOperationDataModelProperties.COMPONENT_NAME);
		return ComponentCore.createComponent(getTargetProject(),moduleName);
	}
	
	private void doInitialize(IProgressMonitor monitor) {
		//Default
	}

	private ArtifactEdit getArtifactEditForModule(WorkbenchComponent module) {
		ComponentHandle handle = ComponentHandle.create(StructureEdit.getContainingProject(module),module.getName());
		IVirtualComponent comp = handle.createComponent();
		ArtifactEditRegistryReader reader = ArtifactEditRegistryReader.instance();
		IArtifactEditFactory factory = reader.getArtifactEdit(comp.getComponentTypeId());
		return factory.createArtifactEditForWrite(comp);
	}

	/**
     * @return
     */
    public WorkbenchComponent getWorkbenchModule() {
        StructureEdit moduleCore = null;
        WorkbenchComponent module = null;
        try {
            moduleCore = StructureEdit.getStructureEditForRead(getTargetProject());
            module = moduleCore.findComponentByName(model.getStringProperty(IArtifactEditOperationDataModelProperties.COMPONENT_NAME));
        } finally {
            if (null != moduleCore) {
                moduleCore.dispose();
            }
        }
        return module;
    }
	
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return null;
	}

	public void dispose() {
		if (artifactEdit!=null) {
			artifactEdit.saveIfNecessary(new NullProgressMonitor());
			artifactEdit.dispose();
		}
			
		super.dispose();
		
	}

	public final IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		try {
			initialize(monitor);
			return doExecute(monitor, info);
		} finally {
			dispose();
		}
	}
	
	public abstract IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException;
	
	public ArtifactEdit getArtifactEdit(){
		return artifactEdit;
	}

}
