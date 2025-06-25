import java.io.*;
import java.util.*;

public class binary_search{

    public static void main(String[] args){
        // Reads file name from user
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter file name: ");
        String filename = scanner.nextLine();

        scanner.close();

        List<Integer> dataList = new ArrayList<>();

        // Reads input file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();

                if (line.isEmpty()) { 
                    continue;
                }

                String[] parts = line.split(",", 2);
                if (parts.length >= 1)
                {
                    int tempNum = Integer.parseInt(parts[0].trim().replaceAll("\"", ""));
                    dataList.add(tempNum);
                }
            }

        } catch (IOException e) {
            System.out.println("File not found.");
            return;
        }

        int n = dataList.size();
        int[] data = new int[n];

        for (int i = 0; i < n; i++) {
            data[i] = dataList.get(i);
        }

        // Best case scenario calculation
        int bestCase = data [n/2];
        long start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            binarySearch(data, bestCase);
        }

        long end = System.nanoTime();
        double bestCaseTime = (end - start) / 1_000.0;

        // Average case scenario calculation
        Random random = new Random();
        start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            int randomIndex = random.nextInt(n);
            binarySearch(data, data[randomIndex]);
        }
        end = System.nanoTime();
        double averageCaseTime = (end - start) / 1_000.0;

        // Worst case scenario calculation
        int worstCase = data[n-1] + 1;
        start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            binarySearch(data, worstCase);
        }
        end = System.nanoTime();
        double worstCaseTime = (end - start) / 1_000.0;

        // Output file to binary_search_n.txt
        String outputFile = "binary_search_n.txt";
        try (PrintWriter writer = new PrintWriter(outputFile))
        {
            writer.printf("Best case time: %.3f microseconds. %n", bestCaseTime);
            writer.printf("Average case time: %.3f microseconds. %n", averageCaseTime);
            writer.printf("Worst case time: %.3f microseconds. %n", worstCaseTime);
        } catch (IOException e) {
            System.out.println("Error writing output file.");
        }

        System.out.println("File saved to ");
    }

    // Binary search implementation
    public static int binarySearch(int[] array, int target)
    {
        int left = 0, right = array.length -1;
        while (left <= right)
        {
            int mid = (left + right) / 2;
            if (array[mid] == target) 
                return mid;
            else if (array[mid] < target) 
                left = mid + 1;
            else 
                right = mid - 1;
        }

        return -1;
    }

}