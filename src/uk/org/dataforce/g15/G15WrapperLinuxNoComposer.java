/*
 * Copyright (c) 2006-2007 Shane Mc Cormack
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
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;

/**
 * Linux commands for LCD Drawing.
 * This class relies on g15composer
 */
public class G15WrapperLinuxNoComposer extends G15Wrapper {
	/** This is the socket used for reading from/writing to the Daemon. */
	private Socket socket;
	/** Used for writing to the daemon. */
	private PrintWriter out;
	/** Used for reading from the daemon. */
	private BufferedReader in;

	/** Image used to draw on. */
	private BufferedImage image = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
	/** Graphics for the image */
	private Graphics2D graphicsArea = image.createGraphics();

	/** Previously drawn image. */
	private BufferedImage oldImage = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
	
	
	/**
	 * Create a new Linux G15 LCD Wrapper.
	 *
	 * @param pipeLocation Location of g15 socket file
	 */
	public G15WrapperLinuxNoComposer() {
		try {
			socket = new Socket("127.0.0.1", 15550);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.print("GBUF");
			clearScreen(false);
		} catch (Exception e) {
			throw new G15NotFoundException("Unable to open socket to G15Daemon");
		}
	}
	
	/** Close socket on destroy. */
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	/** Close socket. */
	public void close() {
		try { socket.close(); }
		catch (IOException e) { 
			System.out.println("Could not close socket");
		}
	}

	/**
	 * Convert a boolean into a char
	 *
	 * @param bool Boolean to convert
	 * @return white for false, black for true.
	 */
	private Color convertBoolean(boolean bool) {
		if (bool == true) {
			return Color.black;
		} else {
			return Color.white;
		}
	}

	/** Reset the drawing Image to the last drawn image. */
	public void clear() {
		image = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
		graphicsArea = image.createGraphics();
		graphicsArea.drawImage(oldImage, 0, 0, null);
	}

	/**
	 * Get the G15-Char for the given RGB value.
	 *
	 * @param rgb RGB Value (from getRGB)
	 * @return 0 for white, else 1.
	 */
	public char getChar(final int rgb) {
		if (rgb == Color.white.getRGB()) {
			return (char)0;
	//		return '0';
		} else {
			return (char)1;
	//		return '1';
		}
	}
	
	/**
	 * Get the RGB value for the given char
	 *
	 * @param in Char Value (from getChar or so)
	 * @return rgb for char. (white for 0, else black)
	 */
	public int getRGB(final char in) {
		if (in == 0 || in == '0') {
			return Color.white.getRGB();
		} else {
			return Color.black.getRGB();
		}
	}

	/**
	 * Draw to the screen
	 *
	 * @throws java.io.IOException Throws this if the socket is not able to be written to
	 */
	public void draw() throws IOException {
		oldImage = new BufferedImage(LCD_WIDTH, LCD_HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
		oldImage.createGraphics().drawImage(image, 0, 0, null);
		
		for (int y = 0; y < LCD_HEIGHT ; ++y) {
			for (int x = 0; x < LCD_WIDTH ; ++x) {
				out.printf("%c", getChar(oldImage.getRGB(x,y)));
			}
		}
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
	
	}
	
	/**
	 * Draw a line of text in the default position.
	 *
	 * @param size FontSize of text to draw.
	 * @param text line to draw
	 */
	public void drawText(FontSize size, String text) {

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

	}

	/**
	 * Loads a font into a font slot.
	 *
	 * @param fontSlot Which slot to load font into
	 * @param size Size of font
	 * @param pathToFont Path to font file
	 */
	public void loadFont(int fontSlot, FontSize size, String pathToFont) {

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
	public void drawFont(int fontSlot, FontSize size, Point point, boolean isBlack, G15Position position, String[] text) {

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
		if (pixels.length() < (width*height)) {
			System.out.println("[drawPixels] Not recieved enough pixels. Failing.");
		} else if (pixels.length() > (width*height)) {
			System.out.println("[drawPixels] Recieved more pixels than space to draw. This may look wrong!");
		}
		for (int y = 0; y < height ; ++y) {
			for (int x = 0; x < width ; ++x) {
				image.setRGB(x+point.x, y+point.y, getRGB(pixels.charAt((y*width)+x)));
			}
		}
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
		image.setRGB(point.x, point.y, convertBoolean(isBlack).getRGB());
	}

	/**
	 * Clear the screen and set it all to a specified colour
	 *
	 * @param isBlack True to set to black, false to set to white
	 */
	public void clearScreen(boolean isBlack) {
		graphicsArea.setColor(convertBoolean(isBlack));
		graphicsArea.fillRect(0, 0, LCD_WIDTH, LCD_HEIGHT);
	}

	/**
	 * Fill a specified area in either white or black
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 * @param isBlack True to set to black, false to set to white
	 */
	public void fillArea(Point point1, Point point2, boolean isBlack) {
		graphicsArea.setColor(convertBoolean(isBlack));
		graphicsArea.fillRect(point1.x, point1.y, point2.x-point1.x, point2.y-point1.y);
	}

	/**
	 * Reverse all the pixels in a specific area
	 *
	 * @param point1 Point to start at for area
	 * @param point2 Point to finish at for area
	 */
	public void reversePixels(Point point1, Point point2) {
		for (int x = point1.x ; x <= point2.x ; ++x) {
			for (int y = point1.y; y <= point2.y ; ++y) {
				setPixelColour(new Point(x, y), (image.getRGB(x, y) == Color.white.getRGB()));
			}
		}
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
		Stroke oldStroke = graphicsArea.getStroke();
		graphicsArea.setStroke(new BasicStroke(thickness));
	
		graphicsArea.setColor(convertBoolean(isBlack));
		graphicsArea.drawRect(point1.x, point1.y, point2.x-point1.x, point2.y-point1.y);
		
		graphicsArea.setStroke(oldStroke);
	}

	/**
	 * Draw a line
	 *
	 * @param point1 Point to start at for line
	 * @param point2 Point to finish at for line
	 * @param isBlack True to set to black, false to set to white
	 */
	public void drawLine(Point point1, Point point2, boolean isBlack) {
		graphicsArea.setColor(convertBoolean(isBlack));
		graphicsArea.drawLine(point1.x, point1.y, point2.x, point2.y);
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
		graphicsArea.setColor(convertBoolean(isBlack));
		
		graphicsArea.drawOval(center.x-radius, center.y-radius, radius*2, radius*2);
		if (filled) {
			graphicsArea.drawOval(center.x-radius, center.y-radius, radius*2, radius*2);
		}
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
		graphicsArea.setColor(convertBoolean(isBlack));
		graphicsArea.drawRoundRect(point1.x, point1.y, point2.x-point1.x, point2.y-point1.y, 2, 2);
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

	}

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
	public void screenPosition(G15ScreenPosition position) {

	}
}