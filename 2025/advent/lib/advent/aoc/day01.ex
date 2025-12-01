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
        _ -> raise ArgumentError, "Invalid direction: #{inspect(d)}"
      end

    dist =
      rest
      |> String.trim()
      |> String.to_integer()

    {dir, dist}
  end

  @spec part1(rotations()) :: non_neg_integer()
  def part1(rotations) do
    {_pos, count} =
      Enum.reduce(rotations, {50, 0}, fn {dir, dist}, {pos, count} ->
        new_pos =
          case dir do
            :L -> rem(pos - dist, 100)
            :R -> rem(pos + dist, 100)
          end
          |> normalize()

        new_count = if new_pos == 0, do: count + 1, else: count
        {new_pos, new_count}
      end)

    count
  end

  @spec part2(term()) :: String.t()
  def part2(_data), do: ""

  @spec normalize(integer()) :: 0..99
  defp normalize(n) when n < 0, do: n + 100
  defp normalize(n), do: n
end
