#!/usr/bin/env kotlin

import java.io.File
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

fun isDominatedBy(smaller: IntArray, larger: IntArray): Boolean {
    val smallerSum = smaller.sum()
    val largerSum = larger.sum()

    if (smallerSum >= largerSum) return false

    for (i in 0 until 26) {
        if (smaller[i] > larger[i]) {
            return false
        }
    }
    return true
}

fun areEqual(freq1: IntArray, freq2: IntArray): Boolean {
    return freq1.contentEquals(freq2)
}

fun removeAnagramsAndSubAnagramsPairwise(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val wordToFreq = words.map { word -> word to getFrequencyVector(word) }
    val groupedByFreq = wordToFreq.groupBy { it.second.contentToString() }

    val uniqueFreqs = mutableListOf<Pair<IntArray, List<String>>>()
    for ((_, pairs) in groupedByFreq) {
        if (pairs.size == 1) {
            uniqueFreqs.add(pairs[0].second to listOf(pairs[0].first))
        }
    }

    val maximalFreqs = mutableListOf<Pair<IntArray, List<String>>>()

    for (candidate in uniqueFreqs) {
        var isDominated = false
        for (other in uniqueFreqs) {
            if (candidate !== other && isDominatedBy(candidate.first, other.first)) {
                isDominated = true
                break
            }
        }
        if (!isDominated) {
            maximalFreqs.add(candidate)
        }
    }

    return maximalFreqs.flatMap { it.second }
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
    println("Kotlin Solution 2: Frequency Vectors + Pairwise")
    println("Time: O(g² · 26), Space: O(g · 26)")
    println("Run: kotlin Solution02_Pairwise.main.kts")
    println()

    val testCases = loadTestCases()
    var passed = 0
    var failed = 0

    println("Running ${testCases.size} tests...")
    println()

    for (testCase in testCases) {
        val result = removeAnagramsAndSubAnagramsPairwise(testCase.input)
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