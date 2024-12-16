#[derive(Debug, PartialEq, Eq, Hash, Clone, Copy)]
pub struct Vec2 {
    pub(crate) y: isize, // row
    pub(crate) x: isize, //col
}

impl Vec2 {
    pub(crate) fn negate(&self) -> Vec2 {
        Vec2 {
            x: -self.x,
            y: -self.y,
        }
    }
}

impl Vec2 {

    pub(crate) fn add(self, other: Vec2) -> Vec2 {
        Vec2 {
            y: self.y + other.y,
            x: self.x + other.x,
        }
    }
    pub(crate) fn move_dir(self, dir: Dir4) -> Vec2 {
        self.add(dir.move_vec())
    }

    pub fn subtract(self, other: Vec2) -> Vec2 {
        Vec2 {
            y: self.y - other.y,
            x: self.x - other.x,
        }
    }

    pub(crate) fn is_within_bounds(self, dimensions: Vec2) -> bool {
        self.y >= 0 && self.y < dimensions.y && self.x >= 0 && self.x < dimensions.x
    }

    pub fn wrap(&self, bounds: Vec2) -> Vec2 {
        Vec2 {
            x: (self.x.rem_euclid(bounds.x)),
            y: (self.y.rem_euclid(bounds.y)),
        }
    }
    pub fn neighbors(&self) -> Vec<Vec2> {
        let directions = [
            Vec2 { x: -1, y: 0 }, // Left
            Vec2 { x: 1, y: 0 },  // Right
            Vec2 { x: 0, y: -1 }, // Up
            Vec2 { x: 0, y: 1 },  // Down
        ];
        directions.iter().map(|d| self.add(*d)).collect()
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
    Vec2 { y: -1, x: 0 }, // Up
    Vec2 { y: 1, x: 0 },  // Down
    Vec2 { y: 0, x: -1 }, // Left
    Vec2 { y: 0, x: 1 },  // Right
];

#[derive(Debug, PartialEq, EnumIter, Hash, Clone, Copy, Eq)]
pub enum Dir4 {
    N,
    E,
    W,
    S,
}

impl Dir4 {
    pub(crate) fn rotate_clockwise(&self) -> Dir4 {
        match self {
            Dir4::N => Dir4::E,
            Dir4::E => Dir4::S,
            Dir4::S => Dir4::W,
            Dir4::W => Dir4::N,
        }
    }

    pub(crate) fn rotate_counter_clockwise(&self) -> Dir4 {
        match self {
            Dir4::N => Dir4::W,
            Dir4::W => Dir4::S,
            Dir4::S => Dir4::E,
            Dir4::E => Dir4::N,
        }
    }

    pub(crate) fn is_vertical(&self) -> bool {
        self == &Dir4::N || self == &Dir4::S
    }

    pub const VALUES: [Dir4; 4] = [Dir4::N, Dir4::E, Dir4::W, Dir4::S];

    pub fn perpendicular(self) -> [Dir4; 2] {
        match self {
            Dir4::N | Dir4::S => [Dir4::E, Dir4::W],
            Dir4::E | Dir4::W => [Dir4::N, Dir4::S],
        }
    }

    pub fn move_vec(self) -> Vec2 {
        match self {
            Dir4::N => Vec2 { y: -1, x: 0 },
            Dir4::E => Vec2 { y: 0, x: 1 },
            Dir4::W => Vec2 { y: 0, x: -1 },
            Dir4::S => Vec2 { y: 1, x: 0 },
        }
    }
}
