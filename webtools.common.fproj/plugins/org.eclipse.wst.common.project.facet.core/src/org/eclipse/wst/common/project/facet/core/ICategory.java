/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;

/**
 * Models a grouping of project facets that are intended to be selected and 
 * deselected as a set. This interface is not intended to be implemented by 
 * clients.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface ICategory

    extends IAdaptable
    
{
    String getId();
    String getPluginId();
    String getLabel();
    String getDescription();
    
    /**
     * Returns the project facets that compose this category.
     * 
     * @return the member project facets (element type: {@see IProjectFacet})
     */
    
    Set getProjectFacets();
}