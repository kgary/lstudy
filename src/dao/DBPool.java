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

package dao;

import java.sql.*;
import javax.naming.*;
import javax.sql.*;

import org.apache.log4j.Logger;

import properties.PropertiesClass;
import util.LoggerUtil;

/**
 * @author Srikesh Mandala(smandal2@exchange.asu.edu)
 *
 * The DBPool class consists of a  method
 * which gets a connection from a pool of opened connections
 */
public class DBPool {
	private static Logger logger = LoggerUtil.getClassLogger();

	/**
     * The method getConnection gets an open 
     * connection from pool of available 
     * connections
     * 
     * @param none
     * 
     * @return Connection connection
     */
	
	public static Connection getConnection() {
		Connection conn = null;
//adding a test comment to check if svn commit is working or not
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource dataSource = (DataSource) envContext
					.lookup("jdbc/LongitudinalStudyDB");
			conn = (Connection) dataSource.getConnection();
		} catch (NamingException ne) {
			conn = getDbConnectionUsingDrivers();
		} catch (SQLException se) {
			logger
					.error("unable to get connection from pool. creating a new connection "+se.getMessage());
			//incase if this method is not able to get
			//connection from pool of connections this method
			//will create a new connection and return it
			conn = getDbConnectionUsingDrivers();
		} catch (Exception e) {
			conn = getDbConnectionUsingDrivers();
		} finally {
			return conn;
		}
	}

	/**
	 * This method used to get connection when there is a failure to get
	 * connection from the DB pool.
	 * 
	 * @param none
	 * 
	 * @return Connection Connection object for the database.
	 */
	private static Connection getDbConnectionUsingDrivers() {
		String dbDetails = PropertiesClass.url;
		String userName = PropertiesClass.userName;
		String password = PropertiesClass.password;
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(dbDetails, userName, password);
		} catch (SQLException se) {
			throw se;
		} catch (Exception e) {
			throw e;
		} finally {
			return conn;
		}

	}

}
