use std::collections::{HashMap, VecDeque};

pub fn main() {
    let input = include_str!("day21.txt");
    let codes = parse_input(input);

    let total_complexity = calculate_total_complexity(&codes);
    println!("Part 1: Total complexity of the five codes: {}", total_complexity);
}

// Keypad Layout
const NUMERIC_KEYPAD: &[&str] = &[
    "789",
    "456",
    "123",
    " 0A",
];

const DIRECTIONAL_KEYPAD: &[&str] = &[
    " ^A",
    "<v>",
];

#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
struct State {
    position: (isize, isize),
    sequence_length: usize,
}

fn parse_input(input: &str) -> Vec<String> {
    input.lines().map(|line| line.trim().to_string()).collect()
}

/// Find the shortest sequence of moves to type a code on the numeric keypad.
fn find_shortest_sequence(code: &str) -> usize {
    let mut queue = VecDeque::new();
    let mut visited = HashMap::new();

    let start_pos = (3, 2); // Starting at 'A' on the numeric keypad

    queue.push_back(State {
        position: start_pos,
        sequence_length: 0,
    });
    visited.insert(start_pos, 0);

    let mut target_chars = code.chars().collect::<Vec<_>>();
    target_chars.reverse(); // Start from the last character and move backward

    while let Some(state) = queue.pop_front() {
        if let Some(&target) = target_chars.last() {
            let (x, y) = state.position;
            let current_char = NUMERIC_KEYPAD[y as usize].chars().nth(x as usize).unwrap_or(' ');

            if current_char == target {
                target_chars.pop();
                if target_chars.is_empty() {
                    return state.sequence_length + 1; // Include pressing 'A'
                }
            }

            // Explore valid moves
            for (dx, dy) in &[(0, -1), (0, 1), (-1, 0), (1, 0)] {
                let new_x = x + dx;
                let new_y = y + dy;

                if let Some(row) = NUMERIC_KEYPAD.get(new_y as usize) {
                    if let Some(next_char) = row.chars().nth(new_x as usize) {
                        if next_char != ' ' && !visited.contains_key(&(new_x, new_y)) {
                            visited.insert((new_x, new_y), state.sequence_length + 1);
                            queue.push_back(State {
                                position: (new_x, new_y),
                                sequence_length: state.sequence_length + 1,
                            });
                        }
                    }
                }
            }
        }
    }

    0
}

/// Extract the numeric part of a code
fn extract_numeric_part(code: &str) -> usize {
    code.chars()
        .filter(|c| c.is_numeric())
        .collect::<String>()
        .parse::<usize>()
        .unwrap_or(0)
}

/// Calculate the complexity of a single code
fn calculate_code_complexity(code: &str) -> usize {
    let sequence_length = find_shortest_sequence(code);
    let numeric_value = extract_numeric_part(code);
    sequence_length * numeric_value
}

/// Calculate the total complexity for all codes
fn calculate_total_complexity(codes: &[String]) -> usize {
    codes.iter().map(|code| calculate_code_complexity(code)).sum()
}
