import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class dataset_generator {
    private static final int STRING_LENGTH = 5;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final long MAX_NUMBER = 2_147_483_647L; // 2^31 - 1 (max 32-bit positive int)

    private static String generateRandomString(Random random) {
        StringBuilder sb = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static void generateDataset(long n, String filename) throws IOException {
        if (n <= 0) {
            throw new IllegalArgumentException("Dataset size must be positive");
        }

        Random random = new Random();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (long i = 0; i < n; i++) {
                // Generate completely random number between 1 and MAX_NUMBER
                long number = 1 + random.nextLong(MAX_NUMBER);
                writer.write(number + "," + generateRandomString(random));
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
            String filename = "../datasets/dataset_random_" + size + ".csv";
            
            System.out.println("Generating random dataset with " + size + " entries...");
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
