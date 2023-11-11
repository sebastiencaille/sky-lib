package ch.scaille.util.dao.metadata;

/**
 * This class allows to create DO's accessors
 *
 * @author Sebastien Caille
 *
 */
public class DataObjectManagerFactory {

	private DataObjectManagerFactory() {
	}

	/*
	 * Creates an Accessor that does not enforce the data types in its methods. The
	 * meta-data are extracted from the data's class.
	 */
	public static <T> UntypedDataObjectManager createFor(final T data) {
		return new UntypedDataObjectMetaData(data.getClass()).createUntypedAccessorTo(data);
	}

	/*
	 * Creates an Accessor that enforce the data types in its methods. The meta-data
	 * are extracted from the class clazz.
	 */
	public static <D> DataObjectManager<D> createFor(final Class<D> clazz, final D data) {
		return new DataObjectMetaData<>(clazz).createAccessorTo(data);
	}
}
