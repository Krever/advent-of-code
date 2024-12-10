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
