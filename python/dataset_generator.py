import random
import string
import csv
import sys
import os

# Constants
STRING_LEN = 5
ALPHABET = "abcdefghijklmnopqrstuvwxyz"
MAX_ALLOWED_SIZE = 1_000_000_000  # Max integer limit

# Generate a random lowercase string of fixed length
def create_random_word():
    return ''.join(random.choices(ALPHABET, k=STRING_LEN))

# Generate unique random integers and write to CSV with random strings
def generate_dataset(count, output_file_path):
    if count <= 0 or count > MAX_ALLOWED_SIZE:
        raise ValueError(f"Size must be between 1 and {MAX_ALLOWED_SIZE}")

    print(f"Generating {count:,} unique integers...")

    # Generate unique random numbers
    generated_nums = set()
    while len(generated_nums) < count:
        next_num = random.randint(1, MAX_ALLOWED_SIZE)
        generated_nums.add(next_num)
        if len(generated_nums) % 1_000_000 == 0:
            print(f"Generated {len(generated_nums):,} entries...")

    # Shuffle the numbers
    num_list = list(generated_nums)
    random.shuffle(num_list)

    # Make sure the output directory exists
    os.makedirs(os.path.dirname(output_file_path), exist_ok=True)

    # Write to CSV
    with open(output_file_path, mode='w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        for value in num_list:
            writer.writerow([value, create_random_word()])

    print(f"Done! Created file with {count:,} entries at: {output_file_path}")

# Entry point
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python dataset_generator.py <entry_count>")
        sys.exit(1)

    try:
        how_many = int(sys.argv[1])
        path = f"../datasets/dataset_{how_many}.csv"
        generate_dataset(how_many, path)
    except ValueError:
        print("Oops, that didn't look like a valid number.")
    except IOError as e:
        print(f"Something went wrong during file writing: {e}")
