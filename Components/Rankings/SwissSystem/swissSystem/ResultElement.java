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
public class ResultElement {

	private SwissElement against;
	private int result;
	private int hashcode;
	
	public ResultElement (SwissElement id, int result) {
		this.against = id;
		this.result = result;
	}
	
	public ResultElement (SwissElement id, int result, int hc) {
		this(id,result);
		hashcode = hc;
	}
	
	public SwissElement getId() {
		return against;
	}
	
	public int getHashcode() {
		return hashcode;
	}
	
	public void setId(SwissElement id) {
		this.against = id;
	}
	
	public int getResult() {
		return this.result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	/**
	 * Return true if the game against this id was won, false otherwise
	 * @return
	 */
	public boolean isWin() {
		if (result == 1) return true;
		return false; 
	}
}
