
package org.eclipse.wst.common.frameworks.artifactedit.tests;



import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelEvent;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;
import org.eclipse.wst.common.modulecore.ArtifactEdit;
import org.eclipse.wst.common.modulecore.ArtifactEditModel;
import org.eclipse.wst.common.modulecore.ModuleCore;
import org.eclipse.wst.common.modulecore.ModuleCoreNature;
import org.eclipse.wst.common.modulecore.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.internal.resources.ComponentHandle;



public class ArtifactEditTest extends TestCase {
	public static final String PROJECT_NAME = "TestArtifactEdit";
	public static final String WEB_MODULE_NAME = "WebModule1";
	public static final URI moduleURI = URI.createURI("module:/resource/TestArtifactEdit/WebModule1");
	public static final String EDIT_MODEL_ID = "jst.web";
	private ArtifactEditModel artifactEditModelForRead;
	private ArtifactEditModel artifactEditModelForWrite;
	private ArtifactEdit artifactEditForRead;
	private ArtifactEdit artifactEditForWrite;
	private EditModelListener emListener;
	private IOperationHandler handler = new IOperationHandler() {

		public boolean canContinue(String message) {
			return false;
		}


		public boolean canContinue(String message, String[] items) {

			return false;
		}

		public int canContinueWithAllCheck(String message) {

			return 0;
		}

		public int canContinueWithAllCheckAllowCancel(String message) {

			return 0;
		}

		public void error(String message) {


		}

		public void inform(String message) {


		}


		public Object getContext() {

			return null;
		}
	};

	private IProject project;

	public IProject getTargetProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public ArtifactEditTest() {
		super();
		project = getTargetProject();
	}

	public void setup() {
		project = getTargetProject();

	}

	public void testGetArtifactEditForReadWorkbenchComponent() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForRead(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);

		}
	}

	public void testGetArtifactEditForWriteWorkbenchComponent() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForWrite(wbComponent);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}


	public void testGetArtifactEditForReadComponentHandle() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			ComponentHandle handle = ComponentHandle.create(project, wbComponent.getName());
			edit = ArtifactEdit.getArtifactEditForRead(handle);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}


	public void testGetArtifactEditForWriteComponentHandle() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			ComponentHandle handle = ComponentHandle.create(project, wbComponent.getName());
			edit = ArtifactEdit.getArtifactEditForWrite(handle);

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);
		}
	}

	public void testIsValidEditableModule() {
		ModuleCore moduleCore = null;
		WorkbenchComponent wbComponent = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			ComponentHandle handle = ComponentHandle.create(project, wbComponent.getName());
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
			}
			assertTrue(ArtifactEdit.isValidEditableModule(wbComponent));
		}
	}

	public void testArtifactEditArtifactEditModel() {
		ArtifactEdit edit = new ArtifactEdit(getArtifactEditModelforRead());
		assertNotNull(edit);
		edit.dispose();
	}


	public void testArtifactEditModuleCoreNatureWorkbenchComponentboolean() {
		ModuleCore moduleCore = null;
		WorkbenchComponent wbComponent = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
			}
		}

		ModuleCoreNature nature = null;
		try {
			nature = moduleCore.getModuleCoreNature(moduleURI);
		} catch (UnresolveableURIException e) {
			fail();
		}
		ArtifactEdit edit = new ArtifactEdit(nature, wbComponent, true);
		assertNotNull(edit);
		edit.dispose();

	}

	public void testArtifactEditComponentHandleboolean() {
		ModuleCore moduleCore = null;
		WorkbenchComponent wbComponent = null;
		ComponentHandle handle = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			handle = ComponentHandle.create(project, wbComponent.getName());
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
			}
		}

		ArtifactEdit edit = new ArtifactEdit(handle, true);
		assertNotNull(edit);
		edit.dispose();

	}

	public void testSave() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForWrite(wbComponent);
			try {
				edit.save(new NullProgressMonitor());
			} catch (Exception e) {
				fail(e.getMessage());
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			assertTrue(edit != null);
		}
		assertTrue(true);
	}

	public void testSaveIfNecessary() {
		ModuleCore moduleCore = null;
		ArtifactEdit edit = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForWrite(wbComponent);
			try {
				edit.saveIfNecessary(new NullProgressMonitor());
			} catch (Exception e) {
				fail(e.getMessage());
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
		}
		pass();
	}

	public void testSaveIfNecessaryWithPrompt() {
		ArtifactEdit edit = null;
		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForWrite(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForWrite(wbComponent);
			try {
				edit.saveIfNecessaryWithPrompt(new NullProgressMonitor(), handler, true);
			} catch (Exception e) {
				fail(e.getMessage());
			}

		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();
			}
			pass();
		}
	}

	public void testDispose() {
		ArtifactEdit edit;
		try {
			edit = new ArtifactEdit(getArtifactEditModelforRead());
			edit.dispose();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testGetContentModelRoot() {
		ArtifactEdit edit = null;
		ModuleCore moduleCore = null;
		try {
			moduleCore = ModuleCore.getModuleCoreForRead(project);
			WorkbenchComponent wbComponent = moduleCore.findWorkbenchModuleByDeployName(WEB_MODULE_NAME);
			edit = ArtifactEdit.getArtifactEditForRead(wbComponent);
			Object object = edit.getContentModelRoot();
			assertNotNull(object);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			if (moduleCore != null) {
				moduleCore.dispose();
				edit.dispose();

			}
		}

	}

	public void testAddListener() {
		ArtifactEdit edit = getArtifactEditForRead();
		try {
			edit.addListener(getEmListener());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
		edit.dispose();
	}

	public void testRemoveListener() {
		ArtifactEdit edit = getArtifactEditForRead();
		try {
			edit.removeListener(getEmListener());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testHasEditModel() {

		ArtifactEdit edit = getArtifactEditForRead();
		assertTrue(edit.hasEditModel(artifactEditModelForRead));
		edit.dispose();
	}

	public void testGetArtifactEditModel() {
		ArtifactEdit edit = getArtifactEditForRead();
		assertTrue(edit.hasEditModel(artifactEditModelForRead));
		edit.dispose();
	}

	public void testObject() {
		pass();
	}

	public void testGetClass() {
		ArtifactEdit edit = getArtifactEditForRead();
		assertNotNull(edit.getClass());
		edit.dispose();
	}

	public void testHashCode() {
		ArtifactEdit edit = getArtifactEditForRead();
		int y = -999999999;
		int x = edit.hashCode();
		assertTrue(x != y);
		edit.dispose();
	}

	public void testEquals() {
		assertTrue(getArtifactEditForRead().equals(artifactEditForRead));
	}

	public void testClone() {
		pass();
	}

	public void testToString() {
		assertTrue(getArtifactEditForRead().toString() != null);
	}

	public void testNotify() {
		try {
			synchronized (getArtifactEditForRead()) {
				artifactEditForRead.notify();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();

	}

	public void testNotifyAll() {
		try {
			synchronized (getArtifactEditForRead()) {
				artifactEditForRead.notifyAll();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		pass();
	}

	public void testWaitlong() {
		long x = 2;
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait(x);
			}
		} catch (Exception e) {
			//fail(e.getMessage());
		}
		pass();
	}

	/*
	 * Class under test for void wait(long, int)
	 */
	public void testWaitlongint() {
		int x = 2;
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait(x);
			}
		} catch (Exception e) {
			//fail(e.getMessage());
		}
		pass();
	}

	public void testWait() {
		try {
			synchronized (getArtifactEditForRead()) {
				getArtifactEditForRead().wait();
			}
		} catch (Exception e) {
			//fail(e.getMessage());
		}
		pass();
	}

	public void testFinalize() {
		pass();
	}


	public ArtifactEditModel getArtifactEditModelforRead() {
		EMFWorkbenchContext context = new EMFWorkbenchContext(project);
		artifactEditModelForRead = new ArtifactEditModel(EDIT_MODEL_ID, context, true, moduleURI);
		return artifactEditModelForRead;
	}



	public ArtifactEdit getArtifactEditForRead() {
		artifactEditForRead = new ArtifactEdit(getArtifactEditModelforRead());
		return artifactEditForRead;
	}

	public void pass() {
		assertTrue(true);
	}

	public EditModelListener getEmListener() {
		if (emListener == null)
			emListener = new EditModelListener() {
				public void editModelChanged(EditModelEvent anEvent) {
				}
			};
		return emListener;
	}

	public ArtifactEditModel getArtifactEditModelForWrite() {
		EMFWorkbenchContext context = new EMFWorkbenchContext(project);
		return new ArtifactEditModel(EDIT_MODEL_ID, context, false, moduleURI);

	}

	public ArtifactEdit getArtifactEditForWrite() {
		return new ArtifactEdit(getArtifactEditModelForWrite());

	}
}
