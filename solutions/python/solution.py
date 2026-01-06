#!/usr/bin/env python3

import json
import sys
from typing import List
from pathlib import Path

def remove_anagrams(words: List[str]) -> List[str]:
    """
    TODO: Implement the solution
    """
    return []

def main():
    # Read test cases from shared file
    try:
        testcases_path = Path(__file__).parent.parent.parent / "testcases" / "cases.json"
        with open(testcases_path, 'r') as f:
            test_data = json.load(f)
    except Exception as e:
        print(f"Error reading test cases: {e}")
        return

    # Run test cases
    for i, test_case in enumerate(test_data["test_cases"]):
        result = remove_anagrams(test_case["input"])
        expected = test_case["expected"]
        passed = result == expected

        print(f"Test case {i + 1}: {'PASS' if passed else 'FAIL'}")
        if not passed:
            print(f"  Input: {test_case['input']}")
            print(f"  Expected: {expected}")
            print(f"  Got: {result}")

if __name__ == "__main__":
    main()