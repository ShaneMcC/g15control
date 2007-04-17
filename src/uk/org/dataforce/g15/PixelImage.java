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

/**
 * Represents a Pixel-Based image for drawing using drawPixels.
 */
public class PixelImage {
	/** My Width */
	int myWidth;

	/** My Height */
	int myHeight;

	/** My Image */
	String myImage;

	/** Create a new PixelImage. */
	public PixelImage(int width, int height, String image) {
		if (image.length() < (width*height)) {
			System.out.println("[PixelImage] Not enough pixels given.");
		} else if (image.length() > (width*height)) {
			System.out.println("[PixelImage] Recieved more pixels than space to draw.");
		}
		myWidth = width;
		myHeight = height;
		myImage = image;
	}

	/**
	 * Get width.
	 *
	 * @return width of image
	 */
	public int width() { return myWidth; }

	/**
	 * Get height.
	 *
	 * @return height of image
	 */
	public int height() { return myHeight; }

	/**
	 * Get image.
	 *
	 * @return String of image
	 */
	public String toString() { return myImage; }
}