import java.io.*;
import java.util.*;

public class binary_search {

    public static void main(String[] args) {
        File baseDir;
        try {
            baseDir = new File(System.getProperty("user.dir")).getParentFile();
        } catch (Exception e) {
            System.out.println("Failed to locate base directory.");
            return;
        }

        File inputDir = new File(baseDir, "outputs");
        File outputDir = new File(baseDir, "outputs");
        outputDir.mkdirs();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String filename = scanner.nextLine();
        scanner.close();

        File inputFile = new File(inputDir, filename);
        if (!inputFile.exists()) {
            System.out.println("File not found: " + inputFile.getAbsolutePath());
            return;
        }

        List<Integer> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length >= 1) {
                    try {
                        int tempNum = Integer.parseInt(parts[0].trim().replaceAll("\"", ""));
                        dataList.add(tempNum);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number: " + parts[0]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
            return;
        }

        int n = dataList.size();
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = dataList.get(i);
        }

        // Best case (middle element)
        int bestCase = data[n / 2];
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            binarySearch(data, bestCase);
        }
        long end = System.nanoTime();
        double bestTime = (end - start) / 1_000_000.0;

        // Average case (random elements)
        Random random = new Random();
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            int randIndex = random.nextInt(n);
            binarySearch(data, data[randIndex]);
        }
        end = System.nanoTime();
        double avgTime = (end - start) / 1_000_000.0;

        // Worst case (value not in list)
        int worstCase = data[n - 1] + 1;
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            binarySearch(data, worstCase);
        }
        end = System.nanoTime();
        double worstTime = (end - start) / 1_000_000.0;

        File outputFile = new File(outputDir, "binary_search_" + n + ".txt");
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.printf("Best case time: %.3f ms%n", bestTime);
            writer.printf("Average case time: %.3f ms%n", avgTime);
            writer.printf("Worst case time: %.3f ms%n", worstTime);
        } catch (IOException e) {
            System.out.println("Error writing output file.");
        }

        System.out.println("Processed " + n + " entries in full dataset.");
        System.out.println("File saved to: " + outputFile.getAbsolutePath());
    }

    public static int binarySearch(int[] array, int target) {
        int left = 0, right = array.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (array[mid] == target)
                return mid;
            else if (array[mid] < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -1;
    }
}
