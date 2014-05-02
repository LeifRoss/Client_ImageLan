package gc.client.editor;

import gc.client.com.MainFrame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * ImageHelper class
 * Contains static methods commonly used to modify images
 */
public class ImageHelper {


	/**
	 * Returns the average color from two argb8888 integers
	 * @param argb0
	 * @param argb1
	 * @return
	 */
	public static int getRgbAvg(int argb0, int argb1){

		int red0 = argb0 & 0xff;
		int green0 = (argb0 >> 8) & 0xff;
		int blue0 = (argb0 >> 16) & 0xff;

		int red1 = argb1 & 0xff;
		int green1 = (argb1 >> 8) & 0xff;
		int blue1 = (argb1 >> 16) & 0xff;

		int red = (red0 + red1) /2;
		int green = (green0 + green1) /2;
		int blue = (blue0 + blue1) /2;


		int avg = (0xff << 24) + (red << 16) + (green << 8) + blue;

		return avg;
	}

	/**
	 * Retusn the scalar from a argb8888 integer
	 * @param argb
	 * @return
	 */
	public static int getRgbScalar(int argb){
		int red = argb & 0xff;
		int green = (argb >> 8) & 0xff;
		int blue = (argb >> 16) & 0xff;
		int sc = (int)Math.sqrt(red*red + green*green + blue*blue);

		return sc;
	}

	/**
	 * Returns the greyscale from a argb8888 integer
	 * @param argb
	 * @return
	 */
	public static int getGreyscale(int argb){
		int red = argb & 0xff;
		int green = (argb >> 8) & 0xff;
		int blue = (argb >> 16) & 0xff;
		int avg = ((red + green + blue) / 3) & 0xff;
		int grey = (0xff << 24) + (avg << 16) + (avg << 8) + avg;
		return grey;
	}



	/**
	 * Returns a new rotated image
	 * @param img
	 * @param angle
	 * @return
	 */
	public static Image rotate(Image img, double angle){

		if(!(img instanceof BufferedImage)){
			MainFrame.error("[Rotate]: Image is not a bufferedimage");
			return img;
		}

		double sin = Math.abs(Math.sin(Math.toRadians(angle)));
		double cos = Math.abs(Math.cos(Math.toRadians(angle)));

		int w = img.getWidth(null); 
		int h = img.getHeight(null);

		int new_width = (int) Math.floor(w*cos + h*sin);
		int new_height = (int) Math.floor(h*cos + w*sin);

		BufferedImage buffered_img = new BufferedImage(new_width,new_height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = buffered_img.createGraphics();


		g.translate((new_width-w)/2, (new_height-h)/2);
		g.rotate(Math.toRadians(angle), w/2, h/2);
		g.drawRenderedImage((BufferedImage)img, null);
		g.dispose();

		return (Image)buffered_img;
	}


	/**
	 * Returns a cropped version of the image
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param src
	 * @return
	 */
	public static BufferedImage cropImage(int x1, int y1, int x2, int y2, BufferedImage src){
		BufferedImage dest = null;
		
		if(x1>x2){
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		
		if(y1>y2){
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		x1 = Math.max(x1, 0);
		x2 = Math.min(x2, src.getWidth()-1);
				
		y1 = Math.max(y1, 0);
		y2 = Math.min(y2, src.getHeight()-1);
		
		dest = src.getSubimage(x1, y1, Math.abs(x2 - x1), Math.abs(y2 - y1));

		return dest;
	}




}
