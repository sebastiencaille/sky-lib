package ch.scaille.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JCheckBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.IBindingController;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.properties.ObjectProperty;

class SwingBindingsTest {
	private static class TestGuiModel extends GuiModel {

		private final ObjectProperty<String> stringProperty = new ObjectProperty<>("StringProperty", this);

		public TestGuiModel(final GuiController controller) {
			super(of(controller));
			getPropertySupport().flushChanges();
		}
	}

    private TestGuiModel model;

	@BeforeEach
	void init() {
        final var controller = new GuiController(new PropertyChangeSupportController(this));
		model = new TestGuiModel(controller);
	}

	@Test
	void testItemSelectable() {
		final JCheckBox cb = new JCheckBox("UnitTest");
		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		final IBindingController cbBinding = model.stringProperty
				.bind(Converters.converter(Boolean::valueOf, Object::toString))
				.bind(SwingBindings.selected(cb));
		model.stringProperty.flush();

		assertEquals(Boolean.FALSE.toString(), model.stringProperty.getValue());
		assertFalse(cb.isSelected(), "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		assertTrue(cb.isSelected(), "cb.isSelected");

		cb.setSelected(true);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());

		cbBinding.unbind();

		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		assertTrue(cb.isSelected(), "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		cb.setSelected(false);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());
	}

}
