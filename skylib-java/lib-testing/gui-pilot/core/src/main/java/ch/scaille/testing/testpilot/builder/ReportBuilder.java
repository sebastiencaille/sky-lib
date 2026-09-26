package ch.scaille.testing.testpilot.builder;

import ch.scaille.testing.testpilot.PilotReport;
import ch.scaille.testing.testpilot.PolledComponent;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.stream.Stream;

public class ReportBuilder {

    private static PilotReport.PilotReportBuilder<Object> NO_REPORT = new PilotReport.PilotReportBuilder<>() {
        @Override
        public void prepare(PolledComponent<Object> context) {
            // nope
        }

        @Override
        public @Nullable String build() {
            return null;
        }
    };

    public static <C> PilotReport.PilotReportBuilder<C> none() {
        return (PilotReport.PilotReportBuilder<C>) NO_REPORT;
    }


    public static class FormattedPilotReport<C> implements PilotReport.PilotReportBuilder<C> {
        private final String text;
        private final Function<PolledComponent<C>, String>[] parametersMapper;
        private String[] parameters = new String[0];

        public FormattedPilotReport(String text, Function<PolledComponent<C>, String>... parametersMapper) {
            this.text = text;
            this.parametersMapper = parametersMapper;
        }

        @Override
        public void prepare(PolledComponent<C> component) {
            parameters = Stream.of(parametersMapper)
                    .map(mapper -> mapper.apply(component))
                    .toArray(String[]::new);
        }

        @Override
        public @Nullable String build() {
            return text.formatted((Object[]) parameters);
        }
    }

}
