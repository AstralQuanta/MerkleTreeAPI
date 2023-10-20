package com.blueokanna.merkletreeapi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.util.encoders.Hex;

public class MerkleTree {

    private List<String> blockHashes;
    private ExecutorService executorService;
    private String Algorithm_Hash;

    public MerkleTree() {
        this.blockHashes = Collections.synchronizedList(new ArrayList<>());
        this.Algorithm_Hash = "SHA256";      //Default Set SHA256 algorithm
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); //Default Run with Max Threads
    }

    public MerkleTree(String Algorithm) {
        this.blockHashes = Collections.synchronizedList(new ArrayList<>());
        this.Algorithm_Hash = Algorithm;        //Custom algorithm,For example: SHA512
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public MerkleTree(String Algorithm, int numThread) {
        this.blockHashes = Collections.synchronizedList(new ArrayList<>());
        this.Algorithm_Hash = Algorithm;      //Custom algorithm
        this.executorService = Executors.newFixedThreadPool(numThread); //Run on custom Thread number
    }

    public void addBlock(String block) {
        if (block == null || block.isEmpty()) {
            throw new IllegalArgumentException("Block cannot be null or empty.");
        }

        String calHash = calculateHash(block, Algorithm_Hash);
        blockHashes.add(calHash);
    }

    public String getBlockHash(int blockIndex) {
        if (blockIndex < 0 || blockIndex >= blockHashes.size()) {
            throw new IllegalArgumentException("Invalid block index.");
        }

        return blockHashes.get(blockIndex);
    }

    public CompletableFuture<Void> computeRootHashAsync() {
        return CompletableFuture.supplyAsync(this::computeRootHash, executorService)
                .thenAccept(root -> {
                });
    }

    public String computeRootHash() {
        List<String> hashes = new ArrayList<>(blockHashes);

        if (hashes.isEmpty()) {
            return null;
        }

        while (hashes.size() > 1) {
            List<CompletableFuture<String>> intermediateHashFutures = new ArrayList<>();
            for (int i = 0; i < hashes.size(); i += 2) {
                String leftHash = hashes.get(i);
                String rightHash = (i + 1 < hashes.size()) ? hashes.get(i + 1) : leftHash;

                CompletableFuture<String> combinedHashFuture = CompletableFuture
                        .supplyAsync(() -> calculateHash((leftHash + rightHash), Algorithm_Hash), executorService);
                intermediateHashFutures.add(combinedHashFuture);
            }

            CompletableFuture<Void> allOf = CompletableFuture
                    .allOf(intermediateHashFutures.toArray(CompletableFuture[]::new));
            allOf.join();

            hashes.clear();
            for (CompletableFuture<String> future : intermediateHashFutures) {
                hashes.add(future.join());
            }
        }

        return hashes.get(0);
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private String calculateHash(String input, String algorithm) {
        try {
            MessageDigest digest;

            switch (algorithm) {
                case "MD5":
                case "SHA1":
                case "SHA224":
                case "SHA256":
                case "SHA384":
                case "SHA512":
                    digest = MessageDigest.getInstance(algorithm);
                    break;
                case "Whirlpool":
                    return whirlpoolHash(input);
                case "RIPEMD160":
                    return RIPEMD160(input);
                case "SHA3":
                    return SHA3Hash(input);
                default:
                    throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
            }

            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed: " + e.getMessage());
        }
    }

    private String SHA3Hash(String input) {
        SHA3Digest digest = new SHA3Digest(256);
        digest.update(input.getBytes(), 0, input.getBytes().length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return Hex.toHexString(result);
    }

    private String RIPEMD160(String input) {
        Digest digest = new RIPEMD160Digest();
        digest.update(input.getBytes(), 0, input.getBytes().length);
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return bytesToHex(result);
    }

    private String whirlpoolHash(String input) {
        WhirlpoolDigest whirlpoolDigest = new WhirlpoolDigest();
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        whirlpoolDigest.update(data, 0, data.length);

        byte[] hash = new byte[whirlpoolDigest.getDigestSize()];
        whirlpoolDigest.doFinal(hash, 0);

        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
