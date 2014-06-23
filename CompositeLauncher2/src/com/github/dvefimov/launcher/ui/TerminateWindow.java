package com.github.dvefimov.launcher.ui;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;

public class TerminateWindow {

	CompositeLauncherLogger debug = new CompositeLauncherLogger();

	/**
	 * Create addition window to have possibility terminate launched processes
	 * @param launchedConfigs map contains list of LC which could be terminated
	 */
	public void createWindow(List<ILaunch> launchedConfigs){
		Display display = new Display();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());
		
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
		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

}
