package parser

import PlaceMulti
import PlaceSingle
import RoadPart
import WayNode
import globalPlaceSingles
import globalPlacesMulti
import globalRoadParts
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Relation
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import parser.places.policies.getAddress
import parser.places.policies.getAddressNumber
import parser.places.policies.getCategoryId
import parser.places.policies.getName
import utils.findTagWithName
import java.io.File


fun parseOsmL(osmPath: String) {
    print("Parsing...")

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)

    val sink = object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> {
                    if (entity.tags.isEmpty()) return
                    insertSinglePlaceL(
                        node = entity
                    )
                }

                is Way -> {
                    if (entity.tags.isEmpty()) return

                    val isRoad = isRoad(entity.tags)
                    when (isRoad) {
                        true -> handleRoadsL(
                            way = entity
                        )

                        false -> insertMultiPlaceL(
                            way = entity
                        )
                    }
                }

//                is Relation -> {
//                    println(entity.tags.getName().toString())
//                }
            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }


    reader.setSink(sink)
    reader.run()

    println("Done")
}


private fun isRoad(tags: MutableCollection<Tag>): Boolean {
    return tags.findTagWithName(listOf("highway")) != null
}

fun insertSinglePlaceL(node: Node) {
    if (node.tags.isEmpty()) return

    val addresses = node.tags.getAddress()
    val category = node.tags.getCategoryId(addresses) ?: return

    val elAddress = addresses.first
    val enAddress = addresses.second

    val names = node.tags.getName()
    val elName = names.first
    val enName = names.second

    val addressNumber = node.tags.getAddressNumber()

    globalPlaceSingles.add(
        PlaceSingle(
            elName = elName,
            enName = enName,
            elAddress = elAddress,
            enAddress = enAddress,
            addressNumber = addressNumber,
            nodeId = node.id,
            latitude = roundToFiveDecimals(node.latitude),
            longitude = roundToFiveDecimals(node.longitude),
            category = category
        )
    )
}

fun handleRoadsL(
    way: Way
) {
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
                WayNode(
                    wayId = way.id,
                    nodeId = wayNode.nodeId,
                    sequence = sequence,
                    latitude = roundToFiveDecimals(wayNode.latitude),
                    longitude = roundToFiveDecimals(wayNode.longitude)
                )
            }.toSet()
        )
    )
}


fun insertMultiPlaceL(
    way: Way,
) {
    val names = way.tags.getName()
    val elName = names.first
    val enName = names.second

    val addresses = way.tags.getAddress()
    val elAddress = addresses.first
    val enAddress = addresses.second
    val addressNumber = way.tags.getAddressNumber()

    val category = way.tags.getCategoryId(addresses) ?: return

    globalPlacesMulti.add(
        PlaceMulti(
            wayId = way.id,
            elName = elName,
            enName = enName,
            elAddress = elAddress,
            enAddress = enAddress,
            addressNumber = addressNumber,
            wayNodes = way.wayNodes.mapIndexed { sequence, wayNode ->
                WayNode(
                    wayId = way.id,
                    nodeId = wayNode.nodeId,
                    sequence = sequence,
                    latitude = roundToFiveDecimals(wayNode.latitude),
                    longitude = roundToFiveDecimals(wayNode.longitude)
                )
            }.toSet(),
            category = category
        )
    )
}
