package actions

import data.Glass
import data.RoadGlass
import data.globalGlassList
import data.globalRoadConnected
import data.globalRoadGlassList


fun convertRoadPartsToRoads() {
    val roadsParts = globalRoadConnected

    roadsParts.forEach { roadPart ->
        val glass = Glass(
            titleEl = roadPart.elName,
            titleEn = roadPart.enName,
            subTitleEl = null,
            subTitleEn = null,
            id = "R${roadPart.wayId}",
            category = null
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
