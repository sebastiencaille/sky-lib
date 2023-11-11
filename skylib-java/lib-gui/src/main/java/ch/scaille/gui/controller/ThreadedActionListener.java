package ch.scaille.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.FutureTask;

public abstract class ThreadedActionListener implements ActionListener {

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
		new Thread(futureTask).start();
	}

	public FutureTask<Void> getFutureTask() {
		return futureTask;
	}

}
