package ch.scaille.tcwriter.services.testexec;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

import ch.scaille.tcwriter.services.testexec.TestApi.Command;
import ch.scaille.util.helpers.Logs;

public class TestExecutionFeedbackClient implements ITestExecutionFeedbackClient {

	private final Semaphore pauseSemaphore = new Semaphore(0);

	private final Set<Integer> breakpoints = new HashSet<>();

	private final Socket remoteControlConnection;

	private final TestApi api;

	private int currentStep = 0;

	public TestExecutionFeedbackClient() throws IOException {
		final var host = System.getProperty("test.host", "127.0.0.1");
		final var tcpPort = Integer.getInteger("test.port");
		if (tcpPort != null && tcpPort > 0) {
			final var remoteControlAddress = new InetSocketAddress(host, tcpPort);

			remoteControlConnection = new Socket();
			int retry = 5;
			while (!remoteControlConnection.isConnected() && retry > 0) {
				try {
					remoteControlConnection.connect(remoteControlAddress, 5000);
				} catch (final SocketTimeoutException e) {
					retry--;
				}
			}
			if (!remoteControlConnection.isConnected()) {
				throw new IllegalStateException("Cannot connect to remote control: " + remoteControlAddress);
			}
			api = new TestApi(remoteControlConnection);
			TestApi.handleCommands(api, this::commandHandler, null, () -> System.exit(1));
		} else {
			api = null;
			remoteControlConnection = null;
		}
	}

	private void commandHandler(final Command command) throws IOException {
		switch (command) {
		case RUN:
			pauseSemaphore.release();
			break;
		case SET_BREAKPOINT:
			breakpoints.add(api.readBreakpoint());
			break;
		case REMOVE_BREAKPOINT:
			breakpoints.remove(api.readBreakpoint());
			break;
		default:
			break;
		}
	}

	@Override
	public void beforeTestExecution() throws InterruptedException {
		currentStep = 0;
		if (remoteControlConnection != null) {
			pauseSemaphore.acquire();
		}
	}

	@Override
	public void beforeStepExecution() throws InterruptedException {
		currentStep++;
		writeStep(currentStep, Command.STEP_START);
		if (breakpoints.contains(currentStep)) {
			pauseSemaphore.acquire();
		}
	}

	@Override
	public void afterStepExecution() {
		writeStep(currentStep, Command.STEP_DONE);
	}

	public void writeStep(final int index, final Command command) {
		if (api != null) {
			try {
				api.writeStepCommand(command, index);
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	@Override
	public void notifyError(final Throwable error) {
		try (var errorStack = new StringWriter()) {
			error.printStackTrace(new PrintWriter(errorStack));
			api.writeError(currentStep, error, errorStack);
		} catch (final IOException e) {
			Logs.of(this).warning("Unable to send error: " + e.getMessage());
		}
	}

}
