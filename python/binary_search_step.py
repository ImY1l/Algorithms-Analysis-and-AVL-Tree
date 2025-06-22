import csv
import sys

def binary_search_step(data,x):
    arr = []
    high = len(data) - 1
    low = 0

    while low <= high:
        mid = low + (high - low) //2
        num, word = data[mid]
        arr.append(f"{mid}: {num}/{word}")

        if num == x:
            return arr # return list of steps
        elif num < x:
            low = mid + 1
        else:
            high = mid - 1
    arr.append("-1") # not found
    return arr

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
                    continue  # skip invalid rows
    return data

if __name__ == "__main__":
    filename = "dataset_sample_1000.csv"
    target= 613479842  # change target value here

    data = read_csv(filename)
    data.sort(key=lambda x: x[0])  # ensure the data is sorted by number

    steps = binary_search_step(data, target)
    
output_filename = f"binary_search_step_{target}.txt"
with open(output_filename, "w") as f:
    for step in steps:
        f.write(step + "\n")
