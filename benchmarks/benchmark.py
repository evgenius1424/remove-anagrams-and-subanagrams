#!/usr/bin/env python3
"""
Benchmark script for Remove Anagrams and Sub-Anagrams algorithms.

This script generates random word lists of various sizes and measures
the performance of different solution approaches.

Run: python benchmark.py
"""

import time
import random
import string
import json
from pathlib import Path
import sys
import statistics

# Add the parent directory to the path to import our solutions
sys.path.append(str(Path(__file__).parent.parent / "solutions" / "python"))

try:
    from solution_01_brute_force import remove_anagrams_and_sub_anagrams_brute_force
    from solution_02_pairwise import remove_anagrams_and_sub_anagrams_pairwise
    from solution_03_bitset import remove_anagrams_and_sub_anagrams_bitset
except ImportError as e:
    print(f"Error importing solutions: {e}")
    print("Make sure you're running this from the benchmarks directory")
    sys.exit(1)


def generate_random_word(min_length=1, max_length=8):
    """Generate a random word of the specified length range."""
    length = random.randint(min_length, max_length)
    return ''.join(random.choice(string.ascii_lowercase) for _ in range(length))


def generate_test_data(size, word_length_range=(1, 8), anagram_probability=0.1):
    """
    Generate test data with specified characteristics.

    Args:
        size: Number of words to generate
        word_length_range: Tuple of (min_length, max_length) for words
        anagram_probability: Probability of creating anagrams
    """
    words = []

    for _ in range(size):
        if random.random() < anagram_probability and words:
            # Create an anagram of an existing word
            base_word = random.choice(words)
            anagram = ''.join(random.sample(base_word, len(base_word)))
            words.append(anagram)
        else:
            # Create a new random word
            word = generate_random_word(word_length_range[0], word_length_range[1])
            words.append(word)

    return words


def measure_time(func, data, iterations=1):
    """
    Measure execution time of a function.

    Returns:
        Tuple of (average_time_seconds, memory_used_mb)
    """
    times = []

    for _ in range(iterations):
        start_time = time.perf_counter()
        result = func(data)
        end_time = time.perf_counter()
        times.append(end_time - start_time)

    avg_time = statistics.mean(times)
    return avg_time, len(result)  # Return avg time and result size


def format_time(seconds):
    """Format time in a human-readable way."""
    if seconds < 0.001:
        return f"{seconds * 1_000_000:.1f}μs"
    elif seconds < 1:
        return f"{seconds * 1000:.1f}ms"
    else:
        return f"{seconds:.2f}s"


def run_benchmark():
    """Run the complete benchmark suite."""
    print("🚀 Remove Anagrams and Sub-Anagrams - Performance Benchmark")
    print("=" * 70)
    print()

    # Test configurations
    test_configs = [
        {"size": 100, "name": "Small (100 words)", "iterations": 5},
        {"size": 500, "name": "Medium (500 words)", "iterations": 3},
        {"size": 1000, "name": "Large (1K words)", "iterations": 3},
        {"size": 2000, "name": "Very Large (2K words)", "iterations": 2},
        {"size": 5000, "name": "Extra Large (5K words)", "iterations": 1},
    ]

    # Solution configurations
    solutions = [
        {
            "name": "Brute Force",
            "func": remove_anagrams_and_sub_anagrams_brute_force,
            "complexity": "O(n² · m)"
        },
        {
            "name": "Pairwise",
            "func": remove_anagrams_and_sub_anagrams_pairwise,
            "complexity": "O(g² · 26)"
        },
        {
            "name": "Bitset",
            "func": remove_anagrams_and_sub_anagrams_bitset,
            "complexity": "O(g · 26 · L)"
        }
    ]

    results = []

    for config in test_configs:
        print(f"📊 Testing {config['name']}")
        print("-" * 50)

        # Generate test data
        print("  Generating test data...")
        test_data = generate_test_data(config["size"])

        config_results = {
            "size": config["size"],
            "name": config["name"],
            "solutions": {}
        }

        for solution in solutions:
            print(f"  Running {solution['name']} ({solution['complexity']})...")

            try:
                avg_time, result_size = measure_time(
                    solution["func"],
                    test_data,
                    config["iterations"]
                )

                config_results["solutions"][solution["name"]] = {
                    "time": avg_time,
                    "time_formatted": format_time(avg_time),
                    "result_size": result_size,
                    "complexity": solution["complexity"]
                }

                print(f"    ✓ {format_time(avg_time)} (result: {result_size} words)")

                # Skip expensive algorithms for large inputs
                if avg_time > 30 and config["size"] > 1000:
                    print(f"    Skipping {solution['name']} for larger inputs (too slow)")
                    break

            except Exception as e:
                print(f"    ✗ Error: {e}")
                config_results["solutions"][solution["name"]] = {
                    "error": str(e)
                }

        results.append(config_results)
        print()

    # Generate summary table
    print("📈 Performance Summary")
    print("=" * 70)
    print()

    # Table header
    header = "| Input Size | Brute Force | Pairwise | Bitset | Winner |"
    separator = "|" + "-" * 10 + "|" + "-" * 12 + "|" + "-" * 9 + "|" + "-" * 7 + "|" + "-" * 7 + "|"

    print(header)
    print(separator)

    for result in results:
        size_str = f"{result['size']:,} words"

        times = []
        time_strs = []

        for solution_name in ["Brute Force", "Pairwise", "Bitset"]:
            if solution_name in result["solutions"]:
                solution_result = result["solutions"][solution_name]
                if "error" not in solution_result:
                    times.append(solution_result["time"])
                    time_strs.append(solution_result["time_formatted"])
                else:
                    times.append(float('inf'))
                    time_strs.append("ERROR")
            else:
                times.append(float('inf'))
                time_strs.append("N/A")

        # Find the winner (minimum time)
        winner_idx = times.index(min(times))
        winner_names = ["Brute Force", "Pairwise", "Bitset"]
        winner = f"**{winner_names[winner_idx]}**"

        # Format row
        row = f"| {size_str:<8} | {time_strs[0]:<10} | {time_strs[1]:<7} | {time_strs[2]:<5} | {winner:<5} |"
        print(row)

    print()

    # Algorithm analysis
    print("🧠 Algorithm Analysis")
    print("=" * 70)
    print()

    analysis = [
        "**Brute Force**: Simple but inefficient. Good for educational purposes.",
        "  - Checks every word against every other word",
        "  - Time complexity: O(n² · m) where n=words, m=max_length",
        "  - Becomes impractical for large inputs (>1000 words)",
        "",
        "**Pairwise**: Efficient for moderate inputs with frequency grouping.",
        "  - Groups words by character frequency to detect anagrams",
        "  - Checks dominance between unique frequency vectors",
        "  - Time complexity: O(g² · 26) where g=unique frequency vectors",
        "  - Good balance between simplicity and performance",
        "",
        "**Bitset**: Optimal approach using advanced indexing.",
        "  - Uses bitset indexing for fast dominance checking",
        "  - Processes words in descending length order",
        "  - Time complexity: O(g · 26 · L) where L=max letter frequency",
        "  - Best for large inputs and production systems",
        "",
        "**Key Insights**:",
        "• Performance gains increase dramatically with input size",
        "• Bitset approach scales best due to efficient indexing",
        "• Algorithm choice depends on input size and performance requirements"
    ]

    for line in analysis:
        print(line)

    print()

    # Save detailed results
    results_file = Path(__file__).parent / "results.md"
    save_results_to_file(results, results_file)
    print(f"💾 Detailed results saved to: {results_file}")


def save_results_to_file(results, filepath):
    """Save benchmark results to a markdown file."""
    with open(filepath, 'w') as f:
        f.write("# Benchmark Results\n\n")
        f.write("Performance comparison of different algorithms for the Remove Anagrams and Sub-Anagrams problem.\n\n")
        f.write(f"**Generated:** {time.strftime('%Y-%m-%d %H:%M:%S')}\n\n")

        f.write("## Summary Table\n\n")
        f.write("| Input Size | Brute Force | Pairwise | Bitset | Winner |\n")
        f.write("|------------|-------------|----------|--------|--------|\n")

        for result in results:
            size_str = f"{result['size']:,} words"

            times = []
            time_strs = []

            for solution_name in ["Brute Force", "Pairwise", "Bitset"]:
                if solution_name in result["solutions"]:
                    solution_result = result["solutions"][solution_name]
                    if "error" not in solution_result:
                        times.append(solution_result["time"])
                        time_strs.append(solution_result["time_formatted"])
                    else:
                        times.append(float('inf'))
                        time_strs.append("ERROR")
                else:
                    times.append(float('inf'))
                    time_strs.append("N/A")

            # Find winner
            winner_idx = times.index(min(times))
            winner_names = ["Brute Force", "Pairwise", "Bitset"]
            winner = f"**{winner_names[winner_idx]}**"

            f.write(f"| {size_str} | {time_strs[0]} | {time_strs[1]} | {time_strs[2]} | {winner} |\n")

        f.write("\n## Detailed Results\n\n")

        for result in results:
            f.write(f"### {result['name']}\n\n")

            for solution_name, solution_result in result["solutions"].items():
                f.write(f"**{solution_name}** ({solution_result.get('complexity', 'N/A')})\n")
                if "error" not in solution_result:
                    f.write(f"- Time: {solution_result['time_formatted']}\n")
                    f.write(f"- Result size: {solution_result['result_size']} words\n")
                else:
                    f.write(f"- Error: {solution_result['error']}\n")
                f.write("\n")

        f.write("## Test Configuration\n\n")
        f.write("- **Word length**: 1-8 characters\n")
        f.write("- **Character set**: Lowercase English letters (a-z)\n")
        f.write("- **Anagram probability**: 10% chance of creating anagrams\n")
        f.write("- **Multiple runs**: Averaged over multiple iterations for accuracy\n")


if __name__ == "__main__":
    try:
        run_benchmark()
    except KeyboardInterrupt:
        print("\n\n⏹️  Benchmark interrupted by user")
    except Exception as e:
        print(f"\n\n❌ Error running benchmark: {e}")
        import traceback
        traceback.print_exc()