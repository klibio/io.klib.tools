package io.klib.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

import io.klib.tools.cpu.FakeLoad;
import io.klib.tools.debug.SysoutDebugging;

/**
 * 
 * This process reads from the <b>std_in</b> the input string and print it on.
 * <b>std_out</b><br>
 * if the <b>input string</b> is one of the following strings the behavior is
 * different
 * <ul>
 * <li><code><b>stop</b></code> - then the application exits normally (return
 * code 0)</li>
 * <li><code><b>errfail</b></code> - then the application exits (return code 1)
 * </li>
 * <li>starts with <code><b>err</b></code> - the following characters are
 * printed on <b>std_err</b></li>
 * </ul>
 *
 */
public class ProcessSubstitute {

	final static boolean DEBUG = Boolean.valueOf(System.getProperty("debug"));
	private static Thread stdInRead;
	private static SysoutDebugging sysoutDebugging = new SysoutDebugging();

	public static void main(String[] args) {

		new SysoutDebugging().printUsage();

		stdInRead = new Thread("readStdIn") {
			@Override
			public void run() {
				printPrompt();
				for (;;) {
					try {
						while (System.in.available() > 0) {
							Scanner s = new Scanner(System.in);
							s.useDelimiter(Pattern.compile("\r\n?|\n"));
							while (s.hasNext()) {
								if (process(s.next())) {
									printPrompt();
								}
							}
							s.close();
						}
						Thread.sleep(100);
					} catch (InterruptedException | IOException e) {
						e.printStackTrace();
						break;
					}
				}
			}

			private void printPrompt() {
				if (DEBUG)
					System.out.print("sin ");
				System.out.print("> ");
			}

		};
		stdInRead.start();
	}

	private static boolean process(String next) {
		boolean running = true;
		String[] line = next.split(" ");

		String command = line[0];
		if (!command.startsWith("#")) {
			switch (command) {
			case "thread":
				String name = line[1];
				int sleep = Integer.parseInt(line[2]);
				new FakeLoad().createThread(name, sleep);
				break;

			case "debug":
				sysoutDebugging.printOSEnv();
				sysoutDebugging.printJavaEnv();
				break;

			case "debugOS":
				sysoutDebugging.printOSEnv();
				break;

			case "debugJava":
				sysoutDebugging.printJavaEnv();
				break;

			case "wait":
				if (DEBUG)
					System.out.print("sout ");
				int wait = Integer.parseInt(line[1]);
				System.out.println("thread is sleeping for ms " + wait);
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// nothing to do
				}
				break;

			case "stop":
				if (DEBUG)
					System.out.print("sout ");
				System.out.println(next);
				running = false;
				System.exit(0);
				break;

			case "err":
				if (DEBUG)
					System.err.print("serr ");
				String[] msg = Arrays.copyOfRange(line, 1, line.length);
				Arrays.stream(msg).forEach(s -> System.err.print(s.toString() + "		 "));
				System.err.println();
				break;

			case "errfail":
				if (DEBUG)
					System.err.print("serr ");
				System.err.print("fail");
				running = false;
				System.exit(1);
				break;

			default:
				if (DEBUG)
					System.out.print("sout ");
				System.out.println(next);
				break;
			}
		} else {
			running = false;
		}
		return running;
	}

}
