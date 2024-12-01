use itertools::Itertools;

pub fn main() {
    let input = include_str!("day1.txt");
    let (first, second): (Vec<i32>, Vec<i32>) = input
        .lines()
        .filter(|line| !line.is_empty())
        .map(|line| {
            let mut words = line.split_ascii_whitespace();
            (
                words.next().unwrap().parse::<i32>().unwrap(),
                words.next().unwrap().parse::<i32>().unwrap(),
            )
        })
        .unzip(); // Split the tuple iterator into two separate vectors
    part1(&first, &second);
    part2(&first, &second);
}

fn part1(first: &[i32], second: &[i32]) {
    let out: i32 = first
        .iter()
        .copied()
        .sorted()
        .zip(second.iter().copied().sorted())
        .map(|(a, b)| (a - b).abs())
        .sum();

    println!("{}", out)
}

fn part2(first: &[i32], second: &[i32]) {
    let counts = second.iter().counts();
    let result: i32 = first
        .iter()
        .map(|a| {
            let count = counts.get(a).unwrap_or(&0); 
            a * *count as i32 
        })
        .sum();
    println!("{:?}", result);
}
