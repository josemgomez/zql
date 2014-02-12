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
 * ZConstant: a representation of SQL constants
 */
public class ZConstant implements ZExp {

	private static final long serialVersionUID = 5152573166867973827L;

	/**
	 * ZConstant types
	 */
	public static enum Type {
		UNKNOWN, COLUMNNAME, NULL, NUMBER, STRING
	};

	final Type type_;
	final String val_;

	/**
	 * Create a new constant, given its name and type.
	 */
	public ZConstant(String v, Type typ) {
		val_ = v;
		type_ = typ;
	}

	/*
	 * @return the constant value
	 */
	public String getValue() {
		return val_;
	}

	/*
	 * @return the constant type
	 */
	public Type getType() {
		return type_;
	}

	public String toString() {
		if (type_ == Type.STRING) {
			return '\'' + val_ + '\'';
		} else {
			return val_;
		}
	}
}
