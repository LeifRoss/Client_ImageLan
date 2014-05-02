package gc.client.eventqueue;


/**
 * GCEvent interface.
 * All events that is to be run in the EventQueue must implement this interface.
 * @author Leif Andreas Rudlang
 *
 */
public interface GCEvent {
	
	
	/**
	 * Event to be run in the queue
	 */
	public void event();	
	
	
}
