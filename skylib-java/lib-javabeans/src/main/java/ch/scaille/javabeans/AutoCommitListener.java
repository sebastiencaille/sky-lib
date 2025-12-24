package ch.scaille.javabeans;

import ch.scaille.javabeans.properties.IPropertyEventListener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AutoCommitListener implements IPropertyEventListener {

	@Override
	public void propertyModified(final Object caller, final PropertyEvent event) {
		event.property().save();
	}
}
