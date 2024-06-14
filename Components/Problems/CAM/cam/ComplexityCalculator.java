package cam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.jodk.lang.FastMath;

public class ComplexityCalculator {

	public static final String DEFAULT_IMAGE_LOCATION = "/Users/ifeherva/Works/demesos/Sourcecode/Frevo/Components/Problems/CAM/cow.png";

	private static final double LOG_OF_2 = Math.log(2);
	private static final double PI_TIMES_LOG2_OF_PI = Math.PI * log2(Math.PI);

	private final int W;
	private final int H;
	private final BufferedImage image;

	private double moranI = -2;

	ArrayList<Integer> colorSet;

	public ComplexityCalculator(BufferedImage image) {
		this.image = image;
		W = image.getWidth();
		H = image.getHeight();

		// Build color list
		colorSet = new ArrayList<Integer>();
		for (int x = 0; x < W; x++)
			for (int y = 0; y < H; y++) {
				int shortColor = rgbToShortColor(image.getRGB(x, y));
				if (!colorSet.contains(shortColor))
					colorSet.add(shortColor);
			}

		System.out.println("Dimensions: " + W + "x" + H + " Number of colors: "
				+ colorSet.size());
		/*
		 * System.out.println("Image (" + W + "x" + H +
		 * ") successfully loaded with " + colorSet.size() + " colors.");
		 */
	}

	private double getMoran1() {
		if (moranI == -2) {
			calcMoran1();
			return moranI;
		}

		return moranI;
	}

	/*
	 * class NeighborColors { public int top; public int left; public int
	 * bottom; public int right;
	 * 
	 * public NeighborColors(int top, int left, int bottom, int right) {
	 * this.top = top; this.left = left; this.bottom = bottom; this.right =
	 * right; }
	 * 
	 * //equals only if all values match public boolean equals(NeighborColors o)
	 * { if ((top == o.top) && (bottom == o.bottom) && (left == o.left) &&
	 * (right == o.right)) return true; else return false; } }
	 */

	private double getEntropy(boolean isMooran) {
		HashMap<String, Integer> variations = new HashMap<String, Integer>();

		// consider only inner part (wrong)
		for (int x = 1; x < W - 1; x++) {
			for (int y = 1; y < H - 1; y++) {

				// for (int x = 0; x < W; x++) {
				// for (int y = 0; y < H; y++) {

				int top = -1;
				int bottom = -1;
				int left = -1;
				int right = -1;

				int topleft = -1;
				int topright = -1;
				int bottomleft = -1;
				int bottomright = -1;

				// top
				if (y != 0) {
					top = colorSet.indexOf(rgbToShortColor(image.getRGB(x,
							y - 1)));
					if (x != 0) {
						topleft = colorSet.indexOf(rgbToShortColor(image
								.getRGB(x - 1, y - 1)));
					}

					if (x != W - 1) {
						topright = colorSet.indexOf(rgbToShortColor(image
								.getRGB(x + 1, y - 1)));
					}

				}
				// bottom
				if (y != H - 1) {
					bottom = colorSet.indexOf(rgbToShortColor(image.getRGB(x,
							y + 1)));

					if (x != 0) {
						bottomleft = colorSet.indexOf(rgbToShortColor(image
								.getRGB(x - 1, y + 1)));
					}

					if (x != W - 1) {
						bottomright = colorSet.indexOf(rgbToShortColor(image
								.getRGB(x + 1, y + 1)));
					}

				}

				// left
				if (x != 0) {
					left = colorSet.indexOf(rgbToShortColor(image.getRGB(x - 1,
							y)));
				}
				// right
				if (x != W - 1) {
					right = colorSet.indexOf(rgbToShortColor(image.getRGB(
							x + 1, y)));
				}

				StringBuilder sb = new StringBuilder();
				sb.append(colorSet.indexOf(rgbToShortColor(image.getRGB(x,
						y))));
				sb.append(",").append(top);
				sb.append(",").append(left);
				sb.append(",").append(bottom);
				sb.append(",").append(right);

				if (isMooran) {
					sb.append(",").append(topleft);
					sb.append(",").append(topright);
					sb.append(",").append(bottomleft);
					sb.append(",").append(bottomright);
				}

				String n = sb.toString();
				Integer currentnum = variations.get(n);

				if (currentnum == null) {
					variations.put(n, 1);
				} else {
					variations.put(n, ++currentnum);
				}
			}
		}

		int vsize = variations.size();
		System.out.println("There are " + vsize + " variations");

		/*
		 * int sum = 0; for (String v:variations.keySet()) {
		 * //System.out.println (v+" : "+variations.get(v)); sum +=
		 * variations.get(v); }
		 */

		// System.out.println("Sum="+sum);

		double entropy = 0.0;
		int dataset_size = (W - 2) * (H - 2);
		//int dataset_size = W * H;

		for (Map.Entry<String, Integer> stringIntegerEntry : variations.entrySet()) {
			String key = stringIntegerEntry.getKey();
			Integer num = stringIntegerEntry.getValue();
			double pi = (double) num / dataset_size;
			System.out.println(key+" ["+num+"] {"+pi+"}");
			entropy += PI_TIMES_LOG2_OF_PI;
		}

		return -1 * entropy;
	}

	static double log2(double value) {
		return FastMath.log(value) / LOG_OF_2;
	}

	/** Calculates Moran1 */
	private void calcMoran1() {

		// generate datapoint list
		ArrayList<LocationData> datapoints = new ArrayList<LocationData>();
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				int v = colorSet.indexOf(rgbToShortColor(image.getRGB(x, y)));
				datapoints.add(new LocationData(x, y, v));
			}
		}

		/*
		 * try { datapoints = new ArrayList<LocationData>(); FileInputStream
		 * fstream = new FileInputStream(
		 * "Components\\Problems\\CAM\\ozone.csv"); DataInputStream in = new
		 * DataInputStream(fstream); BufferedReader br = new BufferedReader(new
		 * InputStreamReader(in)); String strLine; // Read File Line By Line
		 * while ((strLine = br.readLine()) != null) { Scanner sc = new
		 * Scanner(strLine).useLocale(Locale.ENGLISH); ; sc.useDelimiter(",");
		 * sc.nextInt(); double val = sc.nextDouble(); double x =
		 * sc.nextDouble(); double y = sc.nextDouble(); if (datapoints.size() <
		 * 4) datapoints.add(new LocationData(x, y, val)); } in.close(); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
		/*
		 * int cutn = 10; if (cutn > datapoints.size()) cutn =
		 * datapoints.size();
		 * 
		 * System.out.println("First " + cutn + " rows of data:"); for (int i =
		 * 0; i < cutn; i++) { System.out.println(datapoints.get(i).x + ", " +
		 * datapoints.get(i).y + ", " + datapoints.get(i).value); }
		 */
		int N = datapoints.size();

		// mean
		double mean = 0;
		for (LocationData ld : datapoints)
			mean += ld.value;
		mean = mean / N;

		// create distance matrix (spatial weights)
		double[][] idmatrix = new double[N][N];
		for (int l1 = 0; l1 < N; l1++) {
			LocationData ldthis = datapoints.get(l1);
			for (int l2 = 0; l2 < N; l2++) {
				LocationData ldother = datapoints.get(l2);
				if (ldthis == ldother)
					idmatrix[l1][l2] = 0;
				/*
				 * else { idmatrix[l1][l2] = 1.0/(Math.abs(ldthis.x-ldother.x) +
				 * Math.abs(ldthis.y-ldother.y)); }
				 */

				else
				/*
				 * idmatrix[l1][l2] = 1.0 / (Math.sqrt(Math.pow(ldthis.x -
				 * ldother.x, 2) + Math.pow(ldthis.y - ldother.y, 2)));
				 */
				if ((Math.abs(ldthis.x - ldother.x) == 1)
						&& (Math.abs(ldthis.y - ldother.y) == 1))
					idmatrix[l1][l2] = 0;
				else if (((Math.abs(ldthis.x - ldother.x) == 1) && (Math
						.abs(ldthis.y - ldother.y) == 0))
						|| ((Math.abs(ldthis.y - ldother.y) == 1) && (Math
								.abs(ldthis.x - ldother.x) == 0)))
					idmatrix[l1][l2] = 1;
				else
					idmatrix[l1][l2] = 0;
			}
		}
		/*
		 * cutn = 5; if (cutn > datapoints.size()) cutn = datapoints.size();
		 * 
		 * System.out.println("First " + cutn + "x" + cutn +
		 * " elements of weight matrix:");
		 */
		// print weight matrix
		/*
		 * for (int x = 0; x < N; x++) { for (int y = 0; y < N; y++) {
		 * System.out.format("%.0f,", idmatrix[x][y]); } System.out.println(); }
		 */
		// aggregate of spatial weights
		double s0 = 0;
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				s0 += idmatrix[x][y];
			}
		}

		// calculate Moran I.
		// I = n/S0 * (sum{i=1..n} sum{j=1..n} wij(yi - ym))(yj - ym) /
		// (sum{i=1..n} (yi - ym)^2)

		double nominator = 0;
		for (int y = 0; y < N; y++) {
			for (int x = 0; x < N; x++) {
				LocationData z1 = datapoints.get(x);
				LocationData z2 = datapoints.get(y);
				// double xx = idmatrix[x][y] * (z1.value - mean)
				// * (z2.value - mean);
				// System.out.print(xx + ", ");
				nominator += idmatrix[x][y] * (z1.value - mean)
						* (z2.value - mean);
			}
			// System.out.println();
		}

		double denominator = 0;
		for (LocationData ld : datapoints) {
			denominator += ((ld.value - mean) * (ld.value - mean));
		}

		// just for test purposes
		double sum = 0;
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				sum += idmatrix[x][y];
			}
		}

		moranI = (N / s0) * (nominator / denominator);
		System.out.println("sum:" + sum + " N:" + N + " s0:" + s0
				+ " nominator:" + nominator + " denominator:" + denominator
				+ " => " + moranI);
	}

	// TODO implement!
	public void MoranIb() {

	}

	private class LocationData {
		public double x;
		public double y;
		public double value;

		public LocationData(double x, double y, double v) {
			this.x = x;
			this.y = y;
			this.value = v;
		}
	}

	public void printMoran1() {
		System.out.println(this.getMoran1());
	}

	public void printGearysC() {
		// TODO implement gearysC
	}

	public void printEntropy() {
		System.out.println("Entropy = " + this.getEntropy(false));
	}

	public static void main(String[] args) {
		// load image
		String fileloc;
		if (args.length == 1) {
			fileloc = args[0];
		} else {
			fileloc = DEFAULT_IMAGE_LOCATION;
		}

		BufferedImage iconImg;
		try {
			File imageFile = new File(fileloc);
			iconImg = ImageIO.read(imageFile);

			System.out.println(imageFile.getName()
					+ " has been successfully loaded.");

			ComplexityCalculator c = new ComplexityCalculator(iconImg);

			// c.printMoran1();
			// c.printGearysC();
			c.printEntropy();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int rgbToShortColor(int rgbcolor) {
		int[] rgbs = new int[3];
		int colorDepth = 24;

		rgbs[0] = (rgbcolor & 0xFF0000) >> 16; // get red
		rgbs[1] = (rgbcolor & 0x00FF00) >> 8; // get green
		rgbs[2] = (rgbcolor & 0x0000FF); // get blue
		int bitpos = 0x100; // start with bit 8
		int shortColor = 0;

		for (int i = 0; i < colorDepth; i++) {
			if (i % 3 == 0)
				bitpos >>= 1;
			if ((rgbs[i % 3] & bitpos) > 0)
				shortColor = (shortColor << 1) + 1;
			else
				shortColor <<= 1;
		}
		// System.out.printf("color was %x, converted to %x (%d)\n",rgbcolor,shortColor,shortColor);
		return shortColor;
	}

}
