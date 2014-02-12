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
 * ZLockTable: an SQL LOCK TABLE statement
 */
public class ZLockTable implements ZStatement {
	private static final long serialVersionUID = 8699068775733575142L;

	private boolean nowait = false;
	private String lockMode = null;
	private List<String> tables = null;

	public ZLockTable() {
	}

	public void addTables(List<String> v) {
		tables = v;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setLockMode(String lc) {
		lockMode = lc;
	}

	public String getLockMode() {
		return lockMode;
	}

	public boolean isNowait() {
		return nowait;
	}

	public void setNowait(final boolean nowait) {
		this.nowait = nowait;
	}
}
