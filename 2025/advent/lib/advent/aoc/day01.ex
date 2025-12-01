defmodule Advent.AOC.Day01 do
  def parse(input) do
    input
    |> String.split("\n", trim: true)
    |> Enum.map(&String.to_integer/1)
  end

  def part1(data) do
    Enum.sum(data)
  end

  def part2(data) do
    Enum.product(data)
  end
end