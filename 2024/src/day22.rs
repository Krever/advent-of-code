use std::collections::HashMap;

pub fn main() {
    let input = include_str!("day22.txt");
    let buyers = parse_input(input);

    // Part 1
    let total_sum: u64 = buyers
        .iter()
        .map(|&secret| simulate_secret(secret, 2000))
        .sum();
    println!("Part 1: {}", total_sum);

    // Part 2
    let caches: Vec<_> = buyers.iter().map(|&secret| build_buyer_cache(secret)).collect();
    let (best_sequence, max_bananas) = find_best_sequence(&caches);

    println!(
        "Part 2: Best sequence {:?} gives the most bananas: {}",
        best_sequence, max_bananas
    );
}


/// Simulate a buyer's sequence of secret numbers and map them to prices
fn simulate_secret_prices(mut secret: u64, steps: usize) -> Vec<u8> {
    const MOD: u64 = 16777216; // 2^24
    let mut prices = Vec::with_capacity(steps);

    for _ in 0..steps {
        secret = (secret ^ (secret * 64)) % MOD;  // Step 1: Multiply by 64 and XOR
        secret = (secret ^ (secret / 32)) % MOD; // Step 2: Divide by 32 and XOR
        secret = (secret ^ (secret * 2048)) % MOD; // Step 3: Multiply by 2048 and XOR

        prices.push((secret % 10) as u8); // Get the ones digit as price
    }

    prices
}

/// Calculate price changes from a sequence of prices
fn calculate_changes(prices: &[u8]) -> Vec<i8> {
    prices
        .windows(2)
        .map(|w| w[1] as i8 - w[0] as i8)
        .collect()
}

/// Build a cache for a single buyer mapping 4-price-change sequences to the first occurrence of bananas
fn build_buyer_cache(secret: u64) -> HashMap<[i8; 4], u8> {
    let prices = simulate_secret_prices(secret, 2000);
    let changes = calculate_changes(&prices);

    let mut cache = HashMap::new();

    for i in 0..changes.len().saturating_sub(3) {
        let sequence = [
            changes[i],
            changes[i + 1],
            changes[i + 2],
            changes[i + 3],
        ];

        // Store the first occurrence of each sequence
        cache.entry(sequence).or_insert(prices[i + 4]);
    }

    cache
}

/// Find the best sequence by aggregating across all buyer caches
fn find_best_sequence(caches: &[HashMap<[i8; 4], u8>]) -> (Vec<i8>, u64) {
    let mut aggregate = HashMap::new();

    for cache in caches {
        for (sequence, &bananas) in cache {
            *aggregate.entry(*sequence).or_insert(0) += bananas as u64;
        }
    }

    // Find the sequence with the maximum bananas
    let (best_sequence, max_bananas) = aggregate
        .into_iter()
        .max_by_key(|&(_, bananas)| bananas)
        .unwrap_or(([0, 0, 0, 0], 0));

    (best_sequence.to_vec(), max_bananas)
}

/// Parse input into initial secret numbers
fn parse_input(input: &str) -> Vec<u64> {
    input
        .lines()
        .filter_map(|line| line.trim().parse::<u64>().ok())
        .collect()
}

/// Simulate the nth secret number directly
fn simulate_secret(mut secret: u64, steps: usize) -> u64 {
    const MOD: u64 = 16777216; // 2^24

    for _ in 0..steps {
        secret = (secret ^ (secret * 64)) % MOD;  // Step 1: Multiply by 64 and XOR
        secret = (secret ^ (secret / 32)) % MOD; // Step 2: Divide by 32 and XOR
        secret = (secret ^ (secret * 2048)) % MOD; // Step 3: Multiply by 2048 and XOR
    }

    secret
}