import os
import sys
import time

# Configuration
CHUNK_SIZE = 5_000_000  # Safe for 16GB RAM, improves speed
TEMP_DIR = "temp_chunks"
OUTPUTS_FOLDER = "../outputs"
DATA_FOLDER = "../datasets"

class Record:
    def __init__(self, num, line):
        self.num = num
        self.line = line

    def __lt__(self, other):
        return self.num < other.num

# Step 1: Split the input file into smaller sorted chunks
def split_and_sort(input_file_path):
    os.makedirs(TEMP_DIR, exist_ok=True)
    chunk_files = []
    records = []
    index = 0

    with open(input_file_path, 'r') as src:
        for ln_num, ln in enumerate(src, 1):
            parts = ln.strip().split(',', 1)
            if len(parts) == 2:
                try:
                    num = int(parts[0])
                    rest = parts[1]
                    records.append(Record(num, rest))
                except ValueError:
                    continue

            if len(records) >= CHUNK_SIZE:
                dump_chunk(records, index, chunk_files)
                records.clear()
                index += 1

        if records:
            dump_chunk(records, index, chunk_files)

    return chunk_files

# Sort and save each chunk to disk using iterative quicksort
def dump_chunk(items, idx, out_files):
    quicksort_iterative(items)
    path = os.path.join(TEMP_DIR, f"chunk_{idx}.csv")
    with open(path, 'w', buffering=1_048_576) as f:
        for entry in items:
            f.write(f"{entry.num},{entry.line}\n")
    out_files.append(path)

# Iterative quicksort avoids recursion limit issues
def quicksort_iterative(arr):
    stack = [(0, len(arr) - 1)]
    while stack:
        lo, hi = stack.pop()
        if lo < hi:
            pivot_idx = partition(arr, lo, hi)
            stack.append((lo, pivot_idx - 1))
            stack.append((pivot_idx + 1, hi))

# Partitioning using last element as pivot 
def partition(arr, low, high):
    pivot_val = arr[high].num
    i = low - 1
    for j in range(low, high):
        if arr[j].num < pivot_val:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i + 1], arr[high] = arr[high], arr[i + 1]
    return i + 1

# Merge sorted chunks manually without using heapq
def combine_chunks(sorted_files, final_output_path):
    open_files = [open(path, 'r') for path in sorted_files]
    buffers = []

    for f in open_files:
        line = f.readline()
        if line:
            parts = line.strip().split(',', 1)
            if len(parts) == 2:
                buffers.append((int(parts[0]), parts[1], f))
            else:
                buffers.append((float('inf'), '', f))
        else:
            buffers.append((float('inf'), '', f))

    with open(final_output_path, 'w', buffering=1_048_576) as out:
        while True:
            min_index = -1
            min_val = float('inf')

            for i, (val, _, _) in enumerate(buffers):
                if val < min_val:
                    min_val = val
                    min_index = i

            if min_index == -1:
                break  # All files exhausted

            num, txt, f = buffers[min_index]
            out.write(f"{num},{txt}\n")

            nxt = f.readline()
            if nxt:
                parts = nxt.strip().split(',', 1)
                if len(parts) == 2:
                    buffers[min_index] = (int(parts[0]), parts[1], f)
                else:
                    buffers[min_index] = (float('inf'), '', f)
            else:
                buffers[min_index] = (float('inf'), '', f)

    for f in open_files:
        f.close()
    for f in sorted_files:
        os.remove(f)
    os.rmdir(TEMP_DIR)

# Generate output file path from input filename
def make_output_path(fname):
    os.makedirs(OUTPUTS_FOLDER, exist_ok=True)
    label = fname.replace("dataset_", "").replace(".csv", "")
    return os.path.join(OUTPUTS_FOLDER, f"quick_sort_{label}.csv")

# Main entry point of the script
def main():
    if len(sys.argv) < 2:
        sys.exit(1)

    file_name = sys.argv[1]
    full_path = os.path.join(DATA_FOLDER, file_name)

    if not os.path.exists(full_path):
        sys.exit(1)

    start = time.time()
    pieces = split_and_sort(full_path)
    output_file = make_output_path(file_name)
    combine_chunks(pieces, output_file)
    end = time.time()

    print(int((end - start) * 1000))  # Total sort runtime in milliseconds

if __name__ == "__main__":
    main()
