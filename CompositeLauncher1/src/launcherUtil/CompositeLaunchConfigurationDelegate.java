package launcherUtil;

import java.util.ArrayList;
import java.util.List;

import launcherGui.CompositeTab;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class CompositeLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	public CompositeLaunchConfigurationDelegate() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 * 
	 * Launch all selected configuration
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
	
		List<String> mementos = configuration.getAttribute(CompositeTab.SELECTED_LAUNCHES, new ArrayList<>());
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		for(String memento: mementos){
			ILaunchConfiguration conf = manager.getLaunchConfiguration(memento);
			conf.launch(mode, monitor);
		}
	}

}
