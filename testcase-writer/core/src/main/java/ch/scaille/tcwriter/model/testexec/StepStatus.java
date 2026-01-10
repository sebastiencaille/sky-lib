package ch.scaille.tcwriter.model.testexec;

import lombok.Getter;
import lombok.Setter;

public class StepStatus {

	public enum StepState {
		NOT_RUN, STARTED, OK, FAILED
	}

	
	public final int ordinal;
	@Setter
    @Getter
    private boolean breakPoint = false;
	@Setter
    @Getter
    private StepState state = StepState.NOT_RUN;
	@Setter
    @Getter
    private String message;

    public StepStatus(final int ordinal) {
		this.ordinal = ordinal;
	}

	@Override
	public String toString() {
		return ordinal + "/bp=" + breakPoint + ",state=" + state + ",message=" + message;
	}

}
