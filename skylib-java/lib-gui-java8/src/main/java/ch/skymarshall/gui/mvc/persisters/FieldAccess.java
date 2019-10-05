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
package ch.skymarshall.gui.mvc.persisters;

import java.lang.reflect.Field;

import ch.skymarshall.gui.mvc.properties.IPersister;

/**
 * Access to object fields
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T>
 */
public interface FieldAccess<T> {

	public abstract T get(Object object);

	public abstract void set(Object object, T value);

	public static IllegalStateException unableToAccessFieldException(final IllegalAccessException e) {
		return new IllegalStateException("Unable to access field", e);
	}

	public default IPersister<T> asPersister(final Object object) {
		return new IPersister<T>() {
			@Override
			public T get() {
				return FieldAccess.this.get(object);
			}

			@Override
			public void set(final T value) {
				FieldAccess.this.set(object, value);
			}
		};
	}

	public static FieldAccess<Boolean> booleanAccess(final Field field) {
		field.setAccessible(true);

		return new FieldAccess<Boolean>() {

			@Override
			public Boolean get(final Object object) {
				try {
					return Boolean.valueOf(field.getBoolean(object));
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

			@Override
			public void set(final Object object, final Boolean value) {
				try {
					field.setBoolean(object, value.booleanValue());
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

		};
	}

	public static FieldAccess<Integer> intAccess(final Field field) {
		field.setAccessible(true);

		return new FieldAccess<Integer>() {

			@Override
			public Integer get(final Object object) {
				try {
					return Integer.valueOf(field.getInt(object));
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

			@Override
			public void set(final Object object, final Integer value) {
				try {
					field.setInt(object, value.intValue());
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

		};
	}

	public static FieldAccess<Long> longAccess(final Field field) {
		field.setAccessible(true);

		return new FieldAccess<Long>() {

			@Override
			public Long get(final Object object) {
				try {
					return Long.valueOf(field.getLong(object));
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

			@Override
			public void set(final Object object, final Long value) {
				try {
					field.setLong(object, value.longValue());
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

		};
	}

	public static FieldAccess<Float> floatAccess(final Field field) {
		field.setAccessible(true);

		return new FieldAccess<Float>() {

			@Override
			public Float get(final Object object) {
				try {
					return Float.valueOf(field.getFloat(object));
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

			@Override
			public void set(final Object object, final Float value) {
				try {
					field.setFloat(object, value.floatValue());
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

		};
	}

	public static <T> FieldAccess<T> objectAccess(final Field field) {
		field.setAccessible(true);

		return new FieldAccess<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T get(final Object object) {
				try {
					return (T) (field.get(object));
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

			@Override
			public void set(final Object object, final T value) {
				try {
					field.set(object, value);
				} catch (final IllegalAccessException e) {
					throw unableToAccessFieldException(e);
				}
			}

		};
	}

	@SuppressWarnings("unchecked")
	public static <T> FieldAccess<T> create(final Field field) {
		if (int.class.equals(field.getType())) {
			return (FieldAccess<T>) intAccess(field);
		} else if (long.class.equals(field.getType())) {
			return (FieldAccess<T>) longAccess(field);
		} else if (float.class.equals(field.getType())) {
			return (FieldAccess<T>) floatAccess(field);
		} else if (boolean.class.equals(field.getType())) {
			return (FieldAccess<T>) booleanAccess(field);
		} else {
			return FieldAccess.<T>objectAccess(field);
		}
	}

}
