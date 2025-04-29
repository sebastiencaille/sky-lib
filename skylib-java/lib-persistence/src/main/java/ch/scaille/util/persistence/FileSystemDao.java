package ch.scaille.util.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ch.scaille.util.helpers.Logs;
import ch.scaille.util.helpers.StreamExt;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

/**
 * Handles resources located on a file system
 * 
 * @param <T>
 */
public class FileSystemDao<T> extends AbstractFSSerializationDao<T> {

	private static final Logger LOGGER = Logs.of(FileSystemDao.class);

	private final Path basePath;

	public FileSystemDao(Class<T> daoType, String basePath, String subPath,
			StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		var path = resolvePlaceHolders(subPath);
		if (!path.startsWith("/")) {
			path = basePath + '/' + path;
		}
		this.basePath = Paths.get(path).toAbsolutePath();
	}

	public FileSystemDao(Class<T> daoType, Path baseFolder, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(daoType, serDeserializerRegistry);
		this.basePath = baseFolder;
	}

	/**
	 * Finds the locator in basePath
	 * 
	 * @param locator a value to locate the files, empty String to use basePath, null to return all the content
	 * @return a stream of metadata
	 */
	private Stream<ResourceMetaData> findInFolder(String locator) throws IOException {
		if (locator != null && locator.isEmpty() && !Files.isDirectory(basePath)) {
			// basePath points to a file
			return Stream.of(buildAndValidateMetadata(basePath.getFileName().toString(), basePath.toString()));
		}
		final Path folder;
		final String filter;
		if (locator != null) {
			// The locator may contain a folder
			var target = basePath.resolve(locator);
			if (!target.startsWith(basePath)) {
				throw new IllegalStateException("Locator must be within base path");
			}
			folder = target.getParent();
			filter = target.getFileName().toString();
		} else {
			folder = basePath;
			filter = null;
		}
		if (!Files.exists(folder)) {
			return Stream.empty();
		}
		// basic filter
		return StreamExt.onCloseableF(Files.list(folder), folders->
			folders.filter(f -> (filter == null || f.getFileName().toString().startsWith(filter))
				&& dataHandlerRegistry.find(extensionOf(f.toString())).isPresent())
				.map(p -> buildMetadata(nameAndExtensionOf(folder.relativize(p).toString())[0], p.toString()))
				// filter on the metadata
				.filter(m -> filterMetaData(filter, m.orElse(null)))
				.map(Optional::get).toList())
			.stream();
	}

	private boolean filterMetaData(final String filter, ResourceMetaData m) {
		return m != null && (filter == null || m.getLocator().equals(filter));
	}

	@Override
	public ResourceMetaData resolve(String locator) throws IOException {
		return findInFolder(locator).findFirst().orElseThrow(() -> new StorageException("Resource not found: " + locator));
	}

	@Override
	protected ResourceMetaData resolveOrCreate(String locator) throws IOException {
		return findInFolder(locator).findFirst()
				.orElse(buildAndValidateMetadata(locator, basePath.resolve(locator).toString()));
	}

	@Override
	public Stream<ResourceMetaData> list() throws StorageException {
		return StorageException.wrap("list", () -> findInFolder(null));
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

	public static String resolvePlaceHolders(String path) {
		return path.replace("${user.home}", System.getProperty("user.home"))
				.replace("~", System.getProperty("user.home"));
	}

}
