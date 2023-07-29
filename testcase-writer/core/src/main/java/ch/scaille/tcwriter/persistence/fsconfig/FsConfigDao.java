package ch.scaille.tcwriter.persistence.fsconfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.CPResourceLoader;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.IResourceRepository;
import ch.scaille.tcwriter.persistence.JacksonFactory;
import ch.scaille.util.helpers.LambdaExt;

public class FsConfigDao implements IConfigDao {

	private static final JacksonFactory jacksonFactory = new JacksonFactory();

	private final FsResourceLoader loader;

	private final List<Consumer<TCConfig>> onReloads = new ArrayList<>();

	private TCConfig currentConfig = null;

	public static String resolvePlaceHolders(String path) {
		return path.replace("${user.home}", System.getProperty("user.home")).replace("~",
				System.getProperty("user.home"));
	}

	public static FsConfigDao local() {
		return new FsConfigDao(Paths.get(resolvePlaceHolders("${user.home}/.tcwriter")));
	}

	public FsConfigDao(Path baseFolder) {
		loader = new FsResourceLoader(baseFolder, "yaml");
	}

	@Override
	public IResourceRepository loaderOf(String locator, String extension) {
		if (locator.startsWith(CPResourceLoader.PREFIX)) {
			return new CPResourceLoader(locator, extension);
		}
		return loader.inSubFolder(locator, extension);
	}

	public FsConfigDao setConfiguration(String locator) {
		apply(LambdaExt.uncheck(() -> loader.read(locator).decode(jacksonFactory.yaml(TCConfig.class))));
		return this;
	}

	public FsConfigDao setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	@Override
	public void saveConfiguration() throws IOException {
		loader.write(currentConfig.getName(), jacksonFactory.yaml().writeValueAsString(currentConfig));
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
