pub fn main() {
    let input = include_str!("day19.txt");
    let (patterns_input, designs_input) = input.split_once("\n\n").unwrap();

    let towel_patterns: Vec<&str> = patterns_input.split(',').map(|s| s.trim()).collect();
    let designs: Vec<&str> = designs_input.lines().map(|s| s.trim()).collect();

    part1(&towel_patterns, &designs);
    part2(&towel_patterns, &designs);
}

fn part1(patterns: &[&str], designs: &[&str]) {
    // could be faster if we shortcircuit instead of counting
    let possible_count = designs
        .iter()
        .filter(|&&design| count_ways_to_form_design(design, patterns) > 0)
        .count();
    println!("Part 1: {}", possible_count);
}

fn part2(patterns: &[&str], designs: &[&str]) {
    let total_ways: usize = designs
        .iter()
        .map(|&design| count_ways_to_form_design(design, patterns))
        .sum();
    println!("Part 2: {}", total_ways);
}

fn count_ways_to_form_design(design: &str, patterns: &[&str]) -> usize {
    let design_len = design.len();
    let mut dp = vec![0; design_len + 1];
    dp[0] = 1;

    for i in 1..=design_len {
        for pattern in patterns {
            if i >= pattern.len() {
                if &design[i - pattern.len()..i] == *pattern {
                    dp[i] += dp[i - pattern.len()];
                }
            }
        }
    }

    dp[design_len]
}
