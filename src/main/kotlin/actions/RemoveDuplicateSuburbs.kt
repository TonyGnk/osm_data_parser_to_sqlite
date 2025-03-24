package actions

import data.Suburb
import data.fullNodesMap
import data.globalCoordinateList
import data.globalSuburbsList
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * Removes duplicate suburbs from the globalSuburbsList.
 * Suburbs with the same name and within a certain distance are considered duplicates.
 */
fun removeDuplicateSuburbs() {
    print("Remove duplicate suburbs...")

    val oldSize = globalSuburbsList.size
    val suburbs = globalSuburbsList.toMutableSet()

    // Remove the suburbs with the same name and if they are too close
    val suburbsWithTheSameName = suburbs.groupBy { it.titleEl }
    val newSuburbs = mutableListOf<Suburb>()
    suburbsWithTheSameName.forEach { (_, list) ->
        findDuplicateSuburbRecursive(list, newSuburbs)
    }

    globalSuburbsList.clear()
    globalSuburbsList.addAll(newSuburbs)

    println("\rRemove duplicate suburbs...OK:  $oldSize -> ${globalSuburbsList.size}")
}

/**
 * Recursively finds and removes duplicate suburbs from the given list.
 * Suburbs with the same name and within a certain distance are considered duplicates.
 *
 * @param suburbs The list of suburbs to check for duplicates.
 * @param newSuburbs The list to store the non-duplicate suburbs.
 */
fun findDuplicateSuburbRecursive(suburbs: List<Suburb>, newSuburbs: MutableList<Suburb>) {
    if (suburbs.isEmpty()) return

    val suburb = suburbs.first()
    newSuburbs.add(suburb)

    val otherSuburbs = suburbs.drop(1)

    val otherSuburbsFiltered: List<Suburb> = otherSuburbs.filter { currentSuburb ->
        val currentNode = globalCoordinateList.firstOrNull { it.id == currentSuburb.id.toLong() }
        val node = globalCoordinateList.firstOrNull { it.id == suburb.id.toLong() }

        val currentLat = currentNode?.latitude
        val currentLon = currentNode?.longitude
        val lat = node?.latitude
        val lon = node?.longitude

        // Compare distance between the two suburbs nodes and if too close remove it
        if (
            currentLat != null && currentLon != null &&
            lat != null && lon != null
        ) {
            val distance = distanceBetweenTwoPoints(
                currentLat, currentLon, lat, lon
            )

            distance > 0.1
        } else {
            true
        }
    }
    newSuburbs.addAll(otherSuburbsFiltered)
}

/**
 * Calculates the distance between two geographical points using the Haversine formula.
 *
 * @param lat1 The latitude of the first point.
 * @param lon1 The longitude of the first point.
 * @param lat2 The latitude of the second point.
 * @param lon2 The longitude of the second point.
 * @return The distance between the two points in miles.
 */
fun distanceBetweenTwoPoints(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val theta = lon1 - lon2
    var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            cos(Math.toRadians(theta))
    dist = acos(dist)
    dist = Math.toDegrees(dist)
    dist *= 60 * 1.1515
    return dist
}
