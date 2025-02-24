package xyz.kimherala.digestive;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        final Set<String> algorithmsSupported = Set.of(
                "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512", "SHA-512/224", "SHA-512/256",
                "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"
        );
        final Set<String> encodingsSupported = Set.of(
                "int", "bin", "octal", "hex", "base64"
        );
        String hashHint = "SHA-256";
        String encodingHint = "hex";
        String directoryPath = ".";

        boolean verboseOutputFormating = false;

        // Command line flags/options
        Options options = new Options();
        options.addOption("h", false, "Display manual/help page.");
        options.addOption("a", true, "Select hashing function.");
        options.addOption("e", true, "Select encoding for output.");
        options.addOption("p", true, "Select path for directory to hash.");
        options.addOption("v", false, "Verbose output formating.");

        CommandLineParser parser = new DefaultParser();
        try {
            // The third parameter (true) tells the parser to stop when it hits a non-option argument.
            CommandLine cmd = parser.parse(options, args, false);

            if (cmd.hasOption("h")) {
                System.out.println("I hope that this helps. :)");
                System.exit(0);
            }
            if (cmd.hasOption("a")) {
                hashHint = cmd.getOptionValue("a");
                if (!algorithmsSupported.contains(hashHint)) {
                    System.out.println("Unsupported algorithm detected!");
                    System.exit(0);
                }
            }
            if (cmd.hasOption("e")) {
                encodingHint = cmd.getOptionValue("e");
                if (!encodingsSupported.contains(encodingHint)) {
                    System.out.println("Unsupported encoding detected!");
                    System.exit(0);
                }
            }
            if (cmd.hasOption("p")) {
                directoryPath = cmd.getOptionValue("p");
            }
            if (cmd.hasOption("v")) {
                verboseOutputFormating = true;
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // Checking if path is valid and that the path is not a file.
        try {
            Paths.get(directoryPath);
            Objects.requireNonNull(new File(directoryPath).listFiles());
        } catch (InvalidPathException | NullPointerException e) {
            System.out.println("Invalid directory path detected!");
            System.exit(0);
        }

        final int processors = Runtime.getRuntime().availableProcessors();

        List<String> result = new ArrayList<>();
        try (ExecutorService threadPool = Executors.newFixedThreadPool(processors)) {
            List<String> fileNames = Ioutil.listFileInDir(directoryPath);

            if (verboseOutputFormating) {
                System.out.println("Using: " + processors + " threads.");
                System.out.println("Algorithm: " + hashHint + ".");
                System.out.println("Encoding: " + encodingHint + ".\n");
                System.out.printf("Digesting %d files.\n\n", fileNames.size());
            }

            List<Callable<String>> digestCallables = new ArrayList<>();
            for (String fileName : fileNames) {
                digestCallables.add(new FileDigestTask(Path.of(directoryPath, fileName), hashHint, encodingHint));
            }

            List<Future<String>> digestFutures = threadPool.invokeAll(digestCallables);
            for (Future<String> digestFuture : digestFutures) {
                result.add(digestFuture.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        for (String digest : result) {
            System.out.println(digest + "\n");
        }
    }
}