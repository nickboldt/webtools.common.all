/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCorePackageImpl.java,v 1.7 2005/01/26 16:48:35 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.wst.common.modulecore.DeployScheme;
import org.eclipse.wst.common.modulecore.DeployedModule;
import org.eclipse.wst.common.modulecore.IModuleType;
import org.eclipse.wst.common.modulecore.ModuleCoreFactory;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchApplication;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModuleCorePackageImpl extends EPackageImpl implements ModuleCorePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deploySchemeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass workbenchModuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass workbenchModuleResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass workbenchApplicationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iModuleTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass projectModulesEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deployedModuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uriEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ModuleCorePackageImpl() {
		super(eNS_URI, ModuleCoreFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ModuleCorePackage init() {
		if (isInited) return (ModuleCorePackage)EPackage.Registry.INSTANCE.getEPackage(ModuleCorePackage.eNS_URI);

		// Obtain or create and register package
		ModuleCorePackageImpl theModuleCorePackage = (ModuleCorePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ModuleCorePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ModuleCorePackageImpl());

		isInited = true;

		// Create package meta-data objects
		theModuleCorePackage.createPackageContents();

		// Initialize created meta-data
		theModuleCorePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theModuleCorePackage.freeze();

		return theModuleCorePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployScheme() {
		return deploySchemeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployScheme_Type() {
		return (EAttribute)deploySchemeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDeployScheme_ServerTarget() {
		return (EAttribute)deploySchemeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchModule() {
		return workbenchModuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModule_Handle() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModule_DeployedPath() {
		return (EAttribute)workbenchModuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_Modules() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_Resources() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchModule_ModuleType() {
		return (EReference)workbenchModuleEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchModuleResource() {
		return workbenchModuleResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_SourcePath() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_DeployedPath() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getWorkbenchModuleResource_Exclusions() {
		return (EAttribute)workbenchModuleResourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWorkbenchApplication() {
		return workbenchApplicationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWorkbenchApplication_DeployScheme() {
		return (EReference)workbenchApplicationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIModuleType() {
		return iModuleTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIModuleType_Root() {
		return (EAttribute)iModuleTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIModuleType_MetadataResources() {
		return (EAttribute)iModuleTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getIModuleType_TypeName() {
		return (EAttribute)iModuleTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProjectModules() {
		return projectModulesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProjectModules_ProjectName() {
		return (EAttribute)projectModulesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProjectModules_WorkbenchApplications() {
		return (EReference)projectModulesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getProjectModules_WorkbenchModules() {
		return (EReference)projectModulesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeployedModule() {
		return deployedModuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getURI() {
		return uriEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ModuleCoreFactory getModuleCoreFactory() {
		return (ModuleCoreFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		deploySchemeEClass = createEClass(DEPLOY_SCHEME);
		createEAttribute(deploySchemeEClass, DEPLOY_SCHEME__TYPE);
		createEAttribute(deploySchemeEClass, DEPLOY_SCHEME__SERVER_TARGET);

		workbenchModuleEClass = createEClass(WORKBENCH_MODULE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__HANDLE);
		createEAttribute(workbenchModuleEClass, WORKBENCH_MODULE__DEPLOYED_PATH);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__MODULES);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__RESOURCES);
		createEReference(workbenchModuleEClass, WORKBENCH_MODULE__MODULE_TYPE);

		workbenchModuleResourceEClass = createEClass(WORKBENCH_MODULE_RESOURCE);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__SOURCE_PATH);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH);
		createEAttribute(workbenchModuleResourceEClass, WORKBENCH_MODULE_RESOURCE__EXCLUSIONS);

		workbenchApplicationEClass = createEClass(WORKBENCH_APPLICATION);
		createEReference(workbenchApplicationEClass, WORKBENCH_APPLICATION__DEPLOY_SCHEME);

		iModuleTypeEClass = createEClass(IMODULE_TYPE);
		createEAttribute(iModuleTypeEClass, IMODULE_TYPE__ROOT);
		createEAttribute(iModuleTypeEClass, IMODULE_TYPE__METADATA_RESOURCES);
		createEAttribute(iModuleTypeEClass, IMODULE_TYPE__TYPE_NAME);

		projectModulesEClass = createEClass(PROJECT_MODULES);
		createEAttribute(projectModulesEClass, PROJECT_MODULES__PROJECT_NAME);
		createEReference(projectModulesEClass, PROJECT_MODULES__WORKBENCH_APPLICATIONS);
		createEReference(projectModulesEClass, PROJECT_MODULES__WORKBENCH_MODULES);

		deployedModuleEClass = createEClass(DEPLOYED_MODULE);

		// Create data types
		uriEDataType = createEDataType(URI);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Add supertypes to classes
		workbenchApplicationEClass.getESuperTypes().add(this.getWorkbenchModule());

		// Initialize classes and features; add operations and parameters
		initEClass(deploySchemeEClass, DeployScheme.class, "DeployScheme", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeployScheme_Type(), ecorePackage.getEString(), "type", null, 0, 1, DeployScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeployScheme_ServerTarget(), ecorePackage.getEString(), "serverTarget", null, 0, 1, DeployScheme.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchModuleEClass, WorkbenchModule.class, "WorkbenchModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWorkbenchModule_Handle(), this.getURI(), "handle", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModule_DeployedPath(), this.getURI(), "deployedPath", null, 0, 1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_Modules(), this.getWorkbenchModule(), null, "modules", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_Resources(), this.getWorkbenchModuleResource(), null, "resources", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWorkbenchModule_ModuleType(), this.getIModuleType(), null, "moduleType", null, 0, -1, WorkbenchModule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchModuleResourceEClass, WorkbenchModuleResource.class, "WorkbenchModuleResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWorkbenchModuleResource_SourcePath(), this.getURI(), "sourcePath", null, 0, 1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModuleResource_DeployedPath(), this.getURI(), "deployedPath", null, 0, 1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWorkbenchModuleResource_Exclusions(), this.getURI(), "exclusions", null, 0, -1, WorkbenchModuleResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(workbenchApplicationEClass, WorkbenchApplication.class, "WorkbenchApplication", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWorkbenchApplication_DeployScheme(), this.getDeployScheme(), null, "deployScheme", null, 1, 1, WorkbenchApplication.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(iModuleTypeEClass, IModuleType.class, "IModuleType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIModuleType_Root(), this.getURI(), "root", null, 0, 1, IModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIModuleType_MetadataResources(), this.getURI(), "metadataResources", null, 0, -1, IModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIModuleType_TypeName(), ecorePackage.getEString(), "typeName", null, 0, 1, IModuleType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(projectModulesEClass, ProjectModules.class, "ProjectModules", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProjectModules_ProjectName(), ecorePackage.getEString(), "projectName", null, 0, 1, ProjectModules.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProjectModules_WorkbenchApplications(), this.getWorkbenchApplication(), null, "workbenchApplications", null, 0, -1, ProjectModules.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProjectModules_WorkbenchModules(), this.getWorkbenchModule(), null, "workbenchModules", null, 0, -1, ProjectModules.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deployedModuleEClass, DeployedModule.class, "DeployedModule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Initialize data types
		initEDataType(uriEDataType, org.eclipse.emf.common.util.URI.class, "URI", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ModuleCorePackageImpl
