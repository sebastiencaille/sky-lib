package ch.scaille.example.gui.model.impl;

import ch.scaille.example.gui.TestObject;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyGroup;
import ch.scaille.javabeans.properties.BooleanProperty;
import ch.scaille.javabeans.properties.ObjectProperty;

public class TableModelExampleModel {

	private final IPropertiesGroup changeSupport = PropertyChangeSupportController.mainGroup(this);

	public final BooleanProperty reverseOrder = new BooleanProperty("Order", changeSupport);

	public final BooleanProperty enableFilter = new BooleanProperty("Filter", changeSupport);

	public final ObjectProperty<TestObject> objectSelection = new ObjectProperty<>("Selection", changeSupport);

	public final PropertyGroup listChangers = new PropertyGroup();

	public TableModelExampleModel() {
		listChangers.addProperty(reverseOrder);
		listChangers.addProperty(enableFilter);
	}

	public void setCreated() {
		changeSupport.attachAll();
	}

}
