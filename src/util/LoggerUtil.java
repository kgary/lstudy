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

/**
 * Provides static and utility convenience methods for use with log4j.
 */
package util;

import util.*;
import org.apache.log4j.Logger;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The DebugUtil class consists of methods used for
 * logging purposes.
 */
public class LoggerUtil {

	public static Logger getClassLogger() {
		Logger logger = Logger.getLogger(DebugUtil.getCaller(1).getClassName());
		return logger;
	}

}
