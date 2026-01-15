ALPHABET = 26

def removeAnagramsAndSubAnagrams(words: list[str]) -> list[str]:
    if not words:
        return []

    groups = {}
    for word in words:
        key = tuple(freq_vector(word))
        groups.setdefault(key, []).append(word)

    unique_groups = {k: v[0] for k, v in groups.items() if len(v) == 1}

    if not unique_groups:
        return []

    to_remove = set()
    keys = list(unique_groups.keys())

    for i, vec_i in enumerate(keys):
        for j, vec_j in enumerate(keys):
            if i == j:
                continue
            if is_sub_anagram(vec_i, vec_j):
                to_remove.add(vec_i)
                break

    return [unique_groups[k] for k in keys if k not in to_remove]


def freq_vector(word: str) -> list[int]:
    f = [0] * ALPHABET
    for c in word:
        if 'a' <= c <= 'z':
            f[ord(c) - ord('a')] += 1
    return f


def is_sub_anagram(smaller: tuple[int, ...], larger: tuple[int, ...]) -> bool:
    if sum(smaller) >= sum(larger):
        return False

    for i in range(ALPHABET):
        if smaller[i] > larger[i]:
            return False
    return True


