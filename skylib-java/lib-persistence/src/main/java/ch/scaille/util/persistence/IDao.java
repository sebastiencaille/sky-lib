package ch.scaille.util.persistence;

import java.util.stream.Stream;

public interface IDao<T> {

	Stream<String> list() throws StorageException;

	Resource<T> loadResource(ResourceMeta resourceMeta) throws StorageException;
	
	default Resource<T> loadResource(String locator) throws StorageException {
		return loadResource(new ResourceMeta(locator, null, null));
	}
	
	default  T load(String locator) throws StorageException {
		return loadResource(locator).getValue();
	}

	/**
	 * 
	 * @param locator
	 * @param value
	 * @return information about the storage location value
	 * @throws StorageException
	 */
	Resource<T> saveOrUpdate(String locator, T value) throws StorageException;
	
	Resource<T> saveOrUpdate(Resource<T> value) throws StorageException;

}
