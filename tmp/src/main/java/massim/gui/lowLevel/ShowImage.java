package massim.gui.lowLevel;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class ShowImage extends Panel {

	private static final long serialVersionUID = -2572232127494161711L;

	BufferedImage  image;
		
	int width, height;

	/**
	 * adds image from the given path, returns Panel! (container.add(panel);)
	 * @param imageName path to image
	 * @param width		size of image (should be the same as preferred size of window)
	 * @param height
	 */
	public ShowImage(String imageName, int width, int height) {
		
		this.width = width;
		this.height = height;
		
		try {
			File input = new File(imageName);
			image = ImageIO.read(input);
			
	    } catch (IOException ie) {
	      System.out.println("Error:"+ie.getMessage());
	    }
	}
	
	/**
	 * should paint it, this is not explicitly called
	 */
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, width, height, null);
	}
	
	
}