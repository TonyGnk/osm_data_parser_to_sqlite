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

/*
public final data class WayNode(
    val wayId: Long,
    val nodeId: Long,
    val sequence: Int,
    val latitude: Double,
    val longitude: Double
)
data class Coordinate(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
)

Finds the closest suburb from all the way nodes of a road part
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
