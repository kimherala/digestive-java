package xyz.kimherala.digestive;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;

public class FileDigestTask implements Callable<String> {
    final Path filePath;
    final String hashHint;
    final String encodingHint;

    FileDigestTask(Path filePath, String hashHint, String encodingHint) {
        this.filePath = filePath;
        this.hashHint = hashHint;
        this.encodingHint = encodingHint;
    }

    @Override
    public String call() {
        byte[] rawHash;

        try {
            MessageDigest digest = MessageDigest.getInstance(hashHint);
            rawHash = digestFile(digest, filePath);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return String.format("%s %s", formatHash(encodingHint, rawHash), filePath.getFileName());
    }

    private static byte[] digestFile(MessageDigest digest, Path filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(String.valueOf(filePath)))) {
            int readBytesCount;
            byte[] buffer = new byte[8192];

            while ((readBytesCount = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, readBytesCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return digest.digest();
    }

    private static String formatHash(String encodingHint, byte[] rawHash) {
        if (encodingHint.equals("hex")) {
            return HexFormat.of().formatHex(rawHash);
        }
        if (encodingHint.equals("base64")) {
            return new String(Base64.getEncoder().encode(rawHash), StandardCharsets.UTF_8);
        }

        // How many bytes encoded per line, only applies to int, bin and octal encoding.
        int rowLength = 16;

        if (Objects.equals(encodingHint, "bin")) {
            rowLength = 8;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < rawHash.length; i++) {
            if (Objects.equals(encodingHint, "int")) {
                // The bitmask of 0xFF only show last 8 bits of the byte.
                result.append(String.format("%03d", rawHash[i] & 0xFF));
            }
            if (Objects.equals(encodingHint, "bin")) {
                StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(rawHash[i] & 0xFF));
                while (binaryString.length() < 8) {
                    binaryString.insert(0, "0");
                }
                result.append(binaryString.toString());
            }
            if (Objects.equals(encodingHint, "octal")) {
                result.append(String.format("%03o", rawHash[i] & 0xFF));
            }
            if ((i+1) != rawHash.length) {
                result.append(" ");
            }
            if ((i+1) % rowLength == 0 && (i+1) < rawHash.length) {
                result.append("\n");
            }
        }

        return result.toString();
    }
}
