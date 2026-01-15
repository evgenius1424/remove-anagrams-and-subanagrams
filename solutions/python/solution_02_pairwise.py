ALPHABET = 26


def removeAnagramsAndSubAnagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups: dict[tuple[int, ...], list[str]] = {}
    for word in words:
        key = tuple(freq_vector(word))
        groups.setdefault(key, []).append(word)

    unique = {k: v[0] for k, v in groups.items() if len(v) == 1}
    if not unique:
        return []

    vecs = list(unique.keys())
    return [
        unique[vec] for vec in vecs
        if not any(dominates(other, vec) for other in vecs)
    ]


def freq_vector(word: str) -> list[int]:
    freq = [0] * ALPHABET
    for c in word:
        freq[ord(c) - ord('a')] += 1
    return freq


def dominates(a: tuple[int, ...], b: tuple[int, ...]) -> bool:
    if a == b:
        return False
    return all(a[i] >= b[i] for i in range(ALPHABET))