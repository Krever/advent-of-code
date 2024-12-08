use std::collections::{HashMap, HashSet};
use itertools::Itertools;

pub fn main() {
    let input = include_str!("day8.txt");
    let dimensions = Vec2 {
        row: input.lines().count() as isize,
        col: input.lines().next().unwrap().chars().count() as isize,
    };

    let groups = parse_coordinates(input);

    part1(&groups, dimensions);
    part2(&groups, dimensions);
}

fn part1(groups: &HashMap<char, Vec<Vec2>>, dimensions: Vec2) {
    let result = calculate_antinodes(groups, dimensions).len();
    println!("Part 1: {}", result);
}

fn part2(groups: &HashMap<char, Vec<Vec2>>, dimensions: Vec2) {
    let result = count_points_on_lines(groups, dimensions);
    println!("Part 2: {}", result);
}

fn parse_coordinates(input: &str) -> HashMap<char, Vec<Vec2>> {
    input
        .lines()
        .enumerate()
        .flat_map(|(row, line)| {
            line.chars().enumerate().filter_map(move |(col, c)| {
                if c.is_alphanumeric() {
                    Some((c, Vec2 {
                        row: row as isize,
                        col: col as isize,
                    }))
                } else {
                    None
                }
            })
        })
        .into_group_map()
}

fn calculate_antinodes(
    groups: &HashMap<char, Vec<Vec2>>,
    dimensions: Vec2,
) -> HashSet<Vec2> {
    groups
        .values()
        .flat_map(|positions| {
            positions.iter().tuple_combinations().flat_map(|(&pos1, &pos2)| {
                let difference = pos1.difference(pos2);
                [
                    pos1.add(difference),
                    pos2.subtract(difference),
                ]
                    .into_iter()
                    .filter(move |&antinode| antinode.is_within_bounds(dimensions))
            })
        })
        .collect()
}

fn count_points_on_lines(groups: &HashMap<char, Vec<Vec2>>, dimensions: Vec2) -> usize {
    groups
        .values()
        .flat_map(|positions| {
            positions.iter().tuple_combinations().flat_map(|(&pos1, &pos2)| {
                let difference = pos1.difference(pos2);

                let mut points = Vec::new();
                let mut current = pos1;

                while current.is_within_bounds(dimensions) {
                    points.push(current);
                    current = current.subtract(difference);
                }

                current = pos2;
                while current.is_within_bounds(dimensions) {
                    points.push(current);
                    current = current.add(difference);
                }

                points
            })
        })
        .collect::<HashSet<_>>()
        .len()
}

#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
struct Vec2 {
    row: isize,
    col: isize,
}

impl Vec2 {
    fn difference(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row - other.row,
            col: self.col - other.col,
        }
    }

    fn add(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row + other.row,
            col: self.col + other.col,
        }
    }

    fn subtract(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row - other.row,
            col: self.col - other.col,
        }
    }

    fn is_within_bounds(self, dimensions: Vec2) -> bool {
        self.row >= 0 && self.row < dimensions.row && self.col >= 0 && self.col < dimensions.col
    }
}
