defmodule AdventWeb.Day01Live do
  use AdventWeb, :live_view
  alias Advent.AOC.Day01
  alias AdventWeb.AOCComponents, as: AOC

  @year 2025
  @day 1

  def mount(_params, _session, socket) do
    {:ok,
     socket
     |> assign(:year, @year)
     |> assign(:day, @day)
     |> assign(:result1, nil)
     |> assign(:result2, nil)}
  end

  def handle_event("solve", %{"input" => input}, socket) do
    data = Day01.parse(input)

    {:noreply,
      socket
      |> assign(:result1, Day01.part1(data))
      |> assign(:result2, Day01.part2(data))}
  end

  def render(assigns) do
    ~H"""
    <AOC.aoc_day year={@year} day={@day} result1={@result1} result2={@result2} />
    """
  end

end
