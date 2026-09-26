package ch.scaille.testing.testpilot.factories;

import static java.util.Comparator.comparing;

import java.util.function.Function;
import java.util.stream.Collectors;

import ch.scaille.testing.testpilot.PilotReport;
import ch.scaille.testing.testpilot.PilotReport.PilotReportBuilder;
import ch.scaille.testing.testpilot.PolledComponent;
import ch.scaille.testing.testpilot.builder.ReportBuilder.FormattedPilotReport;
import ch.scaille.util.dao.metadata.DataObjectManagerFactory;
import ch.scaille.util.dao.metadata.IAttributeMetaData;

public interface Reporting {

    public static <C> PilotReport.PilotReportBuilder<C> formatted(String text, Function<PolledComponent<C>, String>... parametersMapper) {
        return new FormattedPilotReport<>(text, parametersMapper);
    }

    public static <C> PilotReport.PilotReportBuilder<C> text(String text) {
        return formatted("%s: %s", PolledComponent::description, _ -> text);
    }
	
	static <C> PilotReportBuilder<C> settingValue(String value) {
		return text("setting: " + value);
	}

	static <C> PilotReportBuilder<C> settingValue(String location, String value) {
		return text("setting " + location + ": " + value);
	}

	static <C> PilotReportBuilder<C> checkingThat(String message) {
		return text("checking that " + message);
	}

	static <C> PilotReportBuilder<C> checkingValue(String value) {
		return text("checking value: " + value);
	}

	static <C> PilotReportBuilder<C> checkingValue(String location, String value) {
		return text("checking value " + location + ": " + value);
	}

	static <C> PilotReportBuilder<C> ettingValue(String location, Object value) {
		return text("setting " + location + ": ["
				+ DataObjectManagerFactory.createFor(value)
						.getMetaData()
						.getAttributes()
						.stream() //
						.filter(a -> a.getValueOf(value) != null) //
						.sorted(comparing(IAttributeMetaData::getName)) //
						.map(a -> a.getName() + ": " + a.getValueOf(value)) //
						.collect(Collectors.joining(", "))
				+ "]");
	}
}