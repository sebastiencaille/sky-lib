package ch.skymarshall.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JCheckBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.skymarshall.gui.mvc.factories.Converters;
import ch.skymarshall.gui.mvc.properties.ObjectProperty;
import ch.skymarshall.gui.swing.factories.SwingBindings;

class SwingBindingsTest {
	private class TestGuiModel extends GuiModel {

		private final ObjectProperty<String> stringProperty = new ObjectProperty<String>("StringProperty", this);

		public TestGuiModel(final GuiController controller) {
			super(of(controller));
			getPropertySupport().attachAll();
		}
	}

	private GuiController controller;
	private TestGuiModel model;

	@BeforeEach
	public void init() {
		controller = new GuiController(new ControllerPropertyChangeSupport(this));
		model = new TestGuiModel(controller);
	}

	@Test
	void testItemSelectable() {
		final JCheckBox cb = new JCheckBox("UnitTest");
		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		final IBindingController cbBinding = model.stringProperty
				.bind(Converters.<String, Boolean>converter(Boolean::valueOf, (b) -> b.toString()))
				.bind(SwingBindings.selected(cb));
		model.stringProperty.attach();

		assertEquals(Boolean.FALSE.toString(), model.stringProperty.getValue());
		assertFalse(cb.isSelected(), () -> "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		assertTrue(cb.isSelected(), () -> "cb.isSelected");

		cb.setSelected(true);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());

		cbBinding.unbind();

		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		assertTrue(cb.isSelected(), () -> "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		cb.setSelected(false);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());
	}

}
