package ch.scaille.tcwriter.persistence.fsconfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.handlers.YamlConfigDataHandler;
import ch.scaille.util.persistence.CPResourceRepository;
import ch.scaille.util.persistence.FsResourceRepository;
import ch.scaille.util.persistence.IResourceRepository;
import ch.scaille.util.persistence.StorageRTException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class FsConfigDao implements IConfigDao {

	private final FsResourceRepository<TCConfig> loader;

	private final List<Consumer<TCConfig>> onReloads = new ArrayList<>();

	private TCConfig currentConfig = null;

	public static StorageDataHandlerRegistry defaultStorageDataHandler() {
		return new StorageDataHandlerRegistry(new YamlConfigDataHandler());
	}

	public static FsConfigDao localUser() {
		return withBaseFolder(Paths.get(FsResourceRepository.resolvePlaceHolders("${user.home}/.tcwriter")));
	}

	public static FsConfigDao withBaseFolder(Path baseFolder) {
		return new FsConfigDao(baseFolder, defaultStorageDataHandler());
	}

	public FsConfigDao(Path baseFolder, StorageDataHandlerRegistry configSerDeserializerRegistry) {
		this.loader = new FsResourceRepository<>(TCConfig.class, baseFolder, configSerDeserializerRegistry);
	}

	@Override
	public <T> IResourceRepository<T> loaderOf(Class<T> daoType, String locator,
			StorageDataHandlerRegistry DataHandlersRegistry) {
		if (locator.startsWith(CPResourceRepository.PREFIX)) {
			return new CPResourceRepository<>(daoType, locator, DataHandlersRegistry);
		}
		return new FsResourceRepository<>(daoType, loader.getBaseFolder().resolve(locator), DataHandlersRegistry);
	}

	public FsConfigDao setConfiguration(String locator) {
		apply(StorageRTException.uncheck("Reading of configuration", () -> loader.read(locator)));
		return this;
	}

	public FsConfigDao setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	@Override
	public void saveConfiguration() {
		StorageRTException.uncheck("Writing of configuration",
				() -> loader.write(currentConfig.getName(), currentConfig));
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
