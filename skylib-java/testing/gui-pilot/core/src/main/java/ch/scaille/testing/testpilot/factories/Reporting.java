package ch.scaille.testing.testpilot.factories;

import static java.util.Comparator.comparing;

import java.util.stream.Collectors;

import ch.scaille.util.dao.metadata.DataObjectManagerFactory;
import ch.scaille.util.dao.metadata.IAttributeMetaData;

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
						.sorted(comparing(IAttributeMetaData::getName)) //
						.map(a -> a.getName() + ": " + a.getValueOf(value)) //
						.collect(Collectors.joining(", "))
				+ "]";
	}
}