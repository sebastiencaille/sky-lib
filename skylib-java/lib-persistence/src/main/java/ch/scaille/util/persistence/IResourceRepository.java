package ch.scaille.util.persistence;

import java.util.stream.Stream;

public interface IResourceRepository<T> {

	Stream<String> list() throws StorageException;

	String readRaw(String locator) throws StorageException;

	/**
	 * 
	 * @param locator
	 * @param value
	 * @return information about the storage location value
	 * @throws StorageException
	 */
	Resource writeRaw(String locator, String value) throws StorageException;

	T read(String locator) throws StorageException;

	/**
	 * 
	 * @param locator
	 * @param value
	 * @return information about the storage location value
	 * @throws StorageException
	 */
	Resource write(String locator, T value) throws StorageException;

}
