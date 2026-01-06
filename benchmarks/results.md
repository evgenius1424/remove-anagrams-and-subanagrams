# Benchmark Results

Performance comparison of different algorithms for the Remove Anagrams and Sub-Anagrams problem.

**Note:** Run `python benchmark.py` to generate actual performance data for your system.

## Expected Performance Characteristics

Based on algorithmic complexity analysis, here are the expected performance patterns:

| Input Size | Brute Force | Pairwise | Bitset | Expected Winner |
|------------|-------------|----------|--------|-----------------|
| 100 words | ~10ms | ~3ms | ~1ms | **Bitset** |
| 500 words | ~150ms | ~15ms | ~4ms | **Bitset** |
| 1,000 words | ~600ms | ~45ms | ~12ms | **Bitset** |
| 5,000 words | ~15s | ~800ms | ~80ms | **Bitset** |
| 10,000 words | >60s | ~3s | ~200ms | **Bitset** |

## Algorithm Complexity Analysis

### 1. Brute Force - O(n² · m)
- **Best case**: All words are unique with no relationships
- **Worst case**: Many anagrams and sub-anagrams requiring extensive comparisons
- **Scaling**: Quadratic growth makes it impractical for large inputs
- **Memory**: Linear in input size

### 2. Frequency Vectors + Pairwise - O(g² · 26)
- **Best case**: Few unique frequency vectors (many exact duplicates)
- **Worst case**: All words have unique frequency vectors
- **Scaling**: Much better than brute force, but still quadratic in unique patterns
- **Memory**: Linear in unique frequency vectors

### 3. Bitset Indexing - O(g · 26 · L)
- **Best case**: Efficient early termination with good bit parallelism
- **Worst case**: Many frequency vectors with complex dominance relationships
- **Scaling**: Near-linear in practice due to efficient indexing
- **Memory**: Higher constant factor but same asymptotic complexity

## Performance Factors

Several factors affect real-world performance:

1. **Input Characteristics**:
   - Word length distribution
   - Number of anagrams vs. unique words
   - Frequency of sub-anagram relationships

2. **Implementation Details**:
   - Hash table performance for frequency grouping
   - Bit manipulation efficiency
   - Memory allocation patterns

3. **System Factors**:
   - CPU cache behavior
   - Memory bandwidth
   - Compiler optimizations

## Recommendations

- **Small inputs (< 1,000 words)**: Any approach works, brute force is simplest
- **Medium inputs (1,000 - 10,000 words)**: Pairwise approach offers good balance
- **Large inputs (> 10,000 words)**: Bitset indexing is essential for reasonable performance
- **Production systems**: Always use bitset approach for scalability

## Running Benchmarks

To generate actual performance data for your system:

```bash
cd benchmarks
python benchmark.py
```

This will:
- Generate random test data of various sizes
- Run all three algorithms with timing measurements
- Create detailed performance comparisons
- Update this file with actual results

The benchmark script uses statistical averaging over multiple runs to ensure accurate measurements and accounts for system variability.