package ch.scaille.tcwriter.persistence;

import ch.scaille.javabeans.IPropertiesGroup;
import ch.scaille.javabeans.PropertyChangeSupportController;
import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;
import ch.scaille.tcwriter.persistence.handlers.YamlConfigDataHandler;
import ch.scaille.util.persistence.DaoFactory;
import ch.scaille.util.persistence.IDao;
import ch.scaille.util.persistence.StorageRTException;
import ch.scaille.util.persistence.handlers.StorageDataHandlerRegistry;

public class ConfigDao implements IConfigDao {

	protected final IPropertiesGroup propertiesGroup = PropertyChangeSupportController.mainGroup(this);

	protected final ObjectProperty<TCConfig> currentConfig = new ObjectProperty<>("config", propertiesGroup);

	public static StorageDataHandlerRegistry defaultDataHandlers() {
		return new StorageDataHandlerRegistry(new YamlConfigDataHandler());
	}

	private final IDao<TCConfig> loader;

	public ConfigDao(DaoFactory daoFactory, String locator, StorageDataHandlerRegistry configSerDeserializerRegistry) {
		this.loader = daoFactory.loaderOf(TCConfig.class, locator, configSerDeserializerRegistry);
		this.propertiesGroup.enableAllTransmit();
	}
	
	@Override
	public ObjectProperty<TCConfig> getCurrentConfigProperty() {
		return currentConfig;
	}

	protected void apply(TCConfig config) {
		this.currentConfig.setValue(this, config);
	}

	/**
	 * Applies the configuration file
	 * 
	 * @param locator the name of the configuration
	 */
	public ConfigDao setConfiguration(String locator) {
		apply(StorageRTException.uncheck("Reading of configuration", () -> loader.load(locator)));
		return this;
	}

	public ConfigDao setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	@Override
	public void saveConfiguration() {
		StorageRTException.uncheck("Writing of configuration",
				() -> loader.saveOrUpdate(currentConfig.getValue().getName(), currentConfig.getValue()));
	}

}
