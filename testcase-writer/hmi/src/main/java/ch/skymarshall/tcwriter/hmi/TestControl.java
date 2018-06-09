package ch.skymarshall.tcwriter.hmi;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.skymarshall.util.helpers.Timeout;
import org.skymarshall.util.helpers.Timeout.TimeoutFactory;

import ch.skymarshall.tcwriter.generators.model.TestStep;
import ch.skymarshall.tcwriter.test.TestExecutionController;
import ch.skymarshall.tcwriter.test.TestExecutionController.Command;

public class TestControl {

	private final int tcpPort;

	private final Set<Integer> breakPoints = new HashSet<>();

	private Socket controlConnection = null;

	private int currentStep = -1;

	private BiConsumer<Integer, Integer> stepChangedListener;

	public TestControl(final int tcpPort) {
		this.tcpPort = tcpPort;
	}

	public void addBreakpoint(final TestStep testStep) {
		breakPoints.add(testStep.getOrdinal());
	}

	public void removeBreakpoint(final TestStep testStep) {
		breakPoints.remove(testStep.getOrdinal());
	}

	public Object hasBreakpoint(final TestStep testStep) {
		return breakPoints.contains(testStep.getOrdinal());
	}

	public void start() throws InterruptedException, IOException {
		controlConnection = null;

		final Timeout timeout = new TimeoutFactory(5, TimeUnit.SECONDS).createTimeout();
		while (controlConnection == null && !timeout.hasTimedOut()) {
			try {
				controlConnection = new Socket("127.0.0.1", tcpPort);
			} catch (final IOException e) {
				Thread.sleep(500);
				System.out.println(e.getMessage());
			}
		}
		if (timeout.hasTimedOut()) {
			throw new IllegalStateException("Cannot connect to test case");
		}
		System.out.println("Connected");
		TestExecutionController.handleCommands(controlConnection, command -> {
			if (command == Command.STEP) {
				updateStep(TestExecutionController.readInt(controlConnection.getInputStream()));
			}
		}, () -> updateStep(-1));

		for (

		final Integer breakPoint : breakPoints) {
			controlConnection.getOutputStream().write(TestExecutionController.Command.SET_BREAKPOINT.cmd);
			TestExecutionController.writeInt(controlConnection, breakPoint);
		}
		controlConnection.getOutputStream().write(TestExecutionController.Command.RUN.cmd);
	}

	public void resume() throws IOException {
		controlConnection.getOutputStream().write(TestExecutionController.Command.RUN.cmd);
	}

	public boolean isRunning(final int stepIndex) {
		return currentStep == stepIndex;
	}

	private void updateStep(final int newStep) {
		final int oldStep = currentStep;
		currentStep = newStep;
		stepChangedListener.accept(oldStep, currentStep);
	}

	public void setStepListener(final BiConsumer<Integer, Integer> stepChangedListener) {
		this.stepChangedListener = stepChangedListener;
	}

}