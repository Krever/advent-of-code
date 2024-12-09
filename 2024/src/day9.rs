pub fn main() {
    let input = include_str!("day9.txt");
    let mut blocks = parse_blocks(input);
    let mut blocks1 = parse_blocks(input);

    move_blocks_to_front(&mut blocks);
    println!("Part 1: {}", calculate_checksum(&blocks));

    compact_files(&mut blocks1);
    println!("Part 2: {}", calculate_checksum(&blocks1));
}

fn parse_blocks(input: &str) -> Vec<Block> {
    let mut taken_id = 0; // Counter for assigning IDs to taken blocks

    input
        .chars()
        .enumerate()
        .flat_map(|(index, ch)| {
            let size = ch.to_digit(10).expect("Invalid input") as usize;

            if index % 2 == 0 {
                // Odd positions (0-based index): Taken blocks
                let blocks = (0..size)
                    .map(|_| Block::Taken(taken_id))
                    .collect::<Vec<_>>();
                taken_id += 1;
                blocks
            } else {
                (0..size).map(|_| Block::Empty).collect::<Vec<_>>()
            }
        })
        .collect()
}

fn move_blocks_to_front(blocks: &mut Vec<Block>) {
    let mut empty_indices: Vec<usize> = blocks
        .iter()
        .enumerate()
        .filter(|(_, block)| matches!(block, Block::Empty))
        .map(|(index, _)| index)
        .collect();

    let mut taken_indices: Vec<usize> = blocks
        .iter()
        .enumerate()
        .filter(|(_, block)| matches!(block, Block::Taken(_)))
        .map(|(index, _)| index)
        .collect();

    while let (Some(&empty_idx), Some(&taken_idx)) = (empty_indices.first(), taken_indices.last()) {
        if taken_idx < empty_idx {
            break; // No more valid moves
        }

        // Move the block to the empty position
        if let Block::Taken(id) = blocks[taken_idx] {
            blocks[empty_idx] = Block::Taken(id);
            blocks[taken_idx] = Block::Empty;
        }

        // Update the indices
        empty_indices.remove(0);
        taken_indices.pop();
    }
}

fn render_blocks(blocks: &[Block]) -> String {
    blocks
        .iter()
        .map(|block| match block {
            Block::Empty => '.',
            Block::Taken(id) => char::from_digit(*id as u32, 10).unwrap(),
        })
        .collect()
}

fn calculate_checksum(blocks: &[Block]) -> usize {
    blocks
        .iter()
        .enumerate()
        .filter_map(|(position, block)| match block {
            Block::Taken(id) => Some(position * id),
            Block::Empty => None,
        })
        .sum()
}

fn compact_files(blocks: &mut Vec<Block>) {
    let mut files = find_files(blocks);
    files.sort_by(|a, b| b.id.cmp(&a.id)); // Sort by ID in descending order

    for file in files {
        if let Some(start_idx) = find_leftmost_free_span(blocks, file.size, file.start) {
            // Move file to the free span
            for i in 0..file.size {
                blocks[start_idx + i] = Block::Taken(file.id);
            }

            // Clear the original file location
            for i in file.start..(file.start + file.size) {
                blocks[i] = Block::Empty;
            }
        }
    }
}
fn find_files(blocks: &[Block]) -> Vec<File> {
    let mut files = Vec::new();
    let mut current_id = None;
    let mut start = 0;
    let mut size = 0;

    for (idx, block) in blocks.iter().enumerate() {
        match block {
            Block::Taken(id) if Some(*id) == current_id => size += 1,
            Block::Taken(id) => {
                if let Some(prev_id) = current_id {
                    files.push(File { id: prev_id, start, size });
                }
                current_id = Some(*id);
                start = idx;
                size = 1;
            }
            Block::Empty => {
                if let Some(prev_id) = current_id {
                    files.push(File { id: prev_id, start, size });
                }
                current_id = None;
                size = 0;
            }
        }
    }

    if let Some(prev_id) = current_id {
        files.push(File { id: prev_id, start, size });
    }

    files
}

fn find_leftmost_free_span(blocks: &[Block], span_size: usize, boundary: usize) -> Option<usize> {
    let mut count = 0;
    let mut start_idx = 0;

    for (idx, block) in blocks.iter().enumerate().take(boundary) {
        if matches!(block, Block::Empty) {
            if count == 0 {
                start_idx = idx;
            }
            count += 1;
            if count == span_size {
                return Some(start_idx);
            }
        } else {
            count = 0;
        }
    }

    None
}

#[derive(Debug, PartialEq)]
enum Block {
    Empty,
    Taken(usize), // The block is taken and assigned an ID
}


#[derive(Debug)]
struct File {
    id: usize,
    start: usize,
    size: usize,
}
