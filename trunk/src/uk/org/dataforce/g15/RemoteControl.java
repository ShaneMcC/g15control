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
	
	/** My Owner. */
	G15Control myOwner;

	/**
	 * Create a remote control.
	 *
	 * @param owner The G15Control that owns this remote control
	 */
	RemoteControl(G15Control owner) throws IOException {
		myOwner = owner;
		serverSocket = new ServerSocket(33523);
	}
	
	/**
	 * Get the first item in the commandQueue.
	 */
	public String getNextCommand()  {
		synchronized (commandQueue) {
			return commandQueue.poll();
		}
	}	
	
	/** Run the remote control. */
	public void run() {
		PrintWriter out;
		BufferedReader in;
		String inputLine;
		
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				inputLine = in.readLine();
				synchronized (commandQueue) {
					commandQueue.add(inputLine);
				}
				out.println("OK");
				out.close();
				in.close();
				clientSocket.close();
				myOwner.gotCommand();
			} catch (IOException e) { continue; }
		}
	}
}