/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleCorePackage.java,v 1.11 2005/02/03 14:42:15 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCoreFactory
 * @generated
 */
public interface ModuleCorePackage extends EPackage{
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "modulecore"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "modulecore.xmi"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.eclipse.wst.common.modulecore"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ModuleCorePackage eINSTANCE = org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl <em>Deploy Scheme</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getDeployScheme()
	 * @generated
	 */
	int DEPLOY_SCHEME = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Server Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME__SERVER_TARGET = 1;

	/**
	 * The number of structural features of the the '<em>Deploy Scheme</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPLOY_SCHEME_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl <em>Workbench Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchModule()
	 * @generated
	 */
	int WORKBENCH_MODULE = 1;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__HANDLE = 0;

	/**
	 * The feature id for the '<em><b>Deployed Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__DEPLOYED_NAME = 1;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__RESOURCES = 2;

	/**
	 * The feature id for the '<em><b>Module Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__MODULE_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE__MODULES = 4;

	/**
	 * The number of structural features of the the '<em>Workbench Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl <em>Workbench Module Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchModuleResource()
	 * @generated
	 */
	int WORKBENCH_MODULE_RESOURCE = 2;

	/**
	 * The feature id for the '<em><b>Source Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_RESOURCE__SOURCE_PATH = 0;

	/**
	 * The feature id for the '<em><b>Deployed Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH = 1;

	/**
	 * The feature id for the '<em><b>Exclusions</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_RESOURCE__EXCLUSIONS = 2;

	/**
	 * The feature id for the '<em><b>Module</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_RESOURCE__MODULE = 3;

	/**
	 * The number of structural features of the the '<em>Workbench Module Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_MODULE_RESOURCE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.WorkbenchApplicationImpl <em>Workbench Application</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.WorkbenchApplicationImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getWorkbenchApplication()
	 * @generated
	 */
	int WORKBENCH_APPLICATION = 3;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__HANDLE = WORKBENCH_MODULE__HANDLE;

	/**
	 * The feature id for the '<em><b>Deployed Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__DEPLOYED_NAME = WORKBENCH_MODULE__DEPLOYED_NAME;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__RESOURCES = WORKBENCH_MODULE__RESOURCES;

	/**
	 * The feature id for the '<em><b>Module Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__MODULE_TYPE = WORKBENCH_MODULE__MODULE_TYPE;

	/**
	 * The feature id for the '<em><b>Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__MODULES = WORKBENCH_MODULE__MODULES;

	/**
	 * The feature id for the '<em><b>Deploy Scheme</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION__DEPLOY_SCHEME = WORKBENCH_MODULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the the '<em>Workbench Application</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WORKBENCH_APPLICATION_FEATURE_COUNT = WORKBENCH_MODULE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ModuleTypeImpl <em>Module Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleTypeImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getModuleType()
	 * @generated
	 */
	int MODULE_TYPE = 4;

	/**
	 * The feature id for the '<em><b>Metadata Resources</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_TYPE__METADATA_RESOURCES = 0;

	/**
	 * The feature id for the '<em><b>Module Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_TYPE__MODULE_TYPE_ID = 1;

	/**
	 * The number of structural features of the the '<em>Module Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl <em>Project Modules</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getProjectModules()
	 * @generated
	 */
	int PROJECT_MODULES = 5;

	/**
	 * The feature id for the '<em><b>Project Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_MODULES__PROJECT_NAME = 0;

	/**
	 * The feature id for the '<em><b>Workbench Applications</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_MODULES__WORKBENCH_APPLICATIONS = 1;

	/**
	 * The feature id for the '<em><b>Workbench Modules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_MODULES__WORKBENCH_MODULES = 2;

	/**
	 * The feature id for the '<em><b>Deployment Schemes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_MODULES__DEPLOYMENT_SCHEMES = 3;

	/**
	 * The number of structural features of the the '<em>Project Modules</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_MODULES_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.wst.common.modulecore.impl.DependentModuleImpl <em>Dependent Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.wst.common.modulecore.impl.DependentModuleImpl
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getDependentModule()
	 * @generated
	 */
	int DEPENDENT_MODULE = 6;

	/**
	 * The feature id for the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENT_MODULE__HANDLE = 0;

	/**
	 * The feature id for the '<em><b>Deployed Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENT_MODULE__DEPLOYED_PATH = 1;

	/**
	 * The number of structural features of the the '<em>Dependent Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENT_MODULE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '<em>URI</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.URI
	 * @see org.eclipse.wst.common.modulecore.impl.ModuleCorePackageImpl#getURI()
	 * @generated
	 */
	int URI = 7;


	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.DeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme
	 * @generated
	 */
	EClass getDeployScheme();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DeployScheme#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme#getType()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget <em>Server Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Server Target</em>'.
	 * @see org.eclipse.wst.common.modulecore.DeployScheme#getServerTarget()
	 * @see #getDeployScheme()
	 * @generated
	 */
	EAttribute getDeployScheme_ServerTarget();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchModule <em>Workbench Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Module</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule
	 * @generated
	 */
	EClass getWorkbenchModule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getHandle()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EAttribute getWorkbenchModule_Handle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getDeployedName <em>Deployed Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deployed Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getDeployedName()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EAttribute getWorkbenchModule_DeployedName();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModules <em>Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Modules</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getModules()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_Modules();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getResources()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_Resources();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType <em>Module Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Module Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModule#getModuleType()
	 * @see #getWorkbenchModule()
	 * @generated
	 */
	EReference getWorkbenchModule_ModuleType();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource <em>Workbench Module Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Module Resource</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource
	 * @generated
	 */
	EClass getWorkbenchModuleResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getSourcePath <em>Source Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getSourcePath()
	 * @see #getWorkbenchModuleResource()
	 * @generated
	 */
	EAttribute getWorkbenchModuleResource_SourcePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getDeployedPath <em>Deployed Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deployed Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getDeployedPath()
	 * @see #getWorkbenchModuleResource()
	 * @generated
	 */
	EAttribute getWorkbenchModuleResource_DeployedPath();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getExclusions <em>Exclusions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Exclusions</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getExclusions()
	 * @see #getWorkbenchModuleResource()
	 * @generated
	 */
	EAttribute getWorkbenchModuleResource_Exclusions();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getModule <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Module</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchModuleResource#getModule()
	 * @see #getWorkbenchModuleResource()
	 * @generated
	 */
	EReference getWorkbenchModuleResource_Module();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.WorkbenchApplication <em>Workbench Application</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Workbench Application</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchApplication
	 * @generated
	 */
	EClass getWorkbenchApplication();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.wst.common.modulecore.WorkbenchApplication#getDeployScheme <em>Deploy Scheme</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Deploy Scheme</em>'.
	 * @see org.eclipse.wst.common.modulecore.WorkbenchApplication#getDeployScheme()
	 * @see #getWorkbenchApplication()
	 * @generated
	 */
	EReference getWorkbenchApplication_DeployScheme();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ModuleType <em>Module Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module Type</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleType
	 * @generated
	 */
	EClass getModuleType();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.wst.common.modulecore.ModuleType#getMetadataResources <em>Metadata Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Metadata Resources</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleType#getMetadataResources()
	 * @see #getModuleType()
	 * @generated
	 */
	EAttribute getModuleType_MetadataResources();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId <em>Module Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Module Type Id</em>'.
	 * @see org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId()
	 * @see #getModuleType()
	 * @generated
	 */
	EAttribute getModuleType_ModuleTypeId();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.ProjectModules <em>Project Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project Modules</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectModules
	 * @generated
	 */
	EClass getProjectModules();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.ProjectModules#getProjectName <em>Project Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Project Name</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectModules#getProjectName()
	 * @see #getProjectModules()
	 * @generated
	 */
	EAttribute getProjectModules_ProjectName();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchApplications <em>Workbench Applications</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Workbench Applications</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchApplications()
	 * @see #getProjectModules()
	 * @generated
	 */
	EReference getProjectModules_WorkbenchApplications();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchModules <em>Workbench Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Workbench Modules</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectModules#getWorkbenchModules()
	 * @see #getProjectModules()
	 * @generated
	 */
	EReference getProjectModules_WorkbenchModules();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.wst.common.modulecore.ProjectModules#getDeploymentSchemes <em>Deployment Schemes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Deployment Schemes</em>'.
	 * @see org.eclipse.wst.common.modulecore.ProjectModules#getDeploymentSchemes()
	 * @see #getProjectModules()
	 * @generated
	 */
	EReference getProjectModules_DeploymentSchemes();

	/**
	 * Returns the meta object for class '{@link org.eclipse.wst.common.modulecore.DependentModule <em>Dependent Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependent Module</em>'.
	 * @see org.eclipse.wst.common.modulecore.DependentModule
	 * @generated
	 */
	EClass getDependentModule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Handle</em>'.
	 * @see org.eclipse.wst.common.modulecore.DependentModule#getHandle()
	 * @see #getDependentModule()
	 * @generated
	 */
	EAttribute getDependentModule_Handle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath <em>Deployed Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deployed Path</em>'.
	 * @see org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath()
	 * @see #getDependentModule()
	 * @generated
	 */
	EAttribute getDependentModule_DeployedPath();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.emf.common.util.URI <em>URI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>URI</em>'.
	 * @see org.eclipse.emf.common.util.URI
	 * @model instanceClass="org.eclipse.emf.common.util.URI"
	 * @generated
	 */
	EDataType getURI();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ModuleCoreFactory getModuleCoreFactory();

} //ModuleCorePackage
