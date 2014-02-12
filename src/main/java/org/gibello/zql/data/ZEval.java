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

package org.gibello.zql.data;

import static org.gibello.zql.ZUtils.isDouble;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.List;

import org.gibello.zql.ZConstant;
import org.gibello.zql.ZExp;
import org.gibello.zql.ZExpression;
import org.gibello.zql.ZqlParser;

/**
 * Evaluate SQL expressions
 */
public class ZEval {

	/**
	 * Evaluate a boolean expression to true or false (for example, SQL WHERE clauses are boolean expressions)
	 * 
	 * @param tuple
	 *            The tuple on which to evaluate the expression
	 * @param exp
	 *            The expression to evaluate
	 * @return true if the expression evaluate to true for this tuple, false if not.
	 */
	public boolean eval(ZTuple tuple, ZExp exp) throws SQLException {

		if (tuple == null || exp == null) {
			throw new SQLException("ZEval.eval(): null argument or operator");
		}
		if (!(exp instanceof ZExpression)) {
			throw new SQLException("ZEval.eval(): only expressions are supported");
		}

		ZExpression pred = (ZExpression) exp;
		String op = pred.getOperator();

		switch (op) {

		case "AND":
			boolean and = true;
			for (int i = 0; and && i < pred.nbOperands(); i++) {
				and &= eval(tuple, pred.getOperand(i));
			}
			return and;
		case "OR":
			boolean or = false;
			for (int i = 0; i < pred.nbOperands(); i++) {
				or |= eval(tuple, pred.getOperand(i));
			}
			return or;
		case "NOT":
			return !eval(tuple, pred.getOperand(0));
		case "=":
			return evalCmp(tuple, pred.getOperands()) == 0;
		case "!=":
			return evalCmp(tuple, pred.getOperands()) != 0;
		case "<>":
			return evalCmp(tuple, pred.getOperands()) != 0;
		case "#":
			throw new SQLException("ZEval.eval(): Operator # not supported");
		case ">":
			return evalCmp(tuple, pred.getOperands()) > 0;
		case ">=":
			return evalCmp(tuple, pred.getOperands()) >= 0;
		case "<":
			return evalCmp(tuple, pred.getOperands()) < 0;
		case "<=":
			return evalCmp(tuple, pred.getOperands()) <= 0;
		case "BETWEEN":
		case "NOT BETWEEN":
			ZExpression newexp = new ZExpression("AND", new ZExpression(">=", pred.getOperand(0), pred.getOperand(1)),
					new ZExpression("<=", pred.getOperand(0), pred.getOperand(2)));

			if ("NOT BETWEEN".equals(op)) {
				return !eval(tuple, newexp);
			} else {
				return eval(tuple, newexp);
			}

		case "LIKE":
		case "NOT LIKE":
			boolean like = evalLike(tuple, pred.getOperands());
			return "LIKE".equals(op) ? like : !like;

		case "IN":
		case "NOT IN":
			newexp = new ZExpression("OR");

			for (int i = 1; i < pred.nbOperands(); i++) {
				newexp.addOperand(new ZExpression("=", pred.getOperand(0), pred.getOperand(i)));
			}

			if ("NOT IN".equals(op)) {
				return !eval(tuple, newexp);
			} else {
				return eval(tuple, newexp);
			}

		case "IS NULL":
			if (pred.nbOperands() <= 0 || pred.getOperand(0) == null) {
				return true;
			}
			ZExp x = pred.getOperand(0);
			if (x instanceof ZConstant) {
				return ((ZConstant) x).getType() == ZConstant.Type.NULL;
			} else {
				throw new SQLException("ZEval.eval(): can't eval IS (NOT) NULL");
			}

		case "IS NOT NULL":
			ZExpression xs = new ZExpression("IS NULL");
			xs.setOperands(pred.getOperands());
			return !eval(tuple, xs);

		default:
			throw new SQLException("ZEval.eval(): Unknown operator " + op);
		}

	}

	double evalCmp(ZTuple tuple, List<ZExp> operands) throws SQLException {

		if (operands.size() < 2) {
			throw new SQLException("ZEval.evalCmp(): Trying to compare less than two values");
		}
		if (operands.size() > 2) {
			throw new SQLException("ZEval.evalCmp(): Trying to compare more than two values");
		}

		Object o1 = null, o2 = null;

		o1 = evalExpValue(tuple, operands.get(0));
		o2 = evalExpValue(tuple, operands.get(1));

		if (o1 instanceof String || o2 instanceof String) {
			return o1.equals(o2) ? 0 : -1;
		}

		if (o1 instanceof Number && o2 instanceof Number) {
			return ((Number) o1).doubleValue() - ((Number) o2).doubleValue();
		} else {
			throw new SQLException("ZEval.evalCmp(): can't compare (" + o1.toString() + ") with (" + o2.toString()
					+ ")");
		}
	}

	// -------------------------------------------------------------------------
	/**
	 * evalLike evaluates the LIKE operand
	 * 
	 * @param tuple
	 *            the tuple to evaluate
	 * @param operands
	 *            the operands
	 * @return true-> the expression matches
	 * @throws SQLException
	 */
	private boolean evalLike(ZTuple tuple, List<ZExp> operands) throws SQLException {
		if (operands.size() < 2) {
			throw new SQLException("ZEval.evalCmp(): Trying to compare less than two values");
		}
		if (operands.size() > 2) {
			throw new SQLException("ZEval.evalCmp(): Trying to compare more than two values");
		}

		Object o1 = evalExpValue(tuple, (ZExp) operands.get(0));
		Object o2 = evalExpValue(tuple, (ZExp) operands.get(1));

		if ((o1 instanceof String) && (o2 instanceof String)) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			if (s2.startsWith("%")) {
				return s1.endsWith(s2.substring(1));
			} else if (s2.endsWith("%")) {
				return s1.startsWith(s2.substring(0, s2.length() - 1));
			} else {
				return s1.equalsIgnoreCase(s2);
			}
		} else {
			throw new SQLException("ZEval.evalLike(): LIKE can only compare strings");
		}

	}

	double evalNumericExp(ZTuple tuple, ZExpression exp) throws SQLException {

		if (tuple == null || exp == null || exp.getOperator() == null) {
			throw new SQLException("ZEval.eval(): null argument or operator");
		}

		String op = exp.getOperator();

		Object o1 = evalExpValue(tuple, exp.getOperand(0));
		if (!(o1 instanceof Double)) {
			throw new SQLException("ZEval.evalNumericExp(): expression not numeric");
		}
		Double dobj = (Double) o1;

		switch (op) {
		case "+":
			double val = dobj.doubleValue();
			for (int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, exp.getOperand(i));
				val += ((Number) obj).doubleValue();
			}
			return val;
		case "-":
			val = dobj.doubleValue();
			if (exp.nbOperands() == 1) {
				return -val;
			}
			for (int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, exp.getOperand(i));
				val -= ((Number) obj).doubleValue();
			}
			return val;

		case "*":
			val = dobj.doubleValue();
			for (int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, exp.getOperand(i));
				val *= ((Number) obj).doubleValue();
			}
			return val;
		case "/":
			val = dobj.doubleValue();
			for (int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, exp.getOperand(i));
				val /= ((Number) obj).doubleValue();
			}
			return val;
		case "**":
			val = dobj.doubleValue();
			for (int i = 1; i < exp.nbOperands(); i++) {
				Object obj = evalExpValue(tuple, exp.getOperand(i));
				val = Math.pow(val, ((Number) obj).doubleValue());
			}
			return val;
		default:
			throw new SQLException("ZEval.evalNumericExp(): Unknown operator " + op);
		}
	}

	/**
	 * Evaluate a numeric or string expression (example: a+1)
	 * 
	 * @param tuple
	 *            The tuple on which to evaluate the expression
	 * @param exp
	 *            The expression to evaluate
	 * @return The expression's value
	 */
	public Object evalExpValue(ZTuple tuple, ZExp exp) throws SQLException {

		Object o2 = null;

		if (exp instanceof ZConstant) {

			ZConstant c = (ZConstant) exp;

			switch (c.getType()) {

			case COLUMNNAME:

				Object o1 = tuple.getAttValue(c.getValue());
				if (o1 == null) {
					throw new SQLException("ZEval.evalExpValue(): unknown column " + c.getValue());
				}

				if (isDouble(o1)) {
					o2 = Double.valueOf(o1.toString());
				} else {
					o2 = o1;
				}
				break;

			case NUMBER:
				o2 = new Double(c.getValue());
				break;

			case STRING:
			default:
				o2 = c.getValue();
				break;
			}
		} else if (exp instanceof ZExpression) {
			o2 = new Double(evalNumericExp(tuple, (ZExpression) exp));
		}
		return o2;
	}

	// test
	public static void main(String args[]) throws Exception {
		BufferedReader db = new BufferedReader(new FileReader("test.db"));
		String tpl = db.readLine();
		ZTuple t = new ZTuple(tpl);

		ZqlParser parser = new ZqlParser();
		ZEval evaluator = new ZEval();

		while ((tpl = db.readLine()) != null) {
			t.setRow(tpl);
			BufferedReader sql = new BufferedReader(new FileReader("test.sql"));
			String query;
			while ((query = sql.readLine()) != null) {
				parser.initParser(new ByteArrayInputStream(query.getBytes()));
				ZExp exp = parser.readExpression();
				System.out.print(tpl + ", " + query + ", ");
				System.out.println(evaluator.eval(t, exp));
			}
			sql.close();
		}
		db.close();
	}
}
