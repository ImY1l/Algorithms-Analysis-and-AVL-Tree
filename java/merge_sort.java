import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class merge_sort {
    private static class DataEntry {
        int number;
        String text;

        DataEntry(int number, String text) {
            this.number = number;
            this.text = text;
        }

        @Override
        public String toString() {
            return number + "," + text;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java merge_sort <input_file>");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            // Read input file from ../datasets/
            List<DataEntry> entries = readDataEntries(args[0]);
            
            // Perform merge sort
            if (entries.size() > 1) {
                mergeSort(entries, 0, entries.size() - 1);
            }
            
            // Write sorted output to ../outputs/
            String outputFile = "../outputs/merge_sort_" + entries.size() + ".csv";
            ensureOutputDirectoryExists();
            writeOutput(entries, outputFile);
            
            // Print execution time
            long endTime = System.currentTimeMillis();
            System.out.println("Execution time (ms): " + (endTime - startTime));
            
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    private static void ensureOutputDirectoryExists() {
        File outputsDir = new File("../outputs");
        if (!outputsDir.exists()) {
            outputsDir.mkdirs();
        }
    }

    private static List<DataEntry> readDataEntries(String filename) throws IOException {
        List<DataEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("../datasets/" + filename))) {
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

    private static void writeOutput(List<DataEntry> entries, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (DataEntry entry : entries) {
                writer.write(entry.toString());
                writer.newLine();
            }
        }
    }

    private static void mergeSort(List<DataEntry> entries, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(entries, left, mid);
            mergeSort(entries, mid + 1, right);
            merge(entries, left, mid, right);
        }
    }

    private static void merge(List<DataEntry> entries, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        DataEntry[] L = new DataEntry[n1];
        DataEntry[] R = new DataEntry[n2];

        for (int i = 0; i < n1; i++) {
            L[i] = entries.get(left + i);
        }
        for (int j = 0; j < n2; j++) {
            R[j] = entries.get(mid + 1 + j);
        }

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].number <= R[j].number) {
                entries.set(k, L[i]);
                i++;
            } else {
                entries.set(k, R[j]);
                j++;
            }
            k++;
        }

        while (i < n1) {
            entries.set(k, L[i]);
            i++;
            k++;
        }

        while (j < n2) {
            entries.set(k, R[j]);
            j++;
            k++;
        }
    }
}
