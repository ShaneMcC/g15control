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
package uk.org.dataforce.g15.process;

import java.util.ArrayList;

public class ProcessDetails {
	/** Output Stream ProcessHandler. */
	private ProcessHandler myOutHandler;
	/** Error Stream ProcessHandler. */
	private ProcessHandler myErrorHandler;
	/** Arguments used to create process. */
	private ArrayList<String> myArguments;
	/** Process Name */
	private String myName;	
	/** Actual Process */
	private Process myProcess;
	/** Process ID in hashtable. */
	private String myProcessID;	

	/**
	 * Create a new ProcessDetails.
	 *
	 * @param process Process this is about
	 * @param name Name of process
	 * @param args Arguments used to launch process
	 * @param args Process ID used to store process in hashtable
	 */
	public ProcessDetails(Process process, String name, ArrayList<String> args, String processID) {
		myOutHandler = new ProcessHandler(name,"stdout", process.getInputStream(), processID);
		myOutHandler.startThread();
		myErrorHandler = new ProcessHandler(name,"stderr", process.getErrorStream(), processID);
		myErrorHandler.startThread();
		myProcess = process;
		myArguments = args;
		myName = name;
		myProcessID = processID;
	}
	
	/**
	 * Get the Process Object for this process
	 *
	 * @return The Process Object for this process
	 */
	public Process getProcess() {
		return myProcess;
	}
	
	/**
	 * Get the ProcessHandler Object for this process output Stream
	 *
	 * @return The ProcessHandler Object for this process output Stream
	 */
	public ProcessHandler getOutHandler() {
		return myOutHandler;
	}
	
	/**
	 * Get the ProcessHandler Object for this process Error Stream
	 *
	 * @return The ProcessHandler Object for this process Error Stream
	 */
	public ProcessHandler getErrorHandler() {
		return myErrorHandler;
	}
	
	/**
	 * Get the process name.
	 *
	 * @return The process Name
	 */
	public String getName() {
		return myName;
	}
	
	/**
	 * Get the process arguments.
	 *
	 * @return The process arguments
	 */
	public ArrayList<String> getArgs() {
		return myArguments;
	}
	
	/**
	 * Called when the process is terminted, this will remove the process from
	 * the processHandler hashtable.
	 */
	protected void terminated() {
		ProcessHandler.removeProcess(myProcessID);
	}
	
	/**
	 * Called to terminate a process
	 */
	public void terminate() {
		myProcess.destroy();
	}	
	
}