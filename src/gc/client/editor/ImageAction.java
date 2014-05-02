package gc.client.editor;


import gc.client.gui.views.ImageDisplay;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Abstract class for adding actions to ImageDisplays
 * @author Leif Andreas Rudlang
 * @Date 07.04.2014
 */
public abstract class ImageAction {


	private Graphics2D graphics, graphics_display;
	private ImageDisplay display;
	private boolean isSetup;

	public ImageAction(ImageDisplay in){

		this.display = in;


		setup();		


		display.addAction(this);
	}


	public abstract void mouseDragged(int x, int y, MouseEvent e);

	public abstract void mouseMoved(int x, int y, MouseEvent e);

	public abstract void mouseWheelMoved(int dx);

	public abstract void mouseClicked(int x, int y, MouseEvent e);

	public abstract void mousePressed(int x, int y, MouseEvent e);

	public abstract void mouseReleased(int x, int y, MouseEvent e);

	public abstract void mouseEntered(int x, int y, MouseEvent e);

	public abstract void mouseExited(int x, int y, MouseEvent e);

	public abstract void paint(Graphics2D g);

	
	private void setup(){

		if(!isSetup){

			if(display.getImage() != null){	

				this.graphics = this.getImage().createGraphics();
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				this.graphics_display = (Graphics2D)display.getGraphics();
				isSetup = true;
			}
		}
		
	}

	public void draw(){
		display.repaint();
	}

	public void finished(){
		graphics.dispose();
		graphics_display.dispose();
		display.removeAction(this);
	}

	public BufferedImage getImage(){
		return (BufferedImage)display.getImage();
	}

	public void setImage(BufferedImage img){
		display.setImage(img);
	}

	public Graphics2D getImageGraphics(){
		
		setup();
		return graphics;
	}

	public Graphics2D getDisplayGraphics(){
		
		setup();		
		return graphics_display;
	}


}
