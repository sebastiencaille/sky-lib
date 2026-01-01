package ch.scaille.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JCheckBox;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.scaille.gui.swing.factories.SwingBindings;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.IVetoer.TransmitMode;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.properties.ObjectProperty;

@NullMarked
class SwingBindingsTest {
	private static class TestGuiModel extends GuiModel {

		private final ObjectProperty<@Nullable String> stringProperty = new ObjectProperty<>("StringProperty", this, null);

		public TestGuiModel(final GuiController controller) {
			super(of(controller));
			activate();
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
		final var cb = new JCheckBox("UnitTest");
		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		final var cbBinding = model.stringProperty
				.bind(Converters.<String, Boolean>converter(Boolean::valueOf, Object::toString))
				.bind(SwingBindings.selected(cb));
		model.stringProperty.setTransmitMode(TransmitMode.TRANSMIT);

		assertEquals(Boolean.FALSE.toString(), model.stringProperty.getValue());
		assertFalse(cb.isSelected(), "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		assertTrue(cb.isSelected(), "cb.isSelected");

		cb.setSelected(true);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());

		cbBinding.disposeBindings();

		model.stringProperty.setValue(this, Boolean.FALSE.toString());
		assertTrue(cb.isSelected(), "cb.isSelected");

		model.stringProperty.setValue(this, Boolean.TRUE.toString());
		cb.setSelected(false);
		assertEquals(Boolean.TRUE.toString(), model.stringProperty.getValue());
	}

}
