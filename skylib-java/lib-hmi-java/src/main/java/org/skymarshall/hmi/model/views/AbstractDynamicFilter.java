/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.model.views;

import org.skymarshall.hmi.model.IFilter;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;

public abstract class AbstractDynamicFilter<T> implements
IFilter<T> {

    private IListViewOwner<T> viewOwner;

    public void attach(final IListViewOwner<T> aViewOwner) {
        this.viewOwner = aViewOwner;
    }

    protected class FilterUpdateBinding<U> implements
            IComponentBinding<U> {

        @Override
        public Object getComponent() {
            return AbstractDynamicFilter.this;
        }

        @Override
        public void addComponentValueChangeListener(final IComponentLink<U> link) {
            // read-only
        }

        @Override
        public void setComponentValue(final AbstractProperty source, final U value) {
            if (viewOwner != null) {
                viewOwner.viewUpdated();
            }

        }
    }

}
