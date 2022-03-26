package ch.scaille.tcwriter.stepping;

import ch.scaille.tcwriter.stepping.TestApi.StepState;

public class StepStatus {
	public final int ordinal;
	public boolean breakPoint = false;
	public StepState state = null;
	public String message;

	public StepStatus(final int ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public String toString() {
		return ordinal + "/bp=" + breakPoint + ",state=" + state + ",message=" + message;
	}
}
