import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by wessel on 9/19/16.
 */
public class PrivateKey {
    public final BigInteger[] knapsack;
    public final BigInteger n;
    public final BigInteger m;

    public PrivateKey(BigInteger[] knapsack) {
        this.knapsack = knapsack;

        RandomUtils random = new RandomUtils();
        BigInteger randomSize = BigInteger.valueOf(knapsack.length);
        BigInteger added = random.nextRandomBigInteger(randomSize);

        n = Arrays.stream(knapsack)
                .reduce(BigInteger::add)
                .orElse(BigInteger.ZERO)
                .add(added);

        randomSize = randomSize.multiply(BigInteger.valueOf(64L));
        m = random.nextRandomBigInteger(randomSize)
                .add(randomSize);
    }
}
