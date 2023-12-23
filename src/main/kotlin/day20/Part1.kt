package day20

import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.map.foldable.forAll
import utils.Utils

fun main() {
    val input = Utils.loadResource("/day20.txt")
    val parsed = parseInput(input)
    val initState = buildInitialState(parsed)
    val x = (1..1000).fold(initState to SignalsSent(0, 0, false)) { (state, stats), _ ->
        val (newState, newStats) = pressButton(state)
        newState to stats + newStats
    }
    println(x.second.low * x.second.high)
}

typealias Input = List<Module>
typealias ModuleId = String

data class Module(val type: ModuleType, val name: ModuleId, val destinations: List<ModuleId>)
enum class ModuleType { FlipFlop, Conjunction, Broadcast }

fun parseInput(input: String): Input = run {
    input.lines()
        .map { line ->
            val split = line.split(" -> ")
            val spec = split[0]
            val dests = split[1]
                .split(", ")
            val (type, name) = if (spec.startsWith("%")) ModuleType.FlipFlop to spec.drop(1)
            else if (spec.startsWith("&")) ModuleType.Conjunction to spec.drop(1)
            else if (spec == "broadcaster") ModuleType.Broadcast to spec
            else TODO()
            Module(type, name, dests)
        }
}

data class Signal(val from: ModuleId, val type: SignalType, val destination: ModuleId)
enum class SignalType { High, Low }

fun render(signal: Signal) = println("${signal.from} -${signal.type}-> ${signal.destination}")

data class SignalsSent(val low: Long, val high: Long, val rxHandled: Boolean) {
    fun add(queue: List<Signal>) =
        SignalsSent(
            low + queue.count { it.type == SignalType.Low },
            high + queue.count { it.type == SignalType.High },
            rxHandled || queue.exists { it.destination == "jz" && it.type == SignalType.High })

    operator fun plus(o: SignalsSent) =
        SignalsSent(low + o.low, high + o.high, rxHandled || o.rxHandled)
}

fun pressButton(initState: State): Pair<State, SignalsSent> = run {
    fun run(queue: List<Signal>, state: State, signalsSent: SignalsSent): Pair<State, SignalsSent> = run {
        //queue.forEach(::render)
        if (queue.isEmpty()) state to signalsSent
        else {
            val acc: List<Signal> = listOf()
            val newSignals = queue.fold(state to acc) { (state, signals), signal ->
                val (newSignals, newState) = handleSignal(signal, state)
                newState to signals + newSignals
            }
            run(newSignals.second, newSignals.first, signalsSent.add(queue))
        }
    }

    val initSignal = Signal("button", SignalType.Low, "broadcaster")
    run(listOf(initSignal), initState, SignalsSent(0, 0, false))
}

fun handleSignal(signal: Signal, state: State): Pair<List<Signal>, State> = run {
    if (state.contains(signal.destination)) {
        val (module, moduleState) = state[signal.destination]!!
        val (newSignal, newState) = moduleState.respond(signal.from, signal.type)
        val signals = if (newSignal != null) module.destinations.map { Signal(module.name, newSignal, it) }
        else listOf()
        signals to state.plus(module.name to (module to newState))
    } else listOf<Signal>() to state
}

sealed interface ModuleState {

    fun respond(from: ModuleId, type: SignalType): Pair<SignalType?, ModuleState>

    data class FlipFlop(val isOn: Boolean) : ModuleState {
        override fun respond(from: ModuleId, type: SignalType): Pair<SignalType?, ModuleState> = run {
            when (type) {
                SignalType.High -> null to this
                SignalType.Low -> {
                    val signal = if (isOn) SignalType.Low else SignalType.High
                    signal to FlipFlop(!isOn)
                }
            }
        }
    }

    data class Conjunction(val lastSignals: Map<ModuleId, SignalType>) : ModuleState {
        override fun respond(from: ModuleId, type: SignalType): Pair<SignalType?, ModuleState> = run {
            val newState = lastSignals.plus(from to type)
            val signal = if (newState.forAll { it == SignalType.High }) SignalType.Low else SignalType.High
            signal to Conjunction(newState)
        }
    }

    data object Broadcast : ModuleState {
        override fun respond(from: ModuleId, type: SignalType): Pair<SignalType?, ModuleState> = run {
            type to this
        }
    }
}
typealias State = Map<ModuleId, Pair<Module, ModuleState>>

fun buildInitialState(input: Input): State = run {
    input.associate { m ->
        val initState = when (m.type) {
            ModuleType.FlipFlop -> ModuleState.FlipFlop(isOn = false)
            ModuleType.Conjunction -> {
                val inputs = input.filter { it.destinations.contains(m.name) }.map { it.name }
                val initState = inputs.associate { it to SignalType.Low }
                ModuleState.Conjunction(initState)
            }

            ModuleType.Broadcast -> ModuleState.Broadcast
        }
        m.name to (m to initState)
    }
}