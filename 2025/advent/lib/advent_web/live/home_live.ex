defmodule AdventWeb.HomeLive do
  use AdventWeb, :live_view

  alias AdventWeb.AOCComponents, as: AOC

  @year 2025

  # Map available solvers by day number
  @solvers %{
    1 => Advent.AOC.Day01
  }

  @impl true
  def mount(_params, _session, socket) do
    {:ok,
     socket
     |> assign(:year, @year)
     |> assign(:day, 1)
     |> assign(:result1, nil)
     |> assign(:result2, nil)}
  end

  @impl true
  def handle_event("solve", %{"input" => input}, socket) do
    day = socket.assigns.day

    case Map.get(@solvers, day) do
      nil ->
        {:noreply, assign(socket, result1: "N/A", result2: "N/A")}

      mod ->
        data = mod.parse(input)

        {:noreply,
         socket
         |> assign(:result1, safe_call(mod, :part1, [data]))
         |> assign(:result2, safe_call(mod, :part2, [data]))}
    end
  end

  # Changing the day should reload input -> we ensure input is not carried over
  # by simply updating the :day and clearing results; the AOC component will
  # auto-prefill based on day when @input is nil.
  @impl true
  def handle_event("change-day", %{"form" => %{"day" => day_str}}, socket) do
    day = parse_day(day_str)

    {:noreply,
     socket
     |> assign(:day, day)
     |> assign(:result1, nil)
     |> assign(:result2, nil)}
  end

  defp parse_day(day_str) when is_binary(day_str) do
    case Integer.parse(day_str) do
      {i, _} when i >= 1 and i <= 25 -> i
      _ -> 1
    end
  end

  defp safe_call(mod, fun, args) do
    try do
      apply(mod, fun, args)
    rescue
      _ -> "error"
    end
  end

  @impl true
  def render(assigns) do
    ~H"""
    <AOC.aoc_day year={@year} day={@day} result1={@result1} result2={@result2}>
      <:tools>
        <.form for={%{}} as={:form} phx-change="change-day" class="flex items-center gap-2">
          <label class="label">
            <span class="label-text">Day</span>
          </label>
          <select name="day" class="select select-bordered">
            <%= for d <- 1..25 do %>
              <option value={d} selected={@day == d}>Day <%= String.pad_leading(to_string(d), 2, "0") %></option>
            <% end %>
          </select>
        </.form>
      </:tools>
    </AOC.aoc_day>
    """
  end
end
