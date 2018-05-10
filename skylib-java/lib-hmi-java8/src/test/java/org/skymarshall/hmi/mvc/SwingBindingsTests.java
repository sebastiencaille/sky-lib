package org.skymarshall.hmi.mvc;

import static org.skymarshall.hmi.mvc.properties.Configuration.errorNotifier;

import javax.swing.JCheckBox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skymarshall.hmi.mvc.converters.Converters;
import org.skymarshall.hmi.mvc.properties.ObjectProperty;
import org.skymarshall.hmi.swing.bindings.SwingBindings;

public class SwingBindingsTests extends Assert {
	private class TestHmiModel extends HmiModel {

		private final ObjectProperty<String> stringProperty;

		public TestHmiModel(final HmiController controller) {
			super(controller);
			try {
				stringProperty = new ObjectProperty<String>("StringProperty", propertySupport)
						.setTypedConfiguration(errorNotifier(errorProperty));
			} catch (final SecurityException e) {
				throw new IllegalStateException(e);
			}
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

	@Test
	public void testItemSelectable() {
		final JCheckBox cb = new JCheckBox("UnitTest");
		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		final IBindingController cbBinding = model.stringProperty
				.bind(Converters.<String, Boolean>converter(Boolean::valueOf, (b) -> b.toString()))
				.bind(SwingBindings.selection(cb));
		model.stringProperty.attach();

		assertEquals(Boolean.FALSE.toString(), model.stringProperty.getValue());
		assertFalse(cb.isSelected());

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		assertTrue(cb.isSelected());

		cb.setSelected(true);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());

		cbBinding.unbind();

		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		assertTrue(cb.isSelected());

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		cb.setSelected(false);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());
	}

}
