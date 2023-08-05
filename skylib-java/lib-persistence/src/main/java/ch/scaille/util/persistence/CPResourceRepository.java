package ch.scaille.util.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class CPResourceRepository<T> extends AbstractResourceRepository<T> {

	static {
		ClassLoaderHelper.registerResourceHandler();
	}

	public static final String PREFIX = "rsrc:";

	private final String resourceName;

	public CPResourceRepository(Class<T> daoType, String locator, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		this.resourceName = locator.substring(PREFIX.length());
	}

	@Override
	public Stream<String> list() {
		throw JavaExt.notImplemented();
	}

	@Override
	protected Resource resolveAndRead(String locator) throws IOException {
		final String resPath = resourceName + locator;
		final Resource resource = resourceOrDefaultOf(locator, resPath);
		try (InputStream resStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resPath)) {
			return new Resource(resource, JavaExt.readUTF8Stream(resStream));
		}
	}

	@Override
	protected Resource resolveOrCreate(String locator) throws IOException {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource write(String locator, T value) throws StorageException {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource writeRaw(String locator, String value) throws StorageException {
		throw JavaExt.notImplemented();
	}

	@Override
	protected Resource writeContent(Resource resource, String value) throws StorageException {
		throw JavaExt.notImplemented();
	}

}
