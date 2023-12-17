package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.stream.Stream;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

/**
 * Handles resources located in the classpath
 * @param <T>
 */
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
	public Stream<ResourceMetaData> list() {
		throw JavaExt.notImplemented();
	}

	@Override
	protected ResourceMetaData resolve(ResourceMetaData resourceMetaData) throws IOException {
		final ResourceMetaData metaWithStorage;
		if (resourceMetaData.getStorageLocator() != null) {
			metaWithStorage = resourceMetaData;
		} else {
			final var resPath = resourceName + resourceMetaData.getLocator();
			metaWithStorage = metadataOf(resourceMetaData.getLocator(), resPath);
		}
		return metaWithStorage;
	}
	
	@Override
	protected Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException {
		try (var resStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceMetaData.getStorageLocator())) {
			if (resStream == null) {
				throw new IOException("Resource not found: " + resourceMetaData);
			}
			return resourceMetaData.withValue(JavaExt.readUTF8Stream(resStream));
		}
	}

	@Override
	protected Resource<T> resolveOrCreate(ResourceMetaData locator) throws IOException {
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
	protected Resource<String> writeRaw(Resource<String> resource) throws StorageException {
		throw JavaExt.notImplemented();
	}

}
