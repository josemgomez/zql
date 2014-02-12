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
 * ZQuery: an SQL SELECT statement
 */
public class ZQuery implements ZStatement, ZExp {
	private static final long serialVersionUID = 7561837297177401348L;

	private List<ZSelectItem> select;
	private boolean distinct = false;
	private List<ZFromItem> from;
	private ZExp where = null;
	private ZGroupBy groupby = null;
	private ZExpression setclause = null;
	private List<ZOrderBy> orderby = null;
	private boolean forupdate = false;

	/**
	 * Create a new SELECT statement
	 */
	public ZQuery() {
	}

	/**
	 * Insert the SELECT part of the statement
	 * 
	 * @param s
	 *            A vector of ZSelectItem objects
	 */
	public void addSelect(List<ZSelectItem> s) {
		this.select = s;
	}

	/**
	 * Insert the FROM part of the statement
	 * 
	 * @param f
	 *            a Vector of ZFromItem objects
	 */
	public void addFrom(List<ZFromItem> f) {
		from = f;
	}

	/**
	 * Insert a WHERE clause
	 * 
	 * @param w
	 *            An SQL Expression
	 */
	public void addWhere(ZExp w) {
		where = w;
	}

	/**
	 * Insert a GROUP BY...HAVING clause
	 * 
	 * @param g
	 *            A GROUP BY...HAVING clause
	 */
	public void addGroupBy(ZGroupBy g) {
		groupby = g;
	}

	/**
	 * Insert a SET clause (generally UNION, INTERSECT or MINUS)
	 * 
	 * @param s
	 *            An SQL Expression (generally UNION, INTERSECT or MINUS)
	 */
	public void addSet(ZExpression s) {
		setclause = s;
	}

	/**
	 * Insert an ORDER BY clause
	 * 
	 * @param v
	 *            A vector of ZOrderBy objects
	 */
	public void addOrderBy(List<ZOrderBy> v) {
		orderby = v;
	}

	/**
	 * Get the SELECT part of the statement
	 * 
	 * @return A vector of ZSelectItem objects
	 */
	public List<ZSelectItem> getSelect() {
		return select;
	}

	/**
	 * Get the FROM part of the statement
	 * 
	 * @return A vector of ZFromItem objects
	 */
	public List<ZFromItem> getFrom() {
		return from;
	}

	/**
	 * Get the WHERE part of the statement
	 * 
	 * @return An SQL Expression or sub-query (ZExpression or ZQuery object)
	 */
	public ZExp getWhere() {
		return where;
	}

	/**
	 * Get the GROUP BY...HAVING part of the statement
	 * 
	 * @return A GROUP BY...HAVING clause
	 */
	public ZGroupBy getGroupBy() {
		return groupby;
	}

	/**
	 * Get the SET clause (generally UNION, INTERSECT or MINUS)
	 * 
	 * @return An SQL Expression (generally UNION, INTERSECT or MINUS)
	 */
	public ZExpression getSet() {
		return setclause;
	}

	/**
	 * Get the ORDER BY clause
	 * 
	 * @param v
	 *            A vector of ZOrderBy objects
	 */
	public List<ZOrderBy> getOrderBy() {
		return orderby;
	}

	/**
	 * @return true if it is a SELECT DISTINCT query, false otherwise.
	 */
	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * @return true if it is a FOR UPDATE query, false otherwise.
	 */
	public boolean isForUpdate() {
		return forupdate;
	}
	
	public void setForUpdate(final boolean forupdate) {
		this.forupdate = forupdate;
	}

	public String toString() {
		String ret = "select ";
		if (distinct) {
			ret += "distinct ";
		}

		int i;
		ret += select.get(0).toString();
		for (i = 1; i < select.size(); i++) {
			ret += ", " + select.get(i).toString();
		}

		ret += " from " + from.get(0).toString();
		for (i = 1; i < from.size(); i++) {
			ret += ", " + from.get(i).toString();
		}

		if (where != null) {
			ret += " where " + where.toString();
		}
		if (groupby != null) {
			ret += " " + groupby.toString();
		}
		if (setclause != null) {
			ret += " " + setclause.toString();
		}
		if (orderby != null) {
			ret += " order by ";
			ret += orderby.get(0).toString();
			for (i = 1; i < orderby.size(); i++) {
				ret += ", " + orderby.get(i).toString();
			}
		}
		if (forupdate) {
			ret += " for update";
		}

		return ret;
	}
}
