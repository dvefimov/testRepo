package launcherGui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.CommonTab; 

/**
 * @author Daniil Efimov
 *
 */
public class CompositeLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		try {
			setTabs(new ILaunchConfigurationTab[] { new CompositeTab(), new CommonTab() });
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
