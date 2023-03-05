package ch.scaille.tcwriter.testexec;

import ch.scaille.tcwriter.testexec.TestApi.StepState;

public class StepStatus {
	public final int ordinal;
	public boolean breakPoint = false;
	public StepState state = StepState.NOT_RUN;
	public String message;

	public StepStatus(final int ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public String toString() {
		return ordinal + "/bp=" + breakPoint + ",state=" + state + ",message=" + message;
	}
}
