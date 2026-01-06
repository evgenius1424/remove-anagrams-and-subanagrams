#!/usr/bin/env rust

use serde::{Deserialize, Serialize};
use std::fs;
use std::path::Path;

#[derive(Serialize, Deserialize, Debug)]
struct TestCase {
    input: Vec<String>,
    expected: Vec<String>,
}

#[derive(Serialize, Deserialize, Debug)]
struct TestData {
    test_cases: Vec<TestCase>,
}

fn remove_anagrams(words: Vec<String>) -> Vec<String> {
    // TODO: Implement the solution
    vec![]
}

fn main() {
    // Read test cases from shared file
    let testcases_path = Path::new("../../testcases/cases.json");
    let test_data = match fs::read_to_string(testcases_path) {
        Ok(content) => match serde_json::from_str::<TestData>(&content) {
            Ok(data) => data,
            Err(e) => {
                println!("Error parsing JSON: {}", e);
                return;
            }
        },
        Err(e) => {
            println!("Error reading test cases: {}", e);
            return;
        }
    };

    // Run test cases
    for (i, test_case) in test_data.test_cases.iter().enumerate() {
        let result = remove_anagrams(test_case.input.clone());
        let passed = result == test_case.expected;

        println!("Test case {}: {}", i + 1, if passed { "PASS" } else { "FAIL" });
        if !passed {
            println!("  Input: {:?}", test_case.input);
            println!("  Expected: {:?}", test_case.expected);
            println!("  Got: {:?}", result);
        }
    }
}