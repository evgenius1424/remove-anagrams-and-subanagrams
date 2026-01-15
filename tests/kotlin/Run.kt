import java.io.File
import kotlin.system.exitProcess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import bruteforce.removeAnagramsAndSubAnagrams as bruteForceRemove
import pairwise.removeAnagramsAndSubAnagrams as pairwiseRemove
import bitset.removeAnagramsAndSubAnagrams as bitsetRemove

@Serializable
data class TestCase(
    val id: Int,
    val category: String,
    val input: List<String>,
    val expected: List<String>,
    val explanation: String
)

@Serializable
data class TestCaseFile(
    @SerialName("test_cases")
    val testCases: List<TestCase>
)

private fun findCasesFile(): File {
    val cwd = File(System.getProperty("user.dir"))
    val candidates = listOf(
        File(cwd, "testcases/cases.json"),
        File(cwd, "../testcases/cases.json"),
        File(cwd, "../../testcases/cases.json")
    )

    return candidates.firstOrNull { it.exists() } ?: run {
        val tried = candidates.joinToString { it.absolutePath }
        throw RuntimeException("Test cases file not found. Tried: $tried")
    }
}

private fun loadTestCases(): List<TestCase> {
    val casesFile = findCasesFile()
    val json = Json { ignoreUnknownKeys = true }
    val parsed = json.decodeFromString<TestCaseFile>(casesFile.readText())
    return parsed.testCases
}

fun main() {
    val testCases = loadTestCases()
    val solutions = listOf(
        "BruteForce" to ::bruteForceRemove,
        "Pairwise" to ::pairwiseRemove,
        "Bitset" to ::bitsetRemove
    )

    var totalFailed = 0
    for ((name, solver) in solutions) {
        println("Running ${testCases.size} Kotlin tests for $name...")
        var passed = 0
        var failed = 0

        for (tc in testCases) {
            val result = solver(tc.input).toSet()
            val expected = tc.expected.toSet()
            val success = result == expected

            if (success) {
                println("OK  Test ${tc.id}: ${tc.category} - ${tc.explanation}")
                passed++
            } else {
                println("FAIL Test ${tc.id}: ${tc.category} - ${tc.explanation}")
                println("  Input:    ${tc.input}")
                println("  Expected: $expected")
                println("  Got:      $result")
                failed++
            }
        }

        println("Results for $name: $passed passed, $failed failed")
        totalFailed += failed
    }

    if (totalFailed > 0) {
        exitProcess(1)
    }
}
