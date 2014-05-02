package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.gui.GUIHandler;
import gc.client.util.Library;
import gc.client.util.VerticalFlowLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;


/**
 * LibraryView GCView
 * This is the main Library GUI class
 * Handles general library GUI functions
 * @author Leif Andreas Rudlang
 */
public class LibraryView extends GCView implements ActionListener, KeyListener, MouseListener{

	public final static String ID = "views.library";

	private static final String ACTION_NEXT = "Next";
	private static final String ACTION_PREV = "Prev";
	private static final String ACTION_TAG_SELECTED = "Tag selected";



	private static final int MAX_IMAGES_PER_ROW = 4;
	private static final int MAX_ROWS_PER_PAGE = 4;

	private Library library;
	private JPanel view, buttons;
	private int page;
	private JButton prev_page, next_page;
	private JPopupMenu popup_menu;


	/**
	 * LibraryView
	 */
	public LibraryView() {
		super(ID, new JPanel());
	}


	@Override
	public void setup(){		

		library = new Library();
		this.setLayout(new VerticalFlowLayout(VerticalFlowLayout.CENTER, VerticalFlowLayout.TOP));		
		this.setColor(10, 10, 10);

		view = (JPanel)this.getContainer();
		view.setFocusable(true);
		view.addKeyListener(this);
		view.addMouseListener(this);

		buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.setOpaque(false);

		prev_page = new JButton(ACTION_PREV);
		prev_page.addActionListener(this);

		next_page = new JButton(ACTION_NEXT);
		next_page.addActionListener(this);

		buttons.add(prev_page);
		buttons.add(next_page);		

		// setup popupmenu
		popup_menu = new JPopupMenu();
		JMenuItem item = null;

		item = new JMenuItem(ACTION_TAG_SELECTED);
		item.addActionListener(this);
		popup_menu.add(item);



		page = 0;
	}

	/**
	 * Load a page
	 * @param n
	 */
	private void loadPage(int n){


		prev_page.setEnabled(page != 0);
		next_page.setEnabled(page < library.size() / ( MAX_IMAGES_PER_ROW*MAX_ROWS_PER_PAGE ));

		int page_start = MAX_IMAGES_PER_ROW*MAX_ROWS_PER_PAGE*n;

		if(!library.isValid() || library.size() <= page_start || n < 0){
			return;
		}

		view.removeAll();
		view.repaint();

		int page_stop = Math.min(library.size(), MAX_IMAGES_PER_ROW*MAX_ROWS_PER_PAGE*(n+1));

		int counter = page_start;

		while(counter < page_stop){


			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.setOpaque(false);

			for(int i = 0; i < MAX_IMAGES_PER_ROW; i++){

				LibraryImage image = new LibraryImage(library.getId(counter),library.getName(counter),library.getPath(counter),library.getThumbnail(counter),counter,library);
				panel.add(image);

				counter++;

				if(counter >= page_stop){
					break;
				}
			}

			view.add(panel);
		}

		view.add(buttons);
	}



	@Override
	public void load(String... in){

		String csv = null;
		this.getContainer().removeAll();

		if(in!=null && in.length > 0){
			csv = search();
		}else{
			csv = MainFrame.getServer().getAllImages();
		}

		library.load(csv);

		page = 0;
		loadPage(0);

		view.requestFocus();
		view.requestFocusInWindow();
		view.grabFocus();
	}


	/**
	 * Search function
	 * @return
	 */
	private String search(){

		String param = GUIHandler.getElementById("elements.menubar.search").getText();		
		String csv = MainFrame.getServer().search(param);

		return csv;
	}


	/**
	 * Clamp the page number
	 */
	private void limitPage(){

		if(library.isValid()) {
			page = Math.max(0, Math.min( page, library.size() / ( MAX_IMAGES_PER_ROW*MAX_ROWS_PER_PAGE )));
		}else{
			page = 0;
		}

	}


	/**
	 * Go to the next page in the library
	 */
	public void next(){
		page++;
		limitPage();

		if(next_page.isEnabled()){
			loadPage(page);
		}
	}


	/**
	 * Go to the previous page in the library
	 */
	public void prev(){
		page--;
		limitPage();

		if(prev_page.isEnabled()){
			loadPage(page);	
		}
	}


	private void tagSelected(){

		final String tag = JOptionPane.showInputDialog(view,"Tag");

		if(tag == null || tag.isEmpty()){
			return;
		}


		GCEvent event = new GCEvent(){

			@Override
			public void event() {

				for(int i = 0; i < library.size(); i++){

					if(library.getSelected(i)){	
						MainFrame.getServer().createTag(library.getId(i), tag);		
					}			
				}				


			}
		};

		MainFrame.add(event);
	}



	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){
		case ACTION_NEXT:
			next();
			break;

		case ACTION_PREV:
			prev();
			break;
		case ACTION_TAG_SELECTED:
			tagSelected();
			break;

		}

	}


	@Override
	public void keyPressed(KeyEvent e) {

		switch(e.getKeyCode()){

		case KeyEvent.VK_RIGHT:
			next();
			break;

		case KeyEvent.VK_LEFT:
			prev();
			break;

		}

	}



	@Override
	public void keyTyped(KeyEvent e) {}


	@Override
	public void keyReleased(KeyEvent e) {}


	@Override
	public void mouseClicked(MouseEvent e) {

		switch(e.getButton()){

		case MouseEvent.BUTTON3:
			popup_menu.show(view, e.getX(), e.getY());
			break;


		}


	}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


}
