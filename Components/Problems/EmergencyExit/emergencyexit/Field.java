package emergencyexit;

import java.awt.Point;
import java.io.Serializable;

/**
 * The class Field is used in the FieldCreator to serialize Fields and in the class EmergencyExit to load Fields from a file.
 * 
 * @author Thomas Dittrich
 *
 */
public class Field implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -8083705180940916476L;
  public int width;
  public int height;
  public Point[] agents;
  public Point[] EmergencyExits;
  public Point[] blockades;
}
