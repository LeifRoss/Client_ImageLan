package gc.client.editor;

import gc.client.gui.IconButton;
import gc.client.gui.views.ImageDisplay;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Tool view for Editor GCView
 * @author Leif Andreas
 *
 */
public class ToolPanel extends JPanel implements ActionListener{

	private static final ImageIcon ICON_ROTATE_CLOCKWISE_REG = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_rotate_right_reg.png"); 
	private static final ImageIcon ICON_ROTATE_COUNTER_CLOCKWISE_REG = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_rotate_left_reg.png"); 
	private static final ImageIcon ICON_ROTATE_CLOCKWISE_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_rotate_right_over.png"); 
	private static final ImageIcon ICON_ROTATE_COUNTER_CLOCKWISE_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_rotate_left_over.png"); 

	
	
	private static final ImageIcon ICON_CROP_REG = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_crop_reg.png"); 
	private static final ImageIcon ICON_CROP_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_crop_over.png"); 


	private static final String ACTION_ROTATE_CLOCKWISE = "rotate clock";
	private static final String ACTION_ROTATE_COUNTER_CLOCKWISE = "rotate counter clock"; 
	private static final String ACTION_CROP = "Crop";
	private ImageDisplay display;

	/**
	 * ToolPanel
	 * @param display
	 */
	public ToolPanel(ImageDisplay display){
		super();

		this.display = display;

		setup();
	}


	private void setup(){
		this.setLayout(new BorderLayout());

		JPanel top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		IconButton button = null;

		button = new IconButton(ICON_ROTATE_COUNTER_CLOCKWISE_REG,ICON_ROTATE_COUNTER_CLOCKWISE_OVER);
		button.setActionCommand(ACTION_ROTATE_COUNTER_CLOCKWISE);
		button.setToolTipText("Rotate the image counter-clockwise");
		button.addActionListener(this);
		top_panel.add(button);

		button = new IconButton(ICON_ROTATE_CLOCKWISE_REG, ICON_ROTATE_CLOCKWISE_OVER);
		button.setActionCommand(ACTION_ROTATE_CLOCKWISE);
		button.setToolTipText("Rotate the image clockwise");
		button.addActionListener(this);
		top_panel.add(button);


		button = new IconButton(ICON_CROP_REG, ICON_CROP_OVER);
		button.setActionCommand(ACTION_CROP);
		button.setToolTipText("Crop the image");
		button.addActionListener(this);
		top_panel.add(button);


		this.add(top_panel,BorderLayout.NORTH);
	}


	/**
	 * Crop the image
	 */
	private void crop(){

		CropAction action = new CropAction(display);					
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_ROTATE_CLOCKWISE:
			display.setImage(ImageHelper.rotate(display.getImage(), 90));
			break;
		case ACTION_ROTATE_COUNTER_CLOCKWISE:
			display.setImage(ImageHelper.rotate(display.getImage(), -90));
			break;
		case ACTION_CROP:
			crop();
			break;

		}

	}





}
