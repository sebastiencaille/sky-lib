package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.IPropertyEventListener;

public class AutoCommitListener implements IPropertyEventListener {

	@Override
	public void propertyModified(final Object caller, final PropertyEvent event) {
		event.property().save();
	}
}
