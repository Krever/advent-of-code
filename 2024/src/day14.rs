use crate::common::Vec2;
use itertools::Itertools;
use rayon::prelude::*;
use regex::Regex;
use std::collections::HashMap;

pub fn main() {
    let input = include_str!("day14.txt");
    let robots = parse_input(input);
    let bounds = Vec2 { x: 101, y: 103 };
    part1(&robots, &bounds);
    part2(&robots, &bounds);
}

fn part1(robots: &[(Vec2, Vec2)], bounds: &Vec2) {
    let positions = simulate_motion(robots, bounds, 100);
    let safety_factor = calculate_safety_factor(&positions, bounds);
    println!("Part 1: {}", safety_factor);
}

fn part2(robots: &[(Vec2, Vec2)], bounds: &Vec2) {
    let best_simulation = (1..10000)
        .into_par_iter()
        .map(|seconds| (seconds, simulate_motion(robots, bounds, seconds)))
        .max_by_key(|(_, positions)| analyze_positions(positions))
        .unwrap();

    println!("Part 2: {}", best_simulation.0);
}

fn parse_input(input: &str) -> Vec<(Vec2, Vec2)> {
    let re = Regex::new(r"p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)").unwrap();

    input
        .lines()
        .filter_map(|line| {
            re.captures(line).map(|caps| {
                let position = Vec2 {
                    x: caps[1].parse().unwrap(),
                    y: caps[2].parse().unwrap(),
                };
                let velocity = Vec2 {
                    x: caps[3].parse().unwrap(),
                    y: caps[4].parse().unwrap(),
                };
                (position, velocity)
            })
        })
        .collect()
}

fn simulate_motion(robots: &[(Vec2, Vec2)], bounds: &Vec2, seconds: isize) -> HashMap<Vec2, usize> {
    let mut positions = HashMap::new();

    robots.iter().for_each(|&(position, velocity)| {
        let final_position = position
            .add(Vec2 {
                x: velocity.x * seconds,
                y: velocity.y * seconds,
            })
            .wrap(*bounds);

        *positions.entry(final_position).or_insert(0) += 1;
    });

    positions
}

fn analyze_positions(positions: &HashMap<Vec2, usize>) -> usize {
    positions
        .iter()
        .map(|(&pos, _)| {
            pos.neighbors()
                .iter()
                .filter(|neighbor| positions.contains_key(neighbor))
                .count()
        })
        .sum()
}

fn render_map(positions: &HashMap<Vec2, usize>, bounds: &Vec2) -> String {
    let mut grid = vec![vec!['.'; bounds.x as usize]; bounds.y as usize];

    for (&pos, &count) in positions {
        if let Some(cell) = grid
            .get_mut(pos.y as usize)
            .and_then(|row| row.get_mut(pos.x as usize))
        {
            *cell = match count {
                1 => '1',
                2 => '2',
                3 => '3',
                _ => '+',
            };
        }
    }

    grid.into_iter()
        .map(|row| row.into_iter().collect::<String>())
        .collect::<Vec<_>>()
        .join("\n")
}

fn calculate_safety_factor(positions: &HashMap<Vec2, usize>, bounds: &Vec2) -> usize {
    let mid_x = bounds.x / 2;
    let mid_y = bounds.y / 2;

    let quadrants = positions.iter().fold([0; 4], |mut quadrants, (&position, &count)| {
        if position.x == mid_x || position.y == mid_y {
            return quadrants; // Skip robots on the middle lines
        }

        let quadrant = match (position.x < mid_x, position.y < mid_y) {
            (true, true) => 0,   // Top-left
            (false, true) => 1,  // Top-right
            (true, false) => 2,  // Bottom-left
            (false, false) => 3, // Bottom-right
        };

        quadrants[quadrant] += count;
        quadrants
    });

    quadrants.iter().product()
}
