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

public enum FontSize {
	/** This is for small text. */
	SMALL ('S', 0),
	/** This is for medium text. */
	MEDIUM ('M', 1),
	/** This is for large text. */
	LARGE ('L', 2);
	
	/** Character value of this enum. */
	char myCharValue;
	/** Integer value of this enum. */
	int myIntValue;
	
	/**
	 * Create a new FontSize.
	 *
	 * @param charValue Character value of this enum.
	 * @param intValue Integer value of this enum.
	 */
	FontSize (char charValue, int intValue) {
		myCharValue = charValue;
		myIntValue = intValue;
	}
	
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