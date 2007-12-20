/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidationSelectionHandlerRegistryReader;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;
import org.eclipse.wst.validation.ui.internal.ManualValidationRunner;

/**
 * This class implements the pop-up menu item "Run Validation" When the item is selected, this
 * action triggers a validation of the project, using all configured, enabled validators.
 */
public class ValidationMenuAction implements IViewActionDelegate {
	private ISelection _currentSelection;
	protected static final String SEP = "/"; //$NON-NLS-1$
	private Display _currentDisplay;
	private IResourceVisitor _folderVisitor;
	private IResourceVisitor _projectVisitor;
	private Map<IProject, Set<IResource>> _selectedResources;

	public ValidationMenuAction() {
		super();
		// cache the display before this action is forked. After the action is forked,
		// Display.getCurrent() returns null.
		_currentDisplay = Display.getCurrent(); 
		_selectedResources = new HashMap<IProject, Set<IResource>>();
	}

	private Display getDisplay() {
		return (_currentDisplay == null) ? Display.getCurrent() : _currentDisplay;
	}

	/**
	 * Return the wizard's shell.
	 */
	Shell getShell() {
		Display display = getDisplay();
		Shell shell = (display == null) ? null : display.getActiveShell();
		if (shell == null && display != null) {
			Shell[] shells = display.getShells();
			if (shells.length > 0)
				shell = shells[0];
		}
		return shell;
	}

	private ISelection getCurrentSelection() {
		return _currentSelection;
	}

	/**
	 * Return a map of the selected elements. Each key of the map is an IProject, and the value is a
	 * Set of the selected resources in that project. If a project is selected, and nothing else in
	 * the project is selected, a full validation (null value) will be done on the project. If a
	 * project is selected, and some files/folders in the project are also selected, only the
	 * files/folders will be validated. If a folder is selected, all of its contents are also
	 * validated.
	 */
	// TODO: Check this method for selected resources.
	private Map<IProject, Set<IResource>> loadSelected(ValidateAction action, boolean refresh) {
		if (refresh) {
			// selectionChanged(IAction, ISelection) has been called. Flush the
			// existing cache of resources and add just the currently selected ones.
			_selectedResources.clear();
		}
		ISelection selection = getCurrentSelection();
		if ((selection == null) || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		Object[] elements = ((IStructuredSelection) selection).toArray();
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			if (element == null) {
				continue;
			}
			addSelected(action, element);
		}
		return _selectedResources;
	}

	private void addSelected(ValidateAction action, Object selected) {
		if (selected instanceof IProject) {
			addVisitor((IProject) selected);
		} else if (selected instanceof IFile) {
			addSelected((IFile) selected);
		} else if (selected instanceof IFolder) {
			addVisitor((IFolder) selected);
		} else if (isValidType(getExtendedType(selected))) {
			addSelected(action,getExtendedType(selected));
		} else {
			// Not a valid input type. Must be IProject, IJavaProject, or IResource.
			// If this ValidationMenuAction is a delegate of ValidateAction, is
			// the input type recognized by the ValidateAction?
			boolean valid = false;
			if (action != null) {
				IResource[] resources = action.getResource(selected);
				if (resources != null) {
					valid = true;
					for (int i = 0; i < resources.length; i++) {
						addSelected(action, resources[i]);
					}
				}
			}
			if (!valid) {
				// Stop processing. (This allows the "Run Validation" menu item
				// to gray out once at least one non-validatable element is selected.)
				_selectedResources.clear();
			}
		}
	}
	
	private Object getExtendedType(Object selected) {
		Object result = ValidationSelectionHandlerRegistryReader.getInstance().getExtendedType(selected);
		return result == null ? selected : result;
	}
	
	private boolean isValidType(Object object) {
		return object instanceof IProject || object instanceof IFile || object instanceof IFolder;
	}


	void addSelected(IResource selected) {
		IProject project = selected.getProject();
		boolean added = _selectedResources.containsKey(project);
		Set<IResource> changedRes = null;
		if (added) {
			// If the value is null, the entire project needs to be validated
			// anyway.
			changedRes = _selectedResources.get(project);
			if (changedRes == null) {
				return;
			}
		} else {
			changedRes = new HashSet<IResource>();
		}
		if (changedRes.add(selected)) {
			_selectedResources.put(project, changedRes);
		}
	}

	private void addVisitor(IFolder selected) {
		// add the folder and its children
		try {
			selected.accept(getFolderVisitor());
		} catch (CoreException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMenuAction.addSelected(IFolder)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return;
		}
	}

	private IResourceVisitor getFolderVisitor() {
		if (_folderVisitor == null) {
			_folderVisitor = new IResourceVisitor() {
				public boolean visit(IResource res) {
					if (res instanceof IFile) {
						addSelected(res);
					} else if (res instanceof IFolder) {
						addSelected(res);
					}
					return true; // visit the resource's children
				}
			};
		}
		return _folderVisitor;
	}
	
	private void addVisitor(IProject selected) {
		// add the folder and its children
		if(!selected.isAccessible())return;
		try {
			selected.accept(getProjectVisitor());
		} catch (CoreException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMenuAction.addSelected(IFolder)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return;
		}
	}

	private IResourceVisitor getProjectVisitor() {
		if (_projectVisitor == null) {
			_projectVisitor = new IResourceVisitor() {
				public boolean visit(IResource res) {
					if (res instanceof IFile)addSelected(res);
					else if (res instanceof IFolder)addSelected(res);
					
					return true; // visit the resource's children
				}
			};
		}
		return _projectVisitor;
	}

	/**
	 * The delegating action has been performed. Implement this method to do the actual work.
	 * 
	 * @param action
	 *            action proxy that handles the presentation portion of the plug-in action
	 */
	public void run(IAction action) {
		ValidateAction vaction = null;
		if (action instanceof ValidateAction) {
			vaction = (ValidateAction) action;
		}
		final Map<IProject, Set<IResource>> projects = loadSelected(vaction, false);
		if ((projects == null) || (projects.size() == 0)) {
			return;
		}
		
		// If the files aren't saved do not run validation.
		if(!handleFilesToSave(projects))return;

//		ValidationJob validationop = new ValidationJob(ValidationUIMessages.RunValidationDialogTitle){
//			protected IStatus run(IProgressMonitor monitor) {
//				final Map projectsMap = projects;
//				IStatus stat = validate(monitor, projectsMap);	
//				_selectedResources.clear();
//				return stat;
//			}
//		};
//		validationop.setProjectsMap(projects);
//		validationop.setRule(ResourcesPlugin.getWorkspace().getRoot());
//		validationop.setUser(true);
//		validationop.schedule();
		
		boolean confirm = org.eclipse.wst.validation.internal.ValManager.getDefault().getGlobalPreferences()
			.getConfirmDialog();
		ManualValidationRunner.validate(projects, true, false, confirm);
	}
	
	/**
	 * Selection in the desktop has changed. Plugin provider can use it to change the availability
	 * of the action or to modify other presentation properties.
	 * 
	 * <p>
	 * Action delegate cannot be notified about selection changes before it is loaded. For that
	 * reason, control of action's enable state should also be performed through simple XML rules
	 * defined for the extension point. These rules allow enable state control before the delegate
	 * has been loaded.
	 * </p>
	 * 
	 * @param action
	 *            action proxy that handles presentation portion of the plugin action
	 * @param selection
	 *            current selection in the desktop
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_currentSelection = selection;
		boolean enabled = false;
		
		// Don't force the plug-in to be activated just to check this setting.
		if (ValidationPlugin.isActivated() && ValidationRegistryReader.isActivated()){
			enabled = hasManualValidators(selection);
		}
		action.setEnabled(enabled);
	}
	
	/**
	 * Answer true if any of the selected items have manual validators enabled.
	 * @param selection
	 */
	private boolean hasManualValidators(ISelection selection){
		if (selection == null || selection.isEmpty())return false;
		
		if (selection instanceof IStructuredSelection){
			IStructuredSelection ss = (IStructuredSelection)selection;
			for (Iterator it = ss.iterator(); it.hasNext();){
				Object sel = it.next();
				if (sel instanceof IProject){
					if (ValManager.getDefault().hasManualValidators((IProject)sel))return true;
				} else if (sel instanceof IResource){
					IResource resource = (IResource)sel;
					Validator[] vals = ValidationFramework.getDefault()
						.getValidatorsFor(resource, true, false);
					if (vals.length > 0)return true;

				}
			}
		
		}
		
		return false;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction,
	 *      org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {
		//init
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() { 
		//dispose
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) { 
		//init
		
	}
	
	/**
	 * Handle any files that must be saved prior to running
	 * validation.
	 * 
	 * @param projects
	 * 			The list of projects that will be validated.
	 * @return
	 * 			True if all files have been saved, false otherwise.
	 */
	protected boolean handleFilesToSave(Map projects)
	{
	  List fileList = getIFiles(projects);
      final IEditorPart[] dirtyEditors = SaveFilesHelper.getDirtyEditors(fileList);
      if(dirtyEditors == null || dirtyEditors.length == 0)return true;
      boolean saveAutomatically = false;
      try
      {
        saveAutomatically = new GlobalConfiguration(ConfigurationManager.getManager().getGlobalConfiguration()).getSaveAutomatically();
      }
      catch(InvocationTargetException e)
      {
    	// In this case simply default to false.
      }
      SaveFilesDialog sfDialog = null;
      if(!saveAutomatically)
      {
	    sfDialog = new SaveFilesDialog(ValidationUIPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell());
	    sfDialog.setInput(Arrays.asList(dirtyEditors));
      }
      
      if(saveAutomatically || sfDialog.open() == Window.OK){
    	  ProgressMonitorDialog ctx = new ProgressMonitorDialog(getShell());
          
          IRunnableWithProgress runnable = new IRunnableWithProgress(){
              public void run(IProgressMonitor monitor) throws InvocationTargetException,
                  InterruptedException{
            	  try {
            		  monitor.beginTask(ValidationUIMessages.SaveFilesDialog_saving, dirtyEditors.length);
            		  int numDirtyEditors = dirtyEditors.length;
                      for(int i = 0; i < numDirtyEditors; i++){
                    	  dirtyEditors[i].doSave(new SubProgressMonitor(monitor, 1));
                      }
            	  } finally {
            		  monitor.done();
            	  }
             }
          };
          
          try {
                ctx.run(false, true, runnable);
                return true;
          } catch (InvocationTargetException e) {
                Logger.getLogger().logError(e);
          } catch (InterruptedException e) {
          }
      }
	  return false;
	}
	
	protected List<IFile> getIFiles(Map projects)
	{
		List<IFile> fileList = new LinkedList<IFile>();
		Set projectKeys = projects.keySet();
		Iterator projectIter = projectKeys.iterator();
		while(projectIter.hasNext())
		{
		  IProject project = (IProject) projectIter.next();
		  Set resourcesList = (Set) projects.get(project);
		  Iterator resourcesIter = resourcesList.iterator();
		  while(resourcesIter.hasNext())
		  {
			IResource resource = (IResource)resourcesIter.next();
			if(resource instanceof IFile)
			{
				fileList.add((IFile)resource);
			}
		  }
		}
		return fileList;
	}
	
}
