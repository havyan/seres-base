/**
 * 
 */
package com.framework.log;

/**
 * @author HWYan
 *
 */
public class Logger {

	private static Log log = new DefaultLog();

	public static void register(Log log) {
		Logger.log = log;
	}

	public static void error(Throwable e) {
		log.error(e);
	}

	public static void info(Exception e) {
		log.info(e);
	}

	public static void info(String msg) {
		log.info(msg);
	}

	public static void error(String msg) {
		log.error(msg);
	}

	public static void warn(String msg) {
		log.warn(msg);
	}

	public static void debug(String msg) {
		log.debug(msg);
	}

}
