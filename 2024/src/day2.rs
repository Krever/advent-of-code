use itertools::Itertools;

pub fn main() {
    let input = include_str!("day2.txt");
    let reports: Vec<Vec<i32>> = input
        .lines()
        .filter(|line| !line.is_empty())
        .map(|line| {
            line.split_ascii_whitespace()
                .map(|x| x.parse::<i32>().expect("Failed to parse integer"))
                .collect()
        })
        .collect();
    part1(&reports);
    part2(&reports);
}

fn part1(reports: &Vec<Vec<i32>>) {
    let count = reports.iter().filter(|&report| is_safe(report)).count();
    println!("{}", count);
}

fn is_safe(report: &[i32]) -> bool {
    let diffs: Vec<i32> = report.windows(2).map(|w| w[0] - w[1]).collect();

    let diffs_in_range = diffs.iter().all(|&diff| (1..=3).contains(&diff.abs()));
    let only_increasing = diffs.iter().all(|&diff| diff > 0);
    let only_decreasing = diffs.iter().all(|&diff| diff < 0);

    diffs_in_range && (only_decreasing || only_increasing)
}

fn part2(reports: &Vec<Vec<i32>>) {
    let count = reports.iter().filter(|&report| is_safe2(report)).count();
    println!("{}", count);
}

fn is_safe2(report: &[i32]) -> bool {
    is_ok(report, 1, false, false, None)
}

// recursive solution, either current elem is ok or current elem removed is ok
fn is_ok(
    report: &[i32],
    threshold: i32,
    was_increasing: bool,
    was_decreasing: bool,
    last_elem_opt: Option<i32>,
) -> bool {
    // Base cases
    if was_increasing && was_decreasing {
        return false; // Both increasing and decreasing is invalid
    }
    if threshold < 0 {
        return false; // Threshold exceeded
    }
    if report.is_empty() {
        return true; // Successfully processed the report
    }

    let elem = report[0];

    // Check the current element
    let is_ok_on_its_own = match last_elem_opt {
        None => is_ok(
            &report[1..],
            threshold,
            was_increasing,
            was_decreasing,
            Some(elem),
        ),
        Some(last_elem) => {
            let diff = elem - last_elem;
            if diff.abs() < 1 || diff.abs() > 3 {
                false // Invalid difference
            } else {
                let is_increasing = diff > 0;
                let is_decreasing = diff < 0;
                is_ok(
                    &report[1..],
                    threshold,
                    was_increasing || is_increasing,
                    was_decreasing || is_decreasing,
                    Some(elem),
                )
            }
        }
    };
    let is_ok_if_removed = is_ok(
        &report[1..],
        threshold - 1,
        was_increasing,
        was_decreasing,
        last_elem_opt,
    );
    is_ok_on_its_own || is_ok_if_removed
}
