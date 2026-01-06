# Bitset Indexing Explained: A Visual Guide

This document provides a beginner-friendly, visual explanation of the most advanced algorithm for solving the "Remove Anagrams and Sub-Anagrams" problem.

## Table of Contents

1. [The Problem with Naive Approaches](#the-problem-with-naive-approaches)
2. [The Key Insight](#the-key-insight)
3. [Bitset Index Structure](#bitset-index-structure)
4. [Step-by-Step Visual Example](#step-by-step-visual-example)
5. [Why It's Fast: Bit Parallelism](#why-its-fast-bit-parallelism)
6. [Advanced Optimizations](#advanced-optimizations)

---

## The Problem with Naive Approaches

### Brute Force: O(n²) Comparisons

```
Words: ["a", "ab", "abc", "abcd", "ba"]

Brute force checks EVERY pair:
a  ↔ ab   ✓ (a dominated by ab)
a  ↔ abc  ✓ (a dominated by abc)
a  ↔ abcd ✓ (a dominated by abcd)
a  ↔ ba   ✓ (a dominated by ba)
ab ↔ abc  ✓ (ab dominated by abc)
ab ↔ abcd ✓ (ab dominated by abcd)
ab ↔ ba   ✓ (ab and ba are anagrams)
... and so on

Total comparisons: n × (n-1) / 2 = 10 comparisons for 5 words!
```

For 10,000 words → **50 million comparisons** 😱

### Pairwise: Still O(g²) After Grouping

Even after removing anagrams by grouping frequency vectors, we still need to check every unique pattern against every other:

```
After anagram removal: ["a", "abc", "abcd"]
(3 unique frequency vectors)

Pairwise still needs:
a   ↔ abc  ✓
a   ↔ abcd ✓
abc ↔ abcd ✓

3 × 2 / 2 = 3 comparisons
```

This scales as O(g²) where g = unique patterns.

---

## The Key Insight

**What if we could avoid checking EVERY pair?**

Instead of asking "Does any other word dominate this one?", we ask:

> "Given this word's frequency pattern, which previously seen words COULD POSSIBLY dominate it?"

### The Magic: Spatial Indexing

Think of each word as a point in 26-dimensional space:

```
"a"    = (1, 0, 0, 0, ..., 0)    [coordinates for a,b,c,d,...]
"ab"   = (1, 1, 0, 0, ..., 0)
"abc"  = (1, 1, 1, 0, ..., 0)
"abcd" = (1, 1, 1, 1, ..., 0)
```

**Dominance rule**: Point A dominates point B if:
- A has ≥ coordinates in ALL dimensions
- A has > total coordinates than B

Instead of checking all pairs, we **index points by their coordinates** and query efficiently!

---

## Bitset Index Structure

### The Index: A Multi-dimensional Map

```
bitsets[letter][count] = {set of word indices with exactly 'count' of 'letter'}
```

**Visual representation:**

```
             Count:  0   1   2   3   4   ...
Letter 'a' (0):    {} {1,3} {2} {} {} ...
Letter 'b' (1):    {} {1,3} {2} {} {} ...
Letter 'c' (2):    {} {3}   {2} {} {} ...
Letter 'd' (3):    {} {3}   {}  {} {} ...
...
Letter 'z' (25):   {} {}    {}  {} {} ...

Where:
1 = "a"     → freq = (1,0,0,0,...)
2 = "abc"   → freq = (1,1,1,0,...)
3 = "abcd"  → freq = (1,1,1,1,...)
```

### How Queries Work

To check if word X is dominated:

1. **For each letter in X**: Look at all counts > X's count for that letter
2. **For each candidate**: Check if it truly dominates X
3. **Early termination**: Stop as soon as we find a dominator

**Example**: Checking if "ab" = (1,1,0,0,...) is dominated:

```
Check letter 'a' (count=1): Look at bitsets[0][2], bitsets[0][3], ...
Check letter 'b' (count=1): Look at bitsets[1][2], bitsets[1][3], ...
Check letter 'c' (count=0): Look at bitsets[2][1], bitsets[2][2], ...
```

If any of these sets contain a word that dominates "ab", we're done!

---

## Step-by-Step Visual Example

Let's trace through: `["a", "ab", "ba", "abc", "abcd"]`

### Step 1: Remove Anagrams and Sort

```
After anagram removal: [("abcd", (1,1,1,1)), ("abc", (1,1,1,0)), ("a", (1,0,0,0))]
Sorted by length:      [("abcd", (1,1,1,1)), ("abc", (1,1,1,0)), ("a", (1,0,0,0))]
```

### Step 2: Process "abcd" (Index 0)

```
Current bitsets: ALL EMPTY

Check if "abcd" is dominated:
- No entries in bitsets yet
- "abcd" is NOT dominated

Add to result: ["abcd"]
Update bitsets:
bitsets[0][1].add(0)  # 'a' appears 1 time
bitsets[1][1].add(0)  # 'b' appears 1 time
bitsets[2][1].add(0)  # 'c' appears 1 time
bitsets[3][1].add(0)  # 'd' appears 1 time

Bitsets now:
         0  1  2  3  4
    a:   {} {0} {} {} {}
    b:   {} {0} {} {} {}
    c:   {} {0} {} {} {}
    d:   {} {0} {} {} {}
    e-z: {} {}  {} {} {}
```

### Step 3: Process "abc" (Index 1)

```
Check if "abc" = (1,1,1,0) is dominated:

Letter 'a' (count=1): Check bitsets[0][2], bitsets[0][3], ... → ALL EMPTY
Letter 'b' (count=1): Check bitsets[1][2], bitsets[1][3], ... → ALL EMPTY
Letter 'c' (count=1): Check bitsets[2][2], bitsets[2][3], ... → ALL EMPTY

Wait! We need to check if ANY existing word dominates "abc"...

Actually, let's check existing index 0 ("abcd"):
"abcd" = (1,1,1,1) vs "abc" = (1,1,1,0)
- a: 1 ≥ 1 ✓
- b: 1 ≥ 1 ✓
- c: 1 ≥ 1 ✓
- d: 1 ≥ 0 ✓
- Sum: 4 > 3 ✓

"abc" IS dominated by "abcd"! Skip it.
```

### Step 4: Process "a" (Index 2)

```
Check if "a" = (1,0,0,0) is dominated:

We know "abcd" exists in our index.
"abcd" = (1,1,1,1) vs "a" = (1,0,0,0)
- a: 1 ≥ 1 ✓
- b: 1 ≥ 0 ✓
- c: 1 ≥ 0 ✓
- d: 1 ≥ 0 ✓
- Sum: 4 > 1 ✓

"a" IS dominated by "abcd"! Skip it.
```

### Final Result

```
Result: ["abcd"] ✅
```

---

## Why It's Fast: Bit Parallelism

### Bitset Operations Are Parallel

Modern CPUs can operate on 64 bits simultaneously:

```
Instead of:
for each candidate:
    if candidate dominates current:
        return True

We can do:
candidates_bitset = bitsets[char][count] ∪ bitsets[char][count+1] ∪ ...
if candidates_bitset.intersects(dominators_of_current):
    return True
```

### Cache-Friendly Access Patterns

**Bad (Pairwise)**:
```
for word1 in words:
    for word2 in words:        # Random memory access
        compare(word1, word2)   # Cache miss likely
```

**Good (Bitset)**:
```
for word in words:
    for letter in range(26):           # Sequential access
        for count in bitsets[letter]:  # Localized data
            # Process batch of candidates
```

### Early Termination

```
Worst case comparisons:
Brute force: n × n = 10,000²  = 100,000,000
Pairwise:    g × g = 1,000²   = 1,000,000
Bitset:      g × 26 × L = 1,000 × 26 × 16 = 416,000

Best case (early termination):
Bitset: Often just a few lookups per word!
```

---

## Advanced Optimizations

### 1. Why Sort by Length?

Processing longer words first maximizes the chances of early termination:

```
Good order: ["abcdef", "abcd", "ab", "a"]
- Process "abcdef": Not dominated (nothing processed yet)
- Process "abcd": Quickly dominated by "abcdef"
- Process "ab": Quickly dominated by "abcdef"
- Process "a": Quickly dominated by "abcdef"

Bad order: ["a", "ab", "abcd", "abcdef"]
- Process "a": Not dominated yet (nothing to check against)
- Process "ab": Check against "a" - not dominated
- Process "abcd": Check against "a", "ab" - not dominated
- Process "abcdef": Check against "a", "ab", "abcd" - not dominated
- Then remove "a", "ab", "abcd" retroactively? Inefficient!
```

### 2. Bitset Optimization Details

**Space efficiency**: Instead of storing full sets, use bitsets:
```
Before: bitsets[char][count] = {0, 5, 17, 234, 1337}  # 40+ bytes
After:  bitsets[char][count] = 0b100010000100000001000001  # 8 bytes
```

**Intersection operations**:
```
# Check if any candidate dominates current word
candidates = bitsets[0][2] | bitsets[0][3] | bitsets[1][2] | ...
potential_dominators = compute_dominators(current_word)
if (candidates & potential_dominators).any():
    # Found dominator!
```

### 3. Memory Layout

```
Optimal layout for cache performance:

bitsets[26][17] stored as:
[letter0: count0-16][letter1: count0-16]...[letter25: count0-16]

Each access pattern:
- Access bitsets[letter][count+1], bitsets[letter][count+2], ...
- Sequential memory access within same letter
- Good spatial locality
```

---

## Comparison Summary

| Algorithm | Time | Space | Cache | Parallelism | Real-world |
|-----------|------|-------|-------|-------------|------------|
| **Brute Force** | O(n²) | O(n) | ❌ Poor | ❌ None | Seconds for 1K words |
| **Pairwise** | O(g²) | O(g) | ⚠️ OK | ⚠️ Limited | Milliseconds for 1K words |
| **Bitset** | O(g·26·L) | O(g) | ✅ Good | ✅ Excellent | Microseconds for 1K words |

### When to Use Each

- **Brute Force**: Educational purposes, tiny inputs (< 100 words)
- **Pairwise**: Good balance for medium inputs (100-10,000 words)
- **Bitset**: Production systems, large inputs (> 1,000 words)

---

## Implementation Tips

### Common Pitfalls

1. **Forgetting to sort by length**: Kills performance benefits
2. **Not handling empty frequency counts**: Index out of bounds errors
3. **Incorrect dominance logic**: Off-by-one errors in comparisons

### Testing Your Implementation

```python
# Test dominance logic
assert dominates([2,1,0], [1,1,0])  # More 'a's, same others
assert not dominates([1,1,0], [2,1,0])  # Fewer 'a's
assert not dominates([1,1,1], [1,1,1])  # Equal (not strictly dominated)

# Test bitset index
words = ["a", "ab", "abc"]
index = build_bitset_index(words)
assert 0 in index[0][1]  # "a" has 1 'a'
assert 1 in index[0][1]  # "ab" has 1 'a'
assert 1 in index[1][1]  # "ab" has 1 'b'
```

### Language-Specific Notes

**Python**: Use `set` for bitsets (simple), or `bitarray` library (optimized)
**Java**: `BitSet` class, or `HashSet<Integer>` for simplicity
**Rust**: `bit-vec` crate, or `HashSet<usize>`
**C++**: `std::bitset<N>` for fixed size, `std::vector<bool>` for dynamic

This completes our deep-dive into the bitset indexing algorithm. The key insight is transforming a quadratic comparison problem into a logarithmic lookup problem through clever spatial indexing!