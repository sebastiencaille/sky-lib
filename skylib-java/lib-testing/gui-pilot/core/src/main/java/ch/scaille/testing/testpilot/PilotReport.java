package ch.scaille.testing.testpilot;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@NullMarked
public class PilotReport {

	public interface ReportFunction<C> {
		String build(PolledComponent<C> context, @Nullable String text);
	}

	private final List<String> report = new ArrayList<>();

	public void report(final String reportLine) {
		report.add(reportLine);
	}

    public String getFormattedReport() {
		return String.join("\n", report);
	}

	@Override
	public String toString() {
		return "Report: " + report.size() + " lines";
	}

}
