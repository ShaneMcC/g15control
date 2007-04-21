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
package uk.org.dataforce.g15.plugins;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to abuse dcop.
 */
public class DCOPHax {
	/** Path to DCOP binary. */
	private static final String DCOP_BINARY = "/usr/bin/dcop";
	
	/** Name of dcop thingy to prod. */
	private String dcopThingy = "";
	
	/**
	 * Create a new DCOPHax instance.
	 *
	 * @param application DCOP Handle of application to manipulate.
	 */
	public DCOPHax(String application) {
		dcopThingy = application;
	}
	
	/**
	 * Send a non-replying dcop command.
	 *
	 * @param command Command to send (ie to amarok you could use "player showOSD")
	 */
	public void sendProcedure(String command) {
		try {
			runProcess(DCOP_BINARY, dcopThingy+' '+command);
		} catch (IOException e) {
			System.out.println("[DCOP ERROR] Error with DCOP Binary. {"+e.getMessage()+"}");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a replying dcop command.
	 *
	 * @param command Command to send (ie to amarok you could use "player nowPlaying")
	 * @return ArrayList of Strings containing all output.
	 */
	public ArrayList<String> sendFunction(String command) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			Process p = runProcess(DCOP_BINARY, dcopThingy+' '+command);
			try {
				// Get stdout
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String temp;
				while (true) {
					temp = in.readLine();
					if (temp == null) {
						break;
					} else {
						result.add(temp);
					}
				}
				in.close();
				
				// Get stderr
				in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while (true) {
					temp = in.readLine();
					if (temp == null) {
						break;
					} else {
						result.add(temp);
					}
				}
				in.close();
				p.destroy();
				return result;
			} catch (IOException e) {
				System.out.println("[DCOP ERROR] Error Reading Output. {"+e.getMessage()+"}");
				e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			System.out.println("[DCOP ERROR] Error with DCOP Binary. {"+e.getMessage()+"}");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Send a replying dcop command.
	 *
	 * @param command Command to send (ie to amarok you could use "player nowPlaying")
	 * @return Strings containing first line of output.
	 */
	public String sendFunctionSingle(String command) {
		try {
			Process p = runProcess(DCOP_BINARY, dcopThingy+' '+command);
			try {
				// Get stdout
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String temp;
				temp = in.readLine();
				in.close();
				p.destroy();
				return temp;
			} catch (IOException e) {
				System.out.println("[DCOP ERROR] Error Reading Output. {"+e.getMessage()+"}");
				e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			System.out.println("[DCOP ERROR] Error with DCOP Binary. {"+e.getMessage()+"}");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Check if a command is valid
	 *
	 * @param command Command to check (ie to amarok you could check "player showosd")
	 */
	public boolean checkCommand(String command) {
		final ArrayList<String> resultList = sendFunction(command);
		if (resultList != null) {
			final String result = resultList.get(0);
			if (result.equalsIgnoreCase("no such function")) {
				return false;
			} else if (result.equalsIgnoreCase("No such application: '"+dcopThingy+"'")) {
				return false;
			} else if (result.equalsIgnoreCase("call failed")) {
				return false;
			} else if (result.equalsIgnoreCase("object not accessible")) {
				return false;
			} else if (result.equalsIgnoreCase("object '"+command+"' in application '"+dcopThingy+"' not accessible")) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Run a process.
	 *
	 * @param processName the Name of the process
	 * @param processArgs the arguments for the process
	 */
	private static Process runProcess(String processName, String processArgs) throws IOException {
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
		return Runtime.getRuntime().exec(processCommands.toArray(new String[0]));
	}
	
	/**
	 * Example Application Stub.
	 *
	 * @param args Parameters passed to the application
	 */
	public static void main(String[] args) {
		DCOPHax me = new DCOPHax("amarok");
		me.sendProcedure("player showOSD");
		System.out.printf("Got: [%s]\n", me.sendFunction("player nowPlaying"));
		
		System.out.printf("Valid: [%b]\n", me.checkCommand(""));
		System.out.printf("Valid: [%b]\n", me.checkCommand("player"));
		System.out.printf("Valid: [%b]\n", me.checkCommand("player nowPlaying"));
		System.out.printf("Invalid: [%b]\n", me.checkCommand("player thisisinvalid"));
		System.out.printf("Invalid: [%b]\n", me.checkCommand("thisisinvalid"));
		System.out.printf("Invalid: [%b]\n", me.checkCommand("thisisinvalid andsomemore"));
	}	
}