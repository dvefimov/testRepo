package com.github.dvefimov.launcher.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;


/**
 * @author Daniil Efimov
 * 
 */

/**
 * Graphic presentation process terminate window
 */

public class TerminateWindow {

	CompositeLauncherLogger debug = new CompositeLauncherLogger();
	Display display;
	
	/**
	 * Create addition window to have possibility terminate launched processes
	 * @param launchedConfigs map contains list of LC which could be terminated
	 */
	public void createWindow(List<ILaunch> launchedConfigs){
		display = getCurrentDisplay();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.addListener(SWT.Close, new Listener() 
        { 
           @Override 
           public void handleEvent(Event event) 
           { 
              shell.dispose();
              if(Arrays.asList(display.getShells()).isEmpty()){
                  display.dispose();
              }
           } 
        }); 
		Group mainGroup = new Group(shell, SWT.BORDER | SWT.H_SCROLL);
		mainGroup.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        mainGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        mainGroup.setLayout(new GridLayout(1, false));
        mainGroup.setText("Stop launched configurations");
        
        for(final ILaunch lcEntity: launchedConfigs){
            final Button button = new Button(mainGroup, SWT.PUSH);
            button.setText(lcEntity.getLaunchConfiguration().getName() + " TERMINATE");
            button.addSelectionListener(new SelectionAdapter() {
            	@Override
                public void widgetSelected(final SelectionEvent event) {
        			try {
        				lcEntity.terminate();
						button.setEnabled(false);
					} catch (DebugException de) {
						debug.logError("Could not terminated LC: '" +lcEntity.getLaunchConfiguration().getName()+"'.", de);
						throw new RuntimeException(de);
					};
            	}
            	
			});
        }
        
		shell.setSize (400, 300);
		shell.open();
	}

	/**
	 * 
	 * @return current SWT display 
	 */
	private Display getCurrentDisplay() {
		Display d = Display.getCurrent();
		if(d == null){
			d = new Display();
		}
		return d;
	}
	
	/**
	 * 
	 * @return return SWT display which hold terminate window 
	 */
	public Display getDisplay() {
		return display;
	}
	
	/**
	 * 
	 * @param shells array of SWT shell
	 * @return true if which of shell is disposed, false otherwise
	 */
	public boolean allShellIsDisposed(Shell[] shells){
		for (Shell shell : shells) {
			if(!shell.isDisposed()){
				return false;
			}
		}
		return true;
	}
	
}
