package org.eclipse.wst.common.modulecore.internal.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.common.internal.emf.resource.GenericTranslator;
import org.eclipse.wst.common.internal.emf.resource.IDTranslator;
import org.eclipse.wst.common.internal.emf.resource.RootTranslator;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;


public class WTPModulesTranslator extends RootTranslator implements WTPModulesXmlMapperI{
	public static WTPModulesTranslator INSTANCE = new WTPModulesTranslator();
	private static Translator[] children;
	private static final ModuleCorePackage MODULE_CORE_PKG = ModuleCorePackage.eINSTANCE;
	/**
	 * @param domNameAndPath
	 * @param eClass
	 */
	public WTPModulesTranslator() {
		super(PROJECT_MODULES, ModuleCorePackage.eINSTANCE.getProjectComponents());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#getChildren(java.lang.Object, int)
	 */
	public Translator[] getChildren(Object target, int versionID) {
		if(children == null)
			children = createWTPModulesTranslator();
		return children;
	}
	
	private static Translator[] createWTPModulesTranslator() {
		return new Translator[] {
				IDTranslator.INSTANCE,
				createWBModuleTranslator(MODULE_CORE_PKG.getProjectComponents_Components())
		};
	}

	/**
	 * @return
	 */
	private static Translator createWBModuleTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBMODULE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			//new Translator(HANDLE, MODULE_CORE_PKG.getWorkbenchComponent_Handle(), DOM_ATTRIBUTE), REMOVED SINCE HANDLE SHOULD NOW BE DERIVED -MDE
			new Translator(DEPLOY_NAME, MODULE_CORE_PKG.getWorkbenchComponent_Name(), DOM_ATTRIBUTE), 
			createModuleTypeTranslator(MODULE_CORE_PKG.getWorkbenchComponent_ComponentType()),
			createWBResourceTranslator(MODULE_CORE_PKG.getWorkbenchComponent_Resources()),
			createDependentModuleTranslator(MODULE_CORE_PKG.getWorkbenchComponent_ReferencedComponents())
		});
		return result;
	}
	private static Translator createModuleTypeTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(MODULE_TYPE, afeature);
		result.setChildren(new Translator[] {			
			new Translator(MODULE_TYPE_ID, MODULE_CORE_PKG.getComponentType_ModuleTypeId(), DOM_ATTRIBUTE),
			new Translator(META_RESOURCES, MODULE_CORE_PKG.getComponentType_MetadataResources()),
			new Translator(MODULE_TYPE_VERSION, MODULE_CORE_PKG.getComponentType_Version()),
			createPropertiesTranslator(MODULE_CORE_PKG.getComponentType_Properties())
			
		});
		return result;
	}
	
	private static Translator createPropertiesTranslator(EStructuralFeature afeature){
		GenericTranslator result = new GenericTranslator(PROPERTY, afeature);
		result.setChildren(new Translator[] {
			new Translator(PROPERTY_NAME, MODULE_CORE_PKG.getProperty_Name(), DOM_ATTRIBUTE ),
			new Translator(PROPERTY_VALUE, MODULE_CORE_PKG.getProperty_Value(), DOM_ATTRIBUTE ),
	
		});
		return result;		
	}
	
	
	private static Translator createDependentModuleTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(DEPENDENT_MODULE, afeature);
		result.setChildren(new Translator[] { 
			new URITranslator(DEPLOY_PATH, MODULE_CORE_PKG.getReferencedComponent_RuntimePath(), DOM_ATTRIBUTE),
			new URITranslator(HANDLE, MODULE_CORE_PKG.getReferencedComponent_Handle(), DOM_ATTRIBUTE),
			new DependencyTypeTranslator()
		});
		return result;
	}

	private static Translator createWBResourceTranslator(EStructuralFeature afeature) {
		GenericTranslator result = new GenericTranslator(WBRESOURCE, afeature);
		result.setChildren(new Translator[] {
			IDTranslator.INSTANCE,
			new URITranslator(SOURCE_PATH, MODULE_CORE_PKG.getComponentResource_SourcePath(), DOM_ATTRIBUTE),
			new URITranslator(DEPLOY_PATH, MODULE_CORE_PKG.getComponentResource_RuntimePath(), DOM_ATTRIBUTE),
			new Translator(EXCLUSIONS, MODULE_CORE_PKG.getComponentResource_Exclusions())
		});
		return result;
	}

}
