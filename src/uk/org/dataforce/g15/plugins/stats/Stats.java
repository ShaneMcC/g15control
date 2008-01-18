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
package uk.org.dataforce.g15.plugins.stats;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.org.dataforce.g15.plugins.Plugin;
import uk.org.dataforce.g15.G15Control;
import uk.org.dataforce.g15.G15Position;
import uk.org.dataforce.g15.FontSize;
import uk.org.dataforce.g15.G15Wrapper;

public class Stats implements Plugin {
	/** The drawing screen. */
	G15Wrapper myScreen;
	
	/** The controller. */
	G15Control myController;	
	
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
		myScreen.clearScreen(false);
		final ArrayList<String> outputString = new ArrayList<String>();
	
		final DateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy HH:mm:ss zz");
		final String date = dateFormat.format(new Date());
	
		String[] uptime;
		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/uptime"));
			uptime = in.readLine().split(" ");
			in.close();
		} catch (IOException e) {
			uptime = new String[]{"0.0", "0.0"};
		}
		
		outputString.add(date);
		try {
			outputString.add("Uptime: "+duration(Integer.parseInt(uptime[0].split("\\.")[0])));
		} catch (NumberFormatException e) {
			outputString.add("Uptime: Unknown");
		}
		
		String[] load;
		try {
			BufferedReader in = new BufferedReader(new FileReader("/proc/loadavg"));
			load = in.readLine().split(" ");
			in.close();
		} catch (IOException e) {
			load = new String[]{"0.00", "0.00", "0.00"};
		}
		outputString.add("Load: "+load[0]+", "+load[1]+", "+load[2]);
	
		myScreen.drawText(FontSize.SMALL, new Point(0, 0), G15Position.CENTER, outputString.toArray(new String[0]));
		myScreen.silentDraw();
	}
	
	/**
	 * Get the duration in seconds as a string.
	 *
	 * @param secondsInput to get duration for
	 * @return Duration as a string
	 */
	private String duration(int secondsInput) {
		final StringBuilder result = new StringBuilder();
		final int weeks = (secondsInput / 86400 / 7);
		final int days = (secondsInput / 86400 % 7);
		final int hours = (secondsInput / 3600 % 24);
		final int minutes = (secondsInput / 60 % 60);
		final int seconds = (secondsInput % 60);
		
		if (weeks > 0) { result.append(weeks+"w "); }
		if (days > 0) { result.append(days+"d "); }
		if (hours > 0) { result.append(hours+"h "); }
		if (minutes > 0) { result.append(minutes+"m "); }
		if (seconds > 0) { result.append(seconds+"s "); }
		
		return result.toString().trim();
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
	public String[] getInformation() { return new String[]{"Dataforce", "Stats display"}; }
}