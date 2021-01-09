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
package ch.skymarshall.gui.mvc;

import static ch.skymarshall.gui.mvc.properties.Configuration.errorNotifier;
import static ch.skymarshall.gui.mvc.properties.Configuration.persistent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.gui.TestObject;
import ch.skymarshall.gui.mvc.converters.ConversionException;
import ch.skymarshall.gui.mvc.factories.Converters;
import ch.skymarshall.gui.mvc.factories.Persisters;
import ch.skymarshall.gui.mvc.persisters.IPersisterFactory;
import ch.skymarshall.gui.mvc.properties.AbstractProperty;
import ch.skymarshall.gui.mvc.properties.Configuration;
import ch.skymarshall.gui.mvc.properties.IntProperty;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;

public class ModelBasicTest extends Assert {

	private class TestGuiModel extends GuiModel {

		private final IntProperty integerProperty;

		private final ObjectProperty<String> stringProperty;

		public TestGuiModel(final GuiController controller) {
			super(controller);
			try {
				integerProperty = new IntProperty("IntegerProperty", propertySupport)
						.configureTyped(errorNotifier(errorProperty));
				stringProperty = new ObjectProperty<String>("StringProperty", propertySupport)
						.configureTyped(errorNotifier(errorProperty));
			} catch (final SecurityException e) {
				throw new IllegalStateException(e);
			}
			integerProperty.attach();
			stringProperty.attach();
		}
	}

	private GuiController controller;
	private TestGuiModel model;

	@Before
	public void init() {
		controller = new GuiController(new ControllerPropertyChangeSupport(this, false));
		model = new TestGuiModel(controller);
	}

	private static class TestBinding implements IComponentBinding<String> {

		private String value;
		private IComponentLink<String> onlyConverter;

		@Override
		public void addComponentValueChangeListener(final IComponentLink<String> converter) {
			this.onlyConverter = converter;
		}

		@Override
		public void removeComponentValueChangeListener() {
			// nope
		}

		public void setValue(final String value) {
			onlyConverter.setValueFromComponent(this, value);
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
		controller.activate();

		final TestObject testObject = new TestObject(321);
		model.integerProperty.configureTyped(persistent(testObject, testObjectValAccess()));

		model.integerProperty.load(this);
		assertEquals("321", binding.value);

		model.integerProperty.setValue(this, 123);
		assertEquals("123", binding.value);

		binding.setValue("456");
		assertEquals(456, model.integerProperty.getValue());

		model.integerProperty.save();
		assertEquals(456, testObject.getVal());

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
		controller.activate();

		model.stringProperty.setValue(this, "123");
		assertEquals("123", binding.value);

		binding.setValue("456");
		assertEquals("456", model.stringProperty.getValue());
	}

	@Test
	public void testAutoCommit() throws NoSuchFieldException {
		final TestObject testObject = new TestObject(123);

		model.integerProperty.configureTyped(persistent(testObject, testObjectValAccess()), Configuration::autoCommit);

		model.integerProperty.setValue(this, 456);
		assertEquals(456, testObject.getVal());
	}

	protected IPersisterFactory<TestObject, Integer> testObjectValAccess() throws NoSuchFieldException {
		return Persisters.unsafeFieldAccess(TestObject.class.getDeclaredField("val"));
	}

}
