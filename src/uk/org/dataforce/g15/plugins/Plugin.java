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
 *
 * Based on Plugin code from DMDirc (DMDirc.com)
 */
package uk.org.dataforce.g15.plugins;

import uk.org.dataforce.g15.G15Control;
import uk.org.dataforce.g15.G15Wrapper;

public abstract class Plugin implements Comparable<Plugin> {
	/**
	 * Called when the plugin is loaded.
	 *
	 * @param control The G15Control that owns this plugin
	 * @param wrapper The screen that this plugin owns
	 */
	public abstract void onLoad(G15Control control, G15Wrapper wrapper);
	
	/**
	 * Called if the screen changes.
	 *
	 * @param wrapper The screen that this plugin now owns
	 */
	public abstract void changeScreen(G15Wrapper wrapper);
	
	/**
	 * Called when the plugin is about to be unloaded.
	 */
	public abstract void onUnload();
	
	/**
	 * Called every 1/2 second for drawing related tasks when this screen is active.
	 */
	public abstract void onRedraw();
	
	/**
	 * Called when this plugin becomes active.
	 * This needs to FULLY redraw the screen.
	 */
	public abstract void onActivate();
	
	/**
	 * Called when this plugin becomes active.
	 */
	public abstract void onDeactivate();
	
	/**
	 * Called when LCD Button 1 is pressed.
	 */
	public void onLCD1() { }
	
	/**
	 * Called when LCD Button 2 is pressed.
	 */
	public void onLCD2() { }
	
	/**
	 * Called when LCD Button 3 is pressed.
	 */
	public void onLCD3() { }
		
	/**
	 * Called when LCD Button 4 is pressed.
	 */
	public void onLCD4() { }
	
	/**
	 * Check any further Prerequisites for this plugin to load that can not be
	 * checked using metainfo.
	 *
	 * @return true if the plugin can be used, else false.
	 */
	public boolean checkPrerequisites() { return true; }
	
	/**
	 * Compares this object with the specified object for order.
	 * Returns a negative integer, zero, or a positive integer as per String.compareTo();
	 *
	 * @param o Object to compare to
	 * @return a negative integer, zero, or a positive integer.
	 */
	public int compareTo(Plugin o) {
		return toString().compareTo(o.toString());
	}
}
