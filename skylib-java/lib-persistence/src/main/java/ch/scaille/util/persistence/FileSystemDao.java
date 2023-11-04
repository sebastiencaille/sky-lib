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
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class FileSystemDao<T> extends AbstractSerializationDao<T> {

	private static final Logger LOGGER = Logs.of(FileSystemDao.class);

	private final Path basePath;

	public FileSystemDao(Class<T> daoType, String basePath, String subPath,
			StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		var path = resolvePlaceHolders(subPath);
		if (!path.startsWith("/")) {
			path = basePath + '/' + path;
		}
		this.basePath = Paths.get(path);
	}

	public FileSystemDao(Class<T> daoType, Path baseFolder, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		this.basePath = baseFolder;
	}

	/**
	 * 
	 * @param locator empty to use basePath, null to list
	 * @return
	 * @throws IOException
	 */
	private Stream<ResourceMeta> inFolder(String locator) throws IOException {
		if (locator != null && locator.isEmpty()) {
			// config targets one file
			return Collections.singleton(resourceOrDefaultOf(basePath.getFileName().toString(), basePath.toString()))
					.stream();
		}
		var folder = basePath;
		var file = locator;
		if (locator != null && new File(locator).isAbsolute()) {
			var path = Path.of(locator);
			folder = path.getParent();
			file = path.getFileName().toString();
		}
		final var filter = file;
		if (!Files.exists(folder)) {
			return Collections.<ResourceMeta>emptyList().stream();
		}
		return Files.list(folder)
				.filter(f -> filter == null || f.getFileName().toString().startsWith(filter))
				.map(p -> resourceOf(p.getFileName().toString(), p.toString()))
				.filter(r -> r.isPresent() && (filter == null || r.get().getLocator().equals(filter)))
				.map(Optional::get);
	}

	private ResourceMeta resolve(ResourceMeta resourceMeta) throws IOException {
		return fixExtension(inFolder(resourceMeta.getLocator()).findFirst()
				.orElseThrow(() -> new StorageException("Resource not found: " + resourceMeta)));
	}

	@Override
	protected ResourceMeta resolveOrCreate(ResourceMeta resourceMeta) throws IOException {
		final var locator = resourceMeta.getLocator();
		return fixExtension(inFolder(locator).findFirst()
				.orElse(resourceOrDefaultOf(locator, basePath.resolve(locator).toString())));
	}

	private ResourceMeta fixExtension(ResourceMeta resourceMeta) {
		// Correct the extension if not consistent
		final var nameAndExt = nameAndExtension(resourceMeta.getStorage());
		final var handler = dataHandlerRegistry.find(resourceMeta.getMimeType());
		if (handler.isPresent() && !handler.get().supports(nameAndExt[1]) && !handler.get().getDefaultExtension().isEmpty()) {
			return resourceMeta.withStorage(resourceMeta.getStorage() + '.' + handler.get().getDefaultExtension());
		}
		return resourceMeta;
	}

	@Override
	public Stream<String> list() throws StorageException {
		return StorageException.wrap("list", () -> inFolder(null).map(r -> r.getLocator()));
	}

	@Override
	protected Resource<String> resolveAndReadRaw(ResourceMeta resourceMeta) throws IOException {
		final var resource = resolve(resourceMeta);
		LOGGER.info(() -> "Reading " + resource);
		return resource.withValue(Files.readString(Paths.get(resource.getStorage()), StandardCharsets.UTF_8));
	}

	@Override
	protected Resource<String> writeContent(Resource<String> resource) throws StorageException {
		LOGGER.info(() -> "Writing " + resource);
		try {
			final var path = basePath.resolve(resource.getStorage());
			Files.createDirectories(path.getParent());
			Files.writeString(path, resource.getValue(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING,
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
