package ch.scaille.tcwriter.persistence.fsconfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	private final List<Consumer<TCConfig>> onReloads = new ArrayList<>();

	private TCConfig currentConfig = null;

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
	public <T> IDao<T> loaderOf(Class<T> daoType, String locator,
			StorageDataHandlerRegistry dataHandlersRegistry) {
		if (locator.startsWith(ClassPathDao.PREFIX)) {
			return new ClassPathDao<>(daoType, locator, dataHandlersRegistry);
		}
		return new FileSystemDao<>(daoType, loader.getBaseFolder().resolve(locator), dataHandlersRegistry);
	}

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
				() -> loader.saveOrUpdate(currentConfig.getName(), currentConfig));
	}

	@Override
	public TCConfig getCurrentConfig() {
		return currentConfig;
	}

	private void apply(TCConfig config) {
		this.currentConfig = config;
		onReloads.forEach(h -> h.accept(config));
	}

	@Override
	public void onReload(Consumer<TCConfig> hook) {
		onReloads.add(hook);
		if (currentConfig != null) {
			hook.accept(currentConfig);
		}
	}

}
