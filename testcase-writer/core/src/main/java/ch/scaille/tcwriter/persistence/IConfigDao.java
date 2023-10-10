package ch.scaille.tcwriter.persistence;

import java.io.IOException;
import java.util.function.Consumer;

import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public interface IConfigDao {

    void onReload(Consumer<TCConfig> hook);

    TCConfig getCurrentConfig();

    <T> IDao<T> loaderOf(Class<T> daoType, String subPath, StorageDataHandlerRegistry dataHandlerRegistry);

	void saveConfiguration() throws IOException;
	
}