package ch.scaille.gui.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.scaille.gui.mvc.properties.ObjectProperty;

class ModelChildTest {

	private static class Parent {

		String child;

		public String getChild() {
			return child;
		}

		public void setChild(final String child) {
			this.child = child;
		}

	}

	@Test
	void testChildProperty() {

		final var propertySupport = ControllerPropertyChangeSupport.mainGroup(this);
		final var parentProperty = new ObjectProperty<Parent>("parent", propertySupport);
		final var childProperty = parentProperty.child("child", Parent::getChild, Parent::setChild);
		propertySupport.attachAll();

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
