use crate::common::{Dir4, Vec2};
use std::collections::{HashSet, VecDeque};

pub fn main() {
    let input = include_str!("day18.txt");
    let bytes = parse_input(input);

    part1(&bytes);
    part2(&bytes);
}

const GRID_SIZE: Vec2 = Vec2 { x: 71, y: 71 };
const START: Vec2 = Vec2 { x: 0, y: 0 };
const END: Vec2 = Vec2 {
    x: GRID_SIZE.x - 1,
    y: GRID_SIZE.y - 1,
};

fn part1(bytes: &[Vec2]) {
    let corrupted: HashSet<Vec2> = bytes.iter().take(1024).cloned().collect();
    let steps = find_shortest_path(&START, &END, &corrupted, &GRID_SIZE);
    println!("Part 1: {}", steps.unwrap());
}

fn part2(bytes: &[Vec2]) {
    let index = (0..bytes.len()).collect::<Vec<_>>().partition_point(|&idx| {
        let corrupted: HashSet<Vec2> = bytes.iter().take(idx+1).cloned().collect();
        find_shortest_path(&START, &END, &corrupted, &GRID_SIZE).is_some()
    });

    let blocking_byte = bytes[index];
    println!("Part 2: {},{}", blocking_byte.x, blocking_byte.y);
}

fn parse_input(input: &str) -> Vec<Vec2> {
    input
        .lines()
        .map(|line| {
            let coords: Vec<_> = line.split(',').collect();
            Vec2 {
                x: coords[0].parse().unwrap(),
                y: coords[1].parse().unwrap(),
            }
        })
        .collect()
}

fn find_shortest_path(
    start: &Vec2,
    end: &Vec2,
    corrupted: &HashSet<Vec2>,
    bounds: &Vec2,
) -> Option<usize> {
    let mut queue = VecDeque::new();
    let mut visited = HashSet::new();

    queue.push_back((*start, 0));
    visited.insert(*start);

    while let Some((current, steps)) = queue.pop_front() {
        if current == *end {
            return Some(steps);
        }

        for &dir in &Dir4::VALUES {
            if let Some(next) = current.add(dir.move_vec()).as_positive() {
                if next.is_within_bounds(*bounds)
                    && !corrupted.contains(&next)
                    && visited.insert(next)
                {
                    queue.push_back((next, steps + 1));
                }
            }
        }
    }

    None // No path found
}
