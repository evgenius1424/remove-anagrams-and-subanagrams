#!/usr/bin/env kotlin

import java.io.File

data class TestCase(
    val id: Int,
    val category: String,
    val input: List<String>,
    val expected: List<String>,
    val explanation: String
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

private sealed class JValue {
    data class JObject(val map: Map<String, JValue>) : JValue()
    data class JArray(val list: List<JValue>) : JValue()
    data class JString(val value: String) : JValue()
    data class JNumber(val value: Double) : JValue()
    data class JBool(val value: Boolean) : JValue()
    data object JNull : JValue()
}

private class JsonParser(private val input: String) {
    private var index = 0

    fun parse(): JValue {
        skipWhitespace()
        val value = parseValue()
        skipWhitespace()
        if (index != input.length) {
            throw RuntimeException("Unexpected trailing data at position $index")
        }
        return value
    }

    private fun parseValue(): JValue {
        skipWhitespace()
        if (index >= input.length) throw RuntimeException("Unexpected end of input")
        return when (val c = input[index]) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> JValue.JString(parseString())
            't', 'f' -> parseBool()
            'n' -> parseNull()
            else -> if (c == '-' || c.isDigit()) parseNumber() else error("Unexpected char '$c' at $index")
        }
    }

    private fun parseObject(): JValue.JObject {
        expect('{')
        skipWhitespace()
        val map = mutableMapOf<String, JValue>()
        if (peek() == '}') {
            index++
            return JValue.JObject(map)
        }
        while (true) {
            skipWhitespace()
            val key = parseString()
            skipWhitespace()
            expect(':')
            val value = parseValue()
            map[key] = value
            skipWhitespace()
            when (peek()) {
                ',' -> {
                    index++
                    continue
                }
                '}' -> {
                    index++
                    break
                }
                else -> error("Expected ',' or '}' at $index")
            }
        }
        return JValue.JObject(map)
    }

    private fun parseArray(): JValue.JArray {
        expect('[')
        skipWhitespace()
        val list = mutableListOf<JValue>()
        if (peek() == ']') {
            index++
            return JValue.JArray(list)
        }
        while (true) {
            val value = parseValue()
            list.add(value)
            skipWhitespace()
            when (peek()) {
                ',' -> {
                    index++
                    continue
                }
                ']' -> {
                    index++
                    break
                }
                else -> error("Expected ',' or ']' at $index")
            }
        }
        return JValue.JArray(list)
    }

    private fun parseString(): String {
        expect('"')
        val sb = StringBuilder()
        while (index < input.length) {
            val c = input[index++]
            when (c) {
                '"' -> return sb.toString()
                '\\' -> {
                    if (index >= input.length) error("Unterminated escape at $index")
                    val esc = input[index++]
                    when (esc) {
                        '"', '\\', '/' -> sb.append(esc)
                        'b' -> sb.append('\b')
                        'f' -> sb.append('\u000C')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        't' -> sb.append('\t')
                        'u' -> {
                            if (index + 4 > input.length) error("Invalid unicode escape at $index")
                            val hex = input.substring(index, index + 4)
                            sb.append(hex.toInt(16).toChar())
                            index += 4
                        }
                        else -> error("Invalid escape '$esc' at $index")
                    }
                }
                else -> sb.append(c)
            }
        }
        error("Unterminated string at $index")
    }

    private fun parseNumber(): JValue.JNumber {
        val start = index
        if (peek() == '-') index++
        while (peek().isDigit()) index++
        if (peek() == '.') {
            index++
            while (peek().isDigit()) index++
        }
        if (peek() == 'e' || peek() == 'E') {
            index++
            if (peek() == '+' || peek() == '-') index++
            while (peek().isDigit()) index++
        }
        val numberText = input.substring(start, index)
        return JValue.JNumber(numberText.toDouble())
    }

    private fun parseBool(): JValue.JBool {
        return if (input.startsWith("true", index)) {
            index += 4
            JValue.JBool(true)
        } else if (input.startsWith("false", index)) {
            index += 5
            JValue.JBool(false)
        } else {
            error("Invalid boolean at $index")
        }
    }

    private fun parseNull(): JValue {
        if (input.startsWith("null", index)) {
            index += 4
            return JValue.JNull
        }
        error("Invalid null at $index")
    }

    private fun skipWhitespace() {
        while (index < input.length && input[index].isWhitespace()) index++
    }

    private fun expect(ch: Char) {
        if (index >= input.length || input[index] != ch) {
            error("Expected '$ch' at $index")
        }
        index++
    }

    private fun peek(): Char = if (index < input.length) input[index] else '\u0000'
}

private fun asString(value: JValue, field: String): String {
    return (value as? JValue.JString)?.value ?: error("Expected string for '$field'")
}

private fun asInt(value: JValue, field: String): Int {
    return (value as? JValue.JNumber)?.value?.toInt() ?: error("Expected number for '$field'")
}

private fun asStringList(value: JValue, field: String): List<String> {
    val array = value as? JValue.JArray ?: error("Expected array for '$field'")
    return array.list.map { asString(it, field) }
}

fun loadTestCases(): List<TestCase> {
    val casesFile = findCasesFile()
    val jsonString = casesFile.readText()
    val root = JsonParser(jsonString).parse() as? JValue.JObject
        ?: error("Expected root object")
    val testCases = root.map["test_cases"] as? JValue.JArray
        ?: error("Missing test_cases")

    return testCases.list.map { item ->
        val obj = item as? JValue.JObject ?: error("Expected test case object")
        val map = obj.map
        TestCase(
            id = asInt(map.getValue("id"), "id"),
            category = asString(map.getValue("category"), "category"),
            input = asStringList(map.getValue("input"), "input"),
            expected = asStringList(map.getValue("expected"), "expected"),
            explanation = asString(map.getValue("explanation"), "explanation")
        )
    }
}
