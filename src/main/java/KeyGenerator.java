import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by wessel on 9/19/16.
 */
public class KeyGenerator {
    private RandomUtils random = new RandomUtils();

    public static void main(String[] args) {
        KeyGenerator keyGenerator = new KeyGenerator();
        BigInteger[] knapsack = keyGenerator.generateKnapsack(Integer.valueOf(args[0]));

        PrivateKey privateKey = new PrivateKey(knapsack);

        BigInteger[] publicKey = keyGenerator.generatePublicKey(privateKey);

        printKeys(privateKey, publicKey);
    }

    private static void printKeys(PrivateKey privateKey, BigInteger[] publicKey) {
        System.out.println("Private key:");
        String printSuperIncreasingKnapsack = Arrays.stream(privateKey.knapsack)
                .map(BigInteger::toString)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        System.out.println("    knapsack = " + printSuperIncreasingKnapsack);
        System.out.println("    m = " + privateKey.m);
        System.out.println("    n = " + privateKey.n);

        System.out.println("\nPublic key:");
        String printKnapsack = Arrays.stream(publicKey)
                .map(BigInteger::toString)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
        System.out.println("    super increasing knapsack = " + printKnapsack);
    }

    private BigInteger[] generatePublicKey(PrivateKey privateKey) {
        return Arrays.stream(privateKey.knapsack)
                .map(a -> a.multiply(privateKey.m))
                .map(element -> element.mod(privateKey.n))
                .toArray(BigInteger[]::new);
    }

    private BigInteger[] generateKnapsack(Integer knapsackSize) {
        BigInteger[] key = new BigInteger[knapsackSize];
        BigInteger sum = BigInteger.ZERO;
        BigInteger randSize = BigInteger.valueOf(knapsackSize * 2L);

        for (int i = 0; i < knapsackSize; ++i) {
            BigInteger added = random.nextRandomBigInteger(randSize).add(BigInteger.ONE);
            key[i] = sum.add(added);
            sum = sum.add(key[i]);
        }

        return key;
    }
}
