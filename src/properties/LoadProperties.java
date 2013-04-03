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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.LSUtils;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The LoadProperties servlet is used to get realPath 
 * of properties file
 */
public class LoadProperties extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public LoadProperties()
	{
		
	}
	public void init(ServletConfig config) throws ServletException
    {
		LSUtils.setRealPath(config);
    }
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
	}
}
