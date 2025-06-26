import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class dataset_generator{
    private static final int MAX_INT = 2_000_000_000; // Up to 2 billion
    private static final int STRING_LENGTH = 5;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

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
     * Generates a dataset with n unique entries and saves to CSV file
     */
    public static void generateDataset(int n, String filename) throws IOException {
        if (n <= 0) {
            throw new IllegalArgumentException("Dataset size must be positive");
        }
        
        HashSet<Integer> usedIntegers = new HashSet<>();
        Random random = new Random();
        
        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < n; i++) {
                int num;
                // Ensure uniqueness
                do {
                    num = random.nextInt(MAX_INT) + 1; // 1 to MAX_INT
                } while (usedIntegers.contains(num));
                
                usedIntegers.add(num);
                String str = generateRandomString(random);
                writer.write(num + "," + str + "\n");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java DatasetGenerator <size>");
            return;
        }
        
        try {
            int size = Integer.parseInt(args[0]);
            String filename = "dataset_sample_" + size + ".csv";
            generateDataset(size, filename);
            System.out.println("Dataset generated: " + filename);
        } catch (NumberFormatException e) {
            System.err.println("Invalid size: must be an integer");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}