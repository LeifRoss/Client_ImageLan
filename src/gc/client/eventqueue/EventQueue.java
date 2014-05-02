package gc.client.eventqueue;

import java.util.ArrayDeque;


/**
 * EventQueue to handle asynchronous events
 * @author Leif Andreas
 */
public class EventQueue implements Runnable {

	private static final int QUEUE_SLEEP_TIME = 100;

	private boolean running;
	private boolean working;

	private ArrayDeque<GCEvent> events;	
	private EventQueueListener listener;

	/**
	 * EventQueue
	 */
	public EventQueue(){

		setup();
	}

	
	private void setup(){

		working = false;

		events = new ArrayDeque<GCEvent>();

		Thread t = new Thread(this);
		t.start();		
	}

	
	/**
	 * Stop the queue, queue must be re-instanced to be restarted.
	 */
	public void stop(){
		running = false;
	}

	
	/**
	 * Add a GCEvent to the queue
	 * @param e
	 */
	public synchronized void add(GCEvent e){

		try{
			events.addLast(e);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	/**
	 * Returns true if the Queue is working on something
	 * @return
	 */
	public synchronized boolean isWorking(){
		return working;
	}


	@Override
	public void run() {

		running = true;
		int total = 0;

		while(running){


			total = events.size();
			if(total != 0 && listener != null){
				listener.startedWorking();
			}

			while(!events.isEmpty()){


				GCEvent event = events.removeFirst();

				try{
					working = true;
					event.event();
				}catch(Exception e){
					e.printStackTrace();
				}

				if(listener != null){
					listener.update(total, events.size());
				}
			}
			if(listener != null){
				listener.stoppedWorking();
			}
			working = false;

			try {
				Thread.sleep(QUEUE_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	
	/**
	 * Set the listener
	 * @param in
	 */
	public synchronized void setListener(EventQueueListener in){
		this.listener = in;
	}









}
