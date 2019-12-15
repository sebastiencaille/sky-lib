package ch.skymarshall.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesResourceBundle extends ResourceBundle {

	private final Properties props;

	public PropertiesResourceBundle(final Properties props) {
		this.props = props;
	}

	@Override
	protected Object handleGetObject(final String var1) {
		return props.getOrDefault(var1, "");
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(props.stringPropertyNames());
	}

}
