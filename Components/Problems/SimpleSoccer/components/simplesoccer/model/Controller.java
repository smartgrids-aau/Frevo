/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.simplesoccer.model;

import components.simplesoccer.SimpleSoccer;


/**
 * Interface that has to be implemented in order to control players. The methods
 * are run in a cycle whenever a see command arrives from sserver. At first preInfo()
 * is invoked. then the info*() methods are called according to what kind of objects
 * are currently seen or what other commands where received from the server. At last
 * postInfo() is called. All objects are relative to the current side of the controller.
 */

public interface Controller {

  public void preInfo ();
  public void postInfo ();

  public Player getPlayer ();
  public void setPlayer (Player c);
  public SimpleSoccer getSession();
  
  //inputs
  public void setRelativePosBall (double x,double y);
  public void setRelativePosNearestPlayer (double x,double y);
  public void setRelativePosNearestOpponent (double x,double y);
  public void setRelativePosBorders (double top, double bottom, double left, double right);
  public void setRelativePosOwnGoal (double x, double y);
  public void setRelativePosOppGoal (double x, double y);
  
  // public void setKickNum (int kick); needed?

}
