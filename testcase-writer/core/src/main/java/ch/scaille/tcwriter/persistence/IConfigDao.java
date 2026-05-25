package ch.scaille.tcwriter.persistence;

import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.TCConfig;
import org.jspecify.annotations.Nullable;

public interface IConfigDao {

	@Nullable
	default TCConfig getCurrentConfig() {
		return getCurrentConfigProperty().getValue();
	}

	ObjectProperty<@Nullable TCConfig> getCurrentConfigProperty();

	void saveConfiguration();

}
