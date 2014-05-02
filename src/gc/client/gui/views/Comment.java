package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * Comment class
 * @author Leif Andreas Rudlang
 *
 */
public class Comment extends JPanel implements MouseListener{

	private static final String INVALID_ID = "-1";

	private String id, date, user, content;
	private JLabel label;


	/**
	 * Comment
	 * @param id
	 * @param date
	 * @param user
	 * @param content
	 */
	public Comment(String id, String date, String user, String content){
		super();

		this.id = id;
		this.date = date;
		this.user = user;
		this.content = content;

		setup();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setup(){
		this.setLayout( new BorderLayout());
		this.setBackground(Color.WHITE);


		label = new JLabel(user + " -"+date);	
		Font font = label.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		label.setFont(font.deriveFont(attributes));		

		label.addMouseListener(this);

		if(!id.equals(INVALID_ID)){
			this.add(label,BorderLayout.NORTH);
		}

		JLabel area = new JLabel("<html>"+content+"</html>");
		this.add(area,BorderLayout.CENTER);		
		this.setBorder(new EmptyBorder(20,20,20,20));

	}

	/**
	 * Delete this comment if it has a valid id.
	 */
	private void delete(){

		int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this comment?");

		if(result != JOptionPane.OK_OPTION || id.equals(INVALID_ID)){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().deleteComment(id);			
			}		
		};
		MainFrame.add(event);

		label.setText("<Deleted Comment>");
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		delete();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		label.setForeground(Color.RED);
	}


	@Override
	public void mouseExited(MouseEvent e) {
		label.setForeground(Color.BLACK);
	}


	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}



}
