package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * UploadView GCView
 * Handles Image uploading GUI.
 * Handles drag-drop, browse, and preview.
 * @author Leif Andreas Rudlang
 */
public class UploadView extends GCView implements ActionListener, ListSelectionListener{

	public static final String ID = "views.upload";
	private static final String ACTION_UPLOAD = "Upload";
	private static final String ACTION_BROWSE = "Browse";
	private static final String ACTION_REMOVE = "Remove";


	private JList<String> list_view;
	private DefaultListModel<String> listModel;
	private ImageDisplay display;
	private Image image;

	
	/**
	 * UploadView
	 */
	public UploadView() {
		super(ID, new JPanel());		
	}

	@Override
	public void setup(){	

		this.setLayout(new BorderLayout());		

		JPanel panel = (JPanel)this.getContainer();

		// build list
		listModel = new DefaultListModel<String>();
		list_view = new JList<String>(listModel);
		list_view.setToolTipText("Drop files or folders here..");
		list_view.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(list_view),BorderLayout.WEST);

		// build utility

		JPanel utility = new JPanel(new BorderLayout());
		JPanel viewer = new JPanel(new BorderLayout());
		JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		display = new ImageDisplay();
		viewer.add(display,BorderLayout.CENTER);

		JButton remove = new JButton("Remove");
		remove.addActionListener(this);
		button_panel.add(remove);

		JButton upload = new JButton("Upload");
		upload.addActionListener(this);
		button_panel.add(upload);

		JButton browse = new JButton("Browse");
		browse.addActionListener(this);
		button_panel.add(browse);


		utility.add(button_panel,BorderLayout.NORTH);
		utility.add(viewer,BorderLayout.CENTER);
		panel.add(utility,BorderLayout.CENTER);


		addListeners();
	}

	
	/**
	 * Register listeners
	 */
	private void addListeners(){

		list_view.setDropTarget(new DropTarget() {
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						appendFile(file);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		list_view.addListSelectionListener(this);
	}

	
	/**
	 * Append a file to the list_view
	 * @param f
	 */
	private void appendFile(File f){

		if(f.isDirectory()){
			for(File c : f.listFiles()){
				appendFile(c);
			}
			return;
		}


		if(!Util.formatValid(f.getName())){
			return;
		}

		listModel.addElement(f.getPath());		
	}


	/**
	 * Browse for files
	 */
	private void browse(){

		FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());


		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(imageFilter);

		int check = fc.showOpenDialog(null);

		if(check == JFileChooser.APPROVE_OPTION){

			for(File f : fc.getSelectedFiles()){
				appendFile(f);
			}
		}

	}


	/**
	 * Uploads the files added to the list_view
	 */
	private void upload(){

		int size = listModel.getSize();

		for(int i = 0; i < size; i++){

			final String path = listModel.get(i);

			if(path != null && !path.isEmpty()){

				GCEvent event = new GCEvent(){

					@Override
					public void event() {

						File f = new File(path);

						if(f.exists() && f.canRead() && f.isFile()){

							String folder = f.getParentFile().getName();		
							MainFrame.getServer().uploadImage(path, f.getName(), "0",folder);	
						}
					}
				};

				MainFrame.add(event);

			}


		}

		listModel.clear();
	}

	
	/**
	 * Remove the selected file from the list
	 */
	private void remove(){

		if(listModel.size() > 0 && list_view.getSelectedIndex() >= 0){
			listModel.remove(list_view.getSelectedIndex());		
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_UPLOAD:
			upload();	
			break;

		case ACTION_BROWSE:
			browse();
			break;

		case ACTION_REMOVE:
			remove();
			break;

		}
	}

	
	@Override
	public void valueChanged(ListSelectionEvent e) {

		String path = list_view.getSelectedValue();

		if(path == null || path.isEmpty()){
			return;
		}

		if(image != null){
			display.setImage(null);
			image.flush();
			image = null;
		}


		image = Util.loadImage(path);
		display.setImage(image);
	}



}
