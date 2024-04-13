package ch.scaille.tcwriter.pilot.factories;

import java.util.stream.Collectors;

import ch.scaille.util.dao.metadata.DataObjectManagerFactory;

public interface Reporting {

	static String settingValue(String value) {
		return "setting: " + value;
	}

	static String settingValue(String location, String value) {
		return "setting " + location + ": " + value;
	}

	static String checkingThat(String message) {
		return "checking that " + message;
	}

	static String checkingValue(String value) {
		return "checking value: " + value;
	}

	static String checkingValue(String location, String value) {
		return "checking value " + location + ": " + value;
	}

	static String settingValue(String location, Object value) {
		return "setting " + location + ": ["
				+ DataObjectManagerFactory.createFor(value)
						.getMetaData()
						.getAttributes()
						.stream() //
						.filter(a -> a.getValueOf(value) != null) //
						.sorted((a1, a2) -> a1.getName().compareTo(a2.getName())) //
						.map(a -> a.getName() + ": " + a.getValueOf(value)) //
						.collect(Collectors.joining(", "))
				+ "]";
	}
}