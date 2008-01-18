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

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXParseException;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * XMLParser class to simplify reading xml files
 *
 * @author Shane Mc Cormack
 * @version  $Id$
 */
public class XMLParser {
	/** Filename of file to parse. */
	private String myFileName = "";
	/** last exception encountered. */
	private Exception lastException = null;
	/** XML Document for filename. */
	private Document file = null;
	/** Current Element that we are working with. */
	private Element currentElement = null;
	/** All the sub-elements of this element. */
	private ArrayList<Element> currentElementList = new ArrayList<Element>();
	/** All the attributes associated with this element. */
	private Hashtable<String,String> currentAttributes = new Hashtable<String,String>();
	
	/**
	 * Get the last exception
	 *
	 * @return last exception encountered
	 */
	public Exception getException() {
		return lastException;
	}
	
	/**
	 * Check if the file loaded successfully and is ready for parsing.
	 *
	 * @return true if the file loaded successfully and is ready for parsing.
	 */
	public boolean isReady() {
		return !(file == null);
	}
	
	/**
	 * Change the file.
	 *
	 * @param newFilename New file.
	 */
	public void setFileName(String newFileName) {
		myFileName = newFileName;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			file = docBuilder.parse(new File(myFileName));
			reset();
		} catch (Exception e) {
			lastException = e;
			file = null;
		}
	}
	

	/**
	 * Get the filename of the current file.
	 *
	 * @return Filename of the current file.
	 */
	public String getFileName() {
		return myFileName;
	}
	
	/**
	 * Create a new XMLParser.
	 *
	 * @param filename What file should we parse?
	 */
	public XMLParser(String filename) {
		setFileName(filename);
	}
	
	/** Reset the to the "top" of the file. */
	public void reset() {
		if (!this.isReady()) { return; }
		setCurrentElement(file.getDocumentElement());
	}
	
	/**
	 * Change the current element to the given element.
	 *
	 * @param newCurrentElement Element to set as the new current element (null resets to the "top" of the file)
	 */
	public void setCurrentElement(Element newCurrentElement) {
		if (!this.isReady()) { return; }
		if (newCurrentElement == null) { reset(); return; }
		currentElement = newCurrentElement;
		getElementTree();
		getAttributes();
	}
	
	/**
	 * Get the first sub-element of the current element that has a specific name.
	 *
	 * @param elementName Name to look for ("" will get the very first element)
	 * @return Element matching name. null if none found.
	 */
	public Element getFirstSubElement(String elementName) {
		if (!this.isReady()) { return null; }
		Element childElement = null;
		for (int i = 0; i < currentElementList.size(); ++i) {
			childElement = currentElementList.get(i);
			if (elementName.equals("")) {
				return childElement;
			} else if (childElement.getNodeName().equals(elementName)) {
				return childElement;
			}
		}
		return null;
	}
	
	/**
	 * Get the next sub-element after the given sub-element that has a given name.
	 *
	 * @param givenElement Element to start looking after. (if null, find first)
	 * @param elementName Name to look for ("" if same as given element)
	 * @return Element matching name. null if none found.
	 */
	public Element getNextSubElement(Element givenElement, String elementName) {
		if (!this.isReady()) { return null; }
		if (givenElement == null) { return getFirstSubElement(elementName); }
		if (elementName.equals("")) { elementName = givenElement.getNodeName(); }
		Node childElement = givenElement.getNextSibling();
		while (childElement != null) {
			if (childElement.getNodeType() == Node.ELEMENT_NODE) {
				if (childElement.getNodeName().equals(elementName)) {
					return (Element)childElement;
				}
			}
			childElement = childElement.getNextSibling();
		}
		return null;
	}
	
	/**
	 * Get the Element object for the current Element.
	 *
	 * @return The Element object for the current Element.
	 */
	public Element getCurrentElement() {
		if (!this.isReady()) { return null; }
		return currentElement;
	}
	
	/**
	 * Get an arraylist containing all the sub-elements of the current element.
	 *
	 * @return An arraylist containing all the sub-elements of the current element.
	 */
	public ArrayList<Element> getCurrentTree() {
		if (!this.isReady()) { return null; }
		return currentElementList;
	}
	
	/**
	 * Get the parent-element of the current Element.
	 *
	 * @return Parent Element for the current element.
	 */
 	public Element getParent() {
		if (!this.isReady()) { return null; }
		return getParent(currentElement);
 	}
	
	/**
	 * Get the parent-element of the given Element.
	 *
	 * @param givenElement element to find the parent element for.
	 * @return Parent Element for the given element.
	 */
	public Element getParent(Element givenElement) {
		Node myParent = givenElement.getParentNode();
		while (myParent.getNodeType() != Node.ELEMENT_NODE) {
			myParent = myParent.getParentNode();
			if (myParent == null) { break; }
		}
		return (Element)myParent;
	}
	
	/**
	 * Update the currentElementList ArrayList with the sub-elements of the current element.
	 */
	private void getElementTree() {
		if (!this.isReady()) { return; }
		currentElementList.clear();
		Node itemChild = currentElement.getFirstChild();
		while (itemChild != null) {
			if (itemChild.getNodeType() == Node.ELEMENT_NODE) {
				currentElementList.add((Element)itemChild);
			}
			itemChild = itemChild.getNextSibling();
		}
	}
	
	/**
	 * Update the currentAttributes Hashtable with the attributes of the current element.
	 */
	private void getAttributes() {
		if (!this.isReady()) { return; }
		currentAttributes.clear();
		NamedNodeMap myAttributes = currentElement.getAttributes();
		for (int j = 0; j < myAttributes.getLength(); j++) {
			currentAttributes.put(myAttributes.item(j).getNodeName(), myAttributes.item(j).getNodeValue());
		}
	}
	
	/**
	 * Get the value of an attribute of the current element.
	 *
	 * @param name Name of the attribute
	 * @return String Value of element (null if element doesn't exist)
	 */
	public String getAttribute(String name) {
		if (!this.isReady()) { return null; }
		if (currentAttributes.containsKey(name)) { return currentAttributes.get(name); }
		else { return null; }
	}
	
	/**
	 * Get the value of an attribute of a specific element.
	 *
	 * @param element Element to get attribute from (currentElement if null)
	 * @param name Name of the attribute
	 * @return String Value of element (null if element doesn't exist)
	 */
	public String getAttribute(Element whatElement, String name) {
		if (whatElement == null) { return getAttribute(name); }
		NamedNodeMap myAttributes = whatElement.getAttributes();
		for (int j = 0; j < myAttributes.getLength(); j++) {
			if (myAttributes.item(j).getNodeName().equals(name)) {
				return myAttributes.item(j).getNodeValue();
			}
		}
		return null;
	}
	
	/**
	 * Get the value of this element.
	 *
	 * @return String Value of this element. (null if no value)
	 */
	public String getValue() {
		if (!this.isReady()) { return null; }
		return getValue(currentElement);
	}
	
	/**
	 * Get the value of the given element.
	 *
	 * @param givenElement element to find the parent element for.
	 * @return String Value of this element. (null if no value)
	 */
	public String getValue(Element givenElement) {
		if (hasChildren(givenElement) || givenElement == null) {
			return null;
		} else {
			// Element has no child elements, but it may have children
			if (givenElement.getFirstChild() != null) {
				// Has a child (<element>child</element>)
				return givenElement.getFirstChild().getNodeValue();
			} else {
				// No children at all (<element />)
				return null;
			}
		}
	}
	
	/**
	 * Check if this element has any sub-elements.
	 *
	 * @return Boolean true if this element has sub-elements, false otherwise
	 */
	public boolean hasChildren() {
		if (!this.isReady()) { return false; }	
		return hasChildren(currentElement);
	}
	
	/**
	 * Check if the given element has any sub-elements.
	 *
	 * @param givenElement element to find the parent element for.
	 * @return Boolean true if this element has sub-elements, false otherwise
	 */
	public boolean hasChildren(Element givenElement) {
		if (givenElement == null) { return false; }
		Node itemChild = givenElement.getFirstChild();
		while (itemChild != null) {
			if (itemChild.getNodeType() == Node.ELEMENT_NODE) {
				return true;
			} else {
				itemChild = itemChild.getNextSibling();
			}
		}
		return false;
	}
	
	/**
	 * Recursively print to console ALL elements below this element, including sub-elements.
	 */
	public void printNodes(String tablevel) {
		if (!this.isReady()) { return; }
		Node itemChild = currentElement.getFirstChild();
		while (itemChild != null) {
			if (itemChild.getNodeType() == Node.ELEMENT_NODE) {
				System.out.printf("%s%s - %s\n", tablevel, itemChild.getNodeName(), getValue((Element)itemChild));
				currentElement = (Element)itemChild;
				printNodes(tablevel+"\t");
			}
			itemChild = itemChild.getNextSibling();
		}
	}
	
	/**
	 * Find the first element that matches the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the first
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz /&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * <br>
	 * This works from the current element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param elements Names of elements to find
	 * @return first Element that matches the given element-names, null if none match.
	 */
	public Element findElement(String... elements) {
		if (!this.isReady()) { return null; }
		return findElement(currentElement, elements);
	}
	
	/**
	 * Find the first element that matches the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the first
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the given element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param givenElement element to search under
	 * @param elements Names of elements to find
	 * @return first Element that matches the given element-names, null if none match.
	 */
	public Element findElement(Element givenElement, String... elements) {
		return findElement(givenElement, null, elements);
	}
	
	/**
	 * Find the last element that matches the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the lasst
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the current element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param elements Names of elements to find
	 * @return last Element that matches the given element-names, null if none match.
	 */
	public Element findLastElement(String... elements) {
		if (!this.isReady()) { return null; }
		return findLastElement(currentElement, elements);
	}
	
	/**
	 * Find the last element that matches the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the lasst
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the given element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param givenElement element to search under
	 * @param elements Names of elements to find
	 * @return last Element that matches the given element-names, null if none match.
	 */
	public Element findLastElement(Element givenElement, String... elements) {
		Element thisElement = findElement(givenElement, null, elements);
		Element lastElement = null;
		if (thisElement == null) { return null;}
		while (thisElement != null) {
			lastElement = thisElement;
			thisElement = findElement(givenElement, lastElement, elements);
		}
		return lastElement;
	}
	
	/**
	 * Find all the elements that match the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the lasst
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the current element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param elements Names of elements to find
	 * @return ArrayList of all Elements that match the given element-names, empty ArrayList if none found.
	 */
	public ArrayList<Element> findAllElements(String... elements) {
		if (!this.isReady()) { return null; }
		return findAllElements(currentElement, elements);
	}
	
	/**
	 * Find all the elements that match the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the lasst
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the given element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param givenElement element to search under
	 * @param elements Names of elements to find
	 * @return ArrayList of all Elements that match the given element-names, empty ArrayList if none found.
	 */
	public ArrayList<Element> findAllElements(Element givenElement, String... elements) {
		ArrayList<Element> elementsFound = new ArrayList<Element>();
		Element thisElement = findElement(givenElement, null, elements);
		if (thisElement == null) { return elementsFound;}
		while (thisElement != null) {
			elementsFound.add(thisElement);
			thisElement = findElement(givenElement, thisElement, elements);
		}
		return elementsFound;
	}	
	
	/**
	 * Find the first element that matches the element-names given.
	 * given the strings "foo", "bar", "baz" this will find the first
	 * element that matches &lt;foo&gt;&lt;bar&gt;&lt;baz&gt;value&lt;/baz&gt;&lt;/bar&gt;&lt;/foo&gt;
	 * (XPath: /foo/bar/baz)
	 * <br>
	 * This works from the given element.
	 * <br>
	 * Position in the file is reset to the top after this is run.
	 *
	 * @param givenElement element to search under
	 * @param afterElement only find elements that are after this one
	 * @param elements Names of elements to find
	 * @return first Element that matches the given element-names, null if none match.
	 */
	public Element findElement(Element givenElement, Element afterElement, String... elements) {
		if (!this.isReady()) { return null; }
		if (givenElement != currentElement) { setCurrentElement(givenElement); }
		
		Element testElement = afterElement;
		int i = 0;
		if (afterElement != null) {
			setCurrentElement(getParent(afterElement));
			i = elements.length-1;
		}
		while ((i < elements.length) && (i >= 0)) {
			testElement = getNextSubElement(testElement, elements[i]);
			
			if (testElement != null) {
				++i;
				setCurrentElement(testElement);
				testElement = null;
			} else {
				--i;
				testElement = currentElement;
				setCurrentElement(getParent());
			}
		}
		if (i >= 0) { testElement = currentElement; }
		else { testElement = null; }
		reset();
		return testElement;
	}
	
	
	/**
	 * Get SVN Version information.
	 *
	 * @return SVN Version String
	 */
	public static String getSvnInfo () { return "$Id$"; }	
}
