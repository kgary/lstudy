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

package logging;

import org.apache.log4j.Logger;

public class LoggingUtil
{
  private static Logger log = null;

  public LoggingUtil()
  {
  }

  public static void debug(String message)
  {
    log = Logger.getLogger(Thread.currentThread().getStackTrace()[2]
        .getClassName()
        + ":"
        + Thread.currentThread().getStackTrace()[2].getMethodName()
        + "()");
    log.log(DebugLevel.HCDEBUG, message);
  }

  /**
   * This method is used to log the messages into info log files.
   * 
   * @param message
   *          message to be logged to log files.
   */
  public static void info(String message)
  {
    log = Logger.getLogger(Thread.currentThread().getStackTrace()[2]
        .getClassName()
        + ":"
        + Thread.currentThread().getStackTrace()[2].getMethodName()
        + "()");
    log.info(message);
  }

  /**
   * This method is used to log the messages into error log files.
   * 
   * @param errorMessage
   *          Exception message to be logged to log file.
   */
  public static void error(String errorMessage)
  {
    log = Logger.getLogger(Thread.currentThread().getStackTrace()[2]
        .getClassName()
        + ":"
        + Thread.currentThread().getStackTrace()[2].getMethodName()
        + "()");
    log.error(errorMessage);
  }

  /**
   * This method is used to log that thread is about to execute this method.
   */
  public static void entry()
  {
    log = Logger.getLogger(Thread.currentThread().getStackTrace()[3]
        .getClassName()
        + ":"
        + Thread.currentThread().getStackTrace()[3].getMethodName()
        + "()");

    log.log(DebugLevel.HCDEBUG, "Entry");
    // log.error("Entry");
  }

  /**
   * This method is used to log that thread is about to execute this method.
   * 
   */
  public static void exit()
  {
    log = Logger.getLogger(Thread.currentThread().getStackTrace()[3]
        .getClassName()
        + ":"
        + Thread.currentThread().getStackTrace()[3].getMethodName()
        + "()");
    log.log(DebugLevel.HCDEBUG, "Exit");
    // log.error("Exit");
  }

}
