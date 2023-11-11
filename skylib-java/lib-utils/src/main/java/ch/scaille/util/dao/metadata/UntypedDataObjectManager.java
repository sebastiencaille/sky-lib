package ch.scaille.util.dao.metadata;

/**
 * This class allows accessing a data object without enforcing the data types in
 * the methods parameters
 *
 * @param <D>
 */
public class UntypedDataObjectManager extends DataObjectManager<Object> {

	public UntypedDataObjectManager(final AbstractObjectMetaData<Object> objectMetaData, final Object object) {
		super(objectMetaData, object);
	}

	@Override
	public void copyInto(final Object object) {

		if (!metaData.getDataType().isAssignableFrom(object.getClass())) {
			throw new IllegalStateException("Parameter of type " + object.getClass().getName() + " is not a subtype of "
					+ metaData.getDataType().getName());
		}

		super.copyInto(metaData.getDataType().cast(object));
	}

}
