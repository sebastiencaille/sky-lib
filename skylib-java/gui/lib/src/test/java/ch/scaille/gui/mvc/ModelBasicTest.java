package ch.scaille.gui.mvc;

import static ch.scaille.javabeans.properties.Configuration.persistent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.scaille.javabeans.properties.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.scaille.gui.TestObject;
import ch.scaille.javabeans.IComponentBinding;
import ch.scaille.javabeans.IComponentChangeSource;
import ch.scaille.javabeans.IComponentLink;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.converters.ConversionException;
import ch.scaille.javabeans.converters.Converters;
import ch.scaille.javabeans.persisters.IPersisterFactory;
import ch.scaille.javabeans.persisters.Persisters;

import java.util.Objects;

@NullMarked
class ModelBasicTest {

	private static class TestGuiModel extends GuiModel {

		private final IntProperty integerProperty = new IntProperty("IntegerProperty", this);

		private final ObjectProperty<@Nullable String> stringProperty = new ObjectProperty<>("StringProperty", this, null);

		public TestGuiModel(final GuiController controller) {
			super(of(controller));
			activate();
		}
	}

	private GuiController controller;
	private TestGuiModel model;

	@BeforeEach
	void init() {
		controller = new GuiController(new PropertyChangeSupportController(this));
		model = new TestGuiModel(controller);
	}

	private static class TestBinding implements IComponentBinding<String> {

		@Nullable
		private String value;
		@Nullable
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
			Objects.requireNonNull(onlyConverter).setValueFromComponent(this, value);
		}

		@Override
		public void setComponentValue(final IComponentChangeSource source, final String value) {
			this.value = value;
		}

	}

	@Test
	void testChain() {
		final var testBinding = new TestBinding();
		model.integerProperty.bind(Converters.intToString()).bind(testBinding);
		controller.activate();

		final var testObject = new TestObject(321);
		model.integerProperty.configureTyped(persistent(testObject, testObjectValAccess()));

		model.integerProperty.load(this);
		assertEquals("321", testBinding.value);

		model.integerProperty.setValue(this, 123);
		assertEquals("123", testBinding.value);

		testBinding.setValue("456");
		assertEquals(456, model.integerProperty.getValue());

		model.integerProperty.save();
		assertEquals(456, testObject.getVal());

		// Test exception
		testBinding.setValue("bla");
		assertEquals(456, model.integerProperty.getValue());
		final var errorProperty = (ErrorSet) model.getErrorNotifier();
		final var errorValue = errorProperty.getLastError().getValue();
		Assertions.assertNotNull(errorValue, "errorProperty.getLastError().getValue()");
		Assertions.assertNotNull(errorValue.content(), "errorProperty.getLastError().getValue().getContent()");
		assertEquals(ConversionException.class, errorValue.content().getClass());
	}

	@Test
	void testIdentityChain() {
		final var testBinding = new TestBinding();
		model.stringProperty.bind(testBinding);
		controller.activate();

		model.stringProperty.setValue(this, "123");
		assertEquals("123", testBinding.value);

		testBinding.setValue("456");
		assertEquals("456", model.stringProperty.getValue());
	}

	@Test
	void testAutoCommit() {
		final var testObject = new TestObject(123);

		model.integerProperty.configureTyped(persistent(testObject, testObjectValAccess()), Configuration::autoCommit);

		model.integerProperty.setValue(this, 456);
		assertEquals(456, testObject.getVal());
	}

	protected IPersisterFactory<TestObject, Integer> testObjectValAccess() {
		return Persisters.persister(TestObject::getVal, TestObject::setVal);
	}

}
