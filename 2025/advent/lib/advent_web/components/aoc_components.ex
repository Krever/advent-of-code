defmodule AdventWeb.AOCComponents do
  @moduledoc """
  Reusable UI components for Advent of Code days.

  These components follow the same styling conventions as the Phoenix project
  starters in this app: Tailwind CSS with daisyUI, alongside the shared
  `AdventWeb.CoreComponents` helpers (buttons, icons, etc.).

  Components are designed to be dropped into LiveViews to provide a consistent
  layout for: the page title, an input textarea with a submit button, and
  nicely formatted result panels for Part 1 and Part 2.
  """

  use Phoenix.Component
  use Gettext, backend: AdventWeb.Gettext
  alias AdventWeb.CoreComponents, as: CC

  @doc """
  Renders a standard Advent of Code day page section with:
    - Title ("Advent of Code <year> — Day <day>")
    - Input textarea and Solve button
    - Two result cards (Part 1 / Part 2)

  Assigns:
    * `:year` - year as integer or string (optional; defaults to current year)
    * `:day` - day number as integer or string (required for title)
    * `:input` - the textarea value (defaults to "")
    * `:rows` - textarea rows (defaults to 12)
    * `:submit_event` - form submit event name (defaults to "solve")
    * `:placeholder` - placeholder text for the textarea (optional)
    * `:result1` / `:result2` - values to show for each part (optional)

  Slots:
    * `:tools` - optional slot above the textarea (e.g., sample input buttons)
    * `:extra_results` - optional slot rendered after standard results
  """
  attr :year, :any, default: nil
  attr :day, :any, default: nil
  attr :input, :string, default: ""
  attr :rows, :integer, default: 12
  attr :submit_event, :string, default: "solve"
  attr :placeholder, :string, default: nil
  attr :result1, :any, default: nil
  attr :result2, :any, default: nil
  slot :tools
  slot :extra_results

  def aoc_day(assigns) do
    assigns =
      assigns
      |> assign_new(:year, fn -> default_year() end)

    ~H"""
    <section class="container mx-auto p-4 sm:p-6">
      <div class="mb-6">
        <h1 class="text-2xl sm:text-3xl font-semibold">
          Advent of Code {@year}
          <span class="opacity-70">—</span>
          Day {@day}
        </h1>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Input card -->
        <div class="card bg-base-100 shadow">
          <div class="card-body gap-4">
            <div :if={render_slot(@tools) != []} class="flex flex-wrap gap-2 items-center">
              {render_slot(@tools)}
            </div>

            <.form for={%{}} as={:form} phx-submit={@submit_event} class="flex flex-col gap-3">
              <label class="label">
                <span class="label-text">Puzzle input</span>
              </label>
              <textarea
                name="input"
                rows={@rows}
                class="textarea textarea-bordered w-full font-mono text-sm"
                placeholder={@placeholder}
              ><%= @input %></textarea>

              <div class="flex gap-2">
                <CC.button class="btn btn-primary"><%= gettext("Solve") %></CC.button>
                <span class="opacity-60 self-center text-sm">Press Ctrl+Enter to submit</span>
              </div>
            </.form>
          </div>
        </div>

        <!-- Results card -->
        <div class="card bg-base-100 shadow">
          <div class="card-body gap-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="stats shadow bg-base-200">
                <div class="stat">
                  <div class="stat-title">Part 1</div>
                  <div class="stat-value text-primary text-2xl sm:text-3xl">
                    <%= render_result(@result1) %>
                  </div>
                </div>
              </div>
              <div class="stats shadow bg-base-200">
                <div class="stat">
                  <div class="stat-title">Part 2</div>
                  <div class="stat-value text-secondary text-2xl sm:text-3xl">
                    <%= render_result(@result2) %>
                  </div>
                </div>
              </div>
            </div>

            <div :if={render_slot(@extra_results) != []} class="mt-2">
              {render_slot(@extra_results)}
            </div>
          </div>
        </div>
      </div>
    </section>
    """
  end

  # Helpers
  defp default_year do
    # Fallback to current year; in AoC context, you may want to hardcode the running year
    case Date.utc_today() do
      %Date{year: y} -> y
      _ -> 2025
    end
  end

  defp render_result(nil), do: "—"
  defp render_result(result) when is_binary(result), do: result
  defp render_result(result), do: inspect(result)
end
