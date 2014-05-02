package gc.client.editor;

import gc.client.gui.GCComponent;
import gc.client.gui.GUIFactory;
import gc.client.gui.views.ImageDisplay;
import gc.client.util.VerticalFlowLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Paint View for Editor GCView
 *
 */
public class PaintPanel extends JPanel implements ActionListener, ChangeListener{

	private static final String ACTION_TOGGLE = "toggle";

	private static final int STROKE_WIDTH_MIN = 2;
	private static final int STROKE_WIDTH_MAX = 64;
	private static final int STROKE_WIDTH_DEFAULT = 8;

	private ImageDisplay display;
	private PaintAction paint;
	private JColorChooser color_chooser;
	private Color selected_color;
	private JButton toggle_active;
	private boolean active;
	private GCComponent stroke_box;

	private JSlider stroke_slider;

	/**
	 * PaintPanel
	 * @param display
	 */
	public PaintPanel(ImageDisplay display){
		super();

		this.display = display;		
		setup();	
	}

	private void setup(){
		this.setLayout(new BorderLayout());


		selected_color = new Color(0,0,0);

		paint = new PaintAction(display);	
		paint.setColor(selected_color);

		color_chooser = new JColorChooser(selected_color);
		color_chooser.setPreviewPanel(new JPanel());
		color_chooser.getSelectionModel().addChangeListener(this);

		this.add(new JScrollPane(color_chooser),BorderLayout.EAST);


		JPanel panel_top = new JPanel(new FlowLayout(FlowLayout.LEFT));


		// Stroke width panel
		JPanel panel_width = new JPanel(new BorderLayout());
		panel_width.add(new JLabel("Stroke Size"),BorderLayout.NORTH);

		stroke_slider = new JSlider(JSlider.VERTICAL, STROKE_WIDTH_MIN, STROKE_WIDTH_MAX, STROKE_WIDTH_DEFAULT);
		stroke_slider.setMajorTickSpacing(10);
		stroke_slider.setMinorTickSpacing(2);
		stroke_slider.setPaintTicks(true);
		stroke_slider.setPaintLabels(true);
		stroke_slider.addChangeListener(this);

		panel_width.add(stroke_slider,BorderLayout.CENTER);		
		panel_top.add(panel_width);

		// toggle panel and brush type
		JPanel toggle_panel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.LEFT, VerticalFlowLayout.TOP));
		toggle_active = new JButton("Paint");		
		toggle_active.addActionListener(this);
		toggle_active.setActionCommand(ACTION_TOGGLE);
		toggle_active.setBackground(Color.RED);
		toggle_panel.add(toggle_active);

		stroke_box = GUIFactory.combobox("editor.paint.combobox", "Basic Stroke","Double Stroke","Control Point Stroke","Sloppy Stroke");
		toggle_panel.add(stroke_box.getComponent());
		
		
		panel_top.add(toggle_panel);


		this.add(new JScrollPane(panel_top),BorderLayout.CENTER);
	}


	/**
	 * Toggle the paint function on and off
	 */
	private void toggle(){

		active = !active;
		paint.setActive(active);

		if(active){
			toggle_active.setBackground(Color.GREEN);		
		}else{
			toggle_active.setBackground(Color.RED);
		}

	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_TOGGLE:
			toggle();
			break;

		}

	}

	
	@Override
	public void stateChanged(ChangeEvent e) {
		selected_color = color_chooser.getColor();	
		paint.setColor(selected_color);

		if(!stroke_slider.getValueIsAdjusting()){
			paint.setWidth(stroke_slider.getValue(),stroke_box.getText());
		}

	}

}
