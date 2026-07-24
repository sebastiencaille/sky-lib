package ch.scaille.util.dao.metadata;

import org.jspecify.annotations.Nullable;

public interface DataObjectAttribute {

	@Nullable
	Object getValue();

	<T> T getValue(Class<T> clazz);

	void setValue(@Nullable Object value);

}
