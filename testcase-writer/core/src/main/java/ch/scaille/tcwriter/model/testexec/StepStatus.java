package ch.scaille.tcwriter.model.testexec;

public class StepStatus {

	public enum StepState {
		NOT_RUN, STARTED, OK, FAILED
	}

	
	public final int ordinal;
	private boolean breakPoint = false;
	private StepState state = StepState.NOT_RUN;
	private String message;

	public void setBreakPoint(boolean breakPoint) {
		this.breakPoint = breakPoint;
	}

	public boolean isBreakPoint() {
		return breakPoint;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public StepState getState() {
		return state;
	}

	public void setState(StepState state) {
		this.state = state;
	}

	public StepStatus(final int ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public String toString() {
		return ordinal + "/bp=" + breakPoint + ",state=" + state + ",message=" + message;
	}

}
