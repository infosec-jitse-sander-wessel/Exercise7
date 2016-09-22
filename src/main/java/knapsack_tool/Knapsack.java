package knapsack_tool;

import models.PrivateKey;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by wessel on 9/21/16.
 */
public class Knapsack {
    private BigInteger[] knapsack;
    private BigInteger inversMultiplier;
    private BigInteger modulator;

    public void decrypt(InputStream in, PrivateKey privateKey) throws Exception {
        knapsack = privateKey.knapsack;
        modulator = privateKey.n;
        inversMultiplier = privateKey.m.modInverse(modulator);
        if (knapsack.length % 8 != 0) {
            throw new Exception("please use a more sensible knapsack size");
        }

        BigInteger[] input = IOUtils.readLines(in).stream()
                .map(line -> new BigInteger(line, 16))
                .toArray(BigInteger[]::new);

        byte[][] decrypted = Arrays.stream(input)
                .map(this::decryptBlock)
                .toArray(byte[][]::new);

        for (byte[] bytes : decrypted) {
            for (byte b : bytes) {
                System.out.print((char) b);
            }
        }
    }

    private byte[] decryptBlock(BigInteger toDecrypt) {
        BigInteger sum = toDecrypt.multiply(inversMultiplier).mod(modulator);
        BigInteger result = new BigInteger(new byte[(knapsack.length / 8) + 8]);

        Arrays.sort(knapsack, (left, right) -> right.compareTo(left));
        for (int idx = 0; idx < knapsack.length; idx++) {
            if (sum.compareTo(knapsack[idx]) >= 0) {
                result = result.setBit(knapsack.length - idx - 1);
                sum = sum.subtract(knapsack[idx]);
            }
        }
        return pad(result.toByteArray(), knapsack.length / 8);
    }

    private byte[] pad(byte[] bytes, int byteCount) {
        if (bytes.length < byteCount) {
            byte[] padding = new byte[byteCount - bytes.length];
            return ArrayUtils.addAll(bytes, padding);
        } else if (bytes.length > byteCount) {
            return Arrays.copyOfRange(bytes, 1, byteCount + 1);
        }
        return bytes;
    }

    public void encrypt(InputStream in, String publicKey) throws Exception {
        knapsack = parseKnapsack(publicKey);
        if (knapsack.length % 8 != 0) {
            throw new Exception("please use a more sensible knapsack size");
        }

        BigInteger[] input = readInput(in, knapsack.length / 8);

        Arrays.stream(input)
                .map(this::encryptBlock)
                .map(integer -> integer.toString(16))
                .forEach(System.out::println);
    }

    private BigInteger encryptBlock(BigInteger toEncrypt) {
        Boolean[] bits = toEncrypt.toString(2).chars()
                .mapToObj(bit -> bit != 0)
                .toArray(Boolean[]::new);

        BigInteger sum = BigInteger.ZERO;
        for (int idx = 0; idx < bits.length; idx++) {
            sum = bits[idx] ? sum : knapsack[idx].add(sum);
        }
        return sum;
    }

    private BigInteger[] readInput(InputStream in, int length) throws IOException {
        byte[] input = IOUtils.toByteArray(in);
        int paddingSize = input.length % length;
        byte[] padding = new byte[paddingSize];
        input = ArrayUtils.addAll(input, padding);


        BigInteger[] result = new BigInteger[input.length / length];
        for (int idx = 0; idx < input.length / length; idx++) {
            result[idx] = new BigInteger(Arrays.copyOfRange(input, idx * length, idx * length + length));
        }
        return result;
    }

    private BigInteger[] parseKnapsack(String publicKey) {
        String[] splitKey = publicKey.trim().split(",");
        return Arrays.stream(splitKey)
                .map(Long::valueOf)
                .map(BigInteger::valueOf)
                .toArray(BigInteger[]::new);
    }
}
