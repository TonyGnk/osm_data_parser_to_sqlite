package parser.way

import utils.findTagWithName
import org.openstreetmap.osmosis.core.domain.v0_6.Way
import java.sql.Connection

data class SuburbIdWithCoordinates(
    val id: Long,
    val lat: Double,
    val lon: Double,
)

fun handleRoads(
    insertRoadStmt: java.sql.PreparedStatement,
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way
) {
    val elName = way.tags.findTagWithName(listOf("name:el", "name"))
    val enName = way.tags.findTagWithName(listOf("name:en", "int_name"))

    val notEmptyNames = elName.isNotBlank() || enName.isNotBlank()

    if (notEmptyNames) {
        insertRoads(
            insertRoadStmt = insertRoadStmt,
            id = way.id,
            elName = elName,
            enName = enName,
        )
        insertWays(
            insertWayNodeStmt = insertWayNodeStmt,
            way = way
        )
    }
}


private fun insertRoads(
    insertRoadStmt: java.sql.PreparedStatement,
    id: Long,
    elName: String,
    enName: String,
) {
    insertRoadStmt.setLong(1, id)
    insertRoadStmt.setString(2, elName)
    insertRoadStmt.setString(3, enName)

    insertRoadStmt.addBatch()
}


fun insertWays(
    insertWayNodeStmt: java.sql.PreparedStatement,
    way: Way
) {
    way.wayNodes.forEachIndexed { index, wayNode ->
        insertWayNodeStmt.setLong(1, way.id)
        insertWayNodeStmt.setLong(2, wayNode.nodeId)
        insertWayNodeStmt.setInt(3, index)
        insertWayNodeStmt.addBatch()
    }
}
