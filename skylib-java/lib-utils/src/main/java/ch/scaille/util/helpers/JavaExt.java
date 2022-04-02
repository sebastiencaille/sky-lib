package ch.scaille.util.helpers;

import java.util.TimerTask;

public class JavaExt {
	
	private JavaExt() {
		// noop
	}

	public static TimerTask timerTask(Runnable runnable) {
		return new TimerTask() {

			@Override
			public void run() {
				runnable.run();
			}
		};
	}

}
