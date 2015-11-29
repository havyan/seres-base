/**
 * 
 */
package com.framework.log;

import java.util.Date;

/**
 * @author HWYan
 *
 */
public class Logger {

	public static void error(Throwable e) {
		e.printStackTrace();
	}

	public static void info(Exception e) {
		System.out.println(new Date().toString() + "[Info]: " + e.getMessage());
	}

	public static void info(String msg) {
		System.out.println(new Date().toString() + "[Info]: " + msg);
	}

	public static void error(String msg) {

	}

	public static void warn(String msg) {

	}

	public static void debug(String msg) {
		System.out.println(new Date().toString() + "[Debug]: " + msg);
	}

}
