#!/usr/bin/env python3
"""
Remove Anagrams and Sub-Anagrams - Bitset Indexing
Time: O(g · 26 · L), Space: O(26 · L · g/64)
Run: python solution_03_bitset.py
"""

import json
from typing import List, Tuple
from pathlib import Path
from collections import defaultdict


def get_frequency_vector(word: str) -> List[int]:
    """Get frequency vector for a word."""
    freq = [0] * 26
    for char in word:
        freq[ord(char) - ord('a')] += 1
    return freq


def remove_anagrams_and_sub_anagrams_bitset(words: List[str]) -> List[str]:
    """Remove anagrams and sub-anagrams using bitset indexing."""
    if not words:
        return []

    # Group words by frequency vector
    grouped_by_freq = defaultdict(list)
    for word in words:
        freq = get_frequency_vector(word)
        freq_key = tuple(freq)
        grouped_by_freq[freq_key].append(word)

    # Keep only groups with single words (no anagrams)
    unique_groups = []
    for freq_tuple, word_list in grouped_by_freq.items():
        if len(word_list) == 1:
            unique_groups.append((list(freq_tuple), word_list[0]))

    # Sort by total character count (descending) for optimal processing
    unique_groups.sort(key=lambda x: sum(x[0]), reverse=True)

    # Bitset index: bitsets[char][count] = set of indices
    bitsets = [[set() for _ in range(17)] for _ in range(26)]
    result = []

    for index, (freq, word) in enumerate(unique_groups):
        is_dominated = False

        # Check if this frequency vector is dominated
        for char in range(26):
            count = freq[char]
            # Check if any existing vector has more of this character
            # and also dominates in all other characters
            for higher_count in range(count + 1, 17):
                if bitsets[char][higher_count]:
                    # Check if any of these candidates dominates our frequency
                    for candidate_index in bitsets[char][higher_count]:
                        candidate_freq = unique_groups[candidate_index][0]
                        # Check dominance: candidate must have >= count for all chars
                        dominates = True
                        for other_char in range(26):
                            if candidate_freq[other_char] < freq[other_char]:
                                dominates = False
                                break
                        if dominates:
                            is_dominated = True
                            break
                    if is_dominated:
                        break
            if is_dominated:
                break

        if not is_dominated:
            # Add this vector to the result
            result.append(word)
            # Index this vector in bitsets
            for char in range(26):
                count = freq[char]
                if count > 0:
                    bitsets[char][count].add(index)

    return result


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
    print("Python Solution 3: Bitset Indexing")
    print("Time: O(g · 26 · L), Space: O(26 · L · g/64)")
    print("Run: python solution_03_bitset.py")
    print()

    test_cases = load_test_cases()
    passed = 0
    failed = 0

    print(f"Running {len(test_cases)} tests...")
    print()

    for test_case in test_cases:
        result = remove_anagrams_and_sub_anagrams_bitset(test_case['input'])
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