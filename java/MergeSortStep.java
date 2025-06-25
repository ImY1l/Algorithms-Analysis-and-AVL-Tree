import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements merge sort with comprehensive step-by-step logging for a specified range of rows.
 * The algorithm uses divide-and-conquer approach to recursively split arrays and merge them in sorted order.
 */
public class MergeSortStep {
    // Counter to track merge operations for meaningful step numbering
    private static int stepCounter = 0;
    
    /**
     * Data structure to hold paired numeric and text values from CSV input.
     * Encapsulates the relationship between number and text for sorting operations.
     */
    private static class DataEntry {
        int number;    // Primary key for sorting comparisons
        String text;   // Associated text data maintained during sorting
        
        /**
         * Constructor creates a data entry linking numeric and textual components.
         * This pairing ensures data integrity throughout the sorting process.
         */
        DataEntry(int number, String text) {
            this.number = number;
            this.text = text;
        }
        
        /**
         * String representation formatted for clear output display.
         * Uses number/text format for consistent readability in logs.
         */
        @Override
        public String toString() {
            return number + "/" + text;
        }
    }

    /**
     * Extracts data entries from CSV file within specified row boundaries.
     * Implements selective reading to process only the required data range,
     * optimizing memory usage by avoiding unnecessary data loading.
     */
    private static List<DataEntry> readDataEntries(String filename, int startRow, int endRow) throws IOException {
        List<DataEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int currentRow = 0;
            
            // Sequential file processing to locate target row range
            while ((line = reader.readLine()) != null) {
                currentRow++;
                
                // Process only rows within specified range for efficiency
                if (currentRow >= startRow && currentRow <= endRow) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        // Parse numeric component and preserve text component
                        entries.add(new DataEntry(Integer.parseInt(parts[0]), parts[1]));
                    }
                }
                
                // Early termination once target range is exceeded
                if (currentRow > endRow) break;
            }
        }
        return entries;
    }

    /**
     * Records current array state with contextual information for analysis.
     * Provides detailed step information including operation type and array bounds
     * to enable comprehensive understanding of the sorting progression.
     */
    private static void writeStep(List<DataEntry> entries, BufferedWriter writer, String operation, 
                                 int left, int right) throws IOException {
        stepCounter++;
        
        // Write step header with operation context
        writer.write("Step " + stepCounter + ": " + operation);
        if (left != -1 && right != -1) {
            writer.write(" (Range: " + left + " to " + right + ")");
        }
        writer.write("\n");
        
        // Write current array state with clear formatting
        writer.write("Array: [");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) writer.write(", ");
            writer.write(entries.get(i).toString());
        }
        writer.write("]\n\n");
        writer.flush(); // Ensure immediate output for real-time tracking
    }

    /**
     * Overloaded method for initial state recording without range specification.
     * Used for documenting the starting configuration before sorting begins.
     */
    private static void writeStep(List<DataEntry> entries, BufferedWriter writer) throws IOException {
        writeStep(entries, writer, "Initial Configuration", -1, -1);
    }

    /**
     * Recursive merge sort implementation using divide-and-conquer strategy.
     * Splits array into progressively smaller subarrays until single elements remain,
     * then systematically merges them back together in sorted order.
     * 
     * Time complexity: O(n log n) through balanced binary tree of recursive calls
     * Space complexity: O(n) for temporary arrays used in merge operations
     */
    private static void mergeSort(List<DataEntry> entries, int left, int right, BufferedWriter writer) 
            throws IOException {
        
        // Base case: single element subarrays are inherently sorted
        if (left < right) {
            // Calculate midpoint using overflow-safe arithmetic
            int mid = left + (right - left) / 2;
            
            // Recursive divide: sort left subarray independently
            mergeSort(entries, left, mid, writer);
            
            // Recursive divide: sort right subarray independently  
            mergeSort(entries, mid + 1, right, writer);
            
            // Conquer: merge the two sorted subarrays into single sorted segment
            merge(entries, left, mid, right, writer);
        }
    }

    /**
     * Merges two adjacent sorted subarrays into a single sorted segment.
     * Implements the core merge operation that maintains sorted order while
     * combining pre-sorted segments using temporary arrays for safe manipulation.
     * 
     * The merge process compares elements from both segments and places them
     * in correct order, handling remaining elements when one segment is exhausted.
     */
    private static void merge(List<DataEntry> entries, int left, int mid, int right, BufferedWriter writer) 
            throws IOException {
        
        // Calculate sizes of left and right segments to be merged
        int n1 = mid - left + 1;  // Size of left segment
        int n2 = right - mid;     // Size of right segment
        
        // Create temporary arrays to hold segment copies during merge
        DataEntry[] L = new DataEntry[n1];  // Left segment copy
        DataEntry[] R = new DataEntry[n2];  // Right segment copy
        
        // Copy left segment data to temporary array for safe manipulation
        for (int i = 0; i < n1; i++) {
            L[i] = entries.get(left + i);
        }
        
        // Copy right segment data to temporary array for safe manipulation
        for (int j = 0; j < n2; j++) {
            R[j] = entries.get(mid + 1 + j);
        }
        
        // Initialize pointers for merge operation
        int i = 0;      // Pointer for left temporary array
        int j = 0;      // Pointer for right temporary array  
        int k = left;   // Pointer for main array merge position
        
        // Main merge loop: compare and place elements in sorted order
        while (i < n1 && j < n2) {
            // Compare numeric values to determine proper placement
            if (L[i].number <= R[j].number) {
                entries.set(k, L[i]);  // Place left element in main array
                i++;
            } else {
                entries.set(k, R[j]);  // Place right element in main array
                j++;
            }
            k++;
        }
        
        // Handle remaining elements from left segment if right is exhausted
        while (i < n1) {
            entries.set(k, L[i]);
            i++;
            k++;
        }
        
        // Handle remaining elements from right segment if left is exhausted
        while (j < n2) {
            entries.set(k, R[j]);
            j++;
            k++;
        }
        
        // Document the merge result with contextual information
        writeStep(entries, writer, "Merge Complete", left, right);
    }

    /**
     * Main execution method coordinating the entire merge sort process.
     * Handles command-line processing, file operations, and algorithm execution
     * while maintaining comprehensive logging for educational analysis.
     */
    public static void main(String[] args) {
        // Validate command-line arguments for proper program invocation
        if (args.length != 3) {
            System.out.println("Usage: java MergeSortStep <input_file> <start_row> <end_row>");
            return;
        }
        
        try {
            // Parse command-line parameters
            String inputFile = args[0];
            int startRow = Integer.parseInt(args[1]);
            int endRow = Integer.parseInt(args[2]);
            
            // Load specified data range from input file
            List<DataEntry> entries = readDataEntries(inputFile, startRow, endRow);
            
            // Generate descriptive output filename
            String outputFile = "merge_sort_step_" + startRow + "_" + endRow + ".txt";
            
            // Variable to store execution time for final display
            double executionTime = 0.0;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                // Write comprehensive analysis header
                writer.write("=== MERGE SORT ALGORITHM ANALYSIS ===\n");
                writer.write("Input File: " + inputFile + "\n");
                writer.write("Processing Range: Row " + startRow + " to Row " + endRow + "\n");
                writer.write("Total Elements: " + entries.size() + "\n");
                writer.write("Algorithm: Divide-and-Conquer Merge Sort\n");
                writer.write("Time Complexity: O(n log n)\n");
                writer.write("Space Complexity: O(n)\n\n");
                
                // Reset step counter for new analysis
                stepCounter = 0;
                
                // Document initial unsorted state
                writeStep(entries, writer);
                
                // Execute merge sort with comprehensive step logging and timing
                if (entries.size() > 1) {
                    writer.write("--- SORTING PROCESS BEGINS ---\n\n");
                    
                    // Record start time for performance measurement
                    long startTime = System.nanoTime();
                    
                    mergeSort(entries, 0, entries.size() - 1, writer);
                    
                    // Calculate execution time in milliseconds
                    long endTime = System.nanoTime();
                    executionTime = (endTime - startTime) / 1_000_000.0;
                    
                    writer.write("--- SORTING PROCESS COMPLETE ---\n\n");
                    
                    // Document final sorted result
                    writer.write("FINAL RESULT:\n");
                    writer.write("Sorted Array: [");
                    for (int i = 0; i < entries.size(); i++) {
                        if (i > 0) writer.write(", ");
                        writer.write(entries.get(i).toString());
                    }
                    writer.write("]\n\n");
                    writer.write("Total Steps Executed: " + stepCounter + "\n");
                    writer.write("Merge sort completed in " + String.format("%.6f", executionTime) + " ms\n");
                } else {
                    writer.write("Single element array - already sorted!\n");
                    System.out.println("Single element array - no sorting required!");
                }
            }
            
            // Provide completion feedback with analysis summary
            System.out.println("Merge sort analysis completed successfully!");
            System.out.println("Detailed step-by-step analysis saved to: " + outputFile);
            System.out.println("Elements processed: " + entries.size());
            System.out.println("Total merge steps: " + stepCounter);
            if (entries.size() > 1) {
                System.out.println("Merge sort completed in " + String.format("%.6f", executionTime) + " ms");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid row numbers: must be integers");
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}