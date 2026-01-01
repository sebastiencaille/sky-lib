package ch.scaille.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ObjectProperty;
import lombok.Getter;
import lombok.Setter;

class ModelChildTest {

	@Getter
	@Setter
	private static class Parent {

		private String child;
	}

	@Test
	void testChildProperty() {

		final var propertySupport = PropertyChangeSupportController.mainGroup(this);
		final var parentProperty = new ObjectProperty<Parent>("parent", propertySupport, new Parent());
		final var childProperty = parentProperty.child("child", Parent::getChild, Parent::setChild);
		propertySupport.transmitChangesBothWays();

		final var parent = parentProperty.getValue();
		parent.setChild("C1");
		parentProperty.forceChanged(this);
		assertEquals("C1", childProperty.getValue());

		childProperty.setValue(this, "C2");
		assertEquals("C2", parent.getChild());

	}

}
