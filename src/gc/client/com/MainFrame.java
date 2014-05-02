package gc.client.com;

import gc.client.eventqueue.EventQueue;
import gc.client.eventqueue.GCEvent;
import gc.client.gui.GCView;
import gc.client.gui.GUIHandler;
import gc.client.gui.views.ClientView;
import gc.client.gui.views.LoginView;
import gc.client.util.CommandRouter;

public class MainFrame {


	private static CommandRouter root;
	private static String COMMAND_ROOT = "cmd";
	private static ServerConnector connector;
	private static EventQueue event_queue;
	private static ClientView client_view;
	
	/**
	 * Init the mainframe
	 */
	public static void initMainFrame(){

		initCommandRouter();
		initGUI();
		initServerConnector();
		initEventQueue();

		entry();
	}



	// Init Commands 
	
	private static void initCommandRouter(){		
		root = new CommandRouter(COMMAND_ROOT, true);
	}

	private static void initGUI(){
		GUIHandler.init();
	}

	private static void initServerConnector(){	
		connector = new ServerConnector(false);		
	}

	private static void initEventQueue(){
		event_queue = new EventQueue();
	}

	/**
	 * Entry point of application
	 */
	private static void entry(){

		LoginView login = new LoginView();
	}

	
	/// mainframe commands /////
	
	/**
	 * Executes a command through the root commandrouter
	 * @param query
	 */
	public static void execute(String query){
		root.route(query);	
	}

	/**
	 * Regular print
	 * @param in
	 */
	public static void print(String in){		
		System.out.println(in);
	}

	/**
	 * Prints a error
	 * @param in
	 */
	public static void error(String in){
		System.err.println(in);
	}

	/**
	 * Adds a command router
	 * @param in
	 */
	public static void add(CommandRouter in){
		root.addRouter(in);
	}

	/**
	 * Removes a commandrouter
	 * @param in
	 */
	public static void remove(CommandRouter in){
		root.removeRouter(in.getName());
	}

	
	public static void setClientView(ClientView in){
		client_view = in;
	}
	
	public static void addView(String title, GCView view){
		if(client_view != null){
			client_view.addTab(view, title);
		}
	}
	
	/**
	 * Safe shutdown
	 */
	public static void shutdown(){
		
		event_queue.stop();
		connector.destroy();		
		System.exit(0);
	}
	
	
	//// Server methods /////
	
	/**
	 * Returns the server connector
	 * @return
	 */
	public static ServerConnector getServer(){
		return connector;
	}

	/**
	 * Adds a GCEvent to the event queue
	 * @param e
	 */
	public static void add(GCEvent e){
		event_queue.add(e);
	}


	public static EventQueue getEventQueue(){
		return event_queue;
	}
	

}
