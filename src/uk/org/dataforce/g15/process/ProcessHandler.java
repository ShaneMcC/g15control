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
package uk.org.dataforce.g15.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;

public class ProcessHandler implements Runnable {
	/** Store details of all running processes. */
	private static Hashtable<String,ProcessDetails> runningProcesses = new Hashtable<String,ProcessDetails>();
	
	/** Stream Name. */
	private String myStreamName = "";
	/** Process Name. */	
	private String myProcessName = "";
	/** Process ID. */	
	private String myProcessID = "";
	/** Stream. */
	private InputStream myStream;
	
	/** My thread. */
	private Thread controlThread = null;

	/**
	 * Create a new ProcessHandler.
	 *
	 * @param processName Friendly name for process.
	 * @param streamName Friendly name for stream.
	 * @param in Stream to handle.
	 * @param processID ID of process used to store inside hashtable
	 */
	public ProcessHandler(String processName, String streamName, InputStream in, String processID) {
		myStreamName = streamName;
		myProcessName = processName;
		myStream = in;
		myProcessID = processID;
	}
	
	/**
	 * Begin the stream-handling thread.
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(myStream));
			String temp;
			while (true) {
				temp = in.readLine();
				if (temp == null) {
					System.out.println("["+myProcessName+"||"+myStreamName+"] - Stream Closed.");
					break;
				} else {
					System.out.println("["+myProcessName+"||"+myStreamName+"] "+temp);
				}
			}
			in.close();
			ProcessDetails pd = getProcess(myProcessID);
			if (pd != null) {
				pd.terminated();
			}
		} catch (Exception e) {
			System.err.println("["+myProcessName+"||"+myStreamName+"] - Error with stream: "+e);
			e.printStackTrace();
		}
	}
	
	/** Start myself running in a thread. */
	public void startThread() {
		if (controlThread == null) {
			controlThread = new Thread(this);
			controlThread.start();
		}
	}
	
	/**
	 * Run a process.
	 *
	 * @param processName the Name of the process
	 * @param processArgs the arguments for the process
	 */
	public static String runProcess(String processName, String processArgs) throws IOException {
		ArrayList<String> processCommands = new ArrayList<String>();
		processCommands.add(processName);
		StringBuilder tempStr = new StringBuilder();
		String[] bits = processArgs.split(" ");
		if (processArgs.length() > 0) {
			for (String bit : bits) {
				if (tempStr.length() == 0) {
					if (bit.charAt(0) != '"') {
						processCommands.add(bit);
					} else {
						tempStr.append(bit.substring(1));
					}
				} else {
					if (bit.charAt(bit.length()-1) != '"') {
						tempStr.append(' '+bit);
					} else {
						tempStr.append(' '+bit.substring(0,bit.length()-1));
						processCommands.add(tempStr.toString());
						tempStr = new StringBuilder();
					}
				}
			}
		}
		Process p = Runtime.getRuntime().exec(processCommands.toArray(new String[0]));
		String processID = processName+"-"+System.currentTimeMillis();
		synchronized (runningProcesses) {
			int i = 0;
			while (runningProcesses.containsKey(processID)) {
				processID = processName+"-"+System.currentTimeMillis()+"-"+i++;
			}
			ProcessDetails pd = new ProcessDetails(p, processName, processCommands, processID);
			runningProcesses.put(processID, pd);
		}
		System.out.println("Adding process with ID: "+processID);
		return processID;
	}
	
	/**
	 * Removes a process from the hashtable.
	 *
	 * @param processID ID of process to remove.
	 */
	protected static void removeProcess(String processID) {
		synchronized (runningProcesses) {
			System.out.println("Removing process with ID: "+processID);
			runningProcesses.remove(processID);
		}
	}
	
	/**
	 * Get the processDetails object of a process from the hashtable.
	 *
	 * @param processID ID of process to find.
	 */
	public static ProcessDetails getProcess(String processID) {
		synchronized (runningProcesses) {
			return runningProcesses.get(processID);
		}
	}
}