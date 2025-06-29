import time
import random
import os
import sys
import csv

def binary_search(array, target):
    left = 0
    right = len(array) - 1
    while left <= right:
        mid = (left + right) // 2
        if array[mid] == target:
            return mid
        elif array[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return -1

def process_chunk(data_chunk):
    timings = {"best": 0, "average": 0, "worst": 0}
    n = len(data_chunk)

    if n == 0:
        return timings

    # Best Case: Middle element
    best_case = data_chunk[n // 2]
    start = time.perf_counter_ns()
    for _ in range(n):
        binary_search(data_chunk, best_case)
    end = time.perf_counter_ns()
    timings["best"] = (end - start) / 1_000_000.0

    # Average Case: Random targets from chunk
    rand = random.Random()
    start = time.perf_counter_ns()
    for _ in range(n):
        target = data_chunk[rand.randint(0, n - 1)]
        binary_search(data_chunk, target)
    end = time.perf_counter_ns()
    timings["average"] = (end - start) / 1_000_000.0

    # Worst Case: Not found element
    worst_case = float('inf')
    start = time.perf_counter_ns()
    for _ in range(n):
        binary_search(data_chunk, worst_case)
    end = time.perf_counter_ns()
    timings["worst"] = (end - start) / 1_000_000.0

    return timings

def main():
    # Paths
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    input_dir = os.path.join(base_dir, "outputs")
    output_dir = os.path.join(base_dir, "outputs")
    os.makedirs(output_dir, exist_ok=True)

    filename = input("Enter file name: ")
    input_filepath = os.path.join(input_dir, filename)

    chunk_size = 5_000_000
    total_entries = 0
    total_times = {"best": 0, "average": 0, "worst": 0}

    try:
        with open(input_filepath, 'r', newline='') as file:
            reader = csv.reader(file)
            data_chunk = []

            for row in reader:
                if row and row[0].strip():
                    try:
                        num = int(row[0].strip().replace('"', ''))
                        data_chunk.append(num)
                        total_entries += 1
                    except ValueError:
                        print(f"Warning: Could not convert '{row[0]}' to integer.", file=sys.stderr)

                # Process full chunk
                if len(data_chunk) >= chunk_size:
                    times = process_chunk(data_chunk)
                    for key in total_times:
                        total_times[key] += times[key]
                    data_chunk = []

            # Final partial chunk
            if data_chunk:
                times = process_chunk(data_chunk)
                for key in total_times:
                    total_times[key] += times[key]

    except FileNotFoundError:
        print("File not found.")
        return

    # Write output
    output_filename = f"binary_search_{total_entries}.txt"
    output_filepath = os.path.join(output_dir, output_filename)

    try:
        with open(output_filepath, "w") as writer:
            writer.write(f"Best case total time: {total_times['best']: .3f} ms\n")
            writer.write(f"Average case total time: {total_times['average']: .3f} ms\n")
            writer.write(f"Worst case total time: {total_times['worst']: .3f} ms\n")
    except IOError:
        print("Error writing output file.")

    print(f"Processed {total_entries} entries in chunks.")
    print(f"File saved to {output_filepath}")

if __name__ == "__main__":
    main()
