package gc.client.editor;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import static gc.client.editor.ImageHelper.*;

/**
 * Filter factory class
 * 
 *
 */
public class FilterFactory {


	/**
	 * A array with all the image filters
	 */
	public static final ImageFilter[] ALL_FILTERS = new ImageFilter[]{ new GreyScaleFilter(), new SmoothFilter(), new SobelFilter()};

	/**
	 * Return a hashmap with all the filters
	 * @return
	 */
	public static HashMap<String,ImageFilter> getFilterMap(){

		HashMap<String,ImageFilter> map = new HashMap<String,ImageFilter>();

		for(ImageFilter filter : ALL_FILTERS){

			map.put(filter.getName(), filter);
		}

		return map;
	}

	static class SmoothFilter implements ImageFilter{

		private String[] parameters = new String[]{"Passes"};


		@Override
		public void apply(BufferedImage img, float... values) {

			int passes = 1;

			if(values.length > 0){
				passes = (int)Math.max(passes,Math.min(32, values[0]));
			}


			int width = img.getWidth();
			int height = img.getHeight();

			for(int i = 0 ; i < passes; i++){
				for(int x = 0; x < width-1; x++){
					for(int y = 0; y < height-1; y++){

						int t0 = getRgbAvg(img.getRGB(x, y),img.getRGB(x+1, y));
						int t1 = getRgbAvg(img.getRGB(x, y+1),t0);

						img.setRGB(x, y, t1);	
					}				
				}
			}
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "Smooth";
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Smoothes out the image";
		}

		@Override
		public String[] getParameters() {
			// TODO Auto-generated method stub
			return parameters;
		}

	}


	static class GreyScaleFilter implements ImageFilter{

		private String[] parameters = new String[]{"Brightness"};


		@Override
		public void apply(BufferedImage img, float... values) {
			// TODO Auto-generated method stub

			int width = img.getWidth();
			int height = img.getHeight();

			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){

					img.setRGB(x, y, getGreyscale(img.getRGB(x, y)));

				}				
			}


		}

		@Override
		public String getName() {
			return "Grey Scale";
		}

		@Override
		public String getDescription() {
			return "Turns the image into greyscale";
		}

		@Override
		public String[] getParameters() {
			return parameters;
		}

	}


	static class SobelFilter implements ImageFilter{

		private String[] parameters = new String[]{"Treshold"};

		@Override
		public void apply(BufferedImage img, float... values) {


			float max_dt = 5;

			if(values.length > 0){
				max_dt = Math.max(max_dt, values[0]);
			}

			int width = img.getWidth();
			int height = img.getHeight();

			for(int x = 0; x < width-1; x++){
				for(int y = 0; y < height-1; y++){

					int t0 = getRgbScalar(img.getRGB(x, y));
					int t1 = getRgbScalar(img.getRGB(x+1, y));
					int t2 = getRgbScalar(img.getRGB(x, y+1));

					int dt = Math.max(Math.abs(t0-t1), Math.abs(t0-t2));

					if(dt > max_dt){
						img.setRGB(x, y, 0xffffffff);		
					}else{		
						img.setRGB(x, y, 0x0);				
					}

				}				
			}


		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "Sobel Edge detection filter";
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Detects edges in the image, and colors them";
		}

		@Override
		public String[] getParameters() {
			// TODO Auto-generated method stub
			return parameters;
		}


	}





}
