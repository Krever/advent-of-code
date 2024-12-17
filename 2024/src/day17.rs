use itertools::Itertools;
use rayon::prelude::*;
use std::collections::HashMap;
use chrono::Local;

pub fn main() {
    let input = include_str!("day17.txt");

    let (a, b, c, program) = parse_input(input);
    let mut computer = Computer::new(a, b, c, program.clone());

    computer.run();

    println!("{:?}", computer.registers);

    let result = computer
        .output
        .iter()
        .map(|x| x.to_string())
        .collect::<Vec<_>>()
        .join(",");
    println!("Part 1: Output: {}", result);

    let result = find_minimum_a(b, c, &program);
    println!("Part 2: Lowest positive A: {}", result);
}

fn find_minimum_a_2(b: isize, c: isize, program: &Vec<isize>) -> isize {
    for a in 1.. {
        let mut computer = Computer::new(a, b, c, program.clone());
        let is_ok = computer.runToGet(program);
        if is_ok {
            return a;
        }
    }

    unreachable!("The loop should always find a solution.");
}

fn find_minimum_a(b: isize, c: isize, program: &Vec<isize>) -> isize {
    let mut computer = Computer::new(0, b, c, program.clone());
    let target = program;
    let mut a = 0;
    'outer: for digit in 0..computer.program.len() {
        for i in 0.. {
            computer.reset(i + a * 8);
            computer.run();
            if computer.output[..] == target[target.len() - digit - 1..] {
                println!("Out: {:?}", computer.output);
                a *= 8;
                a += i;
                continue 'outer;
            }
        }
    }
    a
}

fn parse_input(input: &str) -> (isize, isize, isize, Vec<isize>) {
    let lines: Vec<&str> = input.lines().collect();

    let a = lines[0]
        .split_whitespace()
        .nth(2)
        .unwrap()
        .parse::<isize>()
        .unwrap();
    let b = lines[1]
        .split_whitespace()
        .nth(2)
        .unwrap()
        .parse::<isize>()
        .unwrap();
    let c = lines[2]
        .split_whitespace()
        .nth(2)
        .unwrap()
        .parse::<isize>()
        .unwrap();

    let program: Vec<isize> = lines[4]
        .split_whitespace()
        .skip(1) // Skip "Program:"
        .flat_map(|x| x.split(','))
        .map(|n| n.parse::<isize>().unwrap())
        .collect();

    (a, b, c, program)
}

#[derive(Debug)]
struct Computer {
    registers: HashMap<char, isize>, // Registers A, B, and C
    program: Vec<isize>,             // Program instructions
    output: Vec<isize>,              // Collected outputs
    instruction_pointer: usize,      // Program counter
}

impl Computer {
    fn new(a: isize, b: isize, c: isize, program: Vec<isize>) -> Self {
        let mut registers = HashMap::new();
        registers.insert('A', a);
        registers.insert('B', b);
        registers.insert('C', c);

        Self {
            registers,
            program,
            output: vec![],
            instruction_pointer: 0,
        }
    }

    fn reset(&mut self, a: isize) {
        self.registers.insert('A', a);
        self.registers.insert('B', 0);
        self.registers.insert('C', 0);
        self.output.clear();
        self.instruction_pointer = 0;
    }

    fn resolve_combo(&self, operand: isize) -> isize {
        match operand {
            0..=3 => operand,
            4 => *self.registers.get(&'A').unwrap(),
            5 => *self.registers.get(&'B').unwrap(),
            6 => *self.registers.get(&'C').unwrap(),
            _ => panic!("Invalid combo operand: {}", operand),
        }
    }

    fn run(&mut self) {
        while self.instruction_pointer < self.program.len() {
            self.step();
        }
    }

    fn runToGet(&mut self, expected_output: &Vec<isize>) -> bool {
        while self.instruction_pointer < self.program.len() {
            if !expected_output.starts_with(&self.output) {
                break;
            }
            self.step()
        }
        self.output == *expected_output
    }

    fn step(&mut self) {
        let opcode = self.program[self.instruction_pointer];
        let operand = self.program[self.instruction_pointer + 1];
        match opcode {
            0 => self.adv(operand),
            1 => self.bxl(operand),
            2 => self.bst(operand),
            3 => self.jnz(operand),
            4 => self.bxc(),
            5 => self.out(operand),
            6 => self.bdv(operand),
            7 => self.cdv(operand),
            _ => panic!("Unknown opcode: {}", opcode),
        }

        // If the instruction was not a jump, advance the pointer
        if opcode != 3 {
            self.instruction_pointer += 2;
        }
    }

    fn adv(&mut self, operand: isize) {
        let denominator = 2_isize.pow(self.resolve_combo(operand) as u32);
        let a_value = self.registers[&'A'] / denominator;
        self.registers.insert('A', a_value);
    }

    fn bxl(&mut self, operand: isize) {
        let b_value = self.registers[&'B'] ^ operand;
        self.registers.insert('B', b_value);
    }

    fn bst(&mut self, operand: isize) {
        let value = self.resolve_combo(operand) % 8;
        self.registers.insert('B', value);
    }

    fn jnz(&mut self, operand: isize) {
        if self.registers[&'A'] != 0 {
            self.instruction_pointer = operand as usize;
        } else {
            self.instruction_pointer += 2;
        }
    }

    fn bxc(&mut self) {
        let b_value = self.registers[&'B'] ^ self.registers[&'C'];
        self.registers.insert('B', b_value);
    }

    fn out(&mut self, operand: isize) {
        let value = self.resolve_combo(operand) % 8;
        self.output.push(value);
    }

    fn bdv(&mut self, operand: isize) {
        let denominator = 2_isize.pow(self.resolve_combo(operand) as u32);
        let b_value = self.registers[&'A'] / denominator;
        self.registers.insert('B', b_value);
    }

    fn cdv(&mut self, operand: isize) {
        let denominator = 2_isize.pow(self.resolve_combo(operand) as u32);
        let c_value = self.registers[&'A'] / denominator;
        self.registers.insert('C', c_value);
    }
}
