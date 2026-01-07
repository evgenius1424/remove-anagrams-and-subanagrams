# Benchmark Results

**Generated:** 2026-01-07 20:18:18

## Summary

| Input Size | Brute Force | Pairwise | Bitset | Winner |
|------------|-------------|----------|--------|--------|
| 100 | 1.5ms | 1.8ms | 254.7μs | Bitset |
| 500 | 20.7ms | 23.1ms | 1.2ms | Bitset |
| 1,000 | 81.2ms | 90.2ms | 2.8ms | Bitset |
| 2,000 | 303.8ms | 349.5ms | 7.0ms | Bitset |
| 5,000 | 1.44s | 1.57s | 24.0ms | Bitset |

## Details

### Small (100 words)

- **Brute Force**: 1.5ms (62 words)
- **Pairwise**: 1.8ms (62 words)
- **Bitset**: 254.7μs (62 words)

### Medium (500 words)

- **Brute Force**: 20.7ms (231 words)
- **Pairwise**: 23.1ms (231 words)
- **Bitset**: 1.2ms (231 words)

### Large (1K words)

- **Brute Force**: 81.2ms (473 words)
- **Pairwise**: 90.2ms (473 words)
- **Bitset**: 2.8ms (473 words)

### Very Large (2K words)

- **Brute Force**: 303.8ms (921 words)
- **Pairwise**: 349.5ms (921 words)
- **Bitset**: 7.0ms (921 words)

### Extra Large (5K words)

- **Brute Force**: 1.44s (1942 words)
- **Pairwise**: 1.57s (1942 words)
- **Bitset**: 24.0ms (1942 words)

