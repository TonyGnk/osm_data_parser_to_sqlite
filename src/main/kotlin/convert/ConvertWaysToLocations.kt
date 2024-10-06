package convert

import data.fullNodesMap
import data.fullWays
import data.globalLocationsList
import org.openstreetmap.osmosis.core.domain.v0_6.Way

fun convertWaysToLocations() {
    print("Ways to locations...")
    val totalWays = fullWays.size

    fullWays.asSequence()
        .withIndex()
        .filter { (_, way) -> way.tags.isNotEmpty() }
        .forEach { (index, way) ->
            val progress = (index + 1) * 100 / totalWays
            print("\rWays to locations...$progress%")

            processLocation(way)
        }

    println("\rWays to locations...OK : ${globalLocationsList.size} locations")
}

private fun processLocation(way: Way) {
    val coordinates = way.wayNodes
        .map { fullNodesMap[it.nodeId]!! }
        .run {
            val totalLat = sumOf { it.latitude }
            val totalLon = sumOf { it.longitude }
            Pair(totalLat / size, totalLon / size)
        }

    if (coordinates.first.isFinite() && coordinates.second.isFinite()) {
        val location = findLocation(way.tags, coordinates.first, coordinates.second)
        if (location != null) globalLocationsList.add(location)
    } else {
        println("Invalid coordinates: $coordinates")
    }
}
