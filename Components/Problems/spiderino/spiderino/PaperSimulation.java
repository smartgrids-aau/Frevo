/**
 * File: PaperSimulation.java
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
import at.aau.frevo.circlesim.FitnessAlgorithm;
import at.aau.frevo.circlesim.Light;
import at.aau.frevo.circlesim.Obstacle;
import at.aau.frevo.circlesim.Robot;
import at.aau.frevo.circlesim.Simulation;
import at.aau.frevo.circlesim.World;
import core.AbstractRepresentation;
import spiderino.SimpleRepresentationSpiderino;

public class PaperSimulation extends Simulation {

  static class PaperSimulationFitnessFunction extends FitnessAlgorithm {
    double maxDistance = 0;
    Light light;
    double fitnessSum = 0;
    double fitnessCount = 0;

    double fitnessScalingFactor = 1;

    public PaperSimulationFitnessFunction(World world, Robot robot) {
      super(world, robot);
      maxDistance =
          Math.sqrt(world.getWidth() * world.getWidth() + world.getHeight() * world.getHeight());
      light = world.getLights().get(0);
    }

    @Override
    public double getFitness() {
      return fitnessSum / fitnessCount;
    }

    @Override
    public void doStep(int step) {

      // compute a new scaling factor every 10 steps
      if (step % 10 == 0) {
        var scaleSum = 0.0;
        var scaleCount = 0;
        for (var otherRobot : world.getRobots()) {
          if (otherRobot == robot) {
            continue;
          }
          var rDx = robot.getX() - otherRobot.getX();
          var rDy = robot.getY() - otherRobot.getY();
          var rDistance =
              Math.sqrt(rDx * rDx + rDy * rDy) - robot.getRadius() - otherRobot.getRadius();

          // clamp the distance between 2 cm and 1 mm
          rDistance = Math.max(Math.min(rDistance, 0.02), 0.001);
          scaleSum += rDistance / .02;
          scaleCount++;
        }

        fitnessScalingFactor = scaleSum / scaleCount;

      }

      var dX = light.getX() - robot.getX();
      var dY = light.getY() - robot.getY();
      var distance = Math.sqrt(dX * dX + dY * dY) - light.getRadius() - robot.getRadius();

      if (distance > maxDistance) {
        distance = maxDistance;
      }

      fitnessSum += fitnessScalingFactor * ((1 - distance / maxDistance) * 100);
      fitnessCount++;
    }

  }

  protected SplittableRandom random;
  protected AbstractRepresentation representation;

  public PaperSimulation(AbstractRepresentation representation, SplittableRandom random) {
    super();
    this.random = random;
    this.representation = representation;

    // set other attributes
    stepCount = 1500;
    stepDelta = 0.1;

    // create world
    world = new World(2, 3);

    // create light
    var light = new Light(0, 1, 2.75, 0.1);
    world.tryAddObject(light);

    // create obstacles
    var obstacleId = 0;
    for (int i = 0; i < 2; i++) {
      world.tryAddObject(new Obstacle(obstacleId++, 0.21 * i + 0.4, 1.5, .1));
    }

    for (int i = 0; i < 2; i++) {
      world.tryAddObject(new Obstacle(obstacleId, 1.6 - 0.21 * i, 1.5, .1));
    }

    // add robots
    for (int i = 0; i < 20; i++) {
      var robot = new SimpleRepresentationSpiderino(i, 0, 0, random.nextDouble(360), stepDelta,
          representation.clone());
      while (!world.tryAddObject(robot)) {
        robot.setX((i % 5) * 0.4 + 0.1 + random.nextDouble() * 0.2);
        robot.setY((i / 5) * 0.3 + 0.1 + random.nextDouble() * 0.2);
      }
      fitnessAlgorithms.add(new PaperSimulationFitnessFunction(world, robot));
    }
  }

}
