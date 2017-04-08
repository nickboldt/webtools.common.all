/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ProjectUtilities.java,v $$
 *  $$Revision: 1.6 $$  $$Date: 2008/03/05 19:43:18 $$ 
 */

package org.eclipse.jem.util.emf.workbench;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;

import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;


/**
 * EMF Workbench Project Utilities.
 * 
 * @since 1.0.0
 */

public class ProjectUtilities {

	/**
	 * Project control file name in project.
	 * 
	 * @since 1.0.0
	 */
	public final static String DOT_PROJECT = ".project"; //$NON-NLS-1$

	/**
	 * Classpath control file name in project.
	 * 
	 * @since 1.0.0
	 */
	public final static String DOT_CLASSPATH = ".classpath"; //$NON-NLS-1$

	public ProjectUtilities() {
	}

	/**
	 * Add the nature id to the project ahead of all other nature ids.
	 * 
	 * @param proj
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addNatureToProject(IProject proj, String natureId) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
		newNatures[0] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, null);
	}

	/**
	 * Add the nature id after all of the other nature ids for the project.
	 * 
	 * @param proj
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addNatureToProjectLast(IProject proj, String natureId) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, null);
	}

	/**
	 * Remove the nature id from the project.
	 * 
	 * @param project
	 * @param natureId
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeNatureFromProject(IProject project, String natureId) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		int size = prevNatures.length;
		int newsize = 0;
		String[] newNatures = new String[size];
		boolean matchfound = false;
		for (int i = 0; i < size; i++) {
			if (prevNatures[i].equals(natureId)) {
				matchfound = true;
				continue;
			} else
				newNatures[newsize++] = prevNatures[i];
		}
		if (!matchfound)
			throw new CoreException(new Status(IStatus.ERROR, JEMUtilPlugin.ID, 0,
					"The nature id " + natureId + " does not exist on the project " + project.getName(), null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		else {
			String[] temp = newNatures;
			newNatures = new String[newsize];
			System.arraycopy(temp, 0, newNatures, 0, newsize);
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}
	}

	/**
	 * Add the list of projects to end of the "referenced projects" list from the project's description.
	 * 
	 * @param project
	 * @param toBeAddedProjectsList
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addReferenceProjects(IProject project, List toBeAddedProjectsList) throws CoreException {

		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		for (int i = 0; i < toBeAddedProjectsList.size(); i++) {
			projectsList.add(toBeAddedProjectsList.get(i));
		}

		IProject[] refProjects = new IProject[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (IProject) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Add the single project to the end of the "referenced projects" list from the project's description.
	 * 
	 * @param project
	 * @param projectToBeAdded
	 * @throws CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void addReferenceProjects(IProject project, IProject projectToBeAdded) throws CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		projectsList.add(projectToBeAdded);

		IProject[] refProjects = new IProject[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (IProject) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Force a an immediate build of the project.
	 * 
	 * @param project
	 * @param progressMonitor
	 * 
	 * @since 1.0.0
	 */
	public static void forceAutoBuild(IProject project, IProgressMonitor progressMonitor) {
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, progressMonitor);
		} catch (CoreException ce) {
			//Revisit: Need to use a Logger
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Return if auto build is turned on.
	 * 
	 * @return <code>true</code> if auto build is turned on.
	 * 
	 * @since 1.0.0
	 */
	public static boolean getCurrentAutoBuildSetting() {

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription wd = workspace.getDescription();
		return wd.isAutoBuilding();
	}

	/**
	 * Get the project associated with the given object.
	 * 
	 * @param object
	 *            may be an <code>IProject, IResource, IAdaptable (to an IProject), EObject (gets IProject if object is in a ProjectResourceSet</code>.
	 * @param natureId
	 *            if <code>null</code> then returns project. If not <code>null</code> then returns project only if project has this nature id.
	 * @return project associated with the object or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Object object, String natureId) {
		IProject result = getProject(object);
		if (natureId == null)
			return result;
		if (result != null && result.isAccessible() && natureId != null)
			try {
				if (result.hasNature(natureId))
					return result;
			} catch (CoreException e) {
				Logger.getLogger().logError(e);
			}
		return null;
	}

	/**
	 * Get the project associated with the given object.
	 * 
	 * @param object
	 *            may be an <code>IProject, IResource, IAdaptable (to an IProject), EObject (gets IProject if object is in a ProjectResourceSet</code>.
	 * @return project associated with the object or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Object object) {
		IProject result = null;

		if (object instanceof IProject)
			result = (IProject) object;
		else if (object instanceof IResource)
			result = ((IResource) object).getProject();
		else if (object instanceof IAdaptable)
			result = (IProject) ((IAdaptable) object).getAdapter(IProject.class);
		else if (object instanceof EObject)
			result = getProject((EObject) object);

		return result;
	}

	/**
	 * Get the project associated with the given EObject. (If in a ProjectResourceSet, then the project from that resource set).
	 * 
	 * @param aRefObject
	 * @return project if associated or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(EObject aRefObject) {
		if (aRefObject != null) {
			Resource resource = aRefObject.eResource();
			if (resource != null)
				return getProject(resource);
		}
		return null;
	}
	
	public static IFile getPlatformFile(URI uri) {
		if (WorkbenchResourceHelperBase.isPlatformResourceURI(uri)) {
			String fileString = URI.decode(uri.path());
			fileString = fileString.substring(JEMUtilPlugin.PLATFORM_RESOURCE.length() + 1);
			return WorkbenchResourceHelperBase.getWorkspace().getRoot().getFile(new Path(fileString));
		}
		return null;
	}

	/**
	 * Get the project associated with the given Resource. (If in a ProjectResourceSet, then the project from that resource set).
	 * 
	 * @param resource
	 * @return project if associated or <code>null</code> if not found.
	 * 
	 * @since 1.0.0
	 */
	public static IProject getProject(Resource resource) {
		ResourceSet set = resource == null ? null : resource.getResourceSet();
		if (set == null) {
			URI uri = resource.getURI();
			if (uri == null) return null;
			IFile file = getPlatformFile(resource.getURI());
			if (file == null) 
				return null;
			return file.getProject();
		}
			
		if (set instanceof ProjectResourceSet)
			return ((ProjectResourceSet) set).getProject();
		URIConverter converter = set == null ? null : set.getURIConverter();
		if (converter != null && converter instanceof WorkbenchURIConverter && ((WorkbenchURIConverter) converter).getOutputContainer() != null)
			return ((WorkbenchURIConverter) converter).getOutputContainer().getProject();
		else
			return null;
	}

	/**
	 * Remove the list of projects from the list of "referenced projects" in the project's description.
	 * 
	 * @param project
	 * @param toBeRemovedProjectList
	 * @throws org.eclipse.core.runtime.CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeReferenceProjects(IProject project, List toBeRemovedProjectList) throws org.eclipse.core.runtime.CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add(projects[i]);
		}

		for (int i = 0; i < toBeRemovedProjectList.size(); i++) {
			projectsList.remove(toBeRemovedProjectList.get(i));
		}

		IProject[] refProjects = new IProject[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (IProject) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Remove the project from the list of "referenced projects" in the description for the given project.
	 * 
	 * @param project
	 * @param toBeRemovedProject
	 * @throws org.eclipse.core.runtime.CoreException
	 * 
	 * @since 1.0.0
	 */
	public static void removeReferenceProjects(IProject project, IProject toBeRemovedProject) throws org.eclipse.core.runtime.CoreException {
		IProjectDescription description = project.getDescription();
		IProject[] projects = description.getReferencedProjects();

		ArrayList projectsList = new ArrayList();

		for (int i = 0; i < projects.length; i++) {
			projectsList.add((projects[i]));
		}

		projectsList.remove(toBeRemovedProject);

		IProject[] refProjects = new IProject[projectsList.size()];

		for (int i = 0; i < refProjects.length; i++) {
			refProjects[i] = (IProject) (projectsList.get(i));
		}

		description.setReferencedProjects(refProjects);
		project.setDescription(description, null);
	}

	/**
	 * Turn auto-building off.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOff() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(false);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Turn auto-building on.
	 * 
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOn() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(true);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);
		}
	}

	/**
	 * Set the auto-building state.
	 * 
	 * @param aBoolean
	 *            <code>true</code> to turn auto-building on.
	 * 
	 * @since 1.0.0
	 */
	public static void turnAutoBuildOn(boolean aBoolean) {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription wd = workspace.getDescription();
			wd.setAutoBuilding(aBoolean);
			workspace.setDescription(wd);
		} catch (CoreException ce) {
			//Logger.getLogger().logError(ce);

		}
	}

	/**
	 * Adds a builder to the build spec for the given project.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param project
	 *            Project to add to.
	 * @return whether the builder id was actually added (it may have already existed)
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean addToBuildSpec(String builderID, IProject project) throws CoreException {
		return addToBuildSpecBefore(builderID, null, project);
	}

	/**
	 * Adds a builder to the build spec for the given project, immediately before the specified successor builder.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param successorID
	 *            The id to put the builder before.
	 * @return whether the builder id was actually added (it may have already existed)
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean addToBuildSpecBefore(String builderID, String successorID, IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			boolean successorFound = false;
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] newCommands = new ICommand[commands.length + 1];
			for (int j = 0, index = 0; j < commands.length; j++, index++) {
				if (successorID != null && commands[j].getBuilderName().equals(successorID)) {
					successorFound = true;
					newCommands[index++] = command;
				}
				newCommands[index] = commands[j];
			}
			if (!successorFound)
				newCommands[newCommands.length - 1] = command;
			description.setBuildSpec(newCommands);
			project.setDescription(description, null);
		}
		return !found;
	}

	/**
	 * Remove the builder from the build spec.
	 * 
	 * @param builderID
	 *            The id of the builder.
	 * @param project
	 *            Project to remove from.
	 * @return boolean if the builder id was found and removed
	 * @throws CoreException
	 * @since 1.0.0
	 */
	public static boolean removeFromBuildSpec(String builderID, IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (found) {
			ICommand[] newCommands = new ICommand[commands.length - 1];
			int newCount = 0;
			for (int i = 0; i < commands.length; ++i) {
				if (!(commands[i].getBuilderName().equals(builderID))) {
					//Add the existng to the new array
					newCommands[newCount] = commands[i];
					newCount++;
				}
			}

			description.setBuildSpec(newCommands);
			project.setDescription(description, null);

		}
		return found;

	}

	/**
	 * Ensure the container is not read-only.
	 * <p>
	 * For Linux, a Resource cannot be created in a ReadOnly folder. This is only necessary for new files.
	 * 
	 * @param resource
	 *            workspace resource to make read/write
	 * @since 1.0.0
	 */
	public static void ensureContainerNotReadOnly(IResource resource) {
		if (resource != null && !resource.exists()) { //it must be new
			IContainer container = resource.getParent();
			ResourceAttributes attr = container.getResourceAttributes();
			while (attr != null && !attr.isReadOnly()) {
				container = container.getParent();
				if (container == null)
					break;
				attr = container.getResourceAttributes();
			}
			if (container != null && attr != null)
				attr.setReadOnly(false);
		}
	}

	/**
	 * Get projects from primary nature.
	 * 
	 * @param natureID
	 * @return All projects that have the given nature id as the first nature id.
	 * 
	 * @since 1.0.0
	 */
	public static IProject[] getProjectsForPrimaryNature(String natureID) {
		IProject[] projectsWithNature = new IProject[] {};
		List result = new ArrayList();
		IProject[] projects = getAllProjects();
		for (int i = 0; i < projects.length; i++) {
			if (isProjectPrimaryNature(projects[i], natureID))
				result.add(projects[i]);
		}
		return (IProject[]) result.toArray(projectsWithNature);
	}

	/**
	 * Get all projects in the workspace
	 * 
	 * @return all workspace projects
	 * 
	 * @since 1.0.0
	 */
	public static IProject[] getAllProjects() {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	/**
	 * Is this nature id the primary nature id for the project
	 * 
	 * @param project
	 * @param natureID
	 * @return <code>true</code> if first nature id for the project.
	 * 
	 * @since 1.0.0
	 */
	public static boolean isProjectPrimaryNature(IProject project, String natureID) {
		String[] natures = null;
		try {
			natures = project.getDescription().getNatureIds();
		} catch (Exception e1) {
		}
		return (natures != null && natures.length > 0 && natures[0].equals(natureID));
	}

	protected static IPath createPath(IProject p, String defaultSourceName) {
		IPath path = new Path(p.getName());
		path = path.append(defaultSourceName);
		path = path.makeAbsolute();
		return path;
	}

	/**
	 * Returns a list of existing files which will be modified if the classpath changes for the given proeject.
	 * 
	 * @param p
	 *            project
	 * @return list of affected files.
	 * 
	 * @since 1.0.0
	 */
	public static List getFilesAffectedByClasspathChange(IProject p) {
		List result = new ArrayList(2);
		addFileIfExists(p, result, DOT_CLASSPATH);
		addFileIfExists(p, result, DOT_PROJECT);
		return result;
	}

	protected static void addFileIfExists(IProject p, List aList, String filename) {
		IFile aFile = p.getFile(filename);
		if (aFile != null && aFile.exists())
			aList.add(aFile);
	}

	/**
	 * Strip off a leading "/" from each project name in the array, if it has one.
	 * 
	 * @param projecNames
	 * @return array of project names with all leading '/' removed.
	 * 
	 * @since 1.0.0
	 */
	public static String[] getProjectNamesWithoutForwardSlash(String[] projecNames) {
		String[] projNames = new String[projecNames.length];
		List temp = java.util.Arrays.asList(projecNames);
		for (int i = 0; i < temp.size(); i++) {
			String name = (String) (temp.get(i));
			if (name.startsWith("/")) { //$NON-NLS-1$
				projNames[i] = name.substring(1, name.length());
			} else {
				projNames[i] = name;
			}
		}
		return projNames;
	}

	protected static URL createFileURL(IPath path) {
		try {
			return path.toFile().toURL();
		} catch (MalformedURLException e) {
			Logger.getLogger().log(e, Level.WARNING);
			return null;
		}
	}

	/**
	 * Find first newObject that is not in the oldObjects array (using "==").
	 * 
	 * @param oldObjects
	 * @param newObjects
	 * @return first newObject not found in oldObjects, or <code>null</code> if all found.
	 * 
	 * @since 1.0.0
	 */
	public static Object getNewObject(Object[] oldObjects, Object[] newObjects) {
		if (oldObjects != null && newObjects != null && oldObjects.length < newObjects.length) {
			for (int i = 0; i < newObjects.length; i++) {
				boolean found = false;
				Object object = newObjects[i];
				for (int j = 0; j < oldObjects.length; j++) {
					if (oldObjects[j] == object) {
						found = true;
						break;
					}
				}
				if (!found)
					return object;
			}
		}
		if (oldObjects == null && newObjects != null && newObjects.length == 1)
			return newObjects[0];
		return null;
	}

	/**
	 * List of all files in the project.
	 * <p>
	 * Note: A more efficient way to do this is to use {@link IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)}
	 * 
	 * @param 1.0.0
	 * @return list of files in the project
	 * 
	 * @see IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
	 * @since 1.0.0
	 */
	public static List getAllProjectFiles(IProject project) {
		List result = new ArrayList();
		if (project == null)
			return result;
		try {
			result = collectFiles(project.members(), result);
		} catch (CoreException e) {
		}
		return result;
	}

	private static List collectFiles(IResource[] members, List result) throws CoreException {
		// recursively collect files for the given members
		for (int i = 0; i < members.length; i++) {
			IResource res = members[i];
			if (res instanceof IFolder) {
				collectFiles(((IFolder) res).members(), result);
			} else if (res instanceof IFile) {
				result.add(res);
			}
		}
		return result;
	}

	/**
	 * Get the project.
	 * 
	 * @param projectName
	 * @return a IProject given the projectName
	 * @since 1.0.0
	 */
	public static IProject getProject(String projectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	/**
	 * Return whether the given builder name is attached to the project.
	 * 
	 * @param project
	 * @param builderName
	 * @return <code>true</code> if builder name is attached to the project.
	 * 
	 * @since 1.0.0
	 */
	public static boolean hasBuilder(IProject project, String builderName) {
		try {
			ICommand[] builders = project.getDescription().getBuildSpec();
			for (int i = 0; i < builders.length; i++) {
				ICommand builder = builders[i];
				if (builder != null) {
					if (builder.getBuilderName().equals(builderName))
						return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
}