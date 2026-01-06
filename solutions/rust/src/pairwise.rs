use std::collections::HashMap;

fn get_frequency_vector(word: &str) -> [i32; 26] {
    let mut freq = [0; 26];
    for ch in word.chars() {
        let index = (ch as u8 - b'a') as usize;
        freq[index] += 1;
    }
    freq
}

fn is_dominated_by(smaller: &[i32; 26], larger: &[i32; 26]) -> bool {
    let smaller_sum: i32 = smaller.iter().sum();
    let larger_sum: i32 = larger.iter().sum();

    if smaller_sum >= larger_sum {
        return false;
    }

    for i in 0..26 {
        if smaller[i] > larger[i] {
            return false;
        }
    }

    true
}

pub fn remove_anagrams_and_sub_anagrams_pairwise(words: &[String]) -> Vec<String> {
    if words.is_empty() {
        return Vec::new();
    }

    // Group words by frequency vector
    let mut grouped_by_freq: HashMap<[i32; 26], Vec<String>> = HashMap::new();
    for word in words {
        let freq = get_frequency_vector(word);
        grouped_by_freq
            .entry(freq)
            .or_insert_with(Vec::new)
            .push(word.clone());
    }

    // Keep only groups with single words (no anagrams)
    let mut unique_groups: Vec<([i32; 26], String)> = Vec::new();
    for (freq, word_list) in grouped_by_freq {
        if word_list.len() == 1 {
            unique_groups.push((freq, word_list[0].clone()));
        }
    }

    // Find maximal (non-dominated) frequency vectors
    let mut maximal_groups = Vec::new();
    for (candidate_freq, candidate_word) in &unique_groups {
        let mut is_dominated = false;

        for (other_freq, _) in &unique_groups {
            if candidate_freq != other_freq && is_dominated_by(candidate_freq, other_freq) {
                is_dominated = true;
                break;
            }
        }

        if !is_dominated {
            maximal_groups.push(candidate_word.clone());
        }
    }

    maximal_groups
}