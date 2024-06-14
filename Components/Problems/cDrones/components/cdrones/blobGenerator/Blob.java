/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package components.cdrones.blobGenerator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/** Class representing a growing space of obstacles. The blob grows in every iteration in its preferred direction.
 * @author Istvan Fehervari*/
public class Blob {
	public static final double THIRDCHANCE = 1;
	public static final double SECONDCHANCE =1;
	public static final double FIRSTCHANCE = 1;
	public static final boolean ALLOWBUILDINGSTOTOUCH = false;
	
	ArrayList<Point> points = new ArrayList<Point>();
	double growthrate;
	Random generator;
	public int id;
	
	private BlobMap parentMap;
	
	/** Creates a new Blob with the given parameters. A blob represents a growing covered area.
	 * @param id Unique ID of this blob.
	 * @param growthrate The rate in which this blob grows.
	 * @param random The random generator object.
	 * @param map The blobmap that registers this blob.*/
	public Blob(int id, double growthrate, Random random, BlobMap map) {
		this.growthrate = growthrate;
		this.generator = random;
		this.id = id;
		this.parentMap = map;
	}
	
	boolean grow (boolean[][] forbiddenzones) {
		//find a suitable point to expand
		
		Point[] vNeumanNeighbors = {new Point(-1,0),new Point(0,-1),new Point(1,0),new Point(0,1)};
		
		List<ScorePoint> candidates = new LinkedList<ScorePoint>();
		
		for (Point p:points) {
			
			for (Point n:vNeumanNeighbors) {
				Point newpoint = new Point (p.x+n.x,p.y+n.y);
				
				if ((parentMap.map[newpoint.x][newpoint.y] != BlobMap.FREE) || (forbiddenzones[newpoint.x][newpoint.y])){
					continue;
				}
				
				int neighborcount = getneighborCount(newpoint,parentMap.map);
				
				double score = getScore(neighborcount);
				candidates.add(new ScorePoint(newpoint,score));
			}
			
		}
		
		// return if no candidates are found
		if (candidates.size() == 0) return false;
		
		// sort candidates starting with the one with the highest score
		Collections.sort(candidates,Collections.reverseOrder());
		
		Iterator<ScorePoint> it = candidates.iterator();
		
		while (it.hasNext()) {
			ScorePoint sp = it.next();
			
			Point nextpoint = sp.point;
			
			parentMap.map[nextpoint.x][nextpoint.y] = id;
			boolean floodcheck = BlobGenerator.checkWithFloodFill(parentMap.map.length, parentMap.map[0].length, parentMap);
			if (floodcheck) {
				points.add(nextpoint);
				return true;
			}
			
			parentMap.map[nextpoint.x][nextpoint.y] = BlobMap.FREE;
			forbiddenzones[nextpoint.x][nextpoint.y] = true;
		}
		
		//if we made it this far that means there are no
		//suitable points
		return false;
	}

	private double getScore(int neighborcount) {
		if (neighborcount == 4) return 1;
		
		double chance = generator.nextDouble();
		
		switch(neighborcount) {
		case 3: return chance;
		case 2: return chance*0.5;
		case 1: return chance * 0.25;
		case 0: return chance * 0.1;
		}
		
		return 0;
	}

	private int getneighborCount(Point p, int[][] map) {
		int counter = 0;
		/*if (!ALLOWBUILDINGSTOTOUCH) {//bugged
			if ((p.x>0) && ((map[p.x-1][p.y] != this.id) && (map[p.x-1][p.y] != BlobMap.FREE) )  ) return -1;
			if ((p.y>0) && ((map[p.x][p.y-1] != this.id) && (map[p.x][p.y-1] != BlobMap.FREE) )  ) return -1;
			if ((p.x<map.length-1) && ((map[p.x+1][p.y] != this.id) && (map[p.x+1][p.y] != BlobMap.FREE) )  ) return -1;
			if ((p.y<map[0].length-1) && ((map[p.x][p.y+1] != this.id) && (map[p.x][p.y+1] != BlobMap.FREE) )  ) return -1;
		}*/
		
		if ((p.x>0) && (map[p.x-1][p.y] == this.id)) counter++;//left
		if ((p.y>0) && (map[p.x][p.y-1] == this.id)) counter++;//top
		if ((p.x<map.length-1) && (map[p.x+1][p.y] == this.id)) counter++;//right
		if ((p.y<map[0].length-1) && (map[p.x][p.y+1] == this.id)) counter++;//bottom
		return counter;
	}
	
	private class ScorePoint implements Comparable<ScorePoint>{
		public double score = 0;
		public Point point = null;
		
		public ScorePoint(Point p, double score) {
			this.point = p;
			this.score = score;
		}

		@Override
		public int compareTo(ScorePoint o) {
			if (this.score > o.score) return 1;
			else if (this.score < o.score) return -1;
			return 0;
		}
	}
}
