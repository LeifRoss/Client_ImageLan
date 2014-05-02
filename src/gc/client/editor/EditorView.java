package gc.client.editor;


import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.gui.GUIFactory;
import gc.client.gui.views.ImageDisplay;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Editor GCView
 * @author Leif Andreas
 *
 */
public class EditorView extends GCView implements ActionListener{


	public static final String ID = "views.imageview";

	private final static String ACTION_SAVE = "Save";
	private final static String ACTION_SAVE_AS = "Save as";

	private final static String TITLE_TOOLS = "Tools";
	private final static String TITLE_PAINT = "Paint";
	private final static String TITLE_FILTERS = "Filter";


	private ImageDisplay display;

	private ToolPanel tool_panel;
	private PaintPanel paint_panel;
	private FilterPanel filter_panel;
	private String image_id, path;


	public EditorView() {
		super(ID, new JPanel());
	}

	@Override
	public void setup(){

		this.setLayout(new BorderLayout());	
		this.setColor(10, 10, 10);

		JButton button = null;

		GCView panel = GUIFactory.view("views.imageview.tmp");
		panel.setLayout(new BorderLayout());

		display = new ImageDisplay(true);


		// save etc
		JPanel top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		button = new JButton(ACTION_SAVE);
		button.addActionListener(this);
		top_panel.add(button);

		button = new JButton(ACTION_SAVE_AS);
		button.addActionListener(this);
		top_panel.add(button);


		tool_panel = new ToolPanel(display);
		paint_panel = new PaintPanel(display);
		filter_panel = new FilterPanel(display);

		// tabs
		JTabbedPane tabs_tmp = new JTabbedPane();

		tabs_tmp.add(TITLE_TOOLS, tool_panel);
		tabs_tmp.add(TITLE_PAINT, paint_panel);
		tabs_tmp.add(TITLE_FILTERS, filter_panel);

		GCView tabs = new GCView("views.imageview.tabs",tabs_tmp);
		GCView top_panel_view = new GCView("views.editorview.top",top_panel);

		panel.add(top_panel_view,BorderLayout.NORTH);
		panel.add(tabs, BorderLayout.CENTER);


		GCView splitpane = GUIFactory.splitpane("views.imageview.split", 0.85, false);

		splitpane.add(new GCView("views.imageivew.display",display));
		splitpane.add(panel);

		this.add(splitpane, BorderLayout.CENTER);		
	}


	/**
	 * Save a copy of the image
	 */
	private void save_as(){	

		if(path == null || image_id == null){
			return;
		}

		final String name = JOptionPane.showInputDialog(display, "Image name");

		if(name.isEmpty() || name.length() < 5){
			JOptionPane.showMessageDialog(display, "Enter a valid Image name!");
			return;
		}


		GCEvent event = new GCEvent(){

			@Override
			public void event() {


				String cache_path = Util.getAssetsLocation()+"cache//"+path;
				Util.saveImage(cache_path, (BufferedImage)display.getImage());				

				if(MainFrame.getServer().uploadImage(cache_path, name, "0","")){
					new File(cache_path).delete();
				}else{
					JOptionPane.showMessageDialog(display, "Update failed, the image is still saved in the cache");
				}

			}		
		};		
		MainFrame.add(event);

	}


	/**
	 * Save the image
	 */
	private void save(){	

		if(path == null || image_id == null){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {


				String cache_path = Util.getAssetsLocation()+"cache//"+path;
				Util.saveImage(cache_path, (BufferedImage)display.getImage());				

				if(MainFrame.getServer().updateImage(image_id, cache_path)){
					new File(cache_path).delete();
				}else{
					JOptionPane.showMessageDialog(display, "Update failed, the image is still saved in the cache");
				}

			}		
		};		
		MainFrame.add(event);

	}

	@Override
	public void load(String... in){


		if(in.length > 1){
			path = in[0];
			image_id = in[1];
		}else{
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {

				Image img = MainFrame.getServer().downloadImageEditor(path);

				if(img == null){
					return;
				}

				display.setImage(img);	
			}
		};

		MainFrame.add(event);
	}




	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){
		case ACTION_SAVE:
			save();
			break;
		case ACTION_SAVE_AS:
			save_as();
			break;

		}

	}


}
