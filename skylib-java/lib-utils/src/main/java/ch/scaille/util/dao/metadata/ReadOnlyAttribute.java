package ch.scaille.util.dao.metadata;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * This class allows accessing a Read Only attribute
 * 
 * @author Sebastien Caille
 *
 * @param <T>
 */
public class ReadOnlyAttribute<T, V> extends GetSetAttribute<T, V> {

	public ReadOnlyAttribute(final String name, final Method attributeGetterInfo, final MethodHandle getter) {
		super(name, attributeGetterInfo, getter, null);
	}

	@Override
	public void setValueOf(final T to, final Object value) {
		throw new IllegalStateException("Attribute " + name + " is read only");
	}

	@Override
	public String toString() {
		return name + '(' + type.getName() + ", ReadOnly)";
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
