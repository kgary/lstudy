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

import org.apache.log4j.Level;

/**
 * This class is defines user defined LoggingLevel like debug, info ...etc
 * 
 * 
 */
public class DebugLevel extends Level
{
  public static final int HC_DEBUG_INT = FATAL_INT + 10;

  public static final Level HCDEBUG = new DebugLevel(HC_DEBUG_INT, "HCDEBUG",
      10);

  protected DebugLevel(int arg0, String arg1, int arg2)
  {
    super(arg0, arg1, arg2);

  }

  public static Level toLevel(String sArg)
  {
    if (sArg != null && sArg.toUpperCase().equals("HCDEBUG"))
    {
      return HCDEBUG;
    }
    return (Level) toLevel(sArg, Level.DEBUG);
  }

  public static Level toLevel(int val)
  {
    if (val == HC_DEBUG_INT)
    {
      return HCDEBUG;
    }
    return (Level) toLevel(val, Level.DEBUG);
  }

  public static Level toLevel(int val, Level defaultLevel)
  {
    if (val == HC_DEBUG_INT)
    {
      return HCDEBUG;
    }
    return Level.toLevel(val, defaultLevel);
  }

  public static Level toLevel(String sArg, Level defaultLevel)
  {
    if (sArg != null && sArg.toUpperCase().equals("HCDEBUG"))
    {
      return HCDEBUG;
    }
    return Level.toLevel(sArg, defaultLevel);
  }
}
