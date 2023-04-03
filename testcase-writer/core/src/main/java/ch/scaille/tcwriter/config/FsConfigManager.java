package ch.scaille.tcwriter.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import ch.scaille.util.helpers.LambdaExt;

public class FsConfigManager implements IConfigManager {

	private static final YAMLMapper configReader = new YAMLMapper().configure(Feature.USE_NATIVE_TYPE_ID, true)
			.configure(Feature.USE_NATIVE_OBJECT_ID, true);

	private final FsResourceLoader loader;

	private final List<Consumer<TCConfig>> onReloads = new ArrayList<>();

	private TCConfig currentConfig = null;

	public static String resolvePlaceHolders(String path) {
		return path.replace("${user.home}", System.getProperty("user.home")).replace("~",
				System.getProperty("user.home"));
	}

	public static FsConfigManager local() {
		return new FsConfigManager(Paths.get(resolvePlaceHolders("${user.home}/.tcwriter")));
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
		apply(LambdaExt.uncheck(() -> configReader.readValue(loader.read(locator), TCConfig.class)));
		return this;
	}

	public FsConfigManager setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	public void saveConfiguration() throws IOException {
		loader.write(currentConfig.getName(), configReader.writeValueAsString(currentConfig));
	}

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
