#!/usr/bin/env python3

import json
import os

ALPHABET = 26
MAX_FREQ = 17

def remove_anagrams_and_sub_anagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups = {}
    for word in words:
        key = tuple(freq_vector(word))
        groups.setdefault(key, []).append(word)

    unique_groups = {k: v for k, v in groups.items() if len(v) == 1}

    if not unique_groups:
        return []

    vecs = sorted(unique_groups.keys(), key=sum, reverse=True)

    maximal = []
    bitsets = [[[] for _ in range(MAX_FREQ)] for _ in range(ALPHABET)]

    for vec in vecs:
        if is_dominated(vec, len(maximal), bitsets):
            continue
        add_to_bitsets(vec, len(maximal), bitsets)
        maximal.append(vec)

    return [unique_groups[vec][0] for vec in maximal]


def freq_vector(word: str) -> list[int]:
    f = [0] * ALPHABET
    for c in word:
        if 'a' <= c <= 'z':
            f[ord(c) - ord('a')] += 1
    return f


def is_dominated(vec: tuple[int, ...], count: int, bitsets: list[list[list[int]]]) -> bool:
    if count == 0:
        return False

    blocks = (count + 63) // 64
    mask = [~0 for _ in range(blocks)]
    last_block_bits = count % 64
    if last_block_bits != 0:
        mask[blocks - 1] = (1 << last_block_bits) - 1

    for letter in range(ALPHABET):
        need = vec[letter]
        if need == 0:
            continue

        bs = bitsets[letter][need]
        if not bs:
            return False

        for j in range(len(mask)):
            mask[j] &= bs[j] if j < len(bs) else 0

        if all(m == 0 for m in mask):
            return False

    return True


def add_to_bitsets(vec: tuple[int, ...], idx: int, bitsets: list[list[list[int]]]) -> None:
    block = idx // 64
    bit = 1 << (idx % 64)

    for letter in range(ALPHABET):
        for c in range(1, vec[letter] + 1):
            bs = bitsets[letter][c]
            while len(bs) <= block:
                bs.append(0)
            bs[block] |= bit


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
    print("Python Solution 3: Bitset Indexing")
    print("Time: O(g · 26 · L), Space: O(26 · L · g/64)")
    print("Run: python solution_03_bitset.py")
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