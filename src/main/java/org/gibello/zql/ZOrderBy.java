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
 * An SQL query ORDER BY clause.
 */
public class ZOrderBy implements java.io.Serializable {
	private static final long serialVersionUID = 8314427185544549626L;

	private final ZExp exp;
	private boolean asc = true;

	public ZOrderBy(ZExp e) {
		exp = e;
	}

	/**
	 * Set the order to ascending or descending (defailt is ascending order).
	 * 
	 * @param a
	 *            true for ascending order, false for descending order.
	 */
	public void setAscOrder(boolean a) {
		asc = a;
	}

	/**
	 * Get the order (ascending or descending)
	 * 
	 * @return true if ascending order, false if descending order.
	 */
	public boolean getAscOrder() {
		return asc;
	}

	/**
	 * Get the ORDER BY expression.
	 * 
	 * @return An expression (generally, a ZConstant that represents a column name).
	 */
	public ZExp getExpression() {
		return exp;
	}

	public String toString() {
		return exp.toString() + " " + (asc ? "ASC" : "DESC");
	}
}
