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

package org.gibello.zql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ZUpdate: an SQL UPDATE statement.
 */
public class ZUpdate implements ZStatement {
	private static final long serialVersionUID = -7716181084287664850L;

	private String table;
	private String alias = null;
	private Map<String, ZExp> set;
	private ZExp where = null;
	private List<String> columns = null;

	/**
	 * Create an UPDATE statement on a given table.
	 */
	public ZUpdate(String tab) {
		table = new String(tab);
	}

	public String getTable() {
		return table;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	/**
	 * Insert a SET... clause in the UPDATE statement
	 * 
	 * @param t
	 *            A Hashtable, where keys are column names (the columns to update), and values are ZExp objects (the
	 *            column values). For example, the values may be ZConstant objects (like "Smith") or more complex SQL
	 *            Expressions.
	 */
	public void addSet(Map<String, ZExp> t) {
		set = t;
	}

	/**
	 * Get the whole SET... clause
	 * 
	 * @return A Hashtable, where keys are column names (the columns to update), and values are ZExp objects
	 *         (Expressions that specify column values: for example, ZConstant objects like "Smith").
	 */
	public Map<String, ZExp> getSet() {
		return set;
	}

	/**
	 * Add one column=value pair to the SET... clause This method also keeps track of the column order
	 * 
	 * @param col
	 *            The column name
	 * @param val
	 *            The column value
	 */
	public void addColumnUpdate(String col, ZExp val) {
		if (set == null) {
			set = new HashMap<String, ZExp>();
		}
		set.put(col, val);
		if (columns == null) {
			columns = new ArrayList<String>();
		}
		columns.add(col);
	}

	/**
	 * Get the SQL expression that specifies a given column's update value. (for example, a ZConstant object like
	 * "Smith").
	 * 
	 * @param col
	 *            The column name.
	 * @return a ZExp, like a ZConstant representing a value, or a more complex SQL expression.
	 */
	public ZExp getColumnUpdate(String col) {
		return set.get(col);
	}

	/**
	 * Get the SQL expression that specifies a given column's update value. (for example, a ZConstant object like
	 * "Smith").<br>
	 * WARNING: This method will work only if column/value pairs have been inserted using addColumnUpdate() - otherwise
	 * it is not possible to guess what the right order is, and null will be returned.
	 * 
	 * @param num
	 *            The column index (starting from 1).
	 * @return a ZExp, like a ZConstant representing a value, or a more complex SQL expression.
	 */
	public ZExp getColumnUpdate(int index) {
		if (--index < 0) {
			return null;
		}
		if (columns == null || index >= columns.size()) {
			return null;
		}
		String col = columns.get(index);
		return set.get(col);
	}

	/**
	 * Get the column name that corresponds to a given index.<br>
	 * WARNING: This method will work only if column/value pairs have been inserted using addColumnUpdate() - otherwise
	 * it is not possible to guess what the right order is, and null will be returned.
	 * 
	 * @param num
	 *            The column index (starting from 1).
	 * @return The corresponding column name.
	 */
	public String getColumnUpdateName(int index) {
		if (--index < 0) {
			return null;
		}
		if (columns == null || index >= columns.size()) {
			return null;
		}
		return columns.get(index);
	}

	/**
	 * Returns the number of column/value pairs in the SET... clause.
	 */
	public int getColumnUpdateCount() {
		if (set == null) {
			return 0;
		}
		return set.size();
	}

	/**
	 * Insert a WHERE... clause in the UPDATE statement
	 * 
	 * @param w
	 *            An SQL Expression compatible with a WHERE... clause.
	 */
	public void addWhere(ZExp w) {
		where = w;
	}

	/**
	 * Get the WHERE clause of this UPDATE statement.
	 * 
	 * @return An SQL Expression compatible with a WHERE... clause.
	 */
	public ZExp getWhere() {
		return where;
	}

	public String toString() {
		String ret = "update " + table;
		if (alias != null) {
			ret += " " + alias;
		}
		ret += " set ";

		Iterator<String> e;
		if (columns != null) {
			e = columns.iterator();
		} else {
			e = set.keySet().iterator();
		}
		boolean first = true;
		while (e.hasNext()) {
			String key = e.next();
			if (!first) {
				ret += ", ";
			}
			ret += key + "=" + set.get(key).toString();
			first = false;
		}

		if (where != null) {
			ret += " where " + where.toString();
		}
		return ret;
	}
}
