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

def main():
    # Set paths
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    input_dir = os.path.join(base_dir, "outputs")
    output_dir = os.path.join(base_dir, "outputs")
    os.makedirs(output_dir, exist_ok=True)

    filename = input("Enter file name: ")
    input_filepath = os.path.join(input_dir, filename)

    data = []

    try:
        with open(input_filepath, 'r', newline='') as file:
            reader = csv.reader(file)
            for row in reader:
                if row and row[0].strip():
                    try:
                        num = int(row[0].strip().replace('"', ''))
                        data.append(num)
                    except ValueError:
                        print(f"Warning: Could not convert '{row[0]}' to integer.", file=sys.stderr)

    except FileNotFoundError:
        print("File not found.")
        return

    n = len(data)
    if n == 0:
        print("No valid data found.")
        return

    # Best Case
    best_case = data[n // 2]
    start = time.perf_counter_ns()
    for _ in range(n):
        binary_search(data, best_case)
    end = time.perf_counter_ns()
    best_time = (end - start) / 1_000_000.0

    # Average Case
    rand = random.Random()
    start = time.perf_counter_ns()
    for _ in range(n):
        target = data[rand.randint(0, n - 1)]
        binary_search(data, target)
    end = time.perf_counter_ns()
    avg_time = (end - start) / 1_000_000.0

    # Worst Case
    worst_case = float('inf')
    start = time.perf_counter_ns()
    for _ in range(n):
        binary_search(data, worst_case)
    end = time.perf_counter_ns()
    worst_time = (end - start) / 1_000_000.0

    # Write output
    output_filename = f"binary_search_{n}.txt"
    output_filepath = os.path.join(output_dir, output_filename)

    try:
        with open(output_filepath, "w") as writer:
            writer.write(f"Best case time: {best_time: .3f} ms\n")
            writer.write(f"Average case time: {avg_time: .3f} ms\n")
            writer.write(f"Worst case time: {worst_time: .3f} ms\n")
    except IOError:
        print("Error writing output file.")

    print(f"Processed {n} entries in full dataset.")
    print(f"File saved to {output_filepath}")

if __name__ == "__main__":
    main()
