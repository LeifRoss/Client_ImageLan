package gc.client.gui.views;


import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.gui.IconButton;
import gc.client.util.Library;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class InfoView extends GCView implements ActionListener{


	private static final ImageIcon ICON_TAG_REG = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_tag_reg.png"); 
	private static final ImageIcon ICON_TAG_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics//icon_tag_over.png"); 	

	private static final String ACTION_TAG = "tag";

	private Library library;
	private int index;
	private JPanel tag_display;
	private JPanel view;
	private JLabel name;

	public InfoView() {
		super("imageview.info", new JPanel());
	}


	@Override
	public void setup(){		
		this.setLayout(new BorderLayout());	

		view = (JPanel)this.getContainer();

		name = new JLabel();
		name.setBorder(new EmptyBorder(5,20,10,10));
		name.setFont(new Font("Arial",Font.BOLD,20));
		view.add(name, BorderLayout.NORTH);

		tag_display = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tag_display.setBackground(new Color(10,10,10));
		tag_display.setBorder(BorderFactory.createLoweredBevelBorder());
		view.add(tag_display, BorderLayout.CENTER);

		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));	

		IconButton create_tag = new IconButton(ICON_TAG_REG, ICON_TAG_OVER);
		create_tag.setToolTipText("Add a new tag to this image");
		create_tag.setActionCommand(ACTION_TAG);
		create_tag.addActionListener(this);
		button_panel.add(create_tag);

		view.add(button_panel, BorderLayout.SOUTH);
	}	

	public void setLibrary(Library library){
		this.library = library;
	}

	public void setIndex(int idx){
		this.index = idx;		
	}

	@Override
	public void load(String... in){

		if( library == null || !library.isValid() || library.size() <= index){
			return;
		}

		name.setText(library.getName(index));

		tag_display.removeAll();

		GCEvent event = new GCEvent(){

			@Override
			public void event() {

				String csv = MainFrame.getServer().getTags(library.getId(index));
				String[][] table = Util.toTable(csv);

				if(table == null || table.length <= 0 || table[0].length < 2){
					return;
				}

				for(int row = 0; row < table.length; row++){		
					tag_display.add(new ImageTag(table[row][0],table[row][1]));
				}		
			}

		};
		MainFrame.add(event);

		tag_display.repaint();
	}


	private void createTag(){

		final String tag = JOptionPane.showInputDialog(tag_display, "Create a new tag");

		if(tag == null || tag.isEmpty()){
			return;
		}

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().createTag(library.getId(index), tag);
			}

		};
		MainFrame.add(event);		
		tag_display.add(new ImageTag("",tag));		


	}

	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_TAG:
			createTag();
			break;
		}
		
	}

}
