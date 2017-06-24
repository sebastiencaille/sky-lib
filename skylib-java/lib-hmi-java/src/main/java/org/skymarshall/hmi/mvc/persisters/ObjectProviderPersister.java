/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.persisters;

import org.skymarshall.hmi.mvc.properties.IPersister;

public class ObjectProviderPersister<T> implements
        IPersister<T> {

    public interface IObjectProvider {
        Object getObject();
    }

    public static class CurrentObjectProvider implements
            IObjectProvider {

        private Object object;

        @Override
        public Object getObject() {
            return object;
        }

        public void setObject(final Object object) {
            this.object = object;
        }

    }

    private final FieldAccess<T>  fieldAccess;
    private final IObjectProvider target;

    public ObjectProviderPersister(final IObjectProvider target, final FieldAccess<T> fieldAccess) {
        this.target = target;
        this.fieldAccess = fieldAccess;
    }

    @Override
    public T get() {
        final Object targetObject = target.getObject();
        if (targetObject == null) {
            throw new IllegalStateException("No target object defined");
        }
        return fieldAccess.get(targetObject);
    }

    @Override
    public void set(final T value) {
        final Object targetObject = target.getObject();
        if (targetObject == null) {
            throw new IllegalStateException("No target object defined");
        }
        fieldAccess.set(targetObject, value);
    }

}
