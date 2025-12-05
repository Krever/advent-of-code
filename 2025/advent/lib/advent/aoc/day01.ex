defmodule Advent.AOC.Day01 do
  @moduledoc false

  @type direction :: :L | :R
  @type rotation :: {direction(), non_neg_integer()}
  @type rotations :: [rotation()]

  @spec parse(String.t()) :: rotations()
  def parse(input) do
    input
    |> String.split("\n", trim: true)
    |> Enum.map(&parse_line/1)
  end

  @spec parse_line(String.t()) :: rotation()
  defp parse_line(line) do
    line =
      line
      |> String.trim()

    <<d::binary-size(1), rest::binary>> = line

    dir =
      case d do
        "L" -> :L
        "R" -> :R
      end

    {dir, String.to_integer(rest)}
  end

  @spec part1(rotations()) :: non_neg_integer()
  def part1(rotations) do
    {_pos, count} =
      Enum.reduce(rotations, {50, 0}, fn {dir, dist}, {pos, count} ->
        new_pos = new_position(pos, dist, dir)
        new_count = if new_pos == 0, do: count + 1, else: count
        {new_pos, new_count}
      end)

    count
  end

  # -------- Part 2 --------

  @spec part2(rotations()) :: non_neg_integer()
  def part2(rotations) do
    {_pos, count} =
      Enum.reduce(rotations, {50, 0}, fn {dir, dist}, {pos, count} ->
        zeros_during = zeros_during_rotation(pos, dist, dir)
        new_pos = new_position(pos, dist, dir)
        {new_pos, count + zeros_during}
      end)

    count
  end

  @spec zeros_during_rotation(pos :: integer(), dist :: integer(), dir :: rotation()) ::
          non_neg_integer()
  defp zeros_during_rotation(pos, dist, dir) do
    raw =
      case dir do
        :R -> rem(100 - pos, 100)
        :L -> rem(pos, 100)
      end

    k0 = if raw == 0, do: 100, else: raw

    if k0 > dist,
      do: 0,
      else: 1 + div(dist - k0, 100)
  end

  def new_position(pos, dist, dir) do
    case dir do
      :R -> rem(pos + dist, 100)
      :L -> rem(pos - dist, 100)
    end
    |> normalize()
  end

  @spec normalize(integer()) :: 0..99
  defp normalize(n) when n < 0, do: n + 100
  defp normalize(n), do: n
end
