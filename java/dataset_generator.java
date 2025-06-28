import java.io.*;
import java.util.*;

public class dataset_generator {
    private static final int STRING_LEN = 5;  // Could maybe make this configurable later
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final long MAX_ALLOWED_SIZE = 1_000_000_000L;  // A safe upper bound

    // Helper to create a random lowercase string of fixed length
    private static String createRandomWord(Random rng) {
        StringBuilder result = new StringBuilder(STRING_LEN);
        
        // Grab random characters from ALPHA
        for (int i = 0; i < STRING_LEN; i++) {
            int randIndex = rng.nextInt(ALPHA.length());
            result.append(ALPHA.charAt(randIndex));
        }

        return result.toString();
    }

    // Generates a CSV dataset with format: number,randomString
    public static void generateDataset(int count, String outputFilePath) throws IOException {
        
        // Just doing some bounds checking here
        if (count <= 0 || count > MAX_ALLOWED_SIZE) {
            throw new IllegalArgumentException("Size must be between 1 and " + MAX_ALLOWED_SIZE);
        }

        // Trying to ensure all numbers are unique
        Set<Integer> generatedNums = new HashSet<>(count);
        Random rand = new Random();

        // Honestly not the most efficient way to get large amounts of unique numbers
        while (generatedNums.size() < count) {
            // Note: MAX_ALLOWED_SIZE casted to int; could cause issues for large values
            int next = 1 + rand.nextInt((int) MAX_ALLOWED_SIZE);  
            generatedNums.add(next);
        }

        // Convert set to list for shuffling
        List<Integer> numList = new ArrayList<>(generatedNums);
        Collections.shuffle(numList);  // Just to mix things up

        // Now write everything to file line-by-line
        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (int value : numList) {
                String entry = value + "," + createRandomWord(rand);
                out.write(entry);
                out.newLine();  // Next line in CSV
            }
        }

        System.out.println("Done! Created file with " + count + " entries at: " + outputFilePath);
    }

    public static void main(String[] args) {
        // Check if user gave us the number of entries they want
        if (args.length != 1) {
            System.out.println("Usage: java DatasetGenerator <entry_count>");
            return;
        }

        try {
            int howMany = Integer.parseInt(args[0]);

            // We'll stash files in this datasets folder, might want to parameterize later
            String path = "../datasets/dataset_" + howMany + ".csv";
            generateDataset(howMany, path);
        } catch (NumberFormatException nfEx) {
            System.err.println("Oops, that didn't look like a valid number.");
        } catch (IOException ioEx) {
            System.err.println("Something went wrong during file writing: " + ioEx.getMessage());
        }
    }
}
