package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.EventQueueListener;
import gc.client.gui.GCComponent;
import gc.client.gui.GCView;
import gc.client.gui.GUIFactory;
import gc.client.gui.GUIHandler;
import gc.client.util.CommandRouter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JProgressBar;

/**
 * MenuBarView class
 * MenuBar for the ClientView
 * @author Leif Andreas Rudlang
 */
public class MenuBarView {

	private GCView panel;
	private CommandRouter router;
	private JProgressBar progress_bar;


	/**
	 * MenuBarView
	 */
	public MenuBarView(){

		setup();
	}

	/**
	 * Setup the GUI
	 */
	private void setup(){

		panel = GUIFactory.view("views.menubar");
		GCView grid = GUIFactory.view("views.menubar.grid");
		GCView tmp = GUIFactory.view("views.menubar.tmp");

		panel.setLayout(new BorderLayout());
		grid.setLayout(new GridLayout(2,1));
		tmp.setLayout(new FlowLayout(FlowLayout.RIGHT));

		progress_bar = new JProgressBar();
		GCComponent bar = new GCComponent("elements.menubar.progressbar",progress_bar);
		tmp.add(bar);

		GCComponent search = GUIFactory.textfield("elements.menubar.search", "window open \"library;search\"");
		GUIHandler.add(search);
		tmp.add(search);

		GCComponent library = GUIFactory.button("elements.menubar.library", "Library", "window open \"library\"");
		tmp.add(library);

		GCComponent upload = GUIFactory.button("elements.menubar.upload", "Upload", "window open \"upload\"");
		tmp.add(upload);

		GCComponent combobox = GUIFactory.combobox("elements.menubar.combobox", "Settings","My Profile","Log Out");
		tmp.add(combobox);


		grid.add(GUIFactory.label("component.menubar.labeltmp", ""));
		grid.add(tmp);
		panel.add(grid,BorderLayout.EAST);


		if(GUIHandler.getViewById("views.window.panel")!=null){
			GUIHandler.getViewById("views.window.panel").add(panel,BorderLayout.NORTH);
			GUIHandler.add(panel);
		}

		buildRouter();


		EventQueueListener listener = new EventQueueListener(){

			@Override
			public void startedWorking() {
				progress_bar.setVisible(true);
			}

			@Override
			public void stoppedWorking() {
				progress_bar.setVisible(false);
			}

			@Override
			public void update(int total, int left) {
				progress_bar.setMaximum(total);
				progress_bar.setValue(total-left);
			}

		};

		MainFrame.getEventQueue().setListener(listener);

	}


	/**
	 * Build the CommandRouter
	 */
	private void buildRouter(){

		router = new CommandRouter("menubar",true);	

		MainFrame.add(router);
	}


	/**
	 * Destroy this view
	 */
	public void destroy(){

		MainFrame.remove(router);
	}






}
