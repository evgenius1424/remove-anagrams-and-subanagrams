package bruteforce

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { it.toCharArray().sorted() }
    val unique = groups.filterValues { it.size == 1 }.values.map { it.first() }

    return unique.filter { candidate ->
        unique.none { other -> isDominatedBy(candidate, other) }
    }
}

private fun isDominatedBy(smaller: String, larger: String): Boolean {
    if (smaller.length >= larger.length) return false

    val freq = IntArray(26)
    for (c in larger) freq[c - 'a']++
    for (c in smaller) {
        if (--freq[c - 'a'] < 0) return false
    }
    return true
}