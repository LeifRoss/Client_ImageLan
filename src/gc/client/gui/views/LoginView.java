package gc.client.gui.views;

import gc.client.com.MainFrame;
import gc.client.com.ServerInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;


/**
 * LoginView class
 * Application GUI Entry-point
 * Provides a GUI to enter the Server Address
 * @author Leif Andreas Rudlang
 */
public class LoginView  implements ActionListener{


	private JFrame window;
	private JTextField address;
	private JProgressBar progress;
	private JButton disc, login;

	private static final String ACTION_LOGIN = "Login";
	private static final String ACTION_DISCOVER = "Discover";


	/**
	 * LoginView
	 */
	public LoginView(){

		setup();
	}

	/**
	 * Setup GUI
	 */
	private void setup(){

		window = new JFrame("GCClient");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panel_top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel panel_bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));



		address = new JTextField(30);
		address.setToolTipText("Enter the server address");
		address.setText("http://127.0.0.1:8080");
		address.setActionCommand(ACTION_LOGIN);
		address.addActionListener(this);
		panel_top.add(address);


		progress = new JProgressBar();
		progress.setVisible(false);
		panel_bottom.add(progress);

		disc = new JButton(ACTION_DISCOVER);
		disc.addActionListener(this);
		panel_bottom.add(disc);

		login = new JButton(ACTION_LOGIN);
		login.addActionListener(this);
		panel_bottom.add(login);

		panel.add(panel_top,BorderLayout.CENTER);
		panel.add(panel_bottom,BorderLayout.SOUTH);
		window.add(panel);
		window.pack();
		window.setVisible(true);
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() / 2) - window.getWidth());
		int y = (int) ((dimension.getHeight() / 2) - window.getHeight());
		window.setLocation(x, y);
	}


	/**
	 * Log in
	 */
	private void login(){

		String address_http = address.getText();
		MainFrame.execute("server seturl \""+address_http+"\"");
		new ClientView();

		destroy();
	}

	
	/**
	 * Trigger a Async server discovery on the network.
	 */
	private void discover(){


		Runnable r = new Runnable(){
			@Override
			public void run() {

				disc.setEnabled(false);
				progress.setVisible(true);
				progress.setIndeterminate(true);

				ServerInfo info = MainFrame.getServer().discover();

				if(info != null){
					String host = "http:/"+info.getIP()+":"+info.getPort();
					address.setText(host);
				}

				progress.setIndeterminate(false);
				progress.setVisible(false);
				disc.setEnabled(true);
			}

		};


		Thread t = new Thread(r);
		t.start();

	}

	
	/**
	 * Destroy this view
	 */
	public void destroy(){

		window.dispose();		
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

		switch(e.getActionCommand()){

		case ACTION_LOGIN:
			login();
			break;
		case ACTION_DISCOVER:
			discover();
			break;


		}

	}


}
