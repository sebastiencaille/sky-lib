package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Interface to the Component binding.
 * <p>
 *
 * @param <T> the data type handled by the component
 */
@NullMarked
public interface IComponentLink<T extends @Nullable Object> {

    void setValueFromComponent(Object component, T componentValue);

    void reloadComponentValue();

    void unbind();

}
