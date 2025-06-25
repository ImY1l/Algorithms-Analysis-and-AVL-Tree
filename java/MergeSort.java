import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements full merge sort on a dataset and measures execution time
 */
public class MergeSort {
    private static class DataEntry implements Comparable<DataEntry> {
        int number;
        String text;
        
        DataEntry(int number, String text) {
            this.number = number;
            this.text = text;
        }
        
        @Override
        public int compareTo(DataEntry other) {
            return Integer.compare(this.number, other.number);
        }
        
        @Override
        public String toString() {
            return number + "," + text;
        }
    }

    /**
     * Reads all data entries from CSV file
     */
    private static List<DataEntry> readDataEntries(String filename) throws IOException {
        List<DataEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    entries.add(new DataEntry(Integer.parseInt(parts[0]), parts[1]));
                }
            }
        }
        return entries;
    }

    /**
     * Writes sorted entries to CSV file
     */
    private static void writeSortedData(List<DataEntry> entries, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (DataEntry entry : entries) {
                writer.write(entry.toString() + "\n");
            }
        }
    }

    /**
     * Merge sort implementation
     */
    private static void mergeSort(List<DataEntry> entries) {
        if (entries.size() > 1) {
            int mid = entries.size() / 2;
            
            // Split into left and right halves
            List<DataEntry> left = new ArrayList<>(entries.subList(0, mid));
            List<DataEntry> right = new ArrayList<>(entries.subList(mid, entries.size()));
            
            // Recursively sort each half
            mergeSort(left);
            mergeSort(right);
            
            // Merge the sorted halves
            merge(entries, left, right);
        }
    }

    private static void merge(List<DataEntry> entries, List<DataEntry> left, List<DataEntry> right) {
        int i = 0, j = 0, k = 0;
        
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) {
                entries.set(k++, left.get(i++));
            } else {
                entries.set(k++, right.get(j++));
            }
        }
        
        // Copy remaining elements
        while (i < left.size()) {
            entries.set(k++, left.get(i++));
        }
        
        while (j < right.size()) {
            entries.set(k++, right.get(j++));
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java MergeSort <input_file>");
            return;
        }
        
        try {
            String inputFile = args[0];
            List<DataEntry> entries = readDataEntries(inputFile);
            
            // Measure execution time (excluding I/O)
            long startTime = System.nanoTime();
            mergeSort(entries);
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000.0; // milliseconds
            
            // Write sorted data
            String outputFile = "merge_sort_" + entries.size() + ".csv";
            writeSortedData(entries, outputFile);
            
            System.out.println("Merge sort completed in " + duration + " ms");
            System.out.println("Sorted data saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}