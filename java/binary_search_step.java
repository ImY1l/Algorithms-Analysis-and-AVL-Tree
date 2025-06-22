import java.io.*;
import java.util.*;

public class binary_search_step {

    public static List<String[]> readCSV(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",", 2);
                if (row.length >= 2) {
                    try {
                        Integer.parseInt(row[0]); // ensure it's a number
                        data.add(row);
                    } catch (NumberFormatException e) {
                    } // skip invalid
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return data;
    }

    public static List<String> binarySearchSteps(List<String[]> data, int target) {
        List<String> steps = new ArrayList<>();
        List<int[]> sorted = new ArrayList<>();
        
        for (String[] row : data) {
            sorted.add(new int[]{Integer.parseInt(row[0]), data.indexOf(row)});
        }
        sorted.sort(Comparator.comparingInt(a -> a[0]));

        int low = 0, high = sorted.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int num = sorted.get(mid)[0];
            int originalIndex = sorted.get(mid)[1];
            String word = data.get(originalIndex)[1];
            steps.add(mid + ": " + num + "/" + word);

            if (num == target) {
                return steps;
            } else if (num < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        steps.add("-1"); // not found
        return steps;
    }

    public static void txtFile(List<String> steps, int target) {
        String filename = "binary_search_step_" + target + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String step : steps) {
                writer.println(step);
            }
            System.out.println("Steps written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String filename = "dataset_sample_1000.csv";
        int target = 613479842; // change target value here

        List<String[]> data = readCSV(filename);
        List<String> steps = binarySearchSteps(data, target);
        txtFile(steps, target);
    }
}
