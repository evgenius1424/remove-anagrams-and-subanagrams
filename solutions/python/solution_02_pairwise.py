ALPHABET = 26

def removeAnagramsAndSubAnagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups = {}
    for word in words:
        key = tuple(freq_vector(word))
        groups.setdefault(key, []).append(word)

    unique_groups = {k: v for k, v in groups.items() if len(v) == 1}

    if not unique_groups:
        return []

    vecs = list(unique_groups.keys())

    maximal = [candidate for candidate in vecs
               if not any(dominates(other, candidate) for other in vecs)]

    return [unique_groups[vec][0] for vec in maximal]


def freq_vector(word: str) -> list[int]:
    f = [0] * ALPHABET
    for c in word:
        if 'a' <= c <= 'z':
            f[ord(c) - ord('a')] += 1
    return f


def dominates(a: tuple[int, ...], b: tuple[int, ...]) -> bool:
    if a is b:
        return False
    if a == b:
        return False

    dominated = True
    strict = False

    for i in range(ALPHABET):
        if a[i] < b[i]:
            dominated = False
            break
        if a[i] > b[i]:
            strict = True

    return dominated and strict


