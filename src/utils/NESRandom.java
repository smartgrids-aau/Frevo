/*
 * Copyright (C) 2009 Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */

package utils;

import java.util.Random;

/**
 * This class extends Java's pseudorandom number generator by functions
 * for retrieving the seed and cloning the random object
 * @author Wilfried Elmenreich
 */
public class NESRandom extends Random implements Cloneable
{
  /**
   * The seed.  This is the number set by {@link NESRandom#setSeed(long)} and which is used
   * in next.
   *
   * @serial the internal state of this generator
   * @see #next(int)
   */
  private long seed;

  /**
   * The initial seed that can get its value from the constructor or 
   * using the {@link NESRandom#setSeed(long)} method.
   */
  private long initialSeed;
  
  /**
   * Compatible with JDK 1.0+.
   */
  private static final long serialVersionUID = 3905348978240129619L;

  /**
   * Creates a new pseudorandom number generator.  The seed is initialized
   * to the current time, as if by
   * <code>setSeed(System.currentTimeMillis());</code>.
   *
   * @see System#currentTimeMillis()
   */
  public NESRandom()
  {
    this(System.currentTimeMillis());
  }

  /**
   * Creates a new pseudorandom number generator, starting with the
   * specified seed, using <code>setSeed(seed);</code>.
   *
   * @param seed the initial seed
   */
  public NESRandom(long seed)
  {
    setSeed(seed);
  }

  /**
   * Sets the seed for this pseudorandom number generator.  As described
   * above, two instances of the same random class, starting with the
   * same seed, produce the same results, if the same methods are called.
   *
   * @param seed the new seed
   */
  public synchronized void setSeed(long seed)
  {
	this.initialSeed = seed;
    this.seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
    super.setSeed(seed);
  }
  
  /** 
   * Returns the current state of the seed, can be used to clone the 
   * object
   * @return the current seed
   */
  public synchronized long getSeed()
  {
    return (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
  }
  
  /**
   * Returns the initial seed of the random generator. 
   * @return the initial seed
   */
  public long getInitialSeed()
  {
	  return initialSeed;
  } 

  /**
   * Generates the next pseudorandom number. This function is used to generate
   * all other random numbers.
   *
   * @param bits the number of random bits to generate, in the range 1..32
   * @return the next pseudorandom value
   */
  protected synchronized int next(int bits)
  {
    seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
    return (int) (seed >>> (48 - bits));
  }
  
  /** Creates a clone of this object based on its current seed. 
   * @return A clone of this random generator object. */
  public NESRandom clone() {
	  return new NESRandom(getSeed());
  }
}