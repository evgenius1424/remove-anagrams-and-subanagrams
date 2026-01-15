private const val ALPHABET_02 = 26

fun removeAnagramsAndSubAnagrams02(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { freqVector02(it).toList() }
    val uniqueGroups = groups.filter { it.value.size == 1 }

    if (uniqueGroups.isEmpty()) return emptyList()

    val vecs = uniqueGroups.keys.map { it.toIntArray() }
    val maximal = vecs.filter { candidate ->
        vecs.none { other -> dominates02(other, candidate) }
    }

    return maximal.map { uniqueGroups[it.toList()]!!.first() }
}

private fun freqVector02(word: String): IntArray {
    val freq = IntArray(ALPHABET_02)
    for (c in word) {
        if (c in 'a'..'z') freq[c - 'a']++
    }
    return freq
}

private fun dominates02(a: IntArray, b: IntArray): Boolean {
    if (a === b) return false
    if (a.contentEquals(b)) return false

    var strict = false
    for (i in 0 until ALPHABET_02) {
        if (a[i] < b[i]) return false
        if (a[i] > b[i]) strict = true
    }
    return strict
}
