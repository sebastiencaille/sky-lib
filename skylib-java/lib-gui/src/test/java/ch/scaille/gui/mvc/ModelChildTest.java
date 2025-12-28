package ch.scaille.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		final var parentProperty = new ObjectProperty<Parent>("parent", propertySupport);
		final var childProperty = parentProperty.child("child", Parent::getChild, Parent::setChild);
		propertySupport.transmitChangesBothWays();

		final var parent = new Parent();
		parent.setChild("C1");
		parentProperty.setValue(this, parent);
		assertEquals("C1", childProperty.getValue());

		parent.setChild("C2");
		parentProperty.forceChanged(this);
		assertEquals("C2", childProperty.getValue());

		childProperty.setValue(this, "C3");
		assertEquals("C3", parent.getChild());

	}

}
