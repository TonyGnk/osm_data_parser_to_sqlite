package convert

import data.RoadPart
import data.WayNode
import data.fullNodesMap
import data.fullWays
import data.globalRoadParts
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import utils.findTagWithName
import utils.getName
import utils.roundToFiveDecimals

fun convertWaysToRoads() {
    print("Ways to roads...")
    val iterator = fullWays.iterator()
    iterator.asSequence()
        .filter { way -> way.tags.isNotEmpty() && isRoad(way.tags) }
        .forEach { way ->
            handleRoadsL(way)
            iterator.remove()
        }

    println("OK : ${globalRoadParts.size} roads")
}

fun handleRoadsL(way: Way) {
    val names = way.tags.getName()
    val elName = names.first
    val enName = names.second

    val notEmptyNames = elName != null || enName != null

    if (notEmptyNames) globalRoadParts.add(
        RoadPart(
            wayId = way.id,
            elName = elName,
            enName = enName,
            wayNodes = way.wayNodes.mapIndexed { sequence, wayNode ->
                val node = fullNodesMap[wayNode.nodeId]!!
                WayNode(
                    wayId = way.id,
                    nodeId = wayNode.nodeId,
                    sequence = sequence,
                    latitude = roundToFiveDecimals(node.latitude),
                    longitude = roundToFiveDecimals(node.longitude)
                )
            }
        )
    )
}


private fun isRoad(tags: MutableCollection<Tag>): Boolean {
    return tags.findTagWithName(listOf("highway")) != null
}
