package ch.scaille.util.dao.metadata;

/**
 * This Class contains the meta-data of Class<DataType>.
 *
 * @param <D>
 */
public class DataObjectMetaData<D> extends AbstractObjectMetaData<D> {

	public DataObjectMetaData(final Class<D> clazz) {
		super(clazz);
	}

}
