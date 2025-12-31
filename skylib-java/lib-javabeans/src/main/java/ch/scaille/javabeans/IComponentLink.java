package ch.scaille.javabeans;

import org.jspecify.annotations.NonNull;

/**
 * Interface to the Component binding.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the data type handled by the component
 */
public interface IComponentLink<T> {

	void setValueFromComponent(@NonNull Object component, T componentValue);

	void reloadComponentValue();

	void unbind();

}
