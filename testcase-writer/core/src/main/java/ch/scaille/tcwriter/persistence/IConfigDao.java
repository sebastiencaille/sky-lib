package ch.scaille.tcwriter.persistence;

import java.io.IOException;

import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public interface IConfigDao {

    TCConfig getCurrentConfig();

	ObjectProperty<TCConfig> getCurrentConfigProperty();

    /**
     * Gets the dao according to the data type
     * @param <T>
     * @param daoType
     * @param subPath
     * @param dataHandlerRegistry
     * @return
     */
    <T> IDao<T> loaderOf(Class<T> daoType, String subPath, StorageDataHandlerRegistry dataHandlerRegistry);

	void saveConfiguration() throws IOException;

	
}
