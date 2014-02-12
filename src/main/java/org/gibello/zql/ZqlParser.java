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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * ZqlParser: an SQL parser
 */
public class ZqlParser {

	private ZqlJJParser parser = null;

	/**
	 * Test program: Parses SQL statements from stdin or from a text file.<br>
	 * If the program receives one argument, it is an SQL text file name; if there's no argument, the program reads from
	 * stdin.
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(String args[]) throws ParseException, FileNotFoundException {

		ZqlParser p = null;

		if (args.length < 1) {
			System.out.println("/* Reading from stdin (exit; to finish) */");
			p = new ZqlParser(System.in);

		} else {

			File f = new File(args[0]);

			if (!f.exists()) {
				System.out.println("/* File " + args[0] + " not found. Reading from stdin */");
				p = new ZqlParser(System.in);
			} else {
				p = new ZqlParser(new DataInputStream(new FileInputStream(f)));
			}
		} // else ends here

		if (args.length > 0) {
			System.out.println("/* Reading from " + args[0] + "*/");
		}

		ZStatement st = null;
		while ((st = p.readStatement()) != null) {
			System.out.println(st.toString() + ";");
		}

		System.out.println("exit;");
		System.out.println("/* Parse Successful */");

	} // main ends here

	/**
	 * Create a new parser to parse SQL statements from a given input stream.
	 * 
	 * @param in
	 *            The InputStream from which SQL statements will be read.
	 */
	public ZqlParser(InputStream in) {
		initParser(in);
	}

	/**
	 * Create a new parser: before use, call initParser(InputStream) to specify an input stream for the parsing.
	 */
	public ZqlParser() {
	}

	/**
	 * Initialize (or re-initialize) the input stream for the parser.
	 */
	public void initParser(InputStream in) {
		if (parser == null) {
			parser = new ZqlJJParser(in);
		} else {
			parser.ReInit(in);
		}
	}

	public void addCustomFunction(String fct, int nparm) {
		ZUtils.addCustomFunction(fct, nparm);
	}

	/**
	 * Parse an SQL Statement from the parser's input stream.
	 * 
	 * @return An SQL statement, or null if there's no more statement.
	 */
	public ZStatement readStatement() throws ParseException {
		if (parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		return parser.SQLStatement();
	}

	/**
	 * Parse a set of SQL Statements from the parser's input stream (all the available statements are parsed and
	 * returned).
	 * 
	 * @return A vector of ZStatement objects (SQL statements).
	 */
	public List<ZStatement> readStatements() throws ParseException {
		if (parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		return parser.SQLStatements();
	}

	/**
	 * Parse an SQL Expression (like the WHERE clause of an SQL query).
	 * 
	 * @return An SQL expression.
	 */
	public ZExp readExpression() throws ParseException {
		if (parser == null) {
			throw new ParseException("Parser not initialized: use initParser(InputStream);");
		}
		return parser.SQLExpression();
	}
}
