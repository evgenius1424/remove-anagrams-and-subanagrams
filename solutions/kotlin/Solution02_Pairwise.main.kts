#!/usr/bin/env kotlin

/**
 * Remove Anagrams and Sub-Anagrams
 * Approach: Frequency Vector + Pairwise Comparison
 * Time: O(g² · 26) where g = unique frequency vectors
 * Space: O(g · 26)
 * Run: kotlin Solution02_Pairwise.main.kts
 */

val ALPHABET = 26

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { freqVector(it).toList() }
    val uniqueGroups = groups.filter { it.value.size == 1 }

    if (uniqueGroups.isEmpty()) return emptyList()

    val vecs = uniqueGroups.keys.map { it.toIntArray() }

    val maximal = vecs.filter { candidate ->
        vecs.none { other -> dominates(other, candidate) }
    }

    return maximal.map { uniqueGroups[it.toList()]!!.first() }
}

private fun freqVector(word: String): IntArray {
    val f = IntArray(ALPHABET)
    for (c in word) {
        if (c in 'a'..'z') f[c - 'a']++
    }
    return f
}

private fun dominates(a: IntArray, b: IntArray): Boolean {
    if (a === b) return false
    if (a.contentEquals(b)) return false

    var dominated = true
    var strict = false

    for (i in 0 until ALPHABET) {
        if (a[i] < b[i]) {
            dominated = false
            break
        }
        if (a[i] > b[i]) {
            strict = true
        }
    }

    return dominated && strict
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
    println("Kotlin Solution 2: Frequency Vector + Pairwise")
    println("Time: O(g² · 26), Space: O(g · 26)")
    println("Run: kotlin Solution02_Pairwise.main.kts")
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