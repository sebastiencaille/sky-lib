package ch.scaille.util.dao.metadata;

public interface DataObjectAttribute {

	Object getValue();

	<T> T getValue(Class<T> clazz);

	void setValue(Object value);

}
