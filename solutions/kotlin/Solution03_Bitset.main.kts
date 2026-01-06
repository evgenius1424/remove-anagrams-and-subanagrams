#!/usr/bin/env kotlin

import java.io.File
import java.util.BitSet
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

@Serializable
data class TestCase(
    val id: Int,
    val category: String,
    val input: List<String>,
    val expected: List<String>,
    val explanation: String
)

@Serializable
data class TestData(val test_cases: List<TestCase>)

fun getFrequencyVector(word: String): IntArray {
    val freq = IntArray(26)
    for (char in word) {
        freq[char - 'a']++
    }
    return freq
}

fun removeAnagramsAndSubAnagramsBitset(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val wordToFreq = words.map { word -> word to getFrequencyVector(word) }
    val groupedByFreq = wordToFreq.groupBy { it.second.contentToString() }

    val uniqueGroups = mutableListOf<Pair<IntArray, String>>()
    for ((_, pairs) in groupedByFreq) {
        if (pairs.size == 1) {
            uniqueGroups.add(pairs[0].second to pairs[0].first)
        }
    }

    uniqueGroups.sortByDescending { it.first.sum() }

    val bitsets = Array(26) { Array(17) { BitSet() } }
    val result = mutableListOf<String>()

    for ((index, pair) in uniqueGroups.withIndex()) {
        val (freq, word) = pair
        var isDominated = false

        for (char in 0 until 26) {
            val count = freq[char]
            for (higherCount in count + 1 until 17) {
                if (bitsets[char][higherCount].intersects(
                        BitSet().apply {
                            for (otherChar in 0 until 26) {
                                if (otherChar != char) {
                                    val otherCount = freq[otherChar]
                                    for (existingIndex in 0 until index) {
                                        val otherFreq = uniqueGroups[existingIndex].first
                                        if (otherFreq[otherChar] >= otherCount) {
                                            set(existingIndex)
                                        }
                                    }
                                }
                            }
                        }
                    )) {
                    isDominated = true
                    break
                }
            }
            if (isDominated) break
        }

        if (!isDominated) {
            for (char in 0 until 26) {
                val count = freq[char]
                if (count > 0) {
                    bitsets[char][count].set(index)
                }
            }
            result.add(word)
        }
    }

    return result
}

fun loadTestCases(): List<TestCase> {
    return try {
        val jsonString = File("../../testcases/cases.json").readText()
        Json.decodeFromString<TestData>(jsonString).test_cases
    } catch (e: Exception) {
        println("Could not load test cases from JSON, using embedded cases")
        listOf(
            TestCase(1, "basic", listOf("a", "ab", "ba", "abc", "abcd"), listOf("abcd"), ""),
            TestCase(2, "basic", listOf("abc", "def", "ghi"), listOf("abc", "def", "ghi"), ""),
            TestCase(3, "basic", listOf("a", "aa", "aaa"), listOf("aaa"), ""),
            TestCase(4, "basic", listOf("cat", "act", "dog"), listOf("dog"), ""),
            TestCase(5, "basic", listOf("listen", "silent", "enlist"), emptyList(), "")
        )
    }
}

fun main() {
    println("Kotlin Solution 3: Bitset Indexing")
    println("Time: O(g · 26 · L), Space: O(26 · L · g/64)")
    println("Run: kotlin Solution03_Bitset.main.kts")
    println()

    val testCases = loadTestCases()
    var passed = 0
    var failed = 0

    println("Running ${testCases.size} tests...")
    println()

    for (testCase in testCases) {
        val result = removeAnagramsAndSubAnagramsBitset(testCase.input)
        val resultSorted = result.sorted()
        val expectedSorted = testCase.expected.sorted()

        if (resultSorted == expectedSorted) {
            println("✓ Test ${testCase.id}: ${testCase.category}")
            passed++
        } else {
            println("✗ Test ${testCase.id}: ${testCase.category}")
            println("  Input: ${testCase.input}")
            println("  Expected: ${testCase.expected}")
            println("  Got: $result")
            failed++
        }
    }

    println()
    println("========================================")
    println("Results: $passed passed, $failed failed")
}

main()