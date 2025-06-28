import os
import sys
import heapq
import time

CHUNK_SIZE = 5000000  # 5 million rows per chunk; might need tuning
TEMP_DIR = "temp_chunks"
OUTPUTS_FOLDER = "../outputs"
DATA_FOLDER = "../datasets"

class Record:
    def __init__(self, num, line):
        self.num = num
        self.line = line

    def __lt__(self, other):
        return self.num < other.num

def split_and_sort(input_file_path):
    os.makedirs(TEMP_DIR, exist_ok=True)
    chunk_files = []
    records = []
    index = 0

    with open(input_file_path, 'r') as src:
        for ln in src:
            parts = ln.strip().split(',', 1)
            if len(parts) == 2:
                try:
                    num = int(parts[0])
                    rest = parts[1]
                    records.append(Record(num, rest))
                except ValueError:
                    # probably a malformed line; just skip it
                    continue

            if len(records) >= CHUNK_SIZE:
                dump_chunk(records, index, chunk_files)
                records.clear()
                index += 1

        # Final leftover records
        if records:
            dump_chunk(records, index, chunk_files)

    return chunk_files

def dump_chunk(items, idx, out_files):
    quicksort_iterative(items)
    path = os.path.join(TEMP_DIR, f"chunk_{idx}.csv")
    with open(path, 'w') as f:
        for entry in items:
            f.write(f"{entry.num},{entry.line}\n")
    out_files.append(path)

def quicksort_iterative(arr):
    # Classic iterative quicksort – avoids recursion
    stack = [(0, len(arr) - 1)]

    while stack:
        lo, hi = stack.pop()
        if lo < hi:
            pivot_idx = partition(arr, lo, hi)
            # Push left and right ranges to stack
            stack.append((lo, pivot_idx - 1))
            stack.append((pivot_idx + 1, hi))

def partition(arr, low, high):
    # Standard Lomuto partitioning
    pivot_val = arr[high].num
    i = low - 1
    for j in range(low, high):
        if arr[j].num < pivot_val:
            i += 1
            arr[i], arr[j] = arr[j], arr[i]
    arr[i+1], arr[high] = arr[high], arr[i+1]
    return i + 1

def combine_chunks(sorted_files, final_output_path):
    # We're using a min-heap to do a N-way merge
    heap = []
    open_files = []

    for path in sorted_files:
        fh = open(path, 'r')
        open_files.append(fh)
        line = fh.readline()
        if line:
            parts = line.strip().split(',', 1)
            if len(parts) == 2:
                heapq.heappush(heap, (int(parts[0]), parts[1], fh))

    with open(final_output_path, 'w') as out:
        while heap:
            num, txt, handle = heapq.heappop(heap)
            out.write(f"{num},{txt}\n")
            nxt = handle.readline()
            if nxt:
                next_parts = nxt.strip().split(',', 1)
                if len(next_parts) == 2:
                    heapq.heappush(heap, (int(next_parts[0]), next_parts[1], handle))

    # Cleanup
    for f in open_files:
        f.close()
    for f in sorted_files:
        os.remove(f)
    os.rmdir(TEMP_DIR)

def make_output_path(fname):
    os.makedirs(OUTPUTS_FOLDER, exist_ok=True)
    label = fname.replace("dataset_", "").replace(".csv", "")
    return os.path.join(OUTPUTS_FOLDER, f"quick_sort_{label}.csv")

def main():
    if len(sys.argv) < 2:
        print("Missing filename input.")
        sys.exit(1)

    file_name = sys.argv[1]
    full_path = os.path.join(DATA_FOLDER, file_name)

    if not os.path.exists(full_path):
        print(f"File '{file_name}' not found.")
        sys.exit(1)

    # Step 1: Chunk, sort, save
    pieces = split_and_sort(full_path)

    # Step 2: Merge the sorted pieces and time it
    t0 = time.time()
    output_file = make_output_path(file_name)
    combine_chunks(pieces, output_file)
    t1 = time.time()

    print(int((t1 - t0) * 1000))  # show elapsed time in ms

if __name__ == "__main__":
    main()
