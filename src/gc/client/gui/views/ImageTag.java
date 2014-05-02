package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * ImageTag class
 * @author Leif Andreas Rudlang
 */
public class ImageTag extends JLabel implements MouseListener{

	private String id, data;

	public ImageTag(String id, String data){
		super();

		this.id = id;
		this.data = data;		
		this.setFont(new Font("Arial",Font.BOLD,14));
		this.setText(" "+data+" ");		
		this.addMouseListener(this);
		this.setBorder(BorderFactory.createRaisedSoftBevelBorder());
		this.setOpaque(true);
		
	}

	private void delete(){

		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this tag? ["+data+"]");

		if(result != JOptionPane.OK_OPTION || id == null || id.isEmpty()){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().deleteTag(id);
				setText("");	
			}

		};
		MainFrame.add(event);		

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		delete();
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setForeground(Color.RED);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setForeground(Color.BLACK);
	}




}
