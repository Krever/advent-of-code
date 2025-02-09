use std::collections::{HashMap, VecDeque};

#[derive(Debug, Clone, PartialEq, Eq)]
enum Gate {
    AND(String, String),
    OR(String, String),
    XOR(String, String),
}

#[derive(Debug, Clone)]
enum Value {
    Known(u8),
    Unknown,
}

/// Parse the input into initial values and gate definitions
fn parse_input(input: &str) -> (HashMap<String, Value>, Vec<(String, Gate)>) {
    let mut initial_values = HashMap::new();
    let mut gates = Vec::new();

    let mut lines = input.lines();

    // Parse initial wire values
    for line in lines.by_ref() {
        if line.trim().is_empty() {
            break;
        }
        let (wire, value) = line.split_once(": ").unwrap();
        initial_values.insert(wire.to_string(), Value::Known(value.parse::<u8>().unwrap()));
    }

    // Parse gate definitions
    for line in lines {
        let (inputs, output) = line.split_once(" -> ").unwrap();
        let output = output.to_string();

        let gate = if inputs.contains("AND") {
            let parts: Vec<&str> = inputs.split(" AND ").collect();
            Gate::AND(parts[0].to_string(), parts[1].to_string())
        } else if inputs.contains("OR") {
            let parts: Vec<&str> = inputs.split(" OR ").collect();
            Gate::OR(parts[0].to_string(), parts[1].to_string())
        } else if inputs.contains("XOR") {
            let parts: Vec<&str> = inputs.split(" XOR ").collect();
            Gate::XOR(parts[0].to_string(), parts[1].to_string())
        } else {
            panic!("Unknown gate type in line: {}", line);
        };

        gates.push((output, gate));
    }

    (initial_values, gates)
}

/// Simulate the circuit logic
fn simulate_circuit(
    mut initial_values: HashMap<String, Value>,
    gates: Vec<(String, Gate)>,
) -> HashMap<String, u8> {
    let mut queue: VecDeque<(String, Gate)> = VecDeque::from(gates);

    while let Some((output, gate)) = queue.pop_front() {
        let result = match gate {
            Gate::AND(ref a, ref b) => match (initial_values.get(a), initial_values.get(b)) {
                (Some(Value::Known(v1)), Some(Value::Known(v2))) => Some(v1 & v2),
                _ => None,
            },
            Gate::OR(ref a, ref b) => match (initial_values.get(a), initial_values.get(b)) {
                (Some(Value::Known(v1)), Some(Value::Known(v2))) => Some(v1 | v2),
                _ => None,
            },
            Gate::XOR(ref a, ref b) => match (initial_values.get(a), initial_values.get(b)) {
                (Some(Value::Known(v1)), Some(Value::Known(v2))) => Some(v1 ^ v2),
                _ => None,
            },
        };

        if let Some(value) = result {
            initial_values.insert(output, Value::Known(value));
        } else {
            // Re-enqueue if inputs are not yet ready
            queue.push_back((output, gate));
        }
    }

    // Extract final values of wires starting with 'z'
    initial_values
        .into_iter()
        .filter_map(|(key, value)| {
            if key.starts_with('z') {
                if let Value::Known(v) = value {
                    return Some((key, v));
                }
            }
            None
        })
        .collect()
}

/// Convert wire values to a decimal number
fn calculate_output(wire_values: &HashMap<String, u8>) -> u64 {
    let mut binary_string = wire_values
        .iter()
        .filter(|(k, _)| k.starts_with('z'))
        .map(|(k, v)| (k.clone(), *v))
        .collect::<Vec<_>>();

    // Sort by wire name (e.g., z00, z01, ...)
    binary_string.sort_by_key(|(k, _)| k.clone());

    let binary_result: String = binary_string
        .into_iter()
        .map(|(_, v)| if v == 1 { '1' } else { '0' })
        .collect();

    u64::from_str_radix(&binary_result, 2).unwrap_or(0)
}

/// Detect swapped gates and correct them
fn detect_swapped_gates(
    initial_values: &HashMap<String, Value>,
    gates: &[(String, Gate)],
) -> Vec<String> {
    let mut swapped_wires = Vec::new();

    let mut gates_clone = gates.to_vec();

    for i in 0..gates.len() {
        for j in (i + 1)..gates.len() {
            gates_clone.swap(i, j);
            let result = simulate_circuit(initial_values.clone(), gates_clone.clone());

            if is_valid_addition(&result, initial_values) {
                swapped_wires.push(gates[i].0.clone());
                swapped_wires.push(gates[j].0.clone());
            }

            gates_clone.swap(i, j); // Revert the swap
        }
    }

    swapped_wires.sort();
    swapped_wires
}

/// Validate if the current output represents valid addition
fn is_valid_addition(wire_values: &HashMap<String, u8>, initial_values: &HashMap<String, Value>) -> bool {
    let x_value = extract_binary_value(initial_values, "x");
    let y_value = extract_binary_value(initial_values, "y");
    let z_value = extract_binary_value(wire_values, "z");

    x_value + y_value == z_value
}

fn extract_binary_value(values: &HashMap<String, Value>, prefix: &str) -> u64 {
    let mut binary_string = values
        .iter()
        .filter(|(k, _)| k.starts_with(prefix))
        .map(|(k, v)| (k.clone(), match v {
            Value::Known(v) => *v,
            _ => 0,
        }))
        .collect::<Vec<_>>();

    binary_string.sort_by_key(|(k, _)| k.clone());

    let binary_result: String = binary_string
        .into_iter()
        .map(|(_, v)| if v == 1 { '1' } else { '0' })
        .collect();

    u64::from_str_radix(&binary_result, 2).unwrap_or(0)
}

pub fn main() {
    let input = include_str!("day24.txt");
    let (initial_values, gates) = parse_input(input);

    let final_wire_values = simulate_circuit(initial_values.clone(), gates.clone());
    let result = calculate_output(&final_wire_values);

    println!("Part 1: The output value is {}", result);

    let swapped_wires = detect_swapped_gates(&initial_values, &gates);
    println!("Part 2: Swapped wires: {}", swapped_wires.join(","));
}
