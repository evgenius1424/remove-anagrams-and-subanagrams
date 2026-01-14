#!/usr/bin/env kotlin

@file:Import("TestCases.kts")

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

fun main() {
    println("Kotlin Solution 1: Brute Force")
    println("Time: O(n² · m), Space: O(n · m)")
    println("Run: kotlin Solution01_BruteForce.main.kts")
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
