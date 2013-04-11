/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.tools.modelling.workbench.gin;

import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import org.jboss.as.console.client.tools.modelling.workbench.ApplicationPresenter;
import org.jboss.as.console.client.tools.modelling.workbench.preview.PreviewPresenter;
import org.jboss.as.console.spi.GinExtension;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
@GinExtension("org.jboss.mbui.MBUI")
public interface WorkbenchGinjector extends Ginjector
{

    // ------------------------------------------------------- presenters (a-z)

    Provider<ApplicationPresenter> getWorkbenchPresenter();
    Provider<PreviewPresenter> getPreviewPresenter();
}
