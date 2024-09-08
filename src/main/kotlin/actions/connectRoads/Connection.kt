package actions.connectRoads

data class RoadSet(
    var elName: String = "",
    var enName: String = "",
    val wayIds: MutableList<Long> = mutableListOf(),
    val nodeIds: MutableList<Long> = mutableListOf(),
)


fun connectASet(roadsWithSameName: List<RoadToBeConnected>): List<RoadSet> {
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
    remainingRoads: MutableList<RoadToBeConnected>, set: RoadSet
) {
    val roadsToRemove = mutableListOf<RoadToBeConnected>()

    for (road in remainingRoads) {
        // println("Road: ${road.elName}, ${road.wayId}  - Remaining: ${remainingRoads.map { it.wayId }} - ${roadsToRemove.size}")
//        if (road.nodeIds.any { it in set.nodeIds }) {
//            set.add(road)
//            roadsToRemove.add(road)
//        }
        if (set.nodeIds.first() in listOf(road.nodeIds.first(), road.nodeIds.last())) {
            set.add(addFront = true, road)
            roadsToRemove.add(road)
        } else if (set.nodeIds.last() in listOf(road.nodeIds.first(), road.nodeIds.last())) {
            set.add(addFront = false, road)
            roadsToRemove.add(road)
        }
    }

    remainingRoads.removeAll(roadsToRemove)

    if (roadsToRemove.isNotEmpty()) {
        connectNestedSets(remainingRoads, set)
    }
}


private fun createSet(road: RoadToBeConnected): RoadSet {
    return RoadSet(
        elName = road.elName,
        enName = road.enName,
        wayIds = mutableListOf(road.wayId),
        nodeIds = road.nodeIds.toMutableList(),
    )
}


private fun RoadSet.add(
    addFront: Boolean, road: RoadToBeConnected
) {
    this.wayIds.add(road.wayId)

    if (elName.isBlank() && road.elName.isNotBlank()) {
        elName = road.elName
    }
    if (enName.isBlank() && road.enName.isNotBlank()) {
        enName = road.enName
    }

    if (addFront) {
        nodeIds.addAll(0, road.nodeIds.filter { it != nodeIds.first() })

    } else {
        nodeIds.addAll(road.nodeIds.filter { it != nodeIds.last() })

    }
}

fun connectTheRoadsShowcase(dbPath: String = "") {
    println("Connect roads")

    val listOfRoadsExample = listOf(
        RoadToBeConnected(
            elName = "Οδός Α",
            enName = "Road A",
            wayId = 1,
            nodeIds = listOf(1L, 2L, 3L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Α",
            enName = "Road A",
            wayId = 2,
            nodeIds = listOf(3L, 4L, 5L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός A",
            enName = "Road Α",
            wayId = 3,
            nodeIds = listOf(5L, 6L, 7L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Α",
            enName = "Road A",
            wayId = 4,
            nodeIds = listOf(7L, 8L, 9L, 10L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Β",
            enName = "Road B",
            wayId = 5,
            nodeIds = listOf(20L, 21L, 22L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Β",
            enName = "Road B",
            wayId = 6,
            nodeIds = listOf(22L, 23L, 24L, 25L, 26L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Β",
            enName = "Road B",
            wayId = 7,
            nodeIds = listOf(26L, 27L, 28L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Γ",
            enName = "Road C",
            wayId = 8,
            nodeIds = listOf(30L, 31L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Γ",
            enName = "Road C",
            wayId = 9,
            nodeIds = listOf(31L, 32L, 33L, 34L).toMutableList(),
        ),
        RoadToBeConnected(
            elName = "Οδός Γ",
            enName = "Road C",
            wayId = 10,
            nodeIds = listOf(34L, 35L).toMutableList(),
        ),
    )
    val listOfRoadsExampleRandomizedOrder = listOfRoadsExample.shuffled()
    listOfRoadsExampleRandomizedOrder.forEach { road ->
        println("Road: ${road.elName} - ${road.enName}")
        println("Way: ${road.wayId}")
        println("Nodes: ${road.nodeIds}")
        println()
    }
    println("------------------")
    val sets = connectASet(listOfRoadsExampleRandomizedOrder)

    //Print
    sets.forEach { set ->
        println("Set: ${set.elName} - ${set.enName}")
        println("Nodes: ${set.nodeIds}")
        println()
    }
}
