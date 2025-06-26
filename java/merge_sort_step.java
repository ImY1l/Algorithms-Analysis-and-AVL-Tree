import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class merge_sort_step {
    private static int stepCounter = 0;
    
    private static class DataEntry {
        int number;
        String text;

        DataEntry(int number, String text) {
            this.number = number;
            this.text = text;
        }

        @Override
        public String toString() {
            return number + "/" + text;
        }
    }

    private static List<DataEntry> readDataEntries(String filename, int startRow, int endRow) throws IOException {
        List<DataEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("../datasets/" + filename))) {
            String line;
            int currentRow = 0;
            
            while ((line = reader.readLine()) != null) {
                currentRow++;
                
                if (currentRow >= startRow && currentRow <= endRow) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        entries.add(new DataEntry(Integer.parseInt(parts[0]), parts[1]));
                    }
                }
                
                if (currentRow > endRow) break;
            }
        }
        return entries;
    }

    private static void writeStep(List<DataEntry> entries, BufferedWriter writer, String operation, 
                                int left, int right) throws IOException {
        stepCounter++;
        writer.write("Step " + stepCounter + ": " + operation);
        if (left != -1 && right != -1) {
            writer.write(" (Range: " + left + " to " + right + ")");
        }
        writer.write("\n");
        
        writer.write("Array: [");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) writer.write(", ");
            writer.write(entries.get(i).toString());
        }
        writer.write("]\n\n");
        writer.flush();
    }

    private static void writeStep(List<DataEntry> entries, BufferedWriter writer) throws IOException {
        writeStep(entries, writer, "Initial Configuration", -1, -1);
    }

    private static void merge_sort(List<DataEntry> entries, int left, int right, BufferedWriter writer) throws IOException {
        if (left < right) {
            int mid = left + (right - left) / 2;
            merge_sort(entries, left, mid, writer);
            merge_sort(entries, mid + 1, right, writer);
            merge(entries, left, mid, right, writer);
        }
    }

    private static void merge(List<DataEntry> entries, int left, int mid, int right, BufferedWriter writer) throws IOException {
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

        writeStep(entries, writer, "Merge Complete", left, right);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java merge_sort_step <input_file> <start_row> <end_row>");
            return;
        }
        
        try {
            // Ensure outputs directory exists
            new File("../outputs").mkdirs();
            
            String inputFile = args[0];
            int startRow = Integer.parseInt(args[1]);
            int endRow = Integer.parseInt(args[2]);
            
            List<DataEntry> entries = readDataEntries(inputFile, startRow, endRow);
            String outputFile = "../outputs/merge_sort_step_" + startRow + "_" + endRow + ".txt";
            
            double executionTime = 0.0;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write("=== MERGE SORT ALGORITHM ANALYSIS ===\n");
                writer.write("Input File: ../datasets/" + inputFile + "\n");
                writer.write("Processing Range: Row " + startRow + " to Row " + endRow + "\n");
                writer.write("Total Elements: " + entries.size() + "\n\n");
                
                stepCounter = 0;
                writeStep(entries, writer);
                
                if (entries.size() > 1) {
                    writer.write("--- SORTING PROCESS BEGINS ---\n\n");
                    long startTime = System.nanoTime();
                    merge_sort(entries, 0, entries.size() - 1, writer);
                    long endTime = System.nanoTime();
                    executionTime = (endTime - startTime) / 1_000_000.0;
                    writer.write("--- SORTING PROCESS COMPLETE ---\n\n");
                    
                    writer.write("FINAL RESULT:\n");
                    writer.write("Sorted Array: [");
                    for (int i = 0; i < entries.size(); i++) {
                        if (i > 0) writer.write(", ");
                        writer.write(entries.get(i).toString());
                    }
                    writer.write("]\n\n");
                    writer.write("Total Steps: " + stepCounter + "\n");
                    writer.write("Time: " + String.format("%.6f", executionTime) + " ms\n");
                } else {
                    writer.write("Single element array - already sorted!\n");
                }
            }
            
            System.out.println("Analysis saved to: " + outputFile);
            System.out.println("Elements processed: " + entries.size());
            if (entries.size() > 1) {
                System.out.println("Time: " + String.format("%.6f", executionTime) + " ms");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}