import os
import time
import heapq
from typing import List, Iterator
from tempfile import TemporaryDirectory

CHUNK_SIZE = 10_000_000  # Records per chunk

class DataEntry:
    def __init__(self, number: int, text: str):
        self.number = number
        self.text = text
    
    def __lt__(self, other: 'DataEntry') -> bool:
        return self.number < other.number
    
    def __str__(self):
        return f"{self.number},{self.text}"

def read_in_chunks(filename: str) -> Iterator[List[DataEntry]]:
    """Read file in chunks to avoid loading everything in memory"""
    chunk = []
    with open(filename, 'r') as file:
        for i, line in enumerate(file):
            parts = line.strip().split(',', 1)
            if len(parts) == 2:
                chunk.append(DataEntry(int(parts[0]), parts[1]))
                if len(chunk) >= CHUNK_SIZE:
                    yield chunk
                    chunk = []
            if i % 1_000_000 == 0:
                print(f"Read {i:,} records...", end='\r')
        if chunk:
            yield chunk

def sort_and_save_chunk(chunk: List[DataEntry], chunk_id: int, temp_dir: str) -> str:
    """Sort a chunk and save to temporary file"""
    print(f"Sorting chunk {chunk_id} ({len(chunk):,} records)...", end='\r')
    chunk.sort()
    temp_file = os.path.join(temp_dir, f"chunk_{chunk_id}.tmp")
    with open(temp_file, 'w') as f:
        for entry in chunk:
            f.write(f"{entry}\n")
    return temp_file

def merge_sorted_files(file_paths: List[str], output_file: str) -> int:
    """Merge multiple sorted files into one using min-heap"""
    file_handles = []
    heap = []
    total_records = 0
    
    print("\nMerging sorted chunks...")
    for i, file_path in enumerate(file_paths):
        f = open(file_path, 'r')
        file_handles.append(f)
        line = f.readline()
        if line:
            parts = line.strip().split(',', 1)
            heapq.heappush(heap, (DataEntry(int(parts[0]), parts[1]), i))
    
    with open(output_file, 'w') as out:
        while heap:
            entry, file_idx = heapq.heappop(heap)
            out.write(f"{entry}\n")
            total_records += 1
            if total_records % 1_000_000 == 0:
                print(f"Merged {total_records:,} records...", end='\r')
            next_line = file_handles[file_idx].readline()
            if next_line:
                parts = next_line.strip().split(',', 1)
                heapq.heappush(heap, (DataEntry(int(parts[0]), parts[1]), file_idx))
    
    for f in file_handles:
        f.close()
    return total_records

def main():
    if len(sys.argv) != 2:
        print("Usage: python merge_sort.py <input_file>")
        return

    input_path = os.path.join("../datasets", sys.argv[1])
    output_dir = "../outputs"
    os.makedirs(output_dir, exist_ok=True)
    
    start_time = time.time() * 1000  # Convert to milliseconds immediately
    print(f"Starting to process {input_path}...")

    try:
        with TemporaryDirectory() as temp_dir:
            # Phase 1: Process chunks
            chunk_files = []
            chunk_count = 0
            
            for chunk in read_in_chunks(input_path):
                chunk_count += 1
                temp_file = sort_and_save_chunk(chunk, chunk_count, temp_dir)
                chunk_files.append(temp_file)
                print(f"Processed {chunk_count} chunks...", end='\r')
            
            # Phase 2: Merge
            temp_output = os.path.join(output_dir, "temp_sorted_output.csv")
            total_records = merge_sorted_files(chunk_files, temp_output)
            
            # Final output
            final_output = os.path.join(output_dir, f"merge_sort_{total_records}.csv")
            if os.path.exists(final_output):
                os.remove(final_output)
            os.rename(temp_output, final_output)
            
            runtime_ms = (time.time() * 1000) - start_time  # Calculate total ms
            print(f"\n\nProcessing completed in {runtime_ms:,.0f} ms")
    
    except Exception as e:
        print(f"\nError: {str(e)}")
        raise

if __name__ == "__main__":
    import sys
    main()
