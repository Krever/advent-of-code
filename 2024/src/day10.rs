use itertools::Itertools;
use crate::common::{Vec2, DIRECTIONS};

pub fn main() {
    let input = include_str!("day10.txt");
    let map: Map = input
        .lines()
        .map(|l| {
            l.chars()
                .map(|c| c.to_digit(10).map_or(-1, |x| x as i32))
                .collect()
        })
        .collect();
    part1(&map);
    part2(&map);
}

type Map = Vec<Vec<i32>>;

fn part1(map: &Map) {
    let starting_points = find_all_zeros(map);
    let num_of_dests: usize = starting_points
        .iter()
        .map(|&point| find_destinations(map, point).into_iter().unique().count())
        .sum();
    println!("Part 1: {}", num_of_dests);
}

fn part2(map: &Map) {
    let starting_points = find_all_zeros(map);
    let num_of_dests: usize = starting_points
        .iter()
        .map(|&point| find_destinations(map, point).len())
        .sum();
    println!("Part 2: {}", num_of_dests);
}

fn find_all_zeros(map: &Map) -> Vec<Vec2> {
    map.iter()
        .enumerate()
        .flat_map(|(row_idx, row)| {
            row.iter()
                .enumerate()
                .filter_map(move |(col_idx, &value)| {
                    if value == 0 {
                        Some(Vec2 {
                            y: row_idx as isize,
                            x: col_idx as isize,
                        })
                    } else {
                        None
                    }
                })
        })
        .collect()
}

fn find_destinations(map: &Map, pos: Vec2) -> Vec<Vec2> {
    let current_value = map[pos.y as usize][pos.x as usize];
    if current_value == 9 {
        return vec![pos];
    }

    get_neighbors(pos, map)
        .into_iter()
        .filter(|&neighbor| {
            let neighbor_value = map[neighbor.y as usize][neighbor.x as usize];
            neighbor_value == current_value + 1
        })
        .flat_map(|neighbor| find_destinations(map, neighbor))
        .collect()
}

fn get_neighbors(pos: Vec2, map: &Map) -> Vec<Vec2> {
    DIRECTIONS
        .iter()
        .filter_map(|&dir| {
            let new_pos = pos.add(dir);
            if new_pos.is_within_bounds(bounds(map)) {
                Some(new_pos)
            } else {
                None
            }
        })
        .collect()
}

fn bounds(map: &Map) -> Vec2 {
    Vec2 {
        y: map.len() as isize,
        x: map[0].len() as isize,
    }
}
