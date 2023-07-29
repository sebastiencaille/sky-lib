package ch.scaille.tcwriter.persistence.fsconfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.tcwriter.persistence.IResourceRepository;
import ch.scaille.tcwriter.persistence.Resource;
import ch.scaille.util.helpers.Logs;

public class FsResourceLoader implements IResourceRepository {

	private static final Logger LOGGER = Logs.of(FsResourceLoader.class);

	private final Path basePath;

	private final String extension;

	public FsResourceLoader(String basePath, String subPath, String extension) {
		this.extension = extension;
		var path = FsConfigDao.resolvePlaceHolders(subPath);
		if (!path.startsWith("/")) {
			path = basePath + '/' + path;
		}
		this.basePath = Paths.get(path);
	}

	public FsResourceLoader(Path baseFolder, String extension) {
		this.extension = extension;
		this.basePath = baseFolder;
	}

	private Path resolve(String locator) {
		var fullName = locator;
		if (extension != null && !Resource.hasExtension(locator)) {
			fullName += '.' + extension;
		}
		return basePath.resolve(fullName);
	}
		
	@Override
	public Stream<String> list() throws IOException {
		return Files.list(basePath).map(p -> {
			var resource = p.getFileName().toString();
			if (extension != null) {
				resource = resource.substring(0, resource.length() - extension.length() - 1);
			}
			return resource;
		});
	}

	@Override
	public Resource read(String resource) throws IOException {
		try {
			return this.read(resolve(resource));	
		} catch (IOException e) {
			// failed, try json file
			try {
				return this.read(resolve(resource+".json"));	
			} catch (IOException e2) {
				// throw original exception
				throw e;
			}
		}
	}

	public Resource read(Path resource) throws IOException {
		LOGGER.info(() -> "Reading " + resource);
		return Resource.of(resource.toString(), Files.readString(resource, StandardCharsets.UTF_8));
	}

	@Override
	public String write(String resource, String value) throws IOException {
		final var resolved = resolve(resource);
		return write(resolved, value);
	}

	public String write(Path resource, String value) throws IOException {
		LOGGER.info(() -> "Writing " + resource);
		Files.createDirectories(resource.getParent());
		Files.writeString(resource, value, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE);
		return resource.toString();
	}

	public Path getBaseFolder() {
		return basePath;
	}

	public FsResourceLoader inSubFolder(String subfolder, String extension) {
		return new FsResourceLoader(basePath.resolve(subfolder),  extension);
	}

}
