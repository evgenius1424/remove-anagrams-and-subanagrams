#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

import java.io.File
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class TestCase(
    val input: Input,
    val expected: List<String>
)

@Serializable
data class Input(
    val words: List<String>
)

@Serializable
data class TestData(
    val testcases: List<TestCase>
)

fun removeAnagrams(words: Array<String>): List<String> {
    // TODO: Implement the solution
    return emptyList()
}

fun main() {
    // Read test cases from shared file
    val testData = try {
        val jsonString = File("../../testcases.json").readText()
        Json.decodeFromString<TestData>(jsonString)
    } catch (e: Exception) {
        println("Error reading test cases: ${e.message}")
        return
    }

    // Run test cases
    testData.testcases.forEachIndexed { index, testCase ->
        val result = removeAnagrams(testCase.input.words.toTypedArray())
        val passed = result == testCase.expected

        println("Test case ${index + 1}: ${if (passed) "PASS" else "FAIL"}")
        if (!passed) {
            println("  Input: ${testCase.input.words}")
            println("  Expected: ${testCase.expected}")
            println("  Got: $result")
        }
    }
}