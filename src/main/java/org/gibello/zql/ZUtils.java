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

import java.util.HashMap;
import java.util.Map;

public class ZUtils {
	private ZUtils() {
	}

	private static Map<String, Integer> fcts = null;

	public static final int VARIABLE_PLIST = 10000;

	public static void addCustomFunction(String fct, int nparm) {
		if (fcts == null) {
			fcts = new HashMap<String, Integer>();
		}
		if (nparm < 0) {
			nparm = 1;
		}
		fcts.put(fct.toUpperCase(), new Integer(nparm));
	}

	public static int isCustomFunction(String fct) {
		Integer nparm;
		if (fct == null || fct.length() < 1 || fcts == null || (nparm = fcts.get(fct.toUpperCase())) == null) {
			return -1;
		}
		return nparm.intValue();
	}

	public static boolean isAggregate(String op) {
		String tmp = op.toUpperCase().trim();
		return "SUM".equals(tmp) || "AVG".equals(tmp) || "MAX".equals(tmp) || "MIN".equals(tmp) || "COUNT".equals(tmp)
				|| (fcts != null && fcts.get(tmp) != null);
	}

	public static String getAggregateCall(String c) {
		int pos = c.indexOf('(');
		if (pos <= 0) {
			return null;
		}
		String call = c.substring(0, pos);
		if (ZUtils.isAggregate(call)) {
			return call.trim();
		} else {
			return null;
		}
	}

	public static boolean isDouble(final Object c) {
		return c != null && c.toString().matches("-?\\d+(\\.\\d+)?");
	}
}
