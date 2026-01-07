#!/usr/bin/env python3

import time
import random
import string
import statistics
from pathlib import Path
import sys

sys.path.append(str(Path(__file__).parent.parent / "solutions" / "python"))

try:
    from solution_01_brute_force import remove_anagrams_and_sub_anagrams as brute_force
    from solution_02_pairwise import remove_anagrams_and_sub_anagrams as pairwise
    from solution_03_bitset import remove_anagrams_and_sub_anagrams as bitset
except ImportError as e:
    print(f"Error importing solutions: {e}")
    print("Make sure you're running this from the benchmarks directory")
    sys.exit(1)

SOLUTIONS = [
    {"name": "Brute Force", "func": brute_force, "complexity": "O(n² · m)"},
    {"name": "Pairwise", "func": pairwise, "complexity": "O(g² · 26)"},
    {"name": "Bitset", "func": bitset, "complexity": "O(g · 26 · L)"},
]

TEST_CONFIGS = [
    {"size": 100, "name": "Small (100 words)", "iterations": 5},
    {"size": 500, "name": "Medium (500 words)", "iterations": 3},
    {"size": 1000, "name": "Large (1K words)", "iterations": 3},
    {"size": 2000, "name": "Very Large (2K words)", "iterations": 2},
    {"size": 5000, "name": "Extra Large (5K words)", "iterations": 1},
]


def run_benchmark():
    print("🚀 Remove Anagrams and Sub-Anagrams - Performance Benchmark")
    print("=" * 70)
    print()

    results = []

    for config in TEST_CONFIGS:
        print(f"📊 Testing {config['name']}")
        print("-" * 50)

        print("  Generating test data...")
        test_data = generate_test_data(config["size"])
        stats = analyze_input(test_data)
        print(f"  Input: {stats['total']} words, {stats['unique']} unique, "
              f"{stats['anagram_groups']} anagram groups ({stats['anagram_words']} words)")

        config_results = {
            "size": config["size"],
            "name": config["name"],
            "input_stats": stats,
            "solutions": {},
        }

        for solution in SOLUTIONS:
            print(f"  Running {solution['name']} ({solution['complexity']})...")

            try:
                avg_time, result = measure_time(solution["func"], test_data, config["iterations"])
                removal = analyze_removal(test_data, result)

                config_results["solutions"][solution["name"]] = {
                    "time": avg_time,
                    "time_formatted": format_time(avg_time),
                    "result_size": len(result),
                    "removed_anagrams": removal["removed_anagrams"],
                    "removed_subs": removal["removed_subs"],
                    "complexity": solution["complexity"],
                }

                print(f"    ✓ {format_time(avg_time)} → {len(result)} words "
                      f"(removed: {removal['removed_anagrams']} anagrams, {removal['removed_subs']} sub-anagrams)")

                if avg_time > 30 and config["size"] > 1000:
                    print(f"    Skipping {solution['name']} for larger inputs (too slow)")
                    break

            except Exception as e:
                print(f"    ✗ Error: {e}")
                config_results["solutions"][solution["name"]] = {"error": str(e)}

        results.append(config_results)
        print()

    print_summary(results)
    print_analysis()

    results_file = Path(__file__).parent / "results.md"
    save_results(results, results_file)
    print(f"💾 Detailed results saved to: {results_file}")


def generate_test_data(size, word_length_range=(1, 8), anagram_probability=0.1):
    words = []
    for _ in range(size):
        if random.random() < anagram_probability and words:
            base_word = random.choice(words)
            anagram = ''.join(random.sample(base_word, len(base_word)))
            words.append(anagram)
        else:
            length = random.randint(word_length_range[0], word_length_range[1])
            word = ''.join(random.choice(string.ascii_lowercase) for _ in range(length))
            words.append(word)
    return words


def analyze_input(words):
    freq_groups = {}
    for word in words:
        key = tuple(sorted(word))
        freq_groups.setdefault(key, []).append(word)

    anagram_groups = sum(1 for g in freq_groups.values() if len(g) > 1)
    anagram_words = sum(len(g) for g in freq_groups.values() if len(g) > 1)
    unique_words = len(words) - anagram_words

    return {
        "total": len(words),
        "unique": unique_words,
        "anagram_groups": anagram_groups,
        "anagram_words": anagram_words,
    }


def measure_time(func, data, iterations=1):
    times = []
    result = None
    for _ in range(iterations):
        start = time.perf_counter()
        result = func(data)
        end = time.perf_counter()
        times.append(end - start)
    return statistics.mean(times), result


def analyze_removal(original, result):
    result_set = set(result)
    removed = [w for w in original if w not in result_set]

    freq_groups = {}
    for word in original:
        key = tuple(sorted(word))
        freq_groups.setdefault(key, []).append(word)

    removed_as_anagram = 0
    removed_as_sub = 0

    for word in removed:
        key = tuple(sorted(word))
        if len(freq_groups[key]) > 1:
            removed_as_anagram += 1
        else:
            removed_as_sub += 1

    return {"removed_anagrams": removed_as_anagram, "removed_subs": removed_as_sub}


def format_time(seconds):
    if seconds < 0.001:
        return f"{seconds * 1_000_000:.1f}μs"
    elif seconds < 1:
        return f"{seconds * 1000:.1f}ms"
    else:
        return f"{seconds:.2f}s"


def get_solution_times(result):
    times = []
    time_strs = []
    for name in ["Brute Force", "Pairwise", "Bitset"]:
        if name in result["solutions"] and "error" not in result["solutions"][name]:
            times.append(result["solutions"][name]["time"])
            time_strs.append(result["solutions"][name]["time_formatted"])
        else:
            times.append(float('inf'))
            time_strs.append("N/A" if name not in result["solutions"] else "ERROR")
    return times, time_strs


def print_summary(results):
    print("📈 Performance Summary")
    print("=" * 70)
    print()
    print("| Input Size | Brute Force | Pairwise | Bitset | Winner |")
    print("|" + "-" * 12 + "|" + "-" * 13 + "|" + "-" * 10 + "|" + "-" * 8 + "|" + "-" * 8 + "|")

    for result in results:
        size_str = f"{result['size']:,}"
        times, time_strs = get_solution_times(result)
        winner_idx = times.index(min(times))
        winner = ["Brute", "Pairwise", "Bitset"][winner_idx]
        print(f"| {size_str:>10} | {time_strs[0]:>11} | {time_strs[1]:>8} | {time_strs[2]:>6} | {winner:>6} |")

    print()


def print_analysis():
    print("🧠 Algorithm Analysis")
    print("=" * 70)
    print()
    print("Brute Force: O(n² · m) - Simple, checks every pair")
    print("Pairwise:    O(g² · 26) - Groups by frequency, checks unique vectors")
    print("Bitset:      O(g · 26 · L) - Bitset indexing, optimal for large inputs")
    print()


def save_results(results, filepath):
    with open(filepath, 'w') as f:
        f.write("# Benchmark Results\n\n")
        f.write(f"**Generated:** {time.strftime('%Y-%m-%d %H:%M:%S')}\n\n")

        f.write("## Summary\n\n")
        f.write("| Input Size | Brute Force | Pairwise | Bitset | Winner |\n")
        f.write("|------------|-------------|----------|--------|--------|\n")

        for result in results:
            times, time_strs = get_solution_times(result)
            winner_idx = times.index(min(times))
            winner = ["Brute Force", "Pairwise", "Bitset"][winner_idx]
            f.write(f"| {result['size']:,} | {time_strs[0]} | {time_strs[1]} | {time_strs[2]} | {winner} |\n")

        f.write("\n## Details\n\n")
        for result in results:
            f.write(f"### {result['name']}\n\n")
            for name, data in result["solutions"].items():
                if "error" not in data:
                    f.write(f"- **{name}**: {data['time_formatted']} ({data['result_size']} words)\n")
                else:
                    f.write(f"- **{name}**: Error - {data['error']}\n")
            f.write("\n")


if __name__ == "__main__":
    try:
        run_benchmark()
    except KeyboardInterrupt:
        print("\n\n⏹️  Benchmark interrupted")
    except Exception as e:
        print(f"\n\n❌ Error: {e}")
        import traceback
        traceback.print_exc()