package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.util.MetadataLibrary;
import gc.client.util.Util;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * StarRater Class for ImageView GCView
 */
public class StarRater extends JPanel implements MouseListener{

	private static final ImageIcon STAR_FILLED = new ImageIcon(Util.getAssetsLocation()+"graphics//star_filled.png"); 
	private static final ImageIcon STAR_EMPTY = new ImageIcon(Util.getAssetsLocation()+"graphics//star_empty.png"); 
	private static final String METADATA_RATING = "Rating";
	private static final int N_STARS = 5;

	private JLabel[] stars;
	private int selected_rating;
	private MetadataLibrary metadata;


	/**
	 * StarRater
	 * @param metadata
	 */
	public StarRater(MetadataLibrary metadata){
		super();

		this.metadata = metadata;
		setup();
	}

	
	/**
	 * Setup the GUI
	 */
	private void setup(){

		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		stars = new JLabel[N_STARS];

		for(int i = 0; i < N_STARS; i++){

			JLabel label = new JLabel(STAR_EMPTY);
			label.addMouseListener(this);

			stars[i] = label;
			this.add(label);
		}


		selected_rating = 0;
	}


	/**
	 * Render the rating
	 * @param rating
	 */
	private void renderRating(int rating){

		if(rating < 0 || rating > N_STARS){
			return;
		}

		for(int i = 0; i < N_STARS; i++){

			if(i < rating){
				stars[i].setIcon(STAR_FILLED);
			}else{
				stars[i].setIcon(STAR_EMPTY);
			}

		}

	}

	
	/**
	 * Load the rating
	 */
	public void load(){

		String rating = "0";

		if(metadata.containsTag(METADATA_RATING)){

			rating = metadata.getTag(METADATA_RATING);

			if(rating == null){
				rating = "0";
			}		
		}

		try{
			selected_rating = Integer.parseInt(rating);
			renderRating(selected_rating);
		}catch(Exception e){
			MainFrame.error("[StarRater]: Error parsing rating");
		}

	}

	
	/**
	 * Get the index of the input JLabel in the array
	 * @param in
	 * @return
	 */
	private int getIndex(Object in){

		if(!(in instanceof JLabel) || in == null){
			return -1;
		}

		for(int i = 0; i < N_STARS; i++){

			if(stars[i].equals(in)){
				return i+1;
			}
		}

		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		int index = getIndex(e.getSource());
		renderRating(index);

		selected_rating = index;

		metadata.updateTag(METADATA_RATING, ""+selected_rating);
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		int index = getIndex(e.getSource());
		renderRating(index);
	}


	@Override
	public void mouseExited(MouseEvent e) {
		renderRating(selected_rating);
	}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}






}
