package ch.scaille.tcwriter.testexec;

public interface ITestExecutionFeedbackClient {

	void beforeTestExecution() throws InterruptedException;

	void beforeStepExecution() throws InterruptedException;

	void afterStepExecution();

	void notifyError(Throwable error);

}
