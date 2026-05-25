package ch.scaille.util.persistence;

import java.io.IOException;
import java.nio.file.Path;
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
public class ClassPathDao<T> extends AbstractFSSerializationDao<T> {

	static {
		ClassLoaderHelper.registerResourceHandler();
	}

	private final Path resourcePath;

	private final Set<String> whiteList;

	private final boolean resourcePathWhiteListed;

	public ClassPathDao(Class<T> daoType, Path resourcePath, StorageDataHandlerRegistry serDeserializerRegistry, Set<String> whiteList) {
		super(daoType, serDeserializerRegistry, true);
		this.whiteList = whiteList;
		this.resourcePath = resourcePath;
		this.resourcePathWhiteListed = whiteList.stream().anyMatch(resourcePath::startsWith);
	}

	@Override
	public Stream<ResourceMetaData> list() {
		throw JavaExt.notImplemented();
	}

	@Override
	public ResourceMetaData resolve(String identifier) {
		final var fullPath =  resourcePath.resolve(validateIdentifier(identifier)).toString();
		if (!resourcePathWhiteListed && whiteList.stream().noneMatch(identifier::startsWith)) {
			throw unableToIdentifyException(identifier, fullPath, "not in white-list");
		}
		return buildAndValidateMetadata(identifier, fullPath);
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
	public Resource<T> resolveOrCreate(String identifier) {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource<T> saveOrUpdate(String identifier, T value) {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource<T> saveOrUpdate(Resource<T> value) {
		throw JavaExt.notImplemented();
	}

	@Override
	protected Resource<String> writeRaw(Resource<String> resource) {
		throw JavaExt.notImplemented();
	}

}
