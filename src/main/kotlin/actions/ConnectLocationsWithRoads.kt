package actions

import data.RoadPart
import data.fullNodesMap
import data.globalRoadParts
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs


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

fun distanceSquared(lat1: Double, lon1: Double, node: Node): Double {
    val dLat = node.latitude - lat1
    val dLon = node.longitude - lon1
    return dLat * dLat + dLon * dLon
}

private val roadNameCache = ConcurrentHashMap<String, Set<RoadPart>>()
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
