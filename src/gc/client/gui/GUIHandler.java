package gc.client.gui;


import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * GUIHandler class
 * @author Leif Andreas
 *
 */
public class GUIHandler {



	private static HashMap<String,GCView> views;
	private static HashMap<String,GCComponent> components;
	private static ActionHandler action_handler;

	/**
	 * Initializes the GUIHandler
	 */
	public static void init(){

		setLookAndFeel("Nimbus");	
		

		views = new HashMap<String,GCView>();
		components = new HashMap<String,GCComponent>();
		action_handler = new ActionHandler();
	}


	/**
	 * Set the look-and-feel of the application
	 * @param in
	 */
	public static void setLookAndFeel(String in){

		try{

			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

				if(in.equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Return the component with the given id
	 * @param id
	 * @return
	 */
	public static GCComponent getElementById(String id){

		if(components.containsKey(id)){
			return components.get(id);
		}

		return null;
	}

	
	/**
	 * Return the view with the given id
	 * @param id
	 * @return
	 */
	public static GCView getViewById(String id){

		if(views.containsKey(id)){
			return views.get(id);
		}

		return null;
	}

	/**
	 * Removes the view with the given id
	 * @param id
	 */
	public static void removeViewById(String id){	
		views.remove(id);		
	}

	
	/**
	 * Return the component with the given id
	 * @param id
	 */
	public static void removeElementById(String id){
		components.remove(id);
	}


	/**
	 * Adds a GCView
	 * @param in
	 */
	public static void add(GCView in){
		views.put(in.getID(), in);
	}

	
	/**
	 * Adds a GCComponent
	 * @param in
	 */
	public static void add(GCComponent in){
		components.put(in.getID(), in);
	}


	/**
	 * Return the Main ActionHandler
	 * @return
	 */
	public static ActionHandler getActionHandler(){
		return action_handler;
	}


	/**
	 * Requests a Async cleanup of old elements
	 */
	public static synchronized void issueCleanup(){

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				cleanup();			
			}
		};

		MainFrame.add(event);
	}

	/**
	 * Cleans up old elements
	 */
	public static void cleanup(){


		ArrayDeque<String> old_components = new ArrayDeque<String>();
		ArrayDeque<String> old_views = new ArrayDeque<String>();


		for(Entry<String,GCView> entry : views.entrySet()){

			String name = entry.getKey();
			GCView view = entry.getValue();

			if(view.isNull()){

				for(String key : components.keySet()){

					if(key.contains(name)){
						old_components.add(key);
					}			
				}	

				old_views.add(name);
			}						
		}

		while(!old_components.isEmpty()){
			components.remove(old_components.removeFirst());
			System.out.println("Destroyed component");
		}

		while(!old_views.isEmpty()){
			views.remove(old_views.removeFirst());
			System.out.println("Destroyed view");
		}

		old_components = null;
		old_views = null;
	}



}
