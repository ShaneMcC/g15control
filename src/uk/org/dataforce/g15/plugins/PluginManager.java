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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PluginManager {
	/** List of known plugins. */
	private final Map<String, PluginInfo> knownPlugins = new Hashtable<String, PluginInfo>();

	/** Directory where plugins are stored. */
	private final String myDir;

	/** Singleton instance of the plugin manager. */
	private static PluginManager me;

	/**
	 * Create a new PluginManager.
	 */
	private PluginManager() {
		myDir = G15Control.getConfigDir()+"plugins"+System.getProperty("file.separator");
	}

	/**
	 * Retrieves the singleton instance of the plugin manager.
	 *
	 * @return A singleton instance of PluginManager.
	 */
	public static final synchronized PluginManager getPluginManager() {
		if (me == null) {
			me = new PluginManager();
		}
		
		return me;
	}

	/**
	 * Adds a new plugin.
	 *
	 * @param filename Filename of Plugin jar
	 * @return True if loaded, false if failed to load or if already loaded.
	 */
	public boolean addPlugin(final String filename) {
		if (knownPlugins.containsKey(filename.toLowerCase())) {
			return false;
		}
		
		if (!(new File(getDirectory() + filename)).exists()) {
// 			System.out.println("Error loading plugin " filename + ": File does not exist");
			return false;
		}
		
		PluginInfo pluginInfo;
		
		try {
			pluginInfo = new PluginInfo(filename);
			knownPlugins.put(filename.toLowerCase(), pluginInfo);
			
			return true;
		} catch (PluginException e) {
			System.out.println("Error loading plugin " + filename + ": " + e.getMessage());
		}
		
		return false;
	}

	/**
	 * Remove a plugin.
	 *
	 * @param filename Filename of Plugin jar
	 * @return True if removed.
	 */
	public boolean delPlugin(final String filename) {
		if (!knownPlugins.containsKey(filename.toLowerCase())) {
			return false;
		}

		PluginInfo pluginInfo = getPluginInfo(filename);
		
		try {
			pluginInfo.unloadPlugin();
		} catch (Exception e) {
			System.out.println("Plugin Delete Error:"+e.getMessage());
		}
		
		knownPlugins.remove(filename.toLowerCase());
		pluginInfo = null;
		return true;
	}

	/**
	 * Reload a plugin.
	 *
	 * @param filename Filename of Plugin jar
	 * @return True if reloaded.
	 */
	public boolean reloadPlugin(final String filename) {
		if (!knownPlugins.containsKey(filename.toLowerCase())) {
			return false;
		}
		
		final boolean wasLoaded = getPluginInfo(filename).isLoaded();
		delPlugin(filename);
		final boolean result = addPlugin(filename);
		
		if (wasLoaded) {
			getPluginInfo(filename).loadPlugin();
		}
		
		return result;
	}

	/**
	 * Reload all plugins.
	 */
	public void reloadAllPlugins() {
		for (PluginInfo pluginInfo : getPluginInfos()) {
			reloadPlugin(pluginInfo.getFilename());
		}
	}

	/**
	 * Get a plugin instance.
	 *
	 * @param filename File name of plugin jar
	 * @return PluginInfo instance, or null
	 */
	public PluginInfo getPluginInfo(final String filename) {
		return knownPlugins.get(filename.toLowerCase());
	}

	/**
	 * Get a plugin instance by plugin name.
	 *
	 * @param name Name of plugin to find.
	 * @return PluginInfo instance, or null
	 */
	public PluginInfo getPluginInfoByName(final String name) {
		for (PluginInfo pluginInfo : knownPlugins.values()) {
			if (pluginInfo.getName().equalsIgnoreCase(name)) {
					return pluginInfo;
			}
		}
		return null;
	}

	/**
	 * Get directory where plugins are stored.
	 *
	 * @return Directory where plugins are stored.
	 */
	public String getDirectory() {
		return myDir;
	}

	/**
	 * Retrieves a list of all installed plugins.
	 * Any file under the main plugin directory (~/.g15control/plugins or similar)
	 * that matches *.jar is deemed to be a valid plugin.
	 *
	 * @param addPlugins Should all found plugins be automatically have addPlugin() called?
	 * @return A list of all installed plugins
	 */
	public List<PluginInfo> getPossiblePluginInfos(final boolean addPlugins) {
		final ArrayList<PluginInfo> res = new ArrayList<PluginInfo>();
		
		final LinkedList<File> dirs = new LinkedList<File>();
		
		dirs.add(new File(myDir));
		
		while (!dirs.isEmpty()) {
			final File dir = dirs.pop();
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) {
					dirs.add(file);
				}
			} else if (dir.isFile() && dir.getName().endsWith(".jar")) {
				String target = dir.getPath();
				
				// Remove the plugin dir
				target = target.substring(myDir.length(), target.length());
				if (addPlugins) {
					addPlugin(target);
				} else {
					try {
						res.add(new PluginInfo(target, false));
					} catch (PluginException pe) { /* This can not be thrown when the second param is false */}
				}
			}
		}

		if (addPlugins) {
			for (String name : knownPlugins.keySet()) {
				res.add(getPluginInfo(name));
			}
		}

		return res;
	}
	
	/**
	 * Get Collection<PluginInfo> of known plugins.
	 *
	 * @return Collection<PluginInfo> of known plugins.
	 */
	public Collection<PluginInfo> getPluginInfos() {
		return new ArrayList<PluginInfo>(knownPlugins.values());
	}
}
