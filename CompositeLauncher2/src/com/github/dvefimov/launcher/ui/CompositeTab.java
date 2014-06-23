package com.github.dvefimov.launcher.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.github.dvefimov.launcher.debug.CompositeLauncherLogger;
import com.github.dvefimov.launcher.util.CompositeTreeLabelProvider;
import com.github.dvefimov.launcher.util.CompositeTreeContentProvider;
import com.github.dvefimov.launcher.util.Node;

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Daniil Efimov
 * 
 */

@SuppressWarnings("restriction")
public class CompositeTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {

	private List<String> allLaunchConfigurationName; // existing launch config (LC) in
													 // current system
	private Map<String, List<ILaunchConfiguration>> availableLaunchConfiguration; // existing launch
													// config (LC) in current system for tree gui presentation
	private Map<String, Image> availableLaunchConfigurationImage; // existing LC image
													// current system
	private List<ILaunchConfiguration> selectedLaunchConfiguration = new ArrayList<>(); // selected
													// LC in current system
	private TableViewer uiSelectedList; // selected LC ui presentation

	private Tree tree; // to refresh size
	private Table table;
	private Composite pComposite;
	
	private TreeViewer viewer;

	public static final String COMPOSITE_TYPE_NAME = "Composite Configuration 2"; // type of my launch configuration
													// - look at plugin.xml
	public static final String SELECTED_LAUNCHES = "SelectedLaunches"; // name of attribute of
													// selected LC

	private String current_configuration_name = "";
	CompositeLauncherLogger debug = new CompositeLauncherLogger();

	/**
	 * Create main tab for gui of Composite Launch Configuration
	 * 
	 * @throws CoreException
	 *             if an exception occurs retrieving configurations
	 */
	public CompositeTab() throws CoreException {
		fillConfiguration();
	}

	/**
	 * Get available configuration from system and put to application 
	 * @throws CoreException
	 */
	private void fillConfiguration() throws CoreException {
		availableLaunchConfiguration = new HashMap<>();
		allLaunchConfigurationName = new ArrayList<>();
		availableLaunchConfigurationImage = new HashMap<>();
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		List<ILaunchConfiguration> list;
		ImageRegistry imageRegistry = DebugPluginImages.getImageRegistry();
		for (ILaunchConfiguration conf : manager.getLaunchConfigurations()) {

			String key = conf.getType().getName();
			Image image = imageRegistry.get(conf.getType().getIdentifier());

			list = availableLaunchConfiguration.get(key);
			if (list == null) {
				list = new ArrayList<>();
				availableLaunchConfiguration.put(key, list);
				availableLaunchConfigurationImage.put(key, image); // set icon
																	// for type
			}
			list.add(conf);

			String uiConfName = getUIPresentationLC(key, conf.getName());
			allLaunchConfigurationName.add(uiConfName);
			availableLaunchConfigurationImage.put(conf.getName(), image); // set icon
																			// for configuration
			availableLaunchConfigurationImage.put(uiConfName, image); // set icon
																		// for selected configuration
		}
	}

	private void reconstractTree() throws CoreException{
		viewer.setInput(new Object[0]);
		fillConfiguration();
		viewer.setLabelProvider(new CompositeTreeLabelProvider(
				availableLaunchConfigurationImage));
		List<Node> nodes = createTreeViewerNodes(availableLaunchConfiguration);
		viewer.setInput(nodes);
	}
	
	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 *      Create gui control for composite tab
	 */
	@Override
	public void createControl(Composite parent) {
		pComposite = parent;
		pComposite.layout();

		Composite topControl = new Composite(parent, SWT.NONE | SWT.NO_SCROLL);
		topControl.setLayout(new GridLayout(3, false));

		Group groupAL = new Group(topControl, SWT.H_SCROLL | SWT.V_SCROLL);
		groupAL.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupAL.setLayout(new GridLayout(1, false));
		groupAL.setText("Available launchers");

		PatternFilter filter = new PatternFilter();
		FilteredTree filteredTree = new FilteredTree(groupAL, SWT.SINGLE
				| SWT.V_SCROLL | SWT.FULL_SELECTION, filter, true);
		viewer = filteredTree.getViewer();
		viewer.setLabelProvider(new CompositeTreeLabelProvider(
				availableLaunchConfigurationImage));
		viewer.setContentProvider(new CompositeTreeContentProvider());

		List<Node> nodes = createTreeViewerNodes(availableLaunchConfiguration);
		viewer.setInput(nodes);
		viewer.setExpandedElements(nodes.toArray());

		tree = viewer.getTree();
		tree.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				TreeItem item = tree.getSelection()[0];
				if (item.getParentItem() == null) {  // we should not have possibility select category
					tree.deselectAll();
				}
			}
		});
		Group buttonGroup = new Group(topControl, SWT.NONE);
		buttonGroup.setLayout(new FillLayout(SWT.VERTICAL));
		Button addButton = new Button(buttonGroup, SWT.PUSH);
		addButton.setText(">");
		Button removeButton = new Button(buttonGroup, SWT.PUSH);
		removeButton.setText("<");
		GridData zeroSizeData = new GridData(SWT.TOP, SWT.LEFT, false, false);
		zeroSizeData.widthHint = 0;
		zeroSizeData.heightHint = 0;
		tree.setLayoutData(zeroSizeData); // tree will be redraw later

		Group groupSL = new Group(topControl, SWT.NONE | SWT.V_SCROLL
				| SWT.H_SCROLL);
		groupSL.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		groupSL.setLayout(new GridLayout(1, false));
		groupSL.setText("Selected launchers new");
		uiSelectedList = new TableViewer(groupSL, SWT.NONE);

		uiSelectedList.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Color getForeground(Object element) {
				String s = element.toString();
				try {
					if (!allLaunchConfigurationName.contains(s)
							|| isCycledComposite(s) || current_configuration_name.equals(getConfOrinalName(s)))
						return Display.getCurrent().getSystemColor(
								SWT.COLOR_RED);
					else
						return Display.getCurrent().getSystemColor(
								SWT.COLOR_BLACK);
				} catch (CoreException ce) {
					debug.logError(
							"Could not set label provider for selected LC", ce);
					throw new RuntimeException(ce);
				}
			}

			@Override
			public Image getImage(Object element) {
				String key = element.toString();
				Image im = availableLaunchConfigurationImage.get(key);
				return im;
			}
		});
		uiSelectedList.setContentProvider(new ArrayContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				try {
					return getLaunchConfigNames(selectedLaunchConfiguration)
							.toArray();
				} catch (CoreException ce) {
					debug.logError(
							"Could not set content provider for selected LC",
							ce);
					throw new RuntimeException(ce);
				}
			}

		});
		table = uiSelectedList.getTable();
		table.setLayoutData(zeroSizeData); // table will
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		uiSelectedList.getControl().setLayoutData(data);

		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				TreeItem treeItem = tree.getSelection()[0];
				String confName = treeItem.getText();
				String confTypeName = treeItem.getParentItem().getText();
				ILaunchConfiguration launchConfiguration = getILaunchConfigurationFromTree(
						confTypeName, confName);
				if (launchConfiguration != null) {
					selectedLaunchConfiguration.add(launchConfiguration);
					uiSelectedList.refresh(true);
				}
				uiSelectedList.setInput(new Object());
				performApplyButton();
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				int num = table.getSelectionIndex();
				if (num > -1) {
					selectedLaunchConfiguration.remove(num);
				}
				uiSelectedList.setInput(new Object());
				performApplyButton();
			}
		});

		setControl(topControl);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 * 
	 *      Initialize from existing launch configuration for example look into
	 *      \.metadata\.plugins\org.eclipse.debug.core\.launches dir in your
	 *      file system to find list of LC
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		resizeTree();

		current_configuration_name = configuration.getName();
		List<?> mementos;
		try {
			mementos = (List<?>) configuration.getAttribute(
					CompositeTab.SELECTED_LAUNCHES, new ArrayList<Object>());
			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			selectedLaunchConfiguration.removeAll(selectedLaunchConfiguration);
			for (Object memento : mementos) {
				if (memento instanceof String) {
					String mString = (String) memento;
					ILaunchConfiguration conf = manager
							.getLaunchConfiguration(mString);
					selectedLaunchConfiguration.add(conf);
				}
			}
			uiSelectedList.setInput(new Object());
		} catch (CoreException e) {
			debug.logError("Could not initialize from. check LC file.", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 * 
	 *      Add list of selected LC as attribute to composite configuration
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		try {
			reconstractTree();  // rename current conf feature
		} catch (CoreException e) {
			debug.logError("Could not reconstract tree LC.",
					e);
			throw new RuntimeException(e);
		}

		List<String> mementos = new ArrayList<>();
		try {
			for (ILaunchConfiguration conf : selectedLaunchConfiguration) {
				mementos.add(conf.getMemento());
			}
		} catch (CoreException e) {
			debug.logError("Could not get memento during save configuration.",
					e);
			throw new RuntimeException(e);
		}

		configuration.setAttribute(SELECTED_LAUNCHES, mementos);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName() return name
	 *      composite tab configuration
	 */
	@Override
	public String getName() {
		return "Composite tab";
	}

	/**
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 *      Return is configuration correct for launch
	 */
	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		// allow launch when a file is selected and file exists
		try {
			boolean isCycle = checkHasLoopByThisConf(launchConfig);
			if (isCycle) {
				return false;
			}
			ILaunchConfiguration lConf = ((ILaunchConfigurationWorkingCopy) launchConfig)
					.getOriginal();
			if (launchConfig instanceof ILaunchConfigurationWorkingCopy) {
				isCycle = checkHasLoopByThisConf(lConf);
				if (isCycle) {
					return false;
				}
			}

			if (selectedLaunchConfiguration.isEmpty()) {
				return false;
			} else {
				for (ILaunchConfiguration conf : selectedLaunchConfiguration) {
					if (!conf.exists()) {
						return false;
					}
				}
			}
		} catch (CoreException e) {
			debug.logError("isValid fails.", e);
			throw new RuntimeException(e);
		}

		return true;
	}

	/**
	 * 
	 * @param launchConfig
	 *            LC which should be tested
	 * @return True if LC has loop for itself, false otherwise
	 * @throws CoreException
	 *             some problem with ILaunchManager or trouble with one of LC
	 */
	private boolean checkHasLoopByThisConf(ILaunchConfiguration launchConfig)
			throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		Map<?, ?> attributes = (Map<?, ?>) launchConfig.getAttributes();

		for (Object key : attributes.keySet()) {
			if (key instanceof String
					&& "SelectedLaunches".equals((String) key)) {
				Object val = attributes.get(key);
				if (val instanceof List<?>) {
					List<?> list = (List<?>) val;
					for (Object obj : list) {
						if (obj instanceof String) {
							String memento = (String) obj;
							ILaunchConfiguration conf = manager
									.getLaunchConfiguration(memento);
							if (conf.exists()
									&& COMPOSITE_TYPE_NAME.equals(conf
											.getType().getName())) {
								boolean isEquals = conf.getName().equals(
										current_configuration_name);
								if (isEquals) {
									return true;
								} else {
									return checkHasLoopByThisConf(conf);
								}
							}
						}

					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param configName Name LC in our format
	 * @return true if LC has reference on current LC, otherwise false.
	 */
	private boolean isCycledComposite(String configName) throws CoreException {
		int start = configName.indexOf("(") + 1;
		int end = configName.indexOf(") ");
		if (start > 0 && end > 0) {
			String name = configName.substring(end + 2);
			String type = configName.substring(start, end);
			ILaunchConfiguration conf = getILaunchConfigurationFromTree(type,
					name);
			return checkHasLoopByThisConf(conf);
		}
		return false;
	}

	/**
	 * @param name
	 *            LC name
	 * @param type
	 *            LC type
	 * @return Name of LC for ui presentation <br/>
	 *         format of LC name is '(type) name'. <br/>
	 *         so DONT use type which name contains bracket!
	 */
	private String getUIPresentationLC(String type, String name) {
		return "(" + type + ") " + name;
	}

	/**
	 * @param input
	 *            list of LC which need to be presented
	 * @return Name information about each of LC from list
	 * @throws CoreException
	 *             could not get type one of LC
	 */
	private List<String> getLaunchConfigNames(List<ILaunchConfiguration> input)
			throws CoreException {
		List<String> res = new ArrayList<>();
		for (ILaunchConfiguration conf : input) {
			String type = "Type this launch configuration could not be found!";
			if (conf.exists()) {
				type = conf.getType().getName();
			}
			res.add(getUIPresentationLC(type, conf.getName()));
		}
		return res;
	}

	/**
	 * Update tab information.
	 */
	private void performApplyButton() {
		scheduleUpdateJob();
	}

	/**
	 * Create tree launch configuration
	 * 
	 * @param map
	 *            map element where key is type and value is list of LC
	 * @return linked structure of Node
	 */
	private List<Node> createTreeViewerNodes(
			Map<String, List<ILaunchConfiguration>> map) {
		List<Node> nodes = new ArrayList<>();
		for (Entry<String, List<ILaunchConfiguration>> entry : map.entrySet()) {
			Node type = new Node(entry.getKey(), null);
			for (ILaunchConfiguration conf : entry.getValue()) {
				new Node(conf.getName(), type);
			}
			nodes.add(type);
		}
		return nodes;
	}

	/**
	 * Returns the launchConfiguration from gui presentation of available launch
	 * configurations or null when available launch configurations do not
	 * contains typeName or confName.
	 * 
	 * @param typeName
	 *            name of ILaunchConfigurationType
	 * @param confName
	 *            name of ILaunchConfiguration
	 * @return launchConfiguration from gui launch configuration tree
	 */
	private ILaunchConfiguration getILaunchConfigurationFromTree(
			String typeName, String confName) {
		for (Entry<String, List<ILaunchConfiguration>> entry : availableLaunchConfiguration
				.entrySet()) {
			if (entry.getKey().equals(typeName)) {
				for (ILaunchConfiguration conf : entry.getValue()) {
					if (conf.getName().equals(confName)) {
						return conf;
					}
				}
			}
		}
		return null;
	}

	/**
	 * This method fit size available LC tree and selected LC
	 */
	private void resizeTree() {
		Composite groupTree = tree.getParent();
		int width = groupTree.getSize().x;
		Point size = pComposite.getSize();
		int height = (int) (0.8 * size.y);

		GridData treeData = new GridData(SWT.TOP, SWT.LEFT, false, false);
		treeData.widthHint = width - 7;
		treeData.heightHint = height;
		tree.setLayoutData(treeData);

		GridData tableData = new GridData(SWT.TOP, SWT.LEFT, false, false);
		tableData.widthHint = width - 7;
		tableData.heightHint = height + 15;
		table.setLayoutData(treeData);
	}


	/**
	 * 
	 * @param fullName
	 * @return original LC name
	 */
	private String getConfOrinalName(String fullName) {
		int start = fullName.indexOf("(") + 1;
		int end = fullName.indexOf(") ");
		if(start < 0 || end < 0){
			return fullName; // could not parse this string;
		}
		return fullName.substring(end + 2);
	}
	

}
