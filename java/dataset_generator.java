import java.io.*;
import java.util.*;

public class dataset_generator {
    private static final int STRING_LENGTH = 5;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final long MAX_NUMBER = 1_000_000_000L; // 1 billion as required
    private static final int CHUNK_SIZE = 10_000_000; // Process 10M records at a time
    private static final int PROGRESS_INTERVAL = 1_000_000;

    private static String generateRandomString(Random random) {
        char[] chars = new char[STRING_LENGTH];
        for (int i = 0; i < STRING_LENGTH; i++) {
            chars[i] = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
        }
        return new String(chars);
    }

    public static void generateDataset(long size, String filename) throws IOException {
        if (size <= 0 || size > MAX_NUMBER) {
            throw new IllegalArgumentException("Size must be between 1 and 1,000,000,000");
        }

        Random random = new Random();
        long[] numbers = new long[CHUNK_SIZE];
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            long remaining = size;
            long base = 1; // Start from 1
            
            while (remaining > 0) {
                int currentChunk = (int) Math.min(CHUNK_SIZE, remaining);
                
                // Fill array with sequential numbers
                for (int i = 0; i < currentChunk; i++) {
                    numbers[i] = base + i;
                }
                
                // Fisher-Yates shuffle
                for (int i = currentChunk - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    long temp = numbers[i];
                    numbers[i] = numbers[j];
                    numbers[j] = temp;
                }
                
                // Write shuffled chunk to file
                for (int i = 0; i < currentChunk; i++) {
                    writer.write(numbers[i] + "," + generateRandomString(random));
                    writer.newLine();
                    
                    // Progress reporting
                    if ((size - remaining + i) % PROGRESS_INTERVAL == 0) {
                        System.out.printf("Progress: %,d/%,d (%.1f%%)%n",
                                size - remaining + i, size, 
                                ((size - remaining + i) * 100.0 / size));
                    }
                }
                
                base += currentChunk;
                remaining -= currentChunk;
            }
        }
        
        System.out.println("Successfully generated dataset with " + size + " entries");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java dataset_generator <size>");
            return;
        }
        
        try {
            long size = Long.parseLong(args[0]);
            String filename = "../datasets/dataset_" + size + ".csv";
            
            System.out.println("Generating dataset with " + size + " entries...");
            generateDataset(size, filename);
            System.out.println("Saved to: " + filename);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid size: must be a positive integer");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
}
