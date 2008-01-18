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
package uk.org.dataforce.g15.plugins.clock;

import java.awt.Point;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.org.dataforce.g15.plugins.Plugin;
import uk.org.dataforce.g15.G15Control;
import uk.org.dataforce.g15.G15Position;
import uk.org.dataforce.g15.FontSize;
import uk.org.dataforce.g15.G15Wrapper;

public class Clock implements Plugin {
	/** The drawing screen. */
	G15Wrapper myScreen;
	
	/** The controller. */
	G15Control myController;	
	
	/** Progress Bar position */
	int progressPos = 0;
	
	/**
	 * Drawing Mode.
	 *
	 * 0 = Small
	 * 1 = Medium
	 * 2 = Large
	 * 3 = Full screen
	 */
	int drawingMode = 3;
	
	/** Draw seconds or not. */
	boolean drawSeconds = true;
	
	/** clearScreen before drawing or not. */
	boolean clearScreen = false;	

	/**
	 * Called when the plugin is loaded.
	 *
	 * @param control The G15Control that owns this plugin
	 * @param wrapper The screen that this plugin owns
	 */
	public void onLoad(G15Control control, G15Wrapper wrapper){
		myController = control;
		myScreen = wrapper;
	}
	
	/**
	 * Called if the screen changes.
	 *
	 * @param wrapper The screen that this plugin now owns
	 */
	public void changeScreen(G15Wrapper wrapper) {
		myScreen = wrapper;
	}
	
	/**
	 * Called when the plugin is about to be unloaded.
	 */
	public void onUnload() { }
	
	/**
	 * Called every 1/2 second for drawing related tasks when this screen is active.
	 */
	public void onRedraw() {
		DateFormat dateFormat;
		if (clearScreen) {
			myScreen.clearScreen(false);
			clearScreen = false;
		}
		if (drawSeconds) {
			dateFormat = new SimpleDateFormat("HH:mm:ss");
		} else {
			dateFormat = new SimpleDateFormat("HH:mm");
		}
		if (drawingMode < 3) {
			FontSize fontSize = FontSize.LARGE;
			switch (drawingMode) {
				case 0: fontSize = FontSize.SMALL; break;
				case 1: fontSize = FontSize.MEDIUM; break;
			}
			myScreen.drawText(fontSize, new Point(0, (myScreen.getHeight()/2)-5), G15Position.CENTER, dateFormat.format(new Date()));	
		} else {
			final String text = dateFormat.format(new Date());
			final String largeSpace = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
			int xPos = 0;
			if (!drawSeconds) {
				xPos = ClockFont.LARGE_WIDTH+14;
			}
			for (int i = 0; i < text.length(); ++i) {
				if (i > 0) {
					myScreen.drawPixels(new Point(xPos,0), 3, ClockFont.LARGE_HEIGHT, largeSpace);
					xPos = xPos+3;
				}
				int number;
				try { number = Integer.parseInt(""+text.charAt(i)); }
				catch (NumberFormatException e) { number = -1; }
				myScreen.drawPixels(new Point(xPos,0), ClockFont.LARGE_WIDTH, ClockFont.LARGE_HEIGHT, ClockFont.getIntFont(number));
				xPos = xPos+ClockFont.LARGE_WIDTH;
			}
		}
		myScreen.silentDraw();
	}
	
	/**
	 * Called when this plugin becomes active.
	 * This needs to FULLY redraw the screen.
	 */
	public void onActivate() {
		onRedraw();
	}
	
	/**
	 * Called when this plugin becomes active.
	 */
	public void onDeactivate() { }
	
	/**
	 * Called when LCD Button 1 is pressed.
	 */
	public void onLCD1() {
		++drawingMode;
		if (drawingMode > 3) { drawingMode = 0; }
		clearScreen = true;
	}
	
	/**
	 * Called when LCD Button 2 is pressed.
	 */
	public void onLCD2() {
		--drawingMode;
		if (drawingMode < 0) { drawingMode = 3; }
		clearScreen = true;
	}
	
	/**
	 * Called when LCD Button 3 is pressed.
	 */
	public void onLCD3() {
		drawSeconds = !drawSeconds;
		clearScreen = true;
	}
		
	/**
	 * Called when LCD Button 4 is pressed.
	 */
	public void onLCD4() { }
	
	/**
	 * Get the plugin version.
	 *
	 * @return Plugin Version
	 */
	public int getVersion() { return 1; }
	
	/**
	 * Get information about the plugin.
	 * result[0] == Author
	 * result[1] == Description
	 *
	 * @return Information about the plugin
	 */
	public String[] getInformation() { return new String[]{"Dataforce", "Example Clock Plugin"}; }
}