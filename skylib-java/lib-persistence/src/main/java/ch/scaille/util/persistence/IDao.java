package ch.scaille.util.persistence;

import org.jspecify.annotations.Nullable;

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

	ResourceMetaData resolve(String identifier) throws StorageException;
	
	ResourceMetaData resolve(String identifier, String mimetype) throws StorageException;
	
	ResourceMetaData resolveOrCreate(String identifier) throws StorageException;
	
	/**
	 * Loads a resource from metadata
	 */
	default Resource<T> loadResource(ResourceMetaData metadata) throws StorageException {
		return loadResource(metadata, null);
	}

	/**
	 * Loads a resource from metadata
	 */
	Resource<T> loadResource(ResourceMetaData metadata, @Nullable T template) throws StorageException;

	/**
	 * Loads a resource from a locator
	 */
	default Resource<T> loadResource(String identifier) throws StorageException {
		return loadResource(identifier, null);
	}

	/**
	 * Loads a resource from a locator
	 */
	Resource<T> loadResource(String identifier, @Nullable T template) throws StorageException;

	/**
	 * Loads data from a locator
	 */
	default T load(String identifier) throws StorageException {
		return loadResource(identifier, null).getValue();
	}

	/**
	 * Loads data from a locator
	 */
	default T load(String identifier, T template) throws StorageException {
		return loadResource(identifier, template).getValue();
	}

	/**
	 * Saves data in a location
	 */
	Resource<T> saveOrUpdate(String identifier, T value) throws StorageException;

	/**
	 * Saves data contained in a resource
	 */
	Resource<T> saveOrUpdate(Resource<T> value) throws StorageException;

}
