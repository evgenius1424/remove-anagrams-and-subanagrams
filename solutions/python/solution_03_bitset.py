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

    vecs = sorted(unique.keys(), key=sum, reverse=True)
    max_freq = max(max(v) for v in vecs) + 1
    bitsets = [[0] * max_freq for _ in range(ALPHABET)]
    maximal = []

    for vec in vecs:
        if is_dominated(vec, bitsets):
            continue
        add_to_bitsets(vec, len(maximal), bitsets)
        maximal.append(vec)

    return [unique[vec] for vec in maximal]


def freq_vector(word: str) -> list[int]:
    freq = [0] * ALPHABET
    for c in word:
        freq[ord(c) - ord('a')] += 1
    return freq


def is_dominated(vec: tuple[int, ...], bitsets: list[list[int]]) -> bool:
    mask = ~0
    for letter in range(ALPHABET):
        need = vec[letter]
        if need == 0:
            continue
        mask &= bitsets[letter][need]
        if mask == 0:
            return False
    return mask != 0


def add_to_bitsets(vec: tuple[int, ...], idx: int, bitsets: list[list[int]]) -> None:
    bit = 1 << idx
    for letter in range(ALPHABET):
        for c in range(1, vec[letter] + 1):
            bitsets[letter][c] |= bit