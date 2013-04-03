/**************************************************************************
 Copyright (c)  2011 Arizona State University
 The following code is the property of Arizona State University and
 the Arizona Board of Regents. Various other restrictions may apply.
 Unauthorized use of this software, in whole or parts thereof, is
 strictly prohibited.

 Contact email: softwareenterprise@asu.edu
        phone: 480 727 1373.

 $Revision$
 $LastChangedBy$
 $LastChangedDate$
 $HeadURL$

**************************************************************************/

package util;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The DebugUtil class consists of methods used for
 * logging purposes.
 */
public class DebugUtil {

	public static StackTraceElement getCaller(int offset) {
		StackTraceElement callerClassName = new Throwable().getStackTrace()[offset+3];
		return callerClassName;
	}

	public static StackTraceElement getCaller() {
		return getCaller(0);
	}
	
	public static StackTraceElement getCallee(int offset) {
		StackTraceElement callerClassName = new Throwable().getStackTrace()[offset+2];
		return callerClassName;
	}
	
	public static StackTraceElement getCallee() {
		return getCallee(0);
	}
	
	public static String getCallerClassName() {
		return getCaller().getClassName();
	}
	
	public static String getCalleeClassName() {
		return getCallee().getClassName();
	}
}
