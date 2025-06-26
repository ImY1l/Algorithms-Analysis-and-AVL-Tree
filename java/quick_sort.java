import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class quick_sort {
    private static class data_entry implements Comparable<data_entry> {
        int number;
        String text;
        
        data_entry(int number, String text) {
            this.number = number;
            this.text = text;
        }
        
        @Override
        public int compareTo(data_entry other) {
            return Integer.compare(this.number, other.number);
        }
        
        @Override
        public String toString() {
            return number + "," + text;
        }
    }

    private static List<data_entry> read_data_entries(String filename) throws IOException {
        List<data_entry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("../datasets/" + filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    entries.add(new data_entry(Integer.parseInt(parts[0]), parts[1]));
                }
            }
        }
        return entries;
    }

    private static void write_sorted_data(List<data_entry> entries, String filename) throws IOException {
        // Ensure outputs directory exists
        new File("../outputs").mkdirs();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("../outputs/" + filename))) {
            for (data_entry entry : entries) {
                writer.write(entry.toString());
                writer.newLine();
            }
        }
    }

    private static void quickSort(List<data_entry> entries) {
        quickSort(entries, 0, entries.size() - 1);
    }

    private static void quickSort(List<data_entry> entries, int low, int high) {
        if (low < high) {
            int partition_index = partition(entries, low, high);
            quickSort(entries, low, partition_index - 1);
            quickSort(entries, partition_index + 1, high);
        }
    }

    private static int partition(List<data_entry> entries, int low, int high) {
        data_entry pivot = entries.get(high);
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (entries.get(j).compareTo(pivot) <= 0) {
                i++;
                data_entry temp = entries.get(i);
                entries.set(i, entries.get(j));
                entries.set(j, temp);
            }
        }
        
        data_entry temp = entries.get(i + 1);
        entries.set(i + 1, entries.get(high));
        entries.set(high, temp);
        
        return i + 1;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java quick_sort <input_file>");
            return;
        }
        
        try {
            String inputFile = args[0];
            List<data_entry> entries = read_data_entries(inputFile);
            
            long startTime = System.nanoTime();
            quickSort(entries);
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000.0;
            
            String outputFile = "quick_sort_" + entries.size() + ".csv";
            write_sorted_data(entries, outputFile);
            
            System.out.println("quick_sort completed in " + duration + " ms");
            System.out.println("Sorted data saved to: ../outputs/" + outputFile);
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}