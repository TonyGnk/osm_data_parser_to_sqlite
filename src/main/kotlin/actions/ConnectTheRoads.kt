package actions

import data.RoadPart
import data.RoadSet
import data.globalLocationsList
import data.globalRoadConnected
import data.globalRoadParts

/**
 * Connects road parts with the same name into continuous road segments.
 * Updates the globalRoadConnected list with the connected road parts.
 */
fun connectRoadParts() {
    print("Connect roads...")

    val roads = globalRoadParts.toMutableSet()
    val size = roads.size

    while (roads.isNotEmpty()) {
        val road = roads.first()
        val elName = road.elName
        val enName = road.enName

        val roadsWithSameNames: List<RoadPart> = roads.filter {
            (elName != null && it.elName == elName) || (enName != null && it.enName == enName)
        }

        roads.removeAll(roadsWithSameNames.toSet())

        val sets = connectASet(roadsWithSameNames)

        //Each set insert it to the table
        sets.forEach { set ->
            // Add the set to the table
            globalRoadConnected.add(
                RoadPart(
                    wayId = set.wayOldIds.first(),
                    elName = set.elName,
                    enName = set.enName,
                    wayNodes = set.wayNewIds
                )
            )

            val newId = set.wayNewIds.first().wayId
            val wayOldIdsSet = set.wayOldIds.toSet()
            globalLocationsList.forEach { place ->
                if (place.wayId in wayOldIdsSet) {
                    place.wayId = newId
                }
            }
        }
    }
    println("\rConnect roads...OK:  $size -> ${globalRoadConnected.size}")
}

/**
 * Connects a set of road parts with the same name into continuous road segments.
 *
 * @param roadsWithSameName The list of road parts with the same name.
 * @return A list of connected road sets.
 */
fun connectASet(roadsWithSameName: List<RoadPart>): List<RoadSet> {
    val sets: MutableList<RoadSet> = mutableListOf()
    val remainingRoads = roadsWithSameName.toMutableList()

    while (remainingRoads.isNotEmpty()) {
        val road = remainingRoads.removeAt(0)
        val set = createSet(road)
        connectNestedSets(remainingRoads, set)
        sets.add(set)
    }

    return sets
}

/**
 * Recursively connects nested road parts to a given road set.
 *
 * @param remainingRoads The list of remaining road parts to connect.
 * @param set The road set to connect the road parts to.
 */
private fun connectNestedSets(
    remainingRoads: MutableList<RoadPart>, set: RoadSet
) {
    val roadsToRemove = mutableListOf<RoadPart>()

    for (road in remainingRoads) {
        if (set.wayNewIds.first().nodeId in listOf(
                road.wayNodes.first().nodeId,
                road.wayNodes.last().nodeId
            )
        ) {
            set.add(addFront = true, road)
            roadsToRemove.add(road)
        } else if (set.wayNewIds.last().nodeId in listOf(
                road.wayNodes.first().nodeId,
                road.wayNodes.last().nodeId
            )
        ) {
            set.add(addFront = false, road)
            roadsToRemove.add(road)
        }
    }

    remainingRoads.removeAll(roadsToRemove)

    if (roadsToRemove.isNotEmpty()) {
        connectNestedSets(remainingRoads, set)
    }
}

/**
 * Creates a new road set from a given road part.
 *
 * @param road The road part to create the set from.
 * @return The created road set.
 */
private fun createSet(road: RoadPart): RoadSet {
    return RoadSet(
        elName = road.elName,
        enName = road.enName,
        wayOldIds = mutableListOf(road.wayId),
        wayNewIds = road.wayNodes.toMutableList(),
    )
}

/**
 * Adds a road part to the road set.
 *
 * @param addFront Whether to add the road part to the front or back of the set.
 * @param road The road part to add.
 */
private fun RoadSet.add(
    addFront: Boolean, road: RoadPart
) {
    this.wayOldIds.add(road.wayId)

    if (elName == null && road.elName != null) {
        elName = road.elName
    }
    if (enName == null && road.enName != null) {
        enName = road.enName
    }

    if (addFront) {
        wayNewIds.addAll(0, road.wayNodes.filter { it != wayNewIds.first() })

    } else {
        wayNewIds.addAll(road.wayNodes.filter { it != wayNewIds.last() })
    }
}
