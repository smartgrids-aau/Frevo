package components.controllers;

import java.util.Random;

public class RandomDirectionController extends DroneController {
	
	private int direction;

	private final int DIRECTIONS = 8;

	public RandomDirectionController(Random r) {
		this.generator = r;
		switch (DIRECTIONS) {
		case 0:
			direction = generator.nextInt(360);
			break;
		case 4:
			direction = 45+ 90 *generator.nextInt(4);
			break;
		case 8:
			direction = 45+ 45 *generator.nextInt(8);
			break;
		case 16:
			direction = (int)(45+ 22.5 *generator.nextInt(16));
			break;
		case 32:
			direction = (int)(45 + 11.25 * generator.nextInt(32));
			break;
		case 64:
			direction = (int)(45 + 5.625 * generator.nextInt(64));
			break;
		}
		//direction = generator.nextInt(360);
		//direction = 45+ 90 *generator.nextInt(4);
		//direction = (int)(45+ 22.5 *generator.nextInt(16));
	}

	@Override
	public void postInfo() {
		this.drone.dashto(100, direction);
	}

	@Override
	public void preInfo() {
	}

	public void reportFailedMove() {
		//direction = generator.nextInt(360);
		//direction = 45+ 90 *generator.nextInt(4);
		//direction = 45+ 45 *generator.nextInt(8);
		//direction = (int)(45+ 22.5 *generator.nextInt(16));
		
		switch (DIRECTIONS) {
		case 0:
			direction = generator.nextInt(360);
			break;
		case 4:
			direction = 45+ 90 *generator.nextInt(4);
			break;
		case 8:
			direction = 45+ 45 *generator.nextInt(8);
			break;
		case 16:
			direction = (int)(45+ 22.5 *generator.nextInt(16));
			break;
		case 32:
			direction = (int)(45 + 11.25 * generator.nextInt(32));
			break;
		case 64:
			direction = (int)(45 + 5.625 * generator.nextInt(64));
			break;
		}
	}

	/*
	 * public void isOnborder() { //this.isOnborder = true; direction =
	 * generator.nextInt(360); //isOnborder = false;
	 * 
	 * }
	 */

}
