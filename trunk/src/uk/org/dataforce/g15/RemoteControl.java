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

import java.awt.Point;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class RemoteControl implements Runnable {
	/** Socket where remote-control commands go. */
	private ServerSocket serverSocket;
	
	/** Socket for current rc command. */
	private Socket clientSocket;
	
	/** Queue for commands. */
	private LinkedList<String> commandQueue = new LinkedList<String>();
	
	/** Singleton Remote Control Instance */
	private static RemoteControl me = null;
	
	/** My Owner. */
	G15Control myOwner;
	
	/** Is this a socket-less RemoteControl? */
	private boolean noSocket = false;
	
	
	/**
	 * Get the Remote Control (Create if needed)
	 *
	 * @param owner The G15Control that owns this remote control
	 * @param noSocket does this RemoteControl need a socket?
	 */
	public static RemoteControl getRemoteControl(G15Control owner, final boolean noSocket) throws IOException {
		if (me == null) {
			me = new RemoteControl(owner, noSocket);
		}
		return me;
	}
	
	/**
	 * Get the Remote Control (Throw exception if not created)
	 */
	public static RemoteControl getRemoteControl() throws IOException {
		if (me == null) {
			throw new IOException("No RemoteControl found.");
		}
		return me;
	}
	
	/**
	 * Create a remote control.
	 *
	 * @param owner The G15Control that owns this remote control
	 * @param noSocket does this RemoteControl need a socket?
	 */
	private RemoteControl(G15Control owner, final boolean noSocket) throws IOException {
		myOwner = owner;
		this.noSocket = noSocket;
		if (!noSocket) {
			serverSocket = new ServerSocket(33523);
		}
	}
	
	/**
	 * Get the first item in the commandQueue.
	 */
	public String getNextCommand()  {
		synchronized (commandQueue) {
			return commandQueue.poll();
		}
	}
	
	/**
	 * Add a command to the commandQueue.
	 *
	 * @param cmd Command to add.
	 */
	public void addCommand(final String cmd)  {
		synchronized (commandQueue) {
			commandQueue.add(cmd);
			myOwner.gotCommand();
		}
	}
	
	/** Run the remote control. */
	public void run() {
		if (noSocket) { return; }
		PrintWriter out;
		BufferedReader in;
		String inputLine;
		
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				inputLine = in.readLine();
				out.println("OK");
				out.close();
				in.close();
				clientSocket.close();
				addCommand(inputLine);
			} catch (IOException e) { continue; }
		}
	}
}