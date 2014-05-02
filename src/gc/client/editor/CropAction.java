package gc.client.editor;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import gc.client.gui.views.ImageDisplay;

/**
 * ImageAction to handle cropping
 *
 */
public class CropAction extends ImageAction{

	private int start_x, start_y, stop_x, stop_y, start_dx, start_dy;
	private Color color;
	

	public CropAction(ImageDisplay display){
		super(display);
		
		color = new Color(0,0,120,50);
	}

	@Override
	public void mouseDragged(int x, int y, MouseEvent e) {
		
		stop_x = e.getX();
		stop_y = e.getY();
		
		draw();
	}

	@Override
	public void mouseMoved(int x, int y, MouseEvent e) {}

	@Override
	public void mouseWheelMoved(int dx) {}

	@Override
	public void mouseClicked(int x, int y, MouseEvent e) {}

	@Override
	public void mousePressed(int x, int y, MouseEvent e) {
		start_x = e.getX();
		start_y = e.getY();
		
		start_dx = x;
		start_dy = y;
	}

	@Override
	public void mouseReleased(int x, int y, MouseEvent e) {
		
		this.setImage(ImageHelper.cropImage(start_dx, start_dy, x, y, this.getImage()));
		draw();
		this.finished();
	}

	@Override
	public void mouseEntered(int x, int y, MouseEvent e) {}

	@Override
	public void mouseExited(int x, int y, MouseEvent e) {}

	@Override
	public void paint(Graphics2D g) {
		
		g.setColor(color);
		g.fillRect(start_x, start_y, stop_x-start_x, stop_y-start_y);	
		
		g.setColor(Color.BLACK);
		g.drawRect(
				Math.min(start_x, stop_x), 
				Math.min(start_y, stop_y), 
				Math.max(start_x-stop_x, stop_x-start_x), 
				Math.max(start_y-stop_y, stop_y-start_y)
				);		
	}


}
