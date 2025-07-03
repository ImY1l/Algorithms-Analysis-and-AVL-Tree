# 📊 Algorithm Design and Analysis - Assignment
This project implements and analyzes sorting and searching algorithms as part of the CCP6214 course assignment for Trimester March/April 2025 (Term 2510). The focus is on:
- Comparative analysis of **Merge Sort** and **Quick Sort** (using last element as pivot)
- Analysis of **Binary Search** best, average, and worst cases
- Implementations in two programming languages (chosen by the group)
- Dataset generation for testing algorithms
- Theoretical and experimental study of time and space complexities
- Discussion on array-based AVL tree implementation vs linked structure

---

## 📁 Project Structure
| Component                | File Name Pattern             | Purpose                                                                |
|--------------------------|-------------------------------|------------------------------------------------------------------------|
| Dataset (Pre-generated)  | `dataset_*.csv`               | Contains randomized datasets used for sorting and searching algorithms |
| Merge Sort (Stepwise)    | `merge_sort_step`             | Performs and logs step-by-step merge sort on a specified row range     |
| Quick Sort (Stepwise)    | `quick_sort_step`             | Performs and logs step-by-step quick sort on a specified row range     |
| Merge Sort (Full)        | `merge_sort`                  | Executes full merge sort on large dataset; outputs sorted file and time|
| Quick Sort (Full)        | `quick_sort`                  | Executes full quick sort on large dataset; outputs sorted file and time|
| Binary Search (Stepwise) | `binary_search_step`          | Traces the search path for a specific element within a sorted dataset  |
| Binary Search (Full)     | `binary_search`               | Runs binary search multiple times to measure best, average, and worst case runtimes |

---

## 💻 Tech Stack
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)

---

## 🔢 Dataset
- Input datasets are CSV files with two fields per row: a unique 32-bit positive integer and a string.
- The dataset generator creates randomized datasets with unique integers up to at least 1 billion.
- Sample dataset provided: [dataset_sample_1000.csv](./dataset_sample_1000.csv)
- Dataset size for experiments should be large enough to show a runtime difference of at least 60 seconds between sorting algorithms.

---

## 📋 Implementation Details
- Algorithms are implemented in **two programming languages** chosen by the group (Java, Python).
- No use of built-in sorting/searching libraries or data structures that perform internal sorting (e.g., TreeSet, TreeMap, PriorityQueue).
- Only arrays and lists (array lists or linked lists) are allowed as data structures.
- Running time measurements exclude I/O time.
- At least 10 different input sizes must be tested for performance analysis.

---

## 🚀 Run Locally
1. Clone the repository
  ```bash
  git clone https://github.com/ImY1l/Algorithms-Analysis-and-AVL-Tree.git
  cd Algorithms-Analysis-and-AVL-Tree
  ```
2. Generate datasets
  ```bash
  cd java
  javac dataset_generator.java
  java dataset_generator y
  ```
4. Run the program
  - Java:
    ```bash
    cd java
    javac x.java
    Java x dataset_y.csv
    ```
  - Python
    ```bash
    cd python
    python x.py dataset_y.csv
    ```
5. Binary search
   ```bash
   cd python
   python binary_search.py
   # or
   cd java
   javac binary_search.java
   java binary_search
   ```
> The binary search will ask you to insert the sorted file name.
> Every algorithm can be run to sort a dataset or to generate steps text file (_step).
|---|---|
| X | Replace x with the actual class name (e.g. merge_sort, quick_sort).   |
| Y | Replace y with the actual number of the dataset you like to generate/sort. |

[![](https://visitcount.itsvg.in/api?id=imy1l&icon=0&color=0)](https://visitcount.itsvg.in)
