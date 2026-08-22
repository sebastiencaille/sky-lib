package ch.scaille.tcwriter.javatc.testexec.client;

public interface ITestExecutionFeedbackClient {

	void beforeTestExecution() throws InterruptedException;

	void beforeStepExecution() throws InterruptedException;

	void afterStepExecution();

	void notifyError(Throwable error);

}
