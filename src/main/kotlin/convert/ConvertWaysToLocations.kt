package convert

import data.fullNodesMap
import data.fullWays
import data.globalGlassList
import data.globalCoordinateList
import data.globalSuburbsList
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import utils.getCategorySuburb

fun convertWaysToLocations() {
    print("Ways to locations...")
    val totalWays = fullWays.size

    fullWays.asSequence()
        .withIndex()
        .filter { (_, way) -> way.tags.isNotEmpty() && !isRoad(way.tags) }
        .forEach { (index, way) ->
            val progress = (index + 1) * 100 / totalWays
            print("\rWays to locations...$progress%")

            processLocation(way)
        }

    println("\rWays to locations...OK : ${globalCoordinateList.size} locations")
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
        val pair = findLocationGlass(
            way.id, way.tags, coordinates.first, coordinates.second
        )
        if (pair != null) {
            globalGlassList.add(pair.first)
            globalCoordinateList.add(pair.second)
        }
    }
}

fun convertWaysToSuburb() {
    print("Ways to locations...")
    val totalWays = fullWays.size

    fullWays.asSequence()
        .withIndex()
        .filter { (_, way) -> way.tags.getCategorySuburb() != null && !isRoad(way.tags) }
        .forEach { (index, way) ->
            val progress = (index + 1) * 100 / totalWays
            print("\rWays to locations...$progress%")

            processSuburb(way)
        }

    println("\rWays to locations...OK : ${globalCoordinateList.size} locations")
}

private fun processSuburb(way: Way) {
    val coordinates = way.wayNodes
        .map { fullNodesMap[it.nodeId]!! }
        .run {
            val totalLat = sumOf { it.latitude }
            val totalLon = sumOf { it.longitude }
            Pair(totalLat / size, totalLon / size)
        }

    if (coordinates.first.isFinite() && coordinates.second.isFinite()) {
        val suburb = findLocationSuburb(
            way.id, way.tags, coordinates.first, coordinates.second
        )
        if (suburb != null) {
            globalSuburbsList.add(suburb.first)
            globalCoordinateList.add(suburb.second)
        }
    }
}
