package gc.client.gui;

import gc.client.com.MainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Main actionhandler class for GCComponents
 * @author Leif Andreas
 *
 */
public class ActionHandler implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		
		MainFrame.execute(e.getActionCommand());
	}
	
}
