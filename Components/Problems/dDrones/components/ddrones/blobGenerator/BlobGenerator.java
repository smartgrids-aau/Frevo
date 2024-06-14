/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.ddrones.blobGenerator;

import java.awt.Point;
import java.util.Random;

import net.jodk.lang.FastMath;
import utils.NESRandom;

/**
 * This class generates 2D maps with obstacles. The obstacles are grown over
 * iterations until their coverage reaches the requested limit. A built-in
 * floodfill algorithm ensures that the resultant map will not contain isolated
 * spaces.
 * 
 * @author Istvan Fehervari
 */
public class BlobGenerator {

	/** Random generator object */
	Random generator;
	private static final int STARTMINDISTANCE = 3;

	public static final int BLOCKED = 1;

	public BlobGenerator() {
		this(new NESRandom());
	}

	public BlobGenerator(Random rnd) {
		this.generator = rnd;
	}

	/**
	 * Generates obstacle maps with the give parameters.
	 * 
	 * @param width
	 *            The discrete width of the map
	 * @param height
	 *            The discrete height of the map
	 * @param seeds
	 *            The number of seeds that will grow during the generation
	 * @param coverage
	 *            Coverage percentage
	 */
	public BlobMap generate(int width, int height, int seeds, double coverage,
			boolean checkFloodFill) {
		boolean good = false;
		BlobMap map = new BlobMap(width, height);
		int needtocover = (int) ((double) (width - 2) * (double) (height - 2) * coverage);

		while (!good) {
			map = new BlobMap(width, height);

			// create borders
			for (int px = 0; px < width; px++) {
				map.map[px][0] = BLOCKED;
				map.map[px][height - 1] = BLOCKED;
			}
			for (int py = 1; py < height; py++) {
				map.map[0][py] = BLOCKED;
				map.map[width - 1][py] = BLOCKED;
			}

			if (needtocover < seeds)
				seeds = needtocover;

			// create blobs
			for (int i = 0; i < seeds; i++)
				map.blobs.add(new Blob(i + 2, nextNormal(), generator, map));

			// deploy initial blobs
			boolean correct = false;
			while (!correct) {
				for (Blob b : map.blobs) {
					b.points.clear();
					b.points.add(new Point(generator.nextInt(width - 2) + 1,
							generator.nextInt(height - 2) + 1));
				}

				correct = true;
				// check if radiuses are correct
				for (int i = 0; i < seeds - 1; i++) {
					for (int k = i + 1; k < seeds; k++) {
						double dist = getDistance(
								map.blobs.get(i).points.get(0),
								map.blobs.get(k).points.get(0)) + 1;
						if ((dist) < STARTMINDISTANCE)
							correct = false;
					}
				}
			}

			// copy blob points to map
			for (int i = 0; i < map.blobs.size(); i++) {
				Point p = map.blobs.get(i).points.get(0);
				map.map[p.x][p.y] = map.blobs.get(i).id;// BlobMap.BLOCKED+1+i;
			}

			if (checkFloodFill) {
				good = checkWithFloodFill(width, height, map);
			} else {
				good = true;
			}
		}

		// return if map is already covered
		if (seeds >= needtocover)
			return map;

		// create map of forbidden zones
		boolean[][] forbiddenzones = new boolean[width][height];

		// grow blobs
		if (needtocover != 0) {
			int covered = seeds;
			while (true) {
				double chance = generator.nextDouble();
				for (Blob b : map.blobs) {
					if (chance < b.growthrate) {
						if (b.grow(forbiddenzones)) {
							covered++;
						}
						if (covered == needtocover)
							break;
					}
				}
				if (covered == needtocover)
					break;
			}
		}

		return map;
	}

	/**
	 * @param width
	 * @param height
	 * @param map
	 * @return
	 */
	static boolean checkWithFloodFill(int width, int height, BlobMap map) {
		boolean good;

		// make a copy of the map
		boolean copy[][] = new boolean[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (map.map[x][y] >= BLOCKED) {
					copy[x][y] = false; // blocked
				} else
					copy[x][y] = true; // open
			}
		}

		// find the first empty slot to start with
		int ix = 1;
		int iy = 1;

		for (int x = 1; x < width - 1; x++) {
			for (int y = 1; y < height - 1; y++) {
				if (copy[x][y]) {
					ix = x;
					iy = y;
					break;
				}
			}
		}

		// start recursion here
		fill(ix, iy, copy);

		// check open spots
		good = true;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (copy[x][y] == true)
					good = false;
				if (!good)
					break;
			}
			if (!good)
				break;
		}
		return good;
	}

	private static void fill(int x, int y, boolean[][] map) {
		if (map[x][y] == false) {
			return;
		}
		map[x][y] = false;
		fill(x - 1, y, map);
		fill(x, y - 1, map);
		fill(x + 1, y, map);
		fill(x, y + 1, map);
		return;
	}

	private static int getDistance(Point p1, Point p2) {
		return (int) FastMath.hypot(Math.abs(p1.x - p2.x),
				Math.abs(p1.y - p2.y));
	}

	private double nextNormal() {
		double r = Math.abs(generator.nextGaussian() * 0.25 + 0.5);
		if (r > 1.0)
			r = 1.0;
		else if (r < 0.1)
			r = 0.1;
		return r;
	}
}
