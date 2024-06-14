package neat;

import java.util.Random;

import net.jodk.lang.FastMath;

/**
 * @author msimmerson
 *
 * Provides some useful maths utility methods used for creating and modifying weights etc 
 */
public class MathUtils
{
    
    // clamped to +plus/-minus
    public static double nextClampedDouble(double minus, double plus, Random rand) {
    	return ((rand.nextDouble() - 0.5) * (plus - minus)); 	
    }
    
    public static double nextDouble(Random rand) {
    	return (rand.nextDouble()); 	
    }

    public static double nextPlusMinusOne(Random rand) {
    	return (nextClampedDouble(-1, 1,rand));
    }
    
    public static double euclideanDist(double[] ePointOne, double[] ePoint2) {
    	double diff;
    	int i;
    	double eDist = 0;
    	
        for (i = 0; i < ePointOne.length; i++) {
        	if (i < ePoint2.length) {
        		diff = ePointOne[i] - ePoint2[i];
        	} else {
        		diff = 0;
        	}
        	eDist += (diff * diff);
        }
        eDist = FastMath.sqrt(eDist);
        
    	return (eDist);
    }
}
