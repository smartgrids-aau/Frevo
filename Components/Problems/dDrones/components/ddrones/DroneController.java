package components.ddrones;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import core.AbstractRepresentation;

public class DroneController {

	public ControllerModel method;
	
	public static final int SENSOR_RANGE = 3;//only valid for controller 8

	private AbstractRepresentation network;
	private Drone drone;
	private ddrones master;
	private ArrayList<Float> input = new ArrayList<Float>();
	private ArrayList<Float> output = new ArrayList<Float>();

	private float previousmovex = 0f;
	private float previousmovey = 0f;
	private int previousdir=-1;
	
	public boolean handled = false;

	public DroneController(AbstractRepresentation rep, ddrones master) {
		this.network = rep;
		this.master = master;
		method = ddrones.DRONECONTROLLER;
	}

	/** The behavior of the drone */
	public void postInfo(Drone[] drones) {

		float movex = 0.0f, movey = 0.0f;
		int newx,newy;

		switch (method) {
		case Evolved_cooperative_4_sensors: // neural network (evolved)
			// add nn inputs
			input.clear();
			Point pos = this.drone.getPosition();
			// obstacles
			if (master.fieldgrid[pos.x][pos.y - 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x][pos.y + 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x - 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x + 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			// add cooperative sensors
			if (master.isFieldOccupied(pos.x, pos.y - 1))
				// if (master.fieldgrid[pos.x][pos.y-1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x, pos.y + 1))
				// if (master.fieldgrid[pos.x][pos.y+1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y))
				// if (master.fieldgrid[pos.x-1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y))
				// if (master.fieldgrid[pos.x+1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			//output.setSize(2);
			output = network.getOutput(input);

			movex = output.get(0) - 0.5f;
			movey = output.get(1) - 0.5f;
			break;
		case Random_Walk: // random walk
			Random r = master.getRandom();
			int d = r.nextInt(4);
			if (d == 0)
				movex = 1;
			else if (d == 1)
				movex = -1;
			else if (d == 2)
				movey = 1;
			else if (d == 3)
				movey = -1;
			break;
		case Random_Direction: // random direction
			movex = previousmovex;
			movey = previousmovey;

			// random starting direction
			if ((movex == 0) && (movey == 0)) {
				r = master.getRandom();
				d = r.nextInt(4);
				if (d == 0)
					movex = 1;
				else if (d == 1)
					movex = -1;
				else if (d == 2)
					movey = 1;
				else if (d == 3)
					movey = -1;
			}

			newx = (int) (this.drone.getPosition().getX() + movex);
			newy = (int) (this.drone.getPosition().getY() + movey);

			if ((master.fieldgrid[newx][newy] == ddrones.BLOCKED)
					|| (master.isFieldOccupied(newx, newy))) {
				// generate random output instead
				movex = 0;
				movey = 0;
				r = master.getRandom();
				d = r.nextInt(4);
				if (d == 0)
					movex = 1;
				else if (d == 1)
					movex = -1;
				else if (d == 2)
					movey = 1;
				else if (d == 3)
					movey = -1;
			}
			break;
		case Belief_based:// belief-based approach
			movex = previousmovex;
			movey = previousmovey;

			// random starting direction
			if ((movex == 0) && (movey == 0)) {
				r = master.getRandom();
				d = r.nextInt(4);
				if (d == 0)
					movex = 1;
				else if (d == 1)
					movex = -1;
				else if (d == 2)
					movey = 1;
				else if (d == 3)
					movey = -1;
			}
			newx = (int) (this.drone.getPosition().getX() + movex);
			newy = (int) (this.drone.getPosition().getY() + movey);

			if (master.fieldgrid[newx][newy] == ddrones.BLOCKED) {
				// generate random output instead
				movex = 0;
				movey = 0;
				r = master.getRandom();
				d = r.nextInt(4);
				if (d == 0)
					movex = 1;
				else if (d == 1)
					movex = -1;
				else if (d == 2)
					movey = 1;
				else if (d == 3)
					movey = -1;
			} else if (master.isFieldOccupied(newx, newy)) {
				// calculate next step based on belief
				// if (handled) break;
				// handled = true;
				Drone other = master.getDrone(newx, newy);
				DroneController oc = other.getController();

				int onewx = other.getPosition().x + (int) oc.previousmovex;
				int onewy = other.getPosition().y + (int) oc.previousmovey;
				if ((drone.getPosition().x == onewx)
						&& (drone.getPosition().y == onewy)) {
					// we would collide
					// oc.handled = true;
					r = master.getRandom();
					int chance = r.nextInt(2);
					if (chance == 0) {
						movex = previousmovey;
						movey = previousmovex;
						float t = oc.previousmovex;
						oc.previousmovex = oc.previousmovey;
						oc.previousmovey = t;
					}
				}
			}

			break;
		case Evolved_non_cooperative:
			// add nn inputs
			input.clear();
			pos = this.drone.getPosition();
			// obstacles
			if (master.fieldgrid[pos.x][pos.y - 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);
			if (master.fieldgrid[pos.x][pos.y + 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);
			if (master.fieldgrid[pos.x - 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x + 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			//output.setSize(2);
			output = network.getOutput(input);

			movex = output.get(0) - 0.5f;
			movey = output.get(1) - 0.5f;
			break;
		case Evolved_cooperative_8_sensors: // neural network (evolved) with 8 sensors
			// add nn inputs
			input.clear();
			pos = this.drone.getPosition();
			// obstacles
			if (master.fieldgrid[pos.x][pos.y - 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x][pos.y + 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x - 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x + 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			// add cooperative sensors
			if (master.isFieldOccupied(pos.x, pos.y - 1))// top
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x, pos.y + 1))// bottom
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y))// left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y))// right
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y - 1))// top-left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y - 1))// top-right
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y + 1))// bottom-left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y + 1))// bottom-right
				input.add(1f);
			else
				input.add(0f);

			//output.setSize(2);
			output = network.getOutput(input);

			movex = output.get(0) - 0.5f;
			movey = output.get(1) - 0.5f;
			break;

		case Evolved_cooperative_24_sensors: // neural network (evolved) with 24 sensors
			// add nn inputs
			input.clear();
			pos = this.drone.getPosition();
			// obstacles
			if (master.fieldgrid[pos.x][pos.y - 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x][pos.y + 1] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x - 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			if (master.fieldgrid[pos.x + 1][pos.y] == ddrones.BLOCKED)
				input.add(1f);
			else
				input.add(0f);

			// add cooperative sensors
			if (master.isFieldOccupied(pos.x, pos.y - 1))// top
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x, pos.y + 1))// bottom
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y))// left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y))// right
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y - 1))// top-left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y - 1))// top-right
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 1, pos.y + 1))// bottom-left
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x + 1, pos.y + 1))// bottom-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 2, pos.y - 2))// top-top-left-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 1, pos.y - 2))// top-top-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x, pos.y - 2))// top-top
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x +1 , pos.y - 2))// top-top-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 2, pos.y - 2))// top-top-right-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 2, pos.y - 1))// top-left-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 2, pos.y - 1))// top-right-right
				input.add(1f);
			else
				input.add(0f);

			if (master.isFieldOccupied(pos.x - 2, pos.y ))// left-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 2, pos.y))// right-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 2, pos.y + 1))// bottom-left-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 2, pos.y + 1))// bottom-right-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 2, pos.y + 2))// bottom-bottom-left-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x - 1, pos.y + 2))// bottom-bottom-left
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x, pos.y + 2))// bottom-bottom
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 1, pos.y + 2))// bottom-bottom-right
				input.add(1f);
			else
				input.add(0f);
			
			if (master.isFieldOccupied(pos.x + 2, pos.y + 2))// bottom-bottom-right-right
				input.add(1f);
			else
				input.add(0f);
			
			//output.setSize(2);
			output = network.getOutput(input);

			movex = output.get(0) - 0.5f;
			movey = output.get(1) - 0.5f;
			break;
		case Random_direction_border_avoiding: // random direction with border-avoiding behavior
			int dx[]={1,0,-1,0};
			int dy[]={0,1,0,-1};
			ArrayList<Integer> opendirs=new ArrayList<Integer>();

			int options=0;
			//count how many options we have
			for(int dir=0; dir<4;dir++) {
				newx = (int) (this.drone.getPosition().getX() + dx[dir]);
				newy = (int) (this.drone.getPosition().getY() + dy[dir]);
				if ((master.fieldgrid[newx][newy] == ddrones.BLOCKED)
						|| (master.isFieldOccupied(newx, newy))) continue;
				options++;
				opendirs.add(dir);
				movex=dx[dir];
				movey=dy[dir];
			}
			if (previousdir != -1) {
				//continue with previous direction if nothing is nearby
				if (options==4) {
					movex = previousmovex;
					movey = previousmovey;				
					break;
				}
				//only one way to go?
				if (options==1)
					break;
				//select a random direction which is unexplored otherwise
				//remove the way back first
				int backwherewecamefrom = (previousdir+2) % 4;
				if (opendirs.contains(backwherewecamefrom))
					opendirs.remove((Object)backwherewecamefrom);
			}
			if (opendirs.size()==0) {
				break;
			}
			//randomly select new dir
			previousdir = opendirs.get(master.getRandom().nextInt(opendirs.size()));
			movex = dx[previousdir];
			movey = dy[previousdir];
			break;		
		/*case 8:
			//TODO range-sensor
			
			break;*/
			default:
				System.err.println("Error: selected controller is undefined!");
		}

		if (Math.abs(movex) > Math.abs(movey)) {
			// we move in x direction
			movex = Math.signum(movex);
			movey = 0;
		} else {
			// we move in y direction
			movey = Math.signum(movey);
			movex = 0;
		}

		// calculate the new position
		newx = (int) (this.drone.getPosition().getX() + movex);
		newy = (int) (this.drone.getPosition().getY() + movey);

		if ((master.fieldgrid[newx][newy] != ddrones.BLOCKED)
		/* && (!master.isFieldOccupied(newx, newy) ) */) {
			master.fieldgrid[drone.getPosition().x][drone.getPosition().y] = ddrones.FREE;// free
																							// old
																							// position
			this.drone.setPosition(new Point(newx, newy));
			if (master.visitedgrid[newx][newy] == false)
				master.visitedzones++;
			master.visitedgrid[newx][newy] = true;
			master.fieldgrid[drone.getPosition().x][drone.getPosition().y] = ddrones.BLOCKED;// occupy
																								// new
																								// position
		}

		previousmovex = movex;
		previousmovey = movey;

	}

	/** Sets the controller to the pre-info stage (before receiving sensor data) */
	public void preInfo() {
		// reset values here if necessary
	}

	public void setDrone(Drone d) {
		this.drone = d;
	}
}
