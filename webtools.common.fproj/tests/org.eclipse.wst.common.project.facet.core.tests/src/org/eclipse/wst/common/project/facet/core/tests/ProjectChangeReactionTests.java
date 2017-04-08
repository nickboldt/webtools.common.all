package org.eclipse.wst.common.project.facet.core.tests;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils;
import org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.ICondition;

public final class ProjectChangeReactionTests

    extends AbstractTests
    
{
    private static final String METADATA_FILE 
        = ".settings/org.eclipse.wst.common.project.facet.core.xml";
    
    private static final String TEST_PROJECT_NAME = "testProject";

    private static IProjectFacet f1;
    private static IProjectFacetVersion f1v10;
    private static IProjectFacetVersion f1v12;
    private static IProjectFacetVersion f1v121;
    private static IProjectFacetVersion f1v13;
    private static IProjectFacetVersion f1v20;
    
    static
    {
        try
        {
            f1 = ProjectFacetsManager.getProjectFacet( "facet1" );
            f1v10 = f1.getVersion( "1.0" );
            f1v12 = f1.getVersion( "1.2" );
            f1v121 = f1.getVersion( "1.2.1" );
            f1v13 = f1.getVersion( "1.3" );
            f1v20 = f1.getVersion( "2.0" );
        }
        catch( Exception e )
        {
            // Ignore failures. This api is tested explicitly.
        }
    }
    
    private IProject pj;
    private IFacetedProject fpj;
    private IFile mdfile;

    private ProjectChangeReactionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Project Change Reaction Tests" );

        suite.addTest( new ProjectChangeReactionTests( "testReactionToProjectDelete" ) );
        suite.addTest( new ProjectChangeReactionTests( "testReactionToMetadataFileDelete" ) );
        suite.addTest( new ProjectChangeReactionTests( "testReactionToMetadataFileChange" ) );
        
        return suite;
    }
    
    protected void setUp()
    
        throws CoreException
        
    {
        assertFalse( ws.getRoot().getProject( TEST_PROJECT_NAME ).exists() );
        
        this.fpj = ProjectFacetsManager.create( TEST_PROJECT_NAME, null, null );
        
        this.pj = this.fpj.getProject();
        addResourceToCleanup( this.pj );
        assertTrue( this.fpj.getProject().exists() );
        
        this.fpj.installProjectFacet( f1v12, null, null );
        assertEquals( this.fpj.getProjectFacets(), TestUtils.asSet( f1v12 ) );
        
        this.mdfile = this.pj.getFile( METADATA_FILE );
    }
    
    public void testReactionToProjectDelete()
    
        throws CoreException
        
    {
        this.pj.delete( true, null );
        
        TestUtils.waitForCondition( createNoFacetsCondition( this.fpj ) );
        assertNull( ProjectFacetsManager.create( this.pj ) );
    }

    public void testReactionToMetadataFileDelete()
    
        throws CoreException
        
    {
        this.mdfile.delete( true, null );

        TestUtils.waitForCondition( createNoFacetsCondition( this.fpj ) );
    }

    public void testReactionToMetadataFileChange()
    
        throws CoreException, IOException
        
    {
        String contents;
        
        contents = TestUtils.readFromFile( this.mdfile );
        contents = contents.replaceFirst( "1.2", "2.0" );
        TestUtils.writeToFile( this.mdfile, contents );
        
        TestUtils.waitForCondition( createFacetCondition( this.fpj, f1v20 ) );
        
        contents = contents.replaceFirst( "2.0", "1.2.1" );
        TestUtils.writeToFile( this.mdfile, contents );
        
        TestUtils.waitForCondition( createFacetCondition( this.fpj, f1v121 ) );
        
        contents = contents.replaceFirst( "<installed facet=\"facet1\" version=\"1.2.1\"/>", "" );
        TestUtils.writeToFile( this.mdfile, contents );
        
        TestUtils.waitForCondition( createNoFacetsCondition( this.fpj ) );
    }
    
    private static ICondition createNoFacetsCondition( final IFacetedProject fpj )
    {
        return new ICondition()
        {
            public boolean check()
            {
                return fpj.getProjectFacets().size() == 0;
            }
        };
    }
    
    private static ICondition createFacetCondition( final IFacetedProject fpj,
                                                    final IProjectFacetVersion fv )
    {
        return new ICondition()
        {
            public boolean check()
            {
                return fpj.hasProjectFacet( fv );
            }
        };
    }
    
}
