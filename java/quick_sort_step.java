import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class quick_sort_step {
    private static int step_count = 0;

    private static class data_entry {
        final int number;
        final String label;

        data_entry(int number, String label) {
            this.number = number;
            this.label = label;
        }

        int compare_to(data_entry other) {
            return this.number - other.number;
        }

        public String toString() {
            return number + "/" + label;
        }
    }

    private static List<data_entry> read_data_from_file(String path, int start_line, int end_line) throws IOException {
        List<data_entry> results = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("../datasets/" + path));
        String current_line;
        int line_no = 0;

        try {
            while ((current_line = reader.readLine()) != null) {
                line_no++;
                if (line_no < start_line) continue;
                if (line_no > end_line) break;

                int split_at = current_line.indexOf(',');
                if (split_at > 0) {
                    try {
                        int num = Integer.parseInt(current_line.substring(0, split_at).trim());
                        String text = current_line.substring(split_at + 1).trim();
                        results.add(new data_entry(num, text));
                    } catch (NumberFormatException e) {
                        System.err.println("Malformed entry at line " + line_no);
                    }
                }
            }
        } finally {
            reader.close();
        }

        return results;
    }

    private static void log_step(List<data_entry> list, int pivot_idx, BufferedWriter out,
                              String note, int from, int to) throws IOException {
        step_count++;

        out.write("Step " + step_count + ": " + note);
        if (from >= 0 && to >= 0) {
            out.write(" (Between indices " + from + " and " + to + ")");
        }
        out.write("\n");

        out.write("Array: [");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) out.write(", ");
            if (i == pivot_idx) out.write("*");
            out.write(list.get(i).toString());
            if (i == pivot_idx) out.write("*");
        }
        out.write("]\n");

        if (pivot_idx != -1) {
            out.write("Pivot = " + list.get(pivot_idx).number + "\n");
        }

        out.write("\n");
    }

    private static void quick_sort(List<data_entry> list, int start, int end, BufferedWriter out) throws IOException {
        if (start < end) {
            int pivot_position = partition(list, start, end, out);
            quick_sort(list, start, pivot_position - 1, out);
            quick_sort(list, pivot_position + 1, end, out);
        }
    }

    private static int partition(List<data_entry> list, int low, int high, BufferedWriter out) throws IOException {
        data_entry pivot = list.get(high);
        log_step(list, high, out, "Picking pivot", low, high);

        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (list.get(j).compare_to(pivot) <= 0) {
                i++;

                data_entry tmp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, tmp);

                if (i != j) {
                    log_step(list, high, out, "Swapped elements", low, high);
                }
            }
        }

        data_entry temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);

        log_step(list, i + 1, out, "Partition complete", low, high);

        return i + 1;
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java quick_sort_step <input_file> <start_row> <end_row>");
            return;
        }

        BufferedWriter log_writer = null;

        try {
            String input_path = args[0];
            int from = Integer.parseInt(args[1]);
            int to = Integer.parseInt(args[2]);

            List<data_entry> dataset = read_data_from_file(input_path, from, to);
            
            // Ensure outputs directory exists
            new File("../outputs").mkdirs();
            String output_path = "../outputs/quick_sort_step_" + from + "_" + to + ".txt";

            log_writer = new BufferedWriter(new FileWriter(output_path));

            log_writer.write("=== QUICK SORT STEP-BY-STEP ===\n");
            log_writer.write("Input File: ../datasets/" + input_path + "\n");
            log_writer.write("Rows Processed: " + from + " to " + to + "\n");
            log_writer.write("Total Records: " + dataset.size() + "\n");
            log_writer.write("Pivot Strategy: Use last element as pivot\n\n");

            log_step(dataset, -1, log_writer, "Initial state", -1, -1);

            long start_time = System.nanoTime();

            if (dataset.size() > 1) {
                log_writer.write("=== BEGIN SORT ===\n\n");
                quick_sort(dataset, 0, dataset.size() - 1, log_writer);
                log_writer.write("=== SORT COMPLETE ===\n\n");
            }

            long end_time = System.nanoTime();

            log_step(dataset, -1, log_writer, "Sorted result", -1, -1);
            log_writer.write("Steps Taken: " + step_count + "\n");
            log_writer.write("Elapsed Time: " + ((end_time - start_time) / 1_000_000.0) + " ms\n");

            System.out.println("Done! Sort log saved to ../outputs/" + output_path);

        } catch (Exception e) {
            System.err.println("Something went wrong: " + e.getMessage());
        } finally {
            if (log_writer != null) {
                try { log_writer.close(); } catch (IOException ignore) {}
            }
        }
    }
}