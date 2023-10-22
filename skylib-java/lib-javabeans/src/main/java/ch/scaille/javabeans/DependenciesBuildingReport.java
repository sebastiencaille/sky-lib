package ch.scaille.javabeans;

public class DependenciesBuildingReport {

	private static DependenciesBuildingReport screenBuildingReport;

	public static void setScreenBuildingReport(final DependenciesBuildingReport screenBuildingReport) {
		DependenciesBuildingReport.screenBuildingReport = screenBuildingReport;
	}

	private final StringBuilder report = new StringBuilder();

	@Override
	public String toString() {
		return report.toString();
	}

	public static void addDependency(final Object from, final Object to) {
		if (screenBuildingReport != null) {
			screenBuildingReport.report.append(from).append(" -> ").append(to).append('\n');
		}
	}

}
