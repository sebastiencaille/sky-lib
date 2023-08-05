package ch.scaille.util.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.util.helpers.Logs;
import ch.scaille.util.persistence.handlers.IStorageDataHandler;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class FsResourceRepository<T> extends AbstractResourceRepository<T> {

	private static final Logger LOGGER = Logs.of(FsResourceRepository.class);

	private final Path basePath;

	public FsResourceRepository(Class<T> daoType, String basePath, String subPath,
			StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		var path = resolvePlaceHolders(subPath);
		if (!path.startsWith("/")) {
			path = basePath + '/' + path;
		}
		this.basePath = Paths.get(path);
	}

	public FsResourceRepository(Class<T> daoType, Path baseFolder, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		this.basePath = baseFolder;
	}

	/**
	 * 
	 * @param locator empty to use basePath, null to list
	 * @return
	 * @throws IOException
	 */
	private Stream<Resource> inFolder(String locator) throws IOException {
		if (locator != null && locator.isEmpty()) {
			// config targets one file
			return Collections.singleton(resourceOrDefaultOf(basePath.getFileName().toString(), basePath.toString()))
					.stream();
		}
		Path folder = basePath;
		String file = locator;
		if (locator != null && new File(locator).isAbsolute()) {
			Path path = Path.of(locator);
			folder = path.getParent();
			file = path.getFileName().toString();
		}
		final String filter = file;
		if (!Files.exists(folder)) {
			return Collections.<Resource>emptyList().stream();
		}
		return Files.list(folder)
				.filter(f -> filter == null || f.getFileName().toString().startsWith(filter))
				.map(p -> resourceOf(p.getFileName().toString(), p.toString()))
				.filter(r -> r.isPresent() && (filter == null || r.get().getName().equals(filter)))
				.map(Optional::get);
	}

	private Resource resolve(String locator) throws IOException {
		return fixExtension(inFolder(locator).findFirst().orElseThrow(() -> new StorageException("Resource not found: " + locator)));
	}

	@Override
	protected Resource resolveOrCreate(String locator) throws IOException {
		return fixExtension(inFolder(locator).findFirst()
				.orElse(resourceOrDefaultOf(locator, basePath.resolve(locator).toString())));
	}

	private Resource fixExtension(Resource resource) {
		// Correct the extension if not consistent
		final String[] nameAndExt = nameAndExtension(resource.getStorage());
		final Optional<IStorageDataHandler> handler = dataHandlerRegistry.find(resource.getMimeType());
		if (handler.isPresent() && !handler.get().supports(nameAndExt[1])) {
			return resource.fixStorage(resource.getStorage() + '.' + handler.get().getDefaultExtension());
		}
		return resource;
	}

	@Override
	public Stream<String> list() throws StorageException {
		return StorageException.wrap("list", () -> inFolder(null).map(r -> r.getName()));
	}

	@Override
	protected Resource resolveAndRead(String locator) throws IOException {
		final Resource resource = resolve(locator);
		LOGGER.info(() -> "Reading " + resource);
		return new Resource(resource, Files.readString(Paths.get(resource.getStorage()), StandardCharsets.UTF_8));
	}

	@Override
	protected Resource writeContent(Resource resource, String value) throws StorageException {
		LOGGER.info(() -> "Writing " + resource);
		try {
			final Path path = basePath.resolve(resource.getStorage());
			Files.createDirectories(path.getParent());
			Files.writeString(path, value, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.CREATE);
			return resource;
		} catch (IOException e) {
			throw new StorageException("Write failed", e);
		}
	}

	public Path getBaseFolder() {
		return basePath;
	}

	public static String resolvePlaceHolders(String path) {
		return path.replace("${user.home}", System.getProperty("user.home"))
				.replace("~", System.getProperty("user.home"));
	}

}
