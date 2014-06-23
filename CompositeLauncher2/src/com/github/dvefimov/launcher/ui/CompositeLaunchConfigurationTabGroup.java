package com.github.dvefimov.launcher.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;

/**
 * @author Daniil Efimov
 * 
 */

public class CompositeLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		try {
			setTabs(new ILaunchConfigurationTab[] { new CompositeTab(),
					new CommonTab() });
		} catch (CoreException e) {
			CompositeLauncherLogger debug = new CompositeLauncherLogger();
			debug.logError("Could not create CompositeTab.", e);
			throw new RuntimeException(e);
		}
	}
}
