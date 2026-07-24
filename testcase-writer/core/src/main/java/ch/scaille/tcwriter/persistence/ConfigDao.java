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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class ConfigDao implements IConfigDao {

	protected final IPropertiesGroup propertiesGroup = PropertyChangeSupportController.mainGroup(this);

	protected final ObjectProperty<@Nullable TCConfig> currentConfig = new ObjectProperty<>("config", propertiesGroup, null);

	public static StorageDataHandlerRegistry defaultDataHandlers() {
		return new StorageDataHandlerRegistry(new YamlConfigDataHandler());
	}

	private final IDao<TCConfig> loader;

	public ConfigDao(DaoFactory daoFactory, String identifier, StorageDataHandlerRegistry configSerDeserializerRegistry) {
		this.loader = daoFactory.loaderOf(TCConfig.class, identifier, configSerDeserializerRegistry);
		this.propertiesGroup.transmitChangesBothWays();
	}
	
	@Override
	public ObjectProperty<@Nullable TCConfig> getCurrentConfigProperty() {
		return currentConfig;
	}

	protected void apply(TCConfig config) {
		this.currentConfig.setValue(this, config);
	}

	/**
	 * Applies the configuration file
	 * 
	 * @param identifier the name of the configuration
	 */
	public ConfigDao setConfiguration(String identifier) {
		apply(StorageRTException.uncheck("Reading of configuration: " + identifier, () -> loader.load(identifier)));
		return this;
	}

	public ConfigDao setConfiguration(TCConfig config) {
		apply(config);
		return this;
	}

	@Override
	public void saveConfiguration() {
		StorageRTException.uncheck("Writing of configuration",
				() -> loader.saveOrUpdate(Objects.requireNonNull(currentConfig.getValue(), "No configuration to save").getName(), currentConfig.getValue()));
	}

}
