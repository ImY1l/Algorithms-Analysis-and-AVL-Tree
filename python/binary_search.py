import time
import random
import os
import sys
import csv

# Binary search implementation
def binary_search(array, target):
    left =0
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
    # Read file name from user
    script_dir = os.path.dirname(os.path.abspath(__file__))
    
    filename = input("Enter file name: ")
    input_filepath = os.path.join(script_dir, filename)

    data_list = []

    try:
        with open(input_filepath, 'r', newline='') as file:
            reader = csv.reader(file)
            for row in reader:
                if row and row[0].strip():
                    try:
                        temp_num = int(row[0].strip().replace('"', ''))
                        data_list.append(temp_num)
                    except ValueError:
                        print(f"Warning: Could not convert '{row[0]}' to integer. Skipping row.", file=sys.stderr)
                    except IndexError:
                        print(f"Warning: Row has missing data. Skipping row.", file=sys.stderr)

    except FileNotFoundError:
        print("File not found.")
        return

    n = len(data_list)
    data = data_list

    # Best case scenario calculation
    best_case = data[n // 2]
    start = time.perf_counter_ns()
    
    for _ in range(n):
        binary_search(data, best_case)

    end = time.perf_counter_ns()
    best_case_time = (end - start) / 1_000.0

    # Average case scenario calculation
    rand = random.Random()
    start = time.perf_counter_ns()
    for _ in range(n):
        random_index = rand.randint(0, n - 1)
        binary_search(data, data[random_index])
        
    end = time.perf_counter_ns()
    average_case_time = (end - start) / 1_000.0

    # Worst case scenario calculation
    worst_case = float('inf')
    start = time.perf_counter_ns()
    for _ in range(n):
        binary_search(data, worst_case)

    end = time.perf_counter_ns()
    worst_case_time = (end - start) / 1_000.0

    # Output file to binary_search_n.txt
    output_file = "binary_search_n.txt"

    try:
        with open(output_file, "w") as writer:
            writer.write(f"Best case time: {best_case_time: .3f} microseconds. \n")
            writer.write(f"Average case time: {average_case_time: .3f} microseconds. \n")
            writer.write(f"Worst case time: {worst_case_time: .3f} microseconds. \n")

    except IOError:
        print("Error writing output file.")

    print(f"File saved to {output_file}")

if __name__ == "__main__":
    main()