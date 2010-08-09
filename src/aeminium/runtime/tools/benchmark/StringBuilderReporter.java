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
