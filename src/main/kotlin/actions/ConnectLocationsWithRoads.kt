package actions

import data.RoadGlass
import data.RoadPart
import data.fullNodesMap
import data.globalGlassList
import data.globalRoadGlassList
import data.globalRoadParts
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

/**
 * Finds the closest road ID from a set of roads based on the given latitude and longitude.
 *
 * @param lat The latitude of the point to find the closest road to.
 * @param lon The longitude of the point to find the closest road to.
 * @param roads The set of roads to search for the closest road.
 * @return The ID of the closest road, or null if no road is found within the specified range.
 */
fun findClosestRoadId(lat: Double, lon: Double, roads: Set<RoadPart>): Long? {
    return roads.asSequence()
        .filter { road ->
            val firstNode = fullNodesMap[road.wayNodes.first().nodeId]
            firstNode?.let { abs(it.latitude - lat) <= 0.007 && abs(it.longitude - lon) <= 0.007 }
                ?: false
        }
        .minByOrNull { road ->
            road.wayNodes.minOf { node ->
                distanceSquared(lat, lon, fullNodesMap[node.nodeId]!!)
            }
        }
        ?.wayId
}

/**
 * Finds the closest road ID from all available roads based on the given latitude and longitude.
 *
 * @param lat The latitude of the point to find the closest road to.
 * @param lon The longitude of the point to find the closest road to.
 * @return The ID of the closest road, or null if no road is found within the specified range.
 */
fun findClosestRoadFromAll(lat: Double, lon: Double): Long? {
    val nearbyRoads = globalRoadParts.asSequence()
        .filter { road ->
            val firstNode = fullNodesMap[road.wayNodes.first().nodeId]
            firstNode?.let {
                abs(it.latitude - lat) <= 0.007 && abs(it.longitude - lon) <= 0.007
            } ?: false
        }
        .filter { road ->
            road.wayNodes.any { wayNode ->
                fullNodesMap[wayNode.nodeId]?.let { node ->
                    abs(node.latitude - lat) <= 0.005 && abs(node.longitude - lon) <= 0.005
                } ?: false
            }
        }

    return nearbyRoads
        .flatMap { road ->
            road.wayNodes.asSequence().mapNotNull { wayNode ->
                fullNodesMap[wayNode.nodeId]?.let { node ->
                    road.wayId to distanceSquared(lat, lon, node)
                }
            }
        }
        .minByOrNull { (_, distance) -> distance }
        ?.first
}

/**
 * Finds the closest road name (both in Greek and English) from all available roads based on the given latitude and longitude.
 *
 * @param lat The latitude of the point to find the closest road name to.
 * @param lon The longitude of the point to find the closest road name to.
 * @return A pair containing the Greek and English names of the closest road, or null if no road is found within the specified range.
 */
fun findClosestRoadNameFromAll(lat: Double, lon: Double): Pair<String?, String?> {
    val nearbyRoads = globalRoadGlassList
        .groupBy { it.wayId }
        .filter { group ->
            val firstWay = group.value.first()
            firstWay.let {
                abs(it.latitude - lat) <= 0.007 && abs(it.longitude - lon) <= 0.007
            }
        }
        .filter { group ->
            group.value.any { wayNode ->
                wayNode.let { node ->
                    abs(node.latitude - lat) <= 0.005 && abs(node.longitude - lon) <= 0.005
                }
            }
        }

    //Filter the closest way node
    val idOfTheClosestWay = nearbyRoads.values
        .flatMap { road ->
            road.map { wayNode ->
                wayNode to distanceSquared(lat, lon, wayNode)
            }
        }
        .minByOrNull { (_, distance) -> distance }
        ?.first


    val item = globalGlassList.find { it.id == "R${idOfTheClosestWay?.wayId}" }
    val nameEl = item?.titleEl
    val nameEn = item?.titleEn
    return Pair(nameEl, nameEn)
}

/**
 * Calculates the squared distance between a given point and a node.
 *
 * @param lat1 The latitude of the point.
 * @param lon1 The longitude of the point.
 * @param node The node to calculate the distance to.
 * @return The squared distance between the point and the node.
 */
fun distanceSquared(lat1: Double, lon1: Double, node: Node): Double {
    val dLat = node.latitude - lat1
    val dLon = node.longitude - lon1
    return dLat * dLat + dLon * dLon
}

/**
 * Calculates the squared distance between a given point and a road glass.
 *
 * @param lat1 The latitude of the point.
 * @param lon1 The longitude of the point.
 * @param roadGlass The road glass to calculate the distance to.
 * @return The squared distance between the point and the road glass.
 */
fun distanceSquared(lat1: Double, lon1: Double, roadGlass: RoadGlass): Double {
    val dLat = roadGlass.latitude - lat1
    val dLon = roadGlass.longitude - lon1
    return dLat * dLat + dLon * dLon
}

private val roadNameCache = ConcurrentHashMap<String, Set<RoadPart>>()

/**
 * Finds roads with the same name (either in Greek or English) as the given addresses.
 *
 * @param elAddress The Greek address to search for.
 * @param enAddress The English address to search for.
 * @return A set of roads with the same name as the given addresses.
 */
fun findRoadsWithSameName(elAddress: String?, enAddress: String?): Set<RoadPart> {
    val elWords = elAddress?.split(" ")?.filter { it.length > 1 }?.toSet()
    val enWords = enAddress?.split(" ")?.filter { it.length > 1 }?.toSet()

    if (elWords.isNullOrEmpty() && enWords.isNullOrEmpty()) {
        return emptySet()
    }

    val cacheKey = "${elWords?.joinToString(",")}|${enWords?.joinToString(",")}"
    return roadNameCache.getOrPut(cacheKey) {
        globalRoadParts.asSequence().filter { road ->
            elWords != null && road.elName != null && elWords.any { it.lowercase() in road.elName.lowercase() } ||
                    enWords != null && road.enName != null && enWords.any { it.lowercase() in road.enName.lowercase() }
        }.toSet()
    }
}
