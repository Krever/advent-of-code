use crate::common::Vec2;
use regex::Regex;

pub fn main() {
    let input = include_str!("day13.txt");
    let machines = parse_input(input);
    part1(&machines);
    part2(&machines);
}

fn part1(machines: &[(Vec2, Vec2, Vec2)]) {
    println!("Part 1: {}", solve(machines));
}

fn part2(machines: &[(Vec2, Vec2, Vec2)]) {
    let offset = Vec2 {
        x: 10_000_000_000_000,
        y: 10_000_000_000_000,
    };

    let updated_machines: Vec<(Vec2, Vec2, Vec2)> = machines
        .iter()
        .map(|(button_a, button_b, prize)| (*button_a, *button_b, prize.add(offset)))
        .collect();

    println!("Part 2: {}", solve(&updated_machines));
}

fn parse_input(input: &str) -> Vec<(Vec2, Vec2, Vec2)> {
    let re = Regex::new(
        r"Button A: X\+(\d+), Y\+(\d+)\s+Button B: X\+(\d+), Y\+(\d+)\s+Prize: X=(\d+), Y=(\d+)",
    )
    .unwrap();

    input
        .split("\n\n")
        .filter_map(|section| {
            re.captures(section).map(|caps| {
                let button_a = Vec2 {
                    x: caps[1].parse().unwrap(),
                    y: caps[2].parse().unwrap(),
                };
                let button_b = Vec2 {
                    x: caps[3].parse().unwrap(),
                    y: caps[4].parse().unwrap(),
                };
                let prize = Vec2 {
                    x: caps[5].parse().unwrap(),
                    y: caps[6].parse().unwrap(),
                };
                (button_a, button_b, prize)
            })
        })
        .collect()
}

fn solve(machines: &[(Vec2, Vec2, Vec2)]) -> isize {
    machines
        .iter()
        .filter_map(|(button_a, button_b, prize)| find_min_cost(button_a, button_b, prize))
        .sum()
}

fn find_min_cost(button_a: &Vec2, button_b: &Vec2, prize: &Vec2) -> Option<isize> {
    let b = (prize.y * button_a.x - prize.x * button_a.y)
        / (button_a.x * button_b.y - button_a.y * button_b.x);
    let a = (prize.x - b * button_b.x) / button_a.x;
    
    if (a * button_a.x + b * button_b.x) == prize.x && (a * button_a.y + b * button_b.y) == prize.y
    {
        Some(3 * a + b)
    } else {
        None
    }
}
