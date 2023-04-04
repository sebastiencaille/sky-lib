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

		final IPropertiesGroup propertySupport = ControllerPropertyChangeSupport.mainGroup(this);
		final ObjectProperty<Parent> parent = new ObjectProperty<>("parent", propertySupport);
		final ObjectProperty<String> child = parent.child("child", Parent::getChild, Parent::setChild);
		propertySupport.attachAll();

		final Parent p = new Parent();
		p.setChild("C1");
		parent.setValue(this, p);
		assertEquals("C1", child.getValue());

		p.setChild("C2");
		parent.forceChanged(this);
		assertEquals("C2", child.getValue());

		child.setValue(this, "C3");
		assertEquals("C3", p.getChild());

	}

}
