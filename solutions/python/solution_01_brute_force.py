#!/usr/bin/env python3

import json
import os

ALPHABET = 26

def remove_anagrams_and_sub_anagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups = {}
    for word in words:
        key = tuple(freq_vector(word))
        groups.setdefault(key, []).append(word)

    unique_groups = {k: v[0] for k, v in groups.items() if len(v) == 1}

    if not unique_groups:
        return []

    to_remove = set()
    keys = list(unique_groups.keys())

    for i, vec_i in enumerate(keys):
        for j, vec_j in enumerate(keys):
            if i == j:
                continue
            if is_sub_anagram(vec_i, vec_j):
                to_remove.add(vec_i)
                break

    return [unique_groups[k] for k in keys if k not in to_remove]


def freq_vector(word: str) -> list[int]:
    f = [0] * ALPHABET
    for c in word:
        if 'a' <= c <= 'z':
            f[ord(c) - ord('a')] += 1
    return f


def is_sub_anagram(smaller: tuple[int, ...], larger: tuple[int, ...]) -> bool:
    if sum(smaller) >= sum(larger):
        return False

    for i in range(ALPHABET):
        if smaller[i] > larger[i]:
            return False
    return True


def load_test_cases():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    cases_path = os.path.join(script_dir, "..", "..", "testcases", "cases.json")

    with open(cases_path, 'r') as f:
        data = json.load(f)

    test_cases = []
    for case in data['test_cases']:
        test_cases.append({
            'id': case['id'],
            'category': case['category'],
            'input': case['input'],
            'expected': set(case['expected']),
            'explanation': case['explanation']
        })
    return test_cases


def main():
    print("Python Solution 1: Brute Force")
    print("Time: O(g² · 26), Space: O(g · 26)")
    print("Run: python solution_01_brute_force.py")
    print()

    test_cases = load_test_cases()
    print(f"Running {len(test_cases)} tests...")
    print()

    passed = 0
    failed = 0

    for tc in test_cases:
        result = set(remove_anagrams_and_sub_anagrams(tc['input']))
        success = result == tc['expected']

        if success:
            print(f"✓ Test {tc['id']}: {tc['category']} - {tc['explanation']}")
            passed += 1
        else:
            print(f"✗ Test {tc['id']}: {tc['category']} - {tc['explanation']}")
            print(f"  Input:    {tc['input']}")
            print(f"  Expected: {tc['expected']}")
            print(f"  Got:      {result}")
            failed += 1

    print()
    print("========================================")
    print(f"Results: {passed} passed, {failed} failed")
    if failed == 0:
        print("All tests passed! ✓")


if __name__ == "__main__":
    main()