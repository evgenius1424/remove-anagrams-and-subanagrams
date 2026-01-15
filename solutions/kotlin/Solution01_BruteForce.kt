package bruteforce

fun removeAnagramsAndSubAnagrams(words: List<String>): List<String> {
    if (words.isEmpty()) return emptyList()

    val toRemove = mutableSetOf<Int>()

    for (i in words.indices) {
        for (j in words.indices) {
            if (i == j) continue

            when {
                isAnagram(words[i], words[j]) -> {
                    toRemove.add(i)
                    toRemove.add(j)
                }
                isSubAnagram(words[i], words[j]) -> {
                    toRemove.add(i)
                }
            }
        }
    }

    return words.filterIndexed { index, _ -> index !in toRemove }
}

private fun isAnagram(a: String, b: String): Boolean {
    if (a.length != b.length) return false
    return a.toCharArray().sorted() == b.toCharArray().sorted()
}

private fun isSubAnagram(smaller: String, larger: String): Boolean {
    if (smaller.length >= larger.length) return false

    val freq = IntArray(26)
    for (c in larger) freq[c - 'a']++
    for (c in smaller) {
        if (--freq[c - 'a'] < 0) return false
    }
    return true
}
