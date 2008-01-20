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

import java.awt.Point;
import java.io.IOException;

/**
 * Base class for all LCD Wrappers.
 * All wrapper msut implement the abstract methods here, and may implement
 * additional methods if they desire.
 */
public abstract class G15Wrapper {
	protected static final int LCD_WIDTH = 160; // 0-159 (160)
	protected static final int LCD_HEIGHT = 43; // 0-42 (43)
	
	/** Get the width of the LCD. */
	public static final int getWidth() { return LCD_WIDTH; }
	
	/** Get the Height of the LCD. */
	public static final int getHeight() { return LCD_HEIGHT; }
	
	/** Get the point for the top-left of the screen */
	public static final Point getTopLeftPoint() { return new Point(0, 0); }	
	
	/** Get the point for the top-right of the screen */
	public static final Point getTopRightPoint() { return new Point(LCD_WIDTH-1, 0); }	
	
	/** Get the point for the bottom-right of the screen */
	public static final Point getBottomRightPoint() { return new Point(LCD_WIDTH-1, LCD_HEIGHT-1); }	
	
	/** Get the point for the bottom-left of the screen */
	public static final Point getBottomLeftPoint() { return new Point(0, LCD_HEIGHT-1); }
	
	/**
	 * Sleep for given numer of milliseconds
	 *
	 * @param number of milliseconds to sleep for.
	 */
	public static final void waitFor(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {}
	}
	
	/**
	 * Draw to the screen
	 *
	 * @throws java.io.IOException Throws this if the socket is not able to be written to
	 */
	public abstract void draw() throws IOException;
	
	/**
	 * Draw to the screen without throwing an exception.
	 *
	 * @return true if draw was successful, else false
	 */
	public abstract boolean silentDraw();
	
	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text String[] of lines to draw
	 */
	public abstract void drawText(FontSize size, String[] text);
	
	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text String[] of lines to draw
	 */
	public abstract void drawText(FontSize size, Point point, G15Position position, String[] text);
	
	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text line to draw
	 */
	public abstract void drawText(FontSize size, String text);
	
	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text line to draw
	 */
	public abstract void drawText(FontSize size, Point point, G15Position position, String text);
		
	/**
	 * Loads a font into a font slot.
	 *
	 * @param fontSlot Which slot to load font into
	 * @param size Size of font
	 * @param pathToFont Path to font file
	 */
	public abstract void loadFont(int fontSlot, int size, String pathToFont);
	
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
	public abstract void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String text);
	
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
	public abstract void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String[] text);
	
	/**
	 * Draws a pixel image of the given Width and Height at the given point
	 *
	 * @param point Location to draw image
	 * @param width Width of image
	 * @param height height of image
	 * @param pixels String containing image as a string of 0's and 1's (0 = white, 1 = black)
	 */
	public abstract void drawPixels(Point point, int width, int height, String pixels);
	
	/**
	 * Draws a pixel image of the given PixelImage
	 *
	 * @param point Location to draw image
	 * @param PixelImage PixelImage to draw.
	 */
	public abstract void drawPixels(Point point, PixelImage image);
	
	/**
	 * Set the colour of the pixel at a given point
	 *
	 * @param point Point to set pixel at
	 * @param isBlack True to set to black, false to set to white
	 */
	public abstract void setPixelColour(Point point, boolean isBlack);
	
	/**
	 * Clear the screen and set it all to a specified colour
	 *
	 * @param isBlack True to set to black, false to set to white
	 */
	public abstract void clearScreen(boolean isBlack);
	
	/**
	 * Fill a specified area in either white or black
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 */
	public abstract void fillArea(Point point1, Point point2, boolean isBlack);
	
	/**
	 * Reverse all the pixels in a specific area
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 */
	public abstract void reversePixels(Point point1, Point point2);
	
	/**
	 * Draw a box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param thickness Thickness of line
	 */
	public abstract void drawBox(Point point1, Point point2, boolean isBlack, int thickness);
	
	/**
	 * Draw a line
	 *
	 * @param point1 Point to start at for line
	 * @param point2 Point to finish at for line
	 * @param isBlack True to set to black, false to set to white
	 */
	public abstract void drawLine(Point point1, Point point2, boolean isBlack);
	
	/**
	 * Draw a circle
	 *
	 * @param center Center Point
	 * @param radius Radius of circle
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the circle filled or not
	 */
	public abstract void drawCircle(Point center, int radius, boolean isBlack, boolean filled);
	
	/**
	 * Draw a rounded box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the box filled or not
	 */
	public abstract void drawRoundedBox(Point point1, Point point2, boolean isBlack, boolean filled);
	
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
	public abstract void drawProgressBar(Point point1, Point point2, boolean isBlack, int position, int maxPosition, ProgressBarType barType);
	
	/**
	 * Set screen to foreground or background
	 *
	 * @param position Position of screen
	 */
	public abstract void screenPosition(G15ScreenPosition position);
	
	/**
	 * Set the MX Light on/off.
	 *
	 * @param light Which light to set (0 = all, 1,2,3 = M1 M2 M3)
	 * @param setOn true to turn on, false to turn off.
	 */
	public abstract boolean setMXLight(int light, boolean setOn);
	
	/**
	 * Set LCD Contrast Level.
	 *
	 * @param level Contrast level, (0 1 or 2)
	 */
	public abstract boolean setContrastLevel(int level);
	
	/**
	 * Set LCD Brightness Level.
	 *
	 * @param level Brightness level, (0 1 or 2)
	 */
	public abstract boolean setBrightnessLevel(int level);
}