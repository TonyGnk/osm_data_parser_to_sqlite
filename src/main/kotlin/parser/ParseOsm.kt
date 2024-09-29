package parser

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Tag
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import parser.places.insertMultiPlace
import parser.places.insertSinglePlace
import parser.way.handleRoads
import utils.findTagWithName
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


fun parseOsm(osmPath: String, dbPath: String) {
    print("Parsing...")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    val insertPlaceStmt = sql.prepareStatement(
        "INSERT INTO places_single " +
                "(el_name, en_name, el_address, en_address, address_number, latitude, longitude, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    )
    val insertPlaceMultiStmt = sql.prepareStatement(
        "INSERT INTO places_multi " +
                "(way_id, el_name, en_name, el_address, en_address, address_number, category) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
    )
    val insertRoadStmt = sql.prepareStatement(
        "INSERT INTO roads (way_id, el_Name, en_Name) VALUES (?, ?, ?)"
    )
    val insertWayNodeStmt = sql.prepareStatement(
        "INSERT INTO way_nodes (way_id, node_id, latitude, longitude, sequence) VALUES (?, ?, ?, ?, ?)"
    )

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)

    val sink = object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> {
                    if (entity.tags.isEmpty()) return
                    insertSinglePlace(
                        insertPlaceStmt = insertPlaceStmt,
                        node = entity
                    )
                }

                is Way -> {
                    if (entity.tags.isEmpty()) return

                    val isRoad = isRoad(entity.tags)
                    when (isRoad) {
                        true -> handleRoads(
                            insertRoadStmt = insertRoadStmt,
                            insertWayNodeStmt = insertWayNodeStmt,
                            way = entity
                        )

                        false -> insertMultiPlace(
                            insertPlaceMultiStmt = insertPlaceMultiStmt,
                            insertWayNodeStmt = insertWayNodeStmt,
                            way = entity
                        )
                    }


                }
            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }


    reader.setSink(sink)
    reader.run()

    insertPlaceStmt.executeBatch()
    insertPlaceStmt.close()

    insertPlaceMultiStmt.executeBatch()
    insertPlaceMultiStmt.close()

    insertRoadStmt.executeBatch()
    insertRoadStmt.close()

    insertWayNodeStmt.executeBatch()
    insertWayNodeStmt.close()

    sql.commit()
    sql.autoCommit = true
    sql.close()

    println("Done")
}


private fun isRoad(tags: MutableCollection<Tag>): Boolean {
    return tags.findTagWithName(listOf("highway")) != null
}

fun insertWays(
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way
) {
    way.wayNodes.forEachIndexed { index, wayNode ->
        insertWayNodeStmt.setLong(1, way.id)
        insertWayNodeStmt.setLong(2, wayNode.nodeId)
        insertWayNodeStmt.setLong(3, 0)
        insertWayNodeStmt.setLong(4, 0)
        insertWayNodeStmt.setInt(5, index)
        insertWayNodeStmt.addBatch()
    }
}
