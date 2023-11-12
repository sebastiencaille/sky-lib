package ch.scaille.tcwriter.persistence;

import java.io.IOException;

import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public interface IConfigDao {

	default TCConfig getCurrentConfig() {
		return getCurrentConfigProperty().getValue();
	}

	ObjectProperty<TCConfig> getCurrentConfigProperty();

	/**
	 * Gets the dao according to the data type and configuration
	 */
	<T> IDao<T> loaderOf(Class<T> daoType, String path, StorageDataHandlerRegistry dataHandlerRegistry);

	void saveConfiguration() throws IOException;

}
