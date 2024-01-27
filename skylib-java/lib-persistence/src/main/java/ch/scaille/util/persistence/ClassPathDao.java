package ch.scaille.util.persistence;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

/**
 * Handles resources located in the classpath
 * 
 * @param <T>
 */
public class ClassPathDao<T> extends AbstractSerializationDao<T> {

	static {
		ClassLoaderHelper.registerResourceHandler();
	}

	private final String resourcePath;

	private final Set<String> whiteList;

	private final boolean resourcePathWhiteListed;

	public ClassPathDao(Class<T> daoType, String resourcePath, StorageDataHandlerRegistry serDeserializerRegistry, Set<String> whiteList) {
		super(daoType, serDeserializerRegistry);
		this.whiteList = whiteList;
		this.resourcePath = resourcePath;
		this.resourcePathWhiteListed = whiteList.stream().anyMatch(resourcePath::startsWith);
	}

	@Override
	public Stream<ResourceMetaData> list() {
		throw JavaExt.notImplemented();
	}

	@Override
	protected ResourceMetaData resolve(String locator) throws IOException {
		if (!resourcePathWhiteListed && whiteList.stream().noneMatch(locator::startsWith)) {
			throw unableToIdentifyException(locator, resourcePath + locator, "not in white-list");
		}
		return buildAndValidateMetadata(locator, resourcePath + locator);
	}

	@Override
	protected Resource<String> readRaw(ResourceMetaData resourceMetaData) throws IOException {
		try (var resStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(resourceMetaData.getStorageLocator())) {
			if (resStream == null) {
				throw new IOException("Resource not found: " + resourceMetaData);
			}
			return resourceMetaData.withValue(JavaExt.readUTF8Stream(resStream));
		}
	}

	@Override
	protected Resource<T> resolveOrCreate(String locator) throws IOException {
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
