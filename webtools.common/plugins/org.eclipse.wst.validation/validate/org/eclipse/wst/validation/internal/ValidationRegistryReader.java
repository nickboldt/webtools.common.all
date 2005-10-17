/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


/**
 * ValidationRegistryReader is a singleton who reads the plugin registry for Validator extensions.
 * The read is done once (in the constructor), and the list of validators can be accessed by calling
 * "getValidatorMetaData(String)" on this class. The read is triggered by a call from
 * ValidatorManager's loadValidatorMetaData(IProject) method. ValidatorManager delegates the load
 * call to this class, and if this class is null, the singleton is new'ed up, and the registry is
 * read.
 * 
 * No Validator should need to know about this class. The only class which should call
 * ValidationRegistryReader is ValidatorManager.
 * 
 * The Validator itself is initialized in the "initializeValidator" method.
 * 
 * <extension point="org.eclipse.wst.validation.internal.provisional.core.core.validator" id="EJBValidator" name="EJB
 * Validator"> <validator><projectNature id="com.ibm.etools.j2ee.EJBNature" include="false"/>
 * <filter objectClass="org.eclipse.core.resources.IFile" nameFilter = "ejb-jar.xml"/> <filter
 * objectClass="org.eclipse.core.resources.IFile" nameFilter = "*.java"/> <helper
 * class="org.eclipse.wst.validation.internal.provisional.core.core.ejb.workbenchimpl.EJBHelper"/> <run
 * class="org.eclipse.wst.validation.internal.provisional.core.core.ejb.EJBValidator" incremental="false" enabled="false"
 * pass="fast,full" async="false"/> <aggregateValidator class="my.aggregate.ValidatorClass"/>
 * <migrate><validator from="old.class.name" to="new.class.name"/> </migrate> </validator>
 * </extension>
 */
public final class ValidationRegistryReader implements RegistryConstants {
	private static ValidationRegistryReader inst = null;
	private HashMap _validators; // list of all validators registered, with their associated
	// ValidatorMetaData, indexed by project nature id
	private HashMap _indexedValidators; // list of all validators, indexed by validator class name,
	// with the validator's ValidatorMetaData as the value.
	// Needed by the WorkbenchReporter, because sometimes the
	// IValidator is not enough to remove all messages from the
	// task list.
	private Set _defaultEnabledValidators;
	// Since IProject's contents are all instances of IResource, every type filter for a validator
	// must be an instance of IResource. This applies to both the rebuildCache pass and to the
	// validation pass.
	private static final String IRESOURCE = "org.eclipse.core.resources.IResource"; //$NON-NLS-1$

	private static final String UNKNOWN_PROJECT = "UNKNOWN"; //$NON-NLS-1$ // This 'project nature id' is used as a key to get the validators which can run on a project type which hasn't been explicitly filtered in or out by any validator.
	private static final String EXCLUDED_PROJECT = "EXCLUDED"; //$NON-NLS-1$ // This 'project nature id' is used as a key to get the validators which are excluded on certain projects.

	private List _tempList = null; // list for temporary values. Retrieve and use via the

	// getTempList() method.

	/**
	 * The registry is read once - when this class is instantiated.
	 */
	private ValidationRegistryReader() {
		super();

		try {
			_validators = new HashMap();
			_indexedValidators = new HashMap();
			_defaultEnabledValidators = new HashSet();

			// Read the registry and build a map of validators. The key into
			// the map is the IValidator instance and the value is the ValidatorMetaData
			// which describes the IValidator.
			readRegistry();

			// Once all of the validators have been read, the caches of the
			// validators need to be updated.
			buildCache();
		} catch (Throwable exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader()"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}
	}

	/**
	 * Traverse over the list of VMDs which have been added and create copies of it. The copies are
	 * created to increase runtime performance.
	 */
	private void buildCache() {
		Iterator iterator = _indexedValidators.values().iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			buildProjectNatureCache(vmd);
			buildDefaultEnabledCache(vmd);
		}

		// Now add the validators which are configured on all projects,
		// and all projects but X.
		addRemainder();

		// this temporary list isn't needed any more. All of the excluded
		// projects have been added to the project natures which they don't exlcude.
		_validators.remove(EXCLUDED_PROJECT);

		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		if (logger.isLoggingLevel(Level.FINEST)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationRegistryReader.buildCache()"); //$NON-NLS-1$
			entry.setText(debug());
			logger.write(Level.FINEST, entry);
		}

	}

	/**
	 * Build the cache of VMDs which is indexed by project nature ids. If the validator is
	 * registered on all project types, the vmd's project nature filters will be null.
	 */
	private void buildProjectNatureCache(ValidatorMetaData vmd) {
		// Build the cache with the identified project natures in validators'
		// extensions.
		ValidatorNameFilter[] projNatureIds = vmd.getProjectNatureFilters();
		if (projNatureIds == null) {
			// Can run on any project
			add(UNKNOWN_PROJECT, vmd);
		} else {
			boolean noneIncluded = true; // assume that the validator does not include any project
			// natures
			for (int i = 0; i < projNatureIds.length; i++) {
				ValidatorNameFilter pn = projNatureIds[i];
				if (pn.isInclude()) {
					noneIncluded = false;
					add(pn.getNameFilter(), vmd);
				}
			}

			if (noneIncluded) {
				// add it to the list of EXCLUDED projects
				// (that is, a validator which excludes project natures but doesn't
				// explicitly include any. This type of validator runs on any unrecognized (UNKNOWN)
				// projects, but the rest of the cache needs to be built before this is added
				// to the UNKNOWN list. See addExcludedRemainder().
				add(EXCLUDED_PROJECT, vmd);
			}
		}
	}

	/**
	 * Build the list of validators which are enabled by default.
	 */
	private void buildDefaultEnabledCache(ValidatorMetaData vmd) {
		if (vmd == null) {
			return;
		}

		if (vmd.isEnabledByDefault()) {
			_defaultEnabledValidators.add(vmd);
		}
	}

	/**
	 * Add vmd to the list of validators, indexed by validator class name
	 */
	private void add(ValidatorMetaData vmd) {
		if (vmd == null) {
			return;
		}

		_indexedValidators.put(vmd.getValidatorUniqueName(), vmd);
	}

	/*
	 * Some validators can run on any type of project. In order to have a static list, add the "any
	 * project" validators to each "project nature" validators' list. This avoids adding the "any
	 * project" validators to the "project nature" validators at runtime, which results in
	 * performance savings.
	 * 
	 * Some validators run on any type of project but X, where X is an excluded project nature.
	 * Those validators should also be added via this method.
	 */
	private void addRemainder() {
		// First, add all "can-run-on-any-project-type" to every registered project nature type in
		// the cache.
		addAnyRemainder();

		// Then add the "can-run-on-any-project-type-but-X" to every non-X registered project nature
		// type in the cache.
		addExcludedRemainder();
	}

	private void addExcludedRemainder() {
		Set excludedProjVmds = (Set) _validators.get(EXCLUDED_PROJECT);
		if (excludedProjVmds == null) {
			// no excluded project natures
			return;
		}

		Iterator exIterator = excludedProjVmds.iterator();
		while (exIterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) exIterator.next();

			boolean noneIncluded = true; // assume that, by default, if someone explicitly excludes
			// a project nature then they don't include any project
			// natures
			Set keys = _validators.keySet();
			Iterator iterator = keys.iterator();
			while (iterator.hasNext()) {
				String projId = (String) iterator.next();
				if (projId.equals(UNKNOWN_PROJECT) || projId.equals(EXCLUDED_PROJECT)) {
					// Don't add list to a project nature which is excluded or applicable to all.
					continue;
				}

				ValidatorNameFilter filter = vmd.findProjectNature(projId);
				if (filter != null) {
					// Don't add list to itself (filter.isIncluded() == true) or
					// to a list from which it's excluded (filter.isIncluded() == false)
					if (filter.isInclude()) {
						noneIncluded = false;
					}
					continue;
				}

				add(projId, vmd);
			}

			if (noneIncluded) {
				// At this point, the "can-run-on-any-project" becomes
				// "not-excluded-on-these-projects". That is, if the project
				// nature id isn't in the list of _validators, then it isn't
				// included or excluded by any validators, so all validators
				// which can run on any project AND all validators which can
				// run on any but certain excluded projects can run on the
				// given IProject.
				add(UNKNOWN_PROJECT, vmd);
			}
		}
	}

	private void addAnyRemainder() {
		Set anyProjVmds = (Set) _validators.get(UNKNOWN_PROJECT);
		if (anyProjVmds == null) {
			// no validators run on all projects
			return;
		}

		Set keys = _validators.keySet();
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()) {
			String projId = (String) iterator.next();
			if (projId.equals(UNKNOWN_PROJECT) || projId.equals(EXCLUDED_PROJECT)) {
				// Don't add list to itself or to a project nature which is excluded.
				continue;
			}

			add(projId, anyProjVmds);
		}
	}

	private void add(String projectNatureId, Set vmdList) {
		if ((vmdList == null) || (vmdList.size() == 0)) {
			return;
		}

		Set pnVal = createSet(projectNatureId); // whether the validator includes or excludes this
		// project nature id, make sure that an entry is
		// created for it in the table
		pnVal.addAll(vmdList);
		_validators.put(projectNatureId, pnVal);
	}

	private void add(String projectNatureId, ValidatorMetaData vmd) {
		if (vmd == null) {
			return;
		}

		Set pnVal = createSet(projectNatureId); // whether the validator includes or excludes this
		// project nature id, make sure that an entry is
		// created for it in the table
		pnVal.add(vmd);
		_validators.put(projectNatureId, pnVal);
	}

	/**
	 * When a validator's class or helper class cannot be loaded, the vmd calls this method to
	 * disable the validator. The validator will be removed from the preference page, properties
	 * page, and enabled list of any project thereafter validated.
	 */
	public void disableValidator(ValidatorMetaData vmd) {
		_indexedValidators.remove(vmd.getValidatorUniqueName());
		_defaultEnabledValidators.remove(vmd);

		// The whole "on-any-project" and "exclude-this-project-nature" would take
		// a lot of processing time... Instead, traverse the list of proj nature ids,
		// and search the Set of that proj nature id, and remove the vmd if it's in the
		// Set.
		Object[] keys = _validators.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Set value = (Set) _validators.get(key);
			if (value == null) {
				continue;
			}

			if (value.contains(vmd)) {
				value.remove(vmd);
				_validators.put(key, value);
			}
		}
	}

	private Set createSet(String projNature) {
		Set v = (Set) _validators.get(projNature);
		if (v == null) {
			v = new HashSet();
		}
		return v;
	}

	/**
	 * Given an IConfigurationElement, if it has a project nature(s) specified, return the
	 * ValidatorNameFilters which represent those natures. Otherwise return null.
	 * 
	 * A project nature can be specified in plugin.xml to indicate what types of IProjects a
	 * validator can run on.
	 */
	private String[] getAggregateValidatorsNames(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_AGGREGATE_VALIDATORS);
		if (filters.length == 0)
			return null;

		String[] names = new String[filters.length];
		for (int i = 0; i < names.length; i++) {
			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_CLASS);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			names[i] = nameFilter;
		}
		return names;
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, if it has any filter tags, construct the
	 * appropriate ValidatorFilters to represent those tags; else return null.
	 * 
	 * A filter can be specified in plugin.xml to filter out certain resources.
	 */
	private ValidatorFilter[] getFilters(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_FILTER);
		if (filters.length == 0)
			return null;

		ValidatorFilter[] vf = new ValidatorFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			vf[i] = new ValidatorFilter(IRESOURCE);

			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_NAME_FILTER);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			String isCaseSensitive = filters[i].getAttribute(ATT_CASE_SENSITIVE);
			vf[i].setNameFilter(nameFilter, isCaseSensitive);

			String objectClass = filters[i].getAttribute(ATT_OBJECT_CLASS);
			if (objectClass != null) {
				objectClass = objectClass.intern();
			}
			vf[i].setTypeFilter(objectClass);

			String actionFilter = filters[i].getAttribute(ATT_ACTION_FILTER);
			if (actionFilter != null) {
				actionFilter = actionFilter.intern();
			}
			vf[i].setActionFilter(actionFilter);
		}
		return vf;
	}

	public boolean getDependentValidatorValue(IConfigurationElement element) {
		IConfigurationElement[] depValidatorElement = element.getChildren(DEP_VALIDATOR);
		if (depValidatorElement.length == 0)
			return false;
		String depValue = depValidatorElement[0].getAttribute(DEP_VAL_VALUE);
		boolean depBoolValue = (new Boolean(depValue)).booleanValue();
		return depBoolValue;
	}

	/**
	 * Return the name of the marker ID associated with the IValidator.
	 */
	public String getMarkerIdValue(IConfigurationElement element) {
		IConfigurationElement[] markerId = element.getChildren(MARKER_ID);
		if (markerId.length == 0)
			return null;
		return markerId[0].getAttribute(MARKER_ID_VALUE);
	}

	/**
	 * Return the name of the helper class associated with the IValidator.
	 */
	private String getHelperName(IConfigurationElement element) {
		IConfigurationElement[] helpers = element.getChildren(TAG_HELPER_CLASS);
		if (helpers.length == 0)
			return null;

		return helpers[0].getAttribute(ATT_CLASS);
	}

	/* package */static IWorkbenchContext createHelper(IConfigurationElement element, String helperClassName) {
		IWorkbenchContext wh = null;
		try {
			wh = (IWorkbenchContext) element.createExecutableExtension(TAG_HELPER_CLASS);
		} catch (Throwable exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.createHelper(IConfigurationElement, String)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_SYNTAX_NO_HELPER_THROWABLE);
				entry.setTargetException(exc);
				String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SYNTAX_NO_HELPER_THROWABLE), new String[]{helperClassName});
				entry.setText(result);				
				//entry.setTokens(new String[]{helperClassName});
				logger.write(Level.SEVERE, entry);
			}
			return null;
		}
		return wh;
	}

	/* package */static IValidator createValidator(IConfigurationElement element, String validatorClassName) {
		IValidator validator = null;
		try {
			validator = (IValidator) element.createExecutableExtension(TAG_RUN_CLASS);
		} catch (Throwable exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.createValidator(IConfigurationElement, String, String)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_THROWABLE);
				//entry.setTokens(new String[]{validatorClassName});
				String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_THROWABLE), new String[]{validatorClassName});
				entry.setText(result);				
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}

		if (validator == null) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.createValidator(IConfigurationElement, String)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_NULL);
				entry.setTokens(new String[]{validatorClassName});
				logger.write(Level.FINE, entry);
			}
			return null;
		}

		return validator;
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator is
	 * enabled by default.
	 * 
	 * If no enabled attribute is specified, the default, true (i.e., enabled) is returned.
	 */
	private boolean getEnabledByDefault(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String inc = runChildren[0].getAttribute(ATT_ENABLED);
		if (inc == null) {
			return RegistryConstants.ATT_ENABLED_DEFAULT;
		}

		return Boolean.valueOf(inc.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * incremental validation.
	 * 
	 * If no incremental attribute is specified, the default, true (i.e., incremental is supported)
	 * is returned.
	 */
	private boolean getIncremental(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String inc = runChildren[0].getAttribute(ATT_INCREMENTAL);
		if (inc == null) {
			return RegistryConstants.ATT_INCREMENTAL_DEFAULT;
		}

		return Boolean.valueOf(inc.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * full build validation.
	 * 
	 * If no incremental attribute is specified, the default, true (i.e., incremental is supported)
	 * is returned.
	 */
	private boolean getFullBuild(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String fb = runChildren[0].getAttribute(ATT_FULLBUILD);
		if (fb == null) {
			return RegistryConstants.ATT_FULLBUILD_DEFAULT;
		}

		return Boolean.valueOf(fb.trim().toLowerCase()).booleanValue(); // this will return true if,
		// and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be considered
		// "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return whether or not the validator supports
	 * asynchronous validation.
	 * 
	 * If no async attribute is specified, the default, true (i.e., the validator is thread-safe) is
	 * returned.
	 */
	private boolean getAsync(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String async = runChildren[0].getAttribute(ATT_ASYNC);
		if (async == null) {
			return RegistryConstants.ATT_ASYNC_DEFAULT;
		}

		return Boolean.valueOf(async.trim().toLowerCase()).booleanValue(); // this will return true
		// if, and only if, the
		// attribute value is
		// "true". For example,
		// "yes" will be
		// considered "false".
	}

	/**
	 * Given an IConfigurationElement from plugin.xml, return the types of validation passes, as
	 * defined in IRuleGroup, that the validator performs.
	 * 
	 * If no pass attribute is specified, the default, IRuleGroup.PASS_FULL, is returned.
	 */
	private int getRuleGroup(IConfigurationElement element) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		// Don't need to check if runChildren is null or empty, because that was checked in the
		// initializeValidator method.

		String pass = runChildren[0].getAttribute(ATT_RULE_GROUP);
		if (pass == null) {
			return RegistryConstants.ATT_RULE_GROUP_DEFAULT;
		}

		final String COMMA = ","; //$NON-NLS-1$
		StringTokenizer tokenizer = new StringTokenizer(pass, COMMA, false); // false means don't
		// return the comma as
		// part of the string
		int result = 0; // no passes identified
		while (tokenizer.hasMoreTokens()) {
			String nextAction = tokenizer.nextToken().trim();
			if (nextAction.equals(IRuleGroup.PASS_FAST_NAME)) {
				result = result | IRuleGroup.PASS_FAST;
			} else if (nextAction.equals(IRuleGroup.PASS_FULL_NAME)) {
				result = result | IRuleGroup.PASS_FULL;
			}
		}

		if (result == 0) {
			// No recognized passes. Return the default.
			return RegistryConstants.ATT_RULE_GROUP_DEFAULT;
		}

		return result;
	}

	private ValidatorMetaData.MigrationMetaData getMigrationMetaData(IConfigurationElement element, ValidatorMetaData vmd) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_MIGRATE);
		if ((runChildren == null) || (runChildren.length == 0)) {
			return null;
		}

		// Only supposed to be one "migrate" section in a validator, so ignore the rest
		IConfigurationElement migrate = runChildren[0];

		// Now look for the "validator" elements. Zero or more can be specified.
		IConfigurationElement[] migrateChildren = migrate.getChildren(TAG_VALIDATOR);
		if ((migrateChildren == null) || (migrateChildren.length == 0)) {
			return null;
		}

		ValidatorMetaData.MigrationMetaData mmd = vmd.new MigrationMetaData();
		for (int i = 0; i < migrateChildren.length; i++) {
			IConfigurationElement migrateChild = migrateChildren[i];
			String from = migrateChild.getAttribute(ATT_FROM);
			if (from == null) {
				continue;
			}

			String to = migrateChild.getAttribute(ATT_TO);
			if (to == null) {
				continue;
			}
			mmd.addId(from, to);
		}
		return mmd;
	}

	/**
	 * Given an IConfigurationElement, if it has a project nature(s) specified, return the
	 * ValidatorNameFilters which represent those natures. Otherwise return null.
	 * 
	 * A project nature can be specified in plugin.xml to indicate what types of IProjects a
	 * validator can run on.
	 */
	private ValidatorNameFilter[] getProjectNatureFilters(IConfigurationElement element) {
		IConfigurationElement[] filters = element.getChildren(TAG_PROJECT_NATURE);
		if (filters.length == 0) {
			return null;
		}

		ValidatorNameFilter[] vf = new ValidatorNameFilter[filters.length];
		for (int i = 0; i < filters.length; i++) {
			vf[i] = new ValidatorNameFilter();
			// In order to speed up our String comparisons, load these
			// names into Java's constants space. This way, we'll be able to
			// use pointer comparison instead of the traditional
			// character-by-character comparison. Since these names should
			// never be set by anyone other than this class, and this class
			// sets them only once, it is safe to declare these Strings
			// constants.
			//
			// To load a String into the constants space, call intern() on the String.
			//
			String nameFilter = filters[i].getAttribute(ATT_ID);
			if (nameFilter != null) {
				nameFilter = nameFilter.intern();
			}
			vf[i].setNameFilter(nameFilter);

			String include = filters[i].getAttribute(ATT_INCLUDE);
			vf[i].setInclude(include);
		}
		return vf;
	}

	/**
	 * Returns the singleton ValidationRegistryReader.
	 */
	public static ValidationRegistryReader getReader() {
		if (inst == null) {
			inst = new ValidationRegistryReader();

			EventManager.getManager().setActive(true);
		}
		return inst;
	}

	public static boolean isActivated() {
		// Whether the registry has been read or not is kept in the EventManager
		// class instead of this class in order to work around a shutdown problem.
		// See the comment in the isActive() method of the EventManager class
		// for details.
		return EventManager.getManager().isActive();
	}

	/**
	 * Returns the Validator extension point
	 */
	private IExtensionPoint getValidatorExtensionPoint() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(PLUGIN_ID, VALIDATOR_EXT_PT_ID);
		if (extensionPoint == null) {
			// If this happens it means that someone removed the "validator" extension point
			// declaration
			// from our plugin.xml file.
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.getValidatorExtensionPoint()"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_MISSING_VALIDATOR_EP);
				//entry.setTokens(new String[]{ValidationPlugin.PLUGIN_ID + "." + VALIDATOR_EXT_PT_ID}); //$NON-NLS-1$
				String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_MISSING_VALIDATOR_EP),
						new String[]{ValidationPlugin.PLUGIN_ID + "." + VALIDATOR_EXT_PT_ID});
				entry.setText(result);		
				logger.write(Level.FINE, entry);
			}
		}
		return extensionPoint;
	}

	/**
	 * It's okay to return a handle to the ValidatorMetaData because the vmd can't be modified by
	 * any code not in this package.
	 */
	public ValidatorMetaData getValidatorMetaData(IValidator validator) {
		// retrieval will be in log(n) time
		if (validator == null) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(IValidator)"); //$NON-NLS-1$
				entry.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_ORPHAN_IVALIDATOR, new String[]{"null"})); //$NON-NLS-1$
				logger.write(Level.SEVERE, entry);
			}
			return null;
		}

		String validatorClassName = validator.getClass().getName();
		ValidatorMetaData vmd = getValidatorMetaData(validatorClassName);
		if (vmd != null) {
			return vmd;
		}

		// If we got here, then vmd is neither a root nor an aggregate validator,
		// yet the IValidator exists. Internal error.
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(IValidator)"); //$NON-NLS-1$
			entry.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_ORPHAN_IVALIDATOR, new String[]{validatorClassName}));
			logger.write(Level.SEVERE, entry);
		}
		return null;
	}

	public Set getValidatorMetaData(IWorkspaceRoot root) {
		// Every validator on the Preferences page must be returned
		Set copy = new HashSet();
		clone(_indexedValidators.values(), copy);
		return copy;
	}

	/**
	 * Return a collection of Validators configured on a certain type of IProject (e.g. EJB Project
	 * vs. Web Project).
	 * 
	 * This is a long-running process. If you can, cache the result.
	 */
	public Set getValidatorMetaData(IProject project) {
		Set copy = new HashSet();
		getValidatorMetaData(project, copy);
		return copy;
	}

	/**
	 * Copy the set of configured validator metadata into the Set.
	 */
	public void getValidatorMetaData(IProject project, Set vmds) {
		if (vmds == null) {
			return;
		}
		vmds.clear();
		int executionMap = 0x0;
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		try {
			if (logger.isLoggingLevel(Level.FINEST)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(IProject)"); //$NON-NLS-1$
				entry.setText("IProject is " + String.valueOf(project)); //$NON-NLS-1$
				logger.write(Level.FINEST, entry);
			}

			if (project == null) {
				executionMap |= 0x1;
				// vmds is already clear
				return;
			}

			String[] projectNatures = null;
			try {
				projectNatures = project.getDescription().getNatureIds();
			} catch (CoreException exc) {
				executionMap |= 0x2;
				// vmds is already clear
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
					entry.setTargetException(exc);
					entry.setExecutionMap(executionMap);
					logger.write(Level.SEVERE, entry);
				}
				return;
			}

			// If there are no project natures on a particular project,
			// or if this project nature has no validators configured
			// on it, return the validators which are configured on all
			// projects.
			Set projVmds = null;
			if ((projectNatures == null) || (projectNatures.length == 0)) {
				executionMap |= 0x4;
				clone(getValidatorMetaDataUnknownProject(), vmds);
			} else {
				executionMap |= 0x8;
				if (logger.isLoggingLevel(Level.FINEST)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(IProject)"); //$NON-NLS-1$
					//entry.setTokens(projectNatures);
					entry.setText(projectNatures.toString());
					logger.write(Level.FINEST, entry);
				}

				for (int i = 0; i < projectNatures.length; i++) {
					String projectNatureId = projectNatures[i];
					projVmds = (Set) _validators.get(projectNatureId);

					if (projVmds == null) {
						// no validators registered for that type of project id
						executionMap |= 0x10;
						continue;
					}

					Iterator iterator = projVmds.iterator();
					while (iterator.hasNext()) {
						ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
						if (!vmds.contains(vmd)) {
							vmds.add(vmd);
						}
					}
				}

				// Now filter out the validators which must not run on this project
				removeExcludedProjects(project, vmds);

				if (vmds.size() == 0) {
					executionMap |= 0x20;
					clone(getValidatorMetaDataUnknownProject(), vmds);
				}
			}
		} finally {
			if (logger.isLoggingLevel(Level.FINER)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(IProject)"); //$NON-NLS-1$
				entry.setExecutionMap(executionMap);

				StringBuffer buffer = new StringBuffer();
				Iterator iterator = vmds.iterator();
				while (iterator.hasNext()) {
					ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
					buffer.append(vmd.getValidatorUniqueName());
					buffer.append("\n"); //$NON-NLS-1$
				}
				entry.setText(buffer.toString());
				logger.write(Level.FINER, entry);
			}
		}
	}

	/*
	 * If one project nature on the project includes a particular validator, but another project
	 * nature excludes that validator, then the validator needs to be removed from the vmd set.
	 * 
	 * For example, if AValidator can run on any java project but not on a J2EE project, which is an
	 * instance of a java project, then the AValidator is included by the java nature and excluded
	 * by the J2EE nature. The AValidator would have to be removed from the set.
	 */
	private void removeExcludedProjects(IProject project, Set vmds) {
		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		if (logger.isLoggingLevel(Level.FINEST)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationRegistryReader.removeExcludedProjects"); //$NON-NLS-1$

			StringBuffer buffer = new StringBuffer("\nBefore:\n"); //$NON-NLS-1$
			Iterator viterator = vmds.iterator();
			while (viterator.hasNext()) {
				ValidatorMetaData vmd = (ValidatorMetaData) viterator.next();
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
			entry.setText(buffer.toString());
			logger.write(Level.FINEST, entry);
		}

		String[] projectNatures = null;
		try {
			projectNatures = project.getDescription().getNatureIds();
		} catch (CoreException exc) {
			// if there's no natures, there's no list.
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.getValidatorMetaData(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return;
		}
		if ((projectNatures == null) || (projectNatures.length == 0)) {
			// nothing needs to be removed from the list
			return;
		}

		// First of all, clone the Set because we need to remove entries
		// from it, and we can't alter the set while we're iterating over
		// it.
		List tempList = getTempList();
		clone(vmds, tempList);

		for (int i = 0; i < projectNatures.length; i++) {
			String nature = projectNatures[i];
			Iterator iterator = tempList.iterator();
			while (iterator.hasNext()) {
				ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
				ValidatorNameFilter[] natureFilters = vmd.getProjectNatureFilters();
				if (natureFilters == null) {
					// Can run on any project
					continue;
				}

				for (int j = 0; j < natureFilters.length; j++) {
					ValidatorNameFilter pn = natureFilters[j];
					if (nature.equals(pn.getNameFilter()) && !pn.isInclude()) {
						vmds.remove(vmd);
					}
				}
			}
		}

		if (logger.isLoggingLevel(Level.FINEST)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationRegistryReader.removeExcludedProjects"); //$NON-NLS-1$

			StringBuffer buffer = new StringBuffer("\nAfter:\n"); //$NON-NLS-1$
			Iterator viterator = vmds.iterator();
			while (viterator.hasNext()) {
				ValidatorMetaData vmd = (ValidatorMetaData) viterator.next();
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
			entry.setText(buffer.toString());
			logger.write(Level.FINEST, entry);
		}
	}

	private Collection clone(Collection input, Collection copy) {
		if (input == null) {
			return null;
		}

		if (copy == null) {
			return null;
		}
		copy.clear();
		copy.addAll(input);
		return copy;
	}

	public String debug() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Project nature => validators configured"); //$NON-NLS-1$
		buffer.append("\n"); //$NON-NLS-1$
		Iterator viterator = _validators.keySet().iterator();
		while (viterator.hasNext()) {
			String projId = (String) viterator.next();
			buffer.append("projId: "); //$NON-NLS-1$
			buffer.append(projId);
			buffer.append("\n"); //$NON-NLS-1$
			Set validators = (Set) _validators.get(projId);
			Iterator innerIterator = validators.iterator();
			while (innerIterator.hasNext()) {
				ValidatorMetaData vmd = (ValidatorMetaData) innerIterator.next();
				buffer.append("\t"); //$NON-NLS-1$
				buffer.append(vmd.getValidatorUniqueName());
				buffer.append("\n"); //$NON-NLS-1$
			}
		}
		buffer.append("\n"); //$NON-NLS-1$

		buffer.append("Enable/disable validator by default"); //$NON-NLS-1$
		buffer.append("\n"); //$NON-NLS-1$
		viterator = _indexedValidators.values().iterator();
		while (viterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) viterator.next();
			buffer.append(vmd.getValidatorUniqueName());
			buffer.append(" enabled? "); //$NON-NLS-1$
			buffer.append(vmd.isEnabledByDefault());
			buffer.append("\n"); //$NON-NLS-1$
		}

		return buffer.toString();
	}

	public boolean isConfiguredOnProject(ValidatorMetaData vmd, IProject project) {
		Set prjVmds = getValidatorMetaData(project);
		if (prjVmds == null) {
			return false;
		}

		if (prjVmds.size() == 0) {
			return false;
		}

		return prjVmds.contains(vmd);
	}

	/**
	 * Return a set of ValidatorMetaData which are configured on all projects or which run on any
	 * projects except certain project types.
	 * 
	 * Unlike other get methods, because this method is private it doesn't return a clone.
	 * 
	 * @see addExcludedRemainder()
	 */
	private Set getValidatorMetaDataUnknownProject() {
		Set projVmds = (Set) _validators.get(UNKNOWN_PROJECT);
		if (projVmds == null) {
			projVmds = Collections.EMPTY_SET;
		}
		return projVmds;
	}

	/**
	 * Return a set of ValidatorMetaData which are enabled by default.
	 */
	public Set getValidatorMetaDataEnabledByDefault() {
		Set copy = new HashSet();
		clone(_defaultEnabledValidators, copy);
		return copy;
	}

	public ValidatorMetaData[] getValidatorMetaDataArrayEnabledByDefault() {
		ValidatorMetaData[] result = new ValidatorMetaData[_defaultEnabledValidators.size()];
		_defaultEnabledValidators.toArray(result);
		return result;
	}

	/**
	 * This method should be called ONLY by the validation framework, UI, or TVT plugin. In general,
	 * only the validation framework and the validation TVT should handle ValidatorMetaData objects.
	 * 
	 * Given a string which identifies a fully-qualified class name of a validator, return the
	 * ValidatorMetaData that uses a validator of that name, if it exists.
	 * 
	 * It's okay to return a handle to the ValidatorMetaData because the vmd can't be modified by
	 * any code not in this package.
	 */
	public ValidatorMetaData getValidatorMetaData(String validatorClassName) {
		if (validatorClassName == null) {
			return null;
		}

		ValidatorMetaData vmd = (ValidatorMetaData) _indexedValidators.get(validatorClassName);
		if (vmd != null) {
			return vmd;
		}

		// Check for an aggregate validator
		Iterator iterator = _indexedValidators.values().iterator();
		while (iterator.hasNext()) {
			vmd = (ValidatorMetaData) iterator.next();
			if (vmd == null) {
				continue;
			}

			if (vmd.getValidatorUniqueName().equals(validatorClassName)) {
				return vmd;
			}

			String[] aggregateNames = vmd.getAggregatedValidatorNames();
			if (aggregateNames != null) {
				for (int i = 0; i < aggregateNames.length; i++) {
					String aggregateName = aggregateNames[i];
					if (validatorClassName.equals(aggregateName)) {
						return vmd;
					}
				}
			}

			// Current name of validator doesn't match; has this validator been
			// migrated from another package?
			ValidatorMetaData.MigrationMetaData mmd = vmd.getMigrationMetaData();
			if (mmd == null) {
				// Validator class name hasn't been migrated
				continue;
			}

			Set idList = mmd.getIds();
			if (idList == null) {
				// Invalid <migrate> element.
				continue;
			}

			Iterator idIterator = idList.iterator();
			while (idIterator.hasNext()) {
				String[] ids = (String[]) idIterator.next();
				if (ids.length != 2) {
					// log
					continue;
				}

				String from = ids[0];
				if (from == null) {
					// log
					continue;
				}

				if (from.equals(validatorClassName)) {
					return vmd;
				}
			}
		}

		// If we got to this point, no validator using that class name is loaded.
		return null;
	}

	/**
	 * Return true if the named validator is installed, otherwise false.
	 */
	public boolean isExistingValidator(String validatorClassName) {
		return (getValidatorMetaData(validatorClassName) != null);
	}

	/**
	 * Initialize the validator with the static metadata (runtime metadata is initialized in the
	 * ValidationOperation class).
	 */
	private ValidatorMetaData initializeValidator(IConfigurationElement element, String validatorName, String pluginId) {
		IConfigurationElement[] runChildren = element.getChildren(TAG_RUN_CLASS);
		if ((runChildren == null) || (runChildren.length < 1)) {
			// How can an IValidatorImpl be created when there no class name to
			// instantiate?
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.initializeValidator(IConfigurationElement, String, String)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_RUN);
				//entry.setTokens(new String[]{validatorName});
				String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_RUN),
						new String[]{validatorName});
				entry.setText(result);
				
				logger.write(Level.FINE, entry);
			}
			return null;
		}

		//WTP Bugzilla defect: 82338
		//Using the Unique Identifier give the flexibility of the same validator class used by other validator extentions without writing a new validation class
		//Reverting the fix back as the class name defined in the ext is unique to this validator and has to be used for the unique id in the validation metadata
		String validatorImplName = runChildren[0].getAttribute(ATT_CLASS);
		
		if (validatorImplName == null) {
			// Same as before; how can we instantiate when...
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.initializeValidator(IConfigurationElement, String, String)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_CLASS);
				entry.setTokens(new String[]{validatorName});
				logger.write(Level.FINE, entry);
			}
			return null;
		}

		String helperImplName = getHelperName(element);
		if (helperImplName == null) {
			// Same as before; how can we instantiate when...
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.FINE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationRegistryReader.initializeValidator(IConfigurationElement, String, String)"); //$NON-NLS-1$
				entry.setMessageTypeID(ResourceConstants.VBF_EXC_SYNTAX_NO_VAL_RUN);
				entry.setTokens(new String[]{validatorImplName});
				logger.write(Level.FINE, entry);
			}
			return null;
		}

		// In order to speed up our String comparisons, load these
		// names into Java's constants space. This way, we'll be able to
		// use pointer comparison instead of the traditional
		// character-by-character comparison. Since these names should
		// never be set by anyone other than this class, and this class
		// sets them only once, it is safe to declare these Strings
		// constants.
		//
		// To load a String into the constants space, call intern() on the String.
		//
		ValidatorMetaData vmd = new ValidatorMetaData();
		vmd.addFilters(getFilters(element)); // validator may, or may not, have filters
		vmd.addProjectNatureFilters(getProjectNatureFilters(element)); // validator may, or may not,
		// specify a project nature
		vmd.addAggregatedValidatorNames(getAggregateValidatorsNames(element)); // if a validator
		// aggregated another
		// validator, it
		// should identify
		// the
		// sub-validator(s)'
		// class name
		vmd.setValidatorDisplayName(validatorName.intern()); // validator must have a display name.
		vmd.setValidatorUniqueName(validatorImplName.intern());
		vmd.setPluginId(pluginId);
		vmd.setIncremental(getIncremental(element));
		vmd.setFullBuild(getFullBuild(element));
		vmd.setAsync(getAsync(element));
		vmd.setRuleGroup(getRuleGroup(element));
		vmd.setEnabledByDefault(getEnabledByDefault(element));
		vmd.setMigrationMetaData(getMigrationMetaData(element, vmd));
		vmd.setHelperClass(element, helperImplName);
		vmd.setValidatorClass(element); // associate the above attributes with the validator
		vmd.addDependentValidator(getDependentValidatorValue(element));
		String customMarkerId = getMarkerIdValue(element);
		if(customMarkerId != null)
		    vmd.setMarkerId(pluginId+"."+customMarkerId); //$NON-NLS-1$

		Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
		if (logger.isLoggingLevel(Level.FINEST)) {
			LogEntry entry = ValidationPlugin.getLogEntry();
			entry.setSourceID("ValidationRegistryReader.initializeValidator(IConfigurationElement, String, String)"); //$NON-NLS-1$
			entry.setText("validator loaded: " + validatorImplName); //$NON-NLS-1$
			logger.write(Level.FINEST, entry);
		}

		return vmd;
	}

	private List getTempList() {
		// Return a list for temporary use
		if (_tempList == null) {
			_tempList = new ArrayList();
		} else {
			// Prepare list for use
			_tempList.clear();
		}

		return _tempList;
	}

	/**
	 * This method should be called ONLY BY THE VALIDATION FRAMEWORK! The value from this method is
	 * used to populate the validation preference page.
	 */
	public Collection getAllValidators() {
		Set validators = new HashSet();
		clone(_indexedValidators.values(), validators);
		return validators;
	}

	public int numberOfValidators() {
		return _indexedValidators.size();
	}

	/**
	 * Reads one extension by looping through its configuration elements.
	 */
	private void readExtension(IExtension extension) {
		IConfigurationElement[] elements = extension.getConfigurationElements();

		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];

			String label = extension.getLabel();
			if (label == null || label.equals("")) { //$NON-NLS-1$
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.FINE)) {
					String[] msgParm = {extension.getUniqueIdentifier()};
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationRegistryReader.readExtension(IExtension)"); //$NON-NLS-1$
					entry.setMessageTypeID(ResourceConstants.VBF_EXC_VALIDATORNAME_IS_NULL);
					//entry.setTokens(msgParm);
					String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_VALIDATORNAME_IS_NULL),
							msgParm);
					entry.setText(result);					
					logger.write(Level.FINE, entry);
				}
			} else {
				// If getLabel() returns an empty string, this is an illegal validator.
				// The PropertyPage, and other status messages, need to have a displayable name for
				// the validator.
				String pluginId = extension.getNamespace();
				ValidatorMetaData vmd = initializeValidator(element, label, pluginId);

				if (vmd != null) {
					// Add this validator to the list of validators; if vmd is null, the validator
					// couldn't be created.
					add(vmd);
				}
			}
		}
	}

	/**
	 * Reads the registry to find the Validators which have been implemented.
	 */
	private void readRegistry() {
		_validators.clear();

		// Get the extensions that have been registered.
		IExtensionPoint validatorEP = getValidatorExtensionPoint();
		if (validatorEP == null) {
			return;
		}
		IExtension[] extensions = validatorEP.getExtensions();

		// find all runtime implementations
		for (int i = 0; i < extensions.length; i++) {
			readExtension(extensions[i]);
		}
	}

	public IValidator getValidator(String validatorClassName) throws InstantiationException {
		ValidatorMetaData vmd = (ValidatorMetaData) _indexedValidators.get(validatorClassName);
		if(vmd != null)
			return vmd.getValidator();
		return null;
	}

}