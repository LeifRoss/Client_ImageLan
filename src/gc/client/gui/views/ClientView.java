package gc.client.gui.views;

import gc.client.com.Main;
import gc.client.com.MainFrame;
import gc.client.editor.EditorView;
import gc.client.gui.GCView;
import gc.client.gui.GUIFactory;
import gc.client.gui.GUIHandler;
import gc.client.util.CommandRouter;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import javax.swing.border.BevelBorder;

/**
 * ClientView class
 * This is the Application main entry point after login.
 * This is also contains the window, tabpane and menubar.
 * @author Leif Andreas Rudlang
 *
 */
public class ClientView {

	private static final String PARAM_ARGS = ";";
	private final Icon CLOSE_TAB_ICON_OVER;
	private final Icon CLOSE_TAB_ICON;
	public  final Icon PAGE_ICON;
	private final ImageIcon APP_ICON;

	private MenuBarView menubar;
	private GCView panel;
	private GCView window;

	private ArrayList<GCView> forms;

	private CommandRouter router;
	private CommandRouter open, set, close;

	private JTabbedPane tabs;

	/**
	 * ClientView
	 */
	public ClientView(){

		String location = Util.getAssetsLocation()+"graphics//";

		CLOSE_TAB_ICON_OVER = new ImageIcon(location+"close_over.png");
		CLOSE_TAB_ICON = new ImageIcon(location+"close_reg.png");
		PAGE_ICON = new ImageIcon(location+"edit.png");
		APP_ICON = new ImageIcon(location+"app_icon.png");
		
		setup();
	}


	private void setup(){

		buildRouter();

		forms = new ArrayList<GCView>();
		tabs = new JTabbedPane();

		GCView tabs_view = new GCView("views.window.tabs",tabs);
		tabs.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		window = GUIFactory.frame("views.window", Main.APPLICATION_NAME, true);
		window.setMinSize(640, 400);
		
		JFrame frame = (JFrame)window.getContainer();
		frame.setIconImage(APP_ICON.getImage());
		
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				MainFrame.shutdown();
			}
		});
	
		panel = GUIFactory.view("views.window.panel");
		panel.setLayout(new BorderLayout());
		GUIHandler.add(panel);

		// menu bar
		menubar = new MenuBarView();
		panel.add(tabs_view,BorderLayout.CENTER);
		window.add(panel);

		MainFrame.setClientView(this);
		openView("library");
	}


	/**
	 * Sets the current tab to a new view
	 * @param in
	 */
	public void setView(String in){


		String[] parameters = in.split(PARAM_ARGS);

		String name = parameters[0];

		String[] args = null;	

		if(parameters.length > 1){

			args = new String[parameters.length-1];	

			for(int i = 1; i < parameters.length; i++){
				args[i-1] = parameters[i];
			}			
		}


		GCView view = createView(name);

		if(view==null){
			return;
		}

		int tabIndex = tabs.indexOfComponent(tabs.getSelectedComponent());

		if(tabIndex >= forms.size() || tabIndex < 0){
			return;
		}
		
		JScrollPane scrollpane = (JScrollPane)forms.get(tabIndex).getContainer();
		scrollpane.getViewport().removeAll();
		scrollpane.getViewport().add(view.getContainer());

		view.load(args);
	}


	/**
	 * Creates a new tab with the new view
	 * @param in
	 */
	public void openView(String in){

		String[] parameters = in.split(PARAM_ARGS);

		String name = parameters[0];

		String[] args = null;	

		if(parameters.length > 1){

			args = new String[parameters.length-1];	

			for(int i = 1; i < parameters.length; i++){
				args[i-1] = parameters[i];
			}			
		}

		GCView view = createView(name);

		if(view==null){
			return;
		}

		view.load(args);
		addTab(view,name);

	}



	/**
	 * Add a view to a new tab
	 * @param view
	 * @param title
	 */
	public void addTab(GCView view, String title){


		GCView scrollpane = GUIFactory.scrollpane("views.window.tab",view);
		forms.add(scrollpane);
		addTab((JScrollPane)scrollpane.getContainer(),title,PAGE_ICON);				
	}


	/**
	 * Add a new tab with a JComponent
	 * @param c
	 * @param title
	 * @param icon
	 */
	public void addTab(final JComponent c, final String title, final Icon icon) {

		// Add the tab to the pane without any label
		tabs.addTab(null, c);
		int pos = tabs.indexOfComponent(c);

		// Create a FlowLayout that will space things 5px apart
		FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel(f);
		pnlTab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		lblTitle.setIcon(icon);

		// Create a JButton for the close tab button
		JButton btnClose = new JButton();
		btnClose.setOpaque(false);

		// Configure icon and rollover icon for button
		btnClose.setRolloverIcon(CLOSE_TAB_ICON_OVER);
		btnClose.setRolloverEnabled(true);
		btnClose.setIcon(CLOSE_TAB_ICON);

		// Set border null so the button doesn't make the tab too big
		btnClose.setBorder(null);

		// Make sure the button can't get focus, otherwise it looks funny
		btnClose.setFocusable(false);

		// Put the panel together
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		pnlTab.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

		// Now assign the component for the tab
		tabs.setTabComponentAt(pos, pnlTab);

		// Add the listener that removes the tab
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				forms.get(tabs.indexOfComponent(c)).close();
				forms.remove(tabs.indexOfComponent(c));
				tabs.remove(c);						
				System.gc();
			}
		};
		btnClose.addActionListener(listener);

		// Optionally bring the new tab to the front
		tabs.setSelectedComponent(c);
	}

	/**
	 * Closes the open view
	 */
	public void closeView(){

		int index = tabs.getSelectedIndex();

		try{

			forms.get(index).close();
			forms.remove(index);
			tabs.remove(index);
			System.gc();

		}catch(Exception e){
			MainFrame.error(e.getMessage());
		}
	}

	/**
	 * Creates a view with the given name
	 * @param in
	 * @return
	 */
	public GCView createView(String in){

		switch(in){

		case "image":
			return new ImageView();
		case "library":
			return new LibraryView();
		case "upload":
			return new UploadView();
		case "editor":
			return new EditorView();
		}		

		return null;
	}


	private void buildRouter(){

		router = new CommandRouter("window",true);

		open = new CommandRouter("open",true);
		open.setFunction(new Runnable(){

			@Override
			public void run() {

				String view = open.getParameter();			
				openView(view);

			}

		});
		router.addRouter(open);


		set = new CommandRouter("set",true);
		set.setFunction(new Runnable(){

			@Override
			public void run() {

				String view = set.getParameter();			
				setView(view);
			}

		});
		router.addRouter(set);

		close = new CommandRouter("close",true);
		close.setFunction(new Runnable(){

			@Override
			public void run() {
				closeView();
			}

		});
		router.addRouter(close);


		MainFrame.add(router);		
	}

}
