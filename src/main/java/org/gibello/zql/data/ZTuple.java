/*
 * This file is part of Zql.
 *
 * Zql is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Zql is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zql.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gibello.zql.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static org.gibello.zql.ZUtils.isDouble;

public class ZTuple {

	/**
	 * the names of the attributes
	 */
	private List<String> attributes;
	/**
	 * the values of the attributes
	 */
	private List<Object> values;
	/**
	 * hashtable to locate attribute names more easily
	 */
	private Map<String, Integer> searchTable;

	/**
	 * The simplest constructor
	 */
	public ZTuple() {
		attributes = new ArrayList<String>();
		values = new ArrayList<Object>();
		searchTable = new HashMap<String, Integer>();
	}

	/**
	 * Create a new tuple, given it's column names
	 * 
	 * @param colnames
	 *            Column names separated by commas (,).
	 */
	public ZTuple(String colnames) {
		this();
		StringTokenizer st = new StringTokenizer(colnames, ",");
		while (st.hasMoreTokens()) {
			setAtt(st.nextToken().trim(), null);
		}
	}

	/**
	 * Set the current tuple's column values.
	 * 
	 * @param row
	 *            Column values separated by commas (,).
	 */
	public void setRow(String row) {
		StringTokenizer st = new StringTokenizer(row, ",");
		for (int i = 0; st.hasMoreTokens(); i++) {
			final String val = st.nextToken().trim();

			if (isDouble(val)) {
				setAtt(getAttName(i), Double.valueOf(val));
			} else {
				setAtt(getAttName(i), val);
			}
		}
	}

	/**
	 * Set the current tuple's column values.
	 * 
	 * @param row
	 *            A vector of column values.
	 */
	public void setRow(List<Object> row) {
		for (int i = 0; i < row.size(); i++) {
			setAtt(getAttName(i), row.get(i));
		}
	}

	/**
	 * Set the value of the given attribute name
	 * 
	 * @param name
	 *            the string representing the attribute name
	 * @param value
	 *            the Object representing the attribute value
	 */
	public final void setAtt(String name, Object value) {
		if (name != null) {
			boolean exist = searchTable.containsKey(name);

			if (exist) {
				int i = searchTable.get(name).intValue();
				values.set(i, value);
			} else {
				int i = attributes.size();
				attributes.add(name);
				values.add(value);
				searchTable.put(name, Integer.valueOf(i));
			}
		}
	}

	/**
	 * Return the name of the attribute corresponding to the index
	 * 
	 * @param index
	 *            integer giving the index of the attribute
	 * @return a String
	 */
	public String getAttName(int index) {
		if (index >= 0 && index < attributes.size()) {
			return attributes.get(index);
		}
		return null;
	}

	/**
	 * Return the index of the attribute corresponding to the name
	 * 
	 * @param index
	 *            integer giving the index of the attribute
	 * @return the index as an int, -1 if name is not an attribute
	 */
	public int getAttIndex(String name) {
		if (name == null) {
			return -1;
		}

		Integer index = searchTable.get(name);
		if (index != null) {
			return index.intValue();
		} else {
			return -1;
		}
	}

	/**
	 * Return the value of the attribute corresponding to the index
	 * 
	 * @param index
	 *            integer giving the index of the attribute
	 * @return an Object (null if index is out of bound)
	 */
	public Object getAttValue(int index) {
		if (index >= 0 && index < values.size()) {
			return values.get(index);
		}
		return null;
	}

	/**
	 * Return the value of the attribute whith the given name
	 * 
	 * @return an Object (null if name is not an existing attribute)
	 */
	public Object getAttValue(String name) {
		boolean exist = false;

		if (name != null) {
			exist = searchTable.containsKey(name);
		}

		if (exist) {
			int index = searchTable.get(name).intValue();
			return values.get(index);
		} else {
			return null;
		}
	}

	/**
	 * To know if an attributes is already defined
	 * 
	 * @param attrName
	 *            the name of the attribute
	 * @return true if there, else false
	 */
	public boolean isAttribute(String attrName) {
		if (attrName != null) {
			return searchTable.containsKey(attrName);
		} else {
			return false;
		}
	}

	/**
	 * Return the number of attributes in the tupple
	 * 
	 * @return int the number of attributes
	 */
	public int getNumAtt() {
		return values.size();
	}

	/**
	 * Returns a string representation of the object
	 * 
	 * @return a string representation of the object
	 */
	public String toString() {
		Object att;
		Object value;
		String attS;
		String valueS;

		String ret = "[";

		if (!attributes.isEmpty()) {
			att = attributes.get(0);
			if (att == null) {
				attS = "(null)";
			} else {
				attS = att.toString();
			}

			value = values.get(0);
			if (value == null) {
				valueS = "(null)";
			} else {
				valueS = value.toString();
			}
			ret += attS + " = " + valueS;
		}

		for (int i = 1; i < attributes.size(); i++) {
			att = attributes.get(i);
			if (att == null) {
				attS = "(null)";
			} else {
				attS = att.toString();
			}

			value = values.get(i);
			if (value == null) {
				valueS = "(null)";
			} else {
				valueS = value.toString();
			}
			ret += ", " + attS + " = " + valueS;
		}
		ret += "]";
		return ret;
	}
}
