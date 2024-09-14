package parser

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer
import org.openstreetmap.osmosis.core.domain.v0_6.Node
import org.openstreetmap.osmosis.core.task.v0_6.Sink
import org.openstreetmap.osmosis.xml.common.CompressionMethod
import org.openstreetmap.osmosis.xml.v0_6.XmlReader
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.Locale

fun parseOsmForCoordinates(osmPath: String, dbPath: String) {
    print("Inserting nodes...")
    DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
        connection.autoCommit = false

        val requiredNodeIds = collectRequiredNodeIds(connection)
        processOsmFile(osmPath, connection, requiredNodeIds)

        connection.commit()
    }
    println("Done")
}

private fun collectRequiredNodeIds(connection: Connection): MutableSet<Long> {
    val nodeIds = mutableSetOf<Long>()
    connection.createStatement().use { statement ->
        statement.executeQuery("SELECT node_id FROM way_nodes").use { resultSet ->
            while (resultSet.next()) {
                nodeIds.add(resultSet.getLong("node_id"))
            }
        }
    }
    return nodeIds
}

private fun processOsmFile(
    osmFilePath: String,
    connection: Connection,
    requiredNodeIds: MutableSet<Long>
) {
    val reader = XmlReader(File(osmFilePath), false, CompressionMethod.None)
    val sink = createOsmSink(connection, requiredNodeIds)
    reader.setSink(sink)
    reader.run()
}

private fun createOsmSink(connection: Connection, requiredNodeIds: MutableSet<Long>): Sink {
    return object : Sink {
        override fun process(entityContainer: EntityContainer) {
            val entity = entityContainer.entity
            if (entity is Node && entity.id in requiredNodeIds) {
                insertNode(connection, entity)
                requiredNodeIds.remove(entity.id)
            }
        }

        override fun close() {}
        override fun complete() {}
        override fun initialize(metaData: MutableMap<String, Any>?) {}
    }
}

private fun insertNode(connection: Connection, node: Node) {
    connection.prepareStatement(
        "INSERT INTO nodes (id, latitude, longitude) VALUES (?, ?, ?)"
    ).use { statement ->
        statement.setLong(1, node.id)
        statement.setDouble(2, roundToFiveDecimals(node.latitude))
        statement.setDouble(3, roundToFiveDecimals(node.longitude))
        statement.executeUpdate()
    }
}

private fun roundToFiveDecimals(value: Double): Double {
    return String.format(Locale.US, "%.5f", value).toDouble()
}
