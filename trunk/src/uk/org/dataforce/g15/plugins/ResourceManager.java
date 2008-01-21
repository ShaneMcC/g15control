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
 * Based on ResourceManager code from DMDirc (DMDirc.com)
 */
package uk.org.dataforce.g15.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceManager {
	/** Zipfile instance. */
	private final ZipFile zipFile;
	
	/** Entries list. */
	private final List<String> entries;
	
	/**
	 * Instantiates ResourceManager.
	 *
	 * @param filename Filename of the zip to load
	 * @throws IOException Throw when the zip fails to load
	 */
	protected ResourceManager(final String filename) throws IOException {
		super();
		
		this.zipFile = new ZipFile(filename);
		entries = new ArrayList<String>();
		final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
		while (zipEntries.hasMoreElements()) {
			entries.add(zipEntries.nextElement().getName());
		}
	}

	/**
	 * Checks if a resource exists.
	 *
	 * @param resource Resource to check
	 * @return true iif the resource exists
	 */
	public boolean resourceExists(final String resource) {
		final ZipEntry zipEntry = zipFile.getEntry(resource);
		
		if (zipEntry == null || zipEntry.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Gets a byte[] of the specified resource.
	 *
	 * @param resource Name of the resource to return
	 *
	 * @return byte[] for the resource, or an empty byte[] if not found
	 */
	public byte[] getResourceBytes(final String resource) {
		final ZipEntry zipEntry = zipFile.getEntry(resource);
		BufferedInputStream inputStream;
		
		if (zipEntry == null) {
			return new byte[0];
		}
		
		if (zipEntry.isDirectory()) {
			return new byte[0];
		}
		
		final byte[] bytes = new byte[(int) zipEntry.getSize()];
		
		try {
			inputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
		} catch (IOException ex) {
			return new byte[0];
		}
		
		try {
			if (inputStream.read(bytes) != bytes.length) {
				inputStream.close();
				return new byte[0];
			}
		} catch (IOException ex) {
			return new byte[0];
		}
		
		try {
			inputStream.close();
		} catch (IOException ex) {
			System.out.println("Unable to close stream");
		}
		
		return bytes;
	}
	
	/**
	 * Gets an InputStream for the specified resource.
	 *
	 * @param resource Name of the resource to return
	 *
	 * @return InputStream for the resource, or null if not found
	 */
	public InputStream getResourceInputStream(final String resource) {
		final ZipEntry zipEntry = zipFile.getEntry(resource);
		
		if (zipEntry == null) {
			return null;
		}
		
		try {
			return zipFile.getInputStream(zipEntry);
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Gets a List of the resources starting with the specified
	 * prefix.
	 *
	 * @param resourcesPrefix Prefix of the resources to return
	 *
	 * @return List of resources found
	 */
	public List<String> getResourcesStartingWith(final String resourcesPrefix) {
		final List<String> resources = new ArrayList<String>();
		
		for (String entry : entries) {
			if (entry.startsWith(resourcesPrefix)) {
				resources.add(entry);
			}
		}
		
		return resources;
	}

}
