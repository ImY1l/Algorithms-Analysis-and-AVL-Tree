/**
 * Quick sort implementation for large files (chunk-based sorting)
 * Done for CCP6214, by Amena Mohammed Abdulkarem
 * 
 * The idea is to:
 * 1. Split huge file into smaller files (chunks)
 * 2. Sort each chunk separately using an iterative quick sort
 * 3. Merge everything back in sorted order
 * 
 * Note: We only time the final merge step (I/O excluded)
 */

import java.io.*;
import java.util.*;

public class quick_sort {

    // Basic structure for holding our number + text lines
    static class Data {
        final int number;
        final String text;

        Data(int number, String text) {
            this.number = number;
            this.text = text;
        }
    }

    private static final int CHUNK_SIZE = 5000000;  // tweak based on memory limits

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java quick_sort <input_filename>");
            return;
        }

        String datasetPath = "../datasets/" + args[0];
        File inFile = new File(datasetPath);
        if (!inFile.exists()) {
            System.err.println("Oops: File doesn't exist.");
            return;
        }

        File tempFolder = new File("temp_chunks");
        tempFolder.mkdir();  // ignore result here for now

        List<String> chunkPaths = new ArrayList<>();

        // === PHASE 1: Read & split into sorted chunks ===
        try (BufferedReader br = new BufferedReader(new FileReader(inFile))) {
            String currentLine;
            int chunkNo = 0;
            List<Data> collector = new ArrayList<>(CHUNK_SIZE);

            while ((currentLine = br.readLine()) != null) {
                String[] bits = currentLine.split(",", 2);
                if (bits.length == 2) {
                    try {
                        int num = Integer.parseInt(bits[0].trim());
                        String msg = bits[1].trim();
                        collector.add(new Data(num, msg));
                    } catch (NumberFormatException e) {
                        // skip line - not valid
                    }
                }

                if (collector.size() == CHUNK_SIZE) {
                    dumpChunkToFile(collector, chunkNo, chunkPaths);
                    chunkNo++;
                    collector.clear(); // reset for next chunk
                }
            }

            // Final bits
            if (!collector.isEmpty()) {
                dumpChunkToFile(collector, chunkNo, chunkPaths);
            }
        }

        // === PHASE 2: Merge sorted chunks into one file ===
        long tStart = System.currentTimeMillis();
        mergeChunks(chunkPaths, args[0]);
        long tEnd = System.currentTimeMillis();

        // Clean up temp files (optional really)
        for (String f : chunkPaths) new File(f).delete();
        tempFolder.delete();

        System.out.println(tEnd - tStart);  // show sorting time
    }

    private static void dumpChunkToFile(List<Data> chunk, int index, List<String> fileList) throws IOException {
        // Sort in-place before saving
        sortWithQuick(chunk);

        String fname = "temp_chunks/chunk_" + index + ".csv";
        try (PrintWriter out = new PrintWriter(new FileWriter(fname))) {
            for (Data entry : chunk) {
                out.println(entry.number + "," + entry.text);
            }
        }

        fileList.add(fname);
    }

    private static void mergeChunks(List<String> chunkFiles, String originalFile) throws IOException {
        PriorityQueue<ChunkReader> heap = new PriorityQueue<>(Comparator.comparingInt(c -> c.current.number));

        for (String chunkPath : chunkFiles) {
            ChunkReader cr = new ChunkReader(chunkPath);
            if (cr.hasMore()) {
                heap.add(cr);
            }
        }

        String finalOutput = getOutputPath(originalFile);
        try (PrintWriter out = new PrintWriter(new FileWriter(finalOutput))) {
            while (!heap.isEmpty()) {
                ChunkReader smallest = heap.poll();
                out.println(smallest.current.number + "," + smallest.current.text);
                if (smallest.loadNext()) {
                    heap.add(smallest);
                } else {
                    smallest.close();  // no more data
                }
            }
        }
    }

    static class ChunkReader {
        BufferedReader reader;
        Data current;
        String sourceFile;

        ChunkReader(String filePath) throws IOException {
            this.sourceFile = filePath;
            this.reader = new BufferedReader(new FileReader(filePath));
            loadNext();  // initialize first record
        }

        boolean loadNext() throws IOException {
            String nextLine = reader.readLine();
            if (nextLine == null) {
                current = null;
                return false;
            }
            String[] pieces = nextLine.split(",", 2);
            if (pieces.length < 2) return false;  // skip malformed line
            current = new Data(Integer.parseInt(pieces[0].trim()), pieces[1].trim());
            return true;
        }

        boolean hasMore() {
            return current != null;
        }

        void close() throws IOException {
            reader.close();
        }
    }

    private static String getOutputPath(String fname) {
        String cleaned = fname.replace("dataset_", "").replace(".csv", "");
        new File("../outputs").mkdirs();  // make sure it exists
        return "../outputs/quick_sort_" + cleaned + ".csv";
    }

    // Iterative quick sort for the chunk — requirement: must not use recursion
    private static void sortWithQuick(List<Data> list) {
        Stack<Integer> bounds = new Stack<>();
        bounds.push(0);
        bounds.push(list.size() - 1);

        while (!bounds.isEmpty()) {
            int end = bounds.pop();
            int start = bounds.pop();

            if (start < end) {
                int pivotPos = partition(list, start, end);

                // Left partition
                if (pivotPos - 1 > start) {
                    bounds.push(start);
                    bounds.push(pivotPos - 1);
                }

                // Right partition
                if (pivotPos + 1 < end) {
                    bounds.push(pivotPos + 1);
                    bounds.push(end);
                }
            }
        }
    }

    private static int partition(List<Data> items, int low, int high) {
        int pivotVal = items.get(high).number;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (items.get(j).number < pivotVal) {
                i++;
                swap(items, i, j);
            }
        }

        swap(items, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Data> arr, int a, int b) {
        // Could inline this, but clearer this way
        Data temp = arr.get(a);
        arr.set(a, arr.get(b));
        arr.set(b, temp);
    }
}
