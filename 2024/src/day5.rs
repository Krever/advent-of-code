use itertools::Itertools;
use std::collections::{HashMap, HashSet};

pub fn main() {
    let input = include_str!("day5.txt");
    let (rules_part, updates_part) = input.split_once("\n\n").unwrap();
    let rules: HashMap<i32, Vec<i32>> = rules_part
        .lines()
        .map(|x| {
            let (a, b) = x.split_once("|").unwrap();
            (a.parse::<i32>().unwrap(), b.parse::<i32>().unwrap())
        })
        .into_group_map();
    let updates: Vec<Vec<i32>> = updates_part
        .lines()
        .map(|x| {
            x.split(',')
                .map(|page| page.parse::<i32>().unwrap())
                .collect()
        })
        .collect();
    part1(&updates, &rules);
    part2(&updates, &rules);
}

fn part1(updates: &Vec<Vec<i32>>,rules: &HashMap<i32, Vec<i32>>) {
    let sum: i32 = updates
        .iter()
        .filter(|&x| is_correct(x, &rules))
        .map(|x| x[x.len() / 2])
        .sum();
    println!("Part 1: {}", sum);
}

fn is_correct(update: &Vec<i32>, rules: &HashMap<i32, Vec<i32>>) -> bool {
    let positions: HashMap<&i32, usize> = update
        .iter()
        .enumerate()
        .map(|(idx, value)| (value, idx))
        .collect();
    update.iter().all(|page| {
        let page_pos = positions.get(page).unwrap();
        let empty = Vec::new();
        let applicable_rules = rules.get(&page).unwrap_or(&empty);
        applicable_rules
            .iter()
            .all(|constraint| positions.get(constraint).unwrap_or(&usize::MAX) > page_pos)
    })
}

fn part2(updates: &Vec<Vec<i32>>,rules: &HashMap<i32, Vec<i32>>) {
    let sum: i32 = updates
        .iter()
        .filter(|&x| !is_correct(x, &rules))
        .map(|x| sort(x, rules))
        .map(|x| x[x.len() / 2])
        .sum();
    println!("Part 2: {}", sum);
}

fn sort(update: &Vec<i32>, rules: &HashMap<i32, Vec<i32>>) -> Vec<i32> {
    let mut sorted: Vec<i32> = Vec::new();
    let mut remaining: HashSet<&i32> = update.iter().collect();

    while sorted.len() != update.len() {
        let without_constraints: Vec<i32> = remaining
            .iter()
            .filter(|&&page| {
                let empty = Vec::new();
                let applicable_rules = rules.get(&page).unwrap_or(&empty);
                applicable_rules
                    .iter()
                    .all(|&required| sorted.contains(&required) || !update.contains(&required))
            })
            .map(|&&page| page)
            .collect();

        if without_constraints.is_empty() {
            panic!("A cycle was detected or unsatisfiable constraints exist!");
        }

        sorted.extend(&without_constraints);
        for page in without_constraints {
            remaining.remove(&page);
        }
    }
    sorted
}
