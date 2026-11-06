package ch.scaille.tcwriter.persistence.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.ConfigDao;
import ch.scaille.tcwriter.persistence.ModelConfig;
import ch.scaille.tcwriter.persistence.ModelDao;
import ch.scaille.util.persistence.FileSystemDao;
import ch.scaille.util.persistence.DaoFactory.FsDsFactory;

public record DaoFactory(ConfigDao configDao, ModelDao modelDao) {

    public static String USER_RESOURCES = "userResources/";

    public static String cp(String path) {
        return ch.scaille.util.persistence.DaoFactory.CP_DATASOURCE + USER_RESOURCES + path;
    }

    public static Path homeFolder() {
        return Paths.get(FileSystemDao.resolvePlaceHolders("${user.home}/.tcwriter"));
    }

    public static Path tempFolder() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }

    public static DaoFactory withFolder(Path path, Module... modules) {
        final var flatModelConfig = new ModelConfig();
        flatModelConfig.setDictionaryPath(".");
        flatModelConfig.setTcPath(".");
        flatModelConfig.setTemplatePath("");
        flatModelConfig.setTcExportPath(".");
        return withFolder(path, new TCConfig("flat", List.of(flatModelConfig)), modules);
    }

    public static DaoFactory withFolder(Path path, TCConfig tcConfig, Module... modules) {
        final var fsDsFactory = new FsDsFactory(path, false);
        final var daoFactory = ch.scaille.util.persistence.DaoFactory.modulesPlus(Set.of(USER_RESOURCES), fsDsFactory, modules);
        final var configDao = new ConfigDao(daoFactory, ".", ConfigDao.defaultDataHandlers());
        configDao.setConfiguration(tcConfig);
        final var modelDao = new ModelDao(daoFactory, configDao.getSubConfig(ModelConfig.class), fsDsFactory, ModelDao.defaultDataHandlers());
        return new DaoFactory(configDao, modelDao);
    }

}
