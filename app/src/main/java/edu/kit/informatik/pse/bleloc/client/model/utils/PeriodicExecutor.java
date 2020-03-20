package edu.kit.informatik.pse.bleloc.client.model.utils;

import android.os.Handler;

/**
 * Executes a certain task periodically with a fixed interval. It is a general abstraction concept to hide platform
 * dependent implementations. An example is the use of Android's <code>Handler</code> instead of the native Java
 * <code>Timer</code> due to increased CPU usage.
 */
public class PeriodicExecutor {
	private Handler handler;
	private Runnable runnableCode;
	private boolean activated = false;

	/**
	 * Creates a new Timer from seconds of delay and function to call on every full interval.
	 *
	 * @param milliSeconds
	 * 		The Timer interval length in seconds.
	 * @param callback
	 * 		The fuction to call on every full interval.
	 */
	public PeriodicExecutor(int milliSeconds, Runnable callback) {
		this.handler = new Handler();
		this.runnableCode = new Runnable() {
			@Override
			public void run() {
				callback.run();
				handler.postDelayed(this, milliSeconds);
			}
		};
	}

	/**
	 * Activates or deactivates the timer firing.
	 *
	 * @param active
	 * 		<code>true</code> activates the timer, <code>false</code> deactivates it
	 */
	public void setTimerActive(boolean active) {
		if (active) {
			if (!activated) {
				handler.post(runnableCode);
				activated = true;
			}
		} else {
			if (activated) {
				handler.removeCallbacks(runnableCode);
				activated = false;
			}
		}
	}
}
