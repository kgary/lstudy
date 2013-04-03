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

package properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import util.LSUtils;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The PropertiesClass is used to get properties 
 * from properties file
 */

public class PropertiesClass {

	private static Logger logger = LoggerUtil.getClassLogger();
	static Properties properties ;
	
	static
	{
		properties = new Properties();
		try {
			//loads the properties file
			properties.load(new FileInputStream(new File(LSUtils.realPath+"WEB-INF/longitudinalStudy.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final String title = properties.getProperty("title");

	// database details
	public static final String userName = properties.getProperty("dBUserName");
	public static final String password = properties.getProperty("dBPassword");
	public static final String url = properties.getProperty("dBUrl");

	public static final String logoutMessage = properties.getProperty("logoutMessage");
	public static final String adminLogoutMessage = properties.getProperty("adminLogoutMessage");
	
	public static final String hostUrl = properties.getProperty("hostUrl");
	
	public static final String host = properties.getProperty("host");
	
	public static final String fromEmailAddress = properties.getProperty("fromEmailAddress");
	
	public static final String sqlQueryForSendingEmails = properties.getProperty("sqlQueryForSendingEmails");
	
	public static final String nsfccliAppAbsoluteURL = properties.getProperty("nsfccliAppAbsoluteURL");
}
