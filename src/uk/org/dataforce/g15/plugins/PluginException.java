/*
 * Copyright (c) 2006-2008 Chris Smith, Shane Mc Cormack, Gregory Holmes
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
 * SVN: $Id: PluginException.java 2022 2007-09-12 00:35:14Z ShaneMcC $
 *
 * Based on Plugin code from DMDirc (DMDirc.com)
 */

package uk.org.dataforce.g15.plugins;

/**
 * Plugin Exception.
 *
 * @author            Shane Mc Cormack
 * @version           $Id: PluginException.java 2022 2007-09-12 00:35:14Z ShaneMcC $
 */
public class PluginException extends Exception {
	/**
	 * A version number for this class. It should be changed whenever the class
	 * structure is changed (or anything else that would prevent serialized
	 * objects being unserialized with the new class).
	 */
	private static final long serialVersionUID = 1;
	
	/**
	 * Create a new PluginException Exception.
	 *
	 * @param message Reason for exception
	 */
	public PluginException(final String message) { super(message); }
	
	/**
	 * Create a new PluginException Exception.
	 *
	 * @param message Reason for exception
	 * @param cause Cause of Exception
	 */
	public PluginException(final String message, final Throwable cause) { super(message, cause); }
	
	/**
	 * Get SVN Version information.
	 *
	 * @return SVN Version String
	 */
	public static String getSvnInfo() { return "$Id: PluginException.java 2022 2007-09-12 00:35:14Z ShaneMcC $"; }	
}
