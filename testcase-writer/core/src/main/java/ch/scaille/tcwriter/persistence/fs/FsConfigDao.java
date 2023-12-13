package ch.scaille.tcwriter.persistence.fs;

import java.nio.file.Path;
import java.nio.file.Paths;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.handlers.YamlConfigDataHandler;
import ch.scaille.util.persistence.ClassPathDao;
import ch.scaille.util.persistence.FileSystemDao;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageRTException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class FsConfigDao implements IConfigDao {

	private final FileSystemDao<TCConfig> loader;

	private final IPropertiesGroup propertiesGroup = PropertyChangeSupportController.mainGroup(this);

	private final ObjectProperty<TCConfig> currentConfig = new ObjectProperty<>("config", propertiesGroup);

	public static StorageDataHandlerRegistry defaultStorageDataHandler() {
		return new StorageDataHandlerRegistry(new YamlConfigDataHandler());
	}

	public static FsConfigDao localUser() {
		return withBaseFolder(Paths.get(FileSystemDao.resolvePlaceHolders("${user.home}/.tcwriter")));
	}

	public static FsConfigDao withBaseFolder(Path baseFolder) {
		return new FsConfigDao(baseFolder, defaultStorageDataHandler());
	}

	public FsConfigDao(Path baseFolder, StorageDataHandlerRegistry configSerDeserializerRegistry) {
		this.loader = new FileSystemDao<>(TCConfig.class, baseFolder, configSerDeserializerRegistry);
	}

	@Override
	public <T> IDao<T> loaderOf(Class<T> daoType, String locator, StorageDataHandlerRegistry dataHandlersRegistry) {
		if (locator.startsWith(ClassPathDao.PREFIX)) {
			return new ClassPathDao<>(daoType, locator, dataHandlersRegistry);
		}
		return new FileSystemDao<>(daoType, loader.getBaseFolder().resolve(locator), dataHandlersRegistry);
	}

	/**
	 * Applies the configuration file 
	 * @param locator the name of the configuration
	 */
	public FsConfigDao setConfiguration(String locator) {
		apply(StorageRTException.uncheck("Reading of configuration", () -> loader.load(locator)));
		return this;
	}

	public FsConfigDao setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	@Override
	public void saveConfiguration() {
		StorageRTException.uncheck("Writing of configuration",
				() -> loader.saveOrUpdate(currentConfig.getValue().getName(), currentConfig.getValue()));
	}

	@Override
	public ObjectProperty<TCConfig> getCurrentConfigProperty() {
		return currentConfig;
	}

	private void apply(TCConfig config) {
		this.currentConfig.setValue(this, config);
	}

}
