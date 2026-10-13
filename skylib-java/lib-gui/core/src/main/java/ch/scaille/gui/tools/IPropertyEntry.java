package ch.scaille.gui.tools;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.properties.AbstractProperty;
import org.jspecify.annotations.Nullable;

public interface IPropertyEntry {

	String getLabel();

	@Nullable
	String getTooltip();

	boolean isReadOnly();

	AbstractProperty getProperty();

	Class<?> getPropertyType();

	void loadFromCurrentObject(final Object caller);

	void saveInCurrentObject();

	/**
	 * Allows to get the typed EndOfChain of the entry
	 *
	 * @return the typed EndOfChain
	 */
	<R> IChainBuilderFactory<R> getChain(Class<R> expectedType);

}
