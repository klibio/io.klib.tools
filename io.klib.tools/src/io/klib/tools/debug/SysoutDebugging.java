package io.klib.tools.debug;

import java.util.Map;
import java.util.Properties;

public class SysoutDebugging {

	final static boolean DEBUG = Boolean.valueOf(System.getProperty("debug"));
	final static String LINE_SEP = System.getProperty("line.separator");

	public void printOSEnv() {
		System.out.println("# OS environment");
		Map<String, String> envMap = System.getenv();
		envMap.keySet().stream().sorted().forEach(s -> System.out.println(s + "=" + envMap.get(s)));
	}

	public void printJavaEnv() {
		System.out.println("# Java properties");
		Properties properties = System.getProperties();
		properties.keySet().stream().sorted()
				.forEach(p -> System.out.println(p + "=" + properties.getProperty((String) p)));
	}

	public void printUsage() {
		//@formatter:off
		System.out.print(LINE_SEP + "### ProcessSubstitute" + LINE_SEP
				+ "# process is running and waiting for following commands" + LINE_SEP
				+ "<string>      - print <string> on std_out" + LINE_SEP
				+ "err <string>  - print <string> on std_err" + LINE_SEP 
				+ LINE_SEP
				+ "thread <name> <sleep>  - creates a new thread with cpu load depending on the sleep time e.g. 0 means full cpu usage" + LINE_SEP
				+ LINE_SEP
				+ "debug      - print OS environment variables and Java system properties on std_out" + LINE_SEP
				+ "debugOS    - print OS environment variables on std_out" + LINE_SEP
				+ "debugJava  - print Java system properties   on std_out" + LINE_SEP
				+ LINE_SEP
				+ "stop     - print <stop> on std_out and exit application with return code 0" + LINE_SEP
				+ "errfail  - print <fail> on std_err on  exit application with return code 1" + LINE_SEP
				+ LINE_SEP
				+ "waiting commands on std_in" + LINE_SEP 
		        + LINE_SEP);
		//@formatter:on
	}


}
