use std::collections::HashMap;

pub fn main() {
    let input = include_str!("day11.txt");
    let stones: Vec<&str> = input.split_whitespace().collect();
    println!("Part 1: {}", go(&stones, 25));
    println!("Part 2: {}", go(&stones, 75));
}

type Cache = HashMap<(String, usize), usize>;

fn go(stones: &[&str], iters: usize) -> usize {
    let mut cache: Cache = HashMap::new();
    stones
        .iter()
        .map(|&stone| blink(stone.to_string(), iters, &mut cache))
        .sum()
}

fn blink(stone: String, remaining: usize, cache: &mut Cache) -> usize {
    if remaining == 0 {
        return 1;
    }

    if let Some(&cached_result) = cache.get(&(stone.clone(), remaining)) {
        return cached_result;
    }

    let blinked: Vec<String> = match stone.as_str() {
        "0" => vec!["1".to_string()],
        x if x.len() % 2 == 0 => {
            let mid = x.len() / 2;
            vec![
                trim_trailing_zeros(&x[..mid]),
                trim_trailing_zeros(&x[mid..]),
            ]
        }
        x => vec![(x.parse::<u64>().unwrap_or(0) * 2024).to_string()],
    };

    let result = blinked
        .into_iter()
        .map(|next| blink(next, remaining - 1, cache))
        .sum();

    cache.insert((stone, remaining), result);
    result
}

fn trim_trailing_zeros(s: &str) -> String {
    let trimmed = s.trim_start_matches('0');
    if trimmed.is_empty() {
        "0".to_string() // If trimming results in empty, return "0"
    } else {
        trimmed.to_string()
    }
}
