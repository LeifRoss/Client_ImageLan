package gc.client.com;


public class Main {

	public static String APPLICATION_NAME = "GCClient";
	private static String MODE_DEBUG = "debug";
	
	/**
	 * Pass in "debug" as arg0 to enter Debug mode
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 0 && args[0].contains(MODE_DEBUG)){	
			initDEV();
		}else{				
			initMASTER();
		}
		
	}

	
	private static void initMASTER(){
		
		MainFrame.initMainFrame();				
	}

	
	private static void initDEV(){
		
		MainFrame.initMainFrame();		
	}
	
	
	
}
