pub fn main() {
    let input = include_str!("day7.txt");
    let data: Vec<(u64, Vec<u64>)> = parse_input(input);
    part1(&data);
    part2(&data);
}

fn parse_input(input: &str) -> Vec<(u64, Vec<u64>)> {
    input
        .lines()
        .filter_map(|line| {
            let (key, values) = line.split_once(": ")?;
            let key = key.parse::<u64>().ok()?;
            let values = values
                .split_whitespace()
                .map(|v| v.parse::<u64>())
                .collect::<Result<Vec<_>, _>>()
                .ok()?;
            Some((key, values))
        })
        .collect()
}

fn part1(data: &[(u64, Vec<u64>)]) {
    let sum: u64 = data
        .iter()
        .filter(|(desired_result, numbers)| is_solvable(*desired_result, numbers.clone(), false))
        .map(|(desired_result, _)| desired_result)
        .sum();
    println!("Part 1: {sum}");
}

fn part2(data: &[(u64, Vec<u64>)]) {
    let sum: u64 = data
        .iter()
        .filter(|(desired_result, numbers)| is_solvable(*desired_result, numbers.clone(), true))
        .map(|(desired_result, _)| desired_result)
        .sum();
    println!("Part 2: {sum}");
}

fn is_solvable(desired_result: u64, mut numbers: Vec<u64>, allow_concat: bool) -> bool {
    let current = numbers.remove(0);
    go(desired_result, current, &numbers, allow_concat)
}

fn go(
    desired_result: u64,
    current_result: u64,
    remaining_nums: &Vec<u64>,
    allow_concat: bool,
) -> bool {
    if desired_result == current_result && remaining_nums.is_empty() {
        return true;
    }
    if desired_result < current_result || remaining_nums.is_empty() {
        return false;
    }

    let mut next_remaining = remaining_nums.clone();
    let next = next_remaining.remove(0);

    go(desired_result, current_result * next, &next_remaining, allow_concat)
        || go(desired_result, current_result + next, &next_remaining, allow_concat)
        || (allow_concat && go(desired_result, concat(current_result, next), &next_remaining, true))
}

fn concat(a: u64, b: u64) -> u64 {
    format!("{}{}", a, b).parse().unwrap()
}
