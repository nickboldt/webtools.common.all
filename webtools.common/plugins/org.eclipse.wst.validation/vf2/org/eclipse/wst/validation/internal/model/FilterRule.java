package org.eclipse.wst.validation.internal.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.validation.internal.ExtensionConstants;
import org.eclipse.wst.validation.internal.PrefConstants;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValMessages;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.Preferences;

/**
 * A rule that is used to filter out (or in) validation on a resource.
 * @author karasiuk
 *
 */
public abstract class FilterRule implements IAdaptable {
	
	protected String _pattern;
	
	/**
	 * Create a rule based on an extension element.
	 * @param name the extension element, it can be one of projectNature, facet, fileext, file or contentType
	 * @return null if there is a problem.
	 */
	public static FilterRule create(String name){
		if (ExtensionConstants.Rule.fileext.equals(name))return new FileExt();
		if (ExtensionConstants.Rule.projectNature.equals(name))return new ProjectNature();
		if (ExtensionConstants.Rule.file.equals(name))return new File();
		if (ExtensionConstants.Rule.contentType.equals(name))return new ContentType();
		if (ExtensionConstants.Rule.facet.equals(name))return new Facet();
		return null;
	}
	
	public static FilterRule createFile(String pattern, boolean caseSensitive, int type){
		File ext = new File();
		ext.setData(pattern);
		ext.setCaseSensitive(caseSensitive);
		ext.setType(type);
		return ext;
	}
	
	public static FilterRule createFileExt(String pattern, boolean caseSensitive){
		FileExt ext = new FileExt();
		ext.setData(pattern);
		ext.setCaseSensitive(caseSensitive);
		return ext;
	}
	
	public static FilterRule createFacet(String facetId){
		Facet facet = new Facet();
		facet.setData(facetId);
		return facet;
	}
	
	public static FilterRule createProject(String projectNature){
		ProjectNature pn = new ProjectNature();
		pn.setData(projectNature);
		return pn;
	}
	
	public static FilterRule createContentType(String contentType){
		ContentType ct = new ContentType();
		ct.setData(contentType);
		return ct;
	}
	
	public abstract void setData(IConfigurationElement rule);

	/** 
	 * Answer true if the rule matches the resource, false if it doesn't, and
	 * null if the rule does not apply to resources.
	 * 
	 * 	@param resource the resource that is being validated.
	 */
	public Boolean matchesResource(IResource resource){
		return null;
	}

	/** 
	 * Answer true if the rule matches the project, false if it doesn't, and null if the
	 * rule does not apply.
	 * 
	 * 	@param project the project that is being validated.
	 */
	public Boolean matchesProject(IProject project){
		return null;
	}
	
	public void setData(String data){
		_pattern = data;
	}
	
	public String toString() {
		return getDisplayableType() + ": " + _pattern; //$NON-NLS-1$
	}
	
	/** Answer a name of the rule, that can be displayed to a user. */
	public String getName(){
		return toString();
	}
	
	public String getPattern(){
		return _pattern;
	}
	
	/** Answer the type of rule. */
	public abstract String getType();
	
	/** Answer a type that can be displayed to an end user. */
	public abstract String getDisplayableType();
	
	public boolean asBoolean(String value, boolean aDefault){
		if (value == null)return aDefault;
		if (value.equals(ExtensionConstants.True))return true;
		if (value.equals(ExtensionConstants.False))return false;
		return aDefault;
	}
	
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	
	public static class ProjectNature extends FilterRule {

		
		public FilterRule copy() {
			ProjectNature rule = new ProjectNature();
			rule._pattern = _pattern;
			return rule;
		}
		
		public void setData(IConfigurationElement rule) {
			_pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.id);
			
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleProjectNature;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.projectNature;
		}
		
		public Boolean matchesProject(IProject project) {
			try {
				return project.hasNature(_pattern);
			}
			catch (CoreException e){
			}
			return Boolean.FALSE;
		}
		
	}
	
	public static class FileExt extends FilterRule {
		
		private boolean _caseSensitive;
		
		public FilterRule copy() {
			FileExt rule = new FileExt();
			rule._pattern = _pattern;
			rule._caseSensitive = _caseSensitive;
			return rule;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.fileext;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleFileExt;
		}
		
		public String getName() {
			return toString();
		}

		public void setData(IConfigurationElement rule) {
			_pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.ext);
			_caseSensitive = asBoolean(rule.getAttribute(ExtensionConstants.RuleAttrib.caseSensitive), false);			
		}
		
		public String toString() {
			if (_caseSensitive)return NLS.bind(ValMessages.FileExtWithCase, getDisplayableType(), _pattern);
			return NLS.bind(ValMessages.FileExtWithoutCase, getDisplayableType(), _pattern);
		}

		public Boolean matchesResource(IResource resource) {
			String ext = resource.getFileExtension();
			if (_caseSensitive)return _pattern.equals(ext);
			return _pattern.equalsIgnoreCase(ext);
		}

		public boolean isCaseSensitive() {
			return _caseSensitive;
		}
		
		@Override
		public void load(Preferences rid) {
			_caseSensitive = rid.getBoolean(PrefConstants.caseSensitive, false);
			super.load(rid);
		}
		
		@Override
		public void save(Preferences rid) {
			rid.putBoolean(PrefConstants.caseSensitive, _caseSensitive);
			super.save(rid);
		}

		public void setCaseSensitive(boolean caseSensitive) {
			_caseSensitive = caseSensitive;
		}		
	}
	
	public static class File extends FilterRule {
		
		private boolean _caseSensitive;
		private int		_type;
		
		public static final int FileTypeFile = 1;
		public static final int FileTypeFolder = 2;
		public static final int FileTypeFull = 3;
		
		public File(){
			
		}
		
		public FilterRule copy() {
			File rule = new File();
			rule._pattern = _pattern;
			rule._caseSensitive = _caseSensitive;
			rule._type = _type;
			return rule;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.file;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleFile;
		}

		public void setData(IConfigurationElement rule) {
			_pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.name);
			if (_pattern == null)throw new IllegalStateException(ValMessages.ErrPatternAttrib);
			_caseSensitive = asBoolean(rule.getAttribute(ExtensionConstants.RuleAttrib.caseSensitive), false);	
			String type = rule.getAttribute(ExtensionConstants.RuleAttrib.fileType);
			if (type == null)throw new IllegalStateException(ValMessages.ErrTypeReq);
			if (ExtensionConstants.FileType.file.equals(type))_type = FileTypeFile;
			else if (ExtensionConstants.FileType.folder.equals(type))_type = FileTypeFolder;
			else if (ExtensionConstants.FileType.full.equals(type))_type = FileTypeFull;
			else {
				Object[] parms = {type, ExtensionConstants.FileType.file, ExtensionConstants.FileType.folder, 
					ExtensionConstants.FileType.full};
				throw new IllegalStateException(NLS.bind(ValMessages.ErrType, parms));
			}
		}
		
		public String toString() {
			if (_caseSensitive)return NLS.bind(ValMessages.FileExtWithCase, getDisplayableType(), _pattern);
			return NLS.bind(ValMessages.FileExtWithoutCase, getDisplayableType(), _pattern);
		}
		
		public Boolean matchesResource(IResource resource) {
			String name = null;
			switch (_type){
			case FileTypeFile:
				name = resource.getName();
				break;
				
			case FileTypeFolder:
				name = resource.getProjectRelativePath().removeLastSegments(1).toString();
				break;
				
			case FileTypeFull:
				name = resource.getProjectRelativePath().toString();
				break;
			}
			
			if (_caseSensitive)return _pattern.equals(name);
			return _pattern.equalsIgnoreCase(name);
		}
		
		@Override
		public void load(Preferences rid) {
			_caseSensitive = rid.getBoolean(PrefConstants.caseSensitive, false);
			_type = rid.getInt(PrefConstants.fileType, -1);
			super.load(rid);
		}
		
		@Override
		public void save(Preferences rid) {
			rid.putBoolean(PrefConstants.caseSensitive, _caseSensitive);
			rid.putInt(PrefConstants.fileType, _type);
			super.save(rid);
		}

		public void setCaseSensitive(boolean caseSensitive) {
			_caseSensitive = caseSensitive;
		}

		public void setType(int type) {
			_type = type;
		}
		
	}
	
	public static class Facet extends FilterRule {
		
		public FilterRule copy() {
			Facet rule = new Facet();
			rule._pattern = _pattern;
			return rule;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.facet;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleFacet;
		}

		public void setData(IConfigurationElement rule) {
			_pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.id);
		}
		
		@Override
		public Boolean matchesProject(IProject project) {
			try {
				return FacetedProjectFramework.hasProjectFacet(project, _pattern);
			}
			catch (CoreException e){
				if (Tracing.isLogging())ValidationPlugin.getPlugin().handleException(e);
			}
			return Boolean.FALSE;
		}
		
	}
	
	public static class ContentType extends FilterRule {
		
		public FilterRule copy() {
			ContentType rule = new ContentType();
			rule._pattern = _pattern;
			return rule;
		}
		
		public String getType() {
			return ExtensionConstants.Rule.contentType;
		}
		
		public String getDisplayableType() {
			return ValMessages.RuleContentType;
		}

		public void setData(IConfigurationElement rule) {
			_pattern = rule.getAttribute(ExtensionConstants.RuleAttrib.id);
		}
		
		public Boolean matchesResource(IResource resource) {
			try {
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					
					String name = resource.getName();
					if (name.equals(".project")){
						int i = 0;
					}
						
					IContentDescription cd = file.getContentDescription();
					if (cd == null)return Boolean.FALSE;
					IContentType ct = cd.getContentType();
					if (ct == null)return Boolean.FALSE;
					boolean match = _pattern.equals(ct.getId());
					if (match && Tracing.isTraceMatches())Tracing.log(toString() + " has matched " + resource); //$NON-NLS-1$
					return match;
				}
			}
			catch (CoreException e){
				if(Tracing.isLogging())ValidationPlugin.getPlugin().handleException(e);
			}
			return Boolean.FALSE;
		}
		
	}

	/** Answer a deep copy of yourself. */
	public abstract FilterRule copy();

	/**
	 * Save yourself in the preference file.
	 * @param rid
	 */
	public void save(Preferences rid) {
		rid.put(PrefConstants.ruleType, getType());
		rid.put(PrefConstants.pattern, getPattern());		
	}

	public void load(Preferences rule) {
		setData(rule.get(PrefConstants.pattern, null));		
	}
	
}
