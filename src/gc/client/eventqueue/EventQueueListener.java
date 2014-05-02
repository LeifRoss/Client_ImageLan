package gc.client.eventqueue;


/**
 * EventQueueListener interface.
 * Implement this interface if you want to register EventQueue status.
 * @author Leif Andreas Rudlang
 */
public interface EventQueueListener {

	/**
	 * Called it the queue starts working on something
	 */
	public void startedWorking();
	
	/**
	 * Called if the queue is finished working
	 */
	public void stoppedWorking();
	
	/**
	 * Called every time when the queue has processed a GCEvent
	 * @param total
	 * @param left
	 */
	public void update(int total, int left);
		
	
}
