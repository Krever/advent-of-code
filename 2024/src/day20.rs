use crate::common::{Dir4, Vec2};
use std::collections::{HashMap, HashSet, VecDeque};

pub fn main() {
    let input = include_str!("day20.txt");
    let track = parse_racetrack(input);

    let cheats = find_extended_cheats(&track, 2);
    let significant_cheats = cheats
        .iter()
        .filter(|&&(_, saved_time)| saved_time >= 100)
        .count();
    println!("Part 1: {}", significant_cheats);

    let extended_cheats = find_extended_cheats(&track, 20);
    let significant_extended_cheats = extended_cheats
        .iter()
        .filter(|&&(_, saved_time)| saved_time >= 100)
        .count();
    println!("Part 2: {}", significant_extended_cheats);
}

type Track = HashMap<Vec2, usize>; // Map of points to their index

fn parse_racetrack(input: &str) -> Track {
    let mut track = HashMap::new();

    let lines: Vec<Vec<char>> = input
        .lines()
        .map(|l| l.chars().collect())
        .collect::<Vec<_>>();

    let mut start = Vec2 { x: -1, y: -1 };
    let mut end = Vec2 { x: -1, y: -1 };
    'outer: for (row, line) in input.lines().enumerate() {
        for (col, char) in line.chars().enumerate() {
            let pos = Vec2 {
                    x: col as isize,
                    y: row as isize,
                };
            match char {
                'S' => start = pos,
                'E' => end = pos,
                _ => {}
            }
            if start.x != -1 && end.x != -1 {
                break 'outer;
            }
        }
    }

    let mut current_pos = start;
    while current_pos != end {
        track.insert(current_pos, track.len());
        current_pos = Dir4::VALUES
            .iter()
            .map(|d| current_pos.add(d.move_vec()))
            .find(|&pos| {
                (lines[pos.y as usize][pos.x as usize] == '.'
                    || lines[pos.y as usize][pos.x as usize] == 'E')
                    && track.get(&pos).is_none()
            })
            .unwrap();
    }

    track.insert(current_pos, track.len()); // end
    track
}

fn find_extended_cheats(track: &Track, max_cheat_length: usize) -> Vec<((Vec2, Vec2), usize)> {
    let mut cheats = vec![];

    for (&start_pos, &start_index) in track {
        let mut visited = HashSet::new();
        let mut queue = VecDeque::new();
        queue.push_back((start_pos, 0, start_pos));

        while let Some((current, steps, origin)) = queue.pop_front() {
            if steps > max_cheat_length {
                continue;
            }

            if let Some(&end_index) = track.get(&current) {
                if end_index > start_index + steps {
                    cheats.push(((origin, current), end_index - start_index - steps));
                }
            }

            for &dir in &Dir4::VALUES {
                let next = current.add(dir.move_vec());
                if visited.insert(next) {
                    queue.push_back((next, steps + 1, origin));
                }
            }
        }
    }

    cheats
}
