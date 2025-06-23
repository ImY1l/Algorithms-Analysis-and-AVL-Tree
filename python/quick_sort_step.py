import csv

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


def quick_sort_step(data, low, high, steps):
    if low < high:
        pi = partition(data, low, high)
        steps.append(f"pi={pi} {[f'{item[0]}/{item[1]}' for item in data]}")
        
        quick_sort_step(data, low, pi - 1, steps)
        quick_sort_step(data, pi + 1, high, steps)

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
    data = read_csv(filename)

    steps = []
    steps.append(f"{[f'{item[0]}/{item[1]}' for item in data]}")

    quick_sort_step(data,0,len(data)-1,steps)

# create text file
output_filename = "quick_sort_step_" + filename.replace(".csv", ".txt")
with open(output_filename, "w") as f:
    for step in steps:
        f.write(step + "\n")
