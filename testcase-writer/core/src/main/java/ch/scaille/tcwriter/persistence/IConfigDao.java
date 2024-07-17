package ch.scaille.tcwriter.persistence;

import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;

public interface IConfigDao {

	default TCConfig getCurrentConfig() {
		return getCurrentConfigProperty().getValue();
	}

	ObjectProperty<TCConfig> getCurrentConfigProperty();

	void saveConfiguration();

}
