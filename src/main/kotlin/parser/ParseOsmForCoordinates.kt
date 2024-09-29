package parser

import globalPlaceSingles
import globalPlacesMulti
import globalRoadConnected
import globalRoadParts
import nodes
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import java.io.File
import java.util.Locale

//private val nodes: MutableSet<Triple<Long, Double, Double>> = mutableSetOf()

fun parseOsmForCoordinates(osmPath: String) {
    print("Inserting nodes...")

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)
    val multiPlaceSet = globalPlacesMulti.map {
        it.wayNodes.map { wayNode -> wayNode.nodeId }
    }.flatten().toSet()
    val roadSet = globalRoadParts.map {
        it.wayNodes.map { wayNode -> wayNode.nodeId }
    }.flatten().toSet()

    //val existingNodes = singlePlaceSet.union(multiPlaceSet).union(roadSet)
    val sink = createOsmSink(multiPlaceSet, roadSet)

//    insertNodeInRoads()
//    insertNodePlaces()

    reader.setSink(sink)
    reader.run()
    println("Done")
}

private fun createOsmSink(
    multiPlaceSet: Set<Long>,
    roadSet: Set<Long>
): Sink {
    return object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> {
                    //If node belongs to set 1 do x1, if belong to set 2 do x2 ...etc
                    when (entity.id) {
                        in multiPlaceSet -> {
                            nodes[entity.id] = Pair(
                                roundToFiveDecimals(entity.latitude),
                                roundToFiveDecimals(entity.longitude)
                            )
//                            nodes.add(
//                                Triple(
//                                    entity.id,
//                                    roundToFiveDecimals(entity.latitude),
//                                    roundToFiveDecimals(entity.longitude)
//                                )
//                            )
//                            globalPlacesMulti.forEach { place ->
//                                place.wayNodes.forEach { wayNode ->
//                                    if (wayNode.nodeId == entity.id) {
//                                        wayNode.latitude = roundToFiveDecimals(entity.latitude)
//                                        wayNode.longitude = roundToFiveDecimals(entity.longitude)
//                                    }
//                                }
//                            }
                        }

                        in roadSet -> {
                            nodes[entity.id] = Pair(
                                roundToFiveDecimals(entity.latitude),
                                roundToFiveDecimals(entity.longitude)
                            )
//                            globalRoadParts.forEach { roadPart ->
//                                roadPart.wayNodes.forEach { wayNode ->
//                                    if (wayNode.nodeId == entity.id) {
//                                        wayNode.latitude = roundToFiveDecimals(entity.latitude)
//                                        wayNode.longitude = roundToFiveDecimals(entity.longitude)
//                                    }
//                                }
//                            }
                        }
                    }


//                    if (entity.id !in existingNodes) {
//                        nodes.add(
//                            Triple(
//                                entity.id,
//                                roundToFiveDecimals(entity.latitude),
//                                roundToFiveDecimals(entity.longitude)
//                            )
//                        )
//                    }
                }
            }

        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }
}

fun insertNodeInRoads() {
    globalRoadConnected.forEach { road ->
        road.wayNodes.forEach { wayNode ->
            val node = nodes[wayNode.nodeId]
            if (node != null) {
                wayNode.latitude = node.first
                wayNode.longitude = node.second
            }
        }
    }
}

fun insertNodePlaces() {
    globalPlacesMulti.forEach { place ->
        place.wayNodes.forEach { wayNode ->
            val node = nodes[wayNode.nodeId]
            if (node != null) {
                wayNode.latitude = node.first
                wayNode.longitude = node.second
            }
        }
    }
}

fun roundToFiveDecimals(value: Double): Double {
    return String.format(Locale.US, "%.5f", value).toDouble()
}
