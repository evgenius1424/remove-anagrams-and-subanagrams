private const val ALPHABET_03 = 26
private const val MAX_FREQ_03 = 17

fun removeAnagramsAndSubAnagrams03(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val groups = words.groupBy { freqVector03(it).toList() }
    val uniqueGroups = groups.filter { it.value.size == 1 }

    if (uniqueGroups.isEmpty()) return emptyList()

    val vecs = uniqueGroups.keys
        .map { it.toIntArray() }
        .sortedByDescending { it.sum() }

    val maximal = mutableListOf<IntArray>()
    val bitsets = Array(ALPHABET_03) { Array(MAX_FREQ_03) { mutableListOf<Long>() } }

    for (vec in vecs) {
        if (isDominated03(vec, maximal.size, bitsets)) continue
        addToBitsets03(vec, maximal.size, bitsets)
        maximal.add(vec)
    }

    return maximal.map { uniqueGroups[it.toList()]!!.first() }
}

private fun freqVector03(word: String): IntArray {
    val freq = IntArray(ALPHABET_03)
    for (c in word) {
        if (c in 'a'..'z') freq[c - 'a']++
    }
    return freq
}

private fun isDominated03(
    vec: IntArray,
    count: Int,
    bitsets: Array<Array<MutableList<Long>>>
): Boolean {
    if (count == 0) return false

    val blocks = (count + 63) / 64
    val mask = LongArray(blocks) { -1L }
    val lastBlockBits = count % 64
    if (lastBlockBits != 0) {
        mask[blocks - 1] = (1L shl lastBlockBits) - 1
    }

    for (letter in 0 until ALPHABET_03) {
        val need = vec[letter]
        if (need == 0) continue

        val bs = bitsets[letter][need]
        if (bs.isEmpty()) return false

        for (j in mask.indices) {
            mask[j] = mask[j] and (bs.getOrElse(j) { 0L })
        }

        if (mask.all { it == 0L }) return false
    }

    return true
}

private fun addToBitsets03(vec: IntArray, idx: Int, bitsets: Array<Array<MutableList<Long>>>) {
    val block = idx / 64
    val bit = 1L shl (idx % 64)

    for (letter in 0 until ALPHABET_03) {
        for (c in 1..vec[letter]) {
            val bs = bitsets[letter][c]
            while (bs.size <= block) bs.add(0L)
            bs[block] = bs[block] or bit
        }
    }
}
