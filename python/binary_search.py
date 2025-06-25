import time
import random

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
    filename = input("Enter file name: ")

    data_list = []

    try:
        with open(filename, 'r') as reader:
            for line in reader:
                parts = line.strip().split(",")
                if parts[0].strip():
                    temp_num = int(parts[0].strip().replace('"', ''))
                    data_list.append(temp_num)

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

if __name__ == "__main__":
    main()