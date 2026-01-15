package pairwise

private const val ALPHABET = 26

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { freqVector(it).toList() }
    val uniqueGroups = groups.filter { it.value.size == 1 }

    if (uniqueGroups.isEmpty()) return emptyList()

    val vecs = uniqueGroups.keys.map { it.toIntArray() }
    val maximal = vecs.filter { candidate ->
        vecs.none { other -> dominates(other, candidate) }
    }

    return maximal.map { uniqueGroups[it.toList()]!!.first() }
}

private fun freqVector(word: String): IntArray {
    val freq = IntArray(ALPHABET)
    for (c in word) {
        if (c in 'a'..'z') freq[c - 'a']++
    }
    return freq
}

private fun dominates(a: IntArray, b: IntArray): Boolean {
    if (a === b) return false
    if (a.contentEquals(b)) return false

    var strict = false
    for (i in 0 until ALPHABET) {
        if (a[i] < b[i]) return false
        if (a[i] > b[i]) strict = true
    }
    return strict
}
