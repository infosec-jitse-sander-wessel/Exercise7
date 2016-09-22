package models;

import key_generator.RandomUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public PrivateKey(BigInteger[] knapsack, BigInteger m, BigInteger n) {
        this.knapsack = knapsack;
        this.m = m;
        this.n = n;
    }

    public static PrivateKey parseFromFile(String fileName) throws IOException {
        String[] keyValues = Files.lines(Paths.get(fileName))
                .map(String::trim)
                .filter(line -> !line.contains(":"))
                .map(line -> line.split("=")[1].trim())
                .toArray(String[]::new);

        BigInteger[] knapsack = Arrays.stream(keyValues[0].split(","))
                .map(String::trim)
                .map(BigInteger::new)
                .toArray(BigInteger[]::new);
        BigInteger m = new BigInteger(keyValues[1]);
        BigInteger n = new BigInteger(keyValues[2]);
        return new PrivateKey(knapsack, m, n);
    }
}
