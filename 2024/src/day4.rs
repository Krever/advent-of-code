use strum::IntoEnumIterator;
use strum_macros::EnumIter;

pub fn main() {
    let input = include_str!("day4.txt");
    let lines: Vec<_> = input.lines().collect();
    part1(&lines);
    part2(&lines);
}

fn part1(input: &Vec<&str>) {
    let xs: Vec<(usize, usize)> = input
        .iter()
        .enumerate()
        .flat_map(|(line_idx, &line)| {
            line.char_indices()
                .filter_map(move |(i, c)| if c == 'X' { Some((line_idx, i)) } else { None })
        })
        .collect();

    let count: usize = xs
        .iter()
        .flat_map(|&(line_idx, char_idx)| {
            Dir::iter().filter(move |dir| {
                find_xmas(
                    input,
                    Letter::X,
                    (line_idx as isize, char_idx as isize),
                    dir,
                )
            })
        })
        .count();
    println!("Part 1: {}", count);
}

fn find_xmas(input: &Vec<&str>, looking_for: Letter, idx: (isize, isize), dir: &Dir) -> bool {
    let (line_idx, char_idx) = idx;

    if line_idx < 0
        || line_idx >= input.len() as isize
        || char_idx < 0
        || char_idx >= input[line_idx as usize].len() as isize
    {
        return false;
    }

    let current_char = input[line_idx as usize]
        .chars()
        .nth(char_idx as usize)
        .unwrap();
    let current_letter = match current_char {
        'X' => Letter::X,
        'M' => Letter::M,
        'A' => Letter::A,
        'S' => Letter::S,
        _ => return false,
    };

    if current_letter != looking_for {
        return false;
    }

    let next_looking_for = match looking_for {
        Letter::X => Letter::M,
        Letter::M => Letter::A,
        Letter::A => Letter::S,
        Letter::S => return true, // Found the full XMAS sequence
    };

    let next_idx = match dir {
        Dir::N => (line_idx - 1, char_idx),
        Dir::E => (line_idx, char_idx + 1),
        Dir::W => (line_idx, char_idx - 1),
        Dir::S => (line_idx + 1, char_idx),
        Dir::NE => (line_idx - 1, char_idx + 1),
        Dir::NW => (line_idx - 1, char_idx - 1),
        Dir::SE => (line_idx + 1, char_idx + 1),
        Dir::SW => (line_idx + 1, char_idx - 1),
    };

    find_xmas(input, next_looking_for, next_idx, dir)
}

#[derive(Debug, PartialEq)]
enum Letter {
    X,
    M,
    A,
    S,
}

#[derive(Debug, PartialEq, EnumIter)]
enum Dir {
    N,
    E,
    W,
    S,
    NE,
    NW,
    SE,
    SW,
}

fn part2(input: &Vec<&str>) {
    let aas: Vec<(usize, usize)> = input
        .iter()
        .enumerate()
        .flat_map(|(line_idx, &line)| {
            line.char_indices()
                .filter_map(move |(i, c)| if c == 'A' { Some((line_idx, i)) } else { None })
        })
        .collect();

    let count: usize = aas
        .iter()
        .filter(|&&(line_idx, char_idx)| {
            find_mas(input, (line_idx as isize, char_idx as isize))
        })
        .count();

    println!("Part 2: {}", count);
}

fn find_mas(input: &Vec<&str>, idx: (isize, isize)) -> bool {
    let center = idx;
    let positions = [
        (center.0 - 1, center.1 - 1),
        (center.0 - 1, center.1 + 1),
        (center.0 + 1, center.1 - 1),
        (center.0 + 1, center.1 + 1),
    ];

    if positions.iter().any(|&(x, y)| {
        x < 0
            || y < 0
            || x >= input.len() as isize
            || y >= input[x as usize].len() as isize
    }) {
        return false;
    }
    
    let letters: String = positions
        .iter()
        .map(|&(x, y)| input[x as usize].chars().nth(y as usize).unwrap())
        .collect();
    
    matches!(letters.as_str(), "MMSS" | "MSMS" | "SSMM" | "SMSM")
}
