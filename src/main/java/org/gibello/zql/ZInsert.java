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

import java.util.List;

/**
 * ZInsert: an SQL INSERT statement
 */
public class ZInsert implements ZStatement {
	private static final long serialVersionUID = 2534470365222319553L;

	private final String table;
	private List<String> columns = null;
	private ZExp valueSpec = null;

	/**
	 * Create an INSERT statement on a given table
	 */
	public ZInsert(String tab) {
		table = tab;
	}

	/**
	 * Get the name of the table involved in the INSERT statement.
	 * 
	 * @return A String equal to the table name
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Get the columns involved in the INSERT statement.
	 * 
	 * @return A Vector of Strings equal to the column names
	 */
	public List<String> getColumns() {
		return columns;
	}

	/**
	 * Specify which columns to insert
	 * 
	 * @param c
	 *            A vector of column names (Strings)
	 */
	public void addColumns(List<String> c) {
		columns = c;
	}

	/**
	 * Specify the VALUES part or SQL sub-query of the INSERT statement
	 * 
	 * @param e
	 *            An SQL expression or a SELECT statement. If it is a list of SQL expressions, e should be represented
	 *            by ONE SQL expression with operator = "," and operands = the expressions in the list. If it is a
	 *            SELECT statement, e should be a ZQuery object.
	 */
	public void addValueSpec(ZExp e) {
		valueSpec = e;
	}

	/**
	 * Get the VALUES part of the INSERT statement
	 * 
	 * @return A vector of SQL Expressions (ZExp objects); If there's no VALUES but a subquery, returns null (use
	 *         getQuery() method).
	 */
	public List<ZExp> getValues() {
		if (!(valueSpec instanceof ZExpression)) {
			return null;
		}
		return ((ZExpression) valueSpec).getOperands();
	}

	/**
	 * Get the sub-query (ex. in INSERT INTO table1 SELECT * FROM table2;, the sub-query is SELECT * FROM table2;)
	 * 
	 * @return A ZQuery object (A SELECT statement), or null if there's no sub-query (in that case, use the getValues()
	 *         method to get the VALUES part).
	 */
	public ZQuery getQuery() {
		if (!(valueSpec instanceof ZQuery)) {
			return null;
		}
		return (ZQuery) valueSpec;
	}

	public String toString() {
		String ret = "insert into " + table;
		if (columns != null && columns.size() > 0) {

			ret += "(" + columns.get(0);
			for (int i = 1; i < columns.size(); i++) {
				ret += "," + columns.get(i);
			}
			ret += ")";
		}

		String vlist = valueSpec.toString();
		ret += " ";
		if (getValues() != null) {
			ret += "values ";
		}
		if (vlist.startsWith("(")) {
			ret += vlist;
		} else {
			ret += " (" + vlist + ")";
		}
		return ret;
	}
}
