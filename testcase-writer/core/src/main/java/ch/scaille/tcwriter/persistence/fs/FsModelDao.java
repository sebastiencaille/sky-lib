package ch.scaille.tcwriter.persistence.fs;

import java.nio.file.Path;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.testcase.ExportableTestCase;
import ch.scaille.tcwriter.persistence.AbstractModelDao;
import ch.scaille.tcwriter.persistence.IConfigDao;
import ch.scaille.tcwriter.persistence.handlers.JsonModelDataHandler;
import ch.scaille.tcwriter.persistence.handlers.YamlModelDataHandler;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;
import ch.scaille.util.persistence.handlers.TextStorageHandler;

public class FsModelDao extends AbstractModelDao {

	private StorageDataHandlerRegistry serDeserializerRegistry;

	public static StorageDataHandlerRegistry defaultDataHandlers() {
		var modelSerDeserializerRegistry = new StorageDataHandlerRegistry(new YamlModelDataHandler());
		modelSerDeserializerRegistry.register(new JsonModelDataHandler());
		modelSerDeserializerRegistry.register(new TextStorageHandler());
		return modelSerDeserializerRegistry;
	}

	private static FsModelConfig configOf(TCConfig config) {
		return config.getSubconfig(FsModelConfig.class)
				.orElseThrow(() -> new IllegalStateException("Cannot find FsModelConfig"));
	}

	public FsModelDao(IConfigDao configLoader) {
		this(configLoader, defaultDataHandlers());
	}

	public FsModelDao(IConfigDao configDao, StorageDataHandlerRegistry serDeserializerRegistry) {
		super(configDao);
		this.serDeserializerRegistry = serDeserializerRegistry;
		reload();
	}

	@Override
	protected void reload(TCConfig config) {
		final var fsConfig = configOf(config);
		this.dictionaryRepo = configDao.loaderOf(TestDictionary.class, fsConfig.getDictionaryPath(),
				serDeserializerRegistry);
		this.testCaseRepo = configDao.loaderOf(ExportableTestCase.class, fsConfig.getTcPath(), serDeserializerRegistry);
		this.templateRepo = configDao.loaderOf(String.class, fsConfig.getTemplatePath(), serDeserializerRegistry);
		this.testCaseCodeRepo = configDao.loaderOf(String.class, fsConfig.getTcExportPath(), serDeserializerRegistry);
	}

	public Path getTCFolder() {
		return Path.of(configOf(configDao.getCurrentConfig()).getTcPath());
	}

}
