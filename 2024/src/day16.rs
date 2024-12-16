use crate::common::{Dir4, Vec2};
use im::HashSet as imHashSet;
use itertools::{izip, Itertools};
use std::collections::{HashMap, HashSet, VecDeque};

#[derive(Debug, Clone, Copy, Hash, PartialEq, Eq)]
struct State {
    position: Vec2,
    direction: Dir4,
    cost: isize,
}

pub fn main() {
    let input = include_str!("day16.txt");
    let maze = parse_maze(input);

    let start = maze.iter().find(|(_, &c)| c == 'S').unwrap().0;
    let end = maze.iter().find(|(_, &c)| c == 'E').unwrap().0;

    let (cost, costs) = traverse_maze(&maze, *start, *end);
    println!("Part1: {}", cost);
    let optimal = backtrack(*end, &costs);
    println!("Part 2: {}", optimal.len());

    // println!("{}", render_maze_with_optimal_points(&maze, &optimal));
}

fn parse_maze(input: &str) -> HashMap<Vec2, char> {
    input
        .lines()
        .enumerate()
        .flat_map(|(y, line)| {
            line.chars().enumerate().map(move |(x, c)| {
                (
                    Vec2 {
                        x: x as isize,
                        y: y as isize,
                    },
                    c,
                )
            })
        })
        .collect()
}

fn traverse_maze(
    maze: &HashMap<Vec2, char>,
    start: Vec2,
    end: Vec2,
) -> (isize, HashMap<Vec2, isize>) {
    let mut queue = VecDeque::new();
    let mut costs: HashMap<Vec2, isize> = HashMap::new();

    queue.push_back(State {
        position: start,
        direction: Dir4::E,
        cost: 0,
    });

    while let Some(
        cur_state @ State {
            position: cur_position,
            direction: cur_direction,
            cost: cur_cost,
        },
    ) = queue.pop_front()
    {
        if maze.get(&cur_position) == Some(&'#') {
            continue;
        }
        if let Some(&existing_cost) = costs.get(&cur_position) {
            if cur_cost > existing_cost {
                continue;
            }
        }

        costs.insert(cur_position, cur_cost);
        push_neighbors(cur_state, &mut queue);
    }

    (costs[&end], costs)
}

fn backtrack(end: Vec2, costs: &HashMap<Vec2, isize>) -> HashSet<Vec2> {
    let mut optimal_points = HashSet::new();
    let mut queue = VecDeque::new();

    let max_cost = costs[&end];

    for dir in Dir4::VALUES {
        queue.push_back(State {
            position: end,
            direction: dir,
            cost: 0,
        })
    }

    while let Some(
        cur_state @ State {
            position: cur_pos,
            direction: cur_dir,
            cost: cur_cost,
        },
    ) = queue.pop_front()
    {
        if let Some(&existing_cost) = costs.get(&cur_pos) {
            if cur_cost + existing_cost > max_cost {
                continue;
            }

            optimal_points.insert(cur_pos);
            push_neighbors(cur_state, &mut queue);
        }
    }

    optimal_points
}

fn push_neighbors(current_state: State, queue: &mut VecDeque<State>) {
    let State {
        position: cur_pos,
        direction: cur_dir,
        cost: cur_cost,
    } = current_state;

    for new_direction in cur_dir.perpendicular() {
        queue.push_back(State {
            position: cur_pos.add(new_direction.move_vec()),
            direction: new_direction,
            cost: cur_cost + 1001,
        });
    }

    queue.push_back(State {
        position: cur_pos.add(cur_dir.move_vec()),
        direction: cur_dir,
        cost: cur_cost + 1,
    });
}

fn render_maze_with_optimal_points(
    maze: &HashMap<Vec2, char>,
    optimal_points: &HashSet<Vec2>,
) -> String {
    let max_x = maze.keys().map(|pos| pos.x).max().unwrap_or(0);
    let max_y = maze.keys().map(|pos| pos.y).max().unwrap_or(0);

    let mut grid = vec![vec!['.'; (max_x + 1) as usize]; (max_y + 1) as usize];

    for (pos, &cell) in maze {
        grid[pos.y as usize][pos.x as usize] = cell;
    }

    for &pos in optimal_points {
        if let Some(cell) = grid
            .get_mut(pos.y as usize)
            .and_then(|row| row.get_mut(pos.x as usize))
        {
            *cell = 'O';
        }
    }

    grid.into_iter()
        .map(|row| row.into_iter().collect::<String>())
        .collect::<Vec<_>>()
        .join("\n")
}
