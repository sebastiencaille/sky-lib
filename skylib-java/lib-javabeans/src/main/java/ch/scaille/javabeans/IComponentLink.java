package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Interface to the Component binding.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the data type handled by the component
 */
@NullMarked
public interface IComponentLink<T> {



	void setValueFromComponent(Object component, @Nullable T componentValue);

	void reloadComponentValue();

	void unbind();

}
