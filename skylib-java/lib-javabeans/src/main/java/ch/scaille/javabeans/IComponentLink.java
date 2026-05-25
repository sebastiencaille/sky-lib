package ch.scaille.javabeans;

import org.jspecify.annotations.Nullable;

/**
 * Interface to the Component binding.
 * <p>
 *
 * @param <T> the data type handled by the component
 */

public interface IComponentLink<T extends @Nullable Object> {

	void setValueFromComponent(Object component, T componentValue, boolean force);
	
    default void setValueFromComponent(Object component, T componentValue) {
    	setValueFromComponent(component, componentValue, false);
    }

    void reloadComponentValue();

    void unbind();

}
