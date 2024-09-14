package parser

import parser.way.insertRoadOrMultiPointPlace
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import parser.nodes.handleSinglePoint
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

fun osmParser(osmPath: String, dbPath: String) {
    print("Parsing...")
    val sql: Connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    sql.autoCommit = false

    //Statements
    val insertPlaceStmt = sql.prepareStatement(
        "INSERT INTO places " +
                "(entity_id, el_name, en_name, el_address, en_address, address_number, category, is_singe_point) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    )
    val insertRoadStmt = sql.prepareStatement(
        "INSERT INTO roads (way_id, el_Name, en_Name) VALUES (?, ?, ?)"
    )
    val insertWayNodeStmt = sql.prepareStatement(
        "INSERT INTO way_nodes (way_id, node_id, sequence) VALUES (?, ?, ?)"
    )

    val reader = XmlReader(File(osmPath), false, CompressionMethod.None)

    val sink = object : Sink {
        override fun process(entityContainer: EntityContainer) {
            when (val entity = entityContainer.entity) {
                is Node -> handleSinglePoint(
                    insertPlaceStmt = insertPlaceStmt,
                    node = entity
                )

                is Way -> insertRoadOrMultiPointPlace(
                    insertPlaceStmt = insertPlaceStmt,
                    insertRoadStmt = insertRoadStmt,
                    insertWayNodeStmt = insertWayNodeStmt,
                    way = entity
                )
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

    insertRoadStmt.executeBatch()
    insertRoadStmt.close()

    insertWayNodeStmt.executeBatch()
    insertWayNodeStmt.close()

    sql.commit()
    sql.autoCommit = true
    // sql.createStatement().execute("VACUUM")
    sql.close()

    println("Done")
}
