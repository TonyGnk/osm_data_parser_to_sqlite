package actions.connectRoads

import RoadPart
import globalPlaceSingles
import globalPlacesMulti
import globalRoadConnected
import globalRoadParts


fun connectTheRoads() {
    print("Connect roads...")

    val roads = globalRoadParts

    while (roads.isNotEmpty()) {
        val road = roads.first()
        val elName = road.elName
        val enName = road.enName

        val roadsWithSameNames: List<RoadPart> = roads.filter {
            it.elName == elName || it.enName == enName
        }
        roads.removeAll(roadsWithSameNames)


        val sets = connectASet(roadsWithSameNames)

        //Each set insert it to the table
        sets.forEach { set ->
            // Add the set to the table
            globalRoadConnected.add(
                RoadPart(
                    wayId = set.wayOldIds.first(),
                    elName = set.elName,
                    enName = set.enName,
                    wayNodes = set.wayNewIds.toSet()
                )
            )


            // If the places single way id is in set.wayOldIds then replace it with the new way id
            val newId = set.wayNewIds.first().wayId
            val wayOldIdsSet = set.wayOldIds.toSet()
            globalPlaceSingles.forEach { place ->
                if (place.wayId in wayOldIdsSet) {
                    place.wayId = newId
                }
            }
            globalPlacesMulti.forEach { place ->
                if (place.roadWayId in wayOldIdsSet) {
                    place.roadWayId = newId
                }
            }
        }

        //Find the progress
        val progress = roads.size
        print("\rConnect roads...$progress left")
    }
    println(" Done")
}
