#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub struct Vec2 {
    pub(crate) row: isize,
    pub(crate) col: isize,
}

impl Vec2 {
    fn difference(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row - other.row,
            col: self.col - other.col,
        }
    }

    pub(crate) fn add(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row + other.row,
            col: self.col + other.col,
        }
    }
    pub(crate) fn move_dir(self, dir: Dir4) -> Vec2 {
        self.add(dir.move_vec())
    }

    fn subtract(self, other: Vec2) -> Vec2 {
        Vec2 {
            row: self.row - other.row,
            col: self.col - other.col,
        }
    }

    pub(crate) fn is_within_bounds(self, dimensions: Vec2) -> bool {
        self.row >= 0 && self.row < dimensions.row && self.col >= 0 && self.col < dimensions.col
    }
}

use std::time::Instant;
use strum_macros::EnumIter;

pub fn measure_time<F, R>(label: &str, func: F) -> R
where
    F: FnOnce() -> R,
{
    let start = Instant::now();
    let result = func();
    let duration = start.elapsed();
    println!("[{label}] Execution time: {:?}", duration);
    result
}

pub const DIRECTIONS: [Vec2; 4] = [
    Vec2 { row: -1, col: 0 }, // Up
    Vec2 { row: 1, col: 0 },  // Down
    Vec2 { row: 0, col: -1 }, // Left
    Vec2 { row: 0, col: 1 },  // Right
];

#[derive(Debug, PartialEq, EnumIter, Hash, Clone, Copy, Eq)]
pub enum Dir4 {
    N,
    E,
    W,
    S,
}

impl Dir4 {
    pub const VALUES: [Dir4; 4] = [Dir4::N, Dir4::E, Dir4::W, Dir4::S];

    pub fn perpendicular(self) -> [Dir4; 2] {
        match self {
            Dir4::N | Dir4::S => [Dir4::E, Dir4::W],
            Dir4::E | Dir4::W => [Dir4::N, Dir4::S],
        }
    }

    pub fn move_vec(self) -> Vec2 {
        match self {
            Dir4::N => Vec2 { row: -1, col: 0 },
            Dir4::E => Vec2 { row: 0, col: 1 },
            Dir4::W => Vec2 { row: 0, col: -1 },
            Dir4::S => Vec2 { row: 1, col: 0 },
        }
    }
}
