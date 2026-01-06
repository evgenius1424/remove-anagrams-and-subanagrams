#!/usr/bin/env python3
"""
Remove Anagrams and Sub-Anagrams - Brute Force Approach
Time: O(n² · m), Space: O(n · m)
Run: python solution_01_brute_force.py
"""

import json
from typing import List
from pathlib import Path
from collections import Counter


def is_anagram(word1: str, word2: str) -> bool:
    """Check if two words are anagrams."""
    return sorted(word1) == sorted(word2)


def is_sub_anagram(smaller: str, larger: str) -> bool:
    """Check if smaller is a sub-anagram of larger."""
    if len(smaller) >= len(larger):
        return False

    smaller_counts = Counter(smaller)
    larger_counts = Counter(larger)

    for char, count in smaller_counts.items():
        if larger_counts.get(char, 0) < count:
            return False

    return True


def remove_anagrams_and_sub_anagrams_brute_force(words: List[str]) -> List[str]:
    """Remove anagrams and sub-anagrams using brute force approach."""
    if not words:
        return []

    to_remove = set()

    for i in range(len(words)):
        for j in range(len(words)):
            if i != j:
                if is_anagram(words[i], words[j]):
                    to_remove.add(i)
                    to_remove.add(j)
                elif is_sub_anagram(words[i], words[j]):
                    to_remove.add(i)

    return [words[i] for i in range(len(words)) if i not in to_remove]


def load_test_cases():
    """Load test cases from JSON file or return embedded cases."""
    try:
        test_file = Path(__file__).parent.parent.parent / "testcases" / "cases.json"
        with open(test_file, 'r') as f:
            data = json.load(f)
            return data['test_cases']
    except (FileNotFoundError, KeyError, json.JSONDecodeError):
        print("Could not load test cases from JSON, using embedded cases")
        return [
            {
                "id": 1,
                "category": "basic",
                "input": ["a", "ab", "ba", "abc", "abcd"],
                "expected": ["abcd"],
                "explanation": ""
            },
            {
                "id": 2,
                "category": "basic",
                "input": ["abc", "def", "ghi"],
                "expected": ["abc", "def", "ghi"],
                "explanation": ""
            },
            {
                "id": 3,
                "category": "basic",
                "input": ["a", "aa", "aaa"],
                "expected": ["aaa"],
                "explanation": ""
            },
            {
                "id": 4,
                "category": "basic",
                "input": ["cat", "act", "dog"],
                "expected": ["dog"],
                "explanation": ""
            },
            {
                "id": 5,
                "category": "basic",
                "input": ["listen", "silent", "enlist"],
                "expected": [],
                "explanation": ""
            }
        ]


def main():
    print("Python Solution 1: Brute Force")
    print("Time: O(n² · m), Space: O(n · m)")
    print("Run: python solution_01_brute_force.py")
    print()

    test_cases = load_test_cases()
    passed = 0
    failed = 0

    print(f"Running {len(test_cases)} tests...")
    print()

    for test_case in test_cases:
        result = remove_anagrams_and_sub_anagrams_brute_force(test_case['input'])
        result_sorted = sorted(result)
        expected_sorted = sorted(test_case['expected'])

        if result_sorted == expected_sorted:
            print(f"✓ Test {test_case['id']}: {test_case['category']}")
            passed += 1
        else:
            print(f"✗ Test {test_case['id']}: {test_case['category']}")
            print(f"  Input: {test_case['input']}")
            print(f"  Expected: {test_case['expected']}")
            print(f"  Got: {result}")
            failed += 1

    print()
    print("========================================")
    print(f"Results: {passed} passed, {failed} failed")


if __name__ == "__main__":
    main()