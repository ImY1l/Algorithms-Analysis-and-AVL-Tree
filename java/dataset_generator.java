import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class dataset_generator {
    private static final int STRING_LENGTH = 5;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final long MAX_NUMBER = 2_000_000_000L; // Up to 2 billion

    /**
     * Generates a random string of fixed length
     */
    private static String generateRandomString(Random random) {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Generates dataset using sequential numbers with random strings
     */
    public static void generateDataset(long n, String filename) throws IOException {
        if (n <= 0) {
            throw new IllegalArgumentException("Dataset size must be positive");
        }
        if (n > MAX_NUMBER) {
            throw new IllegalArgumentException("Dataset size cannot exceed " + MAX_NUMBER);
        }

        Random random = new Random();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (long i = 1; i <= n; i++) {
                writer.write(i + "," + generateRandomString(random));
                writer.newLine();
                
                // Progress reporting
                if (i % 10_000_000 == 0) {
                    System.out.printf("Generated %,d of %,d entries (%.1f%%)%n",
                            i, n, (i * 100.0 / n));
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java dataset_generator <size>");
            return;
        }
        
        try {
            long size = Long.parseLong(args[0]);
            String filename = "../datasets/dataset_sample_" + size + ".csv";
            
            System.out.println("Generating dataset with " + size + " entries...");
            generateDataset(size, filename);
            System.out.println("Dataset successfully generated: " + filename);
        } catch (NumberFormatException e) {
            System.err.println("Invalid size: must be a positive integer");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}