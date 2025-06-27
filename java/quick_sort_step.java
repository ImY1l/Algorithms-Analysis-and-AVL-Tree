import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class quick_sort_step {

    public static void main(String[] args) {
        // Make sure the user gave us 3 arguments
        if (args.length < 3) {
            System.out.println("Usage: java quick_sort_step <filename> <startRow> <endRow>");
            return;
        }

        String fileName = args[0];
        int from = Integer.parseInt(args[1]);
        int to = Integer.parseInt(args[2]);

        // Read data from the datasets folder
        List<DataItem> entries = readFromCSV("../datasets/" + fileName, from, to);

        if (entries == null || entries.isEmpty()) {
            System.out.println("Could not load data from file.");
            return;
        }

        // Create a list to keep track of sorting steps
        List<String> sortSteps = new ArrayList<>();
        sortSteps.add(snapshot(entries)); // initial state

        // Run quicksort and log each change
        quickSort(entries, 0, entries.size() - 1, sortSteps);

        // Save all the steps into a file in outputs folder
        saveStepsToFile(sortSteps, fileName, from, to);
    }

    // This class stores one line from the CSV
    static class DataItem {
        int number;
        String label;

        DataItem(int number, String label) {
            this.number = number;
            this.label = label;
        }

        public String toString() {
            return number + "/" + label;
        }
    }

    // Reads a portion of a CSV file (from line start to end)
    static List<DataItem> readFromCSV(String path, int start, int end) {
        List<DataItem> list = new ArrayList<>();
        int lineNum = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNum++;

                if (lineNum < start) continue;
                if (lineNum > end) break;

                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        int num = Integer.parseInt(parts[0].trim());
                        String text = parts[1].trim();
                        list.add(new DataItem(num, text));
                    } catch (NumberFormatException ignore) {
                        // skip any line where the number part is invalid
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }

        return list;
    }

    // Returns a string showing the current state of the list
    static String snapshot(List<DataItem> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i));
            if (i < items.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    // Save the sorting steps to a text file
    static void saveStepsToFile(List<String> steps, String inputName, int start, int end) {
        // Make sure the outputs folder exists
        new File("../outputs").mkdirs();

        String outputName = "../outputs/quick_sort_step_" + start + "_" + end + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputName))) {
            for (String step : steps) {
                writer.println(step);
            }
            System.out.println("Saved sort steps to " + outputName);
        } catch (IOException e) {
            System.err.println("Failed to write steps: " + e.getMessage());
        }
    }

    // Sort the list and record each change
    static void quickSort(List<DataItem> list, int low, int high, List<String> steps) {
        if (low < high) {
            int pivot = partition(list, low, high, steps);
            steps.add("pi=" + pivot + " " + snapshot(list));
            quickSort(list, low, pivot - 1, steps);
            quickSort(list, pivot + 1, high, steps);
        }
    }


    // Do the partition and log swaps
    static int partition(List<DataItem> list, int low, int high, List<String> steps) {
        int pivotValue = list.get(high).number;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).number < pivotValue) {
                i++;
                // Swap
                DataItem temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }

        // Final pivot swap
        DataItem temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        return i + 1;
    }
}



