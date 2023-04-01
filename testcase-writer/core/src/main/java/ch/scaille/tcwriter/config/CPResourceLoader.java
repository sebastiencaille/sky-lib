package ch.scaille.tcwriter.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CPResourceLoader implements IResourceLoader {

	public static final String PREFIX = "rsrc:";

	private final String resourceName;

	private String extension;

	public CPResourceLoader(String locator, String extension) {
		this.extension = extension;
		this.resourceName = locator.substring(PREFIX.length());
	}

	@Override
	public Stream<String> list() throws IOException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public String read(String locator) throws IOException {
		var resName = resourceName + locator;
		if (extension != null) {
			resName += locator;
		}
		try (var resStream = new BufferedReader(new InputStreamReader(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(resName), StandardCharsets.UTF_8))) {
			return resStream.lines().collect(Collectors.joining("\n"));
		}
	}

	@Override
	public String read(Path path) throws IOException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public String write(String locator, String value) throws IOException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public String write(Path target, String value) throws IOException {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public Path getBaseFolder() {
		throw new IllegalStateException("Not implemented");
	}

}
