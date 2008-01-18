/*
 * Copyright (c) 2006-2008 Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * SVN: $Id$
 */
package uk.org.dataforce.g15;
import java.io.IOException;
import java.awt.Point;

/**
 * Windows commands for LCD Drawing.
 * This class has not been implemented yet.
 */
public class G15WrapperWindows extends G15Wrapper {
	/** Clear the drawing commands. */
	public void clear() { /* Not Implemented */ }
	
	/**
	 * Draw to the screen
	 *
	 * @throws java.io.IOException Throws this if the socket is not able to be written to
	 */
	public void draw() throws IOException { /* Not Implemented */ }

	/**
	 * Draw to the screen without throwing an exception.
	 *
	 * @return true if draw was successful, else false
	 */
	public boolean silentDraw() {
		try {
			draw();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text String[] of lines to draw
	 */
	public void drawText(FontSize size, String[] text) { /* Not Implemented */ }
	
	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text String of line to draw
	 */
	public void drawText(FontSize size, String text) { /* Not Implemented */ }
	
	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text String[] of lines to draw
	 */
	public void drawText(FontSize size, Point point, G15Position position, String[] text) { /* Not Implemented */ }
	
	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text String of line to draw
	 */
	public void drawText(FontSize size, Point point, G15Position position, String text) { /* Not Implemented */ }
		
	/**
	 * Loads a font into a font slot.
	 *
	 * @param fontSlot Which slot to load font into
	 * @param size Size of font
	 * @param pathToFont Path to font file
	 */
	public void loadFont(int fontSlot, int size, String pathToFont) { /* Not Implemented */ }
	
	/**
	 * Draw text using specified font
	 *
	 * @param fontSlot Font slot for font
	 * @param size size of font
	 * @param point location to draw text at
	 * @param isBlack True for bacl text, false for white
	 * @param position Position for text
	 * @param text Text to output
	 */
	public void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String text) { /* Not Implemented */ }
	
	/**
	 * Draw text using specified font
	 *
	 * @param fontSlot Font slot for font
	 * @param size size of font
	 * @param point location to draw text at
	 * @param isBlack True for bacl text, false for white
	 * @param position Position for text
	 * @param text Text to output
	 */
	public void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String[] text) { /* Not Implemented */ }
	
	/**
	 * Draws a pixel image of the given Width and Height at the given point
	 *
	 * @param point Location to draw image
	 * @param width Width of image
	 * @param height height of image
	 * @param pixels String containing image as a string of 0's and 1's (0 = white, 1 = black)
	 */
	public void drawPixels(Point point, int width, int height, String pixels) { /* Not Implemented */ }
	
	/**
	 * Draws a pixel image of the given PixelImage
	 *
	 * @param point Location to draw image
	 * @param PixelImage PixelImage to draw.
	 */
	public void drawPixels(Point point, PixelImage image) {
		drawPixels(point, image.width(), image.height(), image.toString());
	}
	
	/**
	 * Set the colour of the pixel at a given point
	 *
	 * @param point Point to set pixel at
	 * @param isBlack True to set to black, false to set to white
	 */
	public void setPixelColour(Point point, boolean isBlack) { /* Not Implemented */ }
	
	/**
	 * Clear the screen and set it all to a specified colour
	 *
	 * @param isBlack True to set to black, false to set to white
	 */
	public void clearScreen(boolean isBlack) { /* Not Implemented */ }
	
	/**
	 * Fill a specified area in either white or black
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 */
	public void fillArea(Point point1, Point point2, boolean isBlack) { /* Not Implemented */ }
	
	/**
	 * Reverse all the pixels in a specific area
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 */
	public void reversePixels(Point point1, Point point2) { /* Not Implemented */ }
	
	/**
	 * Draw a box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param thickness Thickness of line
	 */
	public void drawBox(Point point1, Point point2, boolean isBlack, int thickness) { /* Not Implemented */ }
	
	/**
	 * Draw a line
	 *
	 * @param point1 Point to start at for line
	 * @param point2 Point to finish at for line
	 * @param isBlack True to set to black, false to set to white
	 */
	public void drawLine(Point point1, Point point2, boolean isBlack) { /* Not Implemented */ }
	
	/**
	 * Draw a circle
	 *
	 * @param center Center Point
	 * @param radius Radius of circle
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the circle filled or not
	 */
	public void drawCircle(Point center, int radius, boolean isBlack, boolean filled) { /* Not Implemented */ }
	
	/**
	 * Draw a rounded box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the box filled or not
	 */
	public void drawRoundedBox(Point point1, Point point2, boolean isBlack, boolean filled) { /* Not Implemented */ }
	
	/**
	 * Draw a progress bar
	 *
	 * @param point1 Point to start at
	 * @param point2 point to finish at
	 * @param isBlack True to set to black, false to set to white
	 * @param position Position of the bar
	 * @param maxPosition Max Position of the bar
	 * @param barType Type of progress bar
	 */
	public void drawProgressBar(Point point1, Point point2, boolean isBlack, int position, int maxPosition, ProgressBarType barType) { /* Not Implemented */ }
	
	/**
	 * Set the MX Light on/off.
	 *
	 * @param light Which light to set (0 = all, 1,2,3 = M1 M2 M3)
	 * @param setOn true to turn on, false to turn off.
	 */
	public boolean setMXLight(int light, boolean setOn) {
		return false;
	}
	
	/**
	 * Set LCD Contrast Level.
	 *
	 * @param level Contrast level, (0 1 or 2)
	 */
	public boolean setContrastLevel(int level) {
		return false;
	}
	
	/**
	 * Set LCD Brightness Level.
	 *
	 * @param level Brightness level, (0 1 or 2)
	 */
	public boolean setBrightnessLevel(int level) {
		return false;
	}
	
	/**
	 * Set screen to foreground or background
	 *
	 * @param position Position of screen
	 */
	public void screenPosition(G15ScreenPosition position) { /* Not Implemented */ }
}