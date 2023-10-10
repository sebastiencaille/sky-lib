package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Optional;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

public abstract class AbstractSerializationDao<T> implements IDao<T> {

	protected final Class<T> resourceType;

	protected final StorageDataHandlerRegistry dataHandlerRegistry;

	protected abstract ResourceMeta resolveOrCreate(ResourceMeta resourceMeta) throws IOException;

	protected abstract Resource<String> resolveAndReadRaw(ResourceMeta resourceMeta) throws IOException;

	protected abstract Resource<String> writeContent(Resource<String> resource) throws StorageException;

	protected AbstractSerializationDao(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
		this.resourceType = daoType;
		if (serDeserializerRegistry != null) {
			this.dataHandlerRegistry = serDeserializerRegistry;
		} else {
			this.dataHandlerRegistry = new StorageDataHandlerRegistry(null);
		}

	}

	protected String[] nameAndExtension(String locator) {
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
	 * Get hard pre-defined resources
	 * @param locator
	 * @param storage
	 * @return
	 */
	private Optional<ResourceMeta> preDefinedResourceOf(String locator, String storage) {
		if (String.class.equals(resourceType)) {
			return Optional.of(new ResourceMeta(locator, storage, TextStorageHandler.TEXT_MIMETYPE));
		}
		return Optional.empty();
	}

	public Optional<ResourceMeta> resourceOf(String locator, String storage) {
		final var nameAndExt = nameAndExtension(locator);
		return completeResource(locator, storage, nameAndExt);
	}

	private Optional<ResourceMeta> completeResource(String locator, String storage, final String[] nameAndExt) {
		return preDefinedResourceOf(locator, storage).or(() -> dataHandlerRegistry.find(nameAndExt[1])
				.map(h -> new Resource<>(nameAndExt[0], storage, h.getDefaultMimeType(), null)));
	}

	public ResourceMeta resourceOrDefaultOf(String locator, String storage) {
		final var nameAndExt = nameAndExtension(locator);
		return completeResource(locator, storage, nameAndExt)
				.orElseGet(() -> new Resource<>(nameAndExt[0], storage, dataHandlerRegistry.getDefaultMimeType(), null));
	}

	@Override
	public Resource<T> loadResource(ResourceMeta resource) throws StorageException {
		return StorageException.wrap("read", () -> dataHandlerRegistry.decode(resolveAndReadRaw(resource), resourceType));
	}

	@Override
	public Resource<T> saveOrUpdate(String locator, T value) throws StorageException {
		return StorageException.wrap("write", () -> {
			final var resource = resolveOrCreate(new ResourceMeta(locator, null, null)).withValue(value);
			return saveOrUpdateRaw(dataHandlerRegistry.encode(resource, resourceType));
		}).withValue(value);
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> resource) throws StorageException {
		return StorageException.wrap("write", () -> {
			return saveOrUpdateRaw(dataHandlerRegistry.encode(resource, resourceType));
		}).withValue(resource.getValue());
	}

	public Resource<String> saveOrUpdateRaw(Resource<String> resource) throws StorageException {
		return StorageException.wrap("writeRaw", () -> writeContent(resource));
	}
}
