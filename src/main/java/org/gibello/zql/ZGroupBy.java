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
 * ZGroupBy: an SQL GROUP BY...HAVING clause
 */
public class ZGroupBy implements java.io.Serializable {

	private static final long serialVersionUID = 1155146337535072741L;

	private List<ZExp> groupby;
	private ZExp having = null;

	/**
	 * Create a GROUP BY given a set of Expressions
	 * 
	 * @param exps
	 *            A vector of SQL Expressions (ZExp objects).
	 */
	public ZGroupBy(List<ZExp> exps) {
		groupby = exps;
	}

	/**
	 * Initiallize the HAVING part of the GROUP BY
	 * 
	 * @param e
	 *            An SQL Expression (the HAVING clause)
	 */
	public void setHaving(ZExp e) {
		having = e;
	}

	/**
	 * Get the GROUP BY expressions
	 * 
	 * @return A vector of SQL Expressions (ZExp objects)
	 */
	public List<ZExp> getGroupBy() {
		return groupby;
	}

	/**
	 * Get the HAVING clause
	 * 
	 * @return An SQL expression
	 */
	public ZExp getHaving() {
		return having;
	}

	public String toString() {
		String ret = "group by ";

		ret += groupby.get(0).toString();
		for (int i = 1; i < groupby.size(); i++) {
			ret += ", " + groupby.get(i).toString();
		}
		if (having != null) {
			ret += " having " + having.toString();
		}
		return ret;
	}
}
