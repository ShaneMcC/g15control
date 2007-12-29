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
package uk.org.dataforce.g15.plugins.songbird;

import java.awt.Point;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.org.dataforce.g15.plugins.Plugin;
import uk.org.dataforce.g15.G15Control;
import uk.org.dataforce.g15.G15Position;
import uk.org.dataforce.g15.FontSize;
import uk.org.dataforce.g15.PixelImage;
import uk.org.dataforce.g15.ProgressBarType;
import uk.org.dataforce.g15.G15Wrapper;
import uk.org.dataforce.g15.G15WrapperLinux;
import uk.org.dataforce.g15.XMLParser;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;

public class Songbird implements Plugin {
	/** Default Menu Buttons */
	private String[] defaultMenuButtons = new String[]{"", "", "", "PB-", "T+"};

	/** Blank LCD Menu Buttons */
	private String[] blankMenuButtons = new String[]{"", "", "", "", ""};

	/** Current LCD Menu Buttons */
	private String[] menuButtons = blankMenuButtons;

	/** Should the menu be blanked? */
	private boolean blankMenu = false;

	/** The drawing screen. */
	private G15Wrapper myScreen;

	/** The controller. */
	private G15Control myController;

	/** Display countdown time rather than countup. */
	private boolean showCountdown = true;

	/** Display countdown time rather than countup in progressbar. */
	private boolean showCountdownBar = false;

	/** Is PBar3 broken? */
	private boolean brokenPBar3 = false;

	/**
	 * Constants used to get information from Songbird.
	 * This saves having to actually rebuild strings every 1/2 second and should speed things up a fraction
 	 */

	private final static String PLAYER_PLAYSTATUS = "Playing";
	private final static String PLAYER_PAUSESTATUS = "Paused";
	private final static String PLAYER_ARTIST = "Artist";
	private final static String PLAYER_TITLE = "Title";
	private final static String PLAYER_POSITION = "Position";
	private final static String PLAYER_LENGTH = "Length";
	private final static String PLAYER_TOTALTIME = "LengthStr";
	private final static String PLAYER_SHUFFLE = "Shuffle";
	private final static String PLAYER_REPEAT = "Repeat";

	private final static String PLAYSTATUS_STOPPED = "Stopped";
	private final static String PLAYSTATUS_PAUSED = "Paused";
	private final static String PLAYSTATUS_PLAYING = "Playing";
	private final static String PLAYSTATUS_UNKNOWN = "";

	private final static String OUTPUT_TIMESPLIT = " - ";
	private final static String OUTPUT_SCREENTITLE = "G15SongBirdControl";

	private final static String ERROR_NOTRUNNING = "Songbird not running.";
	private final static String ERROR_NOTPLAYING = "Nothing playing.";
	private final static String ERROR_GETTINGDATA = "Error getting data.";

	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Called when the plugin is loaded.
	 *
	 * @param control The G15Control that owns this plugin
	 * @param wrapper The screen that this plugin owns
	 */
	public void onLoad(G15Control control, G15Wrapper wrapper){
		myController = control;
		myScreen = wrapper;
		XMLParser configFile = control.getConfig();

		configFile.reset();
		brokenPBar3 = Boolean.parseBoolean(configFile.getValue(configFile.findElement("pbar3fix")));
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
	 * Get the information from songbird
	 */
	public Hashtable<String,String> getInfo() {
		final Hashtable<String,String> result = new Hashtable<String,String>();
		try {
			final Socket socket = new Socket("127.0.0.1", 7055);
			final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				try {
					final String line = in.readLine();
					if (line == null) {
						break;
					} else {
						final String type = line.substring(0, line.indexOf(":")).trim();
						final String data = line.substring(line.indexOf(":")+1, line.length()).trim();
						result.put(type, data);
					}
				} catch (IOException e) {
					break;
				}
			}
		} catch (Exception e) { return null; }
		return result;
	}

	/**
	 * Called when the plugin is about to be unloaded.
	 */
	public void onUnload() { }

	/**
	 * Called every 1/2 second for drawing related tasks when this screen is active.
	 */
	public void onRedraw() {
		myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
		myScreen.drawText(FontSize.SMALL, new Point(126, 2), G15Position.LEFT, dateFormat.format(new Date()));
		String playStatus = PLAYSTATUS_UNKNOWN;
		final Hashtable<String,String> info = getInfo();
		try {
			blankMenu = (info == null);
			if (blankMenu) {
				myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
				myScreen.drawText(FontSize.LARGE, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, ERROR_NOTRUNNING);
			} else {
				try {
					final int playing = Integer.parseInt(info.get(PLAYER_PLAYSTATUS));
					final int paused = Integer.parseInt(info.get(PLAYER_PAUSESTATUS));
					if (playing == 0) {
						playStatus = PLAYSTATUS_STOPPED;
					} else if (paused == 1) {
						playStatus = PLAYSTATUS_PAUSED;
					} else {
						playStatus = PLAYSTATUS_PLAYING;
					}
				} catch (NumberFormatException e) { }
				if (playStatus == PLAYSTATUS_STOPPED) {
					myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
					myScreen.drawText(FontSize.LARGE, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, ERROR_NOTPLAYING);
				} else {

					final String artist = info.get(PLAYER_ARTIST);
					final String title = info.get(PLAYER_TITLE);
					String time = OUTPUT_TIMESPLIT+info.get(PLAYER_TOTALTIME);

					int currentTime = 0;
					int totalTime = 1;
						try {
						currentTime = (int)(Integer.parseInt(info.get(PLAYER_POSITION))/1000);
						totalTime = (int)(Integer.parseInt(info.get(PLAYER_LENGTH))/1000);
						if (totalTime == 0) {
							currentTime = 0;
							totalTime = 1;
						}
					} catch (NumberFormatException e) {
							currentTime = 0;
							totalTime = 1;
					}

					if (myScreen instanceof G15WrapperLinux) {
						ProgressBarType pbar = ProgressBarType.TYPE3;
						if (brokenPBar3) {
							((G15WrapperLinux)myScreen).drawProgressBar(new Point(10, 30), new Point(150, 30), true, 0, totalTime, pbar);
							pbar = ProgressBarType.TYPE1;
						}
						if (showCountdownBar) {
							((G15WrapperLinux)myScreen).drawProgressBar(new Point(10, 30), new Point(150, 30), true, (totalTime-currentTime), totalTime, pbar);
						} else {
							((G15WrapperLinux)myScreen).drawProgressBar(new Point(10, 30), new Point(150, 30), true, currentTime, totalTime, pbar);
						}
					}
					if (showCountdown) {
						time = duration(totalTime-currentTime)+time;
					} else {
						time = duration(currentTime)+time;
					}
					myScreen.drawText(FontSize.SMALL, new Point(0, 10) , G15Position.CENTER, new String[]{artist, title, time});
				}
			}
		} catch (Exception e) {
			myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
			myScreen.drawText(FontSize.LARGE, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, ERROR_GETTINGDATA);
		}
		updateMenuButtons();
		myScreen.silentDraw();
	}

	/**
	 * Set the menu Buttons
	 */
	private void updateMenuButtons() {
		String[] newMenuButtons = new String[]{"", "", "", "", ""};
		if (!blankMenu) {
//			System.out.println("non-blank!");
			for (int i = 0; i < newMenuButtons.length ; i++) {
				newMenuButtons[i] = defaultMenuButtons[i];
			}
			if (showCountdownBar) { newMenuButtons[3] = "PB+"; }
			if (!showCountdown) { newMenuButtons[4] = "T+"; }
		}
		for (int i = 0; i < newMenuButtons.length ; i++) {
			if (!newMenuButtons[i].equals(menuButtons[i])) {
				menuButtons = newMenuButtons;
				drawMenu(false);
				break;
			}
		}
	}

	/**
	 * Draw the main menu.
	 *
	 * @param drawNow Should this be drawn now?
	 */
	private void drawMenu(boolean drawNow) {
		myScreen.fillArea(new Point(3,35), new Point(myScreen.getWidth()-3, 41), false);
		myScreen.drawLine(new Point(0,34), new Point(myScreen.getWidth(), 34), true);
		myScreen.drawText(FontSize.SMALL, new Point(11, 36), G15Position.LEFT, menuButtons[0]);
		myScreen.drawText(FontSize.SMALL, new Point(42, 36), G15Position.LEFT, menuButtons[1]);
		myScreen.drawText(FontSize.SMALL, new Point(70, 36), G15Position.CENTER, menuButtons[2]);
		myScreen.drawText(FontSize.SMALL, new Point(110, 36), G15Position.LEFT, menuButtons[3]);
		myScreen.drawText(FontSize.SMALL, new Point(135, 36), G15Position.LEFT, menuButtons[4]);
		if (drawNow) {
			myScreen.silentDraw();
		}
	}

	/**
	 * Get the duration in seconds as a string.
	 *
	 * @param secondsInput to get duration for
	 * @return Duration as a string
	 */
	private String duration(int secondsInput) {
		final StringBuilder result = new StringBuilder();
		final int hours = (secondsInput / 3600);
		final int minutes = (secondsInput / 60 % 60);
		final int seconds = (secondsInput % 60);

		if (hours > 0) { result.append(hours+":"); }
		result.append(String.format("%0,2d:%0,2d",minutes,seconds));

		return result.toString();
	}

	/**
	 * Called when this plugin becomes active.
	 * This needs to FULLY redraw the screen.
	 */
	public void onActivate() {
		myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
		myScreen.fillArea(new Point(3,1), new Point(121, 7), false);
		myScreen.drawText(FontSize.SMALL, new Point(4, 2), G15Position.LEFT, OUTPUT_SCREENTITLE);
		myScreen.drawLine(new Point(124, 0), new Point(124, 8), true);
		myScreen.drawLine(new Point(114, 0), new Point(114, 8), true);
		myScreen.drawLine(new Point(104, 0), new Point(104, 8), true);
		myScreen.drawLine(new Point(0, 8), new Point(myScreen.getWidth(), 8), true);
		drawMenu(false);
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
		if (menuButtons == blankMenuButtons) { return; }
	}

	/**
	 * Called when LCD Button 2 is pressed.
	 */
	public void onLCD2() {
		if (menuButtons == blankMenuButtons) { return; }
	}

	/**
	 * Called when LCD Button 3 is pressed.
	 */
	public void onLCD3() {
		if (menuButtons == blankMenuButtons) { return; }
		showCountdownBar = !showCountdownBar;
		if (showCountdownBar) {
			menuButtons[3] = "PB+";
		} else {
			menuButtons[3] = "PB-";
		}
		drawMenu(true);
	}

	/**
	 * Called when LCD Button 4 is pressed.
	 */
	public void onLCD4() {
		if (menuButtons == blankMenuButtons) { return; }
		showCountdown = !showCountdown;
		if (showCountdown) {
			menuButtons[4] = "T+";
		} else {
			menuButtons[4] = "T-";
		}
		drawMenu(true);
	}


	public static PixelImage getShuffleOn() {
//		StringBuilder img = new StringBuilder();
//		img.append("0011110");
//		img.append("0100000");
//		img.append("0011100");
//		img.append("0000010");
//		img.append("0111100");
//		img.append("0111100");
//		return new PixelImage(7,6,img.toString());
		final String img = "001111001000000011100000001001111000000000";
		return new PixelImage(7,6,img);
	}

	public static PixelImage getShuffleOff() {
//		StringBuilder img = new StringBuilder();
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		return new PixelImage(7,6,img.toString());
		final String img = "000000000000000000000000000000000000000000";
		return new PixelImage(7,6,img);
	}

	public static PixelImage getRepeatOn() {
//		StringBuilder img = new StringBuilder();
//		img.append("0111100");
//		img.append("0100010");
//		img.append("0111100");
//		img.append("0100100");
//		img.append("0100010");
//		img.append("0000000");
//		return new PixelImage(7,6,img.toString());
		final String img = "011110001000100111100010010001000100000000";
		return new PixelImage(7,6,img);
	}

	public static PixelImage getRepeatTrackOn() {
//		StringBuilder img = new StringBuilder();
//		img.append("0111110");
//		img.append("0001000");
//		img.append("0001000");
//		img.append("0001000");
//		img.append("0001000");
//		img.append("0000000");
//		return new PixelImage(7,6,img.toString());
		final String img = "011111000010000001000000100000010000000000";
		return new PixelImage(7,6,img);
	}

	public static PixelImage getRepeatAlbumOn() {
//		StringBuilder img = new StringBuilder();
//		img.append("0001000");
//		img.append("0010100");
//		img.append("0100010");
//		img.append("0111110");
//		img.append("0100010");
//		img.append("0000000");
//		return new PixelImage(7,6,img.toString());
		final String img = "000100000101000100010011111001000100000000";
		return new PixelImage(7,6,img);
	}

	public static PixelImage getRepeatOff() {
//		StringBuilder img = new StringBuilder();
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		img.append("0000000");
//		return new PixelImage(7,6,img.toString());
		final String img = "000000000000000000000000000000000000000000";
		return new PixelImage(7,6,img);
	}

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
	public String[] getInformation() { return new String[]{"Dataforce", "Amarok control"}; }
}