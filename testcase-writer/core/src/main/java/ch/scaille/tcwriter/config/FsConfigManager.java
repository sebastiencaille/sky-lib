package ch.scaille.tcwriter.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.yaml.snakeyaml.Yaml;

import ch.scaille.util.helpers.LambdaExt;

public class FsConfigManager {

	private final FsResourceLoader loader;

	private final List<Consumer<TCConfig>> onReloads = new ArrayList<>();

	private TCConfig currentConfig = null;

	public FsConfigManager() {
		loader = new FsResourceLoader(".", "yaml");
	}

	public FsConfigManager(Path baseFolder) {
		loader = new FsResourceLoader(baseFolder, "yaml");
	}
	
	public IResourceLoader configure(String locator, String extension) {
		if (locator.startsWith(CPResourceLoader.PREFIX)) {
			return new CPResourceLoader(locator, extension);
		}
		return loader.inSubFolder(locator, extension);
	}

	public FsConfigManager setConfiguration(String locator) {
		apply(LambdaExt.uncheck(() -> new Yaml().load(loader.read(locator))));
		return this;
	}

	public FsConfigManager setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	public void saveConfiguration() throws IOException {
		loader.write(currentConfig.getName(), new Yaml().dump(currentConfig));
	}

	public TCConfig getCurrentConfig() {
		return currentConfig;
	}

	private void apply(TCConfig config) {
		this.currentConfig = config;
		onReloads.forEach(h -> h.accept(config));
	}

	public void onReload(Consumer<TCConfig> hook) {
		onReloads.add(hook);
		if (currentConfig != null) {
			hook.accept(currentConfig);
		}
	}

}
