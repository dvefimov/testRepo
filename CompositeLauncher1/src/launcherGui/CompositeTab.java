package launcherGui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;


public class CompositeTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {
	private List<ILaunchConfiguration> allLaunchConfiguration = new ArrayList<>(); // existing launch(LC) config in current system
	private List<ILaunchConfiguration> selectedLaunchConfiguration = new ArrayList<>(); // selected LC in current system
	private org.eclipse.swt.widgets.List uiSelectedList; // selected LC ui presentation
	private org.eclipse.swt.widgets.List uiList; // all LC ui presentation 
	
	public static final String COMPOSITE_TYPE_NAME = "CompositeConfiguration"; // type of my launch configuration  - look at plugin.xml
	public static final String SELECTED_LAUNCHES = "SelectedLaunches"; // name of attribute of selected LC 
	
	/**
     * Create main tab for gui of Composite Launch Configuration
     * @throws CoreException if an exception occurs retrieving configurations
     */
    public CompositeTab() throws CoreException{
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();		
		for(ILaunchConfiguration conf: manager.getLaunchConfigurations()){
			if (!COMPOSITE_TYPE_NAME.equals(conf.getType().getName())){
				allLaunchConfiguration.add(conf);
			}
		}
     }

    /** 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 * 
	 * Initialize from existing launch configuration
	 * for example look into \.metadata\.plugins\org.eclipse.debug.core\.launches dir in your file system to find list of LC
	 */
    @Override
    public void initializeFrom(final ILaunchConfiguration configuration) {
		List<String> mementos;
		try {
			mementos = configuration.getAttribute(CompositeTab.SELECTED_LAUNCHES, new ArrayList<>());
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			uiSelectedList.removeAll(); 
			selectedLaunchConfiguration = new ArrayList<>();
			for(String memento: mementos){
				ILaunchConfiguration conf = manager.getLaunchConfiguration(memento);
				selectedLaunchConfiguration.add(conf);
				uiSelectedList.add(conf.getName());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

    }	
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 * 
	 * Add list of selected LC as attribute to composite config
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {		
		List<String> mementos = new ArrayList<>();
		try {
			for(ILaunchConfiguration conf: selectedLaunchConfiguration){
				mementos.add(conf.getMemento());
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		configuration.setAttribute(SELECTED_LAUNCHES, mementos);
	}

	/** 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 * return name composite tab configuration  
	 */
	@Override
	public String getName() {
		return "Composite launch configuration";
	}


	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 * Create gui control for composite tab
	 */
	@Override
	public void createControl(Composite parent) {
		Composite topControl = new Composite(parent, SWT.NONE);
        topControl.setLayout(new GridLayout(1, false));

        Group group = new Group(topControl, SWT.NONE);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setLayout(new GridLayout(3, false));
        group.setText("Launchers");
        
        uiList = new org.eclipse.swt.widgets.List(group, SWT.SINGLE | 
                SWT.V_SCROLL | SWT.H_SCROLL);
        uiList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        for(ILaunchConfiguration conf: allLaunchConfiguration){
			uiList.add(conf.getName());
        }
        
        Group buttonGroup = new Group(group, SWT.NONE);
        buttonGroup.setLayout(new FillLayout(SWT.VERTICAL));
        Button addButton = new Button(buttonGroup, SWT.PUSH);
        addButton.setText(">");
        Button removeButton = new Button(buttonGroup, SWT.PUSH);
        removeButton.setText("<");

        uiSelectedList = new org.eclipse.swt.widgets.List(group, SWT.SINGLE | 
                SWT.V_SCROLL | SWT.H_SCROLL); 
        uiSelectedList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
       
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
            	int num = uiList.getFocusIndex();
            	System.out.println("add selected: " + num);
            	uiSelectedList.add(uiList.getSelection()[0]);
            	selectedLaunchConfiguration.add(allLaunchConfiguration.get(num));
//            	setDirty(true);
            }
        });
        removeButton.addSelectionListener(new SelectionAdapter() {
        	@Override
            public void widgetSelected(final SelectionEvent e) {
        		int num = uiList.getFocusIndex();
        		System.out.println("remove selected: " + num);
        		uiSelectedList.remove(uiSelectedList.getSelection()[0]);
            	selectedLaunchConfiguration.remove(num);
  //          	setDirty(true);
            }
		});
       
        setControl(topControl);
	}
	
	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 * Return is configuration ok for launch
	 */
	@Override
    public boolean isValid(final ILaunchConfiguration launchConfig) {
		// allow launch when a file is selected and file exists
        try {
        	if(selectedLaunchConfiguration.isEmpty()){
        		return false;
        	}
        	else{
        		boolean allValid = true;

/*
  	        		for(ILaunchConfiguration conf: selectedLaunchConfiguration){
todo if conf is invalid!	        			if(conf.i)
	        		}
*/	
        		return allValid;
        	}
        } catch (Exception e) {
            // on any configuration error
            setErrorMessage("Nothing to launch!");
        }

        return false;
    }	
	
}
