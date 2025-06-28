import java.io.*;
import java.util.*;

/**
 * Iterative QuickSort implementation that reads a CSV file, sorts by numeric value,
 * and writes the sorted output. Handles large datasets without stack overflow.
 */
public class quick_sort {

    /**
     * Represents a single data item with a number and associated text
     */
    static class Data {
        final int number;  // The numeric value used for sorting
        final String text; // The associated text data

        Data(int number, String text) {
            this.number = number;
            this.text = text;
        }
    }

    public static void main(String[] args) {
        // Verify command line argument
        if (args.length < 1) {
            System.out.println("Usage: java quick_sort <input_filename>");
            return;
        }

        // Direct path to datasets folder
        String inputPath = "../datasets/" + args[0];
        
        // Check if file exists
        if (!new File(inputPath).exists()) {
            System.out.println("Error: Input file not found");
            return;
        }

        // Read and validate data
        List<Data> items = readDataFromFile(inputPath);
        if (items == null || items.isEmpty()) {
            System.out.println("Error: No valid data found");
            return;
        }

        // Time the sorting process
        long start = System.currentTimeMillis();
        quickSortIterative(items);
        long runtime = System.currentTimeMillis() - start;

        // Write sorted output
        String outputPath = prepareOutputFilePath(args[0]);
        if (!writeSortedData(items, outputPath)) {
            System.out.println("Error: Could not write output file");
            return;
        }

        // Print only the runtime as required
        System.out.println(runtime);
    }

    /**
     * Reads and parses CSV data file
     */
    private static List<Data> readDataFromFile(String path) {
        List<Data> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split on first comma only to handle text with commas
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        int num = Integer.parseInt(parts[0].trim());
                        String txt = parts[1].trim();
                        items.add(new Data(num, txt));
                    } catch (NumberFormatException e) {
                        // Skip lines with invalid numbers
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            return null;
        }
        return items;
    }

    /**
     * Generates output file path in ../outputs/ folder
     */
    private static String prepareOutputFilePath(String filename) {
        // Extract size from filename (e.g., "dataset_1000.csv" -> "1000")
        String size = filename.replace("dataset_", "")
                             .replace("sample_", "")
                             .replace(".csv", "");
        
        // Ensure outputs directory exists
        new File("../outputs").mkdirs();
        
        return "../outputs/quick_sort_" + size + ".csv";
    }

    /**
     * Writes sorted data to CSV file
     */
    private static boolean writeSortedData(List<Data> data, String path) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            for (Data item : data) {
                writer.println(item.number + "," + item.text);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Iterative QuickSort implementation using a stack
     */
    private static void quickSortIterative(List<Data> data) {
        // Stack to keep track of subarrays to process
        Stack<Integer> stack = new Stack<>();
        
        // Push initial range (whole array)
        stack.push(0);
        stack.push(data.size() - 1);

        while (!stack.isEmpty()) {
            // Get next subarray to process
            int hi = stack.pop();
            int lo = stack.pop();

            if (lo < hi) {
                // Partition and get pivot position
                int pivot = partition(data, lo, hi);

                // Push left subarray indices if it has elements
                if (pivot - 1 > lo) {
                    stack.push(lo);
                    stack.push(pivot - 1);
                }

                // Push right subarray indices if it has elements
                if (pivot + 1 < hi) {
                    stack.push(pivot + 1);
                    stack.push(hi);
                }
            }
        }
    }

    /**
     * Partitions the subarray using last element as pivot
     */
    private static int partition(List<Data> data, int lo, int hi) {
        int pivotVal = data.get(hi).number;
        int i = lo - 1;

        for (int j = lo; j < hi; j++) {
            if (data.get(j).number < pivotVal) {
                i++;
                swap(data, i, j);
            }
        }

        // Move pivot to correct position
        swap(data, i + 1, hi);
        return i + 1;
    }

    /**
     * Swaps two elements in the list
     */
    private static void swap(List<Data> data, int i, int j) {
        Data temp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }
}
