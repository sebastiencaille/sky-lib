package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import ch.scaille.util.helpers.ClassLoaderHelper;
import ch.scaille.util.helpers.JavaExt;

public class CPResourceLoader implements IResourceRepository {

	static {
		ClassLoaderHelper.registerResourceHandler();
	}
	
	public static final String PREFIX = "rsrc:";

	private final String resourceName;

	private String extension;

	public CPResourceLoader(String locator, String extension) {
		this.extension = extension;
		this.resourceName = locator.substring(PREFIX.length());
	}

	@Override
	public Stream<String> list() throws IOException {
		throw JavaExt.notImplemented();
	}

	@Override
	public Resource read(String locator) throws IOException {
		var resName = resourceName + locator;
		if (extension != null) {
			resName += locator;
		}
		try (var resStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName)) {
			return Resource.of(locator, JavaExt.readUTF8Stream(resStream));
		}
	}

	public String read(Path path) {
		throw JavaExt.notImplemented();
	}

	@Override
	public String write(String locator, String value) throws IOException {
		throw JavaExt.notImplemented();
	}

	public Path getBaseFolder() {
		throw JavaExt.notImplemented();
	}

}
