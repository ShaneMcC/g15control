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

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.w3c.dom.Element;

import uk.org.dataforce.g15.plugins.Plugin;
import uk.org.dataforce.g15.plugins.PluginInfo;
import uk.org.dataforce.g15.plugins.PluginManager;

import uk.org.dataforce.g15.process.ProcessDetails;
import uk.org.dataforce.g15.process.ProcessHandler;

/**
 * Application to control G15 LCD.
 */
public class G15Control {
	/** Default menu buttons */
	private final String[] defultMenuButtons = new String[]{"Menu", "", "", "", ""};
	/** Current LCD Buttons */
	private String[] menuButtons = defultMenuButtons;
	
	/** Main screen of Control application. */
	private G15Wrapper myScreen;
	/** Screen used for the LoadingComposer screen. */
	private G15Wrapper loadingScreen;
	
	/** List of all plugin class names. */
	private ArrayList<String> allScreens = new ArrayList<String>();
	/** Current active plugin number. */
	private int currentScreen = -1;
	/** Current active plugin. */
	private Plugin currentPlugin = null;
	/** Plugin Manager */
	private PluginManager pluginManager = null;
	
	/** Remote Control. */
	private RemoteControl myControl;
	/** Remote Control Thread. */
	private Thread controlThread;
	/** Is there a command waiting in the remoteControl? */
	private boolean gotCommand;
	
	/** Draw timer. */
	private Timer drawTimer = new Timer();
	/** Is it time to draw? */
	private boolean drawTime;
	
	/** Title of this screen. */
	private String screenTitle = "G15Control";
	
	/** Default Title of screens. */
	private String defaultScreenTitle = null;
	
	/** G15Composer ProcessID. */
	String composerProcess;
	/** Location used when we spawn our own G15Composer. */
	String myComposerLocation = "";
	
	/**
	 * Priority of current screen.
	 * This deteriorates by 1 every redraw.
	 */
	int screenPriority = 0;
	
	/**
	 * Countdown (1/2 second intervals) to clear "main" text.
	 * "main" text is cleared when this is exactly 0
	 */
	private int clearMainCount = -1;
	
	/** Config File. */
	private XMLParser configFile;
	
	/** Current M button pressed */
	private int mButton = 0;
	
	/** Is Menu? */
	private boolean isMenu = false;
	/** Which M Button was active before the menu was opened? */
	private int oldMButton = 1;
	/** What was the old screenTitle */
	private String oldTitle = "";
	/** Menu. */
	private G15ControlMenu myMenu = null;
	
	/** Are we running with a WIP version of G15Daemon? */
	private boolean isWIP = false;

	/** Are we showing the splash screen? */
	private boolean isSplash = false;

	/**
	 * Get the configuration directory
	 *
	 * @return Config directory
	 */
	public static String getConfigDir() {
		final String sep = System.getProperty("file.separator");
		final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
		final String dirname = System.getProperty("user.home")+sep+((isWindows) ? "Application Data"+sep : ".")+"g15control"+sep;
		
		return dirname;
	}

	/**
	 * Main application.
	 */
	public void main(String configFilename) {
		String configDirname = getConfigDir();
		System.out.println("Using Config: "+configDirname+configFilename);
		pluginManager = PluginManager.getPluginManager();
		if (!new File(configDirname+configFilename).exists()) {
			try {
				final File dir = new File(configDirname);
				if (!dir.exists()) { dir.mkdir(); }
				System.out.println("Config file not found, default created, please edit the file and change the default settings.");
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configDirname+configFilename)));
				out.println("<g15control>");
				out.println("	<!-- This is the path to the g15composer pipe. (Deprecated) -->");
				out.println("	<!-- If this has attribute exec then this is taken to be the path to -->");
				out.println("	<!-- the g15composer binary and not to an exisitng pipe. -->");
				out.println("	<!-- If given a binary, a pipe will be created in /tmp -->");
				out.println("	<!-- if the value of this is empty, or not present, then we do not use the composer and talk -->");
				out.println("	<!-- to g15daemon directly. -->");
				out.println("	<!-- Use /usr/bin/g15composer binary: <composer exec=\"\">/usr/bin/g15composer</composer> -->");
				out.println("	<!-- Use /tmp/composer pipe: <composer>/tmp/composer</composer> -->");
				out.println("	<!-- Use G15Daemon: <composer/> or omit this parameter entirely -->");
				out.println("	<composer />");
				out.println("	<!-- This is the 'M' button to enable by default -->");
				out.println("	<defaultmbutton>1</defaultmbutton>");
				out.println("</g15control>");
				out.close();
			} catch (IOException e) {
				System.out.println("Config file not found, unable to create default.");
			}
			System.exit(0);
		}
		configFile = new XMLParser(configDirname+configFilename);
		G15DaemonWrapper.debug = (configFile.findElement("debug") != null);
		G15DaemonWrapper.isWIP = (configFile.findElement("wip") != null);
		try {
			G15DaemonWrapper.debugScale = Integer.parseInt(configFile.getAttribute(configFile.findElement("debug"), "scale"));
		} catch (NumberFormatException nfe) {
			G15DaemonWrapper.debugScale = 4;
		}
		boolean needRemoteSocket = false;
		if (System.getProperty("os.name").startsWith("Windows") && !G15DaemonWrapper.debug) {
			System.out.println("Sorry, this application does not yet run on this OS.");
			System.exit(0);
		} else {
			needRemoteSocket = (configFile.findElement("remotesocket") != null);
			String composerLocation = configFile.getValue(configFile.findElement("composer"));
			if (composerLocation == null) {
				System.out.println("Communicating with G15Daemon directly.");
				myScreen = new G15DaemonWrapper();
			} else {
				// Make sure we don't have a debugging G15DaemonWrapper if we are wrapping
				// G15Composer instead.
				G15DaemonWrapper.debug = false;
				needRemoteSocket = true;
				System.out.println("Using "+composerLocation+" for g15composer. [DEPRECATED]");
				System.out.println("===========================================================");
				System.out.println("|                         WARNING                         |");
				System.out.println("===========================================================");
				System.out.println("| Support for G15Composer will be removed in the future,  |");
				System.out.println("| it is reccomended that you stop using this now and make |");
				System.out.println("| sure any plugins work with the new rendering method.    |");
				System.out.println("===========================================================");
				if (configFile.getAttribute(configFile.findElement("composer"), "ignoreDeprecation") == null) {
					System.out.println("| To enable the use of the deprecated G15Composer Wrapper |");
					System.out.println("| you need to add ignoreDeprecation=\"true\" to your config |");
					System.out.println("| fine and restart G15Control.                            |");
					System.out.println("===========================================================");
					showLoadingComposer(true);
					try { Thread.sleep(60000); } catch (InterruptedException e) { }
					System.exit(1);
				}
				if (new File(composerLocation).exists()) {
					if (configFile.getAttribute(configFile.findElement("composer"), "exec") != null) {
						// We need to spawn the g15composer ourself.
						startComposer();
					} else {
						myScreen = new G15ComposerWrapper(composerLocation);
					}
				} else {
					System.out.println("G15 Composer not found. Please make sure the <composer>/path/to/pipe</composer> element is in the config file.");
					System.exit(0);
				}
			}
		}
		
		try {
			myControl = RemoteControl.getRemoteControl(this, !needRemoteSocket);
			controlThread = new Thread(myControl);
			controlThread.start();
		} catch (IOException e) {
			System.out.println("Unable to start RemoteControl, terminating.");
			exitApp();
		}
		drawTimer.schedule(new DrawTimer(this), 0, 500);
		
		myScreen.setMXLight(0, false);
		String defaultMButton = configFile.getValue(configFile.findElement("defaultmbutton"));
		try {
			changeMButton(Integer.parseInt(defaultMButton));
		} catch (NumberFormatException e) {
			changeMButton(1);
		}
		
		drawMe(true);
		
		while (true) {
			if (gotCommand) { processCommand(); }
			if (drawTime) { doRedraw(); }
			try { Thread.sleep(1); } catch (InterruptedException e) { }
		}
	}
	
	/**
	 * This starts the G15Composer process if needed.
	 */
	private void startComposer() {
		showLoadingComposer(false);
		final String composerApp = configFile.getValue(configFile.findElement("composer"));
		if (composerApp == null) {
			System.out.println("G15 Composer not found. Please make sure the <composer>/path/to/pipe</composer> element is in the config file.");
			System.exit(0);
		} else if (!composerApp.equals("")) {
			System.out.println("Creating g15composer..");
			if (myComposerLocation != "") {
				File f = new File(myComposerLocation);
				if (f.exists()) {
					f.delete();
				}
			}
			
			myComposerLocation = "/tmp/g15control.composer."+System.currentTimeMillis();
			System.out.println("\tUsing binary: "+composerApp);
			try {
				composerProcess = ProcessHandler.runProcess(composerApp, myComposerLocation);
			} catch (IOException e) {
				System.out.println("\tCreation failed. Terminating!");
				System.exit(0);
			}
			System.out.println("\tG15Composer created at: "+myComposerLocation);
			myScreen = new G15ComposerWrapper(myComposerLocation);
			
			for (int i = 0 ; i < allScreens.size(); ++i) {
				pluginManager.getPluginInfo(allScreens.get(i)).getPlugin().changeScreen(myScreen);
			}
		}
	}
	
	/**
	 * This draws the main elements of the main UI.
	 * 
	 * @param isFirst Is this the initial draw (ie do we need to load plugins and show the splash?)
	 */
	private void drawMe(boolean isFirst) {
		myScreen.clearScreen(false);
		myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
		
//		screenTitle = configFile.getValue(configFile.findElement("welcometext"));
		if (screenTitle == null) { screenTitle = "G15Control"; }
		if (defaultScreenTitle == null) { defaultScreenTitle = screenTitle; }
		
		if (isFirst) {
			isSplash = true;
			drawSplashText("Loading..");
			myScreen.silentDraw();
			
			drawMainText(screenTitle);
			drawSplashText("Loading.."); // Redrawn beacue drawMainText overlaps it with its fillarea.
			myScreen.drawText(FontSize.SMALL, new Point(70, 28), G15Position.CENTER, new String[]{"Copyright (C) 2007-2008", "Shane 'Dataforce' Mc Cormack"});
			myScreen.silentDraw();
			loadAllPlugins();
			drawSplashText("Loading..");
			myScreen.waitFor(1000);
			drawSplashText("Loaded!");
			isSplash = false;
			if (currentPlugin != null) {
				myScreen.clearScreen(false);
				currentPlugin.onActivate();
			}
		}
		if (currentPlugin == null) {
			myScreen.drawLine(new Point(124, 0), new Point(124, 8), true);
			myScreen.drawLine(new Point(0, 8), new Point(myScreen.getWidth(), 8), true);
			myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
			drawMenu(true);
		}
	}
	
	/** Check the remoteControl for a command */
	public void gotCommand() {
		gotCommand = true;
	}
	
	/** Process the latest command in the remoteControl */
	private void processCommand() {
		gotCommand = false;
		String[] command;
		try {
			command = myControl.getNextCommand().split(" ");
		} catch (NullPointerException e) {
			return;
		}
		if (command != null && command.length > 1) {
			if (command[0].equals("BUTTON")) {
				if (command[1].equals("M1") && !isMenu) { changeMButton(1); }
				else if (command[1].equals("M2") && !isMenu) { changeMButton(2); }
				else if (command[1].equals("M3") && !isMenu) { changeMButton(3); }
				else if (command[1].equals("CHG")) { changeScreen(); }
				else if (command[1].equals("LCD1")) { callLCD1(); }
				else if (command[1].equals("LCD2")) { callLCD2(); }
				else if (command[1].equals("LCD3")) { callLCD3(); }
				else if (command[1].equals("LCD4")) { callLCD4(); }
				else if (!isMenu) {
					if (mButton == -1 || isMenu) { return; }
					configFile.reset();
					ArrayList<Element> elements = configFile.findAllElements("buttons", "M"+mButton, command[1]);
					String buttonCommand;
					String commandType;
					if (elements == null || elements.size() == 0) {
						drawMenu(true);
						drawStatusText("M"+mButton+'-'+command[1]+" Not Assigned");
						clearMainCount = 2;
					} else {
						for (Element commandElement : elements) {
							commandType = configFile.getAttribute(commandElement, "type");
							buttonCommand = configFile.getValue(commandElement);
							if (commandType == null) { commandType = "exec"; }
							if (commandType.equals("exec") || buttonCommand != null) {
								if (commandType.equals("text")) {
									drawMainText(buttonCommand);
								} else if (commandType.equals("title")) {
									screenTitle = buttonCommand;
								} else if (commandType.equals("status")) {
									drawStatusText(buttonCommand);
								} else if (commandType.equals("timeout")) {
									try {	clearMainCount = Integer.parseInt(buttonCommand); }
									catch (NumberFormatException e) { }
								} else if (commandType.equals("exec")) {
									configFile.reset();
									Element tempElement;
									String execArgs;
									String execName;
									configFile.setCurrentElement(commandElement);
									tempElement = configFile.getFirstSubElement("command");
									if (tempElement == null) {
										if (buttonCommand != null && !configFile.hasChildren()) { execName = buttonCommand; }
										else { return; }
									} else {
										execName = configFile.getValue(tempElement);
									}
									drawMediumMainText("Executing: "+execName);
									clearMainCount = 6;
									
									tempElement = configFile.getFirstSubElement("arguments");
									if (tempElement != null) {
										execArgs = configFile.getValue(tempElement);
									} else {
										execArgs = "";
									}
									try {
										ProcessHandler.runProcess(execName, execArgs);
									} catch (IOException e) {
										drawMainText("Exec Command Failed");
										flashLCD();
									}
								} else {
									System.out.println("Unknown command type: "+commandType);
								}
							}
						}
					}
				}
			}
		}
		// Call it again to make sure we clear the command buffer
		processCommand();
	}
	
	/** Change the m button in use at this time. */
	private void changeMButton(int newButton) {
		final int oldButton = mButton;
		if (newButton < 1 || newButton > 3) { newButton = 1; }
		if (mButton == newButton) {
			myScreen.setMXLight(mButton, false);
			mButton = -1;
			drawMediumMainText("'M' Buttons Disabled.");
			clearMainCount = 6;
		} else {
			if (mButton == -1) {
				drawMediumMainText("'M' Buttons Enabled ("+newButton+").");
				clearMainCount = 6;
			}
			mButton = newButton;
			
			myScreen.setMXLight(mButton, true);
			if (oldButton > 0) {
				myScreen.setMXLight(oldButton, false);
			}
		}
	}
	
	/** Timer task executed. */
	public void drawTimerTask() {
		drawTime = true;
	}
	
	/** Redraw the screen. */
	private void doRedraw() {
		drawTime = false;
		
		if (composerProcess != null) {
			if (ProcessHandler.getProcess(composerProcess) == null) {
				startComposer();
				drawMe(true);
				return;
			}
			if (loadingScreen instanceof G15DaemonWrapper) {
				((G15DaemonWrapper)loadingScreen).close();
			}
			loadingScreen = null;
		}
		
		if (clearMainCount >= 0) { --clearMainCount; }
		
		if (currentPlugin != null) {
			currentPlugin.onRedraw();
		} else {
			if (clearMainCount == 0) {
				if (defaultScreenTitle == null) { defaultScreenTitle = ""; }
				screenTitle = defaultScreenTitle;
				drawMainText("");
				drawMenu(true);
			}
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			myScreen.drawText(FontSize.SMALL, new Point(126, 2), G15Position.LEFT, dateFormat.format(new Date()));
			myScreen.fillArea(new Point(3,1), new Point(121, 7), false);
			myScreen.drawText(FontSize.SMALL, new Point(4, 2), G15Position.LEFT, screenTitle);
			myScreen.silentDraw();
		}
		if (screenPriority > 0) { screenPriority--; }
	}
	
	/**
	 * Draw the main menu.
	 *
	 * @param drawNow Should this be drawn now?
	 */
	private void drawMenu(boolean drawNow) {
		if (currentPlugin == null) {
			myScreen.fillArea(new Point(3,35), new Point(myScreen.getWidth()-3, 41), false);
			myScreen.drawLine(new Point(0,34), new Point(myScreen.getWidth(), 34), true);
			myScreen.drawText(FontSize.SMALL, new Point(11, 36), G15Position.LEFT, menuButtons[0]);
			myScreen.drawText(FontSize.SMALL, new Point(42, 36), G15Position.LEFT, menuButtons[1]);
			if (!isMenu) {
				myScreen.drawText(FontSize.SMALL, new Point(70, 36), G15Position.CENTER, menuButtons[2]);
			} else {
				if (myMenu == null) { createMenu(); }
				myScreen.drawText(FontSize.SMALL, new Point(70, 36), G15Position.CENTER, "["+myMenu.getItemNumber()+'/'+myMenu.count()+"]");
			}
		
			myScreen.drawText(FontSize.SMALL, new Point(110, 36), G15Position.LEFT, menuButtons[3]);
			myScreen.drawText(FontSize.SMALL, new Point(135, 36), G15Position.LEFT, menuButtons[4]);
			if (isMenu) {
				if (myMenu == null) { createMenu(); }
				screenTitle = "Menu :: "+myMenu;
				drawMainText(myMenu.getItemName());
			}
			if (drawNow) {
				myScreen.silentDraw();
			}
		}
	}
	
	/**
	 * Draw the small text used in the status bar.
	 *
	 * @param text Text to draw
	 */
	private void drawStatusText(String text) {
		if (currentPlugin == null) {
			drawMenu(true);
			myScreen.drawText(FontSize.SMALL, new Point(70, 36), G15Position.RIGHT, text+" ");
			myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
			myScreen.silentDraw();
		}
	}
	
	/**
	 * Draw the large text to the middle of the screen.
	 * (used by menu and "text" buttons)
	 *
	 * @param text Text to draw
	 */
	private void drawMainText(String text) {
		if (currentPlugin == null) {
			myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
			myScreen.drawText(FontSize.LARGE, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, text);
		}
	}
	
	/**
	 * Draw the medium text to the middle of the screen.
	 * (used by menu and "text" buttons)
	 *
	 * @param text Text to draw
	 */
	private void drawMediumMainText(String text) {
		if (currentPlugin == null) {
			myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
			myScreen.drawText(FontSize.MEDIUM, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, text);
		}
	}	
	
	/**
	 * Draw the small text to the middle of the screen.
	 * (used by menu and "text" buttons)
	 *
	 * @param text Text to draw
	 */
	private void drawSmallMainText(String text) {
		if (currentPlugin == null) {
			myScreen.fillArea(new Point(1,9), new Point(158, 33), false);
			myScreen.drawText(FontSize.SMALL, new Point(0, (myScreen.getHeight()/2)-3), G15Position.CENTER, text);
		}
	}	
	
	/**
	 * Draw the text used for information on the splash screen.
	 *
	 * @param text Text to draw
	 */
	private void drawSplashText(String text) {
		if (isSplash) {
			myScreen.fillArea(new Point(3,3), new Point(156, 12), false);
			myScreen.drawText(FontSize.SMALL, new Point(70, 5), G15Position.CENTER, text);
			myScreen.silentDraw();
		}
	}
	
	/**
	 * Allows a plugin to request screen focus
	 *
	 * @param plugin Plugin that wants the screen
	 * @param priority Priority of request. (A screen will only be granted focus
	 *        if the current screen is a lower priority.)
	 * @return true if focus was granted.
	 */
	public boolean requestFocus(final Plugin plugin, final int priority) {
		if (priority > screenPriority) {
			for (int i = 0; i < allScreens.size(); ++i) {
				if (pluginManager.getPluginInfo(allScreens.get(i)).getPlugin() == plugin) {
					screenPriority = priority;
					changeScreen(i);
					flashLCD();
					return true;
				}
			}
		}
		return false;
	}
	
	/** Flash the LCD */
	private void flashLCD() {
		myScreen.setBrightnessLevel(2);
		myScreen.waitFor(200);
		myScreen.setBrightnessLevel(0);
		myScreen.waitFor(200);
		myScreen.setBrightnessLevel(2);
		myScreen.waitFor(200);
		myScreen.setBrightnessLevel(0);
		myScreen.waitFor(200);
		myScreen.setBrightnessLevel(2);
		myScreen.waitFor(200);
	}
		
	/** What todo when LCD1 is pressed. */
	private void callLCD1() {
		if (currentPlugin != null) {
			currentPlugin.onLCD1();
		} else {
			if (isMenu) {
				myMenu = null;
				isMenu = false;
				menuButtons = defultMenuButtons;
				screenTitle = oldTitle;
				mButton = oldMButton;
				drawMainText("");
				drawMenu(false);
				doRedraw();
				myScreen.setMXLight(mButton, true);
			} else {
				isMenu = true;
				menuButtons = new String[]{"Menu", "Ok", "", "[<]", "[>]"};
				oldTitle = screenTitle;
				oldMButton = mButton;
				mButton = -1;
				myScreen.setMXLight(0, false);
				if (myMenu != null) { myMenu.reset(); }
				drawMenu(false);
				doRedraw();
			}
		}
	}
	
	/** Create the menus */
	private void createMenu() {
		if (myMenu != null) { return; }
		myMenu = new G15ControlMenu("Main");
		myMenu.addItem("Reload Config", "RELOADCONFIG");
		myMenu.addItem("Unload Plugin", "UNLOAD1");
		myMenu.addItem("Reload Plugin", "RELOAD1");
		if (configFile.findElement("noexit") == null) {
			myMenu.addItem("Exit G15 Control", "QUITAPP");
		}
		myMenu.addItem("Exit Menu", "CLOSEMENU");
	}
	
	/** What todo when LCD2 is pressed. */
	private void callLCD2() {
		boolean closeMenu = true;
		if (currentPlugin != null) {
			currentPlugin.onLCD2();
		} else {
			if (myMenu == null) { return; }
			if (myMenu.getItemSubString().equals("RELOADCONFIG")) {
				drawMainText("Reloading Config...");
				configFile = new XMLParser(configFile.getFileName());
				drawMainText("Loading Plugins...");
				loadAllPlugins();
				drawMainText("");
				callLCD1();
				drawStatusText("Config reloaded");
				clearMainCount = 6;
			} else if (myMenu.getItemSubString().equals("UNLOAD1")) {
				myMenu = new G15ControlMenu("Unload Plugin");
				for (PluginInfo plugin : pluginManager.getPluginInfos()) {
					myMenu.addItem(plugin.getNiceName(), "UNLOAD2 "+plugin.getFilename());
				}
				myMenu.addItem("Back to Main Menu", "BACKTOMAIN");
				closeMenu = false;
				drawMenu(true);
			} else if (myMenu.getItemSubString().substring(0, 7).equals("UNLOAD2")) {
				String pluginName = myMenu.getItemSubString().substring(8);
				String pluginNiceName = pluginManager.getPluginInfo(pluginName).getNiceName();
				callLCD1();
				drawMainText("Unloading: "+pluginNiceName);
				drawMainText("");
				clearMainCount = 6;
				if (pluginManager.delPlugin(pluginName)) {
					drawStatusText("Plugin "+pluginNiceName+" unloaded");
				} else {
					drawStatusText("Plugin "+pluginNiceName+" failed to unload");
				}
				for (int i = 0; i < allScreens.size(); ++i) {
					if (allScreens.get(i).equalsIgnoreCase(pluginName)) {
						allScreens.remove(i);
						break;
					}
				}
				myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
				
			} else if (myMenu.getItemSubString().equals("RELOAD1")) {
				myMenu = new G15ControlMenu("Reload Plugin");
				for (PluginInfo plugin : pluginManager.getPluginInfos()) {
					myMenu.addItem(plugin.getNiceName(), "RELOAD2 "+plugin.getFilename());
				}
				myMenu.addItem("Back to Main Menu", "BACKTOMAIN");
				closeMenu = false;
				drawMenu(true);
			} else if (myMenu.getItemSubString().substring(0, 7).equals("RELOAD2")) {
				String pluginName = myMenu.getItemSubString().substring(8);
				String pluginNiceName = pluginManager.getPluginInfo(pluginName).getNiceName();
				callLCD1();
				drawMainText("Reloading: "+pluginNiceName);
				drawMainText("");
				final Boolean reloadState = pluginManager.reloadPlugin(pluginName);
				clearMainCount = 6;
				if (reloadState) {
					drawStatusText("Plugin "+pluginNiceName+" reloaded");
					pluginManager.getPluginInfo(pluginName).getPlugin().onLoad(this, myScreen);
				} else {
					drawStatusText("Plugin "+pluginNiceName+" failed to reload");
					for (int i = 0; i < allScreens.size(); ++i) {
						if (allScreens.get(i).equalsIgnoreCase(pluginName)) {
							allScreens.remove(i);
							break;
						}
					}
				}
				myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
			} else if (myMenu.getItemSubString().equals("BACKTOMAIN")) {
				myMenu = null;
				createMenu();
				drawMenu(true);
			} else if (myMenu.getItemSubString().equals("CLOSEMENU")) {
				callLCD1();
			} else if (myMenu.getItemSubString().equals("QUITAPP")) {
				callLCD1();
				exitApp();
			}
		}
	}
	
	/** Exit application. */
	private void exitApp() {
		if (myScreen != null) {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			
			myScreen.clearScreen(false);
			myScreen.drawRoundedBox(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint(), true, false);
			if (defaultScreenTitle == null) { defaultScreenTitle = ""; }
			drawMainText(defaultScreenTitle);
			myScreen.drawText(FontSize.SMALL, new Point(70, 36), G15Position.CENTER, "Ended at: "+dateFormat.format(new Date()));
			myScreen.setMXLight(0, false);
			myScreen.silentDraw();
		}
		System.exit(1);
	}
	
	/** What todo when LCD3 is pressed. */
	private void callLCD3() {
		if (currentPlugin != null) {
			currentPlugin.onLCD3();
		} else {
			if (myMenu == null) { return; }
			myMenu.prevItem();
			drawMenu(true);
		}
	}
	
	/** What todo when LCD4 is pressed. */
	private void callLCD4() {
		if (currentPlugin != null) {
			currentPlugin.onLCD4();
		} else {
			if (myMenu == null) { return; }
			myMenu.nextItem();
			drawMenu(true);
		}
	}
	
	/** Called when the changeScreen button is pressed. */
	private void changeScreen() {
		changeScreen(++currentScreen);
	}
	
	/**
	 * Called to change to a specific screen.
	 *
	 * @param screenID Screen to change to.
	 */
	private void changeScreen(final int screenID) {
		// reset the screen priority
		screenPriority = 0;
		// Disable the menu.
		if (isMenu) { callLCD1(); }
		if (allScreens.size() < 1) {
			drawMainText("No plugins loaded.");
			return;
		}
		if (currentPlugin != null) { currentPlugin.onDeactivate(); }
		if (screenID > allScreens.size()-1) {
			if (currentPlugin != null) { myScreen.clearScreen(false); }
			currentPlugin = null;
			drawMe(false);
			doRedraw();
			currentScreen = -1;
		} else {
			myScreen.clearScreen(false);
			myScreen.silentDraw();
			currentScreen = screenID;
			currentPlugin = pluginManager.getPluginInfo(allScreens.get(screenID)).getPlugin();
			currentPlugin.onActivate();
		}
	}
	
	/** 
	 * Get the reference to the config file.
	 *
	 * @return The reference to the configFile.
	 */
	public XMLParser getConfig() { return configFile; }
	
	/** 
	 * Load a plugin
	 *
	 * @param plugin Jar name of plugin
	 * @return true/false is plugin loaded
	 */
	private boolean loadPlugin(String plugin) {
		if (pluginManager.addPlugin(plugin)) {
			pluginManager.getPluginInfo(plugin).loadPlugin();
			pluginManager.getPluginInfo(plugin).getPlugin().onLoad(this, myScreen);
			allScreens.add(plugin);
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Load all plugins listed in the configFile.
	 */
	private void loadAllPlugins() {
		drawSplashText("Loading Plugins..");
		ArrayList<Element> elements = configFile.findAllElements("plugin");
		String pluginName;
		for (Element pluginElement : elements) {
			pluginName = configFile.getValue(pluginElement);
			drawSplashText("Loading Plugin: "+pluginName+"...");
			
			if (loadPlugin(pluginName)) {
				if (configFile.getAttribute(pluginElement, "default") != null) {
					currentPlugin = pluginManager.getPluginInfo(pluginName).getPlugin();
					currentScreen = allScreens.size()-1;
				}
			}
		}
	}
	
	/** Show a temp-screen to show that the composer is being loaded. */
	private void showLoadingComposer(boolean showWarning) {
		if (!(loadingScreen instanceof G15DaemonWrapper)) {
			loadingScreen = new G15DaemonWrapper();
		}
		/* Not Needed anymore now that G15DaemonWrapper can do more than draw pixels
		loadingScreen.drawPixels(loadingScreen.getTopLeftPoint(), PixelDrawings.getInfoConsoleBaseScreen());
		loadingScreen.drawPixels(new Point(3, 11), PixelDrawings.getLoading());
		loadingScreen.drawPixels(new Point(35, 11), PixelDrawings.getComposer());
		loadingScreen.drawPixels(new Point(79, 11), PixelDrawings.getEllipsis()); */
		
		// Some Points
		final Point almostTopLeft = new Point(loadingScreen.getTopLeftPoint().x+1, loadingScreen.getTopLeftPoint().y+1);
		final Point almostBottomRight = new Point(loadingScreen.getBottomRightPoint().x-1, loadingScreen.getBottomRightPoint().y-1);
		final Point textPoint = new Point(loadingScreen.getTopLeftPoint().x+4, loadingScreen.getTopLeftPoint().y+3);
		
		// Surrounding Box, including title space
		loadingScreen.drawRoundedBox(almostTopLeft, almostBottomRight, true, false);
		loadingScreen.drawLine(new Point(almostTopLeft.x, textPoint.y+6), new Point(almostBottomRight.x, textPoint.y+6), true);
		
		// Text
		loadingScreen.drawText(FontSize.SMALL, textPoint, G15Position.LEFT, "[G15Control]");
		if (showWarning) {
			loadingScreen.drawText(FontSize.MEDIUM, new Point(3, 13), G15Position.CENTER, new String[]{"Not Loading G15Composer!"});
			loadingScreen.drawText(FontSize.MEDIUM, new Point(3, 25), G15Position.CENTER, new String[]{"Please check the console for", "more information."});
		} else {
			final String[] text = new String[]{"Loading G15Composer...", "(Note: Use of G15Composer is deprecated.)"};
			loadingScreen.drawText(FontSize.MEDIUM, new Point(3, 11), G15Position.LEFT, text);
		}
		
		
		// Give it that console-y feel by making it white-on-black!
		loadingScreen.reversePixels(myScreen.getTopLeftPoint(), myScreen.getBottomRightPoint());
		loadingScreen.silentDraw();
	}
	
	/**
	 * Main application stub.
	 *
	 * @param args Array of strings representing each world given on the command line when starting the application.
	 */
	public static void main(String[] args) {
		final String filename = "g15control.config";
		
		G15Control control = new G15Control();
		control.main(filename);
	}
}