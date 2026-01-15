import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT))

from solutions.python.solution_01_brute_force import removeAnagramsAndSubAnagrams as solution01
from solutions.python.solution_02_pairwise import removeAnagramsAndSubAnagrams as solution02
from solutions.python.solution_03_bitset import removeAnagramsAndSubAnagrams as solution03

SOLUTIONS = [
    ("BruteForce", solution01),
    ("Pairwise", solution02),
    ("Bitset", solution03),
]


def load_test_cases():
    cases_path = ROOT / "testcases" / "cases.json"
    with cases_path.open(encoding="utf-8") as f:
        data = json.load(f)
    return [
        {
            "id": case["id"],
            "category": case["category"],
            "input": case["input"],
            "expected": set(case["expected"]),
            "explanation": case["explanation"],
        }
        for case in data["test_cases"]
    ]


def run_solution(name, solver, test_cases):
    print(f"Running {len(test_cases)} Python tests for {name}...")
    failed = 0

    for tc in test_cases:
        result = set(solver(tc["input"]))
        if result == tc["expected"]:
            print(f"OK   Test {tc['id']}: {tc['category']} - {tc['explanation']}")
        else:
            print(f"FAIL Test {tc['id']}: {tc['category']} - {tc['explanation']}")
            print(f"  Input:    {tc['input']}")
            print(f"  Expected: {tc['expected']}")
            print(f"  Got:      {result}")
            failed += 1

    passed = len(test_cases) - failed
    print(f"Results for {name}: {passed} passed, {failed} failed\n")
    return failed


def main() -> int:
    test_cases = load_test_cases()
    total_failed = sum(run_solution(name, solver, test_cases) for name, solver in SOLUTIONS)
    return 0 if total_failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())