package swissSystem;
/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
import java.util.HashMap;

public class SwissElement /*implements Comparable<SwissElement>*/ {

    private int id;
    private int points;
    private boolean playedWithBye = false;
    
    // results against others
    private HashMap<SwissElement,ResultElement> resultList = new HashMap<SwissElement,ResultElement>();//ArrayList<ResultElement>();

    public SwissElement(int id, int points) {
        this.id = id;
        this.points = points;
    }

    public SwissElement(int id) {
        this(id, 0);
    }

    public int getId() {
        return id;
    }

    public void addPoint(int point) {
        this.points += point;
    }

    public int getPoints() {
        return this.points;
    }

    /**
     * Adds result to this element
     * @param against
     * @param res
     */
    void addResult(SwissElement against, int res) {
    	resultList.put(against,new ResultElement(against, res));
    	
    	if (res == 1) {
    		// win
    		addPoint(SwissSystem.WINPOINT);
    	} else if (res == 0) {
            addPoint(SwissSystem.TIEPOINT);
        }
    }
    

    /**
     * Returns true if there is already a registered game result with this opponent
     * @param against
     * @return
     */
    public boolean isAlreadyPlayed(SwissElement against) {
        return resultList.containsKey(against);
    }

	public void setGameWithByePlayer(boolean playedWithBye) {
		this.playedWithBye = playedWithBye;
	}
	
	public boolean playedWithByePlayer() {
		return this.playedWithBye;
	}
	
}
