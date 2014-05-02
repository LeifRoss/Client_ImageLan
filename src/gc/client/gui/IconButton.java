package gc.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class IconButton extends JLabel implements MouseListener {

	private ActionEvent action;
	private ActionListener listener;
	
	private ImageIcon regular, over;
	
	
	public IconButton(ImageIcon regular, ImageIcon over){		
		super(regular);
				
		this.regular = regular;
		this.over = over;
		
		this.addMouseListener(this);
	}
	
	public void setActionCommand(String in){
		this.action = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,in);
	}
	
	public void addActionListener(ActionListener listener){
		this.listener = listener;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(action != null && listener !=null){
			listener.actionPerformed(action);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		if(over != null){
			this.setIcon(over);
		}
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		if(regular != null){
			this.setIcon(regular);
		}
	}

	


}
