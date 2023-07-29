package ch.scaille.tcwriter.services.testexec;

public interface TestExecutionListener {

	default void testRunning(boolean running)  {
		// dummy
	}
	

	default void testPaused(boolean paused)  {
		// dummy
	}
	

	default void testFinished()  {
		// dummy
	}
}
