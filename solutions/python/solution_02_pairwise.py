#!/usr/bin/env python3
"""
Remove Anagrams and Sub-Anagrams - Frequency Vectors + Pairwise
Time: O(g² · 26), Space: O(g · 26)
Run: python solution_02_pairwise.py
"""

import json
from typing import List, Tuple
from pathlib import Path
from collections import Counter, defaultdict


def get_frequency_vector(word: str) -> Tuple[int, ...]:
    """Get frequency vector for a word."""
    freq = [0] * 26
    for char in word:
        freq[ord(char) - ord('a')] += 1
    return tuple(freq)


def is_dominated_by(smaller: Tuple[int, ...], larger: Tuple[int, ...]) -> bool:
    """Check if smaller frequency vector is dominated by larger."""
    smaller_sum = sum(smaller)
    larger_sum = sum(larger)

    if smaller_sum >= larger_sum:
        return False

    for i in range(26):
        if smaller[i] > larger[i]:
            return False

    return True


def remove_anagrams_and_sub_anagrams_pairwise(words: List[str]) -> List[str]:
    """Remove anagrams and sub-anagrams using frequency vectors and pairwise comparison."""
    if not words:
        return []

    # Group words by frequency vector
    grouped_by_freq = defaultdict(list)
    for word in words:
        freq = get_frequency_vector(word)
        grouped_by_freq[tuple(freq)].append(word)

    # Keep only groups with single words (no anagrams)
    unique_groups = []
    for freq, word_list in grouped_by_freq.items():
        if len(word_list) == 1:
            unique_groups.append((list(freq), word_list[0]))

    # Find maximal (non-dominated) frequency vectors
    maximal_groups = []
    for candidate in unique_groups:
        candidate_freq, candidate_word = candidate
        is_dominated = False

        for other in unique_groups:
            other_freq, other_word = other
            if candidate != other and is_dominated_by(candidate_freq, other_freq):
                is_dominated = True
                break

        if not is_dominated:
            maximal_groups.append(candidate_word)

    return maximal_groups


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
    print("Python Solution 2: Frequency Vectors + Pairwise")
    print("Time: O(g² · 26), Space: O(g · 26)")
    print("Run: python solution_02_pairwise.py")
    print()

    test_cases = load_test_cases()
    passed = 0
    failed = 0

    print(f"Running {len(test_cases)} tests...")
    print()

    for test_case in test_cases:
        result = remove_anagrams_and_sub_anagrams_pairwise(test_case['input'])
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