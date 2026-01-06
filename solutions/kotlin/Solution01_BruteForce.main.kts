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

fun isAnagram(word1: String, word2: String): Boolean {
    return word1.toCharArray().sorted() == word2.toCharArray().sorted()
}

fun isSubAnagram(smaller: String, larger: String): Boolean {
    if (smaller.length >= larger.length) return false

    val smallerCounts = smaller.groupingBy { it }.eachCount()
    val largerCounts = larger.groupingBy { it }.eachCount()

    for ((char, count) in smallerCounts) {
        if (largerCounts.getOrDefault(char, 0) < count) {
            return false
        }
    }
    return true
}

fun removeAnagramsAndSubAnagramsBruteForce(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val toRemove = mutableSetOf<Int>()

    for (i in words.indices) {
        for (j in words.indices) {
            if (i != j) {
                if (isAnagram(words[i], words[j])) {
                    toRemove.add(i)
                    toRemove.add(j)
                } else if (isSubAnagram(words[i], words[j])) {
                    toRemove.add(i)
                }
            }
        }
    }

    return words.filterIndexed { index, _ -> index !in toRemove }
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
    println("Kotlin Solution 1: Brute Force")
    println("Time: O(n² · m), Space: O(n · m)")
    println("Run: kotlin Solution01_BruteForce.main.kts")
    println()

    val testCases = loadTestCases()
    var passed = 0
    var failed = 0

    println("Running ${testCases.size} tests...")
    println()

    for (testCase in testCases) {
        val result = removeAnagramsAndSubAnagramsBruteForce(testCase.input)
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