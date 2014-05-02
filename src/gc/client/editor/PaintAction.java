package gc.client.editor;

import gc.client.gui.views.ImageDisplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;


/**
 * A ImageAction to handle painting
 *
 */
public class PaintAction extends ImageAction {

	private Stroke stroke;
	private int thickness;
	private Color color;
	private float density;

	private int start_x, start_y;
	private int px, py;

	private boolean active;


	public PaintAction(ImageDisplay in) {
		super(in);
		thickness = 5;
		stroke = new BasicStroke(thickness);
		color = new Color(0,0,0);
		density = 1;

	}

	
	/**
	 * Sets the paint active
	 * @param in true if active
	 */
	public void setActive(boolean in){

		this.active = in;

		calculateDensity();
	}

	
	/**
	 * Set the brush color
	 * @param in
	 */
	public void setColor(Color in){
		this.color = in;		
	}

	
	/**
	 * Set the stroke width and type
	 * @param in
	 * @param stroke_type
	 */
	public void setWidth(int in, String stroke_type){

		thickness = in;

		int stroke_width = (int)((float)in*density);


		switch(stroke_type){

		case "Double Stroke":
			stroke = new DoubleStroke(stroke_width,stroke_width/4);
			break;
		case "Control Point Stroke":
			stroke = new ControlPointsStroke(stroke_width);
			break;
		case "Sloppy Stroke":
			stroke = new SloppyStroke(stroke_width,stroke_width/2);
			break;

		default:
			stroke = new BasicStroke(stroke_width);	
		}

		
	}

	
	private void calculateDensity(){


		int mul = Math.min(this.getImage().getWidth(), this.getImage().getHeight() );				
		double l = Math.sqrt(mul*mul);

		density = (float)l/1000.0f;			
	}

	
	@Override
	public void mouseDragged(int x, int y, MouseEvent e) {

		if(!active){
			return;
		}

		this.getImageGraphics().setColor(color);
		this.getImageGraphics().setStroke(stroke);
		this.getImageGraphics().drawLine(start_x, start_y, x, y);
		draw();

		start_x = x;
		start_y = y;
	}

	@Override
	public void mouseMoved(int x, int y, MouseEvent e) {

		if(!active){
			return;
		}

		px = e.getX();
		py = e.getY();

		draw();
	}


	@Override
	public void mousePressed(int x, int y, MouseEvent e) {

		start_x = x;
		start_y = y;	

	}

	
	@Override
	public void paint(Graphics2D g) {

		if(!active){
			return;
		}

		int radius = thickness/2;
		int sel = radius/2;

		g.setColor(color);
		g.drawOval(px-sel, py-sel, radius, radius);

	}

	
	@Override
	public void mouseWheelMoved(int dx) {}

	@Override
	public void mouseClicked(int x, int y, MouseEvent e) {}

	@Override
	public void mouseReleased(int x, int y, MouseEvent e) {}

	@Override
	public void mouseEntered(int x, int y, MouseEvent e) {}

	@Override
	public void mouseExited(int x, int y, MouseEvent e) {}






	/////// Stroke examples from www.java2s.com /////////		
	/**
	 * This Stroke implementation does nothing. Its createStrokedShape() method
	 * returns an unmodified shape. Thus, drawing a shape with this Stroke is the
	 * same as filling that shape!
	 */

	class NullStroke implements Stroke {
		public Shape createStrokedShape(Shape s) {
			return s;
		}
	}

	/**
	 * This Stroke implementation applies a BasicStroke to a shape twice. If you
	 * draw with this Stroke, then instead of outlining the shape, you're outlining
	 * the outline of the shape.
	 */

	class DoubleStroke implements Stroke {
		BasicStroke stroke1, stroke2; // the two strokes to use

		public DoubleStroke(float width1, float width2) {
			stroke1 = new BasicStroke(width1); // Constructor arguments specify
			stroke2 = new BasicStroke(width2); // the line widths for the strokes
		}

		public Shape createStrokedShape(Shape s) {
			// Use the first stroke to create an outline of the shape
			Shape outline = stroke1.createStrokedShape(s);
			// Use the second stroke to create an outline of that outline.
			// It is this outline of the outline that will be filled in
			return stroke2.createStrokedShape(outline);
		}
	}

	/**
	 * This Stroke implementation strokes the shape using a thin line, and also
	 * displays the end points and Bezier curve control points of all the line and
	 * curve segments that make up the shape. The radius argument to the constructor
	 * specifies the size of the control point markers. Note the use of PathIterator
	 * to break the shape down into its segments, and of GeneralPath to build up the
	 * stroked shape.
	 */

	class ControlPointsStroke implements Stroke {
		float radius; // how big the control point markers should be

		public ControlPointsStroke(float radius) {
			this.radius = radius;
		}

		public Shape createStrokedShape(Shape shape) {
			// Start off by stroking the shape with a thin line. Store the
			// resulting shape in a GeneralPath object so we can add to it.
			GeneralPath strokedShape = new GeneralPath(new BasicStroke(radius/6f)
			.createStrokedShape(shape));

			// Use a PathIterator object to iterate through each of the line and
			// curve segments of the shape. For each one, mark the endpoint and
			// control points (if any) by adding a rectangle to the GeneralPath
			float[] coords = new float[6];
			for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i
					.next()) {
				int type = i.currentSegment(coords);
				Shape s = null, s2 = null, s3 = null;
				switch (type) {
				case PathIterator.SEG_CUBICTO:
					markPoint(strokedShape, coords[4], coords[5]); // falls through
				case PathIterator.SEG_QUADTO:
					markPoint(strokedShape, coords[2], coords[3]); // falls through
				case PathIterator.SEG_MOVETO:
				case PathIterator.SEG_LINETO:
					markPoint(strokedShape, coords[0], coords[1]); // falls through
				case PathIterator.SEG_CLOSE:
					break;
				}
			}

			return strokedShape;
		}

		/** Add a small square centered at (x,y) to the specified path */
		void markPoint(GeneralPath path, float x, float y) {
			path.moveTo(x - radius, y - radius); // Begin a new sub-path
			path.lineTo(x + radius, y - radius); // Add a line segment to it
			path.lineTo(x + radius, y + radius); // Add a second line segment
			path.lineTo(x - radius, y + radius); // And a third
			path.closePath(); // Go back to last moveTo position
		}
	}

	/**
	 * This Stroke implementation randomly perturbs the line and curve segments that
	 * make up a Shape, and then strokes that perturbed shape. It uses PathIterator
	 * to loop through the Shape and GeneralPath to build up the modified shape.
	 * Finally, it uses a BasicStroke to stroke the modified shape. The result is a
	 * "sloppy" looking shape.
	 */

	class SloppyStroke implements Stroke {
		BasicStroke stroke;

		float sloppiness;

		public SloppyStroke(float width, float sloppiness) {
			this.stroke = new BasicStroke(width); // Used to stroke modified shape
			this.sloppiness = sloppiness; // How sloppy should we be?
		}

		public Shape createStrokedShape(Shape shape) {
			GeneralPath newshape = new GeneralPath(); // Start with an empty shape

			// Iterate through the specified shape, perturb its coordinates, and
			// use them to build up the new shape.
			float[] coords = new float[6];
			for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i
					.next()) {
				int type = i.currentSegment(coords);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					perturb(coords, 2);
					newshape.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					perturb(coords, 2);
					newshape.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO:
					perturb(coords, 4);
					newshape.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					perturb(coords, 6);
					newshape.curveTo(coords[0], coords[1], coords[2], coords[3],
							coords[4], coords[5]);
					break;
				case PathIterator.SEG_CLOSE:
					newshape.closePath();
					break;
				}
			}

			// Finally, stroke the perturbed shape and return the result
			return stroke.createStrokedShape(newshape);
		}

		// Randomly modify the specified number of coordinates, by an amount
		// specified by the sloppiness field.
		void perturb(float[] coords, int numCoords) {
			for (int i = 0; i < numCoords; i++)
				coords[i] += (float) ((Math.random() * 2 - 1.0) * sloppiness);
		}
	}



}
