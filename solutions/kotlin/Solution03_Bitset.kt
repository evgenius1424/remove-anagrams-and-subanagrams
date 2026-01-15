package bitset

private const val ALPHABET = 26

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val unique = words
        .groupBy { freqVector(it).toList() }
        .filterValues { it.size == 1 }

    if (unique.isEmpty()) return emptyList()

    val vecs = unique.keys
        .map { it.toIntArray() }
        .sortedByDescending { it.sum() }

    val maxFreq = vecs.maxOf { it.max() } + 1
    val bitsets = Array(ALPHABET) { Array(maxFreq) { mutableListOf<Long>() } }
    val maximal = mutableListOf<IntArray>()

    for (vec in vecs) {
        if (isDominated(vec, maximal.size, bitsets)) continue
        addToBitsets(vec, maximal.size, bitsets)
        maximal.add(vec)
    }

    return maximal.map { unique[it.toList()]!!.first() }
}

private fun freqVector(word: String): IntArray {
    val freq = IntArray(ALPHABET)
    for (c in word) freq[c - 'a']++
    return freq
}

private fun isDominated(vec: IntArray, count: Int, bitsets: Array<Array<MutableList<Long>>>): Boolean {
    if (count == 0) return false

    val blocks = (count + 63) / 64
    val mask = LongArray(blocks) { -1L }
    val lastBits = count % 64
    if (lastBits != 0) {
        mask[blocks - 1] = (1L shl lastBits) - 1
    }

    for (letter in 0 until ALPHABET) {
        val need = vec[letter]
        if (need == 0) continue

        val bs = bitsets[letter][need]
        if (bs.isEmpty()) return false

        for (j in mask.indices) {
            mask[j] = mask[j] and bs.getOrElse(j) { 0L }
        }
        if (mask.all { it == 0L }) return false
    }
    return true
}

private fun addToBitsets(vec: IntArray, idx: Int, bitsets: Array<Array<MutableList<Long>>>) {
    val block = idx / 64
    val bit = 1L shl (idx % 64)

    for (letter in 0 until ALPHABET) {
        for (c in 1..vec[letter]) {
            val bs = bitsets[letter][c]
            while (bs.size <= block) bs.add(0L)
            bs[block] = bs[block] or bit
        }
    }
}