# This script loads a CSV file, sorts its contents using QuickSort, and saves the results.
# It tracks runtime and exits silently if the input is missing or invalid.

import sys
import time
import os
from pathlib import Path

class DataItem:
    """Represents one line of data from the CSV file with a number and associated label."""
    def __init__(self, number, text):
        self.number = number
        self.text = text


def read_data(file_path):
    """Reads data from a given CSV path and returns a list of DataItem objects."""
    records = []
    try:
        with open(file_path, 'r') as file:
            for line in file:
                parts = line.strip().split(',', 1)  # Split only at the first comma
                if len(parts) == 2:
                    try:
                        number = int(parts[0])
                        label = parts[1]
                        records.append(DataItem(number, label))
                    except ValueError:
                        continue  # Ignore lines that don't start with a number
        return records if records else None
    except IOError:
        return None


def write_sorted_data(data, original_filename, output_dir="../outputs"):
    """Saves the sorted data to a CSV file named according to the dataset size."""
    try:
        os.makedirs(output_dir, exist_ok=True)
        size = original_filename.replace("dataset_sample_", "").replace(".csv", "")
        output_path = os.path.join(output_dir, f"quick_sort_{size}.csv")
        with open(output_path, 'w') as file:
            for item in data:
                file.write(f"{item.number},{item.text}\n")
        return True
    except IOError:
        return False


def partition(data, low, high):
    """Reorders elements around a pivot so that smaller ones go before it."""
    pivot_value = data[high].number  # Use last element as pivot
    i = low - 1
    for j in range(low, high):
        if data[j].number < pivot_value:
            i += 1
            data[i], data[j] = data[j], data[i]
    data[i + 1], data[high] = data[high], data[i + 1]
    return i + 1


def quick_sort(data, low, high):
    """Implements QuickSort recursively with tail-call optimization."""
    while low < high:
        pivot_idx = partition(data, low, high)
        # Recur on the smaller partition first to reduce stack depth
        if pivot_idx - low < high - pivot_idx:
            quick_sort(data, low, pivot_idx - 1)
            low = pivot_idx + 1
        else:
            quick_sort(data, pivot_idx + 1, high)
            high = pivot_idx - 1


def main():
    if len(sys.argv) < 2:
        sys.exit(1)  # No input provided, exit without error message

    input_filename = sys.argv[1]
    input_path = os.path.join("../datasets", input_filename)

    # Check that input file exists in the expected location
    if not os.path.exists(input_path):
        sys.exit(1)

    # Load data from file
    data = read_data(input_path)
    if not data:
        sys.exit(1)

    # Measure sorting time
    start_time = time.time()
    quick_sort(data, 0, len(data) - 1)
    elapsed_ms = int((time.time() - start_time) * 1000)

    # Save sorted results
    if not write_sorted_data(data, input_filename):
        sys.exit(1)

    # Display runtime only
    print(elapsed_ms)


if __name__ == "__main__":
    main()
