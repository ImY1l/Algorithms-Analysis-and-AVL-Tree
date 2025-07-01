import csv
import os
import sys

def merge_sort_step(data, output_file):
    if len(data) <= 1:
        return data

    mid = len(data) // 2
    left = data[:mid]
    right = data[mid:]

    left = merge_sort_step(left, output_file)
    right = merge_sort_step(right, output_file)

    merged = merge(left, right, output_file)

    return merged

def merge(left, right, output_file):
    merged = []
    left_index = 0
    right_index = 0

    while left_index < len(left) and right_index < len(right):
        if left[left_index][0] <= right[right_index][0]:
            merged.append(left[left_index])
            left_index += 1
        else:
            merged.append(right[right_index])
            right_index += 1
    
    merged.extend(left[left_index:])
    merged.extend(right[right_index:])

    return merged

def main():
    # Read file name from user
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    input_dir = os.path.join(base_dir, "datasets")
    output_dir = os.path.join(base_dir, "outputs")
    os.makedirs(output_dir, exist_ok=True)

    filename = input("Enter file name: ")
    input_file = os.path.join(input_dir, filename)
    
    if not os.path.exists(input_file):
        print(f"File not found.")
        return

    try:
        start_row = int(input("Enter start row: "))
        end_row = int(input("Enter end row: "))

        data_list = []

        with open(input_file, 'r', newline='') as file:
            for current_line_number, line in enumerate(file, 1):
                if current_line_number < start_row:
                    continue

                if current_line_number > end_row:
                    break

                line = line.strip()

                if not line: 
                    continue
                
                parts = line.split(',') 

                if len(parts) == 2: 
                    try:
                        num = int(parts[0].strip()) 
                        text = parts[1] .strip()     
                        data_list.append((num, text))
                    except ValueError:
                        print(f"Warning: Could not convert '{parts[0]}' to integer in row {current_line_number}. Skipping row.", file=sys.stderr)
                    except Exception as e: 
                        print(f"Warning: Error processing row {current_line_number}: {e}.", file=sys.stderr)
                else:
                    print(f"Warning: Row {current_line_number} is invalid). Skipping row.", file=sys.stderr)

        if not data_list:
            print(f"No valid data found in rows {start_row}-{end_row} from '{filename}'. Please check the input file and row range.", file=sys.stderr)
            return

        output_filename = f"merge_sort_step_{start_row}_{end_row}.txt"
        output_file = os.path.join(output_dir, output_filename)

        with open(output_file, 'a') as f:
            f.write(f"Merge Sort Steps for rows {start_row}-{end_row}\n")

        sorted_data = merge_sort_step(data_list, output_file)

        with open(output_file, 'a') as f:
            f.write("\nFinal Sorted Data:\n")
            for num, text in sorted_data:
                f.write(f"{num}, {text}\n")

        print(f"Merged sort steps saved to {output_file}")

    except ValueError:
        print("Error: Please enter valid integer numbers for row values.", file=sys.stderr)
    except Exception as e:
        print(f"An unexpected error occurred: {str(e)}", file=sys.stderr)

if __name__ == "__main__":
    main()

                        