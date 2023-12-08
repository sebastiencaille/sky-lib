package ch.scaille.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadedActionListener implements ActionListener {

	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 4, 30L, TimeUnit.SECONDS,
			new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());

	private FutureTask<Void> futureTask;

	public abstract void actionPerformedInThread(final ActionEvent e);

	protected abstract void handleRuntimeException(final RuntimeException e);

	@Override
	public void actionPerformed(final ActionEvent e) {
		futureTask = new FutureTask<>(() -> {
			try {
				actionPerformedInThread(e);
			} catch (final RuntimeException e2) {
				handleRuntimeException(e2);
			}
			return null;
		});
		run();
	}

	protected void run() {
		executor.execute(futureTask);
	}

	public FutureTask<Void> getFutureTask() {
		return futureTask;
	}

}
