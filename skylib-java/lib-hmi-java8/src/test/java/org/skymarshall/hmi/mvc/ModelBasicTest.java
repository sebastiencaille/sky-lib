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
package org.skymarshall.hmi.mvc;

import static org.skymarshall.hmi.mvc.properties.Configuration.errorNotifier;
import static org.skymarshall.hmi.mvc.properties.Configuration.persistent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skymarshall.hmi.TestObject;
import org.skymarshall.hmi.mvc.converters.ConversionException;
import org.skymarshall.hmi.mvc.converters.Converters;
import org.skymarshall.hmi.mvc.persisters.FieldAccess;
import org.skymarshall.hmi.mvc.properties.AbstractProperty;
import org.skymarshall.hmi.mvc.properties.Configuration;
import org.skymarshall.hmi.mvc.properties.IntProperty;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;

public class ModelBasicTest extends Assert {

	private class TestHmiModel extends HmiModel {

		private final IntProperty integerProperty;

		private final ObjectProperty<String> stringProperty;

		public TestHmiModel(final HmiController controller) {
			super(controller);
			try {
				integerProperty = new IntProperty("IntegerProperty", propertySupport)
						.setTypedConfiguration(errorNotifier(errorProperty));
				stringProperty = new ObjectProperty<String>("StringProperty", propertySupport)
						.setTypedConfiguration(errorNotifier(errorProperty));
			} catch (final SecurityException e) {
				throw new IllegalStateException(e);
			}
			integerProperty.attach();
			stringProperty.attach();
		}
	}

	private HmiController controller;
	private TestHmiModel model;

	@Before
	public void init() {
		controller = new HmiController(new ControllerPropertyChangeSupport(this, false));
		model = new TestHmiModel(controller);
	}

	private static class TestBinding implements IComponentBinding<String> {

		private String value;
		private IComponentLink<String> onlyConverter;

		@Override
		public Object getComponent() {
			return "TestComponent";
		}

		@Override
		public void addComponentValueChangeListener(final IComponentLink<String> converter) {
			this.onlyConverter = converter;
		}

		public void setValue(final String value) {
			onlyConverter.setValueFromComponent(getComponent(), value);
		}

		@Override
		public void setComponentValue(final AbstractProperty source, final String value) {
			this.value = value;
		}

	}

	@Test
	public void testChain() throws NoSuchFieldException {
		final TestBinding binding = new TestBinding();
		model.integerProperty.bind(Converters.intToString()).bind(binding);
		controller.start();

		final TestObject testObject = new TestObject(321);
		model.integerProperty.setTypedConfiguration(persistent(testObject, testObjectValAccess()));

		model.integerProperty.load(this);
		assertEquals("321", binding.value);

		model.integerProperty.setValue(this, 123);
		assertEquals("123", binding.value);

		binding.setValue("456");
		assertEquals(456, model.integerProperty.getValue());

		model.integerProperty.save();
		assertEquals(456, testObject.val);

		// Test exception
		binding.setValue("bla");
		assertEquals(456, model.integerProperty.getValue());
		assertNotNull(model.errorProperty.getValue().getContent());
		assertEquals(ConversionException.class, model.errorProperty.getValue().getContent().getClass());
	}

	@Test
	public void testIdentityChain() {
		final TestBinding binding = new TestBinding();
		model.stringProperty.bind(binding);
		controller.start();

		model.stringProperty.setValue(this, "123");
		assertEquals("123", binding.value);

		binding.setValue("456");
		assertEquals("456", model.stringProperty.getValue());
	}

	@Test
	public void testAutoCommit() throws NoSuchFieldException {
		final TestObject testObject = new TestObject(123);

		model.integerProperty.setTypedConfiguration(persistent(testObject, testObjectValAccess()),
				Configuration::autoCommit);

		model.integerProperty.setValue(this, 456);
		assertEquals(456, testObject.val);
	}

	protected FieldAccess<Integer> testObjectValAccess() throws NoSuchFieldException {
		return FieldAccess.intAccess(TestObject.class.getField("val"));
	}

}
