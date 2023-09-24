package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Optional;

import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public abstract class AbstractResourceRepository<T> implements IResourceRepository<T> {

	private static final String RAW_MIMETYPE = "application/raw";

	protected final Class<T> resourceType;

	protected final StorageDataHandlerRegistry dataHandlerRegistry;

	protected abstract Resource resolveAndRead(String locator) throws IOException;

	protected abstract Resource resolveOrCreate(String locator) throws IOException;

	protected abstract Resource writeContent(Resource resource, String value) throws StorageException;

	protected AbstractResourceRepository(Class<T> daoType, StorageDataHandlerRegistry serDeserializerRegistry) {
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

	private Optional<Resource> defaults(String locator, String storage) {
		if (String.class.equals(resourceType)) {
			return Optional.of(new Resource(locator, storage, RAW_MIMETYPE, null));
		}
		return Optional.empty();
	}

	public Optional<Resource> resourceOf(String locator, String storage) {
		final var nameAndExt = nameAndExtension(locator);
		return completeResource(locator, storage, nameAndExt);
	}

	private Optional<Resource> completeResource(String locator, String storage, final String[] nameAndExt) {
		return defaults(locator, storage).or(() -> dataHandlerRegistry.find(nameAndExt[1])
				.map(h -> new Resource(nameAndExt[0], storage, h.getDefaultMimeType(), null)));
	}

	public Resource resourceOrDefaultOf(String locator, String storage) {
		final var nameAndExt = nameAndExtension(locator);
		return completeResource(locator, storage, nameAndExt)
				.orElseGet(() -> new Resource(nameAndExt[0], storage, dataHandlerRegistry.getDefaultMimeType(), null));
	}

	@Override
	public T read(String locator) throws StorageException {
		return StorageException.wrap("read", () -> dataHandlerRegistry.decode(resolveAndRead(locator), resourceType));
	}

	@Override
	public String readRaw(String locator) throws StorageException {
		Logs.of(getClass()).info(() -> "Reading " + locator);
		return StorageException.wrap("readRaw", () -> resolveAndRead(locator).getData());
	}

	@Override
	public Resource write(String locator, T value) throws StorageException {
		return StorageException.wrap("write", () -> {
			final var resource = resolveOrCreate(locator);
			return writeContent(resource, dataHandlerRegistry.encode(resource, resourceType, value));
		});
	}

	@Override
	public Resource writeRaw(String locator, String value) throws StorageException {
		return StorageException.wrap("writeRaw", () -> writeContent(resolveOrCreate(locator), value));
	}
}
