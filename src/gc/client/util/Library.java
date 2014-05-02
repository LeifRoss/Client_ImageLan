package gc.client.util;


/**
 * ImageLibrary utility-class
 * @author Leif Andreas Rudlang
 */
public class Library {


	private String[][] data;
	private boolean[] selected;
	private boolean valid;


	/**
	 * Library
	 */
	public Library(){

		setup();
	}

	private void setup(){
		valid = false;
	}

	
	/**
	 * Load a string in the .CSV format into the library
	 * @param csv
	 */
	public void load(String csv){

		valid = false;	

		if(csv == null || csv.isEmpty()){
			return;
		}

		String[][] table = Util.toTable(csv);


		if(table != null && table.length!=0 && table[0].length == 4){

			this.data = table;			
			this.selected = new boolean[table.length];
			valid = true;
		}

	}

	
	/**
	 * Returns true if this library is valid.
	 * @return
	 */
	public boolean isValid(){
		return valid;
	}	

	
	/**
	 * Return the size of the library, in number of rows.
	 * @return
	 */
	public int size(){	
		if(isValid()){
			return data.length;
		}
		return 0;
	}

	
	/**
	 * Return the ID of a library element
	 * @param row
	 * @return
	 */
	public String getId(int row){

		if(row < 0 || row >= data.length){
			return null;
		}

		return data[row][0];
	}

	
	/**
	 * Return the name of a library element
	 * @param row
	 * @return
	 */
	public String getName(int row){

		if(row < 0 || row >= data.length){
			return null;
		}

		return data[row][1];
	}

	
	/**
	 * Return the server-path of a library element
	 * @param row
	 * @return
	 */
	public String getPath(int row){

		if(row < 0 || row >= data.length){
			return null;
		}

		return data[row][2];
	}


	/**
	 * Return the server-thumbnail-path of a library element
	 * @param row
	 * @return
	 */
	public String getThumbnail(int row){

		if(row < 0 || row >= data.length){
			return null;
		}

		return data[row][3];
	}

	
	/**
	 * Set a image as selected
	 * @param row
	 * @param select
	 */
	public void setSelected(int row, boolean select){
		
		if(row >= selected.length || row < 0){
			return;
		}
		
		selected[row] = select;
	}
	
	
	/**
	 * Returns true if the image is selected
	 * @param row
	 * @return
	 */
	public boolean getSelected(int row){
		
		if(row >= selected.length || row < 0){
			return false;
		}
		
		return selected[row];
	}
	
}
