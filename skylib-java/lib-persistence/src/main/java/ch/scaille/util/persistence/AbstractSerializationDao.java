package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import ch.scaille.util.persistence.handlers.IStorageDataHandler;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

/**
 * Abstraction of dao with serialization (json, yaml, ...)
 * 
 * @param <T>
 */
public abstract class AbstractSerializationDao<T> implements IDao<T> {
	
	/**
	 * Type of persisted resource
	 */
	protected final Class<T> resourceType;

	/**
	 * Registry that provides serialization/de-serialization
	 */
	protected final StorageDataHandlerRegistry dataHandlerRegistry;

	protected abstract ResourceMetaData resolve(String locator) throws IOException;

	protected abstract ResourceMetaData resolveOrCreate(String locator) throws IOException;

	protected abstract Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException;

	protected abstract Resource<String> writeRaw(Resource<String> resource) throws StorageException;

	protected abstract String extensionOf(String storageLocator);

	protected abstract ResourceMetaData fixOrValidate(ResourceMetaData resourceMetaData);

	protected AbstractSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
		this.resourceType = daoType;
        this.dataHandlerRegistry = Objects.requireNonNullElseGet(serDeserializerRegistry, () -> new StorageDataHandlerRegistry(null));
	}

	protected ResourceMetaData buildAndValidateMetadata(String locator, String storageLocator) {
		return buildMetadata(locator, storageLocator)
				.orElseThrow(() -> unableToIdentifyException(locator, storageLocator, "not found"));
	}

	protected IllegalStateException unableToIdentifyException(String locator, String storageLocator, String extra) {
		return new IllegalStateException(String.format( "Unable to identify meta-data of %s / %s (%s)" ,locator , storageLocator , extra));
	}

	/**
	 * Creates the metadata, searching the mime-type if needed
	 * 
	 * @return the meta data
	 */
	protected Optional<ResourceMetaData> buildMetadata(String locator, String storageLocator) {
		final var extension = extensionOf(storageLocator);
		return dataHandlerRegistry.find(extension)
				.map(IStorageDataHandler::getDefaultMimeType)
				.or(this::getPredefinedResourceMimeType)
				.or(dataHandlerRegistry::getDefaultMimeType)
				.map(mimeType -> fixOrValidate(new ResourceMetaData(locator, storageLocator, mimeType)));
	}
	
	/**
	 * Creates a metadata using pre-defined mime-types
	 */
	private Optional<String> getPredefinedResourceMimeType() {
		if (String.class.equals(resourceType)) {
			return Optional.of(TextStorageHandler.TEXT_MIMETYPE);
		}
		return Optional.empty();
	}

	@Override
	public Resource<T> loadResource(ResourceMetaData resourceMetaData) throws StorageException {
		return StorageException.wrap("loadResource",
				() -> dataHandlerRegistry.decode(readRaw(resourceMetaData), resourceType));
	}

	@Override
	public Resource<T> loadResource(String locator) throws StorageException {
		return StorageException.wrap("loadResource", () -> loadResource(resolve(locator)));
	}

	@Override
	public Resource<T> saveOrUpdate(String locator, T value) throws StorageException {
		return StorageException.wrap("saveOrUpdate", () -> {
			final var resource = resolveOrCreate(locator).withValue(value);
			return writeRaw(dataHandlerRegistry.encode(resource, resourceType));
		}).withValue(value);
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> resource) throws StorageException {
		return StorageException.wrap("saveOrUpdate", () -> writeRaw(dataHandlerRegistry.encode(resource, resourceType)))
				.withValue(resource.getValue());
	}

}
