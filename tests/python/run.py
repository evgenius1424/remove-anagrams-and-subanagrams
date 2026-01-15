import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT))

from solutions.python.solution_01_brute_force import remove_anagrams_and_sub_anagrams as solution01
from solutions.python.solution_02_pairwise import remove_anagrams_and_sub_anagrams as solution02
from solutions.python.solution_03_bitset import remove_anagrams_and_sub_anagrams as solution03


def load_test_cases():
    cases_path = ROOT / "testcases" / "cases.json"
    with cases_path.open("r", encoding="utf-8") as handle:
        data = json.load(handle)

    test_cases = []
    for case in data["test_cases"]:
        test_cases.append(
            {
                "id": case["id"],
                "category": case["category"],
                "input": case["input"],
                "expected": set(case["expected"]),
                "explanation": case["explanation"],
            }
        )
    return test_cases


def main() -> int:
    test_cases = load_test_cases()
    print(f"Running {len(test_cases)} Python tests...")

    passed = 0
    failed = 0

    solutions = [
        ("solution01", solution01),
        ("solution02", solution02),
        ("solution03", solution03),
    ]

    total_failed = 0
    for name, solver in solutions:
        print(f"Running {len(test_cases)} Python tests for {name}...")
        passed = 0
        failed = 0

        for tc in test_cases:
            result = set(solver(tc["input"]))
            success = result == tc["expected"]

            if success:
                print(f"OK  Test {tc['id']}: {tc['category']} - {tc['explanation']}")
                passed += 1
            else:
                print(f"FAIL Test {tc['id']}: {tc['category']} - {tc['explanation']}")
                print(f"  Input:    {tc['input']}")
                print(f"  Expected: {tc['expected']}")
                print(f"  Got:      {result}")
                failed += 1

        print(f"Results for {name}: {passed} passed, {failed} failed")
        total_failed += failed

    return 0 if total_failed == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main())
