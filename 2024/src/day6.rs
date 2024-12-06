use itertools::Itertools;
use std::collections::HashSet;
use rayon::prelude::*;


pub fn main() {
    let input = include_str!("day6.txt");
    let grid: Vec<Vec<char>> = input.lines().map(|line| line.chars().collect()).collect();

    let start_pos = grid
        .iter()
        .enumerate()
        .find_map(|(row, line)| {
            line.iter()
                .position(|&c| c == '^')
                .map(|col| (row as isize, col as isize))
        })
        .unwrap();

    let visited = part1(&grid, start_pos);
    part2(&grid, start_pos, visited);
}

fn part1(grid: &Vec<Vec<char>>, start_pos: (isize, isize)) -> HashSet<(isize, isize)> {
    let visited = go(start_pos, grid);
    println!("Part 1: {}", visited.len());
    visited
}

fn go(start_pos: (isize, isize), grid: &Vec<Vec<char>>) -> HashSet<(isize, isize)> {
    let mut dir = Dir::N;
    let mut pos = start_pos;
    let mut visited = HashSet::new();

    loop {
        visited.insert(pos);

        if let Some((next_pos, next_dir)) = calculate_next_position(pos, dir, grid) {
            pos = next_pos;
            dir = next_dir;
        } else {
            return visited;
        }
    }
}

fn part2(grid: &Vec<Vec<char>>, start_pos: (isize, isize), path: HashSet<(isize, isize)>) {
    let obstructions = find_valid_obstruction_positions(grid, start_pos, path);
    println!("Part 2: {}", obstructions.len());
}

fn find_valid_obstruction_positions(
    grid: &Vec<Vec<char>>,
    start_pos: (isize, isize),
    path: HashSet<(isize, isize)>,
) -> HashSet<(isize, isize)> {
    path.par_iter()
        .filter(|&&(row, col)| grid[row as usize][col as usize] == '.' && (row, col) != start_pos)
        .filter_map(|&(row, col)| {
            let mut modified_grid = grid.to_vec();
            modified_grid[row as usize][col as usize] = '#';
            if causes_loop(&modified_grid, start_pos) {
                Some((row, col))
            } else {
                None
            }
        })
        .collect()
}

fn causes_loop(grid: &Vec<Vec<char>>, start_pos: (isize, isize)) -> bool {
    let mut visited = HashSet::new();
    let mut pos = start_pos;
    let mut dir = Dir::N;

    loop {
        let state = (pos, dir);

        if visited.contains(&state) {
            return true;
        }

        visited.insert(state);

        if let Some((next_pos, next_dir)) = calculate_next_position(pos, dir, grid) {
            pos = next_pos;
            dir = next_dir;
        } else {
            return false;
        }
    }
}

fn calculate_next_position(
    pos: (isize, isize),
    dir: Dir,
    grid: &Vec<Vec<char>>,
) -> Option<((isize, isize), Dir)> {
    let forward_pos = calculate_next_pos(pos, dir);

    if is_out_of_bounds(forward_pos, grid) {
        None
    } else if grid[forward_pos.0 as usize][forward_pos.1 as usize] != '#' {
        Some((forward_pos, dir))
    } else {
        let right_turn_dir = turn_right(dir);
        Some((pos, right_turn_dir))
    }
}

fn calculate_next_pos(pos: (isize, isize), dir: Dir) -> (isize, isize) {
    match dir {
        Dir::N => (pos.0 - 1, pos.1),
        Dir::E => (pos.0, pos.1 + 1),
        Dir::S => (pos.0 + 1, pos.1),
        Dir::W => (pos.0, pos.1 - 1),
    }
}

fn is_out_of_bounds(pos: (isize, isize), grid: &Vec<Vec<char>>) -> bool {
    let (row, col) = pos;
    row < 0 || col < 0 || row >= grid.len() as isize || col >= grid[0].len() as isize
}

fn turn_right(dir: Dir) -> Dir {
    match dir {
        Dir::N => Dir::E,
        Dir::E => Dir::S,
        Dir::S => Dir::W,
        Dir::W => Dir::N,
    }
}

#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
enum Dir {
    N,
    E,
    W,
    S,
}
