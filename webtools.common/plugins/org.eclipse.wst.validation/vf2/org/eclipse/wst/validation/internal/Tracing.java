/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * Control the tracing that this plug-in performs. This is used for service.
 * @author karasiuk
 *
 */
public class Tracing {
	
	private static DateFormat 	_df = new SimpleDateFormat("HH:mm:ss.SSSS"); //$NON-NLS-1$
	private static boolean		_forceLogging;
	private static Boolean		_traceMatches;
	private static Boolean		_traceV1;
	private static String		_extraValDetail;
	
	private static String		_filter;
	private static boolean		_noFilters;
	
	/**
	 * Answer true if the filters allow this validator to be enabled. Normally this method will answer true.
	 * It is only when filters are activated via the debugging options, that this method might return false.
	 * This is used to aid in debugging by making it look like only one validator has been registered.
	 * 
	 * @param validatorId the validator id.
	 * @return true if the validator should be registered via an extension point.
	 */
	public static boolean isEnabled(String validatorId){
		if (_noFilters)return true;
		if (_filter == null){
			_filter = Platform.getDebugOption(DebugConstants.FilterAllExcept);
			if (_filter == null || _filter.length() == 0)_noFilters = true;
			return true;
		}
		return (_filter.equals(validatorId));		
	}
	
	/**
	 * Are we in logging/debugging mode?
	 */
	public static boolean isLogging(){
		return _forceLogging || ValidationPlugin.getPlugin().isDebugging();
	}
	
	public static boolean isTraceMatches(){
		if (_traceMatches == null){
			_traceMatches = Misc.debugOptionAsBoolean(DebugConstants.TraceMatches);
		}
		return _traceMatches;
	}
	
	public static boolean isTraceV1(){
		if (_traceV1 == null){
			_traceV1 = Misc.debugOptionAsBoolean(DebugConstants.TraceV1);
		}
		return _traceV1;
	}
	
	public static boolean matchesExtraDetail(String validatorId){
		if (_extraValDetail == null){
			_extraValDetail = Platform.getDebugOption(DebugConstants.ExtraValDetail);
			if (_extraValDetail == null)_extraValDetail = ""; //$NON-NLS-1$
		}
		if (_extraValDetail.length() == 0)return false;
		return _extraValDetail.equals(validatorId);
	}

	/**
	 * Write a line to the console for debugging, if in debugging mode.
	 * @param line
	 */
	public static void log(String line){
		if (isLogging())write(line);
	}
	
	public static void log(Object... parts){
		if (isLogging()){
			StringBuffer b = new StringBuffer(200);
			for (Object p : parts)b.append(p);
			write(b.toString());
		}
	}

	/**
	 * Write a line to the log. Include a time stamp with the line.
	 * @param line
	 */
	public static void write(String line){
		System.err.println(timestampIt(line));
	}

	public static String timestampIt(String line){
		Date date = new Date();
		long thread = Thread.currentThread().getId();
		return _df.format(date) + " " + thread + " " + line;  //$NON-NLS-1$//$NON-NLS-2$		
	}

	/**
	 * If we are in logging mode, log the item, and then reset the string buffer.
	 */
	public static void log(StringBuffer b){
		log(b.toString());
		b.setLength(0);
	}

	/**
	 * Force the logging to be turned on. Normally logging is turned on via -debug options. However
	 * the logging can be forced to be on by setting this to true. (Setting this to false doesn't force
	 * the logging to be turned off).
	 * 
	 * @param forceLogging
	 */
	public static void setForceLogging(boolean forceLogging) {
		_forceLogging = forceLogging;
	}

}