/**
 * 
 */
package com.framework.exception;

import javax.swing.JOptionPane;

import com.framework.log.Logger;

/**
 * @author HWYan
 *
 */
public class ExceptionUtils {
	
	public static void logException(Exception e){
		e.printStackTrace();
		Logger.error(e);
	}
	
	public static void logAndShowException(Exception e){
		System.out.println("base Test");
		e.printStackTrace();
		Logger.error(e);
		JOptionPane.showMessageDialog(null, e.getMessage());
	}

}
