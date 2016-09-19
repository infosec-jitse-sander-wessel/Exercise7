import java.math.BigInteger;
import java.util.Random;

/**
 * Created by wessel on 9/19/16.
 */
public class RandomUtils {
    private Random rand;

    public RandomUtils() {
        // todo: get true random source?
        rand = new Random();
    }

    public BigInteger nextRandomBigInteger(BigInteger n) {
        BigInteger result = new BigInteger(n.bitLength(), rand);
        while (result.compareTo(n) >= 0) {
            result = new BigInteger(n.bitLength(), rand);
        }
        return result;
    }
}
