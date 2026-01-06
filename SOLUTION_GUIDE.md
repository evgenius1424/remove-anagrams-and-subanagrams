# Solution Guide: Remove Anagrams and Sub-Anagrams

This guide provides detailed explanations of all three algorithmic approaches to solving the "Remove Anagrams and Sub-Anagrams" problem.

## Table of Contents

1. [Problem Analysis](#problem-analysis)
2. [Approach 1: Brute Force](#approach-1-brute-force)
3. [Approach 2: Frequency Vectors + Pairwise](#approach-2-frequency-vectors--pairwise)
4. [Approach 3: Bitset Indexing](#approach-3-bitset-indexing)
5. [Complexity Comparison](#complexity-comparison)
6. [Connection to Computer Science Concepts](#connection-to-computer-science-concepts)

## Problem Analysis

### Problem Statement Breakdown

Given a list of words, we need to remove:
1. **Anagram pairs**: Words that are rearrangements of each other
2. **Dominated words**: Words that are "sub-anagrams" of larger words

### Key Definitions

- **Anagram**: Two words are anagrams if one can be formed by rearranging the letters of the other
  - Example: "cat" and "act" are anagrams
- **Sub-anagram/Dominance**: Word A is dominated by word B if:
  - All letters in A appear in B with at least the same frequency
  - A has strictly fewer total characters than B
  - Example: "ab" is dominated by "abc"

### Mathematical Formulation

We can represent each word as a **frequency vector** `f ∈ ℕ²⁶` where `f[i]` is the count of letter `i`.

- **Anagram detection**: `f(word1) = f(word2)`
- **Dominance**: `f(A) ≤ f(B)` component-wise AND `|A| < |B|`

The goal is to find the **Pareto frontier** of non-dominated words with no anagrams.

---

## Approach 1: Brute Force

### Intuition

The most straightforward approach: check every word against every other word to identify anagrams and dominance relationships.

### Algorithm

```python
def remove_anagrams_brute_force(words):
    to_remove = set()

    for i in range(len(words)):
        for j in range(len(words)):
            if i != j:
                if is_anagram(words[i], words[j]):
                    to_remove.add(i)
                    to_remove.add(j)
                elif is_sub_anagram(words[i], words[j]):
                    to_remove.add(i)

    return [words[i] for i in range(len(words)) if i not in to_remove]
```

### Helper Functions

```python
def is_anagram(word1, word2):
    return sorted(word1) == sorted(word2)

def is_sub_anagram(smaller, larger):
    if len(smaller) >= len(larger):
        return False

    smaller_counts = Counter(smaller)
    larger_counts = Counter(larger)

    for char, count in smaller_counts.items():
        if larger_counts.get(char, 0) < count:
            return False
    return True
```

### Step-by-Step Example

Input: `["a", "ab", "ba", "abc", "abcd"]`

1. Compare "a" with others:
   - vs "ab": "a" dominated by "ab" → remove "a"
   - vs "ba": "a" dominated by "ba" → remove "a"
   - vs "abc": "a" dominated by "abc" → remove "a"
   - vs "abcd": "a" dominated by "abcd" → remove "a"

2. Compare "ab" with others:
   - vs "ba": "ab" and "ba" are anagrams → remove both
   - vs "abc": "ab" dominated by "abc" → remove "ab"
   - vs "abcd": "ab" dominated by "abcd" → remove "ab"

3. Compare "ba" with others:
   - Already marked for removal (anagram with "ab")

4. Compare "abc" with others:
   - vs "abcd": "abc" dominated by "abcd" → remove "abc"

5. Only "abcd" remains unmarked

Result: `["abcd"]`

### Complexity Analysis

- **Time**: O(n² · m)
  - n² comparisons between word pairs
  - Each comparison takes O(m) time for sorting/counting
- **Space**: O(n · m)
  - Store all words and their sorted versions

### Pros and Cons

**Pros:**
- Simple to understand and implement
- Easy to debug and verify correctness
- No complex data structures needed

**Cons:**
- Inefficient for large inputs
- Lots of redundant work (repeatedly sorting same words)
- Poor cache locality due to random access patterns

---

## Approach 2: Frequency Vectors + Pairwise

### Intuition

Instead of comparing words directly, convert each word to a **frequency vector** and work with these vectors. This eliminates redundant work and makes anagram detection trivial.

### Key Insights

1. **Anagram grouping**: Words with identical frequency vectors are anagrams
2. **Dominance checking**: Vector A dominates B if A[i] ≥ B[i] for all i, and sum(A) > sum(B)
3. **Efficient comparison**: Compare frequency vectors instead of strings

### Algorithm

```python
def remove_anagrams_pairwise(words):
    # Step 1: Convert words to frequency vectors
    word_to_freq = {word: get_frequency_vector(word) for word in words}

    # Step 2: Group by frequency vectors (anagram detection)
    grouped_by_freq = defaultdict(list)
    for word, freq in word_to_freq.items():
        grouped_by_freq[tuple(freq)].append(word)

    # Step 3: Keep only singleton groups (no anagrams)
    unique_groups = []
    for freq_tuple, word_list in grouped_by_freq.items():
        if len(word_list) == 1:
            unique_groups.append((list(freq_tuple), word_list[0]))

    # Step 4: Pairwise dominance elimination
    maximal_groups = []
    for candidate_freq, candidate_word in unique_groups:
        is_dominated = False

        for other_freq, other_word in unique_groups:
            if candidate != other and is_dominated_by(candidate_freq, other_freq):
                is_dominated = True
                break

        if not is_dominated:
            maximal_groups.append(candidate_word)

    return maximal_groups
```

### Helper Functions

```python
def get_frequency_vector(word):
    freq = [0] * 26
    for char in word:
        freq[ord(char) - ord('a')] += 1
    return freq

def is_dominated_by(smaller, larger):
    if sum(smaller) >= sum(larger):
        return False

    for i in range(26):
        if smaller[i] > larger[i]:
            return False
    return True
```

### Step-by-Step Example

Input: `["a", "ab", "ba", "abc", "abcd"]`

**Step 1: Frequency vectors**
- "a" → [1,0,0,...,0]
- "ab" → [1,1,0,...,0]
- "ba" → [1,1,0,...,0]
- "abc" → [1,1,1,0,...,0]
- "abcd" → [1,1,1,1,0,...,0]

**Step 2: Group by frequency**
- [1,0,0,...,0] → ["a"]
- [1,1,0,...,0] → ["ab", "ba"] ← anagrams!
- [1,1,1,0,...,0] → ["abc"]
- [1,1,1,1,0,...,0] → ["abcd"]

**Step 3: Remove anagram groups**
Keep only singleton groups:
- [1,0,0,...,0] → "a"
- [1,1,1,0,...,0] → "abc"
- [1,1,1,1,0,...,0] → "abcd"

**Step 4: Pairwise dominance**
- "a" dominated by "abc"? Yes (1≤1, 0≤1, 0≤1, sum 1<3) → remove "a"
- "a" dominated by "abcd"? Yes → remove "a"
- "abc" dominated by "abcd"? Yes (1≤1, 1≤1, 1≤1, 0≤1, sum 3<4) → remove "abc"

Result: `["abcd"]`

### Complexity Analysis

- **Time**: O(g² · 26)
  - g = number of unique frequency vectors
  - g ≤ n, often g << n when many anagrams exist
  - 26 comparisons per dominance check
- **Space**: O(g · 26)
  - Store g frequency vectors of size 26 each

### Pros and Cons

**Pros:**
- Much faster than brute force
- Elegant separation of anagram detection and dominance
- Reduced number of comparisons when many anagrams exist

**Cons:**
- Still O(g²) in the worst case
- Doesn't scale well when all words have unique patterns
- Memory overhead for frequency vectors

---

## Approach 3: Bitset Indexing

### Intuition

The pairwise approach still does O(g²) dominance checks. Can we do better?

**Key insight**: Instead of checking every pair, use a **bitset index** to quickly find potential dominators. For each frequency vector, we can efficiently query which previously seen vectors might dominate it.

### Advanced Algorithm Design

The bitset approach uses **spatial indexing** similar to techniques in computational geometry and database systems.

**Core idea**: Index frequency vectors by (character, count) pairs. When checking if vector V is dominated, look up all vectors that have strictly more of at least one character.

### Algorithm

```python
def remove_anagrams_bitset(words):
    # Steps 1-3: Same as pairwise (anagram removal, unique groups)
    unique_groups = get_unique_frequency_groups(words)

    # Step 4: Sort by total count (descending) - important optimization!
    unique_groups.sort(key=lambda x: sum(x[0]), reverse=True)

    # Step 5: Bitset indexing for fast dominance queries
    bitsets = [[set() for _ in range(17)] for _ in range(26)]
    result = []

    for index, (freq, word) in enumerate(unique_groups):
        # Check if this vector is dominated by any previously processed vector
        if not is_dominated_by_index(freq, bitsets, unique_groups, index):
            result.append(word)
            # Add this vector to the index
            update_bitset_index(freq, index, bitsets)

    return result

def is_dominated_by_index(freq, bitsets, unique_groups, current_index):
    for char in range(26):
        count = freq[char]
        # Look for vectors with more of this character
        for higher_count in range(count + 1, 17):
            if bitsets[char][higher_count]:
                # Check if any candidate actually dominates
                for candidate_index in bitsets[char][higher_count]:
                    candidate_freq = unique_groups[candidate_index][0]
                    if dominates(candidate_freq, freq):
                        return True
    return False

def update_bitset_index(freq, index, bitsets):
    for char in range(26):
        count = freq[char]
        if count > 0:
            bitsets[char][count].add(index)
```

### Why Sorting Matters

Sorting by total character count (descending) is crucial:

1. **Longer words first**: Process potential dominators before dominated words
2. **Early termination**: Once we find a dominator, we can stop searching
3. **Index efficiency**: When checking dominance, we only look at previously processed (longer) words

### Bitset Index Structure

```
bitsets[char][count] = {set of indices with exactly 'count' of 'char'}

Example after processing "abcd", "xyz", "ab":
bitsets[0][1] = {0, 2}  # indices with 1 'a'
bitsets[1][1] = {0, 2}  # indices with 1 'b'
bitsets[2][1] = {0}     # indices with 1 'c'
bitsets[3][1] = {0}     # indices with 1 'd'
bitsets[23][1] = {1}    # indices with 1 'x'
bitsets[24][1] = {1}    # indices with 1 'y'
bitsets[25][1] = {1}    # indices with 1 'z'
```

### Step-by-Step Example

Input: `["a", "ab", "ba", "abc", "abcd"]`

**After anagram removal and sorting:**
```
unique_groups = [
    ([1,1,1,1,0,...,0], "abcd"),  # sum=4
    ([1,1,1,0,0,...,0], "abc"),   # sum=3
    ([1,0,0,0,0,...,0], "a")      # sum=1
]
```

**Processing:**

1. **Process "abcd" (index 0)**:
   - bitsets is empty, not dominated
   - Add to result: ["abcd"]
   - Update bitsets: bitsets[0][1].add(0), bitsets[1][1].add(0), etc.

2. **Process "abc" (index 1)**:
   - Check char 0 (count=1): look at bitsets[0][2], bitsets[0][3], ... (empty)
   - Check char 1 (count=1): look at bitsets[1][2], bitsets[1][3], ... (empty)
   - Check char 2 (count=1): look at bitsets[2][2], bitsets[2][3], ... (empty)
   - Wait! We need to check if any vector with higher counts dominates
   - Actually, "abcd" has counts [1,1,1,1] which dominates [1,1,1,0]
   - Found dominator → skip "abc"

3. **Process "a" (index 2)**:
   - Check char 0 (count=1): look at bitsets[0][2], bitsets[0][3], ...
   - Find that "abcd" dominates → skip "a"

Result: `["abcd"]`

### Complexity Analysis

- **Time**: O(g · 26 · L)
  - g = unique frequency vectors
  - 26 = alphabet size
  - L = maximum letter frequency (≤ 16 in our constraints)
  - Much better than O(g²) for large inputs!

- **Space**: O(26 · L · g/64)
  - 26 × 17 bitsets
  - Each bitset has at most g bits
  - Bit packing gives 64x space efficiency

### Why This is Optimal

The bitset approach achieves **output-sensitive complexity**:
- Time depends on the number of unique patterns, not total words
- Space is proportional to the structural complexity of the input
- Cache-friendly access patterns due to bitset operations

---

## Complexity Comparison

| Approach | Time | Space | Best Case | Worst Case |
|----------|------|--------|-----------|------------|
| **Brute Force** | O(n² · m) | O(n · m) | Still O(n²) | O(n²) |
| **Pairwise** | O(g² · 26) | O(g · 26) | O(g) when no dominance | O(g²) |
| **Bitset** | O(g · 26 · L) | O(26 · L · g/64) | O(g) | O(g · 26 · L) |

**Where:**
- n = total number of words
- m = maximum word length
- g = number of unique frequency vectors
- L = maximum frequency of any letter

**Key insights:**
- g ≤ n, often g << n when anagrams are common
- L ≤ 16 given our constraints (word length ≤ 16)
- Bitset approach scales much better as input size grows

---

## Connection to Computer Science Concepts

### 1. Pareto Optimality
This problem is essentially finding the **Pareto frontier** in a discrete optimization space. Each word represents a point in 26-dimensional frequency space, and we want non-dominated points.

### 2. Skyline Queries
The dominance relationship is similar to **skyline queries** in databases, where you find records that aren't dominated by others across multiple dimensions.

### 3. Bit Manipulation and Parallelism
The bitset approach leverages **bit-level parallelism** available in modern CPUs. Set operations can be performed on 64 elements simultaneously.

### 4. Spatial Indexing
The bitset index is a form of **spatial data structure**, similar to R-trees or KD-trees, optimized for high-dimensional discrete spaces.

### 5. Output-Sensitive Algorithms
The progression from O(n²) to O(g²) to O(g · 26 · L) demonstrates **output-sensitive algorithm design**, where complexity depends on the output structure rather than just input size.

### 6. Cache Optimization
The frequency vector approach improves **cache locality** by grouping related computations and reducing random memory access patterns.

---

## Practical Implementation Notes

### Language-Specific Considerations

**Python**:
- Use `collections.Counter` for frequency counting
- `defaultdict` for grouping
- List comprehensions for filtering

**Java**:
- `HashMap` for frequency grouping
- `HashSet` for efficient lookups
- Careful about integer overflow in large inputs

**Rust**:
- `std::collections::HashMap` with owned keys
- Efficient memory management with borrowing
- `BitVec` crate for advanced bitset operations

**Kotlin**:
- Extension functions for cleaner frequency counting
- `groupBy` for elegant anagram grouping
- Sequence processing for memory efficiency

### Testing Strategy

1. **Unit tests** for helper functions (anagram detection, dominance)
2. **Property-based testing** to verify algorithmic invariants
3. **Performance benchmarks** across different input characteristics
4. **Corner cases**: empty inputs, single words, all anagrams

### Production Considerations

- **Input validation**: Check for null inputs, character set constraints
- **Memory limits**: Monitor memory usage for very large inputs
- **Timeout handling**: Set reasonable time limits for processing
- **Logging**: Track performance metrics and edge cases

This completes our comprehensive analysis of the three approaches to solving the Remove Anagrams and Sub-Anagrams problem. Each approach offers different trade-offs between simplicity, performance, and scalability.