package ch.scaille.javabeans;

/**
 * Interface to the Component binding.
 * <p>
 *
 * @author Sebastien Caille
 *
 * @param <T> the data type handled by the component
 */
public interface IComponentLink<T> {

	void setValueFromComponent(Object component, T componentValue);

	void reloadComponentValue();

	void unbind();

}
