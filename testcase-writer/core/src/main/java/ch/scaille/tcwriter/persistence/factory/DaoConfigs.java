package ch.scaille.tcwriter.persistence.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ConfigDao;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.FileSystemDao;
import ch.scaille.util.persistence.DaoFactory.FsDsFactory;

public interface DaoConfigs {

	String USER_RESOURCES = "userResources/";

	static String cp(String path) {
		return DaoFactory.CP_DATASOURCE + USER_RESOURCES + path;
	}

	static Path homeFolder() {
		return Paths.get(FileSystemDao.resolvePlaceHolders("${user.home}/.tcwriter"));
	}

	static Path tempFolder() {
		return Paths.get(System.getProperty("java.io.tmpdir"));
	}

	ConfigDao configDao();

	ModelDao modelDao();

	static DaoConfigs withFolder(Path path) {
		final var daoFactory = DaoFactory.cpPlus(Set.of(USER_RESOURCES), new FsDsFactory(path));
		final var configDao = new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers());
		final ModelDao modelDao = new ModelDao(daoFactory, configDao.getCurrentConfigProperty(),
                ModelDao::defaultDataHandlers);
		
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
