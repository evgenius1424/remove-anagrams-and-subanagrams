use serde::{Deserialize, Serialize};
use std::fs;

mod brute_force;
mod pairwise;
mod bitset;

#[derive(Debug, Deserialize, Serialize)]
struct TestCase {
    id: i32,
    category: String,
    input: Vec<String>,
    expected: Vec<String>,
    explanation: String,
}

#[derive(Debug, Deserialize)]
struct TestData {
    test_cases: Vec<TestCase>,
}

fn load_test_cases() -> Vec<TestCase> {
    match fs::read_to_string("../../testcases/cases.json") {
        Ok(content) => {
            match serde_json::from_str::<TestData>(&content) {
                Ok(data) => data.test_cases,
                Err(_) => get_embedded_test_cases(),
            }
        }
        Err(_) => {
            println!("Could not load test cases from JSON, using embedded cases");
            get_embedded_test_cases()
        }
    }
}

fn get_embedded_test_cases() -> Vec<TestCase> {
    vec![
        TestCase {
            id: 1,
            category: "basic".to_string(),
            input: vec!["a".to_string(), "ab".to_string(), "ba".to_string(), "abc".to_string(), "abcd".to_string()],
            expected: vec!["abcd".to_string()],
            explanation: "".to_string(),
        },
        TestCase {
            id: 2,
            category: "basic".to_string(),
            input: vec!["abc".to_string(), "def".to_string(), "ghi".to_string()],
            expected: vec!["abc".to_string(), "def".to_string(), "ghi".to_string()],
            explanation: "".to_string(),
        },
        TestCase {
            id: 3,
            category: "basic".to_string(),
            input: vec!["a".to_string(), "aa".to_string(), "aaa".to_string()],
            expected: vec!["aaa".to_string()],
            explanation: "".to_string(),
        },
        TestCase {
            id: 4,
            category: "basic".to_string(),
            input: vec!["cat".to_string(), "act".to_string(), "dog".to_string()],
            expected: vec!["dog".to_string()],
            explanation: "".to_string(),
        },
        TestCase {
            id: 5,
            category: "basic".to_string(),
            input: vec!["listen".to_string(), "silent".to_string(), "enlist".to_string()],
            expected: vec![],
            explanation: "".to_string(),
        },
    ]
}

fn run_tests<F>(approach_name: &str, complexity: &str, solver: F)
where
    F: Fn(&[String]) -> Vec<String>,
{
    println!("Rust Solution: {}", approach_name);
    println!("Complexity: {}", complexity);
    println!();

    let test_cases = load_test_cases();
    let mut passed = 0;
    let mut failed = 0;

    println!("Running {} tests...", test_cases.len());
    println!();

    for test_case in test_cases {
        let result = solver(&test_case.input);
        let mut result_sorted = result.clone();
        result_sorted.sort();
        let mut expected_sorted = test_case.expected.clone();
        expected_sorted.sort();

        if result_sorted == expected_sorted {
            println!("✓ Test {}: {}", test_case.id, test_case.category);
            passed += 1;
        } else {
            println!("✗ Test {}: {}", test_case.id, test_case.category);
            println!("  Input: {:?}", test_case.input);
            println!("  Expected: {:?}", test_case.expected);
            println!("  Got: {:?}", result);
            failed += 1;
        }
    }

    println!();
    println!("========================================");
    println!("Results: {} passed, {} failed", passed, failed);
    println!();
}

fn main() {
    println!("Testing all Rust implementations:\n");

    run_tests(
        "1. Brute Force",
        "Time: O(n² · m), Space: O(n · m)",
        brute_force::remove_anagrams_and_sub_anagrams_brute_force,
    );

    run_tests(
        "2. Frequency Vectors + Pairwise",
        "Time: O(g² · 26), Space: O(g · 26)",
        pairwise::remove_anagrams_and_sub_anagrams_pairwise,
    );

    run_tests(
        "3. Bitset Indexing",
        "Time: O(g · 26 · L), Space: O(26 · L · g/64)",
        bitset::remove_anagrams_and_sub_anagrams_bitset,
    );
}