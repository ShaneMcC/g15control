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
package uk.org.dataforce.g15.fonts;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Big Number font.
 *
 * Used for the clock plugin, based on ClockFont. (Its main user)
 */
public class Font_BigNumber extends G15Font {
	/** Is this font even height? */
	final boolean isEven;
	
	/** What is the midpoint of this font? */
	final int midpoint;
	
	/** Create a new Font_BigNumber */
	public Font_BigNumber(final int width, final int height) {
		super();
		size = new Dimension(width, height);
		fontdata = null;
		
		isEven = (size.height % 2) == 0;
		if (isEven) {
			midpoint = (size.height/2);
		} else {
			midpoint = (size.height/2)+1;
		}
	}
	
	/**
	 * Get the pixel colour for the requested character at the requested X, Y
	 * position.
	 *
	 * @param character Character number to get pixel data for,
	 * @param x X pixel of font to get data for
	 * @param y Y pixel of font to get data for
	 * @return Black/White color for font. (white if invalud position/char)
	 */
	public Color getPixelColor(final int character, final int x, final int y) {
		final int pos = character * size.width * size.height;
		if (x >= size.width || y >= size.height) {
			return Color.white;
		} else if ((character >= '0' && character <= '9') || character == ':') {
			// Work out if this pixel is black or white!
			if (character == ':') {
				if ((y >= 5 && y < 11) || (y < size.height-5 && y >= size.height-11)) {
					if (x >= size.width-5) {
						return Color.black;
					}
				}
			} else {
				// Each of the numbers can be represented by 7 segments.
				// Top segment.
				if (y < 5) {
					switch (character) {
						case '2':
						case '3':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
						case '0':
							return Color.black;
					}
				}
				
				// Middle segment
				if (y >= midpoint-2 && y <= midpoint+2) {
					switch (character) {
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '8':
						case '9':
							return Color.black;
					}
				}
				
				// Bottom segment. (6 pixels high for even height)
				if ((isEven && y >= size.height-6) || (!isEven && y >= size.height-5)) {
					switch (character) {
						case '2':
						case '3':
						case '5':
						case '6':
						case '8':
						case '9':
						case '0':
							return Color.black;
					}
				}
				
				// Now the side segments.
				
				// Top
				if (y <= midpoint+2) {
					// Top Right
					if (x >= size.width-5) {
						switch (character) {
							case '1':
							case '2':
							case '3':
							case '4':
							case '7':
							case '8':
							case '9':
							case '0':
								return Color.black;
						}
					}
					// Top Left
					if (x < 5) {
						switch (character) {
							case '4':
							case '5':
							case '6':
							case '8':
							case '9':
							case '0':
								return Color.black;
						}
					}
				}
				
				// Bottom (Don't use else because we overlap the midpoint with top)
				if (y >= midpoint-2) {
					// Bottom Right
					if (x >= size.width-5) {
						switch (character) {
							case '1':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
							case '0':
								return Color.black;
						}
					}
					// Bottom Left
					if (x < 5) {
						switch (character) {
							case '2':
							case '6':
							case '8':
							case '0':
								return Color.black;
						}
					}
				}
				
				// No Color, return white below.
			}
		}
		
		return Color.white;
	}
}