import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * QuickSort algorithm implementation reads a CSV file, sorts it by the integer column using quick sort
 * (with last element as pivot), and saves the sorted result.
 */
public class quick_sort {

    // Data class representing one row
    static class DataItem {
        int number;
        String text;

        DataItem(int number, String text) {
            this.number = number;
            this.text = text;
        }

        String format() {
            return number + "," + text;
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java QuickSort <dataset_filename>");
            return;
        }

        String inputFilename = "../datasets/" + args[0];
        List<DataItem> data = readCSV(inputFilename);

        if (data == null || data.isEmpty()) {
            System.out.println("Error: No data found in file.");
            return;
        }

        // Timing only the sorting part
        long startTime = System.currentTimeMillis();
        quickSort(data, 0, data.size() - 1);
        long endTime = System.currentTimeMillis();

        String outputFilename = "../outputs/quick_sort_" + getDatasetSize(args[0]) + ".csv";
        writeCSV(data, outputFilename);

        System.out.println("Sorting complete.");
        System.out.println("Output saved to: " + outputFilename);
        System.out.println("Execution time (ms): " + (endTime - startTime));
    }

    // Reads the dataset from the file
    private static List<DataItem> readCSV(String filePath) {
        List<DataItem> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        int num = Integer.parseInt(parts[0].trim());
                        String text = parts[1].trim();
                        list.add(new DataItem(num, text));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }
        return list;
    }

    // Writes sorted data to the file
    private static void writeCSV(List<DataItem> list, String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (DataItem item : list) {
                pw.println(item.format());
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + filePath);
        }
    }

    // Extracts number from filename like 
    private static String getDatasetSize(String filename) {
        return filename.replace("dataset_", "").replace(".csv", "");
    }

    // QuickSort (last element as pivot)
    private static void quickSort(List<DataItem> list, int low, int high) {
        if (low < high) {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    // Partition method using last element as pivot
    private static int partition(List<DataItem> list, int low, int high) {
        int pivot = list.get(high).number;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).number < pivot) {
                i++;
                swap(list, i, j);
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    // Swaps two elements in the list
    private static void swap(List<DataItem> list, int i, int j) {
        DataItem temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
