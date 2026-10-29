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

package aeminium.runtime.tests;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Before;

import aeminium.runtime.Runtime;
import aeminium.runtime.implementations.Factory;

public abstract class BaseTest {
	private Runtime rt;
	private Logger log;

	public BaseTest() {
		log = Logger.getLogger(this.getClass().getName());
		Handler conHdlr = new ConsoleHandler();
		conHdlr.setFormatter(new Formatter() {
			public String format(LogRecord record) {
				return "TEST " + record.getLevel() + "  :  "
				+ record.getMessage() + "\n";
			}
		});
		log.setUseParentHandlers(false);
		log.addHandler(conHdlr);
		log.setLevel(Level.INFO);
	}

	@Before
	public void setUp() throws Exception {
		rt = Factory.getRuntime();
	}

	protected Runtime getRuntime() {
		return rt;
	}

	protected Logger getLogger() {
		return log;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
