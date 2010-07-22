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
		rt = Factory.getRuntime("default", Factory.getFlagsFromEnvironment());
	}

	protected Runtime getRuntime() {
		return rt;
	}

	protected Logger getLogger() {
		return log;
	}
}
