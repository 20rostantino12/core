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
package org.jboss.as.console.client.shared.subsys.undertow;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;

/**
 * @author Harald Pehl
 * @date 10/30/2012
 */
public class ServletViewImpl extends SuspendableViewImpl implements ServletView
{
    private LayoutPanel container;

    @Override
    public void show(Widget widget) {
        container.clear();
        container.add(widget);
        //container.setWidgetTopHeight(widget, 0, Style.Unit.PX, 100, Style.Unit.PCT);
    }

    @Override
    public Widget createWidget() {
        this.container = new LayoutPanel();
        this.container.setStyleName("fill-layout");
        return container;
    }
}

