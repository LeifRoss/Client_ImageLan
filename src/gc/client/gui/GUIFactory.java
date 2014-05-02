package gc.client.gui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * GUIFactory class.
 * Contains static methods to create GCViews and GCComponents
 * @author Leif Andreas Rudlang
 */
public class GUIFactory {

	
	//// GCViews ////////
	
	/**
	 * Create GCView frame
	 * @param id
	 * @param name
	 * @return
	 */
	public static GCView frame(String id, String name){
		return frame(id,name,false);
	}

	/**
	 * Create a GCView frame
	 * @param id
	 * @param name
	 * @param extended
	 * @return
	 */
	public static GCView frame(String id, String name, boolean extended){

		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	//	if(GUIHandler.getIcon()!=null){
	//		frame.setIconImage(GUIHandler.getIcon());
	//	}

		if(extended){
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		}

		GCView view = new GCView(id, frame);

		return view;
	}

	
	/**
	 * Create a GCView splitpane
	 * @param id
	 * @return
	 */
	public static GCView splitpane(String id){
		return splitpane(id, 0.5, false);
	}
	
	
	/**
	 * Create a GCView splitpane
	 * @param id
	 * @param split
	 * @return
	 */
	public static GCView splitpane(String id, boolean split){
		return splitpane(id, 0.5, split);
	}
	
	
	/**
	 * Create a GCView splitpane
	 * @param id
	 * @param pos
	 * @param split
	 * @return
	 */
	public static GCView splitpane(String id, double pos, boolean split){

		int dir = split ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;
		
		JSplitPane pane = new JSplitPane(dir);
	
		pane.setOneTouchExpandable(true);
		pane.setResizeWeight(pos); 

		GCView view = new GCView(id, pane);

		return view;
	}

	
	/**
	 * Create a GCView scrollpane
	 * @param id
	 * @param v
	 * @return
	 */
	public static GCView scrollpane(String id, GCView v){

		GCView view = new GCView(id, new JScrollPane(v.getContainer()));

		return view;
	}
	
	
	/**
	 * Create a GCView scrollpane
	 * @param id
	 * @return
	 */
	public static GCView scrollpane(String id){

		GCView view = new GCView(id, new JScrollPane());

		return view;
	}

	
	/**
	 * Create a GCView menubar
	 * @param id
	 * @param name
	 * @return
	 */
	public static GCView menubar(String id, String name){

		GCView view = new GCView(id, new JMenuBar());

		return view;
	}

	
	/**
	 * Create a GCView menu
	 * @param id
	 * @param name
	 * @return
	 */
	public static GCView menu(String id, String name){

		GCView view = new GCView(id, new JMenu(name));

		return view;
	}

	
	/**
	 * Create a GCView view (panel)
	 * @param id
	 * @return
	 */
	public static GCView view(String id){

		GCView view = new GCView(id, new JPanel());
		view.getContainer().setVisible(true);

		return view;
	}



	//// GCComponents //////

	
	/**
	 * Create a GCComponent menuitem
	 * @param id
	 * @param name
	 * @param command
	 * @return
	 */
	public static GCComponent menuitem(String id, String name, String command){

		JMenuItem item = new JMenuItem(name);
		item.setActionCommand(command);
		item.addActionListener(GUIHandler.getActionHandler());		
		GCComponent comp = new GCComponent(id, item);

		return comp;
	}

	
	/**
	 * Create a GCComponent button
	 * @param id
	 * @param name
	 * @param command
	 * @return
	 */
	public static GCComponent button(String id, String name, String command){

		JButton button = new JButton(name);
		button.setActionCommand(command);
		button.addActionListener(GUIHandler.getActionHandler());
		GCComponent comp = new GCComponent(id, button);

		return comp;
	}


	/**
	 * Create a GCCompoent textfield
	 * @param id
	 * @return
	 */
	public static GCComponent textfield(String id){
	
		JTextField text = new JTextField(20);
		GCComponent comp = new GCComponent(id, text);

		return comp;
	}


	/**
	 * Create a GCComponent textfield
	 * @param id
	 * @param command
	 * @return
	 */
	public static GCComponent textfield(String id, String command){

		JTextField text = new JTextField(20);
		text.setActionCommand(command);
		text.addActionListener(GUIHandler.getActionHandler());
		GCComponent comp = new GCComponent(id, text);

		return comp;	
	}

	
	/**
	 * Create a GCComponent textarea
	 * @param id
	 * @return
	 */
	public static GCComponent textarea(String id){

		JTextArea text = new JTextArea();
		GCComponent comp = new GCComponent(id, text);

		return comp;	
	}


	/**
	 * Create a GCComponent label
	 * @param id
	 * @param name
	 * @return
	 */
	public static GCComponent label(String id, String name){

		JLabel label = new JLabel(name);
		GCComponent comp = new GCComponent(id, label);

		return comp;	
	}

	
	/**
	 * Create a GCComponent combobox
	 * @param id
	 * @param values
	 * @return
	 */
	public static GCComponent combobox(String id, String... values){
			
		JComboBox<String> box = new JComboBox<String>(values);				
		GCComponent comp = new GCComponent(id, box);

		return comp;
	}

	
	/**
	 * Create a GCComponent checkbox
	 * @param id
	 * @param name
	 * @return
	 */
	public static GCComponent checkbox(String id, String name){
		
		JCheckBox box = new JCheckBox(name);				
		GCComponent comp = new GCComponent(id, box);

		return comp;
	}
	
}
