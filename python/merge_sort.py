import os
import time
from typing import List, Tuple

class DataEntry:
    def __init__(self, number: int, text: str):
        self.number = number
        self.text = text
    
    def __str__(self):
        return f"{self.number},{self.text}"

def read_data_entries(filename: str) -> List[DataEntry]:
    entries = []
    with open(f"../datasets/{filename}", 'r') as file:
        for line in file:
            parts = line.strip().split(',')
            if len(parts) == 2:
                entries.append(DataEntry(int(parts[0]), parts[1]))
    return entries

def write_output(entries: List[DataEntry], filename: str):
    os.makedirs("../outputs", exist_ok=True)
    with open(filename, 'w') as file:
        for entry in entries:
            file.write(f"{entry}\n")

def merge_sort(entries: List[DataEntry]):
    if len(entries) > 1:
        mid = len(entries) // 2
        left = entries[:mid]
        right = entries[mid:]

        merge_sort(left)
        merge_sort(right)

        i = j = k = 0

        while i < len(left) and j < len(right):
            if left[i].number <= right[j].number:
                entries[k] = left[i]
                i += 1
            else:
                entries[k] = right[j]
                j += 1
            k += 1

        while i < len(left):
            entries[k] = left[i]
            i += 1
            k += 1

        while j < len(right):
            entries[k] = right[j]
            j += 1
            k += 1

def main():
    import sys
    if len(sys.argv) != 2:
        print("Usage: python merge_sort.py <input_file>")
        return

    start_time = time.time()

    try:
        entries = read_data_entries(sys.argv[1])
        merge_sort(entries)
        
        output_file = f"../outputs/merge_sort_{len(entries)}.csv"
        write_output(entries, output_file)
        
        print(f"Running time: {(time.time() - start_time) * 1000:.2f} ms")
    except Exception as e:
        print(f"Error processing file: {e}")

if __name__ == "__main__":
    main()
