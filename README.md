# Remove Anagrams and Sub-Anagrams

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Languages](https://img.shields.io/badge/Languages-Kotlin%20%7C%20Python%20%7C%20Rust-blue)

A collection of optimized algorithms to solve the "Remove Anagrams and Sub-Anagrams" problem with detailed explanations and implementations in multiple programming languages.

## Problem Statement

Given a list of words, remove all words that:
1. **Have an anagram in the list** (remove BOTH anagram words)
2. **Are a sub-anagram of another word** (the smaller word is "dominated" by the larger one)

A word `A` is a **sub-anagram** of word `B` if:
- All characters in `A` appear in `B` with at least the same frequency
- `A` has fewer total characters than `B`

Return the remaining words in any order.

## Examples

### Example 1
```
Input: ["a", "ab", "ba", "abc", "abcd"]
Output: ["abcd"]

Explanation:
- "ab" and "ba" are anagrams → both removed
- "a" is a sub-anagram of "ab" → removed (but "ab" was already removed)
- "abc" is a sub-anagram of "abcd" → removed
- Only "abcd" remains
```

### Example 2
```
Input: ["abc", "def", "ghi"]
Output: ["abc", "def", "ghi"]

Explanation: No anagrams or dominance relationships exist
```

### Example 3
```
Input: ["cat", "act", "dog"]
Output: ["dog"]

Explanation: "cat" and "act" are anagrams → both removed
```

### Example 4
```
Input: ["listen", "silent", "enlist"]
Output: []

Explanation: All three words are anagrams → all removed
```

### Example 5
```
Input: ["a", "aa", "aaa", "b", "bb"]
Output: ["aaa", "bb"]

Explanation:
- "a" < "aa" < "aaa" (dominance chain)
- "b" < "bb" (dominance chain)
```

## Constraints

- `1 <= words.length <= 10^5`
- `1 <= words[i].length <= 16`
- `words[i]` consists of lowercase English letters only

## Topics/Tags

`hash-table` `string` `sorting` `trie` `bit-manipulation` `frequency-counting` `dominance`

## Related Problems

- [LeetCode 49: Group Anagrams](https://leetcode.com/problems/group-anagrams/)
- [LeetCode 242: Valid Anagram](https://leetcode.com/problems/valid-anagram/)
- [LeetCode 438: Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/)
- [LeetCode 218: The Skyline Problem](https://leetcode.com/problems/the-skyline-problem/) (Similar dominance concept)

## Solution Approaches

| Approach | Time Complexity | Space Complexity | Description |
|----------|----------------|------------------|-------------|
| **Brute Force** | O(n² · m) | O(n · m) | Check every word against every other word |
| **Frequency Vectors + Pairwise** | O(g² · 26) | O(g · 26) | Group by frequency, check pairwise dominance |
| **Bitset Indexing** | O(g · 26 · L) | O(26 · L · g/64) | Optimal approach using bitset indexing |

*Where n = number of words, m = max word length, g = unique frequency vectors, L = max letter frequency*

## Quick Start

### Kotlin
```bash
# Run individual solutions
kotlin solutions/kotlin/Solution01_BruteForce.main.kts
kotlin solutions/kotlin/Solution02_Pairwise.main.kts
kotlin solutions/kotlin/Solution03_Bitset.main.kts
```

### Python
```bash
# Run individual solutions
python solutions/python/solution_01_brute_force.py
python solutions/python/solution_02_pairwise.py
python solutions/python/solution_03_bitset.py
```

### Rust
```bash
# Run all solutions with tests
cd solutions/rust
cargo test
cargo run
```

## How to Run Tests

All solutions include comprehensive test suites using the test cases from `testcases/cases.json`:

1. **26 carefully crafted test cases** covering edge cases, anagrams, sub-anagrams, and mixed scenarios
2. **Consistent output format** across all languages
3. **Self-contained test runners** - each solution file can be run independently

### Test Categories
- **Basic** (5 cases): Simple examples and fundamental scenarios
- **Edge** (7 cases): Empty lists, single words, identical words, max constraints
- **Anagram** (5 cases): Various anagram detection scenarios
- **Sub-anagram** (4 cases): Dominance relationships and chains
- **Mixed** (4 cases): Combination of anagrams and dominance
- **Additional edge case** (1 case): Empty string handling

## Performance Comparison

Run benchmarks to compare all approaches:

```bash
python benchmarks/benchmark.py
```

Sample results on various input sizes:

| Input Size | Brute Force | Pairwise | Bitset | Winner |
|------------|-------------|----------|--------|---------|
| 1,000 words | 150ms | 45ms | 12ms | **Bitset** |
| 10,000 words | 15s | 1.2s | 85ms | **Bitset** |
| 100,000 words | >10min | 2min | 950ms | **Bitset** |

## Algorithm Details

### 1. Brute Force Approach
- **Intuition**: Check every word against every other word
- **Time**: O(n² · m) - For each of n words, check against all other words
- **Space**: O(n · m) - Store all words and their sorted versions
- **Use case**: Small inputs, educational purposes

### 2. Frequency Vectors + Pairwise
- **Intuition**: Group words by character frequency, then check dominance between groups
- **Time**: O(g² · 26) - g unique frequency vectors, 26 letters to compare
- **Space**: O(g · 26) - Store frequency vectors
- **Use case**: Medium inputs with many duplicate frequency patterns

### 3. Bitset Indexing (Optimal)
- **Intuition**: Index words by (letter, count) pairs using bitsets for fast dominance checking
- **Time**: O(g · 26 · L) - For each vector, check against indexed positions
- **Space**: O(26 · L · g/64) - Bitsets for each (letter, count) pair
- **Use case**: Large inputs, production systems

## Project Structure

```
remove-anagrams-and-subanagrams/
├── README.md                    # This file
├── LICENSE                      # MIT License
├── .gitignore                   # Git ignore rules
├── SOLUTION_GUIDE.md            # Detailed algorithm explanations
├── testcases/
│   └── cases.json              # 26 comprehensive test cases
├── solutions/
│   ├── kotlin/                 # Kotlin implementations
│   ├── python/                 # Python implementations
│   ├── rust/                   # Rust implementations
├── visualizations/
│   ├── BITSET_EXPLAINED.md     # Visual explanation of bitset approach
│   └── diagrams/               # Algorithm flow diagrams
└── benchmarks/
    ├── benchmark.py            # Performance comparison script
    └── results.md              # Benchmark results
```

## Key Insights

1. **Anagram Detection**: Words with the same character frequency are anagrams
2. **Dominance Relationship**: Word A dominates B if A contains all letters of B with ≥ frequency
3. **Optimization**: Group by frequency vectors to reduce comparisons
4. **Bit Manipulation**: Use bitsets to represent sets of candidate dominators efficiently
5. **Sorting Strategy**: Process longer words first to maximize early termination

## Contributing

Contributions are welcome! Please feel free to submit pull requests for:
- Additional language implementations
- Performance optimizations
- More comprehensive test cases
- Documentation improvements

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Educational Note**: This problem demonstrates important algorithmic concepts including:
- Hash table operations and collision handling
- String processing and character frequency analysis
- Dominance relationships (similar to Pareto optimality)
- Bit manipulation for set operations
- Trade-offs between time and space complexity