package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.stream.Stream;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class ClassPathDao<T> extends AbstractSerializationDao<T> {

	static {
		ClassLoaderHelper.registerResourceHandler();
	}

	public static final String PREFIX = "rsrc:";

	private final String resourceName;

	public ClassPathDao(Class<T> daoType, String locator, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		this.resourceName = locator.substring(PREFIX.length());
	}

	@Override
	public Stream<String> list() {
		throw JavaExt.notImplemented();
	}

	@Override
	protected Resource<String> resolveAndReadRaw(ResourceMeta resourceMeta) throws IOException {
		final ResourceMeta metaWithStorage;
		if (resourceMeta.getStorage() != null) {
			metaWithStorage = resourceMeta;
		} else {
			final var resPath = resourceName + resourceMeta.getLocator();
			metaWithStorage = resourceOrDefaultOf(resourceMeta.getLocator(), resPath);
		}
		try (var resStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(metaWithStorage.getStorage())) {
			if (resStream == null) {
				throw new IOException("Resource not found: " + metaWithStorage);
			}
			return metaWithStorage.withValue(JavaExt.readUTF8Stream(resStream));
		}
	}

	@Override
	protected Resource<T> resolveOrCreate(ResourceMeta locator) throws IOException {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource<T> saveOrUpdate(String locator, T value) throws StorageException {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> value) throws StorageException {
		throw JavaExt.notImplemented();
	}

	@Override
	protected Resource<String> writeContent(Resource<String> resource) throws StorageException {
		throw JavaExt.notImplemented();
	}

}
