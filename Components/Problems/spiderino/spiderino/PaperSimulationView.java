/**
 * File: PaperSimulationView.java
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

import java.util.SplittableRandom;

import at.aau.frevo.circlesim.Simulation;
import at.aau.frevo.circlesim.SimulationView;
import core.AbstractRepresentation;

public class PaperSimulationView extends SimulationView {

  protected AbstractRepresentation representation;
  protected long seed;

  public PaperSimulationView(AbstractRepresentation representation, long seed) {
    this.representation = representation;
    this.seed = seed;
  }

  @Override
  protected Simulation createSimulation() {
    return new PaperSimulation(representation, new SplittableRandom(seed));
  }

}
