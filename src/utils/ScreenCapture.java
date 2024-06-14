/*
 * Copyright (C) 2009 Istvan Fehervari, Wilfried Elmenreich
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * There is no warranty for this free software.
 */
package utils;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A helper class to save the graphical content of any swing or AWT component to
 * individual files.
 * 
 * @author Istvan Fehervari
 */
public class ScreenCapture {
	/**
	 * Captures the graphical content of the provided swing {@link JComponent}.
	 * The result will be saved to the given file and returned as a
	 * {@link BufferedImage}. The entire component will be captured to an image.
	 * 
	 * @param component
	 *            Swing component to create the image from
	 * @param fileName
	 *            name of file to be created. Passing <tt>null</tt> skips
	 *            saving.
	 * @return the image captured
	 * @exception IOException
	 *                if an error occurs during writing
	 */
	public static BufferedImage captureImageContent(JComponent component,
			String fileName) throws IOException {
		Dimension d = component.getSize();

		if (d.width == 0) {
			d = component.getPreferredSize();
			component.setSize(d);
		}

		// get region
		Rectangle region = new Rectangle(0, 0, d.width, d.height);
		return ScreenCapture.captureImageContent(component, region, fileName);
	}

	/**
	 * Captures the graphical content of the given region of the provided swing
	 * {@link JComponent}. The result will be saved to the given file and
	 * returned as a {@link BufferedImage}. The entire component will be
	 * captured to an image.
	 * 
	 * @param component
	 *            Swing component to create image from
	 * @param region
	 *            The region of the component to be captured to an image
	 * @param fileName
	 *            name of file to be created. Passing <tt>null</tt> skips
	 *            saving.
	 * @return the image captured
	 * @exception IOException
	 *                if an error occurs during writing
	 */
	public static BufferedImage captureImageContent(JComponent component,
			Rectangle region, String fileName) throws IOException {
		// turns on opaque if it false, then sets is back to the original value.
		boolean opaqueValue = component.isOpaque();
		component.setOpaque(true);

		// copy graphical content
		BufferedImage image = new BufferedImage(region.width, region.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setClip(region);
		component.paint(g2d);
		g2d.dispose();
		component.setOpaque(opaqueValue);
		// write data to disk
		ScreenCapture.writeImage(image, fileName);
		return image;
	}

	/**
	 * Captures the graphical content of the provided AWT {@link Component}. The
	 * result will be saved to the given file and returned as a
	 * {@link BufferedImage}. The entire component will be captured to an image.
	 * 
	 * @param component
	 *            AWT component to create the image of
	 * 
	 * @param fileName
	 *            name of file to be created. Passing <tt>null</tt> skips
	 *            saving.
	 * 
	 * @return the image captured
	 * 
	 * @exception AWTException
	 *                see {@link Robot#Robot()}
	 * 
	 * @exception IOException
	 *                if an error occurs during writing
	 */
	public static BufferedImage createImage(Component component, String fileName)
			throws AWTException, IOException {
		// get region coordinates
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, component);
		Rectangle region = component.getBounds();
		region.x = p.x;
		region.y = p.y;
		return ScreenCapture.captureScreen(region, fileName);
	}

	/**
	 * Creates a BufferedImage from a rectangular region on the screen.
	 * 
	 * @param region
	 *            region on the screen to create image from
	 * @param fileName
	 *            name of file to be created or null
	 * @return image the image for the given region
	 * @exception AWTException
	 *                see {@link Robot#Robot()}
	 * @exception IOException
	 *                if an error occurs during writing
	 */
	public static BufferedImage captureScreen(Rectangle region, String fileName)
			throws AWTException, IOException {
		BufferedImage image = new Robot().createScreenCapture(region);
		if (fileName != null)
			ScreenCapture.writeImage(image, fileName);
		return image;
	}

	/**
	 * Writes a <tt>BufferedImage</tt> to a <tt>File</tt>.
	 * 
	 * @param image
	 *            image to be written
	 * @param fileName
	 *            name of file to be created
	 * @exception IOException
	 *                if an error occurs during writing
	 */
	public static void writeImage(BufferedImage image, String fileName)
			throws IOException {
		if (fileName == null)
			throw new IOException("ERROR: Passed file name is null!");

		// handle extension
		int offset = fileName.lastIndexOf(".");
		String type = offset == -1 ? "bmp" : fileName.substring(offset + 1);

		ImageIO.write(image, type, new File(fileName));
	}

	/** Resizes the given Image to the provided dimensions.
	 * @param src the source image
	 * @param width the new width
	 * @param height the new height
	 * @return the resized image*/
	public static ImageIcon resizeImage(Image src, int width, int height) {
		Image newimg = src.getScaledInstance(width, height,
				java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(newimg);
	}
}