package ch.skymarshall.tcwriter.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class TestExecutionController implements Runnable {

	public enum Command {
		SET_BREAKPOINT('b'), REMOVE_BREAKPOINT('c'), RUN('r'), STEP('s'), EXIT((char) 255);
		public final char cmd;

		private Command(final char cmd) {
			this.cmd = cmd;
		}

		public static Command from(final int cmd) {
			for (final Command command : Command.values()) {
				if (command.cmd == (char) (cmd & 0xFF)) {
					return command;
				}
			}
			throw new IllegalArgumentException("No command for " + cmd);
		}
	}

	private ServerSocket serverSocket;

	private final Semaphore pauseSemaphore = new Semaphore(0);

	private final Set<Integer> breakpoints = new HashSet<>();

	private Socket connection;

	public TestExecutionController() throws IOException {
		final Integer port = Integer.getInteger("test.port");
		if (port != null) {
			serverSocket = new ServerSocket(port);
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		try {
			while ((connection = serverSocket.accept()) != null) {
				handleCommands(connection, command -> {
					switch (command) {
					case RUN:
						pauseSemaphore.release();
						break;
					case SET_BREAKPOINT:
						breakpoints.add(readInt(connection.getInputStream()));
						break;
					case REMOVE_BREAKPOINT:
						breakpoints.remove(readInt(connection.getInputStream()));
						break;
					default:
						break;
					}

				}, null);
			}
		} catch (final IOException e) {
			// ignore
		}
	}

	public void beforeTestExecution() throws InterruptedException {
		if (serverSocket != null) {
			pauseSemaphore.acquire();
		}
		System.out.println("Breakpoints: " + breakpoints);
	}

	public void beforeStepExecution(final int index) throws InterruptedException {
		writeStep(index);
		if (breakpoints.contains(index)) {
			System.out.println("Breakpoint");
			pauseSemaphore.acquire();
			System.out.println("Released");
		}
	}

	public void afterStepExecution(final int index) {
		// nothing for now
	}

	private void writeStep(final int index) {
		if (serverSocket != null) {
			try {
				connection.getOutputStream().write(Command.STEP.cmd);
				writeInt(connection, index);
			} catch (final IOException e) {
				// ignore
			}
		}
	}

	@FunctionalInterface
	public interface CommandHandler {
		void execute(Command command) throws IOException;
	}

	public static void handleCommands(final Socket connection, final CommandHandler commandHandler,
			final Runnable disconnectionHandler) {
		new Thread(() -> {
			try {
				Command receivedCommand;
				while ((receivedCommand = Command.from(connection.getInputStream().read())) != Command.EXIT) {
					commandHandler.execute(receivedCommand);
				}
			} catch (final IOException e) {
				// ignore
			} finally {
				if (disconnectionHandler != null) {
					disconnectionHandler.run();
				}
			}
		}).start();
	}

	public static int readInt(final InputStream inputStream) throws IOException {
		final int high = inputStream.read();
		final int low = inputStream.read();
		return ((high & 0xFF) << 8) | (low & 0xFF);
	}

	public static void writeInt(final Socket connection, final int value) throws IOException {
		connection.getOutputStream().write(value >> 8 & 0xFF);
		connection.getOutputStream().write(value & 0xFF);
	}

}
