use std::collections::{HashMap, HashSet};

fn get_frequency_vector(word: &str) -> [i32; 26] {
    let mut freq = [0; 26];
    for ch in word.chars() {
        let index = (ch as u8 - b'a') as usize;
        freq[index] += 1;
    }
    freq
}

pub fn remove_anagrams_and_sub_anagrams_bitset(words: &[String]) -> Vec<String> {
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

    // Sort by total character count (descending) for optimal processing
    unique_groups.sort_by(|a, b| {
        let sum_a: i32 = a.0.iter().sum();
        let sum_b: i32 = b.0.iter().sum();
        sum_b.cmp(&sum_a)
    });

    // Bitset index: bitsets[char][count] = set of indices
    let mut bitsets: [[HashSet<usize>; 17]; 26] = Default::default();
    let mut result = Vec::new();

    for (index, (freq, word)) in unique_groups.iter().enumerate() {
        let mut is_dominated = false;

        // Check if this frequency vector is dominated
        for char in 0..26 {
            let count = freq[char] as usize;
            // Check if any existing vector has more of this character
            // and also dominates in all other characters
            for higher_count in (count + 1)..17 {
                if !bitsets[char][higher_count].is_empty() {
                    // Check if any of these candidates dominates our frequency
                    for &candidate_index in &bitsets[char][higher_count] {
                        let candidate_freq = &unique_groups[candidate_index].0;
                        // Check dominance: candidate must have >= count for all chars
                        let mut dominates = true;
                        for other_char in 0..26 {
                            if candidate_freq[other_char] < freq[other_char] {
                                dominates = false;
                                break;
                            }
                        }
                        if dominates {
                            is_dominated = true;
                            break;
                        }
                    }
                    if is_dominated {
                        break;
                    }
                }
            }
            if is_dominated {
                break;
            }
        }

        if !is_dominated {
            // Add this vector to the result
            result.push(word.clone());
            // Index this vector in bitsets
            for char in 0..26 {
                let count = freq[char] as usize;
                if count > 0 && count < 17 {
                    bitsets[char][count].insert(index);
                }
            }
        }
    }

    result
}