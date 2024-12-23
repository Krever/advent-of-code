use std::collections::{HashMap, HashSet};


pub fn main() {
    let input = include_str!("day23.txt");
    let network = parse_network(input);

    // Part 1: Find cliques of size 3
    let cliques = find_triangular_cliques(&network);
    let count_with_t = count_cliques_with_t(&cliques);
    println!("Part 1: {}", count_with_t);

    // Part 2: Find the largest clique
    let largest_clique = find_largest_clique(&network);
    let password = largest_clique.join(",");
    println!("Part 2: LAN Party Password: {}", password);
}

/// Parse the input into an adjacency list representation of the graph
fn parse_network(input: &str) -> HashMap<String, HashSet<String>> {
    let mut graph: HashMap<String, HashSet<String>> = HashMap::new();

    for line in input.lines() {
        let (a, b) = line.trim().split_once('-').unwrap();
        graph.entry(a.to_string()).or_default().insert(b.to_string());
        graph.entry(b.to_string()).or_default().insert(a.to_string());
    }

    graph
}

/// Find all cliques of size 3 in the graph
fn find_triangular_cliques(graph: &HashMap<String, HashSet<String>>) -> Vec<HashSet<String>> {
    let mut cliques = Vec::new();

    for (a, neighbors_a) in graph {
        for b in neighbors_a {
            if let Some(neighbors_b) = graph.get(b) {
                for c in neighbors_b {
                    if a != b && b != c && a != c {
                        if graph[c].contains(a) {
                            let clique: HashSet<String> = [a.clone(), b.clone(), c.clone()]
                                .iter()
                                .cloned()
                                .collect();
                            if !cliques.contains(&clique) {
                                cliques.push(clique);
                            }
                        }
                    }
                }
            }
        }
    }

    cliques
}

fn count_cliques_with_t(cliques: &[HashSet<String>]) -> usize {
    cliques
        .iter()
        .filter(|clique| clique.iter().any(|node| node.starts_with('t')))
        .count()
}

/// Bron-Kerbosch Algorithm to find the largest clique
fn bron_kerbosch(
    graph: &HashMap<String, HashSet<String>>,
    r: &mut HashSet<String>,
    p: &mut HashSet<String>,
    x: &mut HashSet<String>,
    largest_clique: &mut HashSet<String>,
) {
    if p.is_empty() && x.is_empty() {
        // Maximal clique found
        if r.len() > largest_clique.len() {
            *largest_clique = r.clone();
        }
        return;
    }

    let pivot = p.union(x).next().cloned().unwrap();
    let non_neighbors = p.difference(&graph[&pivot]).cloned().collect::<HashSet<_>>();

    for node in non_neighbors {
        r.insert(node.clone());

        let neighbors = graph[&node].clone();
        let new_p = p.intersection(&neighbors).cloned().collect::<HashSet<_>>();
        let new_x = x.intersection(&neighbors).cloned().collect::<HashSet<_>>();

        bron_kerbosch(graph, r, &mut new_p.clone(), &mut new_x.clone(), largest_clique);

        r.remove(&node);
        p.remove(&node);
        x.insert(node);
    }
}

/// Find the largest clique in the network
fn find_largest_clique(graph: &HashMap<String, HashSet<String>>) -> Vec<String> {
    let mut largest_clique = HashSet::new();
    let mut r = HashSet::new();
    let mut p = graph.keys().cloned().collect::<HashSet<_>>();
    let mut x = HashSet::new();

    bron_kerbosch(graph, &mut r, &mut p, &mut x, &mut largest_clique);

    let mut sorted_clique: Vec<String> = largest_clique.into_iter().collect();
    sorted_clique.sort();
    sorted_clique
}