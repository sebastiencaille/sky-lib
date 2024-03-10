package ch.scaille.util.persistence;

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

/**
 * Handles resources located on a file system
 * 
 * @param <T>
 */
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
	 * Finds the locator in basePath
	 * 
	 * @param locator empty to use basePath, null to list
	 * @return a stream of metadata
	 * @throws IOException
	 */
	private Stream<ResourceMetaData> inFolder(String locator) throws IOException {
		if (!Files.isDirectory(basePath)) {
			// basePath points to a file
			return Collections.singleton(buildAndValidateMetadata(basePath.getFileName().toString(), basePath.toString())).stream();
		}
		var target = basePath.resolve(locator);
		if (!target.startsWith(basePath)) {
			throw new IllegalStateException("Locator must be within base path");
		}
		final var folder = target.getParent();
		final var filter = target.getFileName().toString();
		if (!Files.exists(folder)) {
			return Collections.<ResourceMetaData>emptyList().stream();
		}
		return Files.list(folder)
				// basic filter
				.filter(f -> filter == null || f.getFileName().toString().startsWith(filter))
				.map(p -> buildMetadata(nameAndExtensionOf(p.getFileName().toString())[0], p.toString()))
				// filter on the metadata
				.filter(m -> filterMetaData(filter, m))
				.map(Optional::get);
	}

	private boolean filterMetaData(final String filter, Optional<ResourceMetaData> m) {
		return m.isPresent() && (filter == null || m.get().getLocator().equals(filter));
	}

	@Override
	public ResourceMetaData resolve(String locator) throws IOException {
		return inFolder(locator).findFirst()
				.orElseThrow(() -> new StorageException("Resource not found: " + locator));
	}

	@Override
	protected ResourceMetaData resolveOrCreate(String locator) throws IOException {
		return inFolder(locator).findFirst().orElse(buildAndValidateMetadata(locator, basePath.resolve(locator).toString()));
	}

	
	@Override
	public Stream<ResourceMetaData> list() throws StorageException {
		return StorageException.wrap("list", () -> inFolder(null));
	}

	@Override
	protected Resource<String> readRaw(ResourceMetaData metadata) throws IOException {
		LOGGER.info(() -> "Reading " + metadata);
		return metadata.withValue(Files.readString(Paths.get(metadata.getStorageLocator()), StandardCharsets.UTF_8));
	}

	@Override
	protected Resource<String> writeRaw(Resource<String> resource) throws StorageException {
		LOGGER.info(() -> "Writing " + resource);
		try {
			final var path = basePath.resolve(resource.getStorageLocator());
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
