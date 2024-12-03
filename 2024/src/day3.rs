use regex::Regex;
use std::collections::{HashMap, HashSet};

pub fn main() {
    let input = include_str!("day3.txt");
    part1(input);
    part2(input);
}

fn part1(input: &str) {
    let pattern = r"mul\(([0-9]{1,3}),([0-9]{1,3})\)";
    let re = Regex::new(pattern).expect("Invalid regex");

    // Extract matches and calculate the result
    let result: i32 = re
        .captures_iter(input)
        .map(|cap| {
            let first = cap[1].parse::<i32>().expect("Failed to parse first number");
            let second = cap[2].parse::<i32>().expect("Failed to parse second number");
            first * second
        })
        .sum();

    println!("Part 1 Result: {}", result);
}

pub fn part2(input: &str) {
    let mul_re = Regex::new(r"mul\(([0-9]{1,3}),([0-9]{1,3})\)").expect("Invalid mul regex");
    let do_re = Regex::new(r"do\(\)").expect("Invalid do regex");
    let dont_re = Regex::new(r"don't\(\)").expect("Invalid don't regex");
    let muls: HashMap<usize, (i32, i32)> = mul_re
        .captures_iter(input)
        .map(|cap| {
            let start = cap.get(0).unwrap().start();
            let first = cap[1].parse::<i32>().expect("Failed to parse first number");
            let second = cap[2].parse::<i32>().expect("Failed to parse second number");
            (start, (first, second))
        })
        .collect();

    let dos: HashSet<usize> = do_re.find_iter(input).map(|mat| mat.start()).collect();
    let donts: HashSet<usize> = dont_re.find_iter(input).map(|mat| mat.start()).collect();

    // Calculate the result based on enabled/disabled states
    let mut enabled = true;
    let mut result = 0;

    for i in 0..input.len() {
        if dos.contains(&i) {
            enabled = true;
        }
        if donts.contains(&i) {
            enabled = false;
        }
        if enabled {
            if let Some((a, b)) = muls.get(&i) {
                result += a * b;
            }
        }
    }

    println!("Part 2 Result: {}", result);
}
