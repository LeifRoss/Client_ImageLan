package gc.client.gui.views;

import gc.client.editor.ImageAction;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * Image Display class
 * @author Leif Andreas Rudlang
 *
 */
public class ImageDisplay extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {

	private Image img;
	private float proportion;
	private float zoom;

	private int offset_x;
	private int offset_y;

	private int start_x;
	private int start_y;
	
	private int px;
	private int py;

	private float scale;
	private boolean inverted;
	private boolean ctrl_down;
	
	private float img_width, img_height;
	
	private LinkedList<ImageAction> actions;
	
	
	/**
	 * ImageDisplay
	 * If inverted is set to true, ctrl needs to be held in order to move the image around.
	 * @param inverted
	 */
	public ImageDisplay(boolean inverted){
		this();
		
		this.inverted = inverted;	
		
		if(inverted){
			this.addKeyListener(this);
		}
	}
	
	/**
	 * Image Display
	 */
	public ImageDisplay(){

		this.setBackground(new Color(10,10,10));
		proportion = 1f;
		zoom = 1f;
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setFocusable(true);
		
		actions = new LinkedList<ImageAction>();
	}


	/**
	 * Sets the image of this display
	 * @param img
	 */
	public void setImage(Image img){

		if(img == null){
			return;
		}

		this.img = img;

		this.repaint();
	}

	
	/**
	 * Return the image of this display
	 * @return
	 */
	public Image getImage(){
		return img;
	}

	
	/**
	 * Clear the display and flush the image
	 */
	public void clear(){

		if(img == null){
			return;
		}

		img.flush();
		img = null;				
		actions.clear();
	}

	

	/**
	 * Limit image offset
	 */
	private void limitOffset(){


		float max_x = (this.getWidth()/2)*zoom;
		float max_y = (scale/2)*zoom;


		float min_x = -max_x;
		float min_y = -max_y;


		offset_x = (int)(Math.max(min_x, Math.min(max_x, offset_x)));
		offset_y = (int)(Math.max(min_y, Math.min(max_y, offset_y)));
	}


	/**
	 * Get image proportion
	 */
	private void getProportion(){

		img_width = img.getWidth(this);
		img_height = img.getHeight(this);
		proportion = img_width/img_height;

	}

	
	/**
	 * Return the local posX from the image
	 * @param dx
	 * @return
	 */
	public int getLocalX(int dx){

		float width = img_width/(scale*proportion);
		
		
		return (int)((float)(dx - px)*width);
	}
	
	
	/**
	 * Return the local posY from the image
	 * @param dy
	 * @return
	 */
	public int getLocalY(int dy){
		
		float height = img_height/scale;
		
		return (int)((float)(dy - py)*height);
	}

	
	/**
	 * Add a ImageAction to the display
	 * @param in
	 */
	public synchronized void addAction(ImageAction in){
		actions.addLast(in);
	}
	
	
	/**
	 * Remove a ImageAction from the display
	 * @param in
	 */
	public synchronized void removeAction(ImageAction in){
		actions.remove(in);		
	}
	
	
	
	
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		if(img == null){
			return;
		}

		
		getProportion();

		int p_width = this.getWidth();
		int p_height = this.getHeight();

		scale = Math.min(p_width*proportion, p_height)*zoom;

		int width = (int)(scale*proportion);
		int height = (int)(scale);		

		limitOffset();
		
		px = offset_x+(p_width-width)/2;
		py = offset_y;

		Graphics2D g2d = (Graphics2D)g; 
		g.drawImage(img, px, py, width, height, this);
		
		for(ImageAction action : actions){
			action.paint(g2d);
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {

		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseDragged(dx,dy,e);
		}
		
		if(inverted && !ctrl_down){
			return;
		}

		int delta_x = e.getX() - start_x;
		int delta_y = e.getY() - start_y;

		start_x = e.getX();
		start_y = e.getY();

		offset_x += delta_x;
		offset_y += delta_y;

		this.repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		
		
		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseMoved(dx,dy,e);
		}
		
	}


	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		int notches = e.getWheelRotation();

		for(ImageAction action : actions){
			action.mouseWheelMoved(notches);
		}
		
		if(inverted && !ctrl_down){
			return;
		}
		
		if(notches > 0){

			if(zoom > 1.0f){
				offset_x -= (int)(float)( (this.getWidth()/2)-e.getX())*0.25;
				offset_y -= (int)(float)( (this.getHeight()/2)-e.getY())*0.25;
				zoom-=0.25f;
			}
		}else if(notches < 0){

			if(zoom < 10){
				offset_x += (int)(float)( (this.getWidth()/2)-e.getX())*0.25;
				offset_y += (int)(float)( (this.getHeight()/2)-e.getY())*0.25;
				zoom+=0.25f;
			}
		}

		this.repaint();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		this.requestFocus();
		
		
		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseClicked(dx,dy,e);
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		start_x = e.getX();
		start_y = e.getY();
		
		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mousePressed(dx,dy,e);
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {

		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseReleased(dx,dy,e);
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {

		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseEntered(dx,dy,e);
		}
	}


	@Override
	public void mouseExited(MouseEvent e) {

		int dx = getLocalX(e.getX());
		int dy = getLocalY(e.getY());
		
		for(ImageAction action : actions){
			action.mouseExited(dx,dy,e);
		}
	}


	
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		
		case KeyEvent.VK_CONTROL:
			ctrl_down = true;
			break;	
		}
		
	}

	
	@Override
	public void keyReleased(KeyEvent e) {
		
		switch(e.getKeyCode()){
		
		case KeyEvent.VK_CONTROL:
			ctrl_down = false;
			break;	
		}	
	}

	
}
