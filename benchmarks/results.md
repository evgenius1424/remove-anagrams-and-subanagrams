# Benchmark Results

**Generated:** 2026-01-15 20:35:45

## Summary

| Input Size | Brute Force | Pairwise | Bitset | Winner |
|------------|-------------|----------|--------|--------|
| 100 | 1.1ms | 1.2ms | 225.9μs | Bitset |
| 500 | 23.3ms | 25.4ms | 1.2ms | Bitset |
| 1,000 | 79.1ms | 84.3ms | 2.9ms | Bitset |
| 2,000 | 265.4ms | 290.1ms | 6.7ms | Bitset |
| 5,000 | 1.57s | 1.72s | 26.4ms | Bitset |

## Details

### Small (100 words)

- **Brute Force**: 1.1ms (53 words)
- **Pairwise**: 1.2ms (53 words)
- **Bitset**: 225.9μs (53 words)

### Medium (500 words)

- **Brute Force**: 23.3ms (251 words)
- **Pairwise**: 25.4ms (251 words)
- **Bitset**: 1.2ms (251 words)

### Large (1K words)

- **Brute Force**: 79.1ms (483 words)
- **Pairwise**: 84.3ms (483 words)
- **Bitset**: 2.9ms (483 words)

### Very Large (2K words)

- **Brute Force**: 265.4ms (842 words)
- **Pairwise**: 290.1ms (842 words)
- **Bitset**: 6.7ms (842 words)

### Extra Large (5K words)

- **Brute Force**: 1.57s (2030 words)
- **Pairwise**: 1.72s (2030 words)
- **Bitset**: 26.4ms (2030 words)

