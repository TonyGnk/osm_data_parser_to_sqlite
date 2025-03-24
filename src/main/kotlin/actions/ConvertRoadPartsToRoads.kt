package actions

import data.Coordinate
import data.Glass
import data.RoadGlass
import data.Suburb
import data.WayNode
import data.globalCoordinateList
import data.globalGlassList
import data.globalRoadConnected
import data.globalRoadGlassList
import data.globalSuburbsList
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Converts road parts to roads and updates the global glass list and road glass list.
 */
fun convertRoadPartsToRoads() {
    val roadsParts = globalRoadConnected

    roadsParts.forEach { roadPart ->
        val suburb = findClosestSuburbFromAll(roadPart.wayNodes)

        val glass = Glass(
            titleEl = roadPart.elName,
            titleEn = roadPart.enName,
            subTitleEl = suburb?.titleEl,
            subTitleEn = suburb?.titleEn,
            id = "R${roadPart.wayId}",
            category = null,
        )

        globalGlassList.add(glass)

        roadPart.wayNodes.forEach { wayNode ->
            val roadGlass = RoadGlass(
                wayId = roadPart.wayId,
                latitude = wayNode.latitude,
                longitude = wayNode.longitude,
                sequence = wayNode.sequence
            )
            globalRoadGlassList.add(roadGlass)
        }
    }
}

/**
 * Finds the closest suburb from all the way nodes of a road part.
 *
 * @param wayNodes The list of way nodes of the road part.
 * @return The closest suburb, or null if no suburb is found within the specified range.
 */
private fun findClosestSuburbFromAll(wayNodes: List<WayNode>): Suburb? {
    val suburbsMap: Map<Suburb, Coordinate?> = globalSuburbsList.associateWith {
        globalCoordinateList.firstOrNull { coordinate ->
            coordinate.id == it.id.toLong()
        }
    }

    // Initialize variables to track the closest suburb and the shortest distance found
    var closestSuburb: Suburb? = null
    val maxDistance = 500.0 // 1.5 km in meters
    var shortestDistance = Double.MAX_VALUE

    // Iterate over each way node
    for (wayNode in wayNodes) {
        // Iterate over each suburb and its coordinate
        for ((suburb, coordinate) in suburbsMap) {
            // Ensure the coordinate is not null
            if (coordinate != null) {
                // Calculate the distance between the way node and the suburb's coordinate
                val distance = calculateDistance(
                    wayNode.latitude, wayNode.longitude,
                    coordinate.latitude, coordinate.longitude
                )
                // Update the closest suburb if a shorter distance is found
                if (distance <= maxDistance && distance < shortestDistance) {
                    shortestDistance = distance
                    closestSuburb = suburb
                }
            }
        }
    }
    // Return the closest suburb found, or null if none was found
    return closestSuburb
}

/**
 * Calculates the distance between two geographical points using the Haversine formula.
 *
 * @param lat1 The latitude of the first point.
 * @param lon1 The longitude of the first point.
 * @param lat2 The latitude of the second point.
 * @param lon2 The longitude of the second point.
 * @return The distance between the two points in meters.
 */
fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val earthRadius = 6371e3 // Earth's radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}
