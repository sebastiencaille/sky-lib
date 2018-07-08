package ch.skymarshall.tcwriter.test;

public interface ITestExecutionController {

	void beforeTestExecution() throws InterruptedException;

	void beforeStepExecution(int i) throws InterruptedException;

	void afterStepExecution(int i);

	void notifyError(Throwable error);

}
