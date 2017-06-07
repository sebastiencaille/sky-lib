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
package org.skymarshall.example.hmi;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import org.skymarshall.hmi.mvc.ControllerPropertyChangeSupport;
import org.skymarshall.hmi.mvc.HmiController;
import org.skymarshall.hmi.mvc.HmiModel;
import org.skymarshall.hmi.mvc.IComponentBinding;
import org.skymarshall.hmi.mvc.IComponentLink;
import org.skymarshall.hmi.mvc.IObjectHmiModel;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.persisters.ObjectProviderPersister;
import org.skymarshall.hmi.mvc.persisters.Persisters;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.ErrorProperty;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.mvc.properties.Properties;

public class TestObjectHmiModel extends HmiModel implements IObjectHmiModel<org.skymarshall.example.hmi.TestObject> {
	public static final String ASECOND_VALUE = "ASecondValue";

	private static final Field ASECOND_VALUE_FIELD;

	public static final String AFIRST_VALUE = "AFirstValue";

	private static final Field AFIRST_VALUE_FIELD;

	static {
		try {
			AFIRST_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aFirstValue");
			ASECOND_VALUE_FIELD = org.skymarshall.example.hmi.TestObject.class.getDeclaredField("aSecondValue");
			AccessibleObject.setAccessible(new AccessibleObject[] { AFIRST_VALUE_FIELD, ASECOND_VALUE_FIELD }, true);
		} catch (final Exception e) {
			throw new IllegalStateException("Cannot initialize class", e);
		}
	}
	private final ObjectProviderPersister.CurrentObjectProvider currentObjectProvider = new ObjectProviderPersister.CurrentObjectProvider();

	protected final IntProperty aSecondValueProperty;
	protected final ObjectProperty<java.lang.String> aFirstValueProperty;

	public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport,
			final ErrorProperty errorProperty) {
		super(propertySupport, errorProperty);
		aSecondValueProperty = Properties.of(new IntProperty(prefix + "-ASecondValue", propertySupport))
				.persistent(Persisters.from(currentObjectProvider, FieldAccess.intAccess(ASECOND_VALUE_FIELD)))
				.setErrorNotifier(errorProperty).getProperty();
		aFirstValueProperty = Properties
				.of(new ObjectProperty<java.lang.String>(prefix + "-AFirstValue", propertySupport))
				.persistent(Persisters.from(currentObjectProvider,
						FieldAccess.<java.lang.String>create(AFIRST_VALUE_FIELD)))
				.setErrorNotifier(errorProperty).getProperty();
	}

	public TestObjectHmiModel(final String prefix, final HmiController controller) {
		this(prefix, controller.getPropertySupport(),
				HmiModel.createErrorProperty(prefix + "-Error", controller.getPropertySupport()));
	}

	public TestObjectHmiModel(final HmiController controller) {
		this("TestObject", controller.getPropertySupport(),
				HmiModel.createErrorProperty("TestObject-Error", controller.getPropertySupport()));
	}

	public TestObjectHmiModel(final String prefix, final ControllerPropertyChangeSupport propertySupport) {
		this(prefix, propertySupport, HmiModel.createErrorProperty(prefix + "-Error", propertySupport));
	}

	public TestObjectHmiModel(final ControllerPropertyChangeSupport propertySupport) {
		this("TestObject", propertySupport, HmiModel.createErrorProperty("TestObject-Error", propertySupport));
	}

	public IntProperty getASecondValueProperty() {
		return aSecondValueProperty;
	}

	public ObjectProperty<java.lang.String> getAFirstValueProperty() {
		return aFirstValueProperty;
	}

	@Override
	public void setCurrentObject(final org.skymarshall.example.hmi.TestObject value) {
		currentObjectProvider.setObject(value);
	}

	@Override
	public void load() {
		aSecondValueProperty.load(this);
		aFirstValueProperty.load(this);
	}

	@Override
	public void save() {
		aSecondValueProperty.save();
		aFirstValueProperty.save();
	}

	public IComponentBinding<org.skymarshall.example.hmi.TestObject> binding() {
		return new IComponentBinding<org.skymarshall.example.hmi.TestObject>() {
			@Override
			public Object getComponent() {
				return TestObjectHmiModel.this;
			}

			@Override
			public void addComponentValueChangeListener(
					final IComponentLink<org.skymarshall.example.hmi.TestObject> link) {
				// nope
			}

			@Override
			public void setComponentValue(final AbstractProperty source,
					final org.skymarshall.example.hmi.TestObject value) {
				if (value != null) {
					setCurrentObject(value);
					load();
				}
			}
		};
	}
}
