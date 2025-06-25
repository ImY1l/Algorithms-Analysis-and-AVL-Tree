import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Implements QuickSort manually with logging for each key step.
 * Just basic Java I/O and data handling 
 */
public class QuickSortStep {

    private static int stepCount = 0;

    // Handy class to represent a row from the input
    private static class DataEntry {
        final int number;
        final String label;

        DataEntry(int number, String label) {
            this.number = number;
            this.label = label;
        }

        // Hand-rolled compare logic (not using Comparable)
        int compareTo(DataEntry other) {
            return this.number - other.number;
        }

        public String toString() {
            return number + "/" + label;
        }
    }

    // Reads file and parses selected rows into DataEntry objects
    private static List<DataEntry> readDataFromFile(String path, int startLine, int endLine) throws IOException {
        List<DataEntry> results = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String currentLine;
        int lineNo = 0;

        try {
            while ((currentLine = reader.readLine()) != null) {
                lineNo++;
                if (lineNo < startLine) continue;
                if (lineNo > endLine) break;

                int splitAt = currentLine.indexOf(',');
                if (splitAt > 0) {
                    try {
                        int num = Integer.parseInt(currentLine.substring(0, splitAt).trim());
                        String text = currentLine.substring(splitAt + 1).trim();
                        results.add(new DataEntry(num, text));
                    } catch (NumberFormatException e) {
                        // Malformed number - log and move on
                        System.err.println("Malformed entry at line " + lineNo);
                    }
                }
            }
        } finally {
            reader.close(); // don’t forget to close streams
        }

        return results;
    }

    // Logs each sorting step into the output file
    private static void logStep(List<DataEntry> list, int pivotIdx, BufferedWriter out,
                                String note, int from, int to) throws IOException {
        stepCount++;

        out.write("Step " + stepCount + ": " + note);
        if (from >= 0 && to >= 0) {
            out.write(" (Between indices " + from + " and " + to + ")");
        }
        out.write("\n");

        out.write("Array: [");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) out.write(", ");
            if (i == pivotIdx) out.write("*");
            out.write(list.get(i).toString());
            if (i == pivotIdx) out.write("*");
        }
        out.write("]\n");

        if (pivotIdx != -1) {
            out.write("Pivot = " + list.get(pivotIdx).number + "\n");
        }

        out.write("\n");
    }

    // Recursive quick sort logic
    private static void quickSort(List<DataEntry> list, int start, int end, BufferedWriter out) throws IOException {
        if (start < end) {
            int pivotPosition = partition(list, start, end, out);
            quickSort(list, start, pivotPosition - 1, out);
            quickSort(list, pivotPosition + 1, end, out);
        }
    }

    // Manual in-place partition method (using the last element as pivot)
    private static int partition(List<DataEntry> list, int low, int high, BufferedWriter out) throws IOException {
        DataEntry pivot = list.get(high);
        logStep(list, high, out, "Picking pivot", low, high);

        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;

                // Swap manually
                DataEntry tmp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, tmp);

                if (i != j) {
                    logStep(list, high, out, "Swapped elements", low, high);
                }
            }
        }

        // Put pivot in final place
        DataEntry temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        logStep(list, i + 1, out, "Partition complete", low, high);

        return i + 1;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java QuickSortStep <input_file> <start_row> <end_row>");
            return;
        }

        BufferedWriter logWriter = null;

        try {
            String inputPath = args[0];
            int from = Integer.parseInt(args[1]);
            int to = Integer.parseInt(args[2]);

            List<DataEntry> dataset = readDataFromFile(inputPath, from, to);
            String outputPath = "quick_sort_step_" + from + "_" + to + ".txt";

            logWriter = new BufferedWriter(new FileWriter(outputPath));

            logWriter.write("=== QUICK SORT STEP-BY-STEP ===\n");
            logWriter.write("Input File: " + inputPath + "\n");
            logWriter.write("Rows Processed: " + from + " to " + to + "\n");
            logWriter.write("Total Records: " + dataset.size() + "\n");
            logWriter.write("Pivot Strategy: Use last element as pivot\n\n");

            logStep(dataset, -1, logWriter, "Initial state", -1, -1);

            long startTime = System.nanoTime();

            if (dataset.size() > 1) {
                logWriter.write("=== BEGIN SORT ===\n\n");
                quickSort(dataset, 0, dataset.size() - 1, logWriter);
                logWriter.write("=== SORT COMPLETE ===\n\n");
            }

            long endTime = System.nanoTime();

            logStep(dataset, -1, logWriter, "Sorted result", -1, -1);
            logWriter.write("Steps Taken: " + stepCount + "\n");
            logWriter.write("Elapsed Time: " + ((endTime - startTime) / 1_000_000.0) + " ms\n");

            System.out.println("Done! Sort log saved to " + outputPath);

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        } finally {
            if (logWriter != null) {
                try { logWriter.close(); } catch (IOException ignore) {}
            }
        }
    }
}
