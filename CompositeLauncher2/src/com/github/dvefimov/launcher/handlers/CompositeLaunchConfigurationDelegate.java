package com.github.dvefimov.launcher.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;
import com.github.dvefimov.launcher.ui.CompositeTab;
import com.github.dvefimov.launcher.ui.TerminateWindow;

/**
 * @author Daniil Efimov
 * 
 */

public class CompositeLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {
	TerminateWindow terminateWindow = new TerminateWindow();
	CompositeLauncherLogger debug = new CompositeLauncherLogger();

	/**
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration,
	 *      java.lang.String, org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 * 
	 *      Launch all selected configuration
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		List<?> attrs = (List<?>) configuration.getAttribute(
				CompositeTab.SELECTED_LAUNCHES, new ArrayList<>());
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		List<ILaunch> launchers = new ArrayList<>();
		for (Object memento : attrs) {
			if (memento instanceof String) {
				ILaunchConfiguration conf = manager
						.getLaunchConfiguration((String) memento);
				ILaunch iLaunch = conf.launch(mode, monitor);
				launchers.add(iLaunch);
			}
		}

		if (!launchers.isEmpty()) {
			terminateWindow.createWindow(launchers);
		} else {
			debug.log("smth wrong! - list of LC isEmpty!");
		}
	}
}
