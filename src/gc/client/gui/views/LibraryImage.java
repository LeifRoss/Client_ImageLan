package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.util.Library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * LibraryImage class.
 * This is a Icon to the displayed in a LibraryView
 * @author Leif Andreas
 */
public class LibraryImage extends JPanel implements MouseListener {


	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String path;
	private String thumbnail;

	private int index;
	private Library library;

	private JLabel label;
	private JLabel image;

	private boolean selected;
	private Color background_color;

	/**
	 * Library image view
	 * @param id
	 * @param name
	 * @param path
	 * @param thumbnail
	 */
	public LibraryImage(String id, String name, String path, String thumbnail, int index, Library library){


		this.id = id;
		this.name = name;
		this.path = path.replace("//", "/");
		this.thumbnail = thumbnail.replace("//", "/");		

		this.library = library;
		this.index = index;			
		this.selected = library.getSelected(index);

		setup();	
	}

	/**
	 * Setup the GUI
	 */
	private void setup(){

		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(10, 5, 10, 5) );
		this.addMouseListener(this);

		image = new JLabel();
		this.add(image, BorderLayout.CENTER);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottom.setOpaque(false);
		label = new JLabel(name.substring(0, Math.min(name.length(),15)));

		bottom.add(label);
		this.add(bottom,BorderLayout.SOUTH);
		background_color = (selected ? Color.BLUE : null);
		this.setBackground(background_color);

		loadAsync();
	}


	/**
	 * Loads the image with the eventqueue
	 */
	private void loadAsync(){

		if(thumbnail==null || thumbnail.isEmpty()){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				Image img = MainFrame.getServer().downloadImage(thumbnail);
				setImage(img);			
			}

		};

		MainFrame.add(event);
	}


	/**
	 * Sets the image icon
	 * @param img
	 */
	public void setImage(Image img){

		if(img==null){
			return;
		}

		image.setIcon(new ImageIcon(img));
		image.setText("");		

	}

	/**
	 * Open a ImageView with this Image
	 */
	private void loadImageView(){

		ImageView imageview = new ImageView(library,index);
		imageview.load(path,id);
		MainFrame.addView(name, imageview);
	}

	private void toggleSelected(){

		selected = !selected;
		library.setSelected(index, selected);


		background_color = (selected ? new Color(115,164,209) : null);
		this.setBackground(selected ? background_color : Color.WHITE);
	}


	@Override
	public void mouseClicked(MouseEvent e) {	

		switch(e.getButton()){

		case MouseEvent.BUTTON1:
			loadImageView();
			break;

		case MouseEvent.BUTTON3:
			toggleSelected();
			break;	
		}

	}


	@Override
	public void mousePressed(MouseEvent e) {
		this.setBackground(new Color(191,98,4));
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		this.setBackground(background_color);
	}


	@Override
	public void mouseEntered(MouseEvent e) {

		this.setBackground(selected ? background_color : Color.WHITE);
	}


	@Override
	public void mouseExited(MouseEvent e) {
		this.setBackground(background_color);
	}




}
