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

/**
 * ZSelectItem: an item in the SELECT part of an SQL query. (The SELECT part of a query is a Vector of ZSelectItem).
 */
public class ZSelectItem extends ZAliasedName {
	private static final long serialVersionUID = -7275987727679325506L;

	private ZExp expression = null;
	private String aggregate = null;

	/**
	 * Create a new SELECT item
	 */
	public ZSelectItem() {
		super();
	}

	/**
	 * Create a new SELECT item, given its name (for column names and wildcards).
	 * 
	 * @param fullname
	 *            A string that represents a column name or wildcard (example: a.*).
	 */
	public ZSelectItem(String fullname) {
		super(fullname, ZAliasedName.Form.COLUMN);
		setAggregate(ZUtils.getAggregateCall(fullname)); // PY.Gibello 21 Apr
		// 2001
	}

	/**
	 * @return An SQL Expression if this SELECT item is an expression, a ZConstant if it is a column name, null if it is
	 *         a wildcard
	 */
	public ZExp getExpression() {
		if (isExpression()) {
			return expression;
		} else if (isWildcard()) {
			return null;
		} else {
			return new ZConstant(getColumn(), ZConstant.Type.COLUMNNAME);
		}
	}

	/**
	 * Initialize this SELECT item as an SQL expression (not a column name nor wildcard) Example: SELECT a+b FROM
	 * table1; (a+b is an expression)
	 */
	public void setExpression(ZExp e) {
		expression = e;
		setStrform(expression.toString());
	}

	/**
	 * @return true if this item is an SQL expression, false if not. (Example: SELECT a+b, c FROM num; -> a+b is an
	 *         expression, not c)
	 */
	public boolean isExpression() {
		return expression instanceof ZExpression;
	}

	/**
	 * Initialize an aggregate function on this item (generally SUM, AVG, MAX, MIN) Example: SELECT AVG(age) FROM
	 * people; -> The aggregate function is AVG.
	 * 
	 * @param a
	 *            The name of the aggregate function (a String, like SUM, AVG, MAX, MIN)
	 */
	public final void setAggregate(String a) {
		aggregate = a;
	}

	/**
	 * If this item is an aggregate function, return the function name.
	 * 
	 * @return The name of an aggregate function (generally SUM, AVG, MAX, MIN), or null if there's no aggregate.
	 *         Example: SELECT name, AVG(age) FROM people; -> null for the "name" item, and "AVG" for the "AVG(age)"
	 *         item.
	 */
	public String getAggregate() {
		return aggregate;
	}

	/**
	 * TBD public String toString() { String agg = getAggregate(); if(agg == null) agg = ""; return agg +
	 * super.toString(); }
	 **/
}
