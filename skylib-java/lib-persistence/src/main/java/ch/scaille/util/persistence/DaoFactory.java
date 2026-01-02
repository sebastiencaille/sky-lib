package ch.scaille.util.persistence;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import lombok.Getter;

/**
 * Factory responsible for creating the DAO, which is turning Resources loaded by
 * DataSource into Data
 */
public class DaoFactory {

	public static final String CP_DATASOURCE = "rsrc:";
	public static final String FS_DATASOURCE = "file:";

	public interface IDataSourceFactory {
		boolean matches(String locator);

		<T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry);
	}

	public static class ClassPathDsFactory implements IDataSourceFactory {

		private final Set<String> whiteList;

		public ClassPathDsFactory(Set<String> whiteList) {
			this.whiteList = whiteList;
		}

		@Override
		public boolean matches(String locator) {
			return locator.startsWith(CP_DATASOURCE);
		}

		@Override
		public <T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry) {
			return new ClassPathDao<>(daoType, locator.substring(CP_DATASOURCE.length()), dataHandlersRegistry,
					whiteList);
		}

	}

	@Getter
	public static class FsDsFactory implements IDataSourceFactory {

		private final Path baseFolder;

		public FsDsFactory(Path baseFolder) {
			this.baseFolder = baseFolder;
		}

		@Override
		public boolean matches(String locator) {
			return locator.startsWith(FS_DATASOURCE);
		}

		@Override
		public <T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry) {
			var cleaned = locator;
			if (cleaned.startsWith(FS_DATASOURCE)) {
				cleaned = cleaned.substring(FS_DATASOURCE.length());
			}
			final var resolved = baseFolder != null ? baseFolder.resolve(cleaned) : Paths.get(cleaned);
			return new FileSystemDao<>(daoType, resolved, dataHandlersRegistry);
		}

	}

	private final List<IDataSourceFactory> factories;
	private final IDataSourceFactory defaultDs;

	public DaoFactory(List<IDataSourceFactory> factories, IDataSourceFactory defaultDs) {
		this.factories = factories;
		this.defaultDs = defaultDs;
	}

	public <T> IDao<T> loaderOf(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlerRegistry) {
		return factories.stream()
				.filter(factory -> factory.matches(locator))
				.findFirst()
				.orElse(defaultDs)
				.create(daoType, locator, dataHandlerRegistry);
	}

	/**
	 * Creates a dao factory that includes resources on classpath, and more
	 * @param cpWhiteList the classpath whitelist (packages)
	 */
	public static DaoFactory cpPlus(Set<String> cpWhiteList, IDataSourceFactory extraFactory) {
		final var factories = new ArrayList<IDataSourceFactory>();
		factories.add(new ClassPathDsFactory(cpWhiteList));
		return new DaoFactory(factories, extraFactory);
	}

	public static String fs(String path) {
		return FS_DATASOURCE + path;
	}
}
