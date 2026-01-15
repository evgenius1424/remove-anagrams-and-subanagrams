ALPHABET = 26


def removeAnagramsAndSubAnagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups: dict[str, list[str]] = {}
    for word in words:
        key = "".join(sorted(word))
        groups.setdefault(key, []).append(word)

    unique = [v[0] for v in groups.values() if len(v) == 1]
    if not unique:
        return []

    return [
        word for word in unique
        if not any(is_sub_anagram(word, other) for other in unique)
    ]


def is_sub_anagram(smaller: str, larger: str) -> bool:
    if len(smaller) >= len(larger):
        return False

    freq = [0] * ALPHABET
    for c in larger:
        freq[ord(c) - ord('a')] += 1
    for c in smaller:
        idx = ord(c) - ord('a')
        freq[idx] -= 1
        if freq[idx] < 0:
            return False
    return True