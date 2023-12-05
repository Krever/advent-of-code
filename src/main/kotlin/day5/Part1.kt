package day5

import java.io.File


fun main(args: Array<String>) {

    val result = File(args[0]).useLines {
        val lines = it.toMutableList()
        val almanac = parseAlmanac(lines)
        almanac.resolve().minOf { it.location }
    }

    println(result)
}

typealias Id = Long

data class Almanac(
    val seeds: List<Id>,
    val seedToSoil: AlmanacMap,
    val soilToFertilizer: AlmanacMap,
    val fertilizerToWater: AlmanacMap,
    val waterToLight: AlmanacMap,
    val lightToTemp: AlmanacMap,
    val tempToHumidity: AlmanacMap,
    val humidityToLocation: AlmanacMap
) {

    fun resolveSeed(seed: Id): SeedRecipe {
        val soil = seedToSoil.get(seed)
        val fertilizer = soilToFertilizer.get(soil)
        val water = fertilizerToWater.get(fertilizer)
        val light = waterToLight.get(water)
        val temp = lightToTemp.get(light)
        val humidity = tempToHumidity.get(temp)
        val location = humidityToLocation.get(humidity)
        return SeedRecipe(seed, soil, fertilizer, water, light, temp, humidity, location)
    }

    fun resolve(): List<SeedRecipe> {
        return seeds.map { resolveSeed(it) }
    }

}

data class AlmanacMap(val entries: List<AlmanacMapEntry>) {
    fun get(value: Id): Id {
        return entries.firstNotNullOfOrNull { it.resolve(value) } ?: value
    }
}

data class AlmanacMapEntry(val destRangeStart: Id, val srcRangeStart: Id, val rangeLength: Id) {
    fun resolve(id: Id): Id? {
        return if (id >= srcRangeStart && id < srcRangeStart + rangeLength) id - srcRangeStart + destRangeStart
        else null
    }
}

data class SeedRecipe(
    val seed: Id,
    val soil: Id,
    val fertilizer: Id,
    val water: Id,
    val light: Id,
    val temp: Id,
    val humidity: Id,
    val location: Id,
)

fun parseAlmanac(input: List<String>): Almanac {
    return Almanac(
        seeds = input.first().split(" ").drop(1).map { it.toLong() },
        seedToSoil = parseMap("seed-to-soil", input),
        soilToFertilizer = parseMap("soil-to-fertilizer", input),
        fertilizerToWater = parseMap("fertilizer-to-water", input),
        waterToLight = parseMap("water-to-light", input),
        lightToTemp = parseMap("light-to-temperature", input),
        tempToHumidity = parseMap("temperature-to-humidity", input),
        humidityToLocation = parseMap("humidity-to-location", input),
    )

}


fun parseMap(header: String, input: List<String>): AlmanacMap {
    val entries = input.dropWhile { !it.startsWith(header) }.drop(1) // header
        .takeWhile { it.trim().isNotEmpty() }.map { parseEntry(it) }
    return AlmanacMap(entries)
}

fun parseEntry(line: String): AlmanacMapEntry {
    val split = line.split(" ")
    return AlmanacMapEntry(split[0].toLong(), split[1].toLong(), split[2].toLong())
}