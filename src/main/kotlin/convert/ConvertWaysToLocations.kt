package convert

import data.fullNodesMap
import data.fullWays
import data.globalGlassList
import data.globalCoordinateList
import data.globalSuburbsList
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import utils.getCategorySuburb

/**
 * Converts ways to locations and updates the global glass list and coordinate list.
 */
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

/**
 * Processes a way to extract location information and updates the global glass list and coordinate list.
 *
 * @param way The way to process.
 */
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

/**
 * Converts ways to suburbs and updates the global suburbs list and coordinate list.
 */
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

/**
 * Processes a way to extract suburb information and updates the global suburbs list and coordinate list.
 *
 * @param way The way to process.
 */
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
