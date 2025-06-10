# 📊 Algorithm Design and Analysis - Assignment
This project implements and analyzes sorting and searching algorithms as part of the CCP6214 course assignment for Trimester March/April 2025 (Term 2510). The focus is on:
- Comparative analysis of **Merge Sort** and **Quick Sort** (using last element as pivot)
- Analysis of **Binary Search** best, average, and worst cases
- Implementations in two programming languages (chosen by the group)
- Dataset generation for testing algorithms
- Theoretical and experimental study of time and space complexities
- Discussion on array-based AVL tree implementation vs linked structure

## 📁 Project Structure
| Algorithm                 | File Name Pattern                 | Description                        |
|---------------------------|---------------------------------|----------------------------------|
| Dataset Generator         | `dataset_generator`              | Generates randomized unique datasets (integer, string pairs) |
| Merge Sort (stepwise)     | `merge_sort_step`                | Outputs sorting steps for a specified range of rows |
| Quick Sort (stepwise)     | `quick_sort_step`                | Outputs sorting steps for a specified range of rows |
| Merge Sort (full)         | `merge_sort`                    | Sorts entire dataset and outputs sorted file and running time |
| Quick Sort (full)         | `quick_sort`                    | Sorts entire dataset and outputs sorted file and running time |
| Binary Search (stepwise)  | `binary_search_step`             | Outputs search path for a target element |
| Binary Search (full)      | `binary_search`                  | Measures running time for best, average, worst cases over multiple searches |


## 💻 Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)

## 🔢 Dataset
- Input datasets are CSV files with two fields per row: a unique 32-bit positive integer and a string.
- The dataset generator creates randomized datasets with unique integers up to at least 1 billion.
- Sample dataset provided: [dataset_sample_1000.csv](./dataset_sample_1000.csv)
- Dataset size for experiments should be large enough to show a runtime difference of at least 60 seconds between sorting algorithms.

## 📋 Implementation Details
- Algorithms are implemented in **two programming languages** chosen by the group (e.g., Java, Python, C++).
- No use of built-in sorting/searching libraries or data structures that perform internal sorting (e.g., TreeSet, TreeMap, PriorityQueue).
- Only arrays and lists (array lists or linked lists) are allowed as data structures.
- Running time measurements exclude I/O time.
- At least 10 different input sizes must be tested for performance analysis.

## 🚀 Run Locally

[![](https://visitcount.itsvg.in/api?id=imy1l&icon=0&color=0)](https://visitcount.itsvg.in)
