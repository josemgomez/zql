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
import java.util.List;

/**
 * ZExpression: an SQL Expression An SQL expression is an operator and one or more operands Example: a AND b AND c ->
 * operator = AND, operands = (a, b, c)
 */
public class ZExpression implements ZExp {
	private static final long serialVersionUID = 3034365237806330384L;

	private final String op;
	private final List<ZExp> operands = new ArrayList<ZExp>();

	/**
	 * Create an SQL Expression given the operator
	 * 
	 * @param op
	 *            The operator
	 */
	public ZExpression(String op) {
		this.op = op;
	}

	/**
	 * Create an SQL Expression given the operator and 1st operand
	 * 
	 * @param op
	 *            The operator
	 * @param o1
	 *            The 1st operand
	 */
	public ZExpression(String op, ZExp o1) {
		this.op = op;
		addOperand(o1);
	}

	/**
	 * Create an SQL Expression given the operator, 1st and 2nd operands
	 * 
	 * @param op
	 *            The operator
	 * @param o1
	 *            The 1st operand
	 * @param o2
	 *            The 2nd operand
	 */
	public ZExpression(String op, ZExp o1, ZExp o2) {
		this.op = op;
		addOperand(o1);
		addOperand(o2);
	}

	/**
	 * Get this expression's operator.
	 * 
	 * @return the operator.
	 */
	public String getOperator() {
		return op;
	}

	/**
	 * Set the operands list
	 * 
	 * @param v
	 *            A vector that contains all operands (ZExp objects).
	 */
	public void setOperands(List<ZExp> v) {
		operands.clear();
		operands.addAll(v);
	}

	/**
	 * Get this expression's operands.
	 * 
	 * @return the operands (as a Vector of ZExp objects).
	 */
	public List<ZExp> getOperands() {
		return operands;
	}

	/**
	 * Add an operand to the current expression.
	 * 
	 * @param o
	 *            The operand to add.
	 */
	public final void addOperand(ZExp o) {
		operands.add(o);
	}

	/**
	 * Get an operand according to its index (position).
	 * 
	 * @param pos
	 *            The operand index, starting at 0.
	 * @return The operand at the specified index, null if out of bounds.
	 */
	public ZExp getOperand(int pos) {
		if (operands == null || pos >= operands.size()) {
			return null;
		}
		return operands.get(pos);
	}

	/**
	 * Get the number of operands
	 * 
	 * @return The number of operands
	 */
	public int nbOperands() {
		if (operands == null) {
			return 0;
		}
		return operands.size();
	}

	/**
	 * String form of the current expression (reverse polish notation). Example: a > 1 AND b = 2 -> (AND (> a 1) (= b
	 * 2))
	 * 
	 * @return The current expression in reverse polish notation (a String)
	 */
	public String toReversePolish() {
		String ret = "(";
		ret += op;
		for (int i = 0; i < nbOperands(); i++) {
			ZExp opr = getOperand(i);
			if (opr instanceof ZExpression) {
				ret += " " + ((ZExpression) opr).toReversePolish();
				// Warning recursive call
			} else if (opr instanceof ZQuery) {
				ret += " (" + opr.toString() + ")";
			} else {
				ret += " " + opr.toString();
			}
		}
		ret += ")";
		return ret;
	}

	public String toString() {
		if ("?".equals(op)) {
			return op; // For prepared columns ("?")
		}
		if (ZUtils.isCustomFunction(op) >= 0) {
			return formatFunction();
		}

		String ret = "";
		if (needPar(op)) {
			ret += "(";
		}

		ZExp operand;
		switch (nbOperands()) {

		case 1:
			operand = getOperand(0);
			if (operand instanceof ZConstant) {
				// Operator may be an aggregate function (MAX, SUM...)
				if (ZUtils.isAggregate(op)) {
					ret += op + "(" + operand.toString() + ")";
				} else if ("IS NULL".equals(op) || "IS NOT NULL".equals(op)) {
					ret += operand.toString() + " " + op;
					// "," = list of values, here just one single value
				} else if (",".equals(op)) {
					ret += operand.toString();
				} else {
					ret += op + " " + operand.toString();
				}
			} else if (operand instanceof ZQuery) {
				ret += op + " (" + operand.toString() + ")";
			} else {
				if ("IS NULL".equals(op) || "IS NOT NULL".equals(op)) {
					ret += operand.toString() + " " + op;
					// "," = list of values, here just one single value
				} else if (",".equals(op)) {
					ret += operand.toString();
				} else {
					ret += op + " " + operand.toString();
				}
			}
			break;

		case 3:
			if (op.toUpperCase().endsWith("BETWEEN")) {
				ret += getOperand(0).toString() + " " + op + " " + getOperand(1).toString() + " AND "
						+ getOperand(2).toString();
				break;
			}
		default:

			boolean inOp = "IN".equals(op) || "NOT IN".equals(op);

			int nb = nbOperands();
			for (int i = 0; i < nb; i++) {

				if (inOp && i == 1) {
					ret += " " + op + " (";
				}

				operand = getOperand(i);
				if (operand instanceof ZQuery && !inOp) {
					ret += "(" + operand.toString() + ")";
				} else {
					ret += operand.toString();
				}
				if (i < nb - 1) {
					if (",".equals(op) || (inOp && i > 0)) {
						ret += ", ";
					} else if (!inOp) {
						ret += " " + op + " ";
					}
				}
			}
			if (inOp) {
				ret += ")";
			}
			break;
		}

		if (needPar(op)) {
			ret += ")";
		}
		return ret;
	}

	private boolean needPar(String op) {
		String tmp = op.toUpperCase();
		return !("ANY".equals(tmp) || "ALL".equals(tmp) || "UNION".equals(tmp) || ZUtils.isAggregate(tmp));
	}

	private String formatFunction() {
		String ret = op + "(";
		int nb = nbOperands();
		for (int i = 0; i < nb; i++) {
			ret += getOperand(i).toString() + (i < nb - 1 ? "," : "");
		}
		ret += ")";
		return ret;
	}
}