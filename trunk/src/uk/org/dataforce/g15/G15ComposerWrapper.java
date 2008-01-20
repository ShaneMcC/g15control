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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * G15Composer Wrapper for LCD Drawing.
 * This class relies on g15composer
 */
public class G15ComposerWrapper extends G15Wrapper {
	/** Arraylist of stuff to draw. */
	private ArrayList<String> instructions = new ArrayList<String>();
	/** File output stream. */
	private FileOutputStream myOutput;

	/**
	 * Create a new G15Composer Wrapper.
	 *
	 * @param pipeLocation Location of g15 socket file
	 */
	public G15ComposerWrapper(String pipeLocation) {
		try {
			myOutput = new FileOutputStream(new File(pipeLocation));
		} catch (FileNotFoundException e) {
			throw new G15NotFoundException("Unable to open socket to G15Composer");
		}
	}

	/**
	 * Convert a boolean into an int
	 *
	 * @param bool Boolean to convert
	 * @return 0 for false, 1 for true.
	 */
	private int convertBoolean(boolean bool) {
		if (bool == true) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Draw to the screen
	 *
	 * @throws java.io.IOException Throws this if the socket is not able to be written to
	 */
	public void draw() throws IOException {
		for (String line: instructions) {
			myOutput.write((line + "\n").getBytes());
		}
		myOutput.flush();
		instructions.clear();
	}
	
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
	public void drawText(FontSize size, String[] text) {
		StringBuilder builder = new StringBuilder();
		for (String line: text) {
			builder.append(" \"" + line.replaceAll("\"", "\\\"") + "\"");
		}
		instructions.add('T' + size.getCharValue() + builder.toString());
	}

	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text String[] of lines to draw
	 */
	public void drawText(FontSize size, Point point, G15Position position, String[] text) {
		StringBuilder builder = new StringBuilder();
		for (String line: text) {
			builder.append(" \"" + line.replaceAll("\"", "\\\"") + "\"");
		}
		instructions.add("TO " + (int)point.getX() + ' ' + (int)point.getY() + ' ' + size.getIntValue() + ' ' + position.getIntValue() + ' ' + builder.toString());
	}
	
	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text line to draw
	 */
	public void drawText(FontSize size, String text) {
		instructions.add('T' + size.getCharValue() + '"' + text.replaceAll("\"", "\\\"") + '"');
	}

	/**
	 * Draw a line of text to a specific position.
	 *
	 * @param size FontSize of text to draw.
	 * @param point Location to draw text
	 * @param position G15Position to draw text (left, right, center)
	 * @param text line to draw
	 */
	public void drawText(FontSize size, Point point, G15Position position, String text) {
		instructions.add("TO " + (int)point.getX() + ' ' + (int)point.getY() + ' ' + size.getIntValue() + ' ' + position.getIntValue() + ' ' + '"' + text.replaceAll("\"", "\\\"") + '"');
	}

	/**
	 * Loads a font into a font slot.
	 *
	 * @param fontSlot Which slot to load font into
	 * @param size Size of font
	 * @param pathToFont Path to font file
	 */
	public void loadFont(int fontSlot, int size, String pathToFont) {
		instructions.add("FL " + fontSlot + ' ' + size + " \"" + pathToFont + "\"");
	}

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
	public void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String text) {
		instructions.add("FP " + fontSlot + ' ' + size + ' ' + (int)point.getX() + ' ' + (int)point.getY() + ' ' + convertBoolean(isBlack) + ' ' + position.getIntValue() + ' ' + '"' + text.replaceAll("\"", "\\\"") + '"');
	}

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
	public void drawFont(int fontSlot, int size, Point point, boolean isBlack, G15Position position, String[] text) {
		StringBuilder builder = new StringBuilder();
		for (String line: text) {
			builder.append(" \"" + line.replaceAll("\"", "\\\"") + "\"");
		}
		instructions.add("FP " + fontSlot + ' ' + size + ' ' + (int)point.getX() + ' ' + (int)point.getY() + ' ' + convertBoolean(isBlack) + ' ' + position.getIntValue() + ' ' + builder.toString());
	}

	/**
	 * Draws a pixel image of the given Width and Height at the given point
	 *
	 * @param point Location to draw image
	 * @param width Width of image
	 * @param height height of image
	 * @param pixels String containing image as a string of 0's and 1's (0 = white, 1 = black)
	 */
	public void drawPixels(Point point, int width, int height, String pixels) {
		instructions.add("PO " + (int)point.getX() + ' ' + (int)point.getY() + ' ' + width + ' ' + height + " \"" + pixels + '"');
	}
	
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
	public void setPixelColour(Point point, boolean isBlack) {
		instructions.add("PS " + (int)point.getX() + ' ' + (int)point.getY() + ' ' + convertBoolean(isBlack));
	}

	/**
	 * Clear the screen and set it all to a specified colour
	 *
	 * @param isBlack True to set to black, false to set to white
	 */
	public void clearScreen(boolean isBlack) {
		instructions.add("PC " + convertBoolean(isBlack));
	}

	/**
	 * Fill a specified area in either white or black
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 */
	public void fillArea(Point point1, Point point2, boolean isBlack) {
		instructions.add("PF " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY() + ' ' + convertBoolean(isBlack));
	}

	/**
	 * Reverse all the pixels in a specific area
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 */
	public void reversePixels(Point point1, Point point2) {
		instructions.add("PR " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY());
	}

	/**
	 * Draw a box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param thickness Thickness of line
	 */
	public void drawBox(Point point1, Point point2, boolean isBlack, int thickness) {
		instructions.add("PB " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY() + ' ' + convertBoolean(isBlack) + ' ' + thickness + " 0");
	}

	/**
	 * Draw a line
	 *
	 * @param point1 Point to start at for line
	 * @param point2 Point to finish at for line
	 * @param isBlack True to set to black, false to set to white
	 */
	public void drawLine(Point point1, Point point2, boolean isBlack) {
		instructions.add("DL " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY() + ' ' + convertBoolean(isBlack));
	}

	/**
	 * Draw a circle
	 *
	 * @param center Center Point
	 * @param radius Radius of circle
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the circle filled or not
	 */
	public void drawCircle(Point center, int radius, boolean isBlack, boolean filled) {
		instructions.add("DC " + (int)center.getX() + ' ' + (int)center.getY() + ' ' + radius + ' ' + convertBoolean(isBlack) + ' ' + convertBoolean(filled));
	}

	/**
	 * Draw a rounded box
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 * @param filled Is the box filled or not
	 */
	public void drawRoundedBox(Point point1, Point point2, boolean isBlack, boolean filled) {
		instructions.add("DR " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY() + ' ' + convertBoolean(isBlack) + ' ' + convertBoolean(filled));
	}

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
	public void drawProgressBar(Point point1, Point point2, boolean isBlack, int position, int maxPosition, ProgressBarType barType) {
		instructions.add("DB " + (int)point1.getX() + ' ' + (int)point1.getY() + ' ' + (int)point2.getX() + ' ' + (int)point2.getY() + ' ' + convertBoolean(isBlack) + ' ' + position + ' ' + maxPosition + ' ' + barType.getIntValue());
	}

	/**
	 * Send an LCD mode. Not supported, see man g15composer.
	 *
	 * @param type Type of mode (C, X, R etc)
	 * @param value Value to set mode to
	 */
	public void sendMode(char type, int value) {
		instructions.add("M" + type + ' ' + value);
	}
	
	/**
	 * Set the MX Light on/off.
	 *
	 * @param light Which light to set (0 = all, 1,2,3 = M1 M2 M3)
	 * @param setOn true to turn on, false to turn off.
	 */
	public boolean setMXLight(int light, boolean setOn) {
		final String line = "KM " + light + ' ' + convertBoolean(setOn);
		
		try {
			myOutput.write((line + "\n").getBytes());
			myOutput.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Set LCD Contrast Level.
	 *
	 * @param level Contrast level, (0 1 or 2)
	 */
	public boolean setContrastLevel(int level) {
		final String line = "LC " + level;
		
		try {
			myOutput.write((line + "\n").getBytes());
			myOutput.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Set LCD Brightness Level.
	 *
	 * @param level Brightness level, (0 1 or 2)
	 */
	public boolean setBrightnessLevel(int level) {
		final String line = "LB " + level;
		
		try {
			myOutput.write((line + "\n").getBytes());
			myOutput.flush();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Set screen to foreground or background
	 *
	 * @param position Position of screen
	 */
	public void screenPosition(G15ScreenPosition position) {
		instructions.add("MP " + position.getIntValue());
	}
}