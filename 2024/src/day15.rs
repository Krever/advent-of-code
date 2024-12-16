use crate::common::{Dir4, Vec2};
use itertools::Itertools;
use rayon::prelude::*;
use regex::Regex;
use std::collections::HashMap;

pub fn main() {
    let input = include_str!("day15.txt");
    let (input_map, input_moves) = input.split_once("\n\n").unwrap();
    let moves = parse_moves(input_moves);

    part1(input_map, &moves);
    part2(input_map, &moves);
}

fn part1(input_map: &str, moves: &Vec<char>) {
    let (mut warehouse, mut robot_position) = parse_warehouse(input_map);
    simulate_robot(&mut warehouse, &mut robot_position, &moves);

    let gps_sum = calculate_gps_sum(&warehouse);
    println!("Part 1: Sum of GPS coordinates of boxes: {}", gps_sum);
}

fn part2(input_map: &str, moves: &Vec<char>) {
    let (mut scaled_warehouse, mut scaled_robot_position) = scale_warehouse(input_map);
    simulate_robot(&mut scaled_warehouse, &mut scaled_robot_position, &moves);

    let scaled_gps_sum = calculate_gps_sum(&scaled_warehouse);
    println!(
        "Part 2: Sum of GPS coordinates of scaled boxes: {}",
        scaled_gps_sum
    );
}

type Warehouse = HashMap<Vec2, char>;

fn parse_warehouse(input: &str) -> (Warehouse, Vec2) {
    let mut robot_position = Vec2 { x: 0, y: 0 };
    let warehouse: Warehouse = input
        .lines()
        .enumerate()
        .flat_map(|(row, line)| {
            line.chars()
                .enumerate()
                .map(|(col, c)| {
                    let pos = Vec2 {
                        x: col as isize,
                        y: row as isize,
                    };
                    if c == '@' {
                        robot_position = pos;
                    }
                    (pos, c)
                })
                .collect_vec()
        })
        .collect();
    (warehouse, robot_position)
}

fn scale_warehouse(input: &str) -> (Warehouse, Vec2) {
    let mut robot_position = Vec2 { x: 0, y: 0 };
    let mut scaled_warehouse = Warehouse::new();

    for (row, line) in input.lines().enumerate() {
        for (col, c) in line.chars().enumerate() {
            let base_pos = Vec2 {
                x: (col * 2) as isize,
                y: row as isize,
            };

            match c {
                '#' => {
                    scaled_warehouse.insert(base_pos, '#');
                    scaled_warehouse.insert(base_pos.add(Vec2 { x: 1, y: 0 }), '#');
                }
                '.' => {
                    scaled_warehouse.insert(base_pos, '.');
                    scaled_warehouse.insert(base_pos.add(Vec2 { x: 1, y: 0 }), '.');
                }
                'O' => {
                    scaled_warehouse.insert(base_pos, '[');
                    scaled_warehouse.insert(base_pos.add(Vec2 { x: 1, y: 0 }), ']');
                }
                '@' => {
                    scaled_warehouse.insert(base_pos, '@');
                    scaled_warehouse.insert(base_pos.add(Vec2 { x: 1, y: 0 }), '.');
                    robot_position = base_pos;
                }
                _ => {}
            }
        }
    }

    (scaled_warehouse, robot_position)
}

fn parse_moves(input: &str) -> Vec<char> {
    input.lines().flat_map(|line| line.chars()).collect()
}

fn simulate_robot(warehouse: &mut Warehouse, robot_position: &mut Vec2, moves: &[char]) {
    let directions = [
        ('^', Dir4::N),
        ('v', Dir4::S),
        ('<', Dir4::W),
        ('>', Dir4::E),
    ];

    for &move_dir in moves {
        if let Some(&(_, dir)) = directions.iter().find(|&&(d, _)| d == move_dir) {
            let new_position = robot_position.add(dir.move_vec());

            match warehouse.get(&new_position).unwrap_or(&'#') {
                '.' => {
                    // Move robot to an empty space
                    warehouse.insert(*robot_position, '.');
                    warehouse.insert(new_position, '@');
                    *robot_position = new_position;
                }
                'O' | '[' | ']' => {
                    // Attempt to push the stack of boxes
                    let mut to_inspect = vec![new_position];
                    let mut to_move = vec![new_position];

                    // Collect all consecutive boxes in the direction of the move
                    while let Some(box_pos) = to_inspect.pop() {
                        let next = *warehouse.get(&box_pos).unwrap();
                        match next {
                            'O' => {
                                let next_pos = box_pos.add(dir.move_vec());
                                to_inspect.push(next_pos);
                                to_move.push(box_pos);
                            }
                            '[' => {
                                let next_pos = box_pos.add(dir.move_vec());
                                if (dir.is_vertical()) {
                                    let pair_pos = box_pos.add(Dir4::E.move_vec());
                                    let pair_next_pos = pair_pos.add(dir.move_vec());
                                    to_inspect.push(pair_next_pos);
                                    to_move.push(pair_pos);
                                }
                                to_inspect.push(next_pos);
                                to_move.push(box_pos);
                            }
                            ']' => {
                                let next_pos = box_pos.add(dir.move_vec());
                                if (dir.is_vertical()) {
                                    let pair_pos = box_pos.add(Dir4::W.move_vec());
                                    let pair_next_pos = pair_pos.add(dir.move_vec());
                                    to_inspect.push(pair_next_pos);
                                    to_move.push(pair_pos);
                                }
                                to_inspect.push(next_pos);
                                to_move.push(box_pos);
                            }
                            _ => {
                                continue;
                            }
                        }
                    }

                    // Check if the last box in the stack can be moved
                    let can_be_pushed = to_move
                        .iter()
                        .all(|x| warehouse.get(&x.add(dir.move_vec())).unwrap() != &'#');
                    if !can_be_pushed {
                        continue;
                    }
                    let mut changeset: HashMap<Vec2, char> =
                        to_move.iter().map(|x| (*x, '.')).collect();
                    for &box_pos in to_move.iter() {
                        let new_box_pos = box_pos.add(dir.move_vec());
                        changeset.insert(new_box_pos, *warehouse.get(&box_pos).unwrap());
                    }
                    warehouse.extend(changeset);
                    // Move the robot and all boxes in the complete stack
                    warehouse.insert(*robot_position, '.');
                    warehouse.insert(new_position, '@');
                    *robot_position = new_position;
                }
                _ => {
                    // Do nothing if the move is blocked
                }
            }
            // println!("Move {}:", move_dir);
            // println!("{}", render_map(&warehouse));
            // println!("");
        }
    }
}

fn calculate_gps_sum(warehouse: &Warehouse) -> isize {
    let mut sum = 0;

    for (pos, &cell) in warehouse {
        if cell == 'O' || cell == '[' {
            sum += 100 * pos.y + pos.x;
        }
    }

    sum
}

fn render_map(warehouse: &Warehouse) -> String {
    let max_x = warehouse.keys().map(|pos| pos.x).max().unwrap_or(0);
    let max_y = warehouse.keys().map(|pos| pos.y).max().unwrap_or(0);

    let mut grid = vec![vec!['.'; (max_x + 1) as usize]; (max_y + 1) as usize];

    for (pos, &cell) in warehouse {
        grid[pos.y as usize][pos.x as usize] = cell;
    }

    grid.into_iter()
        .map(|row| row.into_iter().collect::<String>())
        .collect::<Vec<_>>()
        .join("\n")
}
