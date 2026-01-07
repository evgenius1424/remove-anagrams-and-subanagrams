#!/usr/bin/env python3

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


class TestCase:
    def __init__(self, id: int, category: str, input: list[str], expected: set[str], explanation: str):
        self.id = id
        self.category = category
        self.input = input
        self.expected = expected
        self.explanation = explanation


test_cases = [
    TestCase(1, "basic", ["a", "ab", "ba", "abc", "abcd"], {"abcd"}, "chain with anagrams"),
    TestCase(2, "basic", ["cat", "dog", "bird"], {"cat", "dog", "bird"}, "no anagrams"),
    TestCase(3, "basic", ["abc", "def", "cba", "fed", "xyz"], {"xyz"}, "multiple anagram pairs"),
    TestCase(4, "basic", ["aaa", "aa", "a", "aaaa"], {"aaaa"}, "same letter chain"),
    TestCase(5, "basic", ["ab", "cd", "abcd"], {"abcd"}, "two sub-anagrams"),
    TestCase(6, "edge", [], set(), "empty input"),
    TestCase(7, "edge", ["a"], {"a"}, "single word"),
    TestCase(8, "edge", ["ab", "ba"], set(), "two anagrams"),
    TestCase(9, "edge", ["aabb", "bbaa", "abab"], set(), "three anagrams"),
    TestCase(10, "edge", ["ab", "bc", "cd"], {"ab", "bc", "cd"}, "overlapping independent"),
    TestCase(11, "sub_anagram", ["ab", "bc", "abc"], {"abc"}, "partial overlaps dominated"),
    TestCase(12, "sub_anagram", ["a", "ab", "abc", "abcd", "abcde"], {"abcde"}, "long chain"),
    TestCase(13, "sub_anagram", ["xy", "xyz", "wxyz"], {"wxyz"}, "different letters"),
    TestCase(14, "sub_anagram", ["aab", "ab", "a"], {"aab"}, "frequency matters"),
    TestCase(15, "mixed", ["eat", "tea", "ate", "eating"], {"eating"}, "anagrams + sub"),
    TestCase(16, "mixed", ["listen", "silent", "enlist"], set(), "all anagrams"),
    TestCase(17, "mixed", ["abc", "abd", "acd", "bcd", "abcd"], {"abcd"}, "four dominated by one"),
    TestCase(18, "tricky", ["ab", "cd", "ef", "abcdef"], {"abcdef"}, "three pairs dominated"),
    TestCase(19, "tricky", ["aabb", "ab"], {"aabb"}, "aabb dominates ab"),
    TestCase(20, "tricky", ["abc", "def", "ghi"], {"abc", "def", "ghi"}, "independent same length"),
]


def main():
    print("Python Solution 1: Brute Force")
    print("Time: O(g² · 26), Space: O(g · 26)")
    print("Run: python solution_01_brute_force.py")
    print()
    print(f"Running {len(test_cases)} tests...")
    print()

    passed = 0
    failed = 0

    for tc in test_cases:
        result = set(remove_anagrams_and_sub_anagrams(tc.input))
        success = result == tc.expected

        if success:
            print(f"✓ Test {tc.id}: {tc.category} - {tc.explanation}")
            passed += 1
        else:
            print(f"✗ Test {tc.id}: {tc.category} - {tc.explanation}")
            print(f"  Input:    {tc.input}")
            print(f"  Expected: {tc.expected}")
            print(f"  Got:      {result}")
            failed += 1

    print()
    print("========================================")
    print(f"Results: {passed} passed, {failed} failed")
    if failed == 0:
        print("All tests passed! ✓")


if __name__ == "__main__":
    main()