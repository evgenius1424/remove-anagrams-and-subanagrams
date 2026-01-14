#!/usr/bin/env kotlin

@file:Import("TestCases.kts")

fun removeAnagrams(words: Array<String>): List<String> {
    // TODO: Implement the solution
    return emptyList()
}

fun main() {
    val testCases = try {
        loadTestCases()
    } catch (e: Exception) {
        println("Error reading test cases: ${e.message}")
        return
    }

    testCases.forEachIndexed { index, testCase ->
        val result = removeAnagrams(testCase.input.toTypedArray())
        val passed = result == testCase.expected

        println("Test case ${index + 1}: ${if (passed) "PASS" else "FAIL"}")
        if (!passed) {
            println("  Input: ${testCase.input}")
            println("  Expected: ${testCase.expected}")
            println("  Got: $result")
        }
    }
}
