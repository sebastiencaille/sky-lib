package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import ch.scaille.util.persistence.handlers.IStorageDataHandler;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import org.jspecify.annotations.Nullable;

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

	protected abstract Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException;

	protected abstract Resource<String> writeRaw(Resource<String> resource) throws StorageException;

	protected abstract String extensionOf(String storageLocator);

	protected abstract ResourceMetaData fixOrValidate(ResourceMetaData resourceMetaData);

	protected AbstractSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
		this.resourceType = daoType;
        this.dataHandlerRegistry = Objects.requireNonNullElseGet(serDeserializerRegistry, () -> new StorageDataHandlerRegistry(null));
	}

	@Override
	public ResourceMetaData resolve(String identifier, String mimetype) throws StorageException {
		return resolve(identifier).withMimeType(mimetype);
	}
	
	protected ResourceMetaData buildAndValidateMetadata(String identifier, String storageLocator) {
		return buildMetadata(identifier, storageLocator)
				.orElseThrow(() -> unableToIdentifyException(identifier, storageLocator, "not found"));
	}

	protected IllegalStateException unableToIdentifyException(String identifier, String storageLocator, String extra) {
		return new IllegalStateException(String.format( "Unable to identify meta-data of %s / %s (%s)", identifier, storageLocator, extra));
	}

	/**
	 * Creates the metadata, searching the mime-type if needed
	 * 
	 * @return the meta data
	 */
	protected Optional<ResourceMetaData> buildMetadata(String identifier, String storageLocator) {
		final var extension = extensionOf(storageLocator);
		return dataHandlerRegistry.find(extension)
				.map(IStorageDataHandler::getDefaultMimeType)
				.or(dataHandlerRegistry::getDefaultMimeType)
				.map(mimeType -> fixOrValidate(new ResourceMetaData(identifier, storageLocator, mimeType)));
	}

	@Override
	public Resource<T> loadResource(ResourceMetaData resourceMetaData, @Nullable T template) throws StorageException {
		return StorageException.wrap("loadResource",
				() -> dataHandlerRegistry.decode(readRaw(resourceMetaData), resourceType, template));
	}

	@Override
	public Resource<T> loadResource(String identifier, @Nullable T template) throws StorageException {
		return StorageException.wrap("loadResource", () -> loadResource(resolve(identifier), template));
	}

	@Override
	public Resource<T> saveOrUpdate(String identifier, T value) throws StorageException {
		return StorageException.wrap("saveOrUpdate", () -> {
			final var resource = resolveOrCreate(identifier).withValue(value);
			return writeRaw(dataHandlerRegistry.encode(resource, resourceType));
		}).withValue(value);
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> resource) throws StorageException {
		return StorageException.wrap("saveOrUpdate", () -> writeRaw(dataHandlerRegistry.encode(resource, resourceType)))
				.withValue(resource.getValue());
	}

}
