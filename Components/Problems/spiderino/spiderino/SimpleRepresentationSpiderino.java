/**
 * File: SimpleRepresentationSpiderino.java
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

import java.util.ArrayList;

import core.AbstractRepresentation;

/**
 * Spiderino controlled by a {@link RepresentationContext}.
 */
public class SimpleRepresentationSpiderino extends SpiderinoRobot {

  protected AbstractRepresentation representation;
  protected ArrayList<Float> inputs = new ArrayList<Float>(12);

  /**
   * Creates a new {@link SimpleRepresentationSpiderino} instance with the specified parameters.
   * 
   * @param id        the id
   * @param x         the x coordinate
   * @param y         the y coordinate
   * @param rotation  the rotation
   * @param stepDelta the step delta
   * @param context   the {@link RepresentationContext}
   */
  public SimpleRepresentationSpiderino(int id, double x, double y, double rotation,
      double stepDelta, AbstractRepresentation representation) {
    super(id, x, y, rotation, stepDelta);
    this.representation = representation;
    for(int i=0;i<12;i++) inputs.add(0.0f);
  }

  @Override
  public void doStep(int step) {

    // collect all six sensor values
    readSensor(0);
    inputs.set(0,proximityValue);
    inputs.set(1,lightValue);

    readSensor(-45);
    inputs.set(2,proximityValue);
    inputs.set(3,lightValue);

    readSensor(45);
    inputs.set(4,proximityValue);
    inputs.set(5,lightValue);

    readSensor(-90);
    inputs.set(6,proximityValue);
    inputs.set(7,lightValue);

    readSensor(90);
    inputs.set(8,proximityValue);
    inputs.set(9,lightValue);
    
    readSensor(180);
    inputs.set(10,proximityValue);
    inputs.set(11,lightValue);
    
    // calculate
    ArrayList<Float> outputs=representation.getOutput(new ArrayList<Float>(inputs));   
    
    // rotate spiderino
    float turn = outputs.get(0);
    if (turn > 0.75) {
      rotateRight();
    } else if (turn < 0.25) {
      rotateLeft();
    }

    // move spiderino
    float walk = outputs.get(1);
    if (walk > 0.75) {
      tryMoveForward();
    } else if (walk < 0.25) {
      tryMoveBackward();
    }
  }
}
