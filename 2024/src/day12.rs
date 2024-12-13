use crate::common::{Dir4, Vec2, DIRECTIONS};
use itertools::Itertools;
use std::collections::HashSet;

pub fn main() {
    let input = include_str!("day12.txt");
    let map: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();
    part1(&map);
    part2(&map);
}

fn part1(map: &[Vec<char>]) {
    process_regions(map, calculate_area_and_perimeter, "Part 1");
}

fn part2(map: &[Vec<char>]) {
    process_regions(map, calculate_area_and_sides, "Part 2");
}

fn process_regions<F>(map: &[Vec<char>], calculate: F, label: &str)
where
    F: Fn(&[Vec<char>], Vec2, char, Vec2, &mut HashSet<Vec2>) -> (usize, usize),
{
    let mut visited = HashSet::new();
    let bounds = Vec2 {
        y: map.len() as isize,
        x: map[0].len() as isize,
    };

    let total_price: usize = (0..map.len())
        .cartesian_product(0..map[0].len())
        .filter_map(|(row, col)| {
            let pos = Vec2 {
                y: row as isize,
                x: col as isize,
            };
            if visited.contains(&pos) {
                None
            } else {
                let plant_type = map[row][col];
                let (area, metric) = calculate(map, pos, plant_type, bounds, &mut visited);
                Some(area * metric)
            }
        })
        .sum();

    println!("{}: {}", label, total_price);
}

fn calculate_area_and_perimeter(
    map: &[Vec<char>],
    start: Vec2,
    plant_type: char,
    bounds: Vec2,
    visited: &mut HashSet<Vec2>,
) -> (usize, usize) {
    let mut area = 0;
    let mut perimeter = 0;
    let mut stack = vec![start];

    while let Some(pos) = stack.pop() {
        if visited.contains(&pos) {
            continue;
        }
        visited.insert(pos);
        area += 1;

        for &dir in &DIRECTIONS {
            let neighbor = pos.add(dir);

            if !neighbor.is_within_bounds(bounds)
                || map[neighbor.y as usize][neighbor.x as usize] != plant_type
            {
                perimeter += 1;
            } else if !visited.contains(&neighbor) {
                stack.push(neighbor);
            }
        }
    }

    (area, perimeter)
}

fn calculate_area_and_sides(
    map: &[Vec<char>],
    start: Vec2,
    plant_type: char,
    bounds: Vec2,
    visited: &mut HashSet<Vec2>,
) -> (usize, usize) {
    let mut area = 0;
    let mut sides: HashSet<(Vec2, Dir4)> = HashSet::new();
    let mut stack = vec![start];

    while let Some(pos) = stack.pop() {
        if visited.contains(&pos) {
            continue;
        }
        visited.insert(pos);
        area += 1;

        for dir in Dir4::VALUES {
            let neighbor = pos.move_dir(dir);

            if !neighbor.is_within_bounds(bounds)
                || map[neighbor.y as usize][neighbor.x as usize] != plant_type
            {
                sides.insert((neighbor, dir));
            } else if !visited.contains(&neighbor) {
                stack.push(neighbor);
            }
        }
    }

    let mut removed: HashSet<(Vec2, Dir4)> = HashSet::new();
    let mut unique_sides: HashSet<(Vec2, Dir4)> = HashSet::new();
    for &(pos, side_dir) in &sides {
        if removed.contains(&(pos, side_dir)) {
            continue;
        }

        let directions = side_dir.perpendicular();
        unique_sides.insert((pos, side_dir));
        for direction in directions {
            let mut neighbour = pos.move_dir(direction);
            while sides.contains(&(neighbour, side_dir)) {
                removed.insert((neighbour, side_dir));
                neighbour = neighbour.move_dir(direction)
            }
        }
    }

    (area, unique_sides.len())
}
