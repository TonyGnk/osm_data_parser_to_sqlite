package actions

import RoadPart
import globalPlaceSingles
import globalPlacesMulti
import globalRoadParts
import nodes

fun getTheIdOfTheRoadL() {
    print("Address to Road...")

    globalPlaceSingles.forEachIndexed { index, place ->
        val progress = (index + 1) * 100 / globalPlaceSingles.size
        print("\rAddress to Road...1...$progress%")

        val roads = findRoads(place.elAddress, place.enAddress)
        place.wayId = when {
            roads.size == 1 -> roads[0].wayId

            roads.size > 1 -> findClosestRoadId(
                lat = place.latitude,
                lon = place.longitude,
                roads = roads
            )

            else -> findClosestRoadFromAll(
                lat = place.latitude,
                lon = place.longitude,
            )
        }
    }

    //println("\n\n\n\nglobalPlacesMulti size: ${globalPlacesMulti.size}")
    globalPlacesMulti.forEachIndexed { index, place ->
        val progress = (index + 1) * 100 / globalPlacesMulti.size
        print("\rAddress to Road...2...$progress%")

        //print("PlaceInfo: ${place.elName}, ${place.enName}, ${place.elAddress}, ${place.enAddress}, ${place.category}")

        val nodes: List<Pair<Double, Double>?> = place.wayNodes.map { nodes[it.nodeId] }
        val nonNullNodes = nodes.filterNotNull()
        val avLat = nonNullNodes.map { it.first }.average()
        //print(" avLat: $avLat")
        val avLon = nonNullNodes.map { it.second }.average()
        //println(" avLon: $avLon")
        val roads = findRoads(place.elAddress, place.enAddress)

        place.roadWayId = when {
            roads.size == 1 -> roads[0].wayId
            roads.size > 1 -> findClosestRoadId(
                lat = avLat,
                lon = avLon,
                roads = roads
            )


            else -> findClosestRoadFromAll(
                lat = avLat,
                lon = avLon,
            )
        }
    }

    println(" Done")
}


fun findRoads(elAddress: String?, enAddress: String?): List<RoadPart> {
    val elWords = elAddress?.split(" ")?.filter { it.length > 1 }
    val enWords = enAddress?.split(" ")?.filter { it.length > 1 }

    if (elWords != null && enWords != null) {
        return globalRoadParts.filter { road ->
            val elName = road.elName
            val enName = road.enName
            elWords.any { word -> elName?.contains(word, ignoreCase = true) == true } ||
                    enWords.any { word -> enName?.contains(word, ignoreCase = true) == true }
        }
    } else if (elWords != null) {
        return globalRoadParts.filter { road ->
            val elName = road.elName
            elWords.any { word -> elName?.contains(word, ignoreCase = true) == true }
        }
    } else if (enWords != null) {
        return globalRoadParts.filter { road ->
            val enName = road.enName
            enWords.any { word -> enName?.contains(word, ignoreCase = true) == true }
        }
    } else {
        return emptyList()
    }
}


fun findClosestRoadId(
    lat: Double,
    lon: Double,
    roads: List<RoadPart>
): Long {
    //println("Start - ${roads.size}: ${roads.map { it.elName }}")
    //println("Lat: $lat, Lon: $lon")
    //println("Range lat: ${lat - 0.005} - ${lat + 0.005}, Range lon: ${lon - 0.005} - ${lon + 0.005}\n")

    val nearbyRoads = roads.asSequence().filter { road ->
        val firstNode = nodes[road.wayNodes.first().nodeId]
        if (firstNode != null) {
            //println("First node: ${firstNode.first}, ${firstNode.second}")
            firstNode.first in lat - 0.05..lat + 0.05 &&
                    firstNode.second in lon - 0.05..lon + 0.05
        } else false
    }
    // println("Nearby roads: ${nearbyRoad


    return nearbyRoads
        .minByOrNull { road ->
            road.wayNodes.minOf { node ->
                distanceSquared(lat, lon, node.latitude, node.longitude)
            }
        }
        ?.wayId
        ?: throw NoSuchElementException("No nearby roads found")
}

fun findClosestRoadFromAll(
    lat: Double, lon: Double,
): Long? {
    var minDistance = Double.MAX_VALUE
    var closestRoadId: Long? = null


    val nearbyRoads = globalRoadParts.filter { road ->
        val firstNode = nodes[road.wayNodes.first().nodeId]
        if (firstNode != null) {
            firstNode.first in lat - 0.05..lat + 0.05 &&
                    firstNode.second in lon - 0.05..lon + 0.05
        } else false
    }

    val nearby2Roads = nearbyRoads.filter { road ->
        road.wayNodes.any { wayNode ->
            val node = nodes[wayNode.nodeId]
            if (node != null) {
                node.first in lat - 0.005..lat + 0.005 &&
                        node.second in lon - 0.005..lon + 0.005
            } else false
        }
    }

    nearby2Roads.forEach { road ->
        road.wayNodes.forEach { wayNode ->
            val node = nodes[wayNode.nodeId]
            if (node != null) {
                val distance = distanceSquared(lat, lon, node.first, node.second)
                if (distance < minDistance) {
                    minDistance = distance
                    closestRoadId = road.wayId
                }
            }
        }
    }

    return closestRoadId
}


fun distanceSquared(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = lat2 - lat1
    val dLon = lon2 - lon1
    return dLat * dLat + dLon * dLon
}
