import os
import sys
import csv
import shutil
import time

CHUNK_SIZE = 5_000_000
TEMP_DIR = "../temp"
OUTPUT_DIR = "../outputs"

class DataEntry:
    def __init__(self, number, text):
        self.number = number
        self.text = text

    def __lt__(self, other):
        return self.number < other.number

    def __str__(self):
        return f"{self.number},{self.text}"

# Iterative merge sort for a list of DataEntry
def merge_sort_iterative(data):
    if not data:
        return []

    width = 1
    n = len(data)
    result = data[:]
    
    while width < n:
        for i in range(0, n, 2 * width):
            left = result[i:i + width]
            right = result[i + width:i + 2 * width]
            result[i:i + 2 * width] = merge(left, right)
        width *= 2
    return result

def merge(left, right):
    merged = []
    i = j = 0
    while i < len(left) and j < len(right):
        if left[i].number <= right[j].number:
            merged.append(left[i])
            i += 1
        else:
            merged.append(right[j])
            j += 1
    merged.extend(left[i:])
    merged.extend(right[j:])
    return merged

def main(input_file):
    if not input_file:
        print("Usage: python merge_sort.py <input_file>")
        return

    start_time = time.time()
    try:
        sorted_chunks = process_in_chunks(input_file)
        file_name = input_file.replace("dataset_", "")
        output_file = os.path.join(OUTPUT_DIR, f"merge_sort_{file_name}")
        merge_sorted_chunks(sorted_chunks, output_file)

        for chunk_file in sorted_chunks:
            os.remove(chunk_file)
        shutil.rmtree(TEMP_DIR)

        elapsed = time.time() - start_time
        print("Execution time (ms):", int(elapsed * 1000))

    except Exception as e:
        print(f"Error during sorting: {e}")
        raise

def process_in_chunks(input_file):
    os.makedirs(TEMP_DIR, exist_ok=True)
    chunk_files = []
    chunk_counter = 0
    current_chunk = []

    with open(f"../datasets/{input_file}", newline='', encoding="utf-8") as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            if len(row) != 2:
                continue
            number = int(row[0])
            text = row[1]
            current_chunk.append(DataEntry(number, text))

            if len(current_chunk) >= CHUNK_SIZE:
                chunk_file = sort_and_save_chunk(current_chunk, chunk_counter)
                chunk_files.append(chunk_file)
                current_chunk.clear()
                chunk_counter += 1

        if current_chunk:
            chunk_file = sort_and_save_chunk(current_chunk, chunk_counter)
            chunk_files.append(chunk_file)

    return chunk_files

def sort_and_save_chunk(chunk, chunk_number):
    sorted_chunk = merge_sort_iterative(chunk)
    file_path = os.path.join(TEMP_DIR, f"chunk_{chunk_number}.csv")
    with open(file_path, "w", newline='', encoding="utf-8") as file:
        writer = csv.writer(file)
        for entry in sorted_chunk:
            writer.writerow([entry.number, entry.text])
    return file_path

def merge_sorted_chunks(chunk_files, output_file):
    os.makedirs(os.path.dirname(output_file), exist_ok=True)
    readers = []
    entries = []

    for path in chunk_files:
        f = open(path, newline='', encoding="utf-8")
        reader = csv.reader(f)
        try:
            row = next(reader)
            if row:
                number = int(row[0])
                entries.append((number, row[1], reader, f))
        except StopIteration:
            f.close()

    with open(output_file, "w", newline='', encoding="utf-8") as out_file:
        writer = csv.writer(out_file)
        while entries:
            min_index = min(range(len(entries)), key=lambda i: entries[i][0])
            num, txt, reader, f = entries[min_index]
            writer.writerow([num, txt])
            try:
                row = next(reader)
                if row:
                    number = int(row[0])
                    entries[min_index] = (number, row[1], reader, f)
                else:
                    raise StopIteration
            except StopIteration:
                f.close()
                entries.pop(min_index)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python merge_sort.py <input_file>")
    else:
        main(sys.argv[1])
