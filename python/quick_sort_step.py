import csv
import os

def swap(arr, i, j):
    arr[i], arr[j] = arr[j], arr[i]

def partition(data, low, high):
    pivot = data[high][0]
    i = low - 1

    for j in range(low, high):
        if data[j][0] < pivot:
            i = i + 1
            swap(data, i, j)
    
    swap(data, i + 1, high)
    return i + 1

def quick_sort_step(data, low, high, steps, step_counter):
    if low < high:
        pi = partition(data, low, high)
        step_counter[0] += 1
        steps.append(f"Step {step_counter[0]} - pi={pi}: {[f'{item[0]}/{item[1]}' for item in data]}")
        
        quick_sort_step(data, low, pi - 1, steps, step_counter)
        quick_sort_step(data, pi + 1, high, steps, step_counter)

def read_csv(filename):
    data = []
    with open(filename, newline='') as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            if len(row) >= 2:
                try:
                    num = int(row[0])
                    word = row[1]
                    data.append((num, word))
                except ValueError:
                    continue
    return data

if __name__ == "__main__":
    filename = os.path.join("datasets", "dataset_1000.csv") # change dataset here
    data = read_csv(filename)

    steps = []
    step_counter = [0]  # Use list for mutable integer
    steps.append(f"Initial: {[f'{item[0]}/{item[1]}' for item in data]}")

    quick_sort_step(data, 0, len(data) - 1, steps, step_counter)

    output_directory = "outputs"
    os.makedirs(output_directory, exist_ok=True)
    output_filename = os.path.join(output_directory, f"quick_sort_step_{os.path.basename(filename).replace('.csv', '.txt')}")
    
    with open(output_filename, "w") as f:
        for step in steps:
            f.write(step + "\n")
