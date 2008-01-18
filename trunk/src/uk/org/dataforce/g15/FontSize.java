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

import java.awt.Dimension;
import uk.org.dataforce.g15.fonts.G15Font;
import uk.org.dataforce.g15.fonts.Font_4x6;
import uk.org.dataforce.g15.fonts.Font_5x7;
import uk.org.dataforce.g15.fonts.Font_8x8;

public enum FontSize {
	/** This is for small text. */
	SMALL ('S', 0, new Font_4x6()),
	/** This is for medium text. */
	MEDIUM ('M', 1, new Font_5x7()),
	/** This is for large text. */
	LARGE ('L', 2, new Font_8x8());
	
	/** Character value of this enum. */
	final char myCharValue;
	/** Integer value of this enum. */
	final int myIntValue;
	/** G15Font of this font. */
	final G15Font myFont;
	
	/**
	 * Create a new FontSize.
	 *
	 * @param charValue Character value of this enum.
	 * @param intValue Integer value of this enum.
	 * @param G15Font font for this font.
	 */
	FontSize (final char charValue, final int intValue, final G15Font font) {
		myCharValue = charValue;
		myIntValue = intValue;
		myFont = font;
	}
	
	/**
	 * Get the Font of this enum.
	 *
	 * @return Font of this enum.
	 */
	public G15Font getFont() { return myFont; }
	
	/**
	 * Get the integer value of this enum.
	 *
	 * @return integer value of this enum.
	 */
	public int getIntValue() { return myIntValue; }

	/**
	 * Get the char value of this enum.
	 *
	 * @return char value of this enum.
	 */
	public char getCharValue() { return myCharValue; }
};