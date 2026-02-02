package ch.scaille.util.persistence;

import java.util.stream.Stream;

/**
 * To access Resources
 * <p>
 * The locator allows finding persisted resource.
 * 
 * @param <T>
 */
public interface IDao<T> {

	/**
	 * Lists available resources
	 */
	Stream<ResourceMetaData> list() throws StorageException;

	/**
	 * Loads a resource from metadata
	 */
	Resource<T> loadResource(ResourceMetaData metadata) throws StorageException;
	
	/**
	 * Loads a resource from a locator
	 */
	Resource<T> loadResource(String locator) throws StorageException;

	/**
	 * Loads data from a locator
	 */
	default T load(String locator) throws StorageException {
		return loadResource(locator).getValue();
	}

	/**
	 * Saves data in a location
	 */
	Resource<T> saveOrUpdate(String locator, T value) throws StorageException;

	/**
	 * Saves data contained in a resource
	 */
	Resource<T> saveOrUpdate(Resource<T> value) throws StorageException;

}
