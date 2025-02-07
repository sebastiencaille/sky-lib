package ch.scaille.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.jetbrains.annotations.NotNull;

public class PropertiesResourceBundle extends ResourceBundle {

	private final Properties props;

	public PropertiesResourceBundle(final Properties props) {
		this.props = props;
	}

	@Override
	protected Object handleGetObject(final String key) {
		return props.getOrDefault(key, "");
	}

	@Override
	public @NotNull Enumeration<String> getKeys() {
		return Collections.enumeration(props.stringPropertyNames());
	}

}
