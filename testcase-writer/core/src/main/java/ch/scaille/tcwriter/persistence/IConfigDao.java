package ch.scaille.tcwriter.persistence;

import ch.scaille.javabeans.properties.ObjectProperty;
import ch.scaille.tcwriter.model.config.SubConfig;
import ch.scaille.tcwriter.model.config.TCConfig;

public interface IConfigDao {

	ObjectProperty<TCConfig> getCurrentConfig();

	void saveConfiguration();

    default <S extends SubConfig> ObjectProperty<S> getSubConfig(Class<S> subConfigClass) {
		return getCurrentConfig().child(subConfigClass.getName(), config -> config
				.getSubconfig(subConfigClass)
				.orElseThrow(() -> new IllegalStateException("Missing configuration: " + subConfigClass.getName())), (_, _) -> { });
	}

}
