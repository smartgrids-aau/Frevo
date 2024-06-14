package spiderinosim;

public class Cny70Sensor {
	
	// look up by int degree
	protected static double[] angleLut;
	
	// look up by int centimeter * 4
	protected static double[] objectLut;
	
	// look up by int centimeter / 5
	protected static double[] lightLut;
	
	public Cny70Sensor() {
				
		if (angleLut == null) {
			angleLut = createLut(
					new int[]{0, 10, 20, 30, 40, 50, 60, 70},
					new double[]{1, 0.96, 0.84, 0.69, 0.55, 0.35, 0.1, 0});
		}
		
		if (objectLut == null) {
			objectLut = createLut(
					new int[]{0, 1, 2, 3, 4, 6, 8, 10, 12},
					new double[]{20, 28.75, 60, 175, 300, 586, 862, 975, 1023});
		}
		
		if (lightLut == null) {
			lightLut = createLut(
					new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 98},
					new double[]{0, 1, 45, 85, 120, 145, 170, 185, 200, 225, 245, 260, 280, 290, 300, 310, 325, 335, 350, 360, 375, 395, 413, 430, 450, 1023});
		}
	}
	
	
	public double getAngleValue(double angle) {
		angle = Math.abs(angle);
		if (angle < 70)
			return readLut(angleLut, angle);
		else
			return 0;
	}
	
	
	public double getObjectValue(double objectDistance) {
		if (objectDistance <= 0.03)
			return readLut(objectLut, objectDistance * 100 * 4) / 1023;
		else
			return 1;
	}
	
	
	public double getLightValue(double distance) {
		if (distance <= 4.9)
			return readLut(lightLut, distance * 100 / 5) / 1023;
		else
			return 1;
	}
	

	protected double readLut(double[] lut, double index) {
		int intIndex = (int) Math.floor(index);
		if (intIndex == index)
			return lut[intIndex];
		
		double offset = index - intIndex;
		return lut[intIndex] * (1 - offset) + lut[intIndex + 1] * offset;
	}
	
	
	protected static double[] createLut(int[] xValues, double[] yValues) {
		double[] lut = new double[xValues[xValues.length - 1] + 1];
		int startX = xValues[0];
		double startY = yValues[0];
		for	(int i = 1; i < xValues.length; i++) {
			int endX = xValues[i];
			double endY = yValues[i];
			for (int x = startX; x <= endX; x++) {
				lut[x] = ((endX - x) * startY + (x - startX) * endY) / (endX - startX);
			}
			startX = endX;
			startY = endY;
		}
		return lut;
	}
	
}
