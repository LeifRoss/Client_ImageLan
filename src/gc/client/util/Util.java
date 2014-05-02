package gc.client.util;



import gc.client.com.MainFrame;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/**
 * Utility class
 * Contains static methods which handles common tasks.
 * @author Leif Andreas Rudlang
 */
public class Util {

	private static final String[] allowed_suffix = new String[]{".jpg",".jpeg",".png",".gif",".bmp"};
	private static String assetsLocation;

	/**
	 * Returns the location to the asset folder
	 * @return
	 */
	public static String getAssetsLocation(){


		if(assetsLocation != null && !assetsLocation.isEmpty()){
			return assetsLocation;			
		}


		String path = "";

		try {

			File jarpath = new File(gc.client.com.MainFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI());

			path = jarpath.getPath();
			path = path.replace("\\", "/");

			if(path.endsWith("bin/")){
				path = path.replaceAll("bin/", "");
			}else if(path.endsWith("bin//")){
				path = path.replaceAll("bin//", "");
			}else if(path.endsWith("bin")){
				path = path.replaceAll("bin", "");
			}

			if(path.endsWith(".jar")){			
				path = path.substring(0, path.lastIndexOf("/")+1);		
			}

			assetsLocation = path + "assets//";		

		}  catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return assetsLocation;
	}



	/**
	 * Convert a string in the csv format to a string table with the format [row][column]
	 * @param csv
	 * @return
	 */
	public static String[][] toTable(String csv){

		char escape = '\\';
		char string_sep = '\'';
		char column_sep = ',';


		char[] data = csv.toCharArray();
		String value = "";


		ArrayList<String> row = new ArrayList<String>();
		ArrayList<String[]> table = new ArrayList<String[]>();

		boolean inLine = false;
		int nColumns = 0;

		for(int i = 0; i < data.length; i++){

			char c = data[i];

			// optimize this!!
			if(c == escape && !inLine || i==data.length-1 || c == '\n'){	

				if(i < data.length-1 && data[i+1] == 'n' || i==data.length-1 || c == '\n'){

					row.add(value);
					value = "";

					nColumns = Math.max(nColumns, row.size());
					String[] row_array = row.toArray(new String[row.size()]);

					table.add(row_array);
					row.clear();	

				}

			}else if(c == column_sep && !inLine){	

				row.add(value);
				value = "";

			}else if(c == string_sep){			
				inLine = !inLine;

			}else{		
				value += c;
			}

		}


		String[][] result = new String[table.size()][nColumns];

		for(int r = 0; r < table.size(); r++){

			String[] row_array = table.get(r);

			for(int c = 0; c < row_array.length; c++){
				result[r][c] = row_array[c];
			}		

		}		

		return result;		
	}




	/**
	 * Returns a scaled image
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
		int imageWidth  = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double)width/imageWidth;
		double scaleY = (double)height/imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(
				image,
				new BufferedImage(width, height, image.getType()));
	}




	/**
	 * Returns true if the format is allowed
	 * @param in
	 * @return
	 */
	public static boolean formatValid(String in){

		String file = in.toLowerCase();

		for(String s : allowed_suffix){

			if(file.endsWith(s)){
				return true;
			}
		}		

		return false;
	}


	/**
	 * Returns true if the format is supported by the Default-Toolkit
	 * @param path
	 * @return
	 */
	public static boolean supportedByToolkit(String path){

		return path.endsWith(".gif") || path.endsWith(".png") || path.endsWith(".jpeg") || path.endsWith(".jpg");
	}


	/**
	 * Load a image from the system
	 * @param path
	 * @return
	 */
	public static Image loadImage(String path){

		File f = new File(path);
		Image img = null;


		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}


	/**
	 * Save a image to the system
	 * @param path
	 * @param img
	 */
	public static void saveImage(String path, BufferedImage img){

		if(path == null || path == null || path.isEmpty()){
			MainFrame.error("[Util]: Tried to save a invalid image");
			return;
		}

		File f = new File(path);

		if(!f.exists()){
			f.mkdirs();
		}


		try {
			ImageIO.write(img, getSuffix(f.getName()), f);
		} catch (IOException e) {
			e.printStackTrace();
		}		

	}


	/**
	 * Returns the suffix
	 * @param in
	 * @return
	 */
	public static String getSuffix(String in){

		int idx0 = in.lastIndexOf(".")+1;
		int idx1 = in.length();

		if(idx0 > 0 && idx1 > idx0){

			return in.substring(idx0);		
		}			

		return null;
	}




}
