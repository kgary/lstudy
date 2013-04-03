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

import javax.servlet.ServletConfig;
/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The LSUtils class consists of method to get realpath
 * of the properties file.
 */
public class LSUtils {
	
	public static String realPath=".";
	
	public static void setRealPath(ServletConfig config)
	{
	  realPath = config.getServletContext().getRealPath("")+System.getProperty("file.separator");
	  System.out.println("realPath= "+realPath);
	}

}
