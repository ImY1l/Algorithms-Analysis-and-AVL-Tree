import java.io.*;
import java.util.*;

public class BinarySearchStep{

    public static List<String> binarySearchSteps(List<Pair> data, int x) {
        List<String> steps = new ArrayList<>();
        int low = 0;
        int high = data.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Pair pair = data.get(mid);
            steps.add(mid + ": " + pair.number + "/" + pair.word);

            if (pair.number == x) {
                return steps;
            } else if (pair.number < x) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        steps.add("-1"); // not found
        return steps;
    }

    public static List<Pair> readCSV(String filename) {
        List<Pair> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length >= 2) {
                    try {
                        int number = Integer.parseInt(parts[0].trim());
                        String word = parts[1].trim();
                        data.add(new Pair(number, word));
                    } catch (NumberFormatException e) {
                    } // skip invalid row
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return data;
    }

    public static void main(String[] args) {
        String filename = "dataset_sample_1000.csv";
        int target = 613479842; // change target here

        List<Pair> data = readCSV(filename);

        // sorting part
        data.sort(Comparator.comparingInt(p -> p.number));

        List<String> steps = binarySearchSteps(data, target);
        for (String step : steps) {
            System.out.println(step);
        }
    }
}

// store pairs
class Pair {
    int number;
    String word;

    public Pair(int number, String word) {
        this.number = number;
        this.word = word;
    }
}
