package ch.scaille.util.dao.metadata;

import java.util.Set;

/**
 * This class contains the meta-data for an arbitrary class.<br>
 * It does not enforce data type on purpose.
 */
public class UntypedDataObjectMetaData extends AbstractObjectMetaData<Object> {

	public UntypedDataObjectMetaData(final Class<?> aclass) {
		super(aclass);
	}

	public UntypedDataObjectMetaData(final Class<?> aclass, final boolean accessPrivateFields) {
		super(aclass, accessPrivateFields);
	}

	public UntypedDataObjectMetaData(final Class<?> dataType, final Set<String> attribNames) {
		super(dataType, attribNames);
	}

	public UntypedDataObjectManager createUntypedObjectAccessorFor(final Object anObject) {
		return new UntypedDataObjectManager(this, anObject);
	}

}
