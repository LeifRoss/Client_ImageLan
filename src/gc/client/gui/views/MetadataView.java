package gc.client.gui.views;


import gc.client.gui.GCView;
import gc.client.util.MetadataLibrary;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


/**
 * MetadataView for ImageView GCView
 * @author Leif Andreas
 */
public class MetadataView extends GCView implements ActionListener {

	private static final String COLUMN_KEY = "Key";
	private static final String COLUMN_VALUE = "Value";

	private JTable table_view;
	private MetadataLibrary metadata;

	private String[][] table_data;
	private String[] columns;
	private JPopupMenu menu;
	private int selected_row;

	
	/**
	 * MetadataView
	 * @param metadata
	 */
	public MetadataView(MetadataLibrary metadata){
		super("views.metadata",new JPanel());
		
		this.metadata = metadata;	
	}


	@Override
	public void setup(){

		this.setLayout(new BorderLayout());
		columns = new String[]{COLUMN_KEY,COLUMN_VALUE};
			
		table_data = new String[][]{{"<Empty>","<Empty>"}};
		table_view = new JTable(new MyTableModel());
		addListener();

		JScrollPane table_pane = new JScrollPane(table_view);
		this.add(new GCView("",table_pane));

		menu = new JPopupMenu();
		JMenuItem item = null;

		item = new JMenuItem("Edit");
		item.addActionListener(this);
		menu.add(item);

		item = new JMenuItem("Add");
		item.addActionListener(this);
		menu.add(item);

		item = new JMenuItem("Delete");
		item.addActionListener(this);
		menu.add(item);
	
	}



	@Override
	public void load(String... in){
		
		this.table_data = metadata.getTable();		
		table_view.revalidate();
	
	}


	/**
	 * Adds the mouse listener
	 */
	private void addListener(){

		if(table_view == null){
			return;
		}

		table_view.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();

				if(e.getButton() == MouseEvent.BUTTON1){

					if (e.getClickCount() == 2) {				
						edit(row);
					}

				}else if(e.getButton() == MouseEvent.BUTTON3){			

					selected_row = row;
					menu.show(table_view, e.getX(), e.getY());
				}

			}
		});		
	}



	/**
	 * Edits a metadata tag based on user input
	 * @param row
	 */
	private void edit(int row){


		final String key = table_data[row][0];
		String value = table_data[row][1];

		final String result = JOptionPane.showInputDialog(table_view, "Edit metadata", value);

		if(result != null && !result.isEmpty()){		
			
			metadata.updateTag(key, result);	
			this.table_data = metadata.getTable();
			table_view.repaint();
		}

	}


	/**
	 * Adds a new metadata tag based on user input
	 */
	private void addTag(){


		final String key = JOptionPane.showInputDialog(table_view, "Tag name");

		if(key == null || key.isEmpty()){
			return;
		}

		if(metadata.containsTag(key)){
			return;
		}

		final String value = JOptionPane.showInputDialog(table_view, "Tag value");

		if(value == null || value.isEmpty()){
			return;
		}

		metadata.updateTag(key,value);
		table_data = metadata.getTable();
		table_view.repaint();
	}


	private void delete(int row){


		final String key = table_data[row][0];

		int result = JOptionPane.showConfirmDialog(table_view, "Are you sure you want to delete this tag?["+key+"]");

		if(result == JOptionPane.OK_OPTION){

			metadata.deleteTag(key);
			this.table_data = metadata.getTable();
			table_view.repaint();
		}

	}


	@Override
	public void actionPerformed(ActionEvent e) {


		switch(e.getActionCommand()){

		case "Add":
			addTag();
			break;
		case "Edit":
			edit(selected_row);
			break;
		case "Delete":
			delete(selected_row);
			break;

		}

	}


	/**
	 * Table model for the metadata table
	 * @author Leif Andreas Rudlang
	 *
	 */
	class MyTableModel extends AbstractTableModel {


		public int getColumnCount() {
			return table_data[0].length;
		}

		public int getRowCount() {
			return table_data.length;
		}

		public String getColumnName(int col) {
			return columns[col];
		}

		public Object getValueAt(int row, int col) {
			return table_data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}


}
