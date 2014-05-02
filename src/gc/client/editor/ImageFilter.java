package gc.client.editor;

import java.awt.image.BufferedImage;


/**
 * ImageFilter interface.
 * All ImageFilters must implement this interface.
 *
 */
public interface ImageFilter {


	/**
	 * Applies the filter to the image
	 * @param img
	 * @param values
	 */
	public void apply(BufferedImage img, float ...values);

	/**
	 * Returns the filter name
	 * @return
	 */
	public String getName();

	/**
	 * Returns the filter description
	 * @return
	 */
	public String getDescription();

	/**
	 * Returns the filter parameters
	 * @return
	 */
	public String[] getParameters();
}
