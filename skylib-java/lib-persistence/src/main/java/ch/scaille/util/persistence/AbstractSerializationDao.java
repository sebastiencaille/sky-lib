package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Optional;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

/**
 * Abstraction of dao with serialization (json, yaml, ...)
 * 
 * @param <T>
 */
public abstract class AbstractSerializationDao<T> implements IDao<T> {

	private static final String[] EMPTY_NAME_EXT = new String[] { "", "" };

	/**
	 * Type of persisted resource
	 */
	protected final Class<T> resourceType;

	/**
	 * Registry that provides serialization/de-serialization
	 */
	protected final StorageDataHandlerRegistry dataHandlerRegistry;

	protected abstract ResourceMetaData resolve(ResourceMetaData resourceMetaData) throws IOException;

	protected abstract ResourceMetaData resolveOrCreate(ResourceMetaData resourceMetaData) throws IOException;

	protected abstract Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException;

	protected abstract Resource<String> writeRaw(Resource<String> resource) throws StorageException;

	protected AbstractSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
		this.resourceType = daoType;
		if (serDeserializerRegistry != null) {
			this.dataHandlerRegistry = serDeserializerRegistry;
		} else {
			this.dataHandlerRegistry = new StorageDataHandlerRegistry(null);
		}

	}

	protected String[] nameAndExtensionOf(String locator) {
		if (locator == null) {
			return EMPTY_NAME_EXT;
		}
		final var lastDot = locator.lastIndexOf('.');
		final String[] nameAndExt;
		if (lastDot >= 0) {
			nameAndExt = new String[] { locator.substring(0, lastDot), locator.substring(lastDot + 1) };
		} else {
			nameAndExt = new String[] { locator, "" };
		}
		return nameAndExt;
	}

	/**
	 * Creates a meta data according to the locator and storage locator
	 * 
	 * @return the meta data
	 */
	protected Optional<ResourceMetaData> optionalMetadataOf(String locator, String storageLocator) {
		return buildMetadata(locator, storageLocator, nameAndExtensionOf(storageLocator));
	}

	protected ResourceMetaData metadataOf(String locator, String storageLocator) {
		return buildMetadata(locator, storageLocator, nameAndExtensionOf(storageLocator)).orElseThrow(
				() -> new IllegalStateException("Unable to identify meta-data of " + locator + " / " + storageLocator));
	}

	/**
	 * Creates the metadata, searching the mime-type if needed
	 * 
	 * @return the meta data
	 */
	private Optional<ResourceMetaData> buildMetadata(String locator, String storage, final String[] nameAndExt) {
		return dataHandlerRegistry.find(nameAndExt[1])
				.map(h -> h.getDefaultMimeType())
				.or(this::getPredefinedResourceMimeType)
				.or(dataHandlerRegistry::getDefaultMimeType)
				.map(m -> new ResourceMetaData(locator, storage, m));
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
		return StorageException.wrap("read",
				() -> dataHandlerRegistry.decode(readRaw(resolve(resourceMetaData)), resourceType));
	}

	@Override
	public Resource<T> saveOrUpdate(String locator, T value) throws StorageException {
		return StorageException.wrap("write", () -> {
			final var resource = resolveOrCreate(new ResourceMetaData(locator, null, null)).withValue(value);
			return writeRaw(dataHandlerRegistry.encode(resource, resourceType));
		}).withValue(value);
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> resource) throws StorageException {
		return StorageException.wrap("write", () -> writeRaw(dataHandlerRegistry.encode(resource, resourceType)))
				.withValue(resource.getValue());
	}

}
