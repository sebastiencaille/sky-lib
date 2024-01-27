package ch.scaille.tcwriter.persistence.factory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.scaille.util.persistence.ClassPathDao;
import ch.scaille.util.persistence.FileSystemDao;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

/**
 * Factory responsible of creating the DAO, which is turning Resources loaded by
 * DataSource into Data
 */
public class DaoFactory {

	public interface IDataSourceFactory {
		boolean matches(String locator);

		<T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry);
	}

	public static class ClassPathDsFactory implements IDataSourceFactory {

		private final Set<String> whiteList = Set.of(DaoConfigs.USER_RESOURCES);

		@Override
		public boolean matches(String locator) {
			return locator.startsWith(DaoConfigs.CP_DATASOURCE);
		}

		@Override
		public <T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry) {
			return new ClassPathDao<>(daoType, locator.substring(DaoConfigs.CP_DATASOURCE.length()),
					dataHandlersRegistry, whiteList);
		}

	}

	public static class FsDsFactory implements IDataSourceFactory {

		private final Path baseFolder;

		public FsDsFactory(Path baseFolder) {
			this.baseFolder = baseFolder;
		}

		@Override
		public boolean matches(String locator) {
			return locator.startsWith(DaoConfigs.FS_DATASOURCE);
		}

		@Override
		public <T> IDao<T> create(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry) {
			var cleaned = locator;
			if (cleaned.startsWith(DaoConfigs.FS_DATASOURCE)) {
				cleaned = cleaned.substring(DaoConfigs.FS_DATASOURCE.length());
			}
			return new FileSystemDao<>(daoType, baseFolder.resolve(cleaned), dataHandlersRegistry);
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

	public static DaoFactory defaultsWith(IDataSourceFactory extraFactory) {
		final var factories = new ArrayList<IDataSourceFactory>();
		factories.add(new ClassPathDsFactory());
		return new DaoFactory(factories, extraFactory);
	}

}
