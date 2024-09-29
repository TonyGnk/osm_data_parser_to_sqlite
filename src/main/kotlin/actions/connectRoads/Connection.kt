package actions.connectRoads

import RoadPart
import WayNode

data class RoadSet(
    var elName: String? = null,
    var enName: String? = null,
    val wayOldIds: MutableList<Long> = mutableListOf(),
    val wayNewIds: MutableList<WayNode> = mutableListOf(),
)


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

private fun connectNestedSets(
    remainingRoads: MutableList<RoadPart>, set: RoadSet
) {
    val roadsToRemove = mutableListOf<RoadPart>()

    for (road in remainingRoads) {
        if (set.wayNewIds.first() in listOf(road.wayNodes.first(), road.wayNodes.last())) {
            set.add(addFront = true, road)
            roadsToRemove.add(road)
        } else if (set.wayNewIds.last() in listOf(road.wayNodes.first(), road.wayNodes.last())) {
            set.add(addFront = false, road)
            roadsToRemove.add(road)
        }
    }

    remainingRoads.removeAll(roadsToRemove)

    if (roadsToRemove.isNotEmpty()) {
        connectNestedSets(remainingRoads, set)
    }
}


private fun createSet(road: RoadPart): RoadSet {
    return RoadSet(
        elName = road.elName,
        enName = road.enName,
        wayOldIds = mutableListOf(road.wayId),
        wayNewIds = road.wayNodes.toMutableList(),
    )
}


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

//fun connectTheRoadsShowcase(dbPath: String = "") {
//    println("Connect roads")
//
//    val listOfRoadsExample = listOf(
//        RoadToBeConnected(
//            elName = "Οδός Α",
//            enName = "Road A",
//            wayId = 1,
//            nodeIds = listOf(1L, 2L, 3L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Α",
//            enName = "Road A",
//            wayId = 2,
//            nodeIds = listOf(3L, 4L, 5L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός A",
//            enName = "Road Α",
//            wayId = 3,
//            nodeIds = listOf(5L, 6L, 7L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Α",
//            enName = "Road A",
//            wayId = 4,
//            nodeIds = listOf(7L, 8L, 9L, 10L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Β",
//            enName = "Road B",
//            wayId = 5,
//            nodeIds = listOf(20L, 21L, 22L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Β",
//            enName = "Road B",
//            wayId = 6,
//            nodeIds = listOf(22L, 23L, 24L, 25L, 26L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Β",
//            enName = "Road B",
//            wayId = 7,
//            nodeIds = listOf(26L, 27L, 28L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Γ",
//            enName = "Road C",
//            wayId = 8,
//            nodeIds = listOf(30L, 31L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Γ",
//            enName = "Road C",
//            wayId = 9,
//            nodeIds = listOf(31L, 32L, 33L, 34L).toMutableList(),
//        ),
//        RoadToBeConnected(
//            elName = "Οδός Γ",
//            enName = "Road C",
//            wayId = 10,
//            nodeIds = listOf(34L, 35L).toMutableList(),
//        ),
//    )
//    val listOfRoadsExampleRandomizedOrder = listOfRoadsExample.shuffled()
//    listOfRoadsExampleRandomizedOrder.forEach { road ->
//        println("Road: ${road.elName} - ${road.enName}")
//        println("Way: ${road.wayId}")
//        println("Nodes: ${road.nodeIds}")
//        println()
//    }
//    println("------------------")
//    val sets = connectASet(listOfRoadsExampleRandomizedOrder)
//
//    //Print
//    sets.forEach { set ->
//        println("Set: ${set.elName} - ${set.enName}")
//        println("Nodes: ${set.wayNewIds}")
//        println()
//    }
//}
