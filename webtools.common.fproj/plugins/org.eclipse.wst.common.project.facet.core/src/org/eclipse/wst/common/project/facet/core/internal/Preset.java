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

package org.eclipse.wst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The implementation of the {@see IPreset} interface.
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class Preset

    implements IPreset
    
{
    private String id;
    private String label;
    private String description;
    
    private final Set facets = new HashSet();
    
    private final Set facetsReadOnly 
        = Collections.unmodifiableSet( this.facets );
    
    private boolean isUserDefined = false;
    
    Preset() {}
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }

    public String getLabel()
    {
        return this.label;
    }
    
    void setLabel( final String label )
    {
        this.label = label;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    void setDescription( final String description )
    {
        this.description = description;
    }
    
    public Set getProjectFacets()
    {
        return this.facetsReadOnly;
    }
    
    void addProjectFacet( final IProjectFacetVersion fv )
    {
        this.facets.add( fv );
    }
    
    void addProjectFacet( final Set facets )
    {
        this.facets.addAll( facets );
    }
    
    public boolean isUserDefined()
    {
        return this.isUserDefined;
    }
    
    void setUserDefined( final boolean isUserDefined )
    {
        this.isUserDefined = isUserDefined;
    }
    
    public String toString()
    {
        return this.id;
    }
    
}
