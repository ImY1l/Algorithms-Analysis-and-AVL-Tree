import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements full quick sort on a dataset and measures execution time
 * Uses last element as pivot as required
 */
public class QuickSort {
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
     * Quick sort implementation
     */
    private static void quickSort(List<DataEntry> entries) {
        quickSort(entries, 0, entries.size() - 1);
    }

    private static void quickSort(List<DataEntry> entries, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(entries, low, high);
            
            // Recursively sort elements before and after partition
            quickSort(entries, low, partitionIndex - 1);
            quickSort(entries, partitionIndex + 1, high);
        }
    }

    private static int partition(List<DataEntry> entries, int low, int high) {
        // pivot (Element to be placed at right position)
        DataEntry pivot = entries.get(high);
        
        int i = low - 1; // Index of smaller element
        
        for (int j = low; j < high; j++) {
            // If current element is smaller than or equal to pivot
            if (entries.get(j).compareTo(pivot) <= 0) {
                i++;
                
                // swap arr[i] and arr[j]
                DataEntry temp = entries.get(i);
                entries.set(i, entries.get(j));
                entries.set(j, temp);
            }
        }
        
        // swap arr[i+1] and arr[high] (or pivot)
        DataEntry temp = entries.get(i + 1);
        entries.set(i + 1, entries.get(high));
        entries.set(high, temp);
        
        return i + 1;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java QuickSort <input_file>");
            return;
        }
        
        try {
            String inputFile = args[0];
            List<DataEntry> entries = readDataEntries(inputFile);
            
            // Measure execution time (excluding I/O)
            long startTime = System.nanoTime();
            quickSort(entries);
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000.0; // milliseconds
            
            // Write sorted data
            String outputFile = "quick_sort_" + entries.size() + ".csv";
            writeSortedData(entries, outputFile);
            
            System.out.println("Quick sort completed in " + duration + " ms");
            System.out.println("Sorted data saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}