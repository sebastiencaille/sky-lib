package ch.scaille.tcwriter.persistence.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ConfigDao;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.tcwriter.persistence.factory.DaoFactory.FsDsFactory;
import ch.scaille.util.persistence.FileSystemDao;

public interface DaoConfigs {

	public static final String CP_DATASOURCE = "rsrc:";

	public static final String USER_RESOURCES = "userResources/";

	public static final String FS_DATASOURCE = "file:";

	public static String cp(String path) {
		return CP_DATASOURCE + USER_RESOURCES + path;
	}

	public static Path homeFolder() {
		return Paths.get(FileSystemDao.resolvePlaceHolders("${user.home}/.tcwriter"));
	}

	public static Path tempFolder() {
		return Paths.get(System.getProperty("java.io.tmpdir"));
	}

	ConfigDao configDao();

	ModelDao modelDao();

	public static DaoConfigs withFolder(Path path) {
		final var daoFactory = DaoFactory.defaultsPlus(new FsDsFactory(path));
		final var configDao = new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers());
		final var modelDao = new ModelDao(daoFactory, configDao.getCurrentConfigProperty(),
				ModelDao.defaultDataHandlers());
		
		final var tempModelConfig = new ModelConfig();
		tempModelConfig.setDictionaryPath(".");
		tempModelConfig.setTcPath(".");
		tempModelConfig.setTemplatePath("");
		tempModelConfig.setTcExportPath(".");
		configDao.setConfiguration(new TCConfig("temp", List.of(tempModelConfig)));
		
		return new DaoConfigs() {
			@Override
			public ConfigDao configDao() {
				return configDao;
			}

			@Override
			public ModelDao modelDao() {
				return modelDao;
			}
		};
	}

}
