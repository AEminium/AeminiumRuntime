/**
 * Copyright (c) 2010-11 The AEminium Project (see AUTHORS file)
 *
 * This file is part of Plaid Programming Language.
 *
 * Plaid Programming Language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  Plaid Programming Language is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaid Programming Language.  If not, see <http://www.gnu.org/licenses/>.
 */

package aeminium.runtime.tools.benchmark;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringBuilderReporter implements Reporter {
	final private StringBuilder sb = new StringBuilder();
	final private String EOL = System.getProperty("line.separator");
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Override
	public void reportLn(String line) {
		sb.append(line + EOL);
	}

	@Override
	public void startBenchmark(String name) {
		reportLn(String.format("# Benchmark : %15s", name));
		reportLn(String.format("# Date      : %s",  getDate()));
	}

	@Override
	public void stopBenchmark(String name) {
	}

	@Override
	public void flush() {
		System.out.print(sb.toString());
		sb.setLength(0);
	}

	protected String getDate() {
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(calender.getTime());
	}
}
