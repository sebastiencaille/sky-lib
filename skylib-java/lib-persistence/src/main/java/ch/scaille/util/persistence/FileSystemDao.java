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
	private Stream<ResourceMetaData> inFolder(ResourceMetaData metadata) throws IOException {
		final var locator = metadata.getLocator();
		if (locator != null && locator.isEmpty()) {
			// basePath points to a file
			return Collections.singleton(metadataOf(basePath.getFileName().toString(), basePath.toString())).stream();
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
			return Collections.<ResourceMetaData>emptyList().stream();
		}
		return Files.list(folder)
				.filter(f -> filter == null || f.getFileName().toString().startsWith(filter))
				.map(p -> optionalMetadataOf(p.getFileName().toString(), p.toString()))
				.filter(m -> m.isPresent() && (filter == null || m.get().getLocator().equals(filter)))
				.map(Optional::get);
	}

	@Override
	public ResourceMetaData resolve(ResourceMetaData metadata) throws IOException {
		return inFolder(fixExtension(metadata)).findFirst()
				.orElseThrow(() -> new StorageException("Resource not found: " + metadata));
	}

	@Override
	protected ResourceMetaData resolveOrCreate(ResourceMetaData metadata) throws IOException {
		final var fixedMetadata = fixExtension(metadata);
		return inFolder(fixedMetadata).findFirst()
				.orElse(metadataOf(fixedMetadata.getLocator(), basePath.resolve(fixedMetadata.getLocator()).toString()));
	}

	private ResourceMetaData fixExtension(ResourceMetaData resourceMeta) {
		// Correct the extension if not consistent
		final var nameAndExt = nameAndExtensionOf(resourceMeta.getStorageLocator());
		final var handler = dataHandlerRegistry.find(resourceMeta.getMimeType());
		if (handler.isPresent() && !handler.get().supports(nameAndExt[1])
				&& !handler.get().getDefaultExtension().isEmpty()) {
			return resourceMeta
					.withStorageLocator(resourceMeta.getStorageLocator() + '.' + handler.get().getDefaultExtension());
		}
		return resourceMeta;
	}

	@Override
	public Stream<ResourceMetaData> list() throws StorageException {
		return StorageException.wrap("list", () -> inFolder(null));
	}

	@Override
	protected Resource<String> readRaw(ResourceMetaData resourceMeta) throws IOException {
		final var resource = resolve(resourceMeta);
		LOGGER.info(() -> "Reading " + resource);
		return resource.withValue(Files.readString(Paths.get(resource.getStorageLocator()), StandardCharsets.UTF_8));
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
