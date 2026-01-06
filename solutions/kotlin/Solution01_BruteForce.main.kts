#!/usr/bin/env kotlin

/**
 * Remove Anagrams and Sub-Anagrams
 * Approach: Brute Force
 * Time: O(n² · m) where n = words, m = max word length
 * Space: O(n · m)
 * Run: kotlin Solution01_BruteForce.main.kts
 */

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val toRemove = mutableSetOf<Int>()

    for (i in words.indices) {
        for (j in words.indices) {
            if (i == j) continue

            when {
                isAnagram(words[i], words[j]) -> {
                    toRemove.add(i)
                    toRemove.add(j)
                }

                isSubAnagram(words[i], words[j]) -> {
                    toRemove.add(i)
                }
            }
        }
    }

    return words.filterIndexed { index, _ -> index !in toRemove }
}

private fun isAnagram(a: String, b: String): Boolean {
    if (a.length != b.length) return false
    return a.toCharArray().sorted() == b.toCharArray().sorted()
}

private fun isSubAnagram(smaller: String, larger: String): Boolean {
    if (smaller.length >= larger.length) return false

    val freq = IntArray(26)
    for (c in larger) freq[c - 'a']++
    for (c in smaller) {
        if (--freq[c - 'a'] < 0) return false
    }
    return true
}

data class TestCase(
    val id: Int,
    val category: String,
    val input: List<String>,
    val expected: Set<String>,
    val explanation: String
)

val testCases = listOf(
    TestCase(1, "basic", listOf("a", "ab", "ba", "abc", "abcd"), setOf("abcd"), "chain with anagrams"),
    TestCase(2, "basic", listOf("cat", "dog", "bird"), setOf("cat", "dog", "bird"), "no anagrams"),
    TestCase(3, "basic", listOf("abc", "def", "cba", "fed", "xyz"), setOf("xyz"), "multiple anagram pairs"),
    TestCase(4, "basic", listOf("aaa", "aa", "a", "aaaa"), setOf("aaaa"), "same letter chain"),
    TestCase(5, "basic", listOf("ab", "cd", "abcd"), setOf("abcd"), "two sub-anagrams"),
    TestCase(6, "edge", emptyList(), emptySet(), "empty input"),
    TestCase(7, "edge", listOf("a"), setOf("a"), "single word"),
    TestCase(8, "edge", listOf("ab", "ba"), emptySet(), "two anagrams"),
    TestCase(9, "edge", listOf("aabb", "bbaa", "abab"), emptySet(), "three anagrams"),
    TestCase(10, "edge", listOf("ab", "bc", "cd"), setOf("ab", "bc", "cd"), "overlapping independent"),
    TestCase(11, "sub_anagram", listOf("ab", "bc", "abc"), setOf("abc"), "partial overlaps dominated"),
    TestCase(12, "sub_anagram", listOf("a", "ab", "abc", "abcd", "abcde"), setOf("abcde"), "long chain"),
    TestCase(13, "sub_anagram", listOf("xy", "xyz", "wxyz"), setOf("wxyz"), "different letters"),
    TestCase(14, "sub_anagram", listOf("aab", "ab", "a"), setOf("aab"), "frequency matters"),
    TestCase(15, "mixed", listOf("eat", "tea", "ate", "eating"), setOf("eating"), "anagrams + sub"),
    TestCase(16, "mixed", listOf("listen", "silent", "enlist"), emptySet(), "all anagrams"),
    TestCase(17, "mixed", listOf("abc", "abd", "acd", "bcd", "abcd"), setOf("abcd"), "four dominated by one"),
    TestCase(18, "tricky", listOf("ab", "cd", "ef", "abcdef"), setOf("abcdef"), "three pairs dominated"),
    TestCase(19, "tricky", listOf("aabb", "ab"), setOf("aabb"), "aabb dominates ab"),
    TestCase(20, "tricky", listOf("abc", "def", "ghi"), setOf("abc", "def", "ghi"), "independent same length"),
)

fun main() {
    println("Kotlin Solution 1: Brute Force")
    println("Time: O(n² · m), Space: O(n · m)")
    println("Run: kotlin Solution01_BruteForce.main.kts")
    println()
    println("Running ${testCases.size} tests...")
    println()

    var passed = 0
    var failed = 0

    for (tc in testCases) {
        val result = removeAnagramsAndSubAnagrams(tc.input).toSet()
        val success = result == tc.expected

        if (success) {
            println("✓ Test ${tc.id}: ${tc.category} - ${tc.explanation}")
            passed++
        } else {
            println("✗ Test ${tc.id}: ${tc.category} - ${tc.explanation}")
            println("  Input:    ${tc.input}")
            println("  Expected: ${tc.expected}")
            println("  Got:      $result")
            failed++
        }
    }

    println()
    println("========================================")
    println("Results: $passed passed, $failed failed")
    if (failed == 0) println("All tests passed! ✓")
}

main()