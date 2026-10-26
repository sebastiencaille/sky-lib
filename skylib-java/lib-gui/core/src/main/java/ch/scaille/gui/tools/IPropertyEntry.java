package ch.scaille.gui.tools;

import org.jspecify.annotations.Nullable;

import ch.scaille.javabeans.IChainBuilderFactory;
import ch.scaille.javabeans.properties.IProperty;

public interface IPropertyEntry {

	String getLabel();

	@Nullable
	String getTooltip();

	boolean isReadOnly();

	IProperty getProperty();

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
