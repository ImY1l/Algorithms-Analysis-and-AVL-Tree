import java.io.*;
import java.util.*;

public class binary_search{

    public static void main(String[] args){
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

        int chunkSize = 5_000_000;
        int totalCount = 0;
        double totalBestTime = 0, totalAvgTime = 0, totalWorstTime = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            List<Integer> chunk = new ArrayList<>();
            Random random = new Random();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length >= 1) {
                    try {
                        int tempNum = Integer.parseInt(parts[0].trim().replaceAll("\"", ""));
                        chunk.add(tempNum);
                        totalCount++;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number: " + parts[0]);
                    }
                }

                // Process chunk when full
                if (chunk.size() == chunkSize) {
                    double[] times = processChunk(chunk, random);
                    totalBestTime += times[0];
                    totalAvgTime += times[1];
                    totalWorstTime += times[2];
                    chunk.clear();
                }
            }

            // Process remaining data
            if (!chunk.isEmpty()) {
                double[] times = processChunk(chunk, random);
                totalBestTime += times[0];
                totalAvgTime += times[1];
                totalWorstTime += times[2];
            }

        } catch (IOException e) {
            System.out.println("Error reading file.");
            return;
        }

        // Write output
        File outputFile = new File(outputDir, "binary_search_" + totalCount + ".txt");
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.printf("Best case total time: %.3f ms%n", totalBestTime);
            writer.printf("Average case total time: %.3f ms%n", totalAvgTime);
            writer.printf("Worst case total time: %.3f ms%n", totalWorstTime);
        } catch (IOException e) {
            System.out.println("Error writing output file.");
        }

        System.out.println("Processed " + totalCount + " entries in chunks.");
        System.out.println("File saved to: " + outputFile.getAbsolutePath());
    }

    // Binary search implementation
    public static int binarySearch(int[] array, int target)
    {
        int left = 0, right = array.length -1;
        while (left <= right)
        {
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

    public static double[] processChunk(List<Integer> chunkList, Random random) {
        int n = chunkList.size();
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = chunkList.get(i);
        }

        double bestTime = 0, avgTime = 0, worstTime = 0;

        // Best case (middle element)
        int bestCase = data[n / 2];
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            binarySearch(data, bestCase);
        }
        long end = System.nanoTime();
        bestTime = (end - start) / 1_000_000.0;

        // Average case (random targets)
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            int randIndex = random.nextInt(n);
            binarySearch(data, data[randIndex]);
        }
        end = System.nanoTime();
        avgTime = (end - start) / 1_000_000.0;

        // Worst case (value not in list)
        int worstCase = data[n - 1] + 1;
        start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            binarySearch(data, worstCase);
        }
        end = System.nanoTime();
        worstTime = (end - start) / 1_000_000.0;

        return new double[]{bestTime, avgTime, worstTime};
    }

}