#!/usr/bin/env kotlin

@file:Import("TestCases.kts")

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

fun main() {
    println("Kotlin Solution 2: Frequency Vector + Pairwise")
    println("Time: O(g² · 26), Space: O(g · 26)")
    println("Run: kotlin Solution02_Pairwise.main.kts")
    println()

    val testCases = loadTestCases()
    println("Running ${testCases.size} tests...")
    println()

    var passed = 0
    var failed = 0

    for (tc in testCases) {
        val result = removeAnagramsAndSubAnagrams(tc.input).toSet()
        val success = result == tc.expected.toSet()

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
