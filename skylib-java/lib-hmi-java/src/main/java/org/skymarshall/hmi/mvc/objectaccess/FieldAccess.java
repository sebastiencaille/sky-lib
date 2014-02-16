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

/**
 * Access to object fields
 * <p>
 * 
 * @author Sebastien Caille
 *
 * @param <T>
 */
public abstract class FieldAccess<T> implements
        IObjectAccess<T> {

    public static IObjectAccess<Boolean> booleanAccess(final Field field) {
        field.setAccessible(true);

        return new IObjectAccess<Boolean>() {

            @Override
            public Boolean get(final Object object) {
                try {
                    return Boolean.valueOf(field.getBoolean(object));
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

            @Override
            public void set(final Object object, final Boolean value) {
                try {
                    field.setBoolean(object, value.booleanValue());
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

        };
    }

    public static IObjectAccess<Integer> intAccess(final Field field) {
        field.setAccessible(true);

        return new IObjectAccess<Integer>() {

            @Override
            public Integer get(final Object object) {
                try {
                    return Integer.valueOf(field.getInt(object));
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

            @Override
            public void set(final Object object, final Integer value) {
                try {
                    field.setInt(object, value.intValue());
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

        };
    }

    public static IObjectAccess<Long> longAccess(final Field field) {
        field.setAccessible(true);

        return new IObjectAccess<Long>() {

            @Override
            public Long get(final Object object) {
                try {
                    return Long.valueOf(field.getLong(object));
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

            @Override
            public void set(final Object object, final Long value) {
                try {
                    field.setLong(object, value.longValue());
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

        };
    }

    public static IObjectAccess<Float> floatAccess(final Field field) {
        field.setAccessible(true);

        return new IObjectAccess<Float>() {

            @Override
            public Float get(final Object object) {
                try {
                    return Float.valueOf(field.getFloat(object));
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

            @Override
            public void set(final Object object, final Float value) {
                try {
                    field.setFloat(object, value.floatValue());
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

        };
    }

    public static <T> IObjectAccess<T> objectAccess(final Field field, final Class<T> clazz) {
        field.setAccessible(true);

        return new IObjectAccess<T>() {

            @Override
            public T get(final Object object) {
                try {
                    return clazz.cast(field.get(object));
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

            @Override
            public void set(final Object object, final T value) {
                try {
                    field.set(object, value);
                } catch (final IllegalAccessException e) {
                    throw new IllegalStateException("Unable to access field", e);
                }
            }

        };
    }

    @SuppressWarnings("unchecked")
    public static <T> IObjectAccess<T> create(final Field field) {
        if (int.class.equals(field.getType())) {
            return (IObjectAccess<T>) intAccess(field);
        } else if (long.class.equals(field.getType())) {
            return (IObjectAccess<T>) longAccess(field);
        } else if (float.class.equals(field.getType())) {
            return (IObjectAccess<T>) floatAccess(field);
        } else if (boolean.class.equals(field.getType())) {
            return (IObjectAccess<T>) booleanAccess(field);
        } else {
            throw new IllegalArgumentException("Unhandled type " + field.getType().getName());
        }
    }

    public static <T> IObjectAccess<T> create(final Field field, final Class<T> type) {
        return FieldAccess.<T> objectAccess(field, type);
    }
}
