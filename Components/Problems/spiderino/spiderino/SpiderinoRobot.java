/**
 * File: Spiderino.java
 * 
 * Copyright (C) 2019 FREVO project contributors
 *
 * Universitaet Klagenfurt licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package spiderino;

import at.aau.frevo.circlesim.Robot;

/**
 * Super class for all Spiderinos.
 */
public abstract class SpiderinoRobot extends Robot {

  /**
   * Spiderinos have a radius of 6 centimeters.
   */
  protected static final double RADIUS = 0.06;

  /**
   * Spiderinos can walk 6 centimeters per second.
   */
  protected static final double WALK_SPEED = 0.06;

  /**
   * Spiderinos can turn 120 degrees per second.
   */
  protected static final double TURN_SPEED = 120;

  /**
   * Spiderinos can sense a light up to 2 meters away.
   */
  protected static final double LIGHT_MAX_DISTANCE = 2;

  /**
   * Spiderinos can sense an object up to 3 centimeters away.
   */
  protected static final double PROXIMITY_MAX_DISTANCE = 0.03;

  /**
   * Proximity value of most recent call to {@link Spiderino#readSensor(double)}.
   * <p>
   * If the light value is greater that the proximity value, the sensor is blinded and returns the
   * same value as the {@link Spiderino#lightValue}. See {@link Spiderino#readSensor(double)} for
   * details.
   */
  protected float proximityValue;

  /**
   * Light value of most recent call to {@link Spiderino#readSensor(double)}.
   */
  protected float lightValue;

  /**
   * Creates a new {@code Spiderino} instance using the specified parameters.
   * 
   * @param id        the id
   * @param x         the x coordinate
   * @param y         the y coordinate
   * @param rotation  the rotation
   * @param stepDelta the step delta
   */
  public SpiderinoRobot(int id, double x, double y, double rotation, double stepDelta) {
    super(id, x, y, RADIUS, rotation, WALK_SPEED * stepDelta, TURN_SPEED * stepDelta);
  }

  /**
   * Reads a sensor at the specified angle.
   * <p>
   * This method updates the {@link Spiderino#proximityValue} and {@link Spiderino#lightValue}
   * member variables.
   * 
   * @param sensorAngle the angle of the sensor relative to the front of the Spiderino
   */
  protected void readSensor(double sensorAngle) {
    var caster = world.getTriangleCaster();
    caster.sendTriangle(this, rotation + sensorAngle, 0, 45);
    lightValue = (float) (Math.min(caster.getClosestLightDistance(), LIGHT_MAX_DISTANCE)
        / LIGHT_MAX_DISTANCE);
    proximityValue = (float) (Math.min(caster.getClosestDistance(), PROXIMITY_MAX_DISTANCE)
        / PROXIMITY_MAX_DISTANCE);
    proximityValue = Math.min(lightValue, proximityValue);
  }
}
