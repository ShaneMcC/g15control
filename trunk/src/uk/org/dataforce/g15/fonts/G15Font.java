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

public abstract class G15Font {
	/** Font Data for this font */
	char[] fontdata;

	/** Font Size */
	Dimension size;
	
	/** Create a new G15Font */
	public G15Font() { }

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
		if (x >= size.width || y >= size.height || pos >= fontdata.length) {
			return Color.white;
		} else if (fontdata[pos + y * size.width + x] == 0x01) {
			return Color.black;
		} else {
			return Color.white;
		}
	}
	
	/**
	 * Get the size of this font.
	 *
	 * @return size of this font.
	 */
	public Dimension getSize() {
		return new Dimension(size);
	}
}