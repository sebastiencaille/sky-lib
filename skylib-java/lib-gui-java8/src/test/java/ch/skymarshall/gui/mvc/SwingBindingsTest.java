package ch.skymarshall.gui.mvc;

import static ch.skymarshall.gui.mvc.properties.Configuration.errorNotifier;

import javax.swing.JCheckBox;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.skymarshall.gui.mvc.factories.Converters;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.bindings.SwingBindings;

public class SwingBindingsTest extends Assert {
	private class TestGuiModel extends GuiModel {

		private final ObjectProperty<String> stringProperty;

		public TestGuiModel(final GuiController controller) {
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

	private GuiController controller;
	private TestGuiModel model;

	@Before
	public void init() {
		controller = new GuiController(new ControllerPropertyChangeSupport(this, false));
		model = new TestGuiModel(controller);
	}

	@Test
	public void testItemSelectable() {
		final JCheckBox cb = new JCheckBox("UnitTest");
		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		final IBindingController cbBinding = model.stringProperty
				.bind(Converters.<String, Boolean>converter(Boolean::valueOf, (b) -> b.toString()))
				.bind(SwingBindings.selected(cb));
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
