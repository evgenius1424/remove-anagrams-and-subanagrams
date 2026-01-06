#!/usr/bin/env kotlin

/**
 * Remove Anagrams and Sub-Anagrams
 * Approach: Bitset Indexing (Optimal)
 * Time: O(g · 26 · L) where g = unique groups, L = max letter frequency
 * Space: O(26 · L · g/64)
 * Run: kotlin Solution03_Bitset.main.kts
 */

val ALPHABET = 26
val MAX_FREQ = 17

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { freqVector(it).toList() }
    val uniqueGroups = groups.filter { it.value.size == 1 }

    if (uniqueGroups.isEmpty()) return emptyList()

    val vecs = uniqueGroups.keys
        .map { it.toIntArray() }
        .sortedByDescending { it.sum() }

    val maximal = mutableListOf<IntArray>()
    val bitsets = Array(ALPHABET) { Array(MAX_FREQ) { mutableListOf<Long>() } }

    for (vec in vecs) {
        if (isDominated(vec, maximal.size, bitsets)) continue
        addToBitsets(vec, maximal.size, bitsets)
        maximal.add(vec)
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

private fun isDominated(vec: IntArray, count: Int, bitsets: Array<Array<MutableList<Long>>>): Boolean {
    if (count == 0) return false

    val blocks = (count + 63) / 64
    val mask = LongArray(blocks) { -1L }
    val lastBlockBits = count % 64
    if (lastBlockBits != 0) {
        mask[blocks - 1] = (1L shl lastBlockBits) - 1
    }

    for (letter in 0 until ALPHABET) {
        val need = vec[letter]
        if (need == 0) continue

        val bs = bitsets[letter][need]
        if (bs.isEmpty()) return false

        for (j in mask.indices) {
            mask[j] = mask[j] and (bs.getOrElse(j) { 0L })
        }

        if (mask.all { it == 0L }) return false
    }

    return true
}

private fun addToBitsets(vec: IntArray, idx: Int, bitsets: Array<Array<MutableList<Long>>>) {
    val block = idx / 64
    val bit = 1L shl (idx % 64)

    for (letter in 0 until ALPHABET) {
        for (c in 1..vec[letter]) {
            val bs = bitsets[letter][c]
            while (bs.size <= block) bs.add(0L)
            bs[block] = bs[block] or bit
        }
    }
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
    println("Kotlin Solution 3: Bitset Indexing")
    println("Time: O(g · 26 · L), Space: O(26 · L · g/64)")
    println("Run: kotlin Solution03_Bitset.main.kts")
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