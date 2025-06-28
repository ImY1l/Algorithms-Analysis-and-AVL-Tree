import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class merge_sort {
    private static final int CHUNK_SIZE = 5_000_000; // Records per chunk
    private static final String TEMP_DIR = "../temp";
    private static final String OUTPUT_DIR = "../outputs";

    private static class DataEntry implements Comparable<DataEntry> {
        final int number;
        final String text;

        DataEntry(int number, String text) {
            this.number = number;
            this.text = text;
        }

        @Override
        public int compareTo(DataEntry other) {
            return Integer.compare(this.number, other.number);
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
            List<File> sortedChunks = processInChunks(args[0]);
            String fileName = args[0].replaceFirst("dataset_", "");
            String outputFile = OUTPUT_DIR + "/merge_sort_" + fileName;
            mergeSortedChunks(sortedChunks, outputFile);

            // Cleanup temporary files
            for (File chunk : sortedChunks) {
                chunk.delete();
            }

            System.out.println("Processing completed in " + 
                (System.currentTimeMillis() - startTime) + " ms");
        } catch (Exception e) {
            System.err.println("Error during sorting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<File> processInChunks(String inputFile) throws Exception {
        new File(TEMP_DIR).mkdirs();
        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
        List<Future<File>> futures = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new FileReader("../datasets/" + inputFile))) {
            
            List<DataEntry> currentChunk = new ArrayList<>(CHUNK_SIZE);
            int chunkCounter = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    currentChunk.add(new DataEntry(
                        Integer.parseInt(parts[0]), parts[1]));
                }

                if (currentChunk.size() >= CHUNK_SIZE) {
                    final int chunkNumber = chunkCounter++;
                    List<DataEntry> chunkToSort = new ArrayList<>(currentChunk);
                    
                    futures.add(executor.submit(() -> {
                        try {
                            return sortAndSaveChunk(chunkToSort, chunkNumber);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to process chunk " + chunkNumber, e);
                        }
                    }));
                    
                    currentChunk = new ArrayList<>(CHUNK_SIZE);
                }
            }

            // Process remaining records in the last chunk
            if (!currentChunk.isEmpty()) {
                final int finalChunkNumber = chunkCounter;
                List<DataEntry> finalChunk = new ArrayList<>(currentChunk);
                
                futures.add(executor.submit(() -> {
                    try {
                        return sortAndSaveChunk(finalChunk, finalChunkNumber);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process final chunk", e);
                    }
                }));
            }

            // Collect all chunk files
            List<File> chunkFiles = new ArrayList<>();
            for (Future<File> future : futures) {
                try {
                    chunkFiles.add(future.get());
                } catch (ExecutionException e) {
                    throw new Exception("Chunk processing failed: " + e.getCause().getMessage());
                }
            }
            return chunkFiles;
        } finally {
            executor.shutdown();
        }
    }

    private static File sortAndSaveChunk(List<DataEntry> chunk, int chunkNumber) 
        throws IOException {
        Collections.sort(chunk);
        File chunkFile = new File(TEMP_DIR + "/chunk_" + chunkNumber + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile))) {
            for (DataEntry entry : chunk) {
                writer.write(entry.toString());
                writer.newLine();
            }
        }
        return chunkFile;
    }

    private static void mergeSortedChunks(List<File> chunks, String outputFile) 
        throws IOException {
        PriorityQueue<ChunkReader> queue = new PriorityQueue<>();
        new File(OUTPUT_DIR).mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Initialize all chunk readers
            for (File chunk : chunks) {
                ChunkReader reader = new ChunkReader(chunk);
                if (reader.next() != null) {
                    queue.add(reader);
                }
            }

            // Merge all chunks
            while (!queue.isEmpty()) {
                ChunkReader reader = queue.poll();
                writer.write(reader.current.toString());
                writer.newLine();

                if (reader.next() != null) {
                    queue.add(reader);
                } else {
                    reader.close();
                }
            }
        }
    }

    private static class ChunkReader implements Comparable<ChunkReader> {
        private final BufferedReader reader;
        private DataEntry current;

        ChunkReader(File file) throws IOException {
            this.reader = new BufferedReader(new FileReader(file));
        }

        DataEntry next() throws IOException {
            String line = reader.readLine();
            if (line == null) return null;
            String[] parts = line.split(",", 2);
            current = new DataEntry(Integer.parseInt(parts[0]), parts[1]);
            return current;
        }

        void close() throws IOException {
            reader.close();
        }

        @Override
        public int compareTo(ChunkReader other) {
            return current.compareTo(other.current);
        }
    }
}
