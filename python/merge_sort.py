import os
import sys
import heapq
import csv
import shutil
import time
from concurrent.futures import ThreadPoolExecutor

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
        elapsed = time.time() - start_time
        print(f"Processing completed in {int(elapsed * 1000)} ms")

    except Exception as e:
        print(f"Error during sorting: {e}")
        raise

def process_in_chunks(input_file):
    os.makedirs(TEMP_DIR, exist_ok=True)
    futures = []
    chunk_files = []
    chunk_counter = 0
    current_chunk = []

    executor = ThreadPoolExecutor()

    with open(f"../datasets/{input_file}", newline='', encoding="utf-8") as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            if len(row) != 2:
                continue
            number = int(row[0])
            text = row[1]
            current_chunk.append(DataEntry(number, text))

            if len(current_chunk) >= CHUNK_SIZE:
                chunk_copy = current_chunk[:]
                current_chunk.clear()
                future = executor.submit(sort_and_save_chunk, chunk_copy, chunk_counter)
                futures.append(future)
                chunk_counter += 1

        # Submit remaining chunk
        if current_chunk:
            future = executor.submit(sort_and_save_chunk, current_chunk, chunk_counter)
            futures.append(future)

    for future in futures:
        chunk_files.append(future.result())

    return chunk_files

def sort_and_save_chunk(chunk, chunk_number):
    chunk.sort()
    file_path = os.path.join(TEMP_DIR, f"chunk_{chunk_number}.csv")
    with open(file_path, "w", newline='', encoding="utf-8") as file:
        writer = csv.writer(file)
        for entry in chunk:
            writer.writerow([entry.number, entry.text])
    return file_path

def merge_sorted_chunks(chunk_files, output_file):
    os.makedirs(os.path.dirname(output_file), exist_ok=True)
    min_heap = []
    file_readers = []

    try:
        for file_path in chunk_files:
            f = open(file_path, newline='', encoding="utf-8")
            reader = csv.reader(f)
            file_readers.append((f, reader))
            try:
                row = next(reader)
                if row:
                    number = int(row[0])
                    heapq.heappush(min_heap, (DataEntry(number, row[1]), reader))
            except StopIteration:
                pass

        with open(output_file, "w", newline='', encoding="utf-8") as out_file:
            writer = csv.writer(out_file)

            while min_heap:
                smallest, reader = heapq.heappop(min_heap)
                writer.writerow([smallest.number, smallest.text])
                try:
                    row = next(reader)
                    if row:
                        number = int(row[0])
                        heapq.heappush(min_heap, (DataEntry(number, row[1]), reader))
                except StopIteration:
                    continue
    finally:
        for f, _ in file_readers:
            f.close()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python merge_sort.py <input_file>")
    else:
        main(sys.argv[1])
