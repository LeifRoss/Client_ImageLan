package gc.client.editor;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.views.ImageDisplay;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Filter panel view for the EditorView
 * @author Leif Andreas
 *
 */
public class FilterPanel extends JPanel implements ActionListener, ListSelectionListener{



	private HashMap<String,ImageFilter> filters;
	private JList<String> list;
	private JPanel parameter_panel;
	private JTextField[] inputs;
	private ImageFilter selected_filter;
	private JLabel description;
	private ImageDisplay display;

	public FilterPanel(ImageDisplay display){
		super();

		this.display = display;
		setup();
	}


	private void setup(){

		this.setLayout(new BorderLayout());


		filters = FilterFactory.getFilterMap();

		// list
		DefaultListModel<String> listModel = new DefaultListModel<String>();


		for(String filterName : filters.keySet()){		
			listModel.addElement(filterName);					
		}

		list = new JList<String>(listModel);

		this.add(new JScrollPane(list),BorderLayout.WEST);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);


		// parameter panel
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		parameter_panel = new JPanel();		
		panel.add(new JScrollPane(parameter_panel));
		this.add(panel,BorderLayout.CENTER);


		JButton applybutton = new JButton("Apply Filter");
		applybutton.addActionListener(this);
		panel.add(applybutton);

		description = new JLabel();
		description.setFont(new Font("Arial", Font.BOLD, 14));
		this.add(description,BorderLayout.NORTH);

	}


	private void loadPanel(String name){

		selected_filter = filters.get(name);
		description.setText(selected_filter.getDescription());
		

		parameter_panel.removeAll();

		String[] params = selected_filter.getParameters();
		inputs = new JTextField[params.length];		

		parameter_panel.setLayout(new GridLayout(params.length,2));


		for(int i = 0; i < params.length; i++){

			JLabel label = new JLabel(params[i]);
			JTextField input = new JTextField(10);		

			inputs[i] = input;

			parameter_panel.add(label);
			parameter_panel.add(input);
		}

		parameter_panel.revalidate();

	}

	private void apply(){

		if(inputs == null || selected_filter == null){
			return;
		}

		final float[] params = new float[inputs.length];

		for(int i = 0; i < params.length; i++){

			try{

				String value = inputs[i].getText();

				if(value.isEmpty()){
					value = "0";
				}

				params[i] = Float.parseFloat(value);
			}catch(NumberFormatException e){
				e.printStackTrace();
			}

		}

		
		if(display.getImage() != null){

			GCEvent event = new GCEvent(){

				@Override
				public void event() {
					selected_filter.apply((BufferedImage)display.getImage(), params);
					display.repaint();
				}

			};

			MainFrame.add(event);


		}

	}



	@Override
	public void actionPerformed(ActionEvent e) {

		apply();
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {


		String filter = list.getSelectedValue();

		loadPanel(filter);


	}

}
