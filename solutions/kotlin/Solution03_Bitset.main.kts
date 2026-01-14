#!/usr/bin/env kotlin

@file:Import("TestCases.kts")

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

fun main() {
    println("Kotlin Solution 3: Bitset Indexing")
    println("Time: O(g · 26 · L), Space: O(26 · L · g/64)")
    println("Run: kotlin Solution03_Bitset.main.kts")
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
