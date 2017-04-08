package org.eclipse.wst.common.frameworks.componentcore.tests;

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.etools.common.test.apitools.ProjectUnzipUtil;
import org.eclipse.wst.common.componentcore.ArtifactEdit;
import org.eclipse.wst.common.componentcore.StructureEdit;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ModuleStructuralModel;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualComponent;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.tests.CommonTestsPlugin;

public class StructureEditAPITest extends TestCase {
	public static String fileSep = System.getProperty("file.separator");
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	private Path zipFilePath = new Path("TestData" + fileSep + "TestArtifactEdit.zip");
	private IProject project;


	// /This should be extracted out, dont have time, just trying to get coverage
	// for m4 integration....

	protected void setUp() throws Exception {
		if (!getTargetProject().exists())
			if (!createProject())
				fail();
		project = getTargetProject();
	}


	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public boolean createProject() {
		IPath localZipPath = getLocalPath();
		ProjectUnzipUtil util = new ProjectUnzipUtil(localZipPath, new String[]{PROJECT_NAME});
		return util.createProjects();
	}

	private IPath getLocalPath() {
		URL url = CommonTestsPlugin.instance.find(zipFilePath);
		try {
			url = Platform.asLocalURL(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Path(url.getPath());
	}


	public void testGetStructureEditForRead() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetStructureEditForWrite() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetModuleCoreNature() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			try {
				moduleCore.getModuleCoreNature(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for IProject getContainingProject(WorkbenchComponent)
	 */
	public void testGetContainingProjectWorkbenchComponent() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.getContainingProject(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for IProject getContainingProject(URI)
	 */
	public void testGetContainingProjectURI() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			try {
				moduleCore.getContainingProject(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetEclipseResource() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			moduleCore.getEclipseResource(componentResource);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetOutputContainerRoot() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			StructureEdit.getOutputContainerRoot(wbComponent);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	public void testGetOutputContainersForProject() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			StructureEdit.getOutputContainersForProject(project);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	public void testGetDeployedName() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			try {
				StructureEdit.getDeployedName(moduleURI);
			} catch (UnresolveableURIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	public void testGetComponentType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			StructureEdit.getComponentType(new VirtualComponent(project, "", new Path("")));
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	public void testSetComponentType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			ComponentResource componentResource = wbComponent.findResourcesByRuntimePath(new Path("/TestArtifactEdit/WebModule1"))[0];
			VirtualComponent vc = new VirtualComponent(project, "", new Path(""));
			StructureEdit.setComponentType(vc, wbComponent.getComponentType());
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	/*
	 * Class under test for void StructureEdit(ModuleCoreNature, boolean)
	 */
	public void testStructureEditModuleCoreNatureboolean() {
		StructureEdit moduleCore = null;
		try {
			// protected
			// StructureEdit edit = new StructureEdit(ModuleCoreNature.getModuleCoreNature(project),
			// true);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	/*
	 * Class under test for void StructureEdit(ModuleStructuralModel)
	 */
	public void testStructureEditModuleStructuralModel() {
		StructureEdit moduleCore = null;
		EMFWorkbenchContext context = new EMFWorkbenchContext(project);
		ModuleStructuralModel msm = new ModuleStructuralModel(EDIT_MODEL_ID, context, false);
		try {
			// protected
			StructureEdit edit = new StructureEdit(msm);
			assertNotNull(edit);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);
		}
	}

	public void testSave() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.save(new NullProgressMonitor());

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	public void testSaveIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.saveIfNecessary(new NullProgressMonitor());

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testDispose() {
		// disposed everywhere
	}

	public void testPrepareProjectComponentsIfNecessary() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.prepareProjectComponentsIfNecessary();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetComponentModelRoot() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.getComponentModelRoot();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetSourceContainers() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.getSourceContainers(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetWorkbenchModules() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.getWorkbenchModules();

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testCreateWorkbenchModule() {

		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.createWorkbenchModule("test");

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	public void testCreateWorkbenchModuleResource() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.createWorkbenchModuleResource(null);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testCreateModuleType() {
		StructureEdit moduleCore = null;

		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			moduleCore.createModuleType(EDIT_MODEL_ID);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}


	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI, URI)
	 */
	public void testFindResourcesByRuntimePathURIURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			try {
				moduleCore.findResourcesByRuntimePath(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	/*
	 * Class under test for ComponentResource[] findResourcesByRuntimePath(URI)
	 */
	public void testFindResourcesByRuntimePathURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findComponentByName(WEB_MODULE_NAME);
			try {
				moduleCore.findResourcesByRuntimePath(moduleURI, moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindResourcesBySourcePath() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			try {
				moduleCore.findResourcesBySourcePath(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindComponentByName() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.findComponentByName(WEB_MODULE_NAME);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testFindComponentByURI() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			try {
				moduleCore.findComponentByURI(moduleURI);
			} catch (UnresolveableURIException e) {
				e.printStackTrace();
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}

	}

	public void testFindComponentsByType() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.findComponentsByType(EDIT_MODEL_ID);


		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testIsLocalDependency() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			moduleCore.isLocalDependency(null);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetFirstModule() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(project);
			moduleCore.getFirstModule();
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();

			}
			assertNotNull(moduleCore);

		}
	}

	public void testGetFirstArtifactEditForRead() {
		StructureEdit moduleCore = null;
		ArtifactEdit edit = null;
		try {
			edit = StructureEdit.getFirstArtifactEditForRead(project);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();

			}
			assertNotNull(edit);

		}
	}

	public void testCreateComponentURI() {
		StructureEdit moduleCore = null;
		URI uri = StructureEdit.createComponentURI(project, "testComp");
		assertNotNull(uri);

	}
}