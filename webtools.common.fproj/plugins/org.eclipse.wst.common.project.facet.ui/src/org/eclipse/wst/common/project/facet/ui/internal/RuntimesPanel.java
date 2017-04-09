/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal;

import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gd;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhalign;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhfill;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdhhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gdwhint;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.gl;
import static org.eclipse.wst.common.project.facet.ui.internal.util.GridLayoutUtil.glmargins;
import static org.eclipse.wst.common.project.facet.ui.internal.util.SwtUtil.getPreferredWidth;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.IDecorationsProvider;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimesPanel

    extends Composite

{
    private static final Object NO_RUNTIME_SELECTED_PLACEHOLDER = new Object();
    
    private final IFacetedProjectWorkingCopy fpjwc;
    private boolean showAllRuntimesSetting;
    private final CheckboxTableViewer runtimes;
    private final Button showAllRuntimesCheckbox;
    private final Button makePrimaryButton;
    private final Button newRuntimeButton;
    private final Label runtimeComponentsLabel;
    private final TableViewer runtimeComponents;
    private IRuntime currentPrimaryRuntime;
    private final List<IFacetedProjectListener> listeners;
    private Color colorGray;
    
    public RuntimesPanel( final Composite parent,
                          final IFacetedProjectWorkingCopy fpjwc )
    {
        super( parent, SWT.NONE );
        
        this.listeners = new ArrayList<IFacetedProjectListener>();
        
        addDisposeListener
        ( 
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent e )
                {
                    handleWidgetDisposed();
                }
            }
        );
        
        // Bind to the data model.
        
        this.fpjwc = fpjwc;
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleAvailableRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.AVAILABLE_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleTargetableRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.TARGETABLE_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handleTargetedRuntimesChanged();
                }
            },
            IFacetedProjectEvent.Type.TARGETED_RUNTIMES_CHANGED
        );
        
        addDataModelListener
        ( 
            new IFacetedProjectListener()
            {
                public void handleEvent( final IFacetedProjectEvent event )
                {
                    handlePrimaryRuntimeChanged();
                }
            },
            IFacetedProjectEvent.Type.PRIMARY_RUNTIME_CHANGED
        );
        
        this.showAllRuntimesSetting = false;

        // Initialize the colors.
        
        this.colorGray = new Color( null, 160, 160, 164 );

        // Layout the panel.
        
        final GridLayout layout = new GridLayout( 1, false );
        layout.marginHeight = 5;
        layout.marginWidth = 5;

        setLayout( layout );

        this.runtimes = CheckboxTableViewer.newCheckList( this, SWT.BORDER );
        this.runtimes.getTable().setLayoutData( gdfill() );
        this.runtimes.setContentProvider( new ContentProvider() );
        this.runtimes.setLabelProvider( new LabelProvider() );
        this.runtimes.setSorter( new Sorter() );
        this.runtimes.setInput( new Object() );
        
        this.runtimes.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent e )
                {
                    handleRuntimeSelectionChanged();
                }
            }
        );
        
        this.runtimes.addCheckStateListener
        (
            new ICheckStateListener()
            {
                public void checkStateChanged( final CheckStateChangedEvent e )
                {
                    handleCheckStateChanged( e );
                }
            }
        );
        
        this.showAllRuntimesCheckbox = new Button( this, SWT.CHECK );
        this.showAllRuntimesCheckbox.setText( Resources.showAllRuntimes );
        this.showAllRuntimesCheckbox.setSelection( this.showAllRuntimesSetting );
        
        this.showAllRuntimesCheckbox.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleShowAllRuntimesSelected();
                }
            }
        );
        
        final Composite buttons = new Composite( this, SWT.NONE );
        buttons.setLayoutData( gdhalign( gd(), GridData.END ) );
        buttons.setLayout( glmargins( gl( 2 ), 0, 0 ) );
        
        GridData gd;
        
        this.makePrimaryButton = new Button( buttons, SWT.PUSH );
        this.makePrimaryButton.setText( Resources.makePrimaryLabel );
        gd = gdwhint( gd(), getPreferredWidth( this.makePrimaryButton ) + 15 );
        this.makePrimaryButton.setLayoutData( gd );
        this.makePrimaryButton.setEnabled( false );
        
        this.makePrimaryButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleMakePrimarySelected();
                }
            }
        );
        
        this.newRuntimeButton = new Button( buttons, SWT.PUSH );
        this.newRuntimeButton.setText( Resources.newRuntimeButtonLabel );
        gd = gdwhint( gd(), getPreferredWidth( this.newRuntimeButton ) + 15 );
        this.newRuntimeButton.setLayoutData( gd );

        this.newRuntimeButton.addSelectionListener
        (
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent e )
                {
                    handleNewRuntimeButtonSelected();
                }
            }
        );
        
        this.runtimeComponentsLabel = new Label( this, SWT.NONE );
        this.runtimeComponentsLabel.setText( Resources.runtimeCompositionLabel );
        this.runtimeComponentsLabel.setLayoutData( gdhfill() );
        
        final Color infoBackgroundColor
            = parent.getDisplay().getSystemColor( SWT.COLOR_INFO_BACKGROUND );
        
        this.runtimeComponents = new TableViewer( this, SWT.BORDER );
        this.runtimeComponents.getTable().setLayoutData( gdhhint( gdhfill(), 50 ) );
        this.runtimeComponents.getTable().setBackground( infoBackgroundColor );
        this.runtimeComponents.setContentProvider( new RuntimeComponentsContentProvider() );
        this.runtimeComponents.setLabelProvider( new RuntimeComponentsLabelProvider() );
        
        this.runtimeComponents.setInput( NO_RUNTIME_SELECTED_PLACEHOLDER );
        this.runtimeComponents.getTable().setEnabled( false );
        this.runtimeComponentsLabel.setEnabled( false );
        
        refresh();
        this.currentPrimaryRuntime = this.fpjwc.getPrimaryRuntime();
        
	    Dialog.applyDialogFont( parent );
    }
    
    public IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy()
    {
        return this.fpjwc;
    }
    
    private void handleAvailableRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleAvailableRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        if( this.showAllRuntimesSetting )
        {
            refresh();
        }
    }
    
    private void handleTargetableRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleTargetableRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        refresh();
    }
    
    private void handleTargetedRuntimesChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handleTargetedRuntimesChanged();
                    }
                }
            );
            
            return;
        }
        
        final Set<IRuntime> targeted = this.fpjwc.getTargetedRuntimes();
        
        for( IRuntime r : this.fpjwc.getTargetableRuntimes() )
        {
            if( targeted.contains( r ) )
            {
                if( ! this.runtimes.getChecked( r ) )
                {
                    this.runtimes.setChecked( r, true );
                }
            }
            else
            {
                if( this.runtimes.getChecked( r ) )
                {
                    this.runtimes.setChecked( r, false );
                }
            }
        }
    }
    
    private void handlePrimaryRuntimeChanged()
    {
        if( ! Thread.currentThread().equals( getDisplay().getThread() ) )
        {
            getDisplay().asyncExec
            ( 
                new Runnable()
                {
                    public void run()
                    {
                        handlePrimaryRuntimeChanged();
                    }
                }
            );
            
            return;
        }
        
        if( this.currentPrimaryRuntime != null )
        {
            this.runtimes.update( this.currentPrimaryRuntime, null );
        }
        
        this.currentPrimaryRuntime = this.fpjwc.getPrimaryRuntime();
        
        if( this.currentPrimaryRuntime != null )
        {
            this.runtimes.update( this.currentPrimaryRuntime, null );
        }
    }
    
    private void handleCheckStateChanged( final CheckStateChangedEvent e )
    {
        final IRuntime runtime = (IRuntime) e.getElement();
        
        if( ! this.fpjwc.getTargetableRuntimes().contains( runtime ) &&
            e.getChecked() )
        {
            this.runtimes.setChecked( runtime, false );
            return;
        }
        
        if( e.getChecked() )
        {
            this.fpjwc.addTargetedRuntime( runtime );
        }
        else
        {
            this.fpjwc.removeTargetedRuntime( runtime );
        }
    }
    
    private void handleRuntimeSelectionChanged()
    {
        final IRuntime r = getSelection();
        
        if( r == null )
        {
            if( this.runtimeComponents.getInput() != null )
            {
                this.runtimeComponentsLabel.setEnabled( false );
                this.runtimeComponents.getTable().setEnabled( false );
                this.runtimeComponents.setInput( NO_RUNTIME_SELECTED_PLACEHOLDER );
            }
        }
        else
        {
            if( this.runtimeComponents.getInput() == null ||
                ! this.runtimeComponents.getInput().equals( r ) )
            {
                this.runtimeComponentsLabel.setEnabled( true );
                this.runtimeComponents.getTable().setEnabled( true );
                this.runtimeComponents.setInput( r );
            }
            
            if( this.runtimes.getChecked( r ) && 
                this.fpjwc.getPrimaryRuntime() != null && 
                ! this.fpjwc.getPrimaryRuntime().equals( r ) &&
                this.fpjwc.getTargetableRuntimes().contains( r ) )
            {
                this.makePrimaryButton.setEnabled( true );
            }
            else
            {
                this.makePrimaryButton.setEnabled( false );
            }
        }
    }
    
    private void handleShowAllRuntimesSelected()
    {
        this.showAllRuntimesSetting 
            = this.showAllRuntimesCheckbox.getSelection();
        
        refresh();
    }
    
    private void handleMakePrimarySelected()
    {
        this.fpjwc.setPrimaryRuntime( getSelection() );
    }
    
    @SuppressWarnings( "unchecked" )
    private void handleNewRuntimeButtonSelected()
    {
        final String SERVER_UI_PLUGIN_ID = "org.eclipse.wst.server.ui"; //$NON-NLS-1$
        final String CLASS_NAME = "org.eclipse.wst.server.ui.internal.ServerUIPlugin"; //$NON-NLS-1$
        final String METHOD_NAME = "showNewRuntimeWizard"; //$NON-NLS-1$
        
        final Bundle serverUiBundle = Platform.getBundle( SERVER_UI_PLUGIN_ID );
        
        if( serverUiBundle == null )
        {
            this.newRuntimeButton.setEnabled( false );
            return;
        }

        try
        {
            final Class serverUiPluginClass = serverUiBundle.loadClass( CLASS_NAME );
            
            final Method method
                = serverUiPluginClass.getMethod( METHOD_NAME, Shell.class, String.class );
            
            final Object result = method.invoke( null, getShell(), null );
            
            if( result.equals( true ) )
            {
                this.fpjwc.refreshTargetableRuntimes();
            }
        }
        catch( Exception e )
        {
            FacetUiPlugin.log( e );
        }
    }

    private void handleWidgetDisposed()
    {
        removeDataModelListeners();
        
        this.colorGray.dispose();
    }
    
    private void refresh()
    {
        this.runtimes.refresh();

        final Set<IRuntime> untargetable = new HashSet<IRuntime>( RuntimeManager.getRuntimes() );
        untargetable.removeAll( this.fpjwc.getTargetableRuntimes() );
        
        this.runtimes.setCheckedElements( this.fpjwc.getTargetedRuntimes().toArray() );
    }
    
    private IRuntime getSelection()
    {
        final IStructuredSelection ssel 
            = (IStructuredSelection) this.runtimes.getSelection();
        
        if( ssel.isEmpty() )
        {
            return null;
        }
        else
        {
            return (IRuntime) ssel.getFirstElement();
        }
    }
    
    private void addDataModelListener( final IFacetedProjectListener listener,
                                       final IFacetedProjectEvent.Type... types )
    {
        this.fpjwc.addListener( listener, types );
        this.listeners.add( listener );
    }
    
    private void removeDataModelListeners()
    {
        for( IFacetedProjectListener listener : this.listeners )
        {
            this.fpjwc.removeListener( listener );
        }
    }
    
    private final class ContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            if( RuntimesPanel.this.showAllRuntimesSetting )
            {
                return RuntimeManager.getRuntimes().toArray();
            }
            else
            {
                return getFacetedProjectWorkingCopy().getTargetableRuntimes().toArray();
            }
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class LabelProvider

        implements ILabelProvider, IColorProvider
    
    {
        private final ImageRegistry imageRegistry;
        
        public LabelProvider()
        {
            this.imageRegistry = new ImageRegistry();
        }
        
        public String getText( final Object element )
        {
            return ( (IRuntime) element ).getLocalizedName();
        }

        public Image getImage( final Object element )
        {
            final IRuntime r = (IRuntime) element;
            
            final IRuntimeComponent rc = r.getRuntimeComponents().get( 0 );
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            
            final IRuntime primary = getFacetedProjectWorkingCopy().getPrimaryRuntime();
            final boolean isPrimary = primary != null && primary.equals( r );
            
            final String imgid
                = ( isPrimary ? "p:" : "s" ) //$NON-NLS-1$ //$NON-NLS-2$
                  + rct.getId();
            
            Image image = this.imageRegistry.get( imgid );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) rct.getAdapter( IDecorationsProvider.class );
                
                ImageDescriptor imgdesc = decprov.getIcon();
                
                if( isPrimary )
                {
                    imgdesc = new PrimaryRuntimeImageDescriptor( imgdesc );
                }
                
                this.imageRegistry.put( imgid, imgdesc );
                image = this.imageRegistry.get( imgid );
            }

            if( getFacetedProjectWorkingCopy().getTargetableRuntimes().contains( r ) )
            {
                if( RuntimesPanel.this.fpjwc.getTargetedRuntimes().contains( r ) )
                {
                    RuntimesPanel.this.runtimes.setChecked( r, true );
                }
                else
                {
                    RuntimesPanel.this.runtimes.setChecked( r, false );
                }
                
                return image;
            }
            else
            {
                RuntimesPanel.this.runtimes.setChecked( r, false );
                
                final String greyedId = rct.getId() + "##greyed##"; //$NON-NLS-1$
                Image greyed = this.imageRegistry.get( greyedId );
                
                if( greyed == null )
                {
                    greyed = new Image( null, image, SWT.IMAGE_GRAY );
                    this.imageRegistry.put( greyedId, greyed );
                }
                
                return greyed;
            }
        }
        
        public Color getForeground( final Object element )
        {
            if( ! getFacetedProjectWorkingCopy().getTargetableRuntimes().contains( element ) )
            {
                return RuntimesPanel.this.colorGray;
            }
            else
            {
                return null;
            }
        }

        public Color getBackground( final Object element )
        {
            return null;
        }

        public void dispose()
        {
            this.imageRegistry.dispose();
        }

        public boolean isLabelProperty( final Object element, 
                                        final String property )
        {
            return false;
        }

        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( final ILabelProviderListener listener ) {}
    }

    private final class Sorter

        extends ViewerSorter
    
    {
        public int compare( final Viewer viewer,
                            final Object a,
                            final Object b )
        {
            final IRuntime r1 = (IRuntime) a;
            final IRuntime r2 = (IRuntime) b;
            
            return r1.getLocalizedName().compareToIgnoreCase( r2.getLocalizedName() );
        }
    }
    
    private final class RuntimeComponentsContentProvider

        implements IStructuredContentProvider
    
    {
        public Object[] getElements( final Object element )
        {
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return new Object[] { NO_RUNTIME_SELECTED_PLACEHOLDER };
            }
            else
            {
                final IRuntime r = (IRuntime) element;
                return r.getRuntimeComponents().toArray();
            }
        }
    
        public void dispose() { }
    
        public void inputChanged( final Viewer viewer,
                                  final Object oldObject,
                                  final Object newObject ) {}
    }
    
    private final class RuntimeComponentsLabelProvider

        implements ILabelProvider
    
    {
        private final ImageRegistry imageRegistry = new ImageRegistry();
        
        public String getText( final Object element )
        {
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return Resources.noRuntimeSelectedLabel;
            }
            
            final IRuntimeComponent comp = (IRuntimeComponent) element;
            
            final IRuntimeComponentLabelProvider provider
                = (IRuntimeComponentLabelProvider) comp.getAdapter( IRuntimeComponentLabelProvider.class );
            
            if( provider == null )
            {
                final StringBuffer label = new StringBuffer();
                label.append( comp.getRuntimeComponentType().getId() );
                label.append( ' ' );
                label.append( comp.getRuntimeComponentVersion().getVersionString() );
                
                return label.toString();
            }
            else
            {
                return provider.getLabel();
            }
        }

        public Image getImage( final Object element )
        {
            if( element == NO_RUNTIME_SELECTED_PLACEHOLDER )
            {
                return null;
            }

            final IRuntimeComponent rc = (IRuntimeComponent) element;
            final IRuntimeComponentType rct = rc.getRuntimeComponentType();
            
            Image image = this.imageRegistry.get( rct.getId() );
            
            if( image == null )
            {
                final IDecorationsProvider decprov
                    = (IDecorationsProvider) rct.getAdapter( IDecorationsProvider.class );
                
                this.imageRegistry.put( rct.getId(), decprov.getIcon() );
                image = this.imageRegistry.get( rct.getId() );
            }

            return image;
        }
        
        public void dispose()
        {
            this.imageRegistry.dispose();
        }

        public boolean isLabelProperty( final Object element, 
                                        final String property )
        {
            return false;
        }

        public void addListener( final ILabelProviderListener listener ) {}
        public void removeListener( final ILabelProviderListener listener ) {}
    }
    
    private static final class PrimaryRuntimeImageDescriptor 
    
        extends CompositeImageDescriptor 
        
    {
        private static final String OVERLAY_IMG_LOCATION
            = "images/primary-runtime-overlay.gif"; //$NON-NLS-1$
        
        private static final ImageData OVERLAY
            = FacetUiPlugin.getImageDescriptor( OVERLAY_IMG_LOCATION ).getImageData();
        
        private final ImageData base;
        private final Point size;
        
        public PrimaryRuntimeImageDescriptor( final ImageDescriptor base ) 
        {
            this.base = base.getImageData();
            this.size = new Point( this.base.width, this.base.height ); 
        }
    
        protected void drawCompositeImage( final int width, 
                                           final int height ) 
        {
            drawImage( this.base, 0, 0 );
            drawImage( OVERLAY, 0, height - OVERLAY.height );
        }
    
        protected Point getSize()
        {
            return this.size;
        }
    }

    private static final class Resources
    
        extends NLS
        
    {
        public static String runtimeCompositionLabel;
        public static String makePrimaryLabel;
        public static String newRuntimeButtonLabel;
        public static String showAllRuntimes;
        public static String noRuntimeSelectedLabel;
        
        static
        {
            initializeMessages( RuntimesPanel.class.getName(), 
                                Resources.class );
        }
    }
    
}
