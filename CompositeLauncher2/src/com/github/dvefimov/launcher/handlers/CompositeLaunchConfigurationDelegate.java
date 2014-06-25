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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;
import com.github.dvefimov.launcher.ui.CompositeTab;
import com.github.dvefimov.launcher.ui.TerminateWindow;

/**
 * @author Daniil Efimov
 * 
 */

public class CompositeLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {
	
	TerminateWindow terminateWindow = new TerminateWindow(); // graphic presentation of terminate window
	CompositeLauncherLogger debug = new CompositeLauncherLogger(); 
	static Object upLevel = null; // check is SWT display ready readAndDispatch 

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
		if(upLevel == null){
			upLevel = attrs;
		}
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		final List<ILaunch> launchers = new ArrayList<>();
		for (Object memento : attrs) {
			if (memento instanceof String) {
				ILaunchConfiguration conf = manager
						.getLaunchConfiguration((String) memento);
				ILaunch iLaunch = conf.launch(mode, monitor);
				launchers.add(iLaunch);
			}
		}

		Display display;
		if (!launchers.isEmpty()) {
			new Runnable() {
				public void run() {
					terminateWindow.createWindow(launchers);					
				}
			}.run();
		} else {
			debug.log("smth wrong! - list of LC isEmpty!");
		}
		
		if(upLevel.equals(attrs)){
			display = terminateWindow.getDisplay();
			Shell[] shells = display.getShells();
			while (!terminateWindow.allShellIsDisposed(shells)) {
				if (!display.readAndDispatch ()) display.sleep ();
			}
			display.dispose ();
		}
	}
	
}
