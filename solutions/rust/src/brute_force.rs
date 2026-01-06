use std::collections::HashMap;

fn is_anagram(word1: &str, word2: &str) -> bool {
    let mut chars1: Vec<char> = word1.chars().collect();
    let mut chars2: Vec<char> = word2.chars().collect();
    chars1.sort();
    chars2.sort();
    chars1 == chars2
}

fn is_sub_anagram(smaller: &str, larger: &str) -> bool {
    if smaller.len() >= larger.len() {
        return false;
    }

    let mut smaller_counts = HashMap::new();
    let mut larger_counts = HashMap::new();

    for ch in smaller.chars() {
        *smaller_counts.entry(ch).or_insert(0) += 1;
    }

    for ch in larger.chars() {
        *larger_counts.entry(ch).or_insert(0) += 1;
    }

    for (ch, count) in smaller_counts {
        if larger_counts.get(&ch).unwrap_or(&0) < &count {
            return false;
        }
    }

    true
}

pub fn remove_anagrams_and_sub_anagrams_brute_force(words: &[String]) -> Vec<String> {
    if words.is_empty() {
        return Vec::new();
    }

    let mut to_remove = std::collections::HashSet::new();

    for i in 0..words.len() {
        for j in 0..words.len() {
            if i != j {
                if is_anagram(&words[i], &words[j]) {
                    to_remove.insert(i);
                    to_remove.insert(j);
                } else if is_sub_anagram(&words[i], &words[j]) {
                    to_remove.insert(i);
                }
            }
        }
    }

    words
        .iter()
        .enumerate()
        .filter_map(|(index, word)| {
            if !to_remove.contains(&index) {
                Some(word.clone())
            } else {
                None
            }
        })
        .collect()
}