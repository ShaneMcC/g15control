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

import java.util.ArrayList;

public class G15ControlMenu {
	/** Items in this menu. */
	private ArrayList<String> menuItems = new ArrayList<String>();
	/** Sub Strings for menu items. */
	private ArrayList<String> menuItemSubStrings = new ArrayList<String>();	
	
	/** Current menu item number. */
	private int currentItem = -1;
	
	/** This menu name. */
	private String myName;
	
	/**
	 * Create a new menu.
	 *
	 * @param menuName Name of menu to add.
	 */	
	public G15ControlMenu(String menuName) {
		myName = menuName;
	}
	
	/**
	 * Add a menu Item.
	 * This automatically calls reset()
	 *
	 * @param itemName Name of item to add.
	 */	
	public void addItem(String itemName) {
		addItem(itemName, itemName);
	}
	
	/**
	 * Add a menu Item with a sub-string.
	 * This automatically calls reset()
	 *
	 * @param itemName Name of item to add.
	 */	
	public void addItem(String itemName, String subString) {
		menuItems.add(itemName);
		menuItemSubStrings.add(subString);
		reset();
	}
	
	/**
	 * Clear Menu items
	 */	
	public void clear() {
		menuItems.clear();
		currentItem = 0;
	}
	
	
	/**
	 * Get menu name.
	 *
	 * @return Menu name;
	 */	
	public String toString() {
		return myName;
	}
	
	/**
	 * Reset item position to 0.
	 */	
	public void reset() {
		currentItem = 0;
	}
	
	/**
	 * Move to the next menu item.
	 */	
	public void nextItem() {
		++currentItem;
		if (currentItem >= menuItems.size()) {
			currentItem = 0;
		}
	}
	
	/**
	 * Move to the previous menu item.
	 */	
	public void prevItem() {
		--currentItem;
		if (currentItem < 0) {
			currentItem = menuItems.size()-1;
		}
	}
	
	/**
	 * Get current menuItem name.
	 *
	 * @return menuItem name
	 */	
	public String getItemName() {
		if (menuItems.size() < 1) { return ""; }
		return menuItems.get(currentItem);
	}
	
	/**
	 * Get current menuItem subString.
	 *
	 * @return menuItem subString
	 */	
	public String getItemSubString() {
		if (menuItemSubStrings.size() < 1) { return ""; }
		return menuItemSubStrings.get(currentItem);
	}
	
	/**
	 * Get current menuItem Number.
	 *
	 * @return menuItem Number
	 */	
	public int getItemNumber() {
		return currentItem+1;
	}
	
	/**
	 * Get Total menuItem count.
	 *
	 * @return Total menuItem count
	 */	
	public int count() {
		return menuItems.size();
	}
}