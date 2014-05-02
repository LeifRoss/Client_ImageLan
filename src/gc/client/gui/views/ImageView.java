package gc.client.gui.views;


import gc.client.com.MainFrame;
import gc.client.editor.EditorView;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCComponent;
import gc.client.gui.GCView;
import gc.client.gui.GUIFactory;
import gc.client.gui.IconButton;
import gc.client.util.Library;
import gc.client.util.MetadataLibrary;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;


/**
 * ImageView GCView
 * @author Leif Andreas
 */
public class ImageView extends GCView implements ActionListener, KeyListener{


	public static final String ID = "views.imageview";

	private final static String ACTION_DELETE = "Delete";
	private final static String ACTION_EDIT = "Edit";

	private final static String TITLE_INFO = "Information";
	private final static String TITLE_COMMENTS = "Comments";
	private final static String TITLE_TAGS = "Metadata";

	private final static ImageIcon ICON_DELETE_REG = new ImageIcon(Util.getAssetsLocation()+"graphics/icon_delete_reg.png");
	private final static ImageIcon ICON_DELETE_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics/icon_delete_over.png");
	private final static ImageIcon ICON_EDIT_REG = new ImageIcon(Util.getAssetsLocation()+"graphics/icon_edit_reg.png");
	private final static ImageIcon ICON_EDIT_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics/icon_edit_over.png");

	
	private ImageDisplay display;
	private MetadataLibrary metadata;

	private InfoView info;
	private GCView tags, comments, top_panel;
	private String image_id, path;

	private StarRater rating;

	private Library library;
	private int index;

	/**
	 * ImageView
	 */
	public ImageView() {
		super(ID, new JPanel());
	}

	/**
	 * ImageView
	 * @param library
	 * @param index
	 */
	public ImageView(Library library, int index) {
		super(ID, new JPanel());

		this.library = library;
		this.index = index;
	}

	@Override
	public void setup(){

		this.setLayout(new BorderLayout());	
		this.setColor(10, 10, 10);

		display = new ImageDisplay();
		display.addKeyListener(this);

		metadata = new MetadataLibrary();

		GCView panel = GUIFactory.view("views.imageview.tmp");
		panel.setLayout(new BorderLayout());

		// rating etc
		top_panel = GUIFactory.view("views.imageview.tmp");
		top_panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		rating = new StarRater(metadata);

		top_panel.add(new GCView("views.imageview.rating",rating));

		panel.add(top_panel, BorderLayout.NORTH);


		// tabs
		JTabbedPane tabs_tmp = new JTabbedPane();

		info = new InfoView();
		tags = new MetadataView(metadata);
		comments = new CommentView();

		tabs_tmp.add(TITLE_INFO, info.getContainer());
		tabs_tmp.add(TITLE_TAGS, tags.getContainer());
		tabs_tmp.add(TITLE_COMMENTS, comments.getContainer());

		GCView tabs = new GCView("views.imageview.tabs",tabs_tmp);

		panel.add(tabs, BorderLayout.CENTER);


		GCView splitpane = GUIFactory.splitpane("views.imageview.split", 0.85, false);

		splitpane.add(new GCView("views.imageivew.display",display));
		splitpane.add(panel);


		this.add(splitpane, BorderLayout.CENTER);
	}

	/**
	 * Delete the image
	 */
	private void delete(){

		int check = JOptionPane.showConfirmDialog(top_panel.getContainer(), "Are you sure you want to delete this image?");

		if(check != JOptionPane.OK_OPTION){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().deleteImage(image_id);
			}		
		};
		MainFrame.add(event);
		MainFrame.execute("window close");

	}

	/**
	 * Load this view with the EventQueue
	 */
	private void loadImageAsync(){


		GCEvent event = new GCEvent(){

			@Override
			public void event() {


				display.clear();

				Image img = MainFrame.getServer().downloadImage(path);

				if(img == null){
					return;
				}

				display.setImage(img);	
				display.requestFocus();
				display.requestFocusInWindow();
				info.setIndex(index);
				
				metadata.load(image_id);
				info.load();
				rating.load();
				tags.load();
				comments.load(image_id);
			}
		};


		MainFrame.add(event);
	}


	@Override
	public void load(String... in){


		if(in.length > 1){
			path = in[0];
			image_id = in[1];
		}

		if( path == null || path.isEmpty()){
			return;
		}


		loadImageAsync();

		IconButton edit = new IconButton(ICON_EDIT_REG, ICON_EDIT_OVER);
		edit.setToolTipText("Edit the image");
		edit.setActionCommand(ACTION_EDIT);
		edit.addActionListener(this);
		edit.setBorder(new EmptyBorder(0,20,0,20));
		top_panel.add(new GCComponent("elements.imageview.edit",edit));

		IconButton delete = new IconButton(ICON_DELETE_REG, ICON_DELETE_OVER);
		delete.setToolTipText("Delete the image");
		delete.setActionCommand(ACTION_DELETE);
		delete.addActionListener(this);
		top_panel.add(new GCComponent("elements.imageview.delete",delete));
		
		info.setLibrary(library);

		tags.load(in);
	}


	/**
	 * Go the the next image in the library
	 */
	private void next(){

		if(library == null || !library.isValid()){
			return;
		}

		index++;

		if(index >= library.size()){
			index = 0;
		}


		this.image_id = library.getId(index);
		this.path = library.getPath(index);		
		this.loadImageAsync();
	}

	
	/**
	 * Go to the previous image in the library
	 */
	private void prev(){


		if(library == null || !library.isValid()){
			return;
		}


		index--;

		if(index < 0){
			index = library.size()-1;
		}

		this.image_id = library.getId(index);
		this.path = library.getPath(index);		
		this.loadImageAsync();
	}


	/**
	 * Open a EditorView with the current Image.
	 */
	private void edit(){


		EditorView editorview = new EditorView();
		editorview.load(path,image_id);
		MainFrame.addView("Editor", editorview);

	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_DELETE:
			delete();
			break;

		case ACTION_EDIT:
			edit();
			break;

		}

	}


	@Override
	public void keyPressed(KeyEvent e) {


		switch(e.getKeyCode()){

		case KeyEvent.VK_LEFT:
			prev();
			break;

		case KeyEvent.VK_KP_LEFT:
			prev();
			break;

		case KeyEvent.VK_RIGHT:
			next();
			break;		

		case KeyEvent.VK_KP_RIGHT:
			next();
			break;	

		}


	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}



}
