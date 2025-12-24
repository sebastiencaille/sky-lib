package ch.scaille.javabeans;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class DependenciesBuildingReport {

	@Nullable
	private static DependenciesBuildingReport screenBuildingReport = null;

	public static void setScreenBuildingReport(final DependenciesBuildingReport screenBuildingReport) {
		DependenciesBuildingReport.screenBuildingReport = screenBuildingReport;
	}

	private final StringBuilder report = new StringBuilder();

	@Override
	public synchronized String toString() {
		return report.toString();
	}

	public static synchronized void addDependency(final Object from, final Object to) {
		if (screenBuildingReport != null) {
			screenBuildingReport.report.append(from).append(" -> ").append(to).append('\n');
		}
	}

}
