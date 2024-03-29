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
 * SVN: $Id$
 *
 * Based on Plugin code from DMDirc (DMDirc.com)
 */
package uk.org.dataforce.g15.plugins;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

public class PluginInfo implements Comparable<PluginInfo> {
	/** Plugin Meta Data */
	private Properties metaData = null;
	/** File that this plugin was loaded from */
	private final String filename;
	/** The actual Plugin from this jar */
	private Plugin plugin = null;
	/** The classloader used for this Plugin */
	private PluginClassLoader classloader = null;
	/** The resource manager used by this pluginInfo */
	private ResourceManager myResourceManager = null;
	/** List of classes this plugin has */
	private List<String> myClasses = new ArrayList<String>();
	/** Requirements error message. */
	private String requirementsError;

	/**
	 * Create a new PluginInfo.
	 *
	 * @param filename File that this plugin is stored in.
	 * @throws PluginException if there is an error loading the Plugin
	 */
	public PluginInfo(final String filename) throws PluginException {
		this(filename, true);
	}

	/**
	 * Create a new PluginInfo.
	 *
	 * @param filename File that this plugin is stored in.
	 * @param load Should this plugin be loaded, or is this just a placeholder? (true for load, false for placeholder)
	 * @throws PluginException if there is an error loading the Plugin
	 */
	public PluginInfo(final String filename, final boolean load) throws PluginException {
		this.filename = filename;

		if (!load) { return; }

		ResourceManager res;
		try {
			res = getResourceManager();
		} catch (IOException ioe) {
			throw new PluginException("Plugin "+filename+" failed to load, error with resourcemanager: "+ioe.getMessage(), ioe);
		}

		try {
			if (res.resourceExists("META-INF/plugin.info")) {
				metaData = new Properties();
				metaData.load(res.getResourceInputStream("META-INF/plugin.info"));
			} else {
				throw new PluginException("Plugin "+filename+" failed to load, plugin.info doesn't exist in jar");
			}
		} catch (PluginException pe) {
			// Stop the next catch Catching the one we threw ourself
			throw pe;
		} catch (Exception e) {
			throw new PluginException("Plugin "+filename+" failed to load, plugin.info failed to open - "+e.getMessage(), e);
		}

		if (getVersion() < 0) {
			throw new PluginException("Plugin "+filename+" failed to load, incomplete plugin.info (Missing or invalid 'version')");
		} else if (getAuthor().isEmpty()) {
			throw new PluginException("Plugin "+filename+" failed to load, incomplete plugin.info (Missing 'author')");
		} else if (getName().isEmpty()) {
			throw new PluginException("Plugin "+filename+" failed to load, incomplete plugin.info (Missing 'name')");
		} else if (getMinVersion().isEmpty()) {
			throw new PluginException("Plugin "+filename+" failed to load, incomplete plugin.info (Missing 'minversion')");
		} else if (getMainClass().isEmpty()) {
			throw new PluginException("Plugin "+filename+" failed to load, incomplete plugin.info (Missing 'mainclass')");
		}

		final String requirements = checkRequirements();
		if (requirements.isEmpty()) {
			final String mainClass = getMainClass().replace('.', '/')+".class";
			if (!res.resourceExists(mainClass)) {
				throw new PluginException("Plugin "+filename+" failed to load, main class file ("+mainClass+") not found in jar.");
			}

			for (final String classfilename : res.getResourcesStartingWith("")) {
				String classname = classfilename.replace('/', '.');
				if (classname.matches("^.*\\.class$")) {
					classname = classname.replaceAll("\\.class$", "");
					myClasses.add(classname);
				}
			}

			if (isPersistant()) { loadEntirePlugin(); }
		} else {
			throw new PluginException("Plugin "+filename+" was not loaded, one or more requirements not met ("+requirements+")");
		}
		myResourceManager = null;
	}

	/**
	 * Get the resource manager for this plugin
	 *
	 * @throws IOException if there is any problem getting a ResourceManager for this plugin
	 */
	protected synchronized ResourceManager getResourceManager() throws IOException {
		if (myResourceManager == null) {
			final String directory = PluginManager.getPluginManager().getDirectory();
			myResourceManager = new ResourceManager(directory+filename);
		}
		return myResourceManager;
	}

	/**
	 * Checks to see if the minimum version requirement of the plugin is
	 * satisfied. If either version is non-positive, the test passes. On
	 * failure, the requirementsError field will contain a user-friendly
	 * error message.
	 *
	 * @param desired The desired minimum version of DMDirc.
	 * @param actual The actual current version of DMDirc.
	 * @return True if the test passed, false otherwise
	 */
	protected boolean checkMinimumVersion(final String desired, final int actual) {
		int idesired;

		try {
			idesired = Integer.parseInt(desired);
		} catch (NumberFormatException ex) {
			requirementsError = "'minversion' is a non-integer";
			return false;
		}

		if (actual > 0 && idesired > 0 && actual < idesired) {
			requirementsError = "Plugin is for a newer version of DMDirc";
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks to see if the maximum version requirement of the plugin is
	 * satisfied. If either version is non-positive, the test passes. If
	 * the desired version is empty, the test passes. On failure, the
	 * requirementsError field will contain a user-friendly error message.
	 *
	 * @param desired The desired maximum version of DMDirc.
	 * @param actual The actual current version of DMDirc.
	 * @return True if the test passed, false otherwise
	 */
	protected boolean checkMaximumVersion(final String desired, final int actual) {
		int idesired;

		if (desired.isEmpty()) {
			return true;
		}

		try {
			idesired = Integer.parseInt(desired);
		} catch (NumberFormatException ex) {
			requirementsError = "'maxversion' is a non-integer";
			return false;
		}

		if (actual > 0 && idesired > 0 && actual > idesired) {
			requirementsError = "Plugin is for an older version of DMDirc";
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks to see if the OS requirements of the plugin are satisfied.
	 * If the desired string is empty, the test passes. Otherwise it is
	 * used as one to three colon-delimited regular expressions, to test
	 * the name, version and architecture of the OS, respectively. If the
	 * test fails, the requirementsError field will contain a user-friendly
	 * error message.
	 *
	 * @param desired The desired OS requirements
	 * @param actualName The actual name of the OS
	 * @param actualVersion The actual version of the OS
	 * @param actualArch The actual architecture of the OS
	 * @return True if the test passes, false otherwise
	 */
	protected boolean checkOS(final String desired, final String actualName,
			final String actualVersion, final String actualArch) {
		if (desired.isEmpty()) {
			return true;
		}

		final String[] desiredParts = desired.split(":");

		if (!actualName.toLowerCase().matches(desiredParts[0])) {
			requirementsError = "Invalid OS. (Wanted: '" + desiredParts[0] + "', actual: '" + actualName + "')";
			return false;
		} else if (desiredParts.length > 1
				&& !actualVersion.toLowerCase().matches(desiredParts[1])) {
			requirementsError = "Invalid OS version. (Wanted: '" + desiredParts[1] + "', actual: '" + actualVersion + "')";
			return false;
		} else if (desiredParts.length > 2
				&& !actualVersion.toLowerCase().matches(desiredParts[2])) {
			requirementsError = "Invalid OS architecture. (Wanted: '" + desiredParts[2] + "', actual: '" + actualArch + "')";
			return false;
		}

		return true;
	}

	/**
	 * Are the requirements for this plugin met?
	 *
	 * @return Empty string if ok, else a reason for failure
	 */
	public String checkRequirements() {
		if (metaData == null) {
			// No meta-data, so no requirements.
			return "";
		}

		if (!checkMinimumVersion(getMinVersion(), 0)
				|| !checkMaximumVersion(getMaxVersion(), 0)
				|| !checkOS(getMetaInfo(new String[]{"required-os", "require-os"}),
				System.getProperty("os.name"), System.getProperty("os.version"),
				System.getProperty("os.arch"))) {
			return requirementsError;
		}

		// Required Files
		final String requiredFiles = getMetaInfo(new String[]{"required-files", "require-files", "required-file", "require-file"});
		if (!requiredFiles.isEmpty()) {
			for (String files : requiredFiles.split(",")) {
				final String[] filelist = files.split("\\|");
				boolean foundFile = false;
				for (String file : filelist) {
					if ((new File(file)).exists()) {
						foundFile = true;
						break;
					}
				}
				if (!foundFile) {
					return "Required file '"+files+"' not found";
				}
			}
		}

		// Required Plugins
		final String requiredPlugins = getMetaInfo(new String[]{"required-plugins", "require-plugins", "required-plugin", "require-plugin"});
		if (!requiredPlugins.isEmpty()) {
			for (String plugin : requiredPlugins.split(",")) {
				final String[] data = plugin.split(":");
				final PluginInfo pi = PluginManager.getPluginManager().getPluginInfoByName(data[0]);
				if (pi == null) {
					return "Required plugin '"+data[0]+"' was not found";
				} else {
					if (data.length > 1) {
						// Check plugin minimum version matches.
						try {
							final int minversion = Integer.parseInt(data[1]);
							if (pi.getVersion() < minversion) {
								return "Plugin '"+data[0]+"' is too old (Required Version: "+minversion+", Actual Version: "+pi.getVersion()+")";
							} else {
								if (data.length > 2) {
									// Check plugin maximum version matches.
									try {
										final int maxversion = Integer.parseInt(data[2]);
										if (pi.getVersion() > maxversion) {
											return "Plugin '"+data[0]+"' is too new (Required Version: "+maxversion+", Actual Version: "+pi.getVersion()+")";
										}
									} catch (NumberFormatException nfe) {
										return "Plugin max-version '"+data[2]+"' for plugin ('"+data[0]+"') is a non-integer";
									}
								}
							}
						} catch (NumberFormatException nfe) {
							return "Plugin min-version '"+data[1]+"' for plugin ('"+data[0]+"') is a non-integer";
						}
					}
					// Make sure the required plugin is loaded if its not already,
					pi.loadPlugin();
				}
			}
		}

		// All requirements passed, woo \o
		return "";
	}

	/**
	 * Is this plugin loaded?
	 */
	public boolean isLoaded() {
		return (plugin != null);
	}

	/**
	 * Load entire plugin.
	 * This loads all files in the jar immediately.
	 *
	 * @throws PluginException if there is an error with the resourcemanager
	 */
	private void loadEntirePlugin() throws PluginException {
		// Load the main "Plugin" from the jar
		loadPlugin();

		// Now load all the rest.
		for (String classname : myClasses) {
			loadClass(classname);
		}
		myResourceManager = null;
	}

	/**
	 * Load the plugin files.
	 */
	public boolean loadPlugin() {
		if (isLoaded() || metaData == null) {
			return false;
		}
		loadClass(getMainClass());
		myResourceManager = null;
		return isLoaded();
	}

	/**
	 * Load the given classname.
	 *
	 * @param classname Class to load
	 */
	private void loadClass(final String classname) {
		try {
			classloader = new PluginClassLoader(this);

			final Class<?> c = classloader.loadClass(classname);
			final Constructor<?> constructor = c.getConstructor(new Class[] {});

			final Object temp = constructor.newInstance(new Object[] {});

			if (temp instanceof Plugin) {
				if (((Plugin) temp).checkPrerequisites()) {
					plugin = (Plugin) temp;
				} else {
					System.out.println("Prerequisites for plugin not met. ('"+filename+":"+getMainClass()+"')");
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Class not found ('"+filename+":"+getMainClass()+"')");
		} catch (NoSuchMethodException nsme) {
			System.out.println("Constructor missing ('"+filename+":"+getMainClass()+"')");
		} catch (IllegalAccessException iae) {
			System.out.println("Unable to access constructor ('"+filename+":"+getMainClass()+"')");
		} catch (InvocationTargetException ite) {
			System.out.println("Unable to invoke target ('"+filename+":"+getMainClass()+"')");
		} catch (InstantiationException ie) {
			System.out.println("Unable to instantiate plugin ('"+filename+":"+getMainClass()+"')");
		} catch (NoClassDefFoundError ncdf) {
			System.out.println("Unable to instantiate plugin ('"+filename+":"+getMainClass()+"'): Unable to find class: " + ncdf.getMessage());
		} catch (VerifyError ve) {
			System.out.println("Unable to instantiate plugin ('"+filename+":"+getMainClass()+"') - Incompatible");
		}
	}

	/**
	 * Unload the plugin if possible.
	 */
	public void unloadPlugin() {
		if (!isPersistant() && isLoaded()) {
			try {
				plugin.onUnload();
			} catch (Exception e) {
				System.out.println("Error in onUnload for "+getName()+":"+e.getMessage());
			}
			plugin = null;
			classloader = null;
		}
	}

	/**
	 * Get the list of Classes
	 *
	 * @return Classes this plugin has
	 */
	public List<String> getClassList() {
		return myClasses;
	}

	/**
	 * Get the main Class
	 *
	 * @return Main Class to begin loading.
	 */
	public String getMainClass() { return metaData.getProperty("mainclass",""); }

	/**
	 * Get the Plugin for this plugin.
	 *
	 * @return Plugin
	 */
	public Plugin getPlugin() { return plugin; }

	/**
	 * Get the PluginClassLoader for this plugin.
	 *
	 * @return PluginClassLoader
	 */
	protected PluginClassLoader getPluginClassLoader() { return classloader; }

	/**
	 * Get the plugin friendly version
	 *
	 * @return Plugin friendly Version
	 */
	public String getFriendlyVersion() { return metaData.getProperty("friendlyversion",""); }

	/**
	 * Get the plugin version
	 *
	 * @return Plugin Version
	 */
	public int getVersion() {
		try {
			return Integer.parseInt(metaData.getProperty("version","0"));
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Is this a persistant plugin?
	 *
	 * @return true if persistant, else false
	 */
	public boolean isPersistant() {
		final String persistance = metaData.getProperty("persistant","no");
		return persistance.equalsIgnoreCase("true") || persistance.equalsIgnoreCase("yes");
	}

	/**
	 * Does this plugin contain any persistant classes?
	 *
	 * @return true if this plugin contains any persistant classes, else false
	 */
	public boolean hasPersistant() {
		final String persistance = metaData.getProperty("persistant","no");
		if (persistance.equalsIgnoreCase("true")) {
			return true;
		} else {
			for (Object keyObject : metaData.keySet()) {
				if (keyObject.toString().toLowerCase().startsWith("persistant-")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get a list of all persistant classes in this plugin
	 *
	 * @return List of all persistant classes in this plugin
	 */
	public List<String> getPersistantClasses() {
		final List<String> result = new ArrayList<String>();
		final String persistance = metaData.getProperty("persistant","no");
		if (persistance.equalsIgnoreCase("true")) {
			try {
				ResourceManager res = getResourceManager();

				for (final String filename : res.getResourcesStartingWith("")) {
					String classname = filename.replace('.', '/');
					if (classname.matches("^.*\\.class$")) {
						classname = classname.replaceAll("\\.class$", "");
						result.add(classname);
					}
				}
			} catch (IOException e) {
				// Jar no longer exists?
			}
		} else {
			for (Object keyObject : metaData.keySet()) {
				if (keyObject.toString().toLowerCase().startsWith("persistant-")) {
					result.add(keyObject.toString().substring(11));
				}
			}
		}
		return result;
	}

	/**
	 * Is this a persistant class?
	 *
	 * @param classname class to check persistance of
	 * @return true if file (or whole plugin) is persistant, else false
	 */
	public boolean isPersistant(final String classname) {
		if (isPersistant()) {
			return true;
		} else {
			final String persistance = metaData.getProperty("persistant-"+classname,"no");
			return persistance.equalsIgnoreCase("true") || persistance.equalsIgnoreCase("yes");
		}
	}

	/**
	 * Get the plugin Filename.
	 *
	 * @return Filename of plugin
	 */
	public String getFilename() { return filename; }

	/**
	 * Get the full plugin Filename (inc dirname)
	 *
	 * @return Filename of plugin
	 */
	public String getFullFilename() { return PluginManager.getPluginManager().getDirectory()+filename; }

	/**
	 * Get the plugin Author.
	 *
	 * @return Author of plugin
	 */
	public String getAuthor() { return getMetaInfo("author",""); }

	/**
	 * Get the plugin Description.
	 *
	 * @return Description of plugin
	 */
	public String getDescription() { return getMetaInfo("description",""); }

	/**
	 * Get the minimum dmdirc version required to run the plugin.
	 *
	 * @return minimum dmdirc version required to run the plugin.
	 */
	public String getMinVersion() { return getMetaInfo("minversion",""); }

	/**
	 * Get the (optional) maximum dmdirc version on which this plugin can run
	 *
	 * @return optional maximum dmdirc version on which this plugin can run
	 */
	public String getMaxVersion() { return getMetaInfo("maxversion",""); }

	/**
	 * Get the name of the plugin. (Used to identify the plugin)
	 *
	 * @return Name of plugin
	 */
	public String getName() { return getMetaInfo("name",""); }

	/**
	 * Get the nice name of the plugin. (Displayed to users)
	 *
	 * @return Nice Name of plugin
	 */
	public String getNiceName() { return getMetaInfo("nicename",getName()); }

	/**
	 * String Representation of this plugin
	 *
	 * @return String Representation of this plugin
	 */
	public String toString() { return getNiceName()+" - "+filename; }

	/**
	 * Get misc meta-information.
	 *
	 * @param metainfo The metainfo to return
	 * @return Misc Meta Info (or "" if not found);
	 */
	public String getMetaInfo(final String metainfo) { return getMetaInfo(metainfo,""); }

	/**
	 * Get misc meta-information.
	 *
	 * @param metainfo The metainfo to return
	 * @param fallback Fallback value if requested value is not found
	 * @return Misc Meta Info (or fallback if not found);
	 */
	public String getMetaInfo(final String metainfo, final String fallback) { return metaData.getProperty(metainfo,fallback); }

	/**
	 * Get misc meta-information.
	 *
	 * @param metainfo[] The metainfos to look for in order. If the first item in
	 *                   the array is not found, the next will be looked for, and
	 *                   so on until either one is found, or none are found.
	 * @return Misc Meta Info (or "" if none are found);
	 */
	public String getMetaInfo(final String[] metainfo) { return getMetaInfo(metainfo,""); }

	/**
	 * Get misc meta-information.
	 *
	 * @param metainfo[] The metainfos to look for in order. If the first item in
	 *                   the array is not found, the next will be looked for, and
	 *                   so on until either one is found, or none are found.
	 * @param fallback Fallback value if requested values are not found
	 * @return Misc Meta Info (or "" if none are found);
	 */
	public String getMetaInfo(final String[] metainfo, final String fallback) {
		for (String meta : metainfo) {
			String result = metaData.getProperty(meta);
			if (result != null) { return result; }
		}
		return fallback;
	}

	/**
	 * Compares this object with the specified object for order.
	 * Returns a negative integer, zero, or a positive integer as per String.compareTo();
	 *
	 * @param o Object to compare to
	 * @return a negative integer, zero, or a positive integer.
	 */
	public int compareTo(PluginInfo o) {
		return toString().compareTo(o.toString());
	}
}
