/*******************************************************************************
 * Copyright (c) 2013 Sebastien Caille.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package org.skymarshall.hmi.mvc.objectaccess;

import java.lang.reflect.Field;

public class FieldAccess<T> implements
        IObjectAccess<T> {

    private final Field    field;
    private final Class<T> clazz;

    protected FieldAccess(final Field field, final Class<T> clazz) {
        this.field = field;
        this.clazz = clazz;
    }

    protected FieldAccess(final Field field) {
        this.field = field;
        field.setAccessible(true);
        this.clazz = null;
    }

    @Override
    public boolean getBoolean(final Object object) {
        try {
            return field.getBoolean(object);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public void setBoolean(final Object object, final boolean value) {
        try {
            field.setBoolean(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public int getInt(final Object object) {
        try {
            return field.getInt(object);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public void setInt(final Object object, final int value) {
        try {
            field.setInt(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public long getLong(final Object object) {
        try {
            return field.getLong(object);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public void setLong(final Object object, final long value) {
        try {
            field.setLong(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public float getFloat(final Object object) {
        try {
            return field.getFloat(object);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public void setFloat(final Object object, final float value) {
        try {
            field.setFloat(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public T getObject(final Object object) {
        if (clazz == null) {
            throw new IllegalStateException("Field type not defined: " + field);
        }
        try {
            return clazz.cast(field.get(object));
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }
    }

    @Override
    public void setObject(final Object object, final T value) {
        if (clazz == null) {
            throw new IllegalStateException("Field type not defined: " + field);
        }
        try {
            field.set(object, value);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Unable to access field", e);
        }

    }

    public static final IObjectAccess<?> create(final Field field) {
        if (!field.getType().isPrimitive()) {
            throw new IllegalArgumentException("field is not a primitive type. Use create(Field, Class):" + field);
        }
        return new FieldAccess<Object>(field);
    }

    public static final <U> IObjectAccess<U> create(final Field field, final Class<U> expectedType) {
        if (field.getType().isPrimitive()) {
            throw new IllegalArgumentException("field is a primitive type. Use create(Field):" + field);
        }
        return new FieldAccess<U>(field, expectedType);
    }

    public static final <U> IObjectAccess<U> create(final Class<?> declaringClass, final String fieldname,
            final Class<U> clazz) {
        Field field;
        try {
            field = declaringClass.getDeclaredField(fieldname);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Cannot get field " + fieldname + " from " + declaringClass, e);
        }
        if (field.getType().isPrimitive()) {
            throw new IllegalArgumentException("field is a primitive type. Use create(Field):" + field);
        }
        return new FieldAccess<U>(field, clazz);
    }

}
