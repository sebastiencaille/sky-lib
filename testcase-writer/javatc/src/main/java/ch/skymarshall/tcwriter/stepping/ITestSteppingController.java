package ch.skymarshall.tcwriter.stepping;

public interface ITestSteppingController {

	void beforeTestExecution() throws InterruptedException;

	void beforeStepExecution() throws InterruptedException;

	void afterStepExecution();

	void notifyError(Throwable error);

}
