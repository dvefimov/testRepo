package com.github.dvefimov.launcher.debug;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class CompositeLauncherLogger extends Plugin {
	public static final String PLUGIN_ID = "com.github.dvefimov.launcher";

	public void log(String msg) {
		log(msg, null);
	}

	public void log(String msg, Exception e) {
		getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, e));
	}

	public void logError(String msg, Exception e) {
		getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, msg, e));
	}

}
