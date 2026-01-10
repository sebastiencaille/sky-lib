package ch.scaille.util.dao.metadata;

import java.util.Set;

import ch.scaille.util.dao.metadata.AttributeFactory.Mode;
import org.jspecify.annotations.NullMarked;

/**
 * This Class contains the meta-data of Class<DataType>.
 *
 * @param <D>
 */
@NullMarked
public class RecordMetaData<D> extends AbstractObjectMetaData<D> {

	public RecordMetaData(final Class<D> clazz) {
		super(clazz);
		attributeMode = Mode.RECORD;
	}

	@Override
	protected void scanMethods(Class<?> clazz, Set<String> attribNames) {
		for (final var field : clazz.getDeclaredFields()) {
			attribNames.add(field.getName());
		}
	}
}
