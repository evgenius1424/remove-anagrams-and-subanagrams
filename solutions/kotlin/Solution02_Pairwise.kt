package pairwise

private const val ALPHABET = 26

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val unique = words
        .groupBy { freqVector(it).toList() }
        .filterValues { it.size == 1 }

    if (unique.isEmpty()) return emptyList()

    val vecs = unique.keys.map { it.toIntArray() }

    return vecs
        .filter { candidate -> vecs.none { other -> dominates(other, candidate) } }
        .map { unique[it.toList()]!!.first() }
}

private fun freqVector(word: String): IntArray {
    val freq = IntArray(ALPHABET)
    for (c in word) freq[c - 'a']++
    return freq
}

private fun dominates(a: IntArray, b: IntArray): Boolean {
    if (a === b || a.contentEquals(b)) return false

    for (i in 0 until ALPHABET) {
        if (a[i] < b[i]) return false
    }
    return true
}