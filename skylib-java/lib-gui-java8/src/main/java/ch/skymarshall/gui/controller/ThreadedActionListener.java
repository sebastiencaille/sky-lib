/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.gui.controller;

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
