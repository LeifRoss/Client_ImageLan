package gc.client.util;

import gc.client.com.MainFrame;
import gc.client.eventqueue.GCEvent;

import java.util.HashMap;

/**
 * MetadataLibrary utility-class
 * @author Leif Andreas
 *
 */
public class MetadataLibrary {

	private HashMap<String,String> map;
	private String[][] table;

	private String id;

	
	/**
	 * MetadataLibrary
	 */
	public MetadataLibrary(){			
		setup();
	}

	private void setup(){

		map = new HashMap<String,String>();				
		table = new String[0][0];
	}


	/**
	 * Load metadata into the library from a Image-ID.
	 * @param id
	 */
	public void load(String id){

		this.id = id;
		String csv = MainFrame.getServer().getMetadata(id);


		table = Util.toTable(csv);
		map.clear();

		if(table == null || table.length == 0){

			table = new String[][]{{"<Empty>","<Empty>"}};
		}else{

			for(int i = 0; i < table.length; i++){
				map.put(table[i][0], table[i][1]);
			}
		}
	}

	
	/**
	 * Returns true if the library contains this tag
	 * @param key
	 * @return
	 */
	public boolean containsTag(String key){

		return map.containsKey(key);		
	}

	
	/**
	 * Returns the value of a tag
	 * @param key
	 * @return
	 */
	public String getTag(String key){

		if(containsTag(key)){
			return map.get(key);
		}

		return null;
	}


	/**
	 * Deletes a tag from the library and the server
	 * @param key
	 */
	public void deleteTag(final String key){

		if(!containsTag(key)){
			return;
		}

		map.remove(key);

		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().deleteMetadata(id, key);
			}			
		};		
		MainFrame.add(event);	


		if(table.length-1 <= 0){
			this.table = new String[][]{{"<Empty>","<Empty>"}};
			return;
		}

		String[][] buffer = new String[table.length-1][2];

		int i = 0;

		for(int row = 0; row < table.length; row++){

			if(!key.equals(table[i][0])){

				buffer[i][0] = table[row][0];
				buffer[i][1] = table[row][1];
				i++;		
			}	

			if(i >= buffer.length){
				break;
			}
		}

		this.table = buffer;
	}

	
	/**
	 * Update a tag on this library and on the server
	 * @param key
	 * @param value
	 */
	public void updateTag(final String key, final String value){


		if(map.containsKey(key)){

			for(int row = 0; row < table.length; row++){

				if(table[row][0].equals(key)){
					table[row][1] = value;
					break;
				}				
			}			

		}else{

			String[][] buffer = new String[table.length+1][2];


			for(int i = 0; i < table.length; i++){
				buffer[i][0] = table[i][0];
				buffer[i][1] = table[i][1];
			}

			buffer[table.length][0] = key;
			buffer[table.length][1] = value;

			table = buffer;
		}

		map.put(key, value);


		GCEvent event = new GCEvent(){

			@Override
			public void event() {
				MainFrame.getServer().writeMetadata(id, key, value);
			}			
		};		
		MainFrame.add(event);	

	}


	/**
	 * Return the metadata table
	 * @return
	 */
	public String[][] getTable(){
		return table;
	}








}
