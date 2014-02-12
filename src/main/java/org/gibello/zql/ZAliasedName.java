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

import java.util.StringTokenizer;

/**
 * A name/alias association<br>
 * Names can have two forms:
 * <ul>
 * <li>FORM_TABLE for table names ([schema.]table)</li>
 * <li>FORM_COLUMN for column names ([[schema.]table.]column)</li>
 * </ul>
 */
public class ZAliasedName implements java.io.Serializable {
	private static final long serialVersionUID = -7797251652151607601L;

	String strform_ = "";
	String schema_ = null;
	String table_ = null;
	String column_ = null;
	String alias_ = null;

	public static enum Form {
		TABLE, COLUMN
	};

	Form form_ = Form.COLUMN;

	public ZAliasedName() {
	}

	/**
	 * Create a new ZAliasedName given it's full name.
	 * 
	 * @param fullname
	 *            The full name: [[schema.]table.]column
	 * @param form
	 *            The name form (FORM_TABLE or FORM_COLUMN)
	 */
	public ZAliasedName(String fullname, Form form) {

		form_ = form;
		strform_ = fullname;

		StringTokenizer st = new StringTokenizer(fullname, ".");
		switch (st.countTokens()) {
		case 1:
			if (form == Form.TABLE) {
				table_ = st.nextToken();
			} else {
				column_ = st.nextToken();
			}
			break;
		case 2:
			if (form == Form.TABLE) {
				schema_ = st.nextToken();
				table_ = st.nextToken();
			} else {
				table_ = st.nextToken();
				column_ = st.nextToken();
			}
			break;
		case 3:
		default:
			schema_ = st.nextToken();
			table_ = st.nextToken();
			column_ = st.nextToken();
			break;
		}
		schema_ = postProcess(schema_);
		table_ = postProcess(table_);
		column_ = postProcess(column_);
	}

	private String postProcess(String val) {
		if (val == null) {
			return null;
		}

		if (val.indexOf("(") >= 0) {
			val = val.substring(val.lastIndexOf("(") + 1);
		}

		if (val.indexOf(")") >= 0) {
			val = val.substring(0, val.indexOf(")"));
		}
		return val.trim();
	}

	public String toString() {
		if (alias_ == null) {
			return strform_;
		} else {
			return strform_ + " " + alias_;
		}
	}

	/**
	 * @return If the name is of the form schema.table.column, returns the schema part
	 */
	public String getSchema() {
		return schema_;
	}

	/**
	 * @return If the name is of the form [schema.]table.column, returns the schema part
	 */
	public String getTable() {
		return table_;
	}

	/**
	 * @return The name is of the form [[schema.]table.]column: return the column part
	 */
	public String getColumn() {
		return column_;
	}

	/**
	 * @return true if column is "*", false otherwise. Example: *, table.* are wildcards.
	 */
	public boolean isWildcard() {
		if (form_ == Form.TABLE) {
			return table_ != null && table_.equals("*");
		} else {
			return column_ != null && column_.indexOf('*') >= 0;
		}
	}

	/**
	 * @return the alias associated to the current name.
	 */
	public String getAlias() {
		return alias_;
	}

	/**
	 * Associate an alias with the current name.
	 * 
	 * @param a
	 *            the alias associated to the current name.
	 */
	public void setAlias(String a) {
		alias_ = a;
	}
}
