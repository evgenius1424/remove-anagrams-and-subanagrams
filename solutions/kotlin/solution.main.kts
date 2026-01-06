#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TestCase(
    val id: Int,
    val category: String,
    val input: List<String>,
    val expected: List<String>,
    val explanation: String
)

@Serializable
data class TestData(
    val test_cases: List<TestCase>
)

fun removeAnagrams(words: Array<String>): List<String> {
    // TODO: Implement the solution
    return emptyList()
}

fun main() {
    // Read test cases from shared file
    val testData = try {
        val jsonString = File("../../testcases/cases.json").readText()
        Json.decodeFromString<TestData>(jsonString)
    } catch (e: Exception) {
        println("Error reading test cases: ${e.message}")
        return
    }

    // Run test cases
    testData.test_cases.forEachIndexed { index, testCase ->
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