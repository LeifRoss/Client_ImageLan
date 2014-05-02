package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.gui.GUIFactory;
import gc.client.util.Util;
import gc.client.util.VerticalFlowLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * CommentView class for the Image GCView
 * @author Leif Andreas
 *
 */
public class CommentView extends GCView implements ActionListener{

	private static final String ACTION_POST = "Post";

	private String image_id;
	private JPanel comment_display;	
	private JTextField user_name;
	private JTextArea user_comment;

	/**
	 * CommentView
	 */
	public CommentView(){
		super("views.comment", new JPanel());
	}


	@Override
	public void setup(){
		this.setLayout(new BorderLayout());

		// add comments display
		comment_display = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.LEFT, VerticalFlowLayout.TOP));
		comment_display.setBackground(new Color(10,10,10));
		GCView display_view = new GCView("views.comment.display",new JScrollPane(comment_display));


		// add the post comment view
		JPanel post_panel = new JPanel(new BorderLayout());

		user_name = new JTextField(20);
		user_comment = new JTextArea();

		user_name.setToolTipText("Enter username (5-32) characters");
		user_comment.setToolTipText("Enter your comment (10-256) characters\nThe comment field is HTML-enabled");

		post_panel.add(user_name,BorderLayout.NORTH);
		post_panel.add(new JScrollPane(user_comment),BorderLayout.CENTER);


		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JButton button = new JButton(ACTION_POST);
		button.addActionListener(this);
		button_panel.add(button);

		post_panel.add(button_panel,BorderLayout.SOUTH);
		GCView post_view = new GCView("views.comment.post",post_panel);



		// seperate the display and post-view with a splitpane
		GCView splitpane = GUIFactory.splitpane("views.comment.split", 0.85, true);
		splitpane.add(display_view);
		splitpane.add(post_view);

		this.add(splitpane,BorderLayout.CENTER);

	}


	/**
	 * Load the view with the EventQueue
	 */
	public void loadAsync(){

		comment_display.removeAll();

		if(image_id == null || image_id.isEmpty()){
			return;
		}

		String csv = MainFrame.getServer().getComments(image_id);
		String table[][] = Util.toTable(csv);

		if(csv == null || csv.isEmpty() || table.length <= 0 || table[0].length < 4){
			return;
		}

		for(int i = 0; i < table.length; i++){	

			String comment_id = table[i][0];
			String date = table[i][1];
			String user = table[i][2];
			String content = table[i][3];

			if(user == null || user.isEmpty() || content == null || content.isEmpty()){
				continue;
			}

			comment_display.add(new Comment(comment_id,date,user,content));
		}

		comment_display.repaint();
	}


	@Override
	public void load(String ...in){

		if(in.length <= 0 || in[0] == null || in[0].isEmpty()){
			return;
		}

		image_id = in[0];

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				loadAsync();
			}
		};
		MainFrame.add(event);		

	}


	/**
	 * Post a comment
	 */
	private void postComment(){

		final String username = user_name.getText();
		final String comment = user_comment.getText().replace("\n", "<br>");


		if(username.length() < 5 || username.length() > 32 || comment.length() < 10 || comment.length() > 256){

			JOptionPane.showMessageDialog(user_name, "Username must be between 5 and 32 characters long\nComment must be between 10 and 256 characters long");
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().createComment(image_id, username, comment);			
			}

		};
		MainFrame.add(event);		

		comment_display.add(new Comment("-1","Just Now",username,comment));

		user_comment.setText("");

	}


	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_POST:
			postComment();
			break;

		}

	}



}
