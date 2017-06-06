package org.skymarshall.hmi.mvc;

public class AutoCommitListener implements IPropertyEventListener {

	@Override
	public void propertyModified(final Object caller, final PropertyEvent event) {
		event.getProperty().save();
	}
}
